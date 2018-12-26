package org.beigesoft.webstore.service;

/*
 * Copyright (c) 2018 Beigesoft™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.model.IRequestData;
import org.beigesoft.log.ILogger;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.persistable.CustOrderGdLn;
import org.beigesoft.webstore.persistable.CustOrderSrvLn;

/**
 * <p>It accepts all buyer's orders in single transaction.
 * It changes item's availability and orders status to PENDING.
 * If any item is unavailable, then that transaction will be rolled back,
 * and it returns result NULL. Request handler must be non-transactional,
 * i.e. it mustn't be started transaction.</p>
 *
 * @param <RS> platform dependent RDBMS recordset
 * @author Yury Demidenko
 */
public class AcpOrd<RS> {

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>Database service.</p>
   */
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   */
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>I18N query goods specifics for goods.</p>
   **/
  private String quOrGdChk;

  /**
   * <p>It accepts all buyer's orders in single transaction.
   * It changes item's availability and orders status to PENDING.
   * If any item is unavailable, then that transaction will be rolled back,
   * and it returns result NULL. Request handler must be non-transactional,
   * i.e. it mustn't be started transaction.</p>
   * @param pReqVars additional request scoped parameters
   * @param pReqDt Request Data
   * @param pBur Buyer
   * @return list of accepted orders or NULL
   * @throws Exception - an exception
   **/
  public final List<CustOrder> accept(final Map<String, Object> pReqVars,
    final IRequestData pReqDt, final OnlineBuyer pBur) throws Exception {
    List<CustOrder> rez = null;
    try {
      this.srvDatabase.setIsAutocommit(false);
      this.srvDatabase.setTransactionIsolation(ISrvDatabase
        .TRANSACTION_READ_COMMITTED);
      this.srvDatabase.beginTransaction();
      String tbn = CustOrder.class.getSimpleName();
      pReqVars.put(tbn + "buyerdeepLevel", 1);
      pReqVars.put(tbn + "placedeepLevel", 1);
      pReqVars.put(tbn + "currdeepLevel", 1);
      rez = this.srvOrm.retrieveListWithConditions(pReqVars,
        CustOrder.class, "where STAT=0 and BUYER=" + pBur.getItsId());
      pReqVars.remove(tbn + "buyerdeepLevel");
      pReqVars.remove(tbn + "placedeepLevel");
      pReqVars.remove(tbn + "currdeepLevel");
      StringBuffer ordIds = new StringBuffer();
      //Map<Long, Set<Long>> plOrIds = new HashMap<Long, Set<Long>>();
      for (int i=0; i < rez.size(); i++) {
        if (i == 0) {
          ordIds.append(rez.get(i).getItsId().toString());
        } else {
          ordIds.append("," + rez.get(i).getItsId());
        }
      }
      Set<String> ndFl = new HashSet<String>();
      ndFl.add("quant");
      ndFl.add("good");
      tbn = CustOrderGdLn.class.getSimpleName();
      pReqVars.put(tbn + "neededFields", ndFl);
      pReqVars.put(tbn + "gooddeepLevel", 1);
      String quer = lazyGetQuOrGdChk().replace(":ORIDS", ordIds.toString());
      List<CustOrderGdLn> allGoods = this.srvOrm.retrieveListByQuery(
        pReqVars, CustOrderGdLn.class, quer);
      pReqVars.remove(tbn + "gooddeepLevel");
      pReqVars.remove(tbn + "neededFields");
      ndFl.remove("good");
      ndFl.add("service");
      ndFl.add("dt1");
      ndFl.add("dt2");
      tbn = CustOrderSrvLn.class.getSimpleName();
      pReqVars.put(tbn + "neededFields", ndFl);
      pReqVars.put(tbn + "servicedeepLevel", 1);
      List<CustOrderSrvLn> allServs = this.srvOrm.retrieveListWithConditions(
        pReqVars, CustOrderSrvLn.class, "where ITSOWNER in (" + ordIds + ")");
      pReqVars.remove(tbn + "servicedeepLevel");
      pReqVars.remove(tbn + "neededFields");
      this.srvDatabase.commitTransaction();
    } catch (Exception ex) {
      if (!this.srvDatabase.getIsAutocommit()) {
        this.srvDatabase.rollBackTransaction();
      }
      throw ex;
    } finally {
      this.srvDatabase.releaseResources();
    }
    return rez;
  }

  /**
   * <p>Lazy Getter for quOrGdChk.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  public final String lazyGetQuOrGdChk() throws IOException {
    if (this.quOrGdChk == null) {
      String flName = "/webstore/ordGdChk.sql";
      this.quOrGdChk = loadString(flName);
    }
    return this.quOrGdChk;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = AcpOrd.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = AcpOrd.class
          .getResourceAsStream(pFileName);
        byte[] bArray = new byte[inputStream.available()];
        inputStream.read(bArray, 0, inputStream.available());
        return new String(bArray, "UTF-8");
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
      }
    }
    return null;
  }

  //Simple getters and setters:
  /**
   * <p>Geter for logger.</p>
   * @return ILogger
   **/
  public final ILogger getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final void setLogger(final ILogger pLogger) {
    this.logger = pLogger;
  }

  /**
   * <p>Getter for srvDatabase.</p>
   * @return ISrvDatabase<RS>
   **/
  public final ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final void setSrvDatabase(
    final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }

  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }

  /**
   * <p>Setter for quOrGdChk.</p>
   * @param pQuOrGdChk reference
   **/
  public final void setQuOrGdChk(final String pQuOrGdChk) {
    this.quOrGdChk = pQuOrGdChk;
  }
}
