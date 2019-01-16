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
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    Cart cart = this.srvShoppingCart
      .getShoppingCart(pRqVs, pRqDt, false, false);
    if (cart == null) {
      redir(pRqVs, pRqDt);
      return;
    }
    String lnIdStr = pRqDt.getParameter("lnId");
    if (lnIdStr != null) {
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
      AccSettings as = (AccSettings) pRqVs.get("accSet");
      TradingSettings ts = (TradingSettings) pRqVs.get("tradSet");
      TaxDestination txRules = this.srvShoppingCart.revealTaxRules(pRqVs,
        cart, as);
      if (!cartLn.getForc()) {
        cartLn.setDisab(true);
        getSrvOrm().updateEntity(pRqVs, cartLn);
        if (txRules != null && cartLn.getTxCat() != null && !txRules
          .getSalTaxIsInvoiceBase() && !txRules.getSalTaxUseAggregItBas()) {
          pRqVs.put("CartItTxLnitsOwnerdeepLevel", 1);
          List<CartItTxLn> itls = getSrvOrm().retrieveListWithConditions(
              pRqVs, CartItTxLn.class, "where DISAB=0 and ITSOWNER="
                + cartLn.getItsId());
          pRqVs.remove("CartItTxLnitsOwnerdeepLevel");
          for (CartItTxLn itl : itls) {
            if (!itl.getDisab() && itl.getItsOwner().getItsId()
              .equals(cartLn.getItsId())) {
              itl.setDisab(true);
              getSrvOrm().updateEntity(pRqVs, itl);
            }
          }
        }
        this.srvShoppingCart.makeCartTotals(pRqVs, ts, cartLn, as, txRules);
      }
      pRqDt.setAttribute("cart", cart);
      if (txRules != null) {
        pRqDt.setAttribute("txRules", txRules);
      }
      redir(pRqVs, pRqDt);
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "there_is_no_cart_item_id");
    }
  }

  /**
   * <p>Redirect.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @throws Exception - an exception
   **/
  public final void redir(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    String procNm = pRqDt.getParameter("nmPrcRed");
    IProcessor proc = this.processorsFactory.lazyGet(pRqVs, procNm);
    proc.process(pRqVs, pRqDt);
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
