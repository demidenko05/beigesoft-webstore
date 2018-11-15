package org.beigesoft.webstore.service;

/*
 * Copyright (c) 2018 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import java.util.Date;
import java.util.Map;
import java.util.List;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.Currency;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.CartTxLn;
import org.beigesoft.webstore.persistable.TradingSettings;

/**
 * <p>Service that retrieve/create buyer's shopping cart.
 * This is non-transactional service</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class SrvShoppingCart<RS> implements ISrvShoppingCart {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for trading settings.</p>
   **/
  private ISrvTradingSettings srvTradingSettings;

  /**
   * <p>Get/Create Cart.</p>
   * @param pReqVars additional param
   * @param pRequestData Request Data
   * @param pIsNeedToCreate Is Need To Create cart
   * @return shopping cart or null
   * @throws Exception - an exception
   **/
  @Override
  public final Cart getShoppingCart(final Map<String, Object> pReqVars,
    final IRequestData pRequestData,
      final boolean pIsNeedToCreate) throws Exception {
    Long buyerId = null;
    String buyerIdStr = pRequestData.getCookieValue("cBuyerId");
    if (buyerIdStr != null && buyerIdStr.length() > 0) {
       buyerId = Long.valueOf(buyerIdStr);
    }
    OnlineBuyer onlineBuyer;
    if (buyerId == null) {
      TradingSettings tradingSettings = srvTradingSettings
        .lazyGetTradingSettings(pReqVars);
      if (pIsNeedToCreate
        || tradingSettings.getIsCreateOnlineUserOnFirstVisit()) {
        onlineBuyer = createOnlineBuyer(pReqVars, pRequestData);
        pRequestData.setCookieValue("cBuyerId", onlineBuyer.getItsId()
          .toString());
      } else {
        return null;
      }
    } else {
      onlineBuyer = getSrvOrm()
        .retrieveEntityById(pReqVars, OnlineBuyer.class, buyerId);
      if (onlineBuyer == null) { // deleted for any reason, so create new:
        onlineBuyer = createOnlineBuyer(pReqVars, pRequestData);
        pRequestData.setCookieValue("cBuyerId", onlineBuyer.getItsId()
          .toString());
      }
    }
    Cart shoppingCart = getSrvOrm()
      .retrieveEntityById(pReqVars, Cart.class, onlineBuyer);
    if (shoppingCart != null) {
      CartLn ci = new CartLn();
      ci.setItsOwner(shoppingCart);
      pReqVars.put("CartLnitsOwnerdeepLevel", 1);
      List<CartLn> cartItems = getSrvOrm()
        .retrieveListForField(pReqVars, ci, "itsOwner");
      shoppingCart.setItems(cartItems);
      pReqVars.remove("CartLnitsOwnerdeepLevel");
      pReqVars.put("CartTxLnitsOwnerdeepLevel", 1);
      List<CartTxLn> ctls = getSrvOrm().retrieveListWithConditions(
          pReqVars, CartTxLn.class, "where CARTID="
            + shoppingCart.getBuyer().getItsId());
      pReqVars.remove("CartTxLnitsOwnerdeepLevel");
      shoppingCart.setTaxes(ctls);
    } else if (pIsNeedToCreate) {
      shoppingCart = new Cart();
      shoppingCart.setItsId(onlineBuyer);
      getSrvOrm().insertEntity(pReqVars, shoppingCart);
    }
    if (shoppingCart != null) {
      Currency curr = (Currency) pReqVars.get("wscurr");
      shoppingCart.setCurr(curr);
      shoppingCart.setBuyer(onlineBuyer);
    }
    return shoppingCart;
  }

  /**
   * <p>Create OnlineBuyer.</p>
   * @param pReqVars additional param
   * @param pRequestData Request Data
   * @return shopping cart or null
   * @throws Exception - an exception
   **/
  public final OnlineBuyer createOnlineBuyer(
    final Map<String, Object> pReqVars,
      final IRequestData pRequestData) throws Exception {
    OnlineBuyer onlineBuyer = new OnlineBuyer();
    onlineBuyer.setIsNew(true);
    onlineBuyer.setItsName("newbe" + new Date().getTime());
    getSrvOrm().insertEntity(pReqVars, onlineBuyer);
    return onlineBuyer;
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
   * <p>Getter for srvTradingSettings.</p>
   * @return ISrvTradingSettings
   **/
  public final ISrvTradingSettings getSrvTradingSettings() {
    return this.srvTradingSettings;
  }

  /**
   * <p>Setter for srvTradingSettings.</p>
   * @param pSrvTradingSettings reference
   **/
  public final void setSrvTradingSettings(
    final ISrvTradingSettings pSrvTradingSettings) {
    this.srvTradingSettings = pSrvTradingSettings;
  }
}
