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

import java.util.Map;

import org.beigesoft.model.IRequestData;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.base.AItemPrice;

/**
 * <p>Service that retrieve/create buyer's shopping cart, make cart totals
 * after any line action, etc.
 * This is shared non-transactional service.</p>
 *
 * @author Yury Demidenko
 */
public interface ISrvShoppingCart {

  /**
   * <p>Get/Create Cart.</p>
   * @param pReqVars request scoped vars
   * @param pRequestData Request Data
   * @param pIsNeedToCreate Is Need To Create cart
   * @return shopping cart or null
   * @throws Exception - an exception
   **/
  Cart getShoppingCart(Map<String, Object> pReqVars,
    IRequestData pRequestData,
      boolean pIsNeedToCreate) throws Exception;

  /**
   * <p>Refresh cart totals by seller cause line inserted/changed/deleted.</p>
   * @param pReqVars request scoped vars
   * @param pTs TradingSettings
   * @param pCartLn affected cart line
   * @param pAs Accounting Settings
   * @param pTxRules NULL if not taxable
   * @throws Exception - an exception.
   **/
  void makeCartTotals(Map<String, Object> pReqVars,
    TradingSettings pTs, CartLn pCartLn, AccSettings pAs,
      TaxDestination pTxRules) throws Exception;

  /**
   * <p>Reveal shared tax rules for cart. It also makes buyer-regCustomer.</p>
   * @param pReqVars request scoped vars
   * @param pCart cart
   * @param pAs Accounting Settings
   * @return tax rules, NULL if not taxable
   * @throws Exception - an exception.
   **/
  TaxDestination revealTaxRules(Map<String, Object> pReqVars,
    Cart pCart, AccSettings pAs) throws Exception;

  /**
   * <p>Handle event cart currency changed.</p>
   * @param pReqVars request scoped vars
   * @param pCart cart
   * @param pAs Accounting Settings
   * @param pTs TradingSettings
   * @throws Exception - an exception.
   **/
  void handleCurrencyChanged(Map<String, Object> pReqVars,
    Cart pCart, AccSettings pAs, TradingSettings pTs) throws Exception;

  /**
   * <p>Makes cart line. Tax category, price, seller are already done.</p>
   * @param pReqVars request scoped vars
   * @param pCartLn cart line
   * @param pAs Accounting Settings
   * @param pTs TradingSettings
   * @param pTxRules NULL if not taxable
   * @param pRedoPr redo price
   * @param pRedoTxc redo tax category
   * @throws Exception - an exception.
   **/
  void makeCartLine(Map<String, Object> pReqVars, CartLn pCartLn,
    AccSettings pAs, TradingSettings pTs, TaxDestination pTxRules,
      boolean pRedoPr, boolean pRedoTxc) throws Exception;


  /**
   * <p>Reveals item's price descriptor.</p>
   * @param pReqVars request scoped vars
   * @param pTs TradingSettings
   * @param pBuyer Buyer
   * @param pItType Item Type
   * @param pItId Item ID
   * @return item's price descriptor or exception
   * @throws Exception - an exception
   **/
  AItemPrice<?, ?> revealItemPrice(Map<String, Object> pReqVars,
    TradingSettings pTs, OnlineBuyer pBuyer, EShopItemType pItType,
        Long pItId) throws Exception;
}
