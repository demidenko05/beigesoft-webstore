package org.beigesoft.webstore.processor;

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
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.UnitOfMeasure;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.persistable.base.AItemPlace;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.GoodsPlace;
import org.beigesoft.webstore.persistable.SeGoodsPlace;
import org.beigesoft.webstore.persistable.ServicePlace;
import org.beigesoft.webstore.persistable.SeServicePlace;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.service.ISrvShoppingCart;

/**
 * <p>Service that checkouts cart.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcCheckOut<RS> implements IProcessor {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Shopping Cart service.</p>
   **/
  private ISrvShoppingCart srvCart;

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
    Cart cart = this.srvCart.getShoppingCart(pReqVars, pRequestData, false);
    AccSettings as = (AccSettings) pReqVars.get("accSet");
    TaxDestination txRules = this.srvCart.revealTaxRules(pReqVars, cart, as);
    boolean isCompl = true;
    List<CustOrder> orders = new ArrayList<CustOrder>();
    for (CartLn cl : cart.getItems()) {
      Class<?> itPlCl;
      if (cl.getItTyp().equals(EShopItemType.GOODS)) {
        itPlCl = GoodsPlace.class;
      } else if (cl.getItTyp().equals(EShopItemType.SERVICE)) {
        itPlCl = ServicePlace.class;
      } else if (cl.getItTyp().equals(EShopItemType.SESERVICE)) {
        itPlCl = SeServicePlace.class;
      } else {
        itPlCl = SeGoodsPlace.class;
      }
      @SuppressWarnings("unchecked")
      List<AItemPlace<?, ?>> places = (List<AItemPlace<?, ?>>) getSrvOrm()
        .retrieveListWithConditions(pReqVars, itPlCl,
          "where ITEM=" + cl.getItId());
      if (places.size() != 1) {
        isCompl = false;
        String errs = "!Wrong places for item name/ID/type: " + cl.getItsName()
          + "/" + cl.getItId()+ "/" + cl.getItTyp();
        if (cart.getDescr() == null) {
          cart.setDescr(errs);
        } else {
          cart.setDescr(cart.getDescr() + errs);
        }
        cart.setErr(true);
        getSrvOrm().updateEntity(pReqVars, cart);
        break;
      }
      AItemPlace<?, ?> ip = places.get(0);
      if (ip.getItsQuantity().compareTo(cl.getQuant()) == -1) {
        isCompl = false;
        cl.setAvQuan(ip.getItsQuantity());
      }
      if (isCompl) {
        CustOrder cuOr = null;
        for (CustOrder co : orders) {
          if (co.getPlace().getItsId().equals(ip.getPickUpPlace().getItsId())) {
            cuOr = co;
            break;
          }
        }
      }
    }
    if (!isCompl) {
      pRequestData.setAttribute("cart", cart);
      if (txRules != null) {
        pRequestData.setAttribute("txRules", txRules);
      }
    } else {
      pRequestData.setAttribute("orders", orders);
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

  public final void setSrvCart(final ISrvShoppingCart pSrvCart) {
    this.srvCart = pSrvCart;
  }
}
