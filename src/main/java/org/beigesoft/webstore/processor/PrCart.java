package org.beigesoft.webstore.processor;

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

import java.util.Map;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.webstore.model.EPaymentMethod;
import org.beigesoft.webstore.model.EDelivering;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.service.ISrvShoppingCart;

/**
 * <p>Processor that changes cart's delivering and payment methods.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrCart<RS> implements IProcessor {

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
   * <p>Process request.</p>
   * @param pReqVars request scoped vars
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    Cart cart = this.srvCart.getShoppingCart(pReqVars, pRequestData, false,
      false);
    if (cart != null) {
      String dlvStr = pRequestData.getParameter("deliv");
      String payMethStr = pRequestData.getParameter("payMeth");
      EDelivering dlv = EDelivering.class.
        getEnumConstants()[Integer.parseInt(dlvStr)];
      EPaymentMethod payMeth = EPaymentMethod.class.
        getEnumConstants()[Integer.parseInt(payMethStr)];
      cart.setPayMeth(payMeth);
      cart.setDeliv(dlv);
      this.srvOrm.updateEntity(pReqVars, cart);
      pRequestData.setAttribute("cart", cart);
    } //else spam
    //TODO change to servlet redirect???
    String processorName = pRequestData.getParameter("nmPrcRed");
    IProcessor proc = this.processorsFactory.lazyGet(pReqVars, processorName);
    proc.process(pReqVars, pRequestData);
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
