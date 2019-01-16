package org.beigesoft.webstore.service;

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

import java.util.List;
import java.util.Map;
import java.util.Date;

import org.beigesoft.model.IRequestData;
import org.beigesoft.handler.ISpamHnd;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.webstore.persistable.OnlineBuyer;

/**
 * <p>Buyer's service.</p>
 *
 * @param <RS> platform dependent RDBMS recordset
 * @author Yury Demidenko
 */
public class BuySr<RS> implements IBuySr {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Spam handler.</p>
   **/
  private ISpamHnd spamHnd;

  /**
   * <p>Get authorized buyer. It refresh authorized buyer last time
   * and set request variable  "buyr".</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @return authorized buyer or null
   * @throws Exception - an exception
   **/
  @Override
  public final OnlineBuyer getAuthBuyr(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    String buyerIdStr = pRqDt.getCookieValue("cBuyerId");
    if (buyerIdStr == null) {
      this.spamHnd.handle(pRqVs, pRqDt, 1, "Buyer. has no cBuyerId!");
      return null;
    }
    Long buyerId = Long.valueOf(buyerIdStr);
    OnlineBuyer buyer = getSrvOrm().retrieveEntityById(pRqVs,
      OnlineBuyer.class, buyerId);
    if (buyer == null) {
      this.spamHnd.handle(pRqVs, pRqDt, 1, "Buyer. DB has no cBuyerId: "
        + buyerIdStr);
      return null;
    }
    if (buyer.getRegEmail() == null || buyer.getBuSeId() == null) {
      this.spamHnd.handle(pRqVs, pRqDt, 1, "Buyer. Unauthorized cBuyerId: "
        + buyerIdStr);
      return null;
    }
    String buSeId = pRqDt.getCookieValue("buSeId");
    if (!buyer.getBuSeId().equals(buSeId)) {
      this.spamHnd.handle(pRqVs, pRqDt, 1000,
        "Buyer. Authorized invasion cBuyerId: " + buyerIdStr);
      return null;
    }
    long now = new Date().getTime();
    if (now - buyer.getLsTm() > 1800000L) {
      this.spamHnd.handle(pRqVs, pRqDt, 0,
        "Buyer. Authorized exceed cBuyerId/ms: " + buyerIdStr + "/"
          + (now - buyer.getLsTm()));
      return null;
    }
    buyer.setLsTm(now);
    String[] fieldsNames = new String[] {"itsId", "itsVersion", "lsTm"};
    pRqVs.put("fieldsNames", fieldsNames);
    buyer.setLsTm(now);
    this.srvOrm.updateEntity(pRqVs, buyer);
    pRqVs.remove("fieldsNames");
    pRqDt.setAttribute("buyr", buyer);
    return buyer;
  }

  /**
   * <p>Get authorized or not buyer.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @return buyer or null
   * @throws Exception - an exception
   **/
  @Override
  public final OnlineBuyer getBuyr(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    Long buyerId = null;
    String buyerIdStr = pRqDt.getCookieValue("cBuyerId");
    if (buyerIdStr != null && buyerIdStr.length() > 0) {
       buyerId = Long.valueOf(buyerIdStr);
    }
    OnlineBuyer buyer = null;
    if (buyerId != null) {
      buyer = getSrvOrm().retrieveEntityById(pRqVs, OnlineBuyer.class, buyerId);
    }
    if (buyer != null && buyer.getRegEmail() != null
      && buyer.getBuSeId() != null) {
      String buSeId = pRqDt.getCookieValue("buSeId");
      if (!buyer.getBuSeId().equals(buSeId)) {
        this.spamHnd.handle(pRqVs, pRqDt, 100,
          "Buyer. Authorized invasion? cBuyerId: " + buyerIdStr);
        //buyer also might clears cookie, so it's need new authorization
      }
    }
    return buyer;
  }

  /**
   * <p>Creates buyer without saving into DB.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @return created buyer will be unsaved into DB!
   * @throws Exception - an exception
   **/
  @Override
  public final OnlineBuyer createBuyr(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    OnlineBuyer buyer = null;
    String tbn =  OnlineBuyer.class.getSimpleName();
    pRqVs.put(tbn + "regCustomerdeepLevel", 1);
    pRqVs.put(tbn + "taxDestplacedeepLevel", 1);
    List<OnlineBuyer> brs = getSrvOrm().retrieveListWithConditions(pRqVs,
      OnlineBuyer.class, "where FRE=1 and REGISTEREDPASSWORD is null");
    pRqVs.remove(tbn + "regCustomerdeepLevel");
    pRqVs.remove(tbn + "taxDestplacedeepLevel");
    if (brs.size() > 0) {
      double rd = Math.random();
      if (rd > 0.5) {
        buyer = brs.get(brs.size() - 1);
      } else {
        buyer = brs.get(0);
      }
      buyer.setRegisteredPassword(null);
      buyer.setRegEmail(null);
      buyer.setFre(false);
    }
    if (buyer == null) {
      buyer = new OnlineBuyer();
      buyer.setIsNew(true);
      buyer.setItsName("newbe" + new Date().getTime());
    }
    return buyer;
  }

  //Simple getters and setters:
  /**
   * <p>Geter for srvOrm.</p>
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
   * <p>Getter for spamHnd.</p>
   * @return ISpamHnd
   **/
  public final ISpamHnd getSpamHnd() {
    return this.spamHnd;
  }

  /**
   * <p>Setter for spamHnd.</p>
   * @param pSpamHnd reference
   **/
  public final void setSpamHnd(final ISpamHnd pSpamHnd) {
    this.spamHnd = pSpamHnd;
  }
}
