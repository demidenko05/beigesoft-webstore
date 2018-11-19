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
import java.util.List;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.CartItTxLn;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.service.ISrvShoppingCart;

/**
 * <p>Service that delete item from cart.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcDelItemFromCart<RS> implements IProcessor {

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
   * @param pReqVars request scoped vars
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    Cart cart = this.srvShoppingCart
      .getShoppingCart(pReqVars, pRequestData, false);
    String lnIdStr = pRequestData.getParameter("lnId");
    if (cart != null && lnIdStr != null) {
      Long lnId = Long.valueOf(lnIdStr);
      CartLn cartLn = null;
      for (CartLn ci : cart.getItems()) {
        if (ci.getItsId().equals(lnId)) {
          if (ci.getDisab()) {
            throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
              "requested_item_disabled");
          }
          cartLn = ci;
          break;
        }
      }
      if (cartLn == null) {
        throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
          "requested_item_not_found");
      }
      AccSettings as = (AccSettings) pReqVars.get("accSet");
      TradingSettings ts = (TradingSettings) pReqVars.get("tradSet");
      TaxDestination txRules = this.srvShoppingCart.revealTaxRules(pReqVars,
        cart, as);
      if (!cartLn.getForc()) {
        cartLn.setDisab(true);
        getSrvOrm().updateEntity(pReqVars, cartLn);
        if (txRules != null && cartLn.getTxCat() != null && !txRules
          .getSalTaxIsInvoiceBase() && !txRules.getSalTaxUseAggregItBas()) {
          pReqVars.put("CartItTxLnitsOwnerdeepLevel", 1);
          List<CartItTxLn> itls = getSrvOrm().retrieveListWithConditions(
              pReqVars, CartItTxLn.class, "where DISAB=0 and ITSOWNER="
                + cartLn.getItsId());
          pReqVars.remove("CartItTxLnitsOwnerdeepLevel");
          for (CartItTxLn itl : itls) {
            if (!itl.getDisab() && itl.getItsOwner().getItsId()
              .equals(cartLn.getItsId())) {
              itl.setDisab(true);
              getSrvOrm().updateEntity(pReqVars, itl);
            }
          }
        }
        this.srvShoppingCart.makeCartTotals(pReqVars, ts, cartLn, as, txRules);
      }
      pRequestData.setAttribute("cart", cart);
      if (txRules != null) {
        pRequestData.setAttribute("txRules", txRules);
      }
      String processorName = pRequestData.getParameter("nmPrcRed");
      IProcessor proc = this.processorsFactory
        .lazyGet(pReqVars, processorName);
      proc.process(pReqVars, pRequestData);
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "there_is_no_cart_item_id");
    }
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
