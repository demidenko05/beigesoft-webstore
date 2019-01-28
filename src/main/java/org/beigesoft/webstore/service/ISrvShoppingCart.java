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
   * <p>Empties Cart.</p>
   * @param pRqVs request scoped vars
   * @param pBuyr buyer
   * @throws Exception - an exception
   **/
  void emptyCart(Map<String, Object> pRqVs,
    OnlineBuyer pBuyr) throws Exception;

  /**
   * <p>Get/Create Cart.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @param pIsNeedToCreate if need to create, e.g. "NO" for deleting item from
   *  cart, "YES" for adding one.
   * @param pIsBuAuth buyer must be authorized
   * @return shopping cart or null
   * @throws Exception - an exception
   **/
  Cart getShoppingCart(Map<String, Object> pRqVs,
    IRequestData pRqDt, boolean pIsNeedToCreate,
      boolean pIsBuAuth) throws Exception;

  /**
   * <p>Refresh cart totals by seller cause line inserted/changed/deleted.</p>
   * @param pRqVs request scoped vars
   * @param pTs TradingSettings
   * @param pCartLn affected cart line
   * @param pAs Accounting Settings
   * @param pTxRules NULL if not taxable
   * @throws Exception - an exception.
   **/
  void makeCartTotals(Map<String, Object> pRqVs,
    TradingSettings pTs, CartLn pCartLn, AccSettings pAs,
      TaxDestination pTxRules) throws Exception;

  /**
   * <p>Reveal shared tax rules for cart. It also makes buyer-regCustomer.</p>
   * @param pRqVs request scoped vars
   * @param pCart cart
   * @param pAs Accounting Settings
   * @return tax rules, NULL if not taxable
   * @throws Exception - an exception.
   **/
  TaxDestination revealTaxRules(Map<String, Object> pRqVs,
    Cart pCart, AccSettings pAs) throws Exception;

  /**
   * <p>Handle event cart currency changed.</p>
   * @param pRqVs request scoped vars
   * @param pCart cart
   * @param pAs Accounting Settings
   * @param pTs TradingSettings
   * @throws Exception - an exception.
   **/
  void handleCurrencyChanged(Map<String, Object> pRqVs,
    Cart pCart, AccSettings pAs, TradingSettings pTs) throws Exception;

  /**
   * <p>Handle event cart delivering or line changed.</p>
   * @param pRqVs request scoped vars
   * @param pCart cart
   * @param pTxRules Tax Rules
   * @throws Exception - an exception.
   **/
  void hndCartChan(Map<String, Object> pRqVs,
    Cart pCart, TaxDestination pTxRules) throws Exception;
  /**
   * <p>Deletes cart line.</p>
   * @param pRqVs request scoped vars
   * @param pCartLn cart line
   * @param pTxRules Tax Rules
   * @throws Exception - an exception.
   **/
  void delLine(Map<String, Object> pRqVs,
    CartLn pCartLn, TaxDestination pTxRules) throws Exception;
  /**
   * <p>Makes cart line. Tax category, price, seller are already done.</p>
   * @param pRqVs request scoped vars
   * @param pCartLn cart line
   * @param pAs Accounting Settings
   * @param pTs TradingSettings
   * @param pTxRules NULL if not taxable
   * @param pRedoPr redo price
   * @param pRedoTxc redo tax category
   * @throws Exception - an exception.
   **/
  void makeCartLine(Map<String, Object> pRqVs, CartLn pCartLn,
    AccSettings pAs, TradingSettings pTs, TaxDestination pTxRules,
      boolean pRedoPr, boolean pRedoTxc) throws Exception;


  /**
   * <p>Reveals item's price descriptor.</p>
   * @param pRqVs request scoped vars
   * @param pTs TradingSettings
   * @param pBuyer Buyer
   * @param pItType Item Type
   * @param pItId Item ID
   * @return item's price descriptor or exception
   * @throws Exception - an exception
   **/
  AItemPrice<?, ?> revealItemPrice(Map<String, Object> pRqVs,
    TradingSettings pTs, OnlineBuyer pBuyer, EShopItemType pItType,
        Long pItId) throws Exception;
}
