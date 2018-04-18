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
import java.math.BigDecimal;

import org.beigesoft.model.ICookie;
import org.beigesoft.model.CookieTmp;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.ShoppingCart;
import org.beigesoft.webstore.persistable.CartItem;
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
   * <p>Get/Create ShoppingCart.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @param pIsNeedToCreate Is Need To Create cart
   * @return shopping cart or null
   * @throws Exception - an exception
   **/
  @Override
  public final ShoppingCart getShoppingCart(final Map<String, Object> pAddParam,
    final IRequestData pRequestData,
      final boolean pIsNeedToCreate) throws Exception {
    ICookie[] cookies = pRequestData.getCookies();
    Long buyerId = null;
    ICookie cookieWas = null;
    if (cookies != null) {
      for (ICookie cookie : cookies) {
        if (cookie.getName().equals("cBuyerId")) {
          buyerId = Long.valueOf(cookie.getValue());
          cookieWas = cookie;
        }
      }
    }
    OnlineBuyer onlineBuyer;
    if (buyerId == null) {
      TradingSettings tradingSettings = srvTradingSettings
        .lazyGetTradingSettings(pAddParam);
      if (pIsNeedToCreate
        || tradingSettings.getIsCreateOnlineUserOnFirstVisit()) {
        onlineBuyer = createOnlineBuyer(pAddParam, pRequestData);
        CookieTmp cookie = new CookieTmp();
        cookie.setName("cBuyerId");
        cookie.setValue(onlineBuyer.getItsId().toString());
        pRequestData.addCookie(cookie);
      } else {
        return null;
      }
    } else {
      onlineBuyer = getSrvOrm()
        .retrieveEntityById(pAddParam, OnlineBuyer.class, buyerId);
      if (onlineBuyer == null) { // deleted for any reason, so create new:
        onlineBuyer = createOnlineBuyer(pAddParam, pRequestData);
        cookieWas.setValue(onlineBuyer.getItsId().toString());
      }
    }
    ShoppingCart shoppingCart = getSrvOrm()
      .retrieveEntityById(pAddParam, ShoppingCart.class, onlineBuyer);
    if (shoppingCart != null) {
      CartItem ci = new CartItem();
      ci.setItsOwner(shoppingCart);
      List<CartItem> cartItems = getSrvOrm()
        .retrieveListForField(pAddParam, ci, "itsOwner");
      shoppingCart.setItsItems(cartItems);
    } else if (pIsNeedToCreate) {
      shoppingCart = new ShoppingCart();
      shoppingCart.setItsId(onlineBuyer);
      shoppingCart.setBuyer(onlineBuyer);
      shoppingCart.setItsTotal(BigDecimal.ZERO);
      shoppingCart.setTotalItems(0);
      getSrvOrm().insertEntity(pAddParam, shoppingCart);
    }
    return shoppingCart;
  }

  /**
   * <p>Create OnlineBuyer.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @return shopping cart or null
   * @throws Exception - an exception
   **/
  public final OnlineBuyer createOnlineBuyer(
    final Map<String, Object> pAddParam,
      final IRequestData pRequestData) throws Exception {
    OnlineBuyer onlineBuyer = new OnlineBuyer();
    onlineBuyer.setIsNew(true);
    onlineBuyer.setItsName("newbe" + new Date());
    getSrvOrm().insertEntity(pAddParam, onlineBuyer);
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
