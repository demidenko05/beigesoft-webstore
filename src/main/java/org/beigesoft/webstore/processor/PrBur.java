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
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.beigesoft.model.IRequestData;
import org.beigesoft.log.ILogger;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.webstore.model.Purch;
import org.beigesoft.webstore.persistable.CuOrSeGdLn;
import org.beigesoft.webstore.persistable.CuOrSeSrLn;
import org.beigesoft.webstore.persistable.CuOrSe;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.persistable.CustOrderGdLn;
import org.beigesoft.webstore.persistable.CustOrderSrvLn;
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
      String whePuBr = "where PUR=" + purIdStr + " and BUYER="
        + buyer.getItsId();
      Set<String> ndFlNm = new HashSet<String>();
      ndFlNm.add("itsId");
      ndFlNm.add("itsName");
      pRqVs.put("PickUpPlaceneededFields", ndFlNm);
      pRqVs.put(tbn + "buyerdeepLevel", 1);
      List<CustOrder> ords = this.srvOrm.retrieveListWithConditions(pRqVs,
        CustOrder.class, whePuBr);
      pRqVs.remove(tbn + "buyerdeepLevel");
      tbn = CuOrSe.class.getSimpleName();
      Set<String> ndFlDc = new HashSet<String>();
      ndFlDc.add("seller");
      pRqVs.put("DebtorCreditorneededFields", ndFlNm);
      pRqVs.put("SeSellerneededFields", ndFlDc);
      pRqVs.put(tbn + "seldeepLevel", 3);
      pRqVs.put(tbn + "buyerdeepLevel", 1);
      List<CuOrSe> sords = this.srvOrm.retrieveListWithConditions(pRqVs,
        CuOrSe.class, whePuBr);
      pRqVs.remove(tbn + "buyerdeepLevel");
      pRqVs.remove("DebtorCreditorneededFields");
      pRqVs.remove("SeSellerneededFields");
      pRqVs.remove(tbn + "seldeepLevel");
      pRqVs.remove("PickUpPlaceneededFields");
      retLines(pRqVs, buyer, ords, sords);
      Purch pur = new Purch();
      pur.setOrds(ords);
      pur.setSords(sords);
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

  /**
   * <p>Retrieve order lines.</p>
   * @param pRqVs request scoped vars
   * @param pBur buyer
   * @param pOrds orders
   * @param pSords S.E. orders
   * @throws Exception - an exception
   **/
  public final void retLines(final Map<String, Object> pRqVs,
    final OnlineBuyer pBur, final List<CustOrder> pOrds,
      final List<CuOrSe> pSords) throws Exception {
    StringBuffer ordIds = null;
    for (CustOrder co : pOrds) {
      co.setGoods(new ArrayList<CustOrderGdLn>());
      co.setServs(new ArrayList<CustOrderSrvLn>());
      if (ordIds == null) {
        ordIds = new StringBuffer();
        ordIds.append(co.getItsId().toString());
      } else {
        ordIds.append("," + co.getItsId());
      }
    }
    Set<String> ndFlNm = new HashSet<String>();
    ndFlNm.add("itsId");
    ndFlNm.add("itsName");
    Set<String> ndFl = new HashSet<String>();
    ndFl.add("itsId");
    ndFl.add("itsOwner");
    ndFl.add("itsName");
    ndFl.add("uom");
    ndFl.add("quant");
    ndFl.add("price");
    ndFl.add("tot");
    ndFl.add("totTx");
    String tbn;
    if (ordIds != null) {
      tbn = CustOrderGdLn.class.getSimpleName();
      pRqVs.put(tbn + "neededFields", ndFl);
      pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
      pRqVs.put("UnitOfMeasureneededFields", ndFlNm);
      List<CustOrderGdLn> allGds = this.srvOrm.retrieveListWithConditions(pRqVs,
        CustOrderGdLn.class, "where ITSOWNER in (" + ordIds + ")");
      pRqVs.remove(tbn + "neededFields");
      pRqVs.remove(tbn + "itsOwnerdeepLevel");
      pRqVs.remove("UnitOfMeasureneededFields");
      for (CustOrderGdLn il : allGds) {
        for (CustOrder co : pOrds) {
          if (co.getItsId().equals(il.getItsOwner().getItsId())) {
            co.getGoods().add(il);
            break;
          }
        }
      }
      tbn = CustOrderSrvLn.class.getSimpleName();
      pRqVs.put(tbn + "neededFields", ndFl);
      pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
      pRqVs.put("UnitOfMeasureneededFields", ndFlNm);
      List<CustOrderSrvLn> allSrs = this.srvOrm.retrieveListWithConditions(
        pRqVs, CustOrderSrvLn.class, "where ITSOWNER in (" + ordIds + ")");
      pRqVs.remove(tbn + "neededFields");
      pRqVs.remove(tbn + "itsOwnerdeepLevel");
      pRqVs.remove("UnitOfMeasureneededFields");
      for (CustOrderSrvLn il : allSrs) {
        for (CustOrder co : pOrds) {
          if (co.getItsId().equals(il.getItsOwner().getItsId())) {
            co.getServs().add(il);
            break;
          }
        }
      }
    }
    ordIds = null;
    for (CuOrSe co : pSords) {
      co.setGoods(new ArrayList<CuOrSeGdLn>());
      co.setServs(new ArrayList<CuOrSeSrLn>());
      if (ordIds == null) {
        ordIds = new StringBuffer();
        ordIds.append(co.getItsId().toString());
      } else {
        ordIds.append("," + co.getItsId());
      }
    }
    if (ordIds != null) {
      tbn = CuOrSeGdLn.class.getSimpleName();
      pRqVs.put(tbn + "neededFields", ndFl);
      pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
      pRqVs.put("UnitOfMeasureneededFields", ndFlNm);
      List<CuOrSeGdLn> allGds = this.srvOrm.retrieveListWithConditions(pRqVs,
        CuOrSeGdLn.class, "where ITSOWNER in (" + ordIds + ")");
      pRqVs.remove(tbn + "neededFields");
      pRqVs.remove(tbn + "itsOwnerdeepLevel");
      pRqVs.remove("UnitOfMeasureneededFields");
      for (CuOrSeGdLn il : allGds) {
        for (CuOrSe co : pSords) {
          if (co.getItsId().equals(il.getItsOwner().getItsId())) {
            co.getGoods().add(il);
            break;
          }
        }
      }
      tbn = CuOrSeSrLn.class.getSimpleName();
      pRqVs.put(tbn + "neededFields", ndFl);
      pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
      pRqVs.put("UnitOfMeasureneededFields", ndFlNm);
      List<CuOrSeSrLn> allSrs = this.srvOrm.retrieveListWithConditions(
        pRqVs, CuOrSeSrLn.class, "where ITSOWNER in (" + ordIds + ")");
      pRqVs.remove(tbn + "neededFields");
      pRqVs.remove(tbn + "itsOwnerdeepLevel");
      pRqVs.remove("UnitOfMeasureneededFields");
      for (CuOrSeSrLn il : allSrs) {
        for (CuOrSe co : pSords) {
          if (co.getItsId().equals(il.getItsOwner().getItsId())) {
            co.getServs().add(il);
            break;
          }
        }
      }
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
