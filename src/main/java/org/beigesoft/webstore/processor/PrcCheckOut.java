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
import org.beigesoft.webstore.model.EPaymentMethod;
import org.beigesoft.webstore.model.Purch;
import org.beigesoft.webstore.persistable.base.AItemPlace;
import org.beigesoft.webstore.persistable.base.ACustOrderLn;
import org.beigesoft.webstore.persistable.base.ACuOrSeLn;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.CartTxLn;
import org.beigesoft.webstore.persistable.CartItTxLn;
import org.beigesoft.webstore.persistable.GoodsPlace;
import org.beigesoft.webstore.persistable.SeGoodsPlace;
import org.beigesoft.webstore.persistable.ServicePlace;
import org.beigesoft.webstore.persistable.SeServicePlace;
import org.beigesoft.webstore.persistable.CuOrSe;
import org.beigesoft.webstore.persistable.CuOrSeTxLn;
import org.beigesoft.webstore.persistable.CuOrSeSrLn;
import org.beigesoft.webstore.persistable.CuOrSeGdLn;
import org.beigesoft.webstore.persistable.CuOrSeGdTxLn;
import org.beigesoft.webstore.persistable.CuOrSeSrTxLn;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.persistable.CustOrderTxLn;
import org.beigesoft.webstore.persistable.CustOrderSrvLn;
import org.beigesoft.webstore.persistable.CustOrderGdLn;
import org.beigesoft.webstore.persistable.CuOrGdTxLn;
import org.beigesoft.webstore.persistable.CuOrSrTxLn;
import org.beigesoft.webstore.persistable.PayMd;
import org.beigesoft.webstore.persistable.SerBus;
import org.beigesoft.webstore.persistable.SeSerBus;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.SeGoods;
import org.beigesoft.webstore.persistable.SeService;
import org.beigesoft.webstore.persistable.SeSeller;
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
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    Cart cart = this.srvCart.getShoppingCart(pRqVs, pRqDt, false, true);
    if (cart == null) {
      redir(pRqVs, pRqDt);
      return;
    }
    if (EPaymentMethod.PAYPAL.equals(cart.getPayMeth())) {
      //TODO partially online, SE
      List<PayMd> payMds = this.srvOrm.retrieveListWithConditions(pRqVs,
        PayMd.class, "where ITSNAME='PAYPAL'");
      if (payMds.size() != 1) {
        throw new Exception("There is no properly PPL PayMd");
      } else {
        pRqDt.setAttribute("pmde", payMds.get(0).getMde());
      }
    }
    TradingSettings ts = (TradingSettings) pRqVs.get("tradSet");
    AccSettings as = (AccSettings) pRqVs.get("accSet");
    TaxDestination txRules = this.srvCart.revealTaxRules(pRqVs, cart, as);
    //redo prices and taxes:
    for (CartLn cl : cart.getItems()) {
      if (!cl.getDisab() && !cl.getForc()) {
        this.srvCart.makeCartLine(pRqVs, cl, as, ts, txRules, true, true);
        this.srvCart.makeCartTotals(pRqVs, ts, cl, as, txRules);
      }
    }
    boolean isCompl = true;
    Purch pur = retNewOrds(pRqVs, cart.getBuyer());
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
      Set<String> ndFlNm = new HashSet<String>();
      ndFlNm.add("itsId");
      ndFlNm.add("itsName");
      pRqVs.put("PickUpPlaceneededFields", ndFlNm);
      pRqVs.put(itPlCl.getSimpleName() + "itemdeepLevel", 1); //only ID
      @SuppressWarnings("unchecked")
      List<AItemPlace<?, ?>> places = (List<AItemPlace<?, ?>>) getSrvOrm()
        .retrieveListWithConditions(pRqVs, itPlCl, cond);
      pRqVs.remove(itPlCl.getSimpleName() + "itemdeepLevel");
      pRqVs.remove("PickUpPlaceneededFields");
      if (places.size() > 1 && serBus != null && cl.getDt1() != null) {
        //for bookable service it's ambiguous - same service (e.g. appointment
        //to DR.Jonson) is available at same time in two different places)
        //for non-bookable service, e.g. for delivering place means
        //starting-point, but it should be selected automatically TODO
        //for goods: TODO
        //1. buyer chooses place (in filter), so use this place
        //2. buyer will pickups by yourself from different places,
        // but it must chooses them (in filter) in this case.
        // As a result places should ordered by itsQuantity and removed items
        // that are out of filter.
        isCompl = false;
        String errs = "!Wrong places for item name/ID/type: " + cl.getItsName()
          + "/" + cl.getItId() + "/" + cl.getItTyp();
        if (cart.getDescr() == null) {
          cart.setDescr(errs);
        } else {
          cart.setDescr(cart.getDescr() + errs);
        }
        cart.setErr(true);
        getSrvOrm().updateEntity(pRqVs, cart);
        break;
      } else { //only/multiply place/s with non-zero availability
        BigDecimal avQu = BigDecimal.ZERO;
        for (AItemPlace<?, ?> pl : places) {
          avQu = avQu.add(pl.getItsQuantity());
        }
        if (avQu.compareTo(cl.getQuant()) == -1) {
          isCompl = false;
          cl.setAvQuan(avQu);
        }
      }
      if (isCompl && cl.getPrice().compareTo(BigDecimal.ZERO) == 1) {
        //without free delivering:
        if (cl.getSeller() == null) {
          makeOrdLn(pRqVs, pur.getOrds(), null, cl, ts);
        } else {
          makeSeOrdLn(pRqVs, pur.getSords(), cl.getSeller(), null, cl, ts);
        }
      } else {
        getSrvOrm().updateEntity(pRqVs, cl);
      }
    }
    pRqDt.setAttribute("cart", cart);
    if (txRules != null) {
      pRqDt.setAttribute("txRules", txRules);
    }
    if (!isCompl) {
      redir(pRqVs, pRqDt);
    } else {
      saveOrds(pRqVs, pur, cart);
      saveSords(pRqVs, pur, cart);
      pRqDt.setAttribute("orders", pur.getOrds());
      pRqDt.setAttribute("sorders", pur.getSords());
    }
  }

  /**
   * <p>Saves S.E. orders.</p>
   * @param pRqVs request scoped vars
   * @param pPur purchase
   * @param pCart cart
   * @throws Exception - an exception
   **/
  public final void saveSords(final Map<String, Object> pRqVs,
    final Purch pPur, final Cart pCart) throws Exception {
    List<CuOrSe> dels = null;
    for (CuOrSe co : pPur.getSords()) {
      if (co.getCurr() == null) { //stored unused order
        //remove it and all its lines:
        for (CuOrSeGdLn gl : co.getGoods()) {
          for (CuOrSeGdTxLn gtl : gl.getItTxs()) {
            getSrvOrm().deleteEntity(pRqVs, gtl);
          }
          getSrvOrm().deleteEntity(pRqVs, gl);
        }
        for (CuOrSeSrLn sl : co.getServs()) {
          for (CuOrSeSrTxLn stl : sl.getItTxs()) {
            getSrvOrm().deleteEntity(pRqVs, stl);
          }
          getSrvOrm().deleteEntity(pRqVs, sl);
        }
        for (CuOrSeTxLn otlt : co.getTaxes()) {
          getSrvOrm().deleteEntity(pRqVs, otlt);
        }
        getSrvOrm().deleteEntity(pRqVs, co);
        if (dels == null) {
          dels = new ArrayList<CuOrSe>();
        }
        dels.add(co);
        continue;
      }
      if (co.getIsNew()) {
        getSrvOrm().insertEntity(pRqVs, co);
      }
      BigDecimal tot = BigDecimal.ZERO;
      BigDecimal subt = BigDecimal.ZERO;
      List<CuOrSeGdLn> delsGd = null;
      for (CuOrSeGdLn gl : co.getGoods()) {
        gl.setItsOwner(co);
        if (gl.getIsNew()) {
          getSrvOrm().insertEntity(pRqVs, gl);
        }
        if (gl.getItTxs() != null && gl.getItTxs().size() > 0) {
          for (CuOrSeGdTxLn gtl : gl.getItTxs()) {
            gtl.setItsOwner(gl);
            if (gtl.getIsNew()) {
              getSrvOrm().insertEntity(pRqVs, gtl);
            } else if (gl.getGood() == null || gtl.getTax() == null) {
              getSrvOrm().deleteEntity(pRqVs, gtl);
            } else {
              getSrvOrm().updateEntity(pRqVs, gtl);
            }
          }
        }
        if (!gl.getIsNew() && gl.getGood() == null) {
          getSrvOrm().deleteEntity(pRqVs, gl);
          if (delsGd == null) {
            delsGd = new ArrayList<CuOrSeGdLn>();
          }
          delsGd.add(gl);
        } else {
          tot = tot.add(gl.getTot());
          subt = subt.add(gl.getSubt());
          if (!gl.getIsNew()) {
            getSrvOrm().updateEntity(pRqVs, gl);
          }
        }
      }
      if (delsGd != null) {
        for (CuOrSeGdLn gl : delsGd) {
          co.getGoods().remove(gl);
        }
      }
      List<CuOrSeSrLn> delsSr = null;
      for (CuOrSeSrLn sl : co.getServs()) {
        sl.setItsOwner(co);
        if (sl.getIsNew()) {
          getSrvOrm().insertEntity(pRqVs, sl);
        }
        if (sl.getItTxs() != null && sl.getItTxs().size() > 0) {
          for (CuOrSeSrTxLn stl : sl.getItTxs()) {
            stl.setItsOwner(sl);
            if (stl.getIsNew()) {
              getSrvOrm().insertEntity(pRqVs, stl);
            } else if (sl.getService() == null || stl.getTax() == null) {
              getSrvOrm().deleteEntity(pRqVs, stl);
            } else {
              getSrvOrm().updateEntity(pRqVs, stl);
            }
          }
        }
        if (!sl.getIsNew() && sl.getService() == null) {
          getSrvOrm().deleteEntity(pRqVs, sl);
          if (delsSr == null) {
            delsSr = new ArrayList<CuOrSeSrLn>();
          }
          delsSr.add(sl);
        } else {
          tot = tot.add(sl.getTot());
          subt = subt.add(sl.getSubt());
          if (!sl.getIsNew()) {
            getSrvOrm().updateEntity(pRqVs, sl);
          }
        }
      }
      if (delsSr != null) {
        for (CuOrSeSrLn sl : delsSr) {
          co.getServs().remove(sl);
        }
      }
      BigDecimal totTx = BigDecimal.ZERO;
      for (CartTxLn ctl : pCart.getTaxes()) {
        if (ctl.getDisab() || ctl.getSeller() == null
          || !ctl.getSeller().getSeller().getItsId()
            .equals(co.getSel().getSeller().getItsId())) {
          continue;
        }
        CuOrSeTxLn otl = null;
        if (!co.getIsNew()) {
          for (CuOrSeTxLn otlt : co.getTaxes()) {
            if (otlt.getTax() == null) {
              otl = otlt;
              break;
            }
          }
        }
        if (otl == null) {
          otl = new CuOrSeTxLn();
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
          getSrvOrm().insertEntity(pRqVs, otl);
        } else {
          getSrvOrm().updateEntity(pRqVs, otl);
        }
      }
      if (!co.getIsNew()) {
        List<CuOrSeTxLn> delsTx = null;
        for (CuOrSeTxLn otlt : co.getTaxes()) {
          if (otlt.getTax() == null) {
            getSrvOrm().deleteEntity(pRqVs, otlt);
            if (delsTx == null) {
              delsTx = new ArrayList<CuOrSeTxLn>();
            }
            delsTx.add(otlt);
          }
        }
        if (delsTx != null) {
          for (CuOrSeTxLn tl : delsTx) {
            co.getTaxes().remove(tl);
          }
        }
      }
      co.setTot(tot);
      co.setSubt(subt);
      co.setTotTx(totTx);
      getSrvOrm().updateEntity(pRqVs, co);
    }
    if (dels != null) {
      for (CuOrSe co : dels) {
        pPur.getSords().remove(co);
      }
    }
  }

  /**
   * <p>Saves orders.</p>
   * @param pRqVs request scoped vars
   * @param pPur purchase
   * @param pCart cart
   * @throws Exception - an exception
   **/
  public final void saveOrds(final Map<String, Object> pRqVs,
    final Purch pPur, final Cart pCart) throws Exception {
    List<CustOrder> dels = null;
    for (CustOrder co : pPur.getOrds()) {
      if (co.getCurr() == null) { //stored unused order
        //remove it and all its lines:
        for (CustOrderGdLn gl : co.getGoods()) {
          for (CuOrGdTxLn gtl : gl.getItTxs()) {
            getSrvOrm().deleteEntity(pRqVs, gtl);
          }
          getSrvOrm().deleteEntity(pRqVs, gl);
        }
        for (CustOrderSrvLn sl : co.getServs()) {
          for (CuOrSrTxLn stl : sl.getItTxs()) {
            getSrvOrm().deleteEntity(pRqVs, stl);
          }
          getSrvOrm().deleteEntity(pRqVs, sl);
        }
        for (CustOrderTxLn otlt : co.getTaxes()) {
          getSrvOrm().deleteEntity(pRqVs, otlt);
        }
        getSrvOrm().deleteEntity(pRqVs, co);
        if (dels == null) {
          dels = new ArrayList<CustOrder>();
        }
        dels.add(co);
        continue;
      }
      if (co.getIsNew()) {
        getSrvOrm().insertEntity(pRqVs, co);
      }
      BigDecimal tot = BigDecimal.ZERO;
      BigDecimal subt = BigDecimal.ZERO;
      List<CustOrderGdLn> delsGd = null;
      for (CustOrderGdLn gl : co.getGoods()) {
        gl.setItsOwner(co);
        if (gl.getIsNew()) {
          getSrvOrm().insertEntity(pRqVs, gl);
        }
        if (gl.getItTxs() != null && gl.getItTxs().size() > 0) {
          for (CuOrGdTxLn gtl : gl.getItTxs()) {
            gtl.setItsOwner(gl);
            if (gtl.getIsNew()) {
              getSrvOrm().insertEntity(pRqVs, gtl);
            } else if (gl.getGood() == null || gtl.getTax() == null) {
              getSrvOrm().deleteEntity(pRqVs, gtl);
            } else {
              getSrvOrm().updateEntity(pRqVs, gtl);
            }
          }
        }
        if (!gl.getIsNew() && gl.getGood() == null) {
          getSrvOrm().deleteEntity(pRqVs, gl);
          if (delsGd == null) {
            delsGd = new ArrayList<CustOrderGdLn>();
          }
          delsGd.add(gl);
        } else {
          tot = tot.add(gl.getTot());
          subt = subt.add(gl.getSubt());
          if (!gl.getIsNew()) {
            getSrvOrm().updateEntity(pRqVs, gl);
          }
        }
      }
      if (delsGd != null) {
        for (CustOrderGdLn gl : delsGd) {
          co.getGoods().remove(gl);
        }
      }
      List<CustOrderSrvLn> delsSr = null;
      for (CustOrderSrvLn sl : co.getServs()) {
        sl.setItsOwner(co);
        if (sl.getIsNew()) {
          getSrvOrm().insertEntity(pRqVs, sl);
        }
        if (sl.getItTxs() != null && sl.getItTxs().size() > 0) {
          for (CuOrSrTxLn stl : sl.getItTxs()) {
            stl.setItsOwner(sl);
            if (stl.getIsNew()) {
              getSrvOrm().insertEntity(pRqVs, stl);
            } else if (sl.getService() == null || stl.getTax() == null) {
              getSrvOrm().deleteEntity(pRqVs, stl);
            } else {
              getSrvOrm().updateEntity(pRqVs, stl);
            }
          }
        }
        if (!sl.getIsNew() && sl.getService() == null) {
          getSrvOrm().deleteEntity(pRqVs, sl);
          if (delsSr == null) {
            delsSr = new ArrayList<CustOrderSrvLn>();
          }
          delsSr.add(sl);
        } else {
          tot = tot.add(sl.getTot());
          subt = subt.add(sl.getSubt());
          if (!sl.getIsNew()) {
            getSrvOrm().updateEntity(pRqVs, sl);
          }
        }
      }
      if (delsSr != null) {
        for (CustOrderSrvLn sl : delsSr) {
          co.getServs().remove(sl);
        }
      }
      List<CustOrderTxLn> delsTx = null;
      BigDecimal totTx = BigDecimal.ZERO;
      for (CartTxLn ctl : pCart.getTaxes()) {
        if (ctl.getDisab() || ctl.getSeller() != null) {
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
          getSrvOrm().insertEntity(pRqVs, otl);
        } else {
          getSrvOrm().updateEntity(pRqVs, otl);
        }
      }
      if (!co.getIsNew()) {
        for (CustOrderTxLn otlt : co.getTaxes()) {
          if (otlt.getTax() == null) {
            getSrvOrm().deleteEntity(pRqVs, otlt);
            if (delsTx == null) {
              delsTx = new ArrayList<CustOrderTxLn>();
            }
            delsTx.add(otlt);
          }
        }
      }
      if (delsTx != null) {
        for (CustOrderTxLn tl : delsTx) {
          co.getTaxes().remove(tl);
        }
      }
      co.setTot(tot);
      co.setSubt(subt);
      co.setTotTx(totTx);
      getSrvOrm().updateEntity(pRqVs, co);
    }
    if (dels != null) {
      for (CustOrder co : dels) {
        pPur.getOrds().remove(co);
      }
    }
  }

  /**
   * <p>Retrieve new orders to redone.</p>
   * @param pRqVs request scoped vars
   * @param pBur buyer
   * @return purchase with new orders to redone
   * @throws Exception - an exception
   **/
  public final Purch retNewOrds(final Map<String, Object> pRqVs,
    final OnlineBuyer pBur) throws Exception {
    Set<String> ndFl = new HashSet<String>();
    ndFl.add("itsId");
    ndFl.add("itsVersion");
    String tbn = CustOrder.class.getSimpleName();
    pRqVs.put(tbn + "neededFields", ndFl);
    List<CustOrder> orders = getSrvOrm().retrieveListWithConditions(pRqVs,
      CustOrder.class, "where STAT=0 and BUYER=" + pBur.getItsId());
    pRqVs.remove(tbn + "neededFields");
    for (CustOrder cuOr : orders) {
      //redo all lines:
      //itsOwner and other data will be set farther only for used lines!!!
      //unused lines will be removed from DB
      tbn = CustOrderTxLn.class.getSimpleName();
      pRqVs.put(tbn + "neededFields", ndFl);
      cuOr.setTaxes(getSrvOrm().retrieveListWithConditions(pRqVs,
        CustOrderTxLn.class, "where ITSOWNER=" + cuOr.getItsId()));
      pRqVs.remove(tbn + "neededFields");
      tbn = CustOrderGdLn.class.getSimpleName();
      pRqVs.put(tbn + "neededFields", ndFl);
      cuOr.setGoods(getSrvOrm().retrieveListWithConditions(pRqVs,
        CustOrderGdLn.class, "where ITSOWNER=" + cuOr.getItsId()));
      pRqVs.remove(tbn + "neededFields");
      tbn = CustOrderSrvLn.class.getSimpleName();
      pRqVs.put(tbn + "neededFields", ndFl);
      cuOr.setServs(getSrvOrm().retrieveListWithConditions(pRqVs,
        CustOrderSrvLn.class, "where ITSOWNER=" + cuOr.getItsId()));
      pRqVs.remove(tbn + "neededFields");
      tbn = CuOrGdTxLn.class.getSimpleName();
      pRqVs.put(tbn + "neededFields", ndFl);
      for (CustOrderGdLn gl : cuOr.getGoods()) {
        gl.setItTxs(getSrvOrm().retrieveListWithConditions(pRqVs,
          CuOrGdTxLn.class, "where ITSOWNER=" + gl.getItsId()));
      }
      pRqVs.remove(tbn + "neededFields");
      tbn = CuOrSrTxLn.class.getSimpleName();
      pRqVs.put(tbn + "neededFields", ndFl);
      for (CustOrderSrvLn sl : cuOr.getServs()) {
        sl.setItTxs(getSrvOrm().retrieveListWithConditions(pRqVs,
          CuOrSrTxLn.class, "where ITSOWNER=" + sl.getItsId()));
      }
      pRqVs.remove(tbn + "neededFields");
    }
    tbn = CuOrSe.class.getSimpleName();
    pRqVs.put(tbn + "neededFields", ndFl);
    List<CuOrSe> sorders = getSrvOrm().retrieveListWithConditions(pRqVs,
      CuOrSe.class, "where STAT=0 and BUYER=" + pBur.getItsId());
    pRqVs.remove(tbn + "neededFields");
    for (CuOrSe cuOr : sorders) {
      tbn = CuOrSeTxLn.class.getSimpleName();
      pRqVs.put(tbn + "neededFields", ndFl);
      cuOr.setTaxes(getSrvOrm().retrieveListWithConditions(pRqVs,
        CuOrSeTxLn.class, "where ITSOWNER=" + cuOr.getItsId()));
      pRqVs.remove(tbn + "neededFields");
      tbn = CuOrSeGdLn.class.getSimpleName();
      pRqVs.put(tbn + "neededFields", ndFl);
      cuOr.setGoods(getSrvOrm().retrieveListWithConditions(pRqVs,
        CuOrSeGdLn.class, "where ITSOWNER=" + cuOr.getItsId()));
      pRqVs.remove(tbn + "neededFields");
      tbn = CuOrSeSrLn.class.getSimpleName();
      pRqVs.put(tbn + "neededFields", ndFl);
      cuOr.setServs(getSrvOrm().retrieveListWithConditions(pRqVs,
        CuOrSeSrLn.class, "where ITSOWNER=" + cuOr.getItsId()));
      pRqVs.remove(tbn + "neededFields");
      tbn = CuOrSeGdTxLn.class.getSimpleName();
      pRqVs.put(tbn + "neededFields", ndFl);
      for (CuOrSeGdLn gl : cuOr.getGoods()) {
        gl.setItTxs(getSrvOrm().retrieveListWithConditions(pRqVs,
          CuOrSeGdTxLn.class, "where ITSOWNER=" + gl.getItsId()));
      }
      pRqVs.remove(tbn + "neededFields");
      tbn = CuOrSeSrTxLn.class.getSimpleName();
      pRqVs.put(tbn + "neededFields", ndFl);
      for (CuOrSeSrLn sl : cuOr.getServs()) {
        sl.setItTxs(getSrvOrm().retrieveListWithConditions(pRqVs,
          CuOrSeSrTxLn.class, "where ITSOWNER=" + sl.getItsId()));
      }
      pRqVs.remove(tbn + "neededFields");
    }
    Purch pur = new Purch();
    pur.setOrds(orders);
    pur.setSords(sorders);
    return pur;
  }

  /**
   * <p>It makes S.E. order line.</p>
   * @param pRqVs Request scoped Vars
   * @param pOrders Orders
   * @param pSel seller
   * @param pItPl item place
   * @param pCartLn Cart Line
   * @param pTs trading settings
   * @throws Exception an Exception
   **/
  public final void makeSeOrdLn(final Map<String, Object> pRqVs,
    final List<CuOrSe> pOrders, final SeSeller pSel,
      final AItemPlace<?, ?> pItPl, final CartLn pCartLn,
        final TradingSettings pTs) throws Exception {
    CuOrSe cuOr = null;
    boolean isNdOrInit = true;
    for (CuOrSe co : pOrders) {
      if (co.getCurr() != null && co.getSel() != null
  && co.getSel().getSeller().getItsId().equals(pSel.getSeller().getItsId())) {
        cuOr = co;
        isNdOrInit = false;
        break;
      }
    }
    if (cuOr == null) {
      for (CuOrSe co : pOrders) {
        if (co.getCurr() == null) {
          cuOr = co;
          break;
        }
      }
    }
    if (cuOr == null) {
      cuOr = new CuOrSe();
      cuOr.setIsNew(true);
      cuOr.setTaxes(new ArrayList<CuOrSeTxLn>());
      cuOr.setGoods(new ArrayList<CuOrSeGdLn>());
      cuOr.setServs(new ArrayList<CuOrSeSrLn>());
      pOrders.add(cuOr);
    }
    if (isNdOrInit) {
      cuOr.setDat(new Date());
      cuOr.setSeller(pSel);
      cuOr.setStat(EOrdStat.NEW);
      cuOr.setDeliv(pCartLn.getItsOwner().getDeliv());
      cuOr.setPayMeth(pCartLn.getItsOwner().getPayMeth());
      cuOr.setBuyer(pCartLn.getItsOwner().getBuyer());
      //TODO  method "pickup by buyer from several places"
      //cuOr.setPlace(pItPl.getPickUpPlace());
      cuOr.setPur(pCartLn.getItsOwner().getItsVersion());
      cuOr.setCurr(pCartLn.getItsOwner().getCurr());
      cuOr.setExcRt(pCartLn.getItsOwner().getExcRt());
      cuOr.setDescr(pCartLn.getItsOwner().getDescr());
    }
    pRqVs.put(CartItTxLn.class.getSimpleName() + "itsOwnerdeepLevel", 1);
    pRqVs.put(CartItTxLn.class.getSimpleName() + "taxdeepLevel", 1);
    List<CartItTxLn> citls = getSrvOrm().retrieveListWithConditions(pRqVs,
      CartItTxLn.class, "where DISAB=0 and ITSOWNER=" + pCartLn.getItsId());
    pRqVs.remove(CartItTxLn.class.getSimpleName() + "itsOwnerdeepLevel");
    pRqVs.remove(CartItTxLn.class.getSimpleName() + "taxdeepLevel");
    ACuOrSeLn ol;
    if (pCartLn.getItTyp().equals(EShopItemType.SEGOODS)) {
      CuOrSeGdLn ogl = null;
      if (!cuOr.getIsNew()) {
        for (CuOrSeGdLn gl : cuOr.getGoods()) {
          if (gl.getGood() == null) {
            ogl = gl;
            break;
          }
        }
      }
      if (ogl == null) {
        ogl = new CuOrSeGdLn();
        ogl.setIsNew(true);
        cuOr.getGoods().add(ogl);
      }
      SeGoods gd = new SeGoods();
      gd.setSeller(pSel);
      gd.setItsId(pCartLn.getItId());
      gd.setItsName(pCartLn.getItsName());
      ogl.setGood(gd);
      if (citls.size() > 0) {
        if (ogl.getIsNew()) {
          ogl.setItTxs(new ArrayList<CuOrSeGdTxLn>());
        }
        for (CartItTxLn citl : citls) {
          CuOrSeGdTxLn oitl = null;
          if (!cuOr.getIsNew()) {
            for (CuOrSeGdTxLn itl : ogl.getItTxs()) {
              if (itl.getTax() == null) {
                oitl = itl;
                break;
              }
            }
          }
          if (oitl == null) {
            oitl = new CuOrSeGdTxLn();
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
      CuOrSeSrLn osl = null;
      if (!cuOr.getIsNew()) {
        for (CuOrSeSrLn sl : cuOr.getServs()) {
          if (sl.getService() == null) {
            osl = sl;
            break;
          }
        }
      }
      if (osl == null) {
        osl = new CuOrSeSrLn();
        osl.setIsNew(true);
        cuOr.getServs().add(osl);
      }
      SeService sr = new SeService();
      sr.setSeller(pSel);
      sr.setItsId(pCartLn.getItId());
      sr.setItsName(pCartLn.getItsName());
      osl.setService(sr);
      osl.setDt1(pCartLn.getDt1());
      osl.setDt2(pCartLn.getDt2());
      if (citls.size() > 0) {
        if (osl.getIsNew()) {
          osl.setItTxs(new ArrayList<CuOrSeSrTxLn>());
        }
        for (CartItTxLn citl : citls) {
          CuOrSeSrTxLn oitl = null;
          if (!cuOr.getIsNew()) {
            for (CuOrSeSrTxLn itl : osl.getItTxs()) {
              if (itl.getTax() == null) {
                oitl = itl;
                break;
              }
            }
          }
          if (oitl == null) {
            oitl = new CuOrSeSrTxLn();
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

  /**
   * <p>It makes order line.</p>
   * @param pRqVs Request scoped Vars
   * @param pOrders Orders
   * @param pItPl item place
   * @param pCartLn Cart Line
   * @param pTs trading settings
   * @throws Exception an Exception
   **/
  public final void makeOrdLn(final Map<String, Object> pRqVs,
    final List<CustOrder> pOrders, final AItemPlace<?, ?> pItPl,
      final CartLn pCartLn, final TradingSettings pTs) throws Exception {
    CustOrder cuOr = null;
    boolean isNdOrInit = true;
    for (CustOrder co : pOrders) {
      if (co.getCurr() != null) {
        cuOr = co;
        isNdOrInit = false;
        break;
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
      //TODO method "pickup by buyer from several places"
      //cuOr.setPlace(pItPl.getPickUpPlace());
      cuOr.setPur(pCartLn.getItsOwner().getItsVersion());
      cuOr.setCurr(pCartLn.getItsOwner().getCurr());
      cuOr.setExcRt(pCartLn.getItsOwner().getExcRt());
      cuOr.setDescr(pCartLn.getItsOwner().getDescr());
    }
    pRqVs.put(CartItTxLn.class.getSimpleName() + "itsOwnerdeepLevel", 1);
    pRqVs.put(CartItTxLn.class.getSimpleName() + "taxdeepLevel", 1);
    List<CartItTxLn> citls = getSrvOrm().retrieveListWithConditions(pRqVs,
      CartItTxLn.class, "where DISAB=0 and ITSOWNER=" + pCartLn.getItsId());
    pRqVs.remove(CartItTxLn.class.getSimpleName() + "itsOwnerdeepLevel");
    pRqVs.remove(CartItTxLn.class.getSimpleName() + "taxdeepLevel");
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

  /**
   * <p>Redirect.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @throws Exception - an exception
   **/
  public final void redir(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    String procNm = pRqDt.getParameter("nmPrcRed");
    IProcessor proc = this.procFac.lazyGet(pRqVs, procNm);
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
