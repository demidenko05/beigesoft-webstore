package org.beigesoft.webstore.processor;

/*
 * Copyright (c) 2019 Beigesoft™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import java.util.Map;
import java.util.List;

import org.beigesoft.model.IRequestData;
import org.beigesoft.log.ILogger;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.webstore.model.Purch;
//import org.beigesoft.webstore.persistable.CuOrSe;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.persistable.SettingsAdd;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.service.IBuySr;

/**
 * <p>
 * Service that shows buyer's orders from just made purchase.
 * </p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrBur<RS> implements IProcessor {

  /**
   * <p>Logger.</p>
   **/
  private ILogger log;

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDb;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Buyer service.</p>
   **/
  private IBuySr buySr;

  /**
   * <p>Processors factory.</p>
   **/
  private IFactoryAppBeansByName<IProcessor> procFac;

  /**
   * <p>Process request.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    OnlineBuyer buyer = this.buySr.getAuthBuyr(pRqVs, pRqDt);
    if (buyer == null) {
      String procNm = pRqDt.getParameter("nmPrcRed");
      IProcessor proc = this.procFac.lazyGet(pRqVs, procNm);
      proc.process(pRqVs, pRqDt);
      return;
    }
    SettingsAdd setAdd = (SettingsAdd) pRqVs.get("setAdd");
    String purIdStr = pRqDt.getParameter("pur");
    Long.parseLong(purIdStr);
    try {
      this.srvDb.setIsAutocommit(false);
      this.srvDb.setTransactionIsolation(setAdd.getBkTr());
      this.srvDb.beginTransaction();
      String tbn = CustOrder.class.getSimpleName();
      pRqVs.put(tbn + "buyerdeepLevel", 1);
      pRqVs.put(tbn + "placedeepLevel", 1);
      pRqVs.put(tbn + "currdeepLevel", 1);
      List<CustOrder> ords = this.srvOrm.retrieveListWithConditions(pRqVs,
        CustOrder.class, "where PUR=" + purIdStr + " and BUYER="
          + buyer.getItsId());
      pRqVs.remove(tbn + "buyerdeepLevel");
      pRqVs.remove(tbn + "placedeepLevel");
      pRqVs.remove(tbn + "currdeepLevel");
      Purch pur = new Purch();
      pur.setOrds(ords);
      pRqDt.setAttribute("pur",  pur);
      this.srvDb.commitTransaction();
    } catch (Exception ex) {
      if (!this.srvDb.getIsAutocommit()) {
        this.srvDb.rollBackTransaction();
      }
      throw ex;
    } finally {
      this.srvDb.releaseResources();
    }
  }

  //Simple getters and setters:
  /**
   * <p>Getter for log.</p>
   * @return ILogger
   **/
  public final ILogger getLog() {
    return this.log;
  }

  /**
   * <p>Setter for log.</p>
   * @param pLog reference
   **/
  public final void setLog(final ILogger pLog) {
    this.log = pLog;
  }

  /**
   * <p>Getter for srvDb.</p>
   * @return ISrvDatabase<RS>
   **/
  public final ISrvDatabase<RS> getSrvDb() {
    return this.srvDb;
  }

  /**
   * <p>Setter for srvDb.</p>
   * @param pSrvDb reference
   **/
  public final void setSrvDb(final ISrvDatabase<RS> pSrvDb) {
    this.srvDb = pSrvDb;
  }

  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvDatabase<RS>
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
   * <p>Getter for buySr.</p>
   * @return IBuySr
   **/
  public final IBuySr getBuySr() {
    return this.buySr;
  }

  /**
   * <p>Setter for buySr.</p>
   * @param pBuySr reference
   **/
  public final void setBuySr(final IBuySr pBuySr) {
    this.buySr = pBuySr;
  }

  /**
   * <p>Getter for procFac.</p>
   * @return IFactoryAppBeansByName<IProcessor>
   **/
  public final IFactoryAppBeansByName<IProcessor> getProcFac() {
    return this.procFac;
  }

  /**
   * <p>Setter for procFac.</p>
   * @param pProcFac reference
   **/
  public final void setProcFac(
    final IFactoryAppBeansByName<IProcessor> pProcFac) {
    this.procFac = pProcFac;
  }
}
