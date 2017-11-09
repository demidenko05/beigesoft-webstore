package org.beigesoft.webstore.processor;

/*
 * Copyright (c) 2017 Beigesoft ™
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
import java.math.BigDecimal;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IProcessor;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.webstore.persistable.ShoppingCart;
import org.beigesoft.webstore.persistable.CartItem;

/**
 * <p>Service that delete item from cart.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcDelItemFromCart<RS> implements IProcessor {

  /**
   * <p>Query cart totals.</p>
   **/
  private String queryCartTotals;

  /**
   * <p>Processor Webstore Page.</p>
   **/
  private PrcWebstorePage<RS> prcWebstorePage;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pAddParam,
    final IRequestData pRequestData) throws Exception {
    ShoppingCart shoppingCart = this.prcWebstorePage
      .getShoppingCart(pAddParam, pRequestData, false);
    if (shoppingCart == null || shoppingCart.getItsItems() == null) {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "there_is_no_cart_for_requestor");
    }
    String cartItemItsIdStr = pRequestData.getParameter("cartItemItsId");
    if (cartItemItsIdStr != null) {
      Long cartItemItsId = Long.valueOf(cartItemItsIdStr);
      CartItem cartItem = null;
      for (CartItem ci : shoppingCart.getItsItems()) {
        if (ci.getItsId().equals(cartItemItsId)) {
          if (ci.getIsDisabled()) {
            throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
              "requested_item_disabled");
          }
          cartItem = ci;
          break;
        }
      }
      if (cartItem == null) {
        throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
          "requested_item_not_found");
      }
      cartItem.setIsDisabled(true);
      this.prcWebstorePage.getSrvOrm().updateEntity(pAddParam, cartItem);
      String query = lazyGetQueryCartTotals();
      query = query.replace(":CARTID", shoppingCart.getItsId()
        .getItsId().toString());
      String[] columns = new String[]{"ITSTOTAL", "TOTALITEMS"};
      Double[] totals = this.prcWebstorePage.getSrvDatabase()
        .evalDoubleResults(query, columns);
      if (totals[0] == null) {
        totals[0] = 0d;
      }
      if (totals[1] == null) {
        totals[1] = 0d;
      }
      AccSettings accSettings = this.prcWebstorePage.getSrvAccSettings()
        .lazyGetAccSettings(pAddParam);
      shoppingCart.setItsTotal(BigDecimal.valueOf(totals[0]).
        setScale(accSettings.getPricePrecision(),
          accSettings.getRoundingMode()));
      shoppingCart.setTotalItems(totals[1].intValue());
      this.prcWebstorePage.getSrvOrm().updateEntity(pAddParam, shoppingCart);
      pRequestData.setAttribute("shoppingCart", shoppingCart);
      this.prcWebstorePage.process(pAddParam, pRequestData);
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "there_is_no_cart_item_id");
    }
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = PrcDelItemFromCart.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = PrcDelItemFromCart.class
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

  /**
   * <p>Lazy Get queryCartTotals.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String
    lazyGetQueryCartTotals() throws Exception {
    if (this.queryCartTotals == null) {
      String flName = "/webstore/cartTotals.sql";
      this.queryCartTotals = loadString(flName);
    }
    return this.queryCartTotals;
  }

  //Simple getters and setters:
  /**
   * <p>Setter for queryCartTotals.</p>
   * @param pQueryCartTotals reference
   **/
  public final void setQueryCartTotals(final String pQueryCartTotals) {
    this.queryCartTotals = pQueryCartTotals;
  }

  /**
   * <p>Getter for prcWebstorePage.</p>
   * @return PrcWebstorePage<RS>
   **/
  public final PrcWebstorePage<RS> getPrcWebstorePage() {
    return this.prcWebstorePage;
  }

  /**
   * <p>Setter for prcWebstorePage.</p>
   * @param pPrcWebstorePage reference
   **/
  public final void setPrcWebstorePage(
    final PrcWebstorePage<RS> pPrcWebstorePage) {
    this.prcWebstorePage = pPrcWebstorePage;
  }
}
