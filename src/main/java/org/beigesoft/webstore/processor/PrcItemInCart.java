package org.beigesoft.webstore.processor;

/*
 * Copyright (c) 2017 Beigesoft™
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
import java.math.BigDecimal;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.UnitOfMeasure;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.service.ISrvShoppingCart;

/**
 * <p>Service that add item to cart or change quantity
 * (from modal dialog for single item).</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcItemInCart<RS> implements IProcessor {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Shopping Cart service.</p>
   **/
  private ISrvShoppingCart srvCart;

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
    TradingSettings ts = (TradingSettings) pReqVars.get("tradSet");
    Cart cart = this.srvCart
      .getShoppingCart(pReqVars, pRequestData, true);
    CartLn cartLn = null;
    String lnIdStr = pRequestData.getParameter("lnId");
    String quantStr = pRequestData.getParameter("quant");
    String avQuanStr = pRequestData.getParameter("avQuan");
    String unStepStr = pRequestData.getParameter("unStep");
    BigDecimal quant = new BigDecimal(quantStr);
    BigDecimal avQuan = new BigDecimal(avQuanStr);
    BigDecimal unStep = new BigDecimal(unStepStr);
    String itIdStr = pRequestData.getParameter("itId");
    String itTypStr = pRequestData.getParameter("itTyp");
    Long itId = Long.valueOf(itIdStr);
    AccSettings as = (AccSettings) pReqVars.get("accSet");
    TaxDestination txRules = this.srvCart
      .revealTaxRules(pReqVars, cart, as);
    EShopItemType itTyp = EShopItemType.class.
      getEnumConstants()[Integer.parseInt(itTypStr)];
    boolean redoTxc = false;
    if (lnIdStr != null) { //change quantity
      Long lnId = Long.valueOf(lnIdStr);
      cartLn = findCartItemById(cart, lnId);
    } else { //add
      redoTxc = true;
      String uomIdStr = pRequestData.getParameter("uomId");
      Long uomId = Long.valueOf(uomIdStr);
      for (CartLn ci : cart.getItems()) {
        //check for duplicate cause "weird" but accepted request
        if (!ci.getDisab() && ci.getItTyp().equals(itTyp)
          && ci.getItId().equals(itId)) {
          cartLn = ci;
          break;
        }
      }
      if (cartLn == null) {
        for (CartLn ci : cart.getItems()) {
          if (ci.getDisab()) {
            cartLn = ci;
            cartLn.setDisab(false);
            cartLn.setSeller(null);
            cartLn.setTxCat(null);
            cartLn.setTxDsc(null);
            break;
          }
        }
      }
      if (cartLn == null) {
        cartLn = createCartItem(cart);
      }
      UnitOfMeasure uom = new UnitOfMeasure();
      uom.setItsId(uomId);
      cartLn.setUom(uom);
      cartLn.setItId(itId);
      cartLn.setItTyp(itTyp);
    }
    if (!cartLn.getForc()) {
      cartLn.setQuant(quant);
      cartLn.setAvQuan(avQuan);
      cartLn.setUnStep(unStep);
      BigDecimal amount = cartLn.getPrice().multiply(cartLn.getQuant()).
        setScale(as.getPricePrecision(), as.getRoundingMode());
      if (ts.getTxExcl()) {
        cartLn.setSubt(amount);
      } else {
        cartLn.setTot(amount);
      }
      this.srvCart.makeCartLine(pReqVars, cartLn, as, ts,
       txRules, false, redoTxc);
      this.srvCart.makeCartTotals(pReqVars, ts, cartLn, as, txRules);
    }
    pRequestData.setAttribute("cart", cart);
    if (txRules != null) {
      pRequestData.setAttribute("txRules", txRules);
    }
    long now = new Date().getTime();
    if (now - cart.getBuyer().getLsTm() < 1800000L) {
      String[] fieldsNames = new String[] {"itsId", "itsVersion", "lsTm"};
      pReqVars.put("fieldsNames", fieldsNames);
      cart.getBuyer().setLsTm(now);
      this.srvOrm.updateEntity(pReqVars, cart.getBuyer());
      pReqVars.remove("fieldsNames");
    }
    String processorName = pRequestData.getParameter("nmPrcRed");
    IProcessor proc = this.processorsFactory.lazyGet(pReqVars, processorName);
    proc.process(pReqVars, pRequestData);
  }

  /**
   * <p>Find cart item by ID.</p>
   * @param pShoppingCart cart
   * @param pCartItemItsId cart item ID
   * @return cart item
   * @throws Exception - an exception
   **/
  public final CartLn findCartItemById(final Cart pShoppingCart,
    final Long pCartItemItsId) throws Exception {
    CartLn cartLn = null;
    for (CartLn ci : pShoppingCart.getItems()) {
      if (ci.getItsId().equals(pCartItemItsId)) {
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
    return cartLn;
  }

  /**
   * <p>Create cart item.</p>
   * @param pShoppingCart cart
   * @return cart item
   **/
  public final CartLn createCartItem(final Cart pShoppingCart) {
    CartLn cartLn = new CartLn();
    cartLn.setIsNew(true);
    cartLn.setDisab(false);
    cartLn.setItsOwner(pShoppingCart);
    pShoppingCart.getItems().add(cartLn);
    return cartLn;
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

  public final void setSrvCart(
    final ISrvShoppingCart pSrvCart) {
    this.srvCart = pSrvCart;
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
