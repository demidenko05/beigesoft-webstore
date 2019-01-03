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

import org.beigesoft.model.IRequestData;
import org.beigesoft.log.ILogger;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.webstore.model.EOrdStat;
import org.beigesoft.webstore.model.EPaymentMethod;
import org.beigesoft.webstore.model.Purch;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CuOrSe;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.persistable.SettingsAdd;
import org.beigesoft.webstore.service.ISrvShoppingCart;
import org.beigesoft.webstore.service.IAcpOrd;
import org.beigesoft.webstore.service.ICncOrd;

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
  private ILogger log;

  /**
   * <p>Logger security.</p>
   **/
  private ILogger secLog;

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
   * <p>Cancel accepted buyer's orders service.</p>
   **/
  private ICncOrd cncOrd;

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
    Cart cart = null;
    SettingsAdd setAdd = (SettingsAdd) pRqVs.get("setAdd");
    try {
      this.srvDb.setIsAutocommit(false);
      this.srvDb.setTransactionIsolation(setAdd.getBkTr());
      this.srvDb.beginTransaction();
      cart = this.srvCart.getShoppingCart(pRqVs, pRqDt, false);
      if (cart != null && cart.getErr()) {
        cart = null;
      }
      this.srvDb.commitTransaction();
    } catch (Exception ex) {
      if (!this.srvDb.getIsAutocommit()) {
        this.srvDb.rollBackTransaction();
      }
      throw ex;
    } finally {
      this.srvDb.releaseResources();
    }
    if (cart == null) {
      //TODO handle spam
      return;
    }
    Purch pur = this.acpOrd.accept(pRqVs, pRqDt, cart.getBuyer());
    if (pur != null) {
      try {
        this.srvDb.setIsAutocommit(false);
        this.srvDb.setTransactionIsolation(setAdd.getBkTr());
        this.srvDb.beginTransaction();
        this.srvCart.emptyCart(pRqVs, cart.getBuyer());
        this.srvDb.commitTransaction();
        if (pur.getOrds() != null && pur.getOrds().size() > 0) {
          //checking orders with online payment:
          for (CustOrder or : pur.getOrds()) {
            if (or.getPayMeth().equals(EPaymentMethod.PAYPAL)
              || or.getPayMeth().equals(EPaymentMethod.PAYPAL_ANY)
                || or.getPayMeth().equals(EPaymentMethod.PARTIAL_ONLINE)
                  || or.getPayMeth().equals(EPaymentMethod.ONLINE)) {
              throw new Exception("It must by offline payment!!");
            }
          }
        }
        if (pur.getSords() != null && pur.getSords().size() > 0) {
          //checking S.E. orders with online payment:
          for (CuOrSe or : pur.getSords()) {
            if (or.getPayMeth().equals(EPaymentMethod.PAYPAL)
              || or.getPayMeth().equals(EPaymentMethod.PAYPAL_ANY)
                || or.getPayMeth().equals(EPaymentMethod.PARTIAL_ONLINE)
                  || or.getPayMeth().equals(EPaymentMethod.ONLINE)) {
              throw new Exception("It must by offline payment!!");
            }
          }
        }
      } catch (Exception ex) {
        if (!this.srvDb.getIsAutocommit()) {
          this.srvDb.rollBackTransaction();
        }
        this.cncOrd.cancel(pRqVs, pur, EOrdStat.NEW);
        throw ex;
      } finally {
        this.srvDb.releaseResources();
      }
    }
    pRqDt.setAttribute("pur",  pur);
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
   * <p>Getter for secLog.</p>
   * @return ILogger
   **/
  public final ILogger getSecLog() {
    return this.secLog;
  }

  /**
   * <p>Setter for secLog.</p>
   * @param pSecLog reference
   **/
  public final void setSecLog(final ILogger pSecLog) {
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
   * <p>Getter for cncOrd.</p>
   * @return ICncOrd
   **/
  public final ICncOrd getCncOrd() {
    return this.cncOrd;
  }

  /**
   * <p>Setter for cncOrd.</p>
   * @param pCncOrd reference
   **/
  public final void setCncOrd(final ICncOrd pCncOrd) {
    this.cncOrd = pCncOrd;
  }
}
