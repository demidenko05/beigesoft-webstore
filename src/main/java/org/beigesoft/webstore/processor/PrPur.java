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
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.log.ILog;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.webstore.model.EPaymentMethod;
import org.beigesoft.webstore.model.Purch;
import org.beigesoft.webstore.persistable.CuOrSeTxLn;
import org.beigesoft.webstore.persistable.CuOrSe;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.persistable.CustOrderTxLn;
import org.beigesoft.webstore.persistable.SettingsAdd;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.service.ISrvShoppingCart;
import org.beigesoft.webstore.service.IAcpOrd;
import org.beigesoft.webstore.service.IBuySr;

/**
 * <p>
 * Service that accepts purchase (books orders) that will be payed offline.
 * </p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrPur<RS> implements IProcessor {

  /**
   * <p>Logger.</p>
   **/
  private ILog log;

  /**
   * <p>Logger security.</p>
   **/
  private ILog secLog;

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDb;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Shopping Cart service.</p>
   **/
  private ISrvShoppingCart srvCart;

  /**
   * <p>Accept buyer's new orders service.</p>
   **/
  private IAcpOrd acpOrd;

  /**
   * <p>Buyer service.</p>
   **/
  private IBuySr buySr;

  /**
   * <p>Processors factory.</p>
   **/
  private IFactoryAppBeansByName<IProcessor> procFac;

  /**
   * <p>Process entity request.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    if (!pRqDt.getReqUrl().toString().toLowerCase().startsWith("https")) {
      throw new Exception("http not supported!!!");
    }
    OnlineBuyer buyer = this.buySr.getAuthBuyr(pRqVs, pRqDt);
    if (buyer == null) {
      String procNm = pRqDt.getParameter("nmPrcRed");
      IProcessor proc = this.procFac.lazyGet(pRqVs, procNm);
      proc.process(pRqVs, pRqDt);
      return;
    }
    SettingsAdd setAdd = (SettingsAdd) pRqVs.get("setAdd");
    try {
      this.srvDb.setIsAutocommit(false);
      this.srvDb.setTransactionIsolation(setAdd.getBkTr());
      this.srvDb.beginTransaction();
      String tbn;
      Purch pur = this.acpOrd.accept(pRqVs, pRqDt, buyer);
      this.srvCart.emptyCart(pRqVs, buyer);
      if (pur.getOrds() != null && pur.getOrds().size() > 0) {
        //checking orders with online payment:
        tbn = CustOrderTxLn.class.getSimpleName();
        pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
        for (CustOrder or : pur.getOrds()) { //TODO PERFORM to IAcpOrd
          if (or.getPayMeth().equals(EPaymentMethod.PAYPAL)
            || or.getPayMeth().equals(EPaymentMethod.PAYPAL_ANY)
              || or.getPayMeth().equals(EPaymentMethod.PARTIAL_ONLINE)
                || or.getPayMeth().equals(EPaymentMethod.ONLINE)) {
            throw new Exception("It must be offline payment!!");
          }
          if (or.getTotTx().compareTo(BigDecimal.ZERO) == 1) {
            List<CustOrderTxLn> tls = getSrvOrm().retrieveListWithConditions(
              pRqVs, CustOrderTxLn.class, "where ITSOWNER=" + or.getItsId());
            or.setTaxes(tls);
          }
        }
        pRqVs.remove(tbn + "itsOwnerdeepLevel");
      }
      if (pur.getSords() != null && pur.getSords().size() > 0) {
        //checking S.E. orders with online payment:
        tbn = CuOrSeTxLn.class.getSimpleName();
        pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
        for (CuOrSe or : pur.getSords()) {
          if (or.getPayMeth().equals(EPaymentMethod.PAYPAL)
            || or.getPayMeth().equals(EPaymentMethod.PAYPAL_ANY)
              || or.getPayMeth().equals(EPaymentMethod.PARTIAL_ONLINE)
                || or.getPayMeth().equals(EPaymentMethod.ONLINE)) {
            throw new Exception("It must be offline payment!!");
          }
          if (or.getTotTx().compareTo(BigDecimal.ZERO) == 1) {
            List<CuOrSeTxLn> tls = getSrvOrm().retrieveListWithConditions(
              pRqVs, CuOrSeTxLn.class, "where ITSOWNER=" + or.getItsId());
            or.setTaxes(tls);
          }
          pRqVs.remove(tbn + "itsOwnerdeepLevel");
        }
      }
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
   * @return ILog
   **/
  public final ILog getLog() {
    return this.log;
  }

  /**
   * <p>Setter for log.</p>
   * @param pLog reference
   **/
  public final void setLog(final ILog pLog) {
    this.log = pLog;
  }

  /**
   * <p>Getter for secLog.</p>
   * @return ILog
   **/
  public final ILog getSecLog() {
    return this.secLog;
  }

  /**
   * <p>Setter for secLog.</p>
   * @param pSecLog reference
   **/
  public final void setSecLog(final ILog pSecLog) {
    this.secLog = pSecLog;
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
   * <p>Getter for srvCart.</p>
   * @return ISrvShoppingCart
   **/
  public final ISrvShoppingCart getSrvCart() {
    return this.srvCart;
  }

  /**
   * <p>Setter for srvCart.</p>
   * @param pSrvCart reference
   **/
  public final void setSrvCart(final ISrvShoppingCart pSrvCart) {
    this.srvCart = pSrvCart;
  }

  /**
   * <p>Getter for acpOrd.</p>
   * @return IAcpOrd
   **/
  public final IAcpOrd getAcpOrd() {
    return this.acpOrd;
  }

  /**
   * <p>Setter for acpOrd.</p>
   * @param pAcpOrd reference
   **/
  public final void setAcpOrd(final IAcpOrd pAcpOrd) {
    this.acpOrd = pAcpOrd;
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
