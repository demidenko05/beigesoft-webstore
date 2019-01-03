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
import java.util.Date;
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.ServiceToSale;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.model.EOrdStat;
import org.beigesoft.webstore.persistable.base.AItemPlace;
import org.beigesoft.webstore.persistable.base.ACustOrderLn;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.CartTxLn;
import org.beigesoft.webstore.persistable.CartItTxLn;
import org.beigesoft.webstore.persistable.GoodsPlace;
import org.beigesoft.webstore.persistable.SeGoodsPlace;
import org.beigesoft.webstore.persistable.ServicePlace;
import org.beigesoft.webstore.persistable.SeServicePlace;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.persistable.CustOrderTxLn;
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
 * <p>Service that checkouts cart phase#1, i.e. creates orders with status NEW
 * from cart. Next phase#2 is accepting these orders (booking bookable items)
 * and making payments (if there is order with any online payment method).</p>
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
   * <p>Processors factory.</p>
   **/
  private IFactoryAppBeansByName<IProcessor> procFac;

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
    if (cart == null) {
      //TODO handling "it maybe swindler's bot":
      return;
    }
    TradingSettings ts = (TradingSettings) pReqVars.get("tradSet");
    AccSettings as = (AccSettings) pReqVars.get("accSet");
    TaxDestination txRules = this.srvCart.revealTaxRules(pReqVars, cart, as);
    //redo prices and taxes:
    for (CartLn cl : cart.getItems()) {
      if (!cl.getDisab()) {
        this.srvCart.makeCartLine(pReqVars, cl, as, ts, txRules, true, true);
        this.srvCart.makeCartTotals(pReqVars, ts, cl, as, txRules);
      }
    }
    boolean isCompl = true;
    Set<String> ndFl = new HashSet<String>();
    ndFl.add("itsId");
    ndFl.add("itsVersion");
    pReqVars.put(CustOrder.class.getSimpleName() + "neededFields", ndFl);
    List<CustOrder> orders = getSrvOrm().retrieveListWithConditions(pReqVars,
      CustOrder.class, "where STAT=0 and BUYER=" + cart.getBuyer().getItsId());
    pReqVars.remove(CustOrder.class.getSimpleName() + "neededFields");
    for (CustOrder cuOr : orders) {
      //redo all lines:
      //itsOwner and other data will be set farther only for used lines!!!
      //unused lines will be removed from DB
      pReqVars.put(CustOrderTxLn.class.getSimpleName() + "neededFields", ndFl);
      cuOr.setTaxes(getSrvOrm().retrieveListWithConditions(pReqVars,
        CustOrderTxLn.class, "where ITSOWNER=" + cuOr.getItsId()));
      pReqVars.remove(CustOrderTxLn.class.getSimpleName() + "neededFields");
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
    //List<SeCustOrder> seorders = new ArrayList<SeCustOrder>();
    String cond;
    for (CartLn cl : cart.getItems()) {
      if (cl.getDisab()) {
        continue;
      }
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
      if (serBus == null || cl.getDt1() == null) {
        // good or non-bookable service
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
        //for bookable service it's ambiguous - same service (e.g. appointment
        //to DR.Jonson) is available at same time in two different places)
        //for non-bookable service, e.g. for delivering place means
        //starting-point, but it should be selected automatically TODO
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
      } else {
        getSrvOrm().updateEntity(pReqVars, cl);
      }
    }
    pRequestData.setAttribute("cart", cart);
    if (txRules != null) {
      pRequestData.setAttribute("txRules", txRules);
    }
    if (!isCompl) {
      String procNm = pRequestData.getParameter("nmPrcRed");
      IProcessor proc = this.procFac.lazyGet(pReqVars, procNm);
      proc.process(pReqVars, pRequestData);
    } else {
      for (CustOrder co : orders) {
        if (co.getPlace() == null) { //stored unused order
          //remove it and all its lines:
          for (CustOrderGdLn gl : co.getGoods()) {
            for (CuOrGdTxLn gtl : gl.getItTxs()) {
              getSrvOrm().deleteEntity(pReqVars, gtl);
            }
            getSrvOrm().deleteEntity(pReqVars, gl);
          }
          for (CustOrderSrvLn sl : co.getServs()) {
            for (CuOrSrTxLn stl : sl.getItTxs()) {
              getSrvOrm().deleteEntity(pReqVars, stl);
            }
            getSrvOrm().deleteEntity(pReqVars, sl);
          }
          for (CustOrderTxLn otlt : co.getTaxes()) {
            getSrvOrm().deleteEntity(pReqVars, otlt);
          }
          getSrvOrm().deleteEntity(pReqVars, co);
          continue;
        }
        if (co.getIsNew()) {
          getSrvOrm().insertEntity(pReqVars, co);
        }
        BigDecimal tot = BigDecimal.ZERO;
        BigDecimal subt = BigDecimal.ZERO;
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
          } else {
            tot = tot.add(gl.getTot());
            subt = subt.add(gl.getSubt());
            if (!gl.getIsNew()) {
              getSrvOrm().updateEntity(pReqVars, gl);
            }
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
          } else {
            tot = tot.add(sl.getTot());
            subt = subt.add(sl.getSubt());
            if (!sl.getIsNew()) {
              getSrvOrm().updateEntity(pReqVars, sl);
            }
          }
        }
        BigDecimal totTx = BigDecimal.ZERO;
        for (CartTxLn ctl : cart.getTaxes()) {
          if (ctl.getDisab()) {
            continue;
          }
          CustOrderTxLn otl = null;
          if (!co.getIsNew()) {
            for (CustOrderTxLn otlt : co.getTaxes()) {
              if (otlt.getTax() == null) {
                otl = otlt;
                break;
              }
            }
          }
          if (otl == null) {
            otl = new CustOrderTxLn();
            otl.setIsNew(true);
            co.getTaxes().add(otl);
          }
          otl.setItsOwner(co);
          Tax tx = new Tax();
          tx.setItsId(ctl.getTax().getItsId());
          tx.setItsName(ctl.getTax().getItsName());
          otl.setTax(tx);
          otl.setTot(ctl.getTot());
          otl.setTaxab(ctl.getTaxab());
          totTx = totTx.add(otl.getTot());
          if (otl.getIsNew()) {
            getSrvOrm().insertEntity(pReqVars, otl);
          } else {
            getSrvOrm().updateEntity(pReqVars, otl);
          }
        }
        if (!co.getIsNew()) {
          for (CustOrderTxLn otlt : co.getTaxes()) {
            if (otlt.getTax() == null) {
              getSrvOrm().deleteEntity(pReqVars, otlt);
            }
          }
        }
        co.setTot(tot);
        co.setSubt(subt);
        co.setTotTx(totTx);
        getSrvOrm().updateEntity(pReqVars, co);
      }
      pRequestData.setAttribute("orders", orders);
      String listFltAp = pRequestData.getParameter("listFltAp");
      if (listFltAp != null) {
        listFltAp = new String(listFltAp.getBytes("ISO-8859-1"), "UTF-8");
        pRequestData.setAttribute("listFltAp", listFltAp);
      }
      String itFltAp = pRequestData.getParameter("itFltAp");
      if (itFltAp != null) {
        itFltAp = new String(itFltAp.getBytes("ISO-8859-1"), "UTF-8");
        pRequestData.setAttribute("itFltAp", itFltAp);
      }
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
      if (co.getPlace() != null && co.getPlace().getItsId()
        .equals(pItPl.getPickUpPlace().getItsId())) {
        cuOr = co;
        isNdOrInit = false;
        break;
      }
    }
    if (cuOr == null) {
      for (CustOrder co : pOrders) {
        if (co.getPlace() == null) {
          cuOr = co;
          break;
        }
      }
    }
    if (cuOr == null) {
      cuOr = new CustOrder();
      cuOr.setIsNew(true);
      cuOr.setTaxes(new ArrayList<CustOrderTxLn>());
      cuOr.setGoods(new ArrayList<CustOrderGdLn>());
      cuOr.setServs(new ArrayList<CustOrderSrvLn>());
      pOrders.add(cuOr);
    }
    if (isNdOrInit) {
      cuOr.setDat(new Date());
      cuOr.setStat(EOrdStat.NEW);
      cuOr.setDeliv(pCartLn.getItsOwner().getDeliv());
      cuOr.setPayMeth(pCartLn.getItsOwner().getPayMeth());
      cuOr.setBuyer(pCartLn.getItsOwner().getBuyer());
      cuOr.setPlace(pItPl.getPickUpPlace());
      cuOr.setPur(pCartLn.getItsOwner().getItsVersion());
      cuOr.setCurr(pCartLn.getItsOwner().getCurr());
      cuOr.setExcRt(pCartLn.getItsOwner().getExcRt());
      cuOr.setDescr(pCartLn.getItsOwner().getDescr());
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
      gd.setItsName(pCartLn.getItsName());
      ogl.setGood(gd);
      if (citls.size() > 0) {
        if (ogl.getIsNew()) {
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
          tx.setItsName(citl.getTax().getItsName());
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
      sr.setItsName(pCartLn.getItsName());
      osl.setService(sr);
      osl.setDt1(pCartLn.getDt1());
      osl.setDt2(pCartLn.getDt2());
      if (citls.size() > 0) {
        if (osl.getIsNew()) {
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
          tx.setItsName(citl.getTax().getItsName());
          oitl.setTax(tx);
          oitl.setTot(citl.getTot());
        }
      }
      ol = osl;
    }
    ol.setItsName(pCartLn.getItsName());
    ol.setDescr(pCartLn.getDescr());
    ol.setUom(pCartLn.getUom());
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

  /**
   * <p>Getter for procFac.</p>
   * @return IFactoryAppBeansByName<IProcessor>
   **/
  public final IFactoryAppBeansByName<IProcessor> getProcFac() {
    return this.procFac;
  }

  /**
   * <p>Setter for procFac.</p>
   * @param pProcFac reference
   **/
  public final void setProcFac(
    final IFactoryAppBeansByName<IProcessor> pProcFac) {
    this.procFac = pProcFac;
  }
}
