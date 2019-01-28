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
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;
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
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    Cart cart = this.srvCart.getShoppingCart(pRqVs, pRqDt, false,
      false);
    if (cart != null) {
      String dlvStr = pRqDt.getParameter("deliv");
      String payMethStr = pRqDt.getParameter("payMeth");
      EDelivering dlv = EDelivering.class.
        getEnumConstants()[Integer.parseInt(dlvStr)];
      EPaymentMethod payMeth = EPaymentMethod.class.
        getEnumConstants()[Integer.parseInt(payMethStr)];
      if (dlv != cart.getDeliv() || payMeth != cart.getPayMeth()) {
        EDelivering dlvOld = cart.getDeliv();
        cart.setDeliv(dlv);
        cart.setPayMeth(payMeth);
        this.srvOrm.updateEntity(pRqVs, cart);
        if (dlv != dlvOld) {
          AccSettings as = (AccSettings) pRqVs.get("accSet");
          TaxDestination txRules = this.srvCart.revealTaxRules(pRqVs,
            cart, as);
          if (txRules != null) {
            pRqDt.setAttribute("txRules", txRules);
          }
          this.srvCart.hndCartChan(pRqVs, cart, txRules);
        }
      }
    } //else spam
    //TODO change to servlet redirect???
    String processorName = pRqDt.getParameter("nmPrcRed");
    IProcessor proc = this.processorsFactory.lazyGet(pRqVs, processorName);
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
