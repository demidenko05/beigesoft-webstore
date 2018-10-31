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
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.service.ISrvShoppingCart;

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
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Shopping Cart service.</p>
   **/
  private ISrvShoppingCart srvShoppingCart;

  /**
   * <p>Processors factory.</p>
   **/
  private IFactoryAppBeansByName<IProcessor> processorsFactory;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pAddParam,
    final IRequestData pRequestData) throws Exception {
    Cart shoppingCart = this.srvShoppingCart
      .getShoppingCart(pAddParam, pRequestData, false);
    if (shoppingCart == null || shoppingCart.getItems() == null) {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "there_is_no_cart_for_requestor");
    }
    String lnIdStr = pRequestData.getParameter("lnId");
    if (lnIdStr != null) {
      Long lnId = Long.valueOf(lnIdStr);
      CartLn cartItem = null;
      for (CartLn ci : shoppingCart.getItems()) {
        if (ci.getItsId().equals(lnId)) {
          if (ci.getDisab()) {
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
      cartItem.setDisab(true);
      this.getSrvOrm().updateEntity(pAddParam, cartItem);
      String query = lazyGetQueryCartTotals();
      query = query.replace(":CARTID", shoppingCart.getItsId()
        .getItsId().toString());
      String[] columns = new String[]{"ITSTOTAL"};
      Double[] totals = this.getSrvDatabase()
        .evalDoubleResults(query, columns);
      if (totals[0] == null) {
        totals[0] = 0d;
      }
      AccSettings accSettings = (AccSettings) pAddParam.get("accSettings");
      shoppingCart.setTot(BigDecimal.valueOf(totals[0]).
        setScale(accSettings.getPricePrecision(),
          accSettings.getRoundingMode()));
      this.getSrvOrm().updateEntity(pAddParam, shoppingCart);
      pRequestData.setAttribute("shoppingCart", shoppingCart);
      String processorName = pRequestData.getParameter("nmPrcRedirect");
      IProcessor proc = this.processorsFactory
        .lazyGet(pAddParam, processorName);
      proc.process(pAddParam, pRequestData);
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
   * <p>Getter for srvDatabase.</p>
   * @return ISrvDatabase<RS>
   **/
  public final ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final void setSrvDatabase(final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }

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
   * <p>Getter for srvShoppingCart.</p>
   * @return ISrvShoppingCart
   **/
  public final ISrvShoppingCart getSrvShoppingCart() {
    return this.srvShoppingCart;
  }

  /**
   * <p>Setter for srvShoppingCart.</p>
   * @param pSrvShoppingCart reference
   **/
  public final void setSrvShoppingCart(
    final ISrvShoppingCart pSrvShoppingCart) {
    this.srvShoppingCart = pSrvShoppingCart;
  }

  /**
   * <p>Getter for processorsFactory.</p>
   * @return IFactoryAppBeansByName<IProcessor>
   **/
  public final IFactoryAppBeansByName<IProcessor> getProcessorsFactory() {
    return this.processorsFactory;
  }

  /**
   * <p>Setter for processorsFactory.</p>
   * @param pProcessorsFactory reference
   **/
  public final void setProcessorsFactory(
    final IFactoryAppBeansByName<IProcessor> pProcessorsFactory) {
    this.processorsFactory = pProcessorsFactory;
  }
}
