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
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.ServiceToSale;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.persistable.base.AItemPlace;
import org.beigesoft.webstore.persistable.base.ACustOrderLn;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.CartItTxLn;
import org.beigesoft.webstore.persistable.GoodsPlace;
import org.beigesoft.webstore.persistable.SeGoodsPlace;
import org.beigesoft.webstore.persistable.ServicePlace;
import org.beigesoft.webstore.persistable.SeServicePlace;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.persistable.CustOrderSrvLn;
import org.beigesoft.webstore.persistable.CustOrderGdLn;
import org.beigesoft.webstore.persistable.CuOrGdTxLn;
import org.beigesoft.webstore.persistable.CuOrSrTxLn;
//import org.beigesoft.webstore.persistable.SeCustOrder;
import org.beigesoft.webstore.persistable.SerBus;
import org.beigesoft.webstore.persistable.SeSerBus;
import org.beigesoft.webstore.persistable.TradingSettings;
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
    Cart cart = this.srvCart.getShoppingCart(pReqVars, pRequestData, false);
    TradingSettings ts = (TradingSettings) pReqVars.get("tradSet");
    AccSettings as = (AccSettings) pReqVars.get("accSet");
    TaxDestination txRules = this.srvCart.revealTaxRules(pReqVars, cart, as);
    boolean isCompl = true;
    List<CustOrder> orders = new ArrayList<CustOrder>();
    //List<SeCustOrder> seorders = new ArrayList<SeCustOrder>();
    String cond;
    for (CartLn cl : cart.getItems()) {
      Class<?> itPlCl;
      String serBus = null;
      if (cl.getItTyp().equals(EShopItemType.GOODS)) {
        itPlCl = GoodsPlace.class;
      } else if (cl.getItTyp().equals(EShopItemType.SERVICE)) {
        itPlCl = ServicePlace.class;
        serBus = SerBus.class.getSimpleName();
      } else if (cl.getItTyp().equals(EShopItemType.SESERVICE)) {
        itPlCl = SeServicePlace.class;
        serBus = SeSerBus.class.getSimpleName();
      } else {
        itPlCl = SeGoodsPlace.class;
      }
      if (serBus == null) {
        cond = "where ITSQUANTITY>0 and ITEM=" + cl.getItId();
      } else {
        cond = "left join (select distinct SERV from " + serBus + " where SERV="
      + cl.getItId() + " and FRTM>=" + cl.getDt1().getTime() + " and TITM<"
    + cl.getDt2().getTime() + ") as " + serBus + " on " + serBus + ".SERV="
  + itPlCl.getSimpleName() + ".ITEM where ITEM=" + cl.getItId()
+ " and ITSQUANTITY>0 and " + serBus + ".SERV is null";
      }
      pReqVars.put(itPlCl.getSimpleName() + "itemdeepLevel", 1); //only ID
      pReqVars.put(itPlCl.getSimpleName() + "pickUpPlacedeepLevel", 1);
      @SuppressWarnings("unchecked")
      List<AItemPlace<?, ?>> places = (List<AItemPlace<?, ?>>) getSrvOrm()
        .retrieveListWithConditions(pReqVars, itPlCl, cond);
      pReqVars.remove(itPlCl.getSimpleName() + "itemdeepLevel");
      pReqVars.remove(itPlCl.getSimpleName() + "pickUpPlacedeepLevel");
      if (places.size() > 1) {
        //for service it's ambiguous case - same service (e.g. appointment
        //to DR.Jonson) is available at same time in two different places)
        //for goods:
        //1. buyer chooses place (in filter), so use this place
        //2. buyer will pickups by yourself from different places,
        // but it must chooses them (in filter) in this case.
        // As a result places should ordered by itsQuantity and removed items
        // that are out of filter.
//TODO
        isCompl = false;
        String errs = "!Wrong places for item name/ID/type: " + cl.getItsName()
          + "/" + cl.getItId() + "/" + cl.getItTyp();
        if (cart.getDescr() == null) {
          cart.setDescr(errs);
        } else {
          cart.setDescr(cart.getDescr() + errs);
        }
        cart.setErr(true);
        getSrvOrm().updateEntity(pReqVars, cart);
        break;
      } else if (places.size() == 1) { //only place with non-zero availability
        if (places.get(0).getItsQuantity().compareTo(cl.getQuant()) == -1) {
          //for services quant is always 1
          isCompl = false;
          cl.setAvQuan(places.get(0).getItsQuantity());
        }
      } else { //e.g. busy service or updated good
        isCompl = false;
        cl.setAvQuan(BigDecimal.ZERO);
      }
      if (isCompl) {
        for (AItemPlace<?, ?> ip : places) {
          if (cl.getSeller() == null) {
            makeOrdLn(pReqVars, orders, ip, cl, ts);
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
      for (CustOrder co : orders) {
        if (co.getIsNew()) {
          getSrvOrm().insertEntity(pReqVars, co);
        } else {
          getSrvOrm().updateEntity(pReqVars, co);
        }
        for (CustOrderGdLn gl : co.getGoods()) {
          gl.setItsOwner(co);
          if (gl.getIsNew()) {
            getSrvOrm().insertEntity(pReqVars, gl);
          }
          if (gl.getItTxs() != null && gl.getItTxs().size() > 0) {
            for (CuOrGdTxLn gtl : gl.getItTxs()) {
              gtl.setItsOwner(gl);
              if (gtl.getIsNew()) {
                getSrvOrm().insertEntity(pReqVars, gtl);
              } else if (gl.getGood() == null || gtl.getTax() == null) {
                getSrvOrm().deleteEntity(pReqVars, gtl);
              } else {
                getSrvOrm().updateEntity(pReqVars, gtl);
              }
            }
          }
          if (!gl.getIsNew() && gl.getGood() == null) {
            getSrvOrm().deleteEntity(pReqVars, gl);
          } else if (!gl.getIsNew()) {
            getSrvOrm().updateEntity(pReqVars, gl);
          }
        }
        for (CustOrderSrvLn sl : co.getServs()) {
          sl.setItsOwner(co);
          if (sl.getIsNew()) {
            getSrvOrm().insertEntity(pReqVars, sl);
          }
          if (sl.getItTxs() != null && sl.getItTxs().size() > 0) {
            for (CuOrSrTxLn stl : sl.getItTxs()) {
              stl.setItsOwner(sl);
              if (stl.getIsNew()) {
                getSrvOrm().insertEntity(pReqVars, stl);
              } else if (sl.getService() == null || stl.getTax() == null) {
                getSrvOrm().deleteEntity(pReqVars, stl);
              } else {
                getSrvOrm().updateEntity(pReqVars, stl);
              }
            }
          }
          if (!sl.getIsNew() && sl.getService() == null) {
            getSrvOrm().deleteEntity(pReqVars, sl);
          } else if (!sl.getIsNew()) {
            getSrvOrm().updateEntity(pReqVars, sl);
          }
        }
      }
      pRequestData.setAttribute("orders", orders);
    }
  }

  /**
   * <p>It makes order line.</p>
   * @param pReqVars Request scoped Vars
   * @param pOrders Orders
   * @param pItPl item place
   * @param pCartLn Cart Line
   * @param pTs trading settings
   * @throws Exception an Exception
   **/
  public final void makeOrdLn(final Map<String, Object> pReqVars,
    final List<CustOrder> pOrders, final AItemPlace<?, ?> pItPl,
      final CartLn pCartLn, final TradingSettings pTs) throws Exception {
    CustOrder cuOr = null;
    boolean isNdOrInit = true;
    for (CustOrder co : pOrders) {
      if (co.getPlace().getItsId().equals(pItPl.getPickUpPlace()
        .getItsId())) {
        cuOr = co;
        isNdOrInit = false;
        break;
      }
    }
    if (cuOr == null) {
      cuOr = getSrvOrm().retrieveEntityWithConditions(pReqVars,
    CustOrder.class, "where STAT=0 and BUYER=" + pCartLn.getItsOwner()
  .getBuyer().getItsId() + " and PLACE=" + pItPl.getPickUpPlace().getItsId());
      if (cuOr != null) {
        pOrders.add(cuOr);
        //redo all lines:
        //itsOwner will be set farther only for used lines!!!
        //unused lines will be removed from DB
        Set<String> ndFl = new HashSet<String>();
        ndFl.add("itsId");
        ndFl.add("itsVersion");
       pReqVars.put(CustOrderGdLn.class.getSimpleName() + "neededFields", ndFl);
        cuOr.setGoods(getSrvOrm().retrieveListWithConditions(pReqVars,
          CustOrderGdLn.class, "where ITSOWNER=" + cuOr.getItsId()));
        pReqVars.remove(CustOrderGdLn.class.getSimpleName() + "neededFields");
      pReqVars.put(CustOrderSrvLn.class.getSimpleName() + "neededFields", ndFl);
        cuOr.setServs(getSrvOrm().retrieveListWithConditions(pReqVars,
          CustOrderSrvLn.class, "where ITSOWNER=" + cuOr.getItsId()));
        pReqVars.remove(CustOrderSrvLn.class.getSimpleName() + "neededFields");
        for (CustOrderGdLn gl : cuOr.getGoods()) {
          pReqVars.put(CuOrGdTxLn.class.getSimpleName() + "neededFields", ndFl);
          gl.setItTxs(getSrvOrm().retrieveListWithConditions(pReqVars,
            CuOrGdTxLn.class, "where ITSOWNER=" + gl.getItsId()));
          pReqVars.remove(CuOrGdTxLn.class.getSimpleName() + "neededFields");
        }
        for (CustOrderSrvLn sl : cuOr.getServs()) {
          pReqVars.put(CuOrSrTxLn.class.getSimpleName() + "neededFields", ndFl);
          sl.setItTxs(getSrvOrm().retrieveListWithConditions(pReqVars,
            CuOrSrTxLn.class, "where ITSOWNER=" + sl.getItsId()));
          pReqVars.remove(CuOrSrTxLn.class.getSimpleName() + "neededFields");
        }
      }
    }
    if (cuOr == null) {
      cuOr = new CustOrder();
      cuOr.setIsNew(true);
    }
    if (isNdOrInit) {
      cuOr.setPayMeth(pTs.getDefaultPaymentMethod());
      cuOr.setBuyer(pCartLn.getItsOwner().getBuyer());
      cuOr.setPlace(pItPl.getPickUpPlace());
    }
    pReqVars.put(CartItTxLn.class.getSimpleName() + "itsOwnerdeepLevel", 1);
    pReqVars.put(CartItTxLn.class.getSimpleName() + "taxdeepLevel", 1);
    List<CartItTxLn> citls = getSrvOrm().retrieveListWithConditions(pReqVars,
      CartItTxLn.class, "where DISAB=0 and ITSOWNER=" + pCartLn.getItsId());
    pReqVars.remove(CartItTxLn.class.getSimpleName() + "itsOwnerdeepLevel");
    pReqVars.remove(CartItTxLn.class.getSimpleName() + "taxdeepLevel");
    ACustOrderLn ol;
    if (pCartLn.getItTyp().equals(EShopItemType.GOODS)) {
      CustOrderGdLn ogl = null;
      if (!cuOr.getIsNew()) {
        for (CustOrderGdLn gl : cuOr.getGoods()) {
          if (gl.getGood() == null) {
            ogl = gl;
            break;
          }
        }
      }
      if (ogl == null) {
        ogl = new CustOrderGdLn();
        ogl.setIsNew(true);
        cuOr.getGoods().add(ogl);
      }
      InvItem gd = new InvItem();
      gd.setItsId(pCartLn.getItId());
      ogl.setGood(gd);
      if (citls.size() > 0) {
        if (cuOr.getIsNew()) {
          ogl.setItTxs(new ArrayList<CuOrGdTxLn>());
        }
        for (CartItTxLn citl : citls) {
          CuOrGdTxLn oitl = null;
          if (!cuOr.getIsNew()) {
            for (CuOrGdTxLn itl : ogl.getItTxs()) {
              if (itl.getTax() == null) {
                oitl = itl;
                break;
              }
            }
          }
          if (oitl == null) {
            oitl = new CuOrGdTxLn();
            oitl.setIsNew(true);
            ogl.getItTxs().add(oitl);
          }
          oitl.setItsOwner(ogl);
          Tax tx = new Tax();
          tx.setItsId(citl.getTax().getItsId());
          oitl.setTax(tx);
          oitl.setTot(citl.getTot());
        }
      }
      ol = ogl;
    } else {
      CustOrderSrvLn osl = null;
      if (!cuOr.getIsNew()) {
        for (CustOrderSrvLn sl : cuOr.getServs()) {
          if (sl.getService() == null) {
            osl = sl;
            break;
          }
        }
      }
      if (osl == null) {
        osl = new CustOrderSrvLn();
        osl.setIsNew(true);
        cuOr.getServs().add(osl);
      }
      ServiceToSale sr = new ServiceToSale();
      sr.setItsId(pCartLn.getItId());
      osl.setService(sr);
      if (citls.size() > 0) {
        if (cuOr.getIsNew()) {
          osl.setItTxs(new ArrayList<CuOrSrTxLn>());
        }
        for (CartItTxLn citl : citls) {
          CuOrSrTxLn oitl = null;
          if (!cuOr.getIsNew()) {
            for (CuOrSrTxLn itl : osl.getItTxs()) {
              if (itl.getTax() == null) {
                oitl = itl;
                break;
              }
            }
          }
          if (oitl == null) {
            oitl = new CuOrSrTxLn();
            oitl.setIsNew(true);
            osl.getItTxs().add(oitl);
          }
          oitl.setItsOwner(osl);
          Tax tx = new Tax();
          tx.setItsId(citl.getTax().getItsId());
          oitl.setTax(tx);
          oitl.setTot(citl.getTot());
        }
      }
      ol = osl;
    }
    ol.setPrice(pCartLn.getPrice());
    ol.setQuant(pCartLn.getQuant());
    ol.setSubt(pCartLn.getSubt());
    ol.setTot(pCartLn.getTot());
    ol.setTotTx(pCartLn.getTotTx());
    ol.setTxCat(pCartLn.getTxCat());
    ol.setTxDsc(pCartLn.getTxDsc());
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
