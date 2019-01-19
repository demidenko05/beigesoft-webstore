package org.beigesoft.webstore.service;

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

import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.model.IRequestData;
import org.beigesoft.model.ColumnsValues;
import org.beigesoft.log.ILogger;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.accounting.persistable.UnitOfMeasure;
import org.beigesoft.webstore.model.EOrdStat;
import org.beigesoft.webstore.model.EPaymentMethod;
import org.beigesoft.webstore.model.Purch;
import org.beigesoft.webstore.persistable.SeSeller;
import org.beigesoft.webstore.persistable.SeSerBus;
import org.beigesoft.webstore.persistable.SerBus;
import org.beigesoft.webstore.persistable.SeGoodsPlace;
import org.beigesoft.webstore.persistable.GoodsPlace;
import org.beigesoft.webstore.persistable.ServicePlace;
import org.beigesoft.webstore.persistable.SeServicePlace;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.CuOrSe;
import org.beigesoft.webstore.persistable.CuOrSeSrLn;
import org.beigesoft.webstore.persistable.CuOrSeGdLn;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.persistable.CustOrderGdLn;
import org.beigesoft.webstore.persistable.CustOrderSrvLn;
import org.beigesoft.webstore.persistable.SettingsAdd;
import org.beigesoft.webstore.persistable.ItemInList;

/**
 * <p>It accepts all buyer's orders.
 * It changes item's availability and orders status to PENDING.
 * If any item is unavailable, then it throws exception.
 * And so does if there are several payees for online payment.</p>
 *
 * @param <RS> platform dependent RDBMS recordset
 * @author Yury Demidenko
 */
public class AcpOrd<RS> implements IAcpOrd {

  /**
   * <p>Logger.</p>
   **/
  private ILogger log;

  /**
   * <p>ORM service.</p>
   */
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>DB service.</p>
   */
  private ISrvDatabase<RS> srvDb;

  /**
   * <p>Query goods availability checkout.</p>
   **/
  private String quOrGdChk;

  /**
   * <p>Query services availability checkout.</p>
   **/
  private String quOrSrChk;

  /**
   * <p>Fastest locking (only record locking), i.e.:
   * update WAREHOUSEREST set THEREST=THEREST-1.5
   * where UNITOFMEASURE=5 and INVITEM=1 and WAREHOUSESITE=1.
   * It will fail when THEREST become less than 0.
   * Update is atomic RDBMS operation, i.e. it
   * locks affected record during invocation.</p>
   **/
  private boolean fastLoc = true;

  /**
   * <p>It accepts all buyer's orders.
   * It changes item's availability and orders status to PENDING.
   * If any item is unavailable, then it throws exception.
   * And so does if there are several payees for online payment.</p>
   * @param pRqVs additional request scoped parameters
   * @param pReqDt Request Data
   * @param pBur Buyer
   * @return list of accepted orders
   * @throws Exception - an exception
   **/
  @Override
  public final Purch accept(final Map<String, Object> pRqVs,
    final IRequestData pReqDt, final OnlineBuyer pBur) throws Exception {
    Purch rez = null;
    SettingsAdd setAdd = (SettingsAdd) pRqVs.get("setAdd");
    List<CustOrder> ords = null;
    List<CuOrSe> sords = null;
    String tbn = CustOrder.class.getSimpleName();
    String wheStBr = "where STAT=0 and BUYER=" + pBur.getItsId();
    Set<String> ndFlNm = new HashSet<String>();
    ndFlNm.add("itsId");
    ndFlNm.add("itsName");
    pRqVs.put("PickUpPlaceneededFields", ndFlNm);
    pRqVs.put(tbn + "buyerdeepLevel", 1);
    ords = this.srvOrm.retrieveListWithConditions(pRqVs,
      CustOrder.class, wheStBr);
    pRqVs.remove(tbn + "buyerdeepLevel");
    tbn = CuOrSe.class.getSimpleName();
    Set<String> ndFlDc = new HashSet<String>();
    ndFlDc.add("seller");
    pRqVs.put("SeSellerneededFields", ndFlDc);
    pRqVs.put("DebtorCreditorneededFields", ndFlNm);
    pRqVs.put(tbn + "seldeepLevel", 3);
    pRqVs.put(tbn + "buyerdeepLevel", 1);
    sords = this.srvOrm.retrieveListWithConditions(pRqVs,
      CuOrSe.class, wheStBr);
    pRqVs.remove("DebtorCreditorneededFields");
    pRqVs.remove("SeSellerneededFields");
    pRqVs.remove(tbn + "seldeepLevel");
    pRqVs.remove(tbn + "buyerdeepLevel");
    pRqVs.remove("PickUpPlaceneededFields");
    if (setAdd.getOnlMd() == 0 && sords.size() > 0) {
      //checking for several online payees:
      boolean isOwnOnlPay = false;
      for (CustOrder co : ords) {
        if (co.getPayMeth().equals(EPaymentMethod.ONLINE)
          || co.getPayMeth().equals(EPaymentMethod.PARTIAL_ONLINE)
            || co.getPayMeth().equals(EPaymentMethod.PAYPAL)
              || co.getPayMeth().equals(EPaymentMethod.PAYPAL_ANY)) {
          isOwnOnlPay = true;
          break;
        }
      }
      SeSeller selOnl = null;
      for (CuOrSe co : sords) {
        if (co.getPayMeth().equals(EPaymentMethod.ONLINE)
          || co.getPayMeth().equals(EPaymentMethod.PARTIAL_ONLINE)
            || co.getPayMeth().equals(EPaymentMethod.PAYPAL)
              || co.getPayMeth().equals(EPaymentMethod.PAYPAL_ANY)) {
          if (isOwnOnlPay) {
            throw new Exception("Several online payee for buyer#"
              + pBur.getItsId());
          } else if (selOnl == null) {
            selOnl = co.getSel();
          } else if (!selOnl.getItsId().getItsId()
            .equals(co.getSel().getItsId().getItsId())) {
            throw new Exception("Several online S.E.Payee for buyer#"
              + pBur.getItsId());
          }
        }
      }
    }
    //consolidated order with bookable items for farther booking:
    if (ords.size() > 0) {
      CustOrder cor = check1(pRqVs, ords);
      adChekBook(pRqVs, cor);
    }
    if (sords.size() > 0) {
      CuOrSe cor = checkSe1(pRqVs, sords);
      adChekBookSe(pRqVs, cor);
    }
    //change orders status:
    if (setAdd.getOpMd() == 0) {
      String[] fieldsNames = new String[] {"itsId", "itsVersion", "stat"};
      pRqVs.put("fieldsNames", fieldsNames);
      for (CustOrder co : ords) {
        co.setStat(EOrdStat.BOOKED);
        getSrvOrm().updateEntity(pRqVs, co);
      }
      for (CuOrSe co : sords) {
        co.setStat(EOrdStat.BOOKED);
        getSrvOrm().updateEntity(pRqVs, co);
      }
      pRqVs.remove("fieldsNames");
    } else {
      ColumnsValues cvs = new ColumnsValues();
      cvs.put("itsVersion", new Date().getTime());
      cvs.put("stat", EOrdStat.BOOKED.ordinal());
      this.srvDb.executeUpdate("CUSTORDER", cvs, "STAT=0 and BUYER="
        + pBur.getItsId());
      this.srvDb.executeUpdate("CUORSE", cvs, "STAT=0 and BUYER="
        + pBur.getItsId());
    }
    rez = new Purch();
    if (ords.size() > 0) {
      rez.setOrds(ords);
    }
    if (sords.size() > 0) {
      rez.setSords(sords);
    }
    return rez;
  }

  //utils:
  /**
   * <p>It half-checks items.</p>
   * @param pRqVs additional request scoped parameters
   * @param pOrds S.E. orders
   * @return consolidated order with bookable items
   * @throws Exception - an exception if checking fail
   **/
  public final CuOrSe checkSe1(final Map<String, Object> pRqVs,
    final List<CuOrSe> pOrds) throws Exception {
    Map<Long, StringBuffer> plOrIds = new HashMap<Long, StringBuffer>();
    for (CuOrSe co : pOrds) {
      co.setGoods(new ArrayList<CuOrSeGdLn>());
      co.setServs(new ArrayList<CuOrSeSrLn>());
      StringBuffer ordIds = plOrIds.get(co.getPlace().getItsId());
      if (ordIds == null) {
        ordIds = new StringBuffer();
        plOrIds.put(co.getPlace().getItsId(), ordIds);
        ordIds.append(co.getItsId().toString());
      } else {
        ordIds.append("," + co.getItsId());
      }
    }
    Set<String> ndFlNm = new HashSet<String>();
    ndFlNm.add("itsId");
    ndFlNm.add("itsName");
    Set<String> ndFl = new HashSet<String>();
    ndFl.add("itsId");
    ndFl.add("itsOwner");
    ndFl.add("itsName");
    ndFl.add("good");
    ndFl.add("uom");
    ndFl.add("quant");
    ndFl.add("price");
    ndFl.add("tot");
    ndFl.add("totTx");
    String tbn = CuOrSeGdLn.class.getSimpleName();
    String tbnUom = UnitOfMeasure.class.getSimpleName();
    pRqVs.put(tbn + "neededFields", ndFl);
    pRqVs.put(tbn + "gooddeepLevel", 1);
    pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
    pRqVs.put(tbnUom + "neededFields", ndFlNm);
    List<CuOrSeGdLn> allGoods = new ArrayList<CuOrSeGdLn>();
    List<CuOrSeSrLn> allServs = new ArrayList<CuOrSeSrLn>();
    for (Map.Entry<Long, StringBuffer> ent : plOrIds.entrySet()) {
      String quer = lazyGetQuOrGdChk().replace(":TORLN", "CUORSEGDLN")
        .replace(":TITPL", "SEGOODSPLACE").replace(":ORIDS", ent.getValue()
          .toString()).replace(":PLACE", ent.getKey().toString());
      List<CuOrSeGdLn> allGds = this.srvOrm.retrieveListByQuery(
        pRqVs, CuOrSeGdLn.class, quer);
      for (CuOrSeGdLn gl : allGds) {
        if (gl.getQuant().compareTo(BigDecimal.ZERO) == 0) {
          throw new Exception("S.E.Good is not available #"
            + gl.getGood().getItsId());
        }
      }
      for (CuOrSeGdLn gl : allGds) {
        for (CuOrSe co : pOrds) {
          if (co.getItsId().equals(gl.getItsOwner().getItsId())) {
            gl.setItsOwner(co);
            co.getGoods().add(gl);
          }
        }
        CuOrSeGdLn cgl = new CuOrSeGdLn();
        cgl.setItsId(gl.getItsId());
        cgl.setGood(gl.getGood());
        cgl.setQuant(gl.getQuant());
        //UOM holds place ID for additional checking and booking:
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setItsId(ent.getKey());
        cgl.setUom(uom);
        allGoods.add(cgl);
      }
    }
    pRqVs.remove(tbn + "gooddeepLevel");
    pRqVs.remove(tbn + "neededFields");
    pRqVs.remove(tbn + "itsOwnerdeepLevel");
    ndFl.remove("good");
    ndFl.add("service");
    ndFl.add("dt1");
    ndFl.add("dt2");
    tbn = CuOrSeSrLn.class.getSimpleName();
    pRqVs.put(tbn + "neededFields", ndFl);
    pRqVs.put(tbn + "servicedeepLevel", 1);
    pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
    //non-bookable service checkout and bookable services half-checkout:
    for (Map.Entry<Long, StringBuffer> ent : plOrIds.entrySet()) {
      String quer = lazyGetQuOrSrChk().replace(":TORLN", "CUORSESRLN")
        .replace(":TITPL", "SESERVICEPLACE").replace(":ORIDS", ent.getValue()
          .toString()).replace(":PLACE", ent.getKey().toString());
      List<CuOrSeSrLn> allSrvs = this.srvOrm.retrieveListByQuery(
        pRqVs, CuOrSeSrLn.class, quer);
      for (CuOrSeSrLn sl : allSrvs) {
        if (sl.getQuant().compareTo(BigDecimal.ZERO) == 0) {
          throw new Exception("Service is not available #"
            + sl.getService().getItsId());
        }
      }
      for (CuOrSeSrLn sl : allSrvs) {
        for (CuOrSe co : pOrds) {
          if (co.getItsId().equals(sl.getItsOwner().getItsId())) {
            sl.setItsOwner(co);
            co.getServs().add(sl);
          }
        }
        CuOrSeSrLn csl = new CuOrSeSrLn();
        csl.setItsId(sl.getItsId());
        csl.setService(sl.getService());
        csl.setQuant(sl.getQuant());
        csl.setDt1(sl.getDt1());
        csl.setDt2(sl.getDt2());
        //UOM holds place ID for additional checking and booking:
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setItsId(ent.getKey());
        csl.setUom(uom);
        allServs.add(csl);
      }
    }
    pRqVs.remove(tbn + "servicedeepLevel");
    pRqVs.remove(tbn + "neededFields");
    pRqVs.remove(tbn + "itsOwnerdeepLevel");
    pRqVs.remove(tbnUom + "neededFields");
    CuOrSe cor = new CuOrSe();
    cor.setGoods(allGoods);
    cor.setServs(allServs);
    return cor;
  }

  /**
   * <p>It half-checks items.</p>
   * @param pRqVs additional request scoped parameters
   * @param pOrds orders
   * @return consolidated order with bookable items
   * @throws Exception - an exception if checking fail
   **/
  public final CustOrder check1(final Map<String, Object> pRqVs,
    final List<CustOrder> pOrds) throws Exception {
    Map<Long, StringBuffer> plOrIds = new HashMap<Long, StringBuffer>();
    for (CustOrder co : pOrds) {
      co.setGoods(new ArrayList<CustOrderGdLn>());
      co.setServs(new ArrayList<CustOrderSrvLn>());
      StringBuffer ordIds = plOrIds.get(co.getPlace().getItsId());
      if (ordIds == null) {
        ordIds = new StringBuffer();
        plOrIds.put(co.getPlace().getItsId(), ordIds);
        ordIds.append(co.getItsId().toString());
      } else {
        ordIds.append("," + co.getItsId());
      }
    }
    Set<String> ndFlNm = new HashSet<String>();
    ndFlNm.add("itsId");
    ndFlNm.add("itsName");
    Set<String> ndFl = new HashSet<String>();
    ndFl.add("itsId");
    ndFl.add("itsOwner");
    ndFl.add("itsName");
    ndFl.add("good");
    ndFl.add("uom");
    ndFl.add("quant");
    ndFl.add("price");
    ndFl.add("tot");
    ndFl.add("totTx");
    String tbn = CustOrderGdLn.class.getSimpleName();
    String tbnUom = UnitOfMeasure.class.getSimpleName();
    pRqVs.put(tbn + "neededFields", ndFl);
    pRqVs.put(tbn + "gooddeepLevel", 1);
    pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
    pRqVs.put(tbnUom + "neededFields", ndFlNm);
    List<CustOrderGdLn> allGoods = new ArrayList<CustOrderGdLn>();
    List<CustOrderSrvLn> allServs = new ArrayList<CustOrderSrvLn>();
    for (Map.Entry<Long, StringBuffer> ent : plOrIds.entrySet()) {
      String quer = lazyGetQuOrGdChk().replace(":TORLN", "CUSTORDERGDLN")
        .replace(":TITPL", "GOODSPLACE").replace(":ORIDS", ent.getValue()
          .toString()).replace(":PLACE", ent.getKey().toString());
      List<CustOrderGdLn> allGds = this.srvOrm.retrieveListByQuery(
        pRqVs, CustOrderGdLn.class, quer);
      for (CustOrderGdLn gl : allGds) {
        if (gl.getQuant().compareTo(BigDecimal.ZERO) == 0) {
          throw new Exception("Good is not available #"
            + gl.getGood().getItsId());
        }
      }
      for (CustOrderGdLn gl : allGds) {
        for (CustOrder co : pOrds) {
          if (co.getItsId().equals(gl.getItsOwner().getItsId())) {
            gl.setItsOwner(co);
            co.getGoods().add(gl);
          }
        }
        CustOrderGdLn cgl = new CustOrderGdLn();
        cgl.setItsId(gl.getItsId());
        cgl.setGood(gl.getGood());
        cgl.setQuant(gl.getQuant());
        //UOM holds place ID for additional checking and booking:
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setItsId(ent.getKey());
        cgl.setUom(uom);
        allGoods.add(cgl);
      }
    }
    pRqVs.remove(tbn + "gooddeepLevel");
    pRqVs.remove(tbn + "neededFields");
    pRqVs.remove(tbn + "itsOwnerdeepLevel");
    ndFl.remove("good");
    ndFl.add("service");
    ndFl.add("dt1");
    ndFl.add("dt2");
    tbn = CustOrderSrvLn.class.getSimpleName();
    pRqVs.put(tbn + "neededFields", ndFl);
    pRqVs.put(tbn + "servicedeepLevel", 1);
    pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
    //non-bookable service checkout and bookable services half-checkout:
    for (Map.Entry<Long, StringBuffer> ent : plOrIds.entrySet()) {
      String quer = lazyGetQuOrSrChk().replace(":TORLN", "CUSTORDERSRVLN")
        .replace(":TITPL", "SERVICEPLACE").replace(":ORIDS", ent.getValue()
          .toString()).replace(":PLACE", ent.getKey().toString());
      List<CustOrderSrvLn> allSrvs = this.srvOrm.retrieveListByQuery(
        pRqVs, CustOrderSrvLn.class, quer);
      for (CustOrderSrvLn sl : allSrvs) {
        if (sl.getQuant().compareTo(BigDecimal.ZERO) == 0) {
          throw new Exception("Service is not available #"
            + sl.getService().getItsId());
        }
      }
      for (CustOrderSrvLn sl : allSrvs) {
        for (CustOrder co : pOrds) {
          if (co.getItsId().equals(sl.getItsOwner().getItsId())) {
            sl.setItsOwner(co);
            co.getServs().add(sl);
          }
        }
        CustOrderSrvLn csl = new CustOrderSrvLn();
        csl.setItsId(sl.getItsId());
        csl.setService(sl.getService());
        csl.setQuant(sl.getQuant());
        csl.setDt1(sl.getDt1());
        csl.setDt2(sl.getDt2());
        //UOM holds place ID for additional checking and booking:
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setItsId(ent.getKey());
        csl.setUom(uom);
        allServs.add(csl);
      }
    }
    pRqVs.remove(tbn + "servicedeepLevel");
    pRqVs.remove(tbn + "neededFields");
    pRqVs.remove(tbn + "itsOwnerdeepLevel");
    pRqVs.remove(tbnUom + "neededFields");
    CustOrder cor = new CustOrder();
    cor.setGoods(allGoods);
    cor.setServs(allServs);
    return cor;
  }

  /**
   * <p>It checks additionally and books S.E. items.</p>
   * @param pRqVs additional request scoped parameters
   * @param pCoOr consolidate order
   * @throws Exception - an exception if incomplete
   **/
  public final void adChekBookSe(final Map<String, Object> pRqVs,
    final CuOrSe pCoOr) throws Exception {
    //additional checking:
    String tbn;
    //check availability and booking for same good in different orders:
    List<CuOrSeGdLn> gljs = null;
    List<CuOrSeGdLn> glrs = null;
    for (CuOrSeGdLn gl : pCoOr.getGoods()) {
      //join lines with same item:
      for (CuOrSeGdLn gl0 : pCoOr.getGoods()) {
        if (!gl.getItsId().equals(gl0.getItsId())
          && gl.getGood().getItsId().equals(gl0.getGood().getItsId())) {
          if (gljs == null) {
            gljs = new ArrayList<CuOrSeGdLn>();
            glrs = new ArrayList<CuOrSeGdLn>();
          }
          glrs.add(gl0);
          if (!gljs.contains(gl)) {
            gljs.add(gl);
          }
          gl.setQuant(gl.getQuant().add(gl0.getQuant()));
        }
      }
    }
    if (gljs != null) {
      for (CuOrSeGdLn glr : glrs) {
        pCoOr.getGoods().remove(glr);
      }
      tbn = SeGoodsPlace.class.getSimpleName();
      pRqVs.put(tbn + "itemdeepLevel", 1); //only ID
      pRqVs.put(tbn + "pickUpPlacedeepLevel", 1);
      for (CuOrSeGdLn gl : gljs) {
        SeGoodsPlace gp = getSrvOrm().retrieveEntityWithConditions(pRqVs,
          SeGoodsPlace.class, "where ITEM=" + gl.getGood().getItsId()
            + " and PICKUPPLACE=" + gl.getUom().getItsId()
              + " and ITSQUANTITY>=" + gl.getQuant());
        if (gp == null) {
          throw new Exception("AC. S.E.Good is not available #"
            + gl.getGood().getItsId());
        }
      }
      pRqVs.remove(tbn + "itemdeepLevel");
      pRqVs.remove(tbn + "pickUpPlacedeepLevel");
    }
    //bookable services final-checkout:
    String cond;
    tbn = SeServicePlace.class.getSimpleName();
    pRqVs.put(tbn + "itemdeepLevel", 1); //only ID
    pRqVs.put(tbn + "pickUpPlacedeepLevel", 1);
    for (CuOrSeSrLn sl : pCoOr.getServs()) {
      if (sl.getDt1() != null) {
    cond = "left join (select distinct SERV from SESERBUS where FRE=0 and SERV="
    + sl.getService().getItsId() + " and FRTM>=" + sl.getDt1().getTime()
  + " and TITM<" + sl.getDt2().getTime()
+ ") as SERBUS on SERBUS.SERV=SESERVICEPLACE.ITEM where ITEM=" + sl
  .getService() + " and PLACE=" + sl.getUom().getItsId()
    + " and ITSQUANTITY>0 and SERBUS.SERV is null";
        SeServicePlace sp = getSrvOrm()
          .retrieveEntityWithConditions(pRqVs, SeServicePlace.class, cond);
        if (sp == null) {
          throw new Exception("AC. BK.Service is not available #"
            + sl.getService().getItsId());
        }
      }
    }
    pRqVs.remove(tbn + "itemdeepLevel");
    pRqVs.remove(tbn + "pickUpPlacedeepLevel");
    //booking:
    //changing availability (booking):
    ColumnsValues cvsIil = null;
    String[] fnmIil = null;
    if (this.fastLoc) {
      cvsIil = new ColumnsValues();
      cvsIil.getFormula().add("availableQuantity");
    } else {
      fnmIil = new String[] {"itsId", "itsVersion", "availableQuantity"};
    }
    tbn = SeGoodsPlace.class.getSimpleName();
    pRqVs.put(tbn + "itemdeepLevel", 1); //only ID
    pRqVs.put(tbn + "pickUpPlacedeepLevel", 1);
    for (CuOrSeGdLn gl : pCoOr.getGoods()) {
      SeGoodsPlace gp = getSrvOrm().retrieveEntityWithConditions(pRqVs,
        SeGoodsPlace.class, "where ISALWAYS=0 and ITEM=" + gl.getGood()
          .getItsId() + " and PICKUPPLACE=" + gl.getUom().getItsId());
      if (gp != null) {
        gp.setItsQuantity(gp.getItsQuantity().subtract(gl.getQuant()));
        if (gp.getItsQuantity().compareTo(BigDecimal.ZERO) == -1) {
          //previous test should not be passed!!!
          throw new Exception("AC. S.E.Good is not available #"
            + gl.getGood().getItsId());
        } else {
          //TODO PERFORM fastupd
          getSrvOrm().updateEntity(pRqVs, gp);
          String wheTyId = "ITSTYPE=2 and ITEMID=" + gp.getItem().getItsId();
          if (this.fastLoc) {
            //it must be RDBMS constraint: "availableQuantity>=0"!
            cvsIil.put("itsVersion", new Date().getTime());
            cvsIil.put("availableQuantity", "AVAILABLEQUANTITY-"
              + gl.getQuant());
            this.srvDb.executeUpdate("ITEMINLIST", cvsIil, wheTyId);
          } else {
            pRqVs.put("fieldsNames", fnmIil);
            List<ItemInList> iils = this.srvOrm.retrieveListWithConditions(
              pRqVs, ItemInList.class, "where " + wheTyId);
            if (iils.size() == 1) {
              BigDecimal aq = iils.get(0).getAvailableQuantity()
                .subtract(gl.getQuant());
              if (aq.compareTo(BigDecimal.ZERO) == -1) {
                pRqVs.remove("fieldsNames");
        throw new Exception("ItemInList NA avQuan SeGood: id/itId/avQua/quan: "
      + iils.get(0).getItsId() + "/" + gp.getItem().getItsId() + "/"
    + iils.get(0).getAvailableQuantity() + "/" + gl.getQuant());
              } else {
                iils.get(0).setAvailableQuantity(aq);
                getSrvOrm().updateEntity(pRqVs, iils);
              }
            } else {
              pRqVs.remove("fieldsNames");
              throw new Exception("ItemInList WC for SeGood: itId/count: "
                + gp.getItem().getItsId() + "/" + iils.size());
            }
            pRqVs.remove("fieldsNames");
          }
        }
      }
    }
    pRqVs.remove(tbn + "itemdeepLevel");
    pRqVs.remove(tbn + "pickUpPlacedeepLevel");
    tbn = SeServicePlace.class.getSimpleName();
    pRqVs.put(tbn + "itemdeepLevel", 1); //only ID
    pRqVs.put(tbn + "pickUpPlacedeepLevel", 1);
    boolean tibs = false;
    for (CuOrSeSrLn sl : pCoOr.getServs()) {
      if (sl.getDt1() == null) {
        SeServicePlace sp = getSrvOrm().retrieveEntityWithConditions(pRqVs,
          SeServicePlace.class, "where ISALWAYS=0 and ITEM=" + sl.getService()
            .getItsId() + " and PICKUPPLACE=" + sl.getUom().getItsId());
        if (sp != null) {
          sp.setItsQuantity(sp.getItsQuantity().subtract(sl.getQuant()));
          if (sp.getItsQuantity().compareTo(BigDecimal.ZERO) == -1) {
            //previous test should not be passed!!!
            throw new Exception("NBK SeService is not available #"
              + sl.getService().getItsId());
          } else {
            //TODO PERFORM fastupd
            getSrvOrm().updateEntity(pRqVs, sp);
            String wheTyId = "ITSTYPE=3 and ITEMID=" + sp.getItem().getItsId();
            if (this.fastLoc) {
              cvsIil.put("itsVersion", new Date().getTime());
              cvsIil.put("availableQuantity",  "AVAILABLEQUANTITY-"
                + sl.getQuant());
              this.srvDb.executeUpdate("ITEMINLIST", cvsIil, wheTyId);
            } else {
              pRqVs.put("fieldsNames", fnmIil);
              List<ItemInList> iils = this.srvOrm.retrieveListWithConditions(
                pRqVs, ItemInList.class, "where " + wheTyId);
              if (iils.size() == 1) {
                BigDecimal aq = iils.get(0).getAvailableQuantity()
                  .subtract(sl.getQuant());
                if (aq.compareTo(BigDecimal.ZERO) == -1) {
                  pRqVs.remove("fieldsNames");
        throw new Exception("ItemInList NA avQuan SESERV: id/itId/avQua/quan: "
      + iils.get(0).getItsId() + "/" + sp.getItem().getItsId() + "/"
    + iils.get(0).getAvailableQuantity() + "/" + sl.getQuant());
                } else {
                  iils.get(0).setAvailableQuantity(aq);
                  getSrvOrm().updateEntity(pRqVs, iils);
                }
              } else {
                pRqVs.remove("fieldsNames");
                throw new Exception("ItemInList WC for SESERV: itId/count: "
                  + sp.getItem().getItsId() + "/" + iils.size());
              }
              pRqVs.remove("fieldsNames");
            }
          }
        }
      } else {
        tibs = true;
      }
    }
    pRqVs.remove(tbn + "itemdeepLevel");
    pRqVs.remove(tbn + "pickUpPlacedeepLevel");
    if (tibs) {
      tbn = SeSerBus.class.getSimpleName();
      Set<String> ndFl = new HashSet<String>();
      ndFl.add("itsId");
      ndFl.add("itsVersion");
      pRqVs.put(tbn + "neededFields", ndFl);
      List<SeSerBus> sbas = this.srvOrm.retrieveListWithConditions(pRqVs,
        SeSerBus.class, "where FRE=1");
      int i = 0;
      pRqVs.remove(tbn + "neededFields");
      for (CuOrSeSrLn sl : pCoOr.getServs()) {
        if (sl.getDt1() != null) {
          SeSerBus sb;
          if (i < sbas.size()) {
            sb = sbas.get(i);
            sb.setFre(false);
          } else {
            sb = new SeSerBus();
          }
          sb.setServ(sl.getService());
          sb.setFrTm(sl.getDt1());
          sb.setTiTm(sl.getDt2());
          if (i < sbas.size()) {
            getSrvOrm().updateEntity(pRqVs, sb);
            i++;
          } else {
            getSrvOrm().insertEntity(pRqVs, sb);
          }
        }
      }
    }
  }

  /**
   * <p>It checks additionally and books items.</p>
   * @param pRqVs additional request scoped parameters
   * @param pCoOr consolidate order
   * @throws Exception - an exception if incomplete
   **/
  public final void adChekBook(final Map<String, Object> pRqVs,
    final CustOrder pCoOr) throws Exception {
    //additional checking:
    String tbn;
    //check availability and booking for same good in different orders:
    List<CustOrderGdLn> gljs = null;
    List<CustOrderGdLn> glrs = null;
    for (CustOrderGdLn gl : pCoOr.getGoods()) {
      //join lines with same item:
      for (CustOrderGdLn gl0 : pCoOr.getGoods()) {
        if (!gl.getItsId().equals(gl0.getItsId())
          && gl.getGood().getItsId().equals(gl0.getGood().getItsId())) {
          if (gljs == null) {
            gljs = new ArrayList<CustOrderGdLn>();
            glrs = new ArrayList<CustOrderGdLn>();
          }
          glrs.add(gl0);
          if (!gljs.contains(gl)) {
            gljs.add(gl);
          }
          gl.setQuant(gl.getQuant().add(gl0.getQuant()));
        }
      }
    }
    if (gljs != null) {
      for (CustOrderGdLn glr : glrs) {
        pCoOr.getGoods().remove(glr);
      }
      tbn = GoodsPlace.class.getSimpleName();
      pRqVs.put(tbn + "itemdeepLevel", 1); //only ID
      pRqVs.put(tbn + "pickUpPlacedeepLevel", 1);
      for (CustOrderGdLn gl : gljs) {
        GoodsPlace gp = getSrvOrm().retrieveEntityWithConditions(pRqVs,
          GoodsPlace.class, "where ITEM=" + gl.getGood().getItsId()
            + " and PICKUPPLACE=" + gl.getUom().getItsId()
              + " and ITSQUANTITY>=" + gl.getQuant());
        if (gp == null) {
          throw new Exception("AC. Good is not available #"
            + gl.getGood().getItsId());
        }
      }
      pRqVs.remove(tbn + "itemdeepLevel");
      pRqVs.remove(tbn + "pickUpPlacedeepLevel");
    }
    //bookable services final-checkout:
    String cond;
    tbn = ServicePlace.class.getSimpleName();
    pRqVs.put(tbn + "itemdeepLevel", 1); //only ID
    pRqVs.put(tbn + "pickUpPlacedeepLevel", 1);
    for (CustOrderSrvLn sl : pCoOr.getServs()) {
      if (sl.getDt1() != null) {
      cond = "left join (select distinct SERV from SERBUS where FRE=0 and SERV="
    + sl.getService().getItsId() + " and FRTM>=" + sl.getDt1().getTime()
  + " and TITM<" + sl.getDt2().getTime()
+ ") as SERBUS on SERBUS.SERV=SERVICEPLACE.ITEM where ITEM=" + sl
  .getService() + " and PLACE=" + sl.getUom().getItsId()
    + " and ITSQUANTITY>0 and SERBUS.SERV is null";
        ServicePlace sp = getSrvOrm()
          .retrieveEntityWithConditions(pRqVs, ServicePlace.class, cond);
        if (sp == null) {
          throw new Exception("AC. BK.Service is not available #"
            + sl.getService().getItsId());
        }
      }
    }
    pRqVs.remove(tbn + "itemdeepLevel");
    pRqVs.remove(tbn + "pickUpPlacedeepLevel");
    //booking:
    //changing availability (booking):
    ColumnsValues cvsIil = null;
    String[] fnmIil = null;
    if (this.fastLoc) {
      cvsIil = new ColumnsValues();
      cvsIil.getFormula().add("availableQuantity");
    } else {
      fnmIil = new String[] {"itsId", "itsVersion", "availableQuantity"};
    }
    tbn = GoodsPlace.class.getSimpleName();
    pRqVs.put(tbn + "itemdeepLevel", 1); //only ID
    pRqVs.put(tbn + "pickUpPlacedeepLevel", 1);
    for (CustOrderGdLn gl : pCoOr.getGoods()) {
      GoodsPlace gp = getSrvOrm().retrieveEntityWithConditions(pRqVs,
        GoodsPlace.class, "where ISALWAYS=0 and ITEM=" + gl.getGood().getItsId()
          + " and PICKUPPLACE=" + gl.getUom().getItsId());
      if (gp != null) {
        gp.setItsQuantity(gp.getItsQuantity().subtract(gl.getQuant()));
        if (gp.getItsQuantity().compareTo(BigDecimal.ZERO) == -1) {
          //previous test should not be passed!!!
          throw new Exception("AC. Good is not available #"
            + gl.getGood().getItsId());
        } else {
          //TODO PERFORM fastupd
          getSrvOrm().updateEntity(pRqVs, gp);
          String wheTyId = "ITSTYPE=0 and ITEMID=" + gp.getItem().getItsId();
          if (this.fastLoc) {
            //it must be RDBMS constraint: "availableQuantity>=0"!
            cvsIil.put("itsVersion", new Date().getTime());
            cvsIil.put("availableQuantity", "AVAILABLEQUANTITY-"
              + gl.getQuant());
            this.srvDb.executeUpdate("ITEMINLIST", cvsIil, wheTyId);
          } else {
            pRqVs.put("fieldsNames", fnmIil);
            List<ItemInList> iils = this.srvOrm.retrieveListWithConditions(
              pRqVs, ItemInList.class, "where " + wheTyId);
            if (iils.size() == 1) {
              BigDecimal aq = iils.get(0).getAvailableQuantity()
                .subtract(gl.getQuant());
              if (aq.compareTo(BigDecimal.ZERO) == -1) {
                pRqVs.remove("fieldsNames");
        throw new Exception("ItemInList NA avQuan InvItem: id/itId/avQua/quan: "
      + iils.get(0).getItsId() + "/" + gp.getItem().getItsId() + "/"
    + iils.get(0).getAvailableQuantity() + "/" + gl.getQuant());
              } else {
                iils.get(0).setAvailableQuantity(aq);
                getSrvOrm().updateEntity(pRqVs, iils);
              }
            } else {
              pRqVs.remove("fieldsNames");
              throw new Exception("ItemInList WC for InvItem: itId/count: "
                + gp.getItem().getItsId() + "/" + iils.size());
            }
            pRqVs.remove("fieldsNames");
          }
        }
      }
    }
    pRqVs.remove(tbn + "itemdeepLevel");
    pRqVs.remove(tbn + "pickUpPlacedeepLevel");
    tbn = ServicePlace.class.getSimpleName();
    pRqVs.put(tbn + "itemdeepLevel", 1); //only ID
    pRqVs.put(tbn + "pickUpPlacedeepLevel", 1);
    boolean tibs = false;
    for (CustOrderSrvLn sl : pCoOr.getServs()) {
      if (sl.getDt1() == null) {
        ServicePlace sp = getSrvOrm().retrieveEntityWithConditions(pRqVs,
          ServicePlace.class, "where ISALWAYS=0 and ITEM=" + sl.getService()
            .getItsId() + " and PICKUPPLACE=" + sl.getUom().getItsId());
        if (sp != null) {
          sp.setItsQuantity(sp.getItsQuantity().subtract(sl.getQuant()));
          if (sp.getItsQuantity().compareTo(BigDecimal.ZERO) == -1) {
            //previous test should not be passed!!!
            throw new Exception("NBK service is not available #"
              + sl.getService().getItsId());
          } else {
            //TODO PERFORM fastupd
            getSrvOrm().updateEntity(pRqVs, sp);
            String wheTyId = "ITSTYPE=1 and ITEMID=" + sp.getItem().getItsId();
            if (this.fastLoc) {
              cvsIil.put("itsVersion", new Date().getTime());
              cvsIil.put("availableQuantity",  "AVAILABLEQUANTITY-"
                + sl.getQuant());
              this.srvDb.executeUpdate("ITEMINLIST", cvsIil, wheTyId);
            } else {
              pRqVs.put("fieldsNames", fnmIil);
              List<ItemInList> iils = this.srvOrm.retrieveListWithConditions(
                pRqVs, ItemInList.class, "where " + wheTyId);
              if (iils.size() == 1) {
                BigDecimal aq = iils.get(0).getAvailableQuantity()
                  .subtract(sl.getQuant());
                if (aq.compareTo(BigDecimal.ZERO) == -1) {
                  pRqVs.remove("fieldsNames");
        throw new Exception("ItemInList NA avQuan SERV: id/itId/avQua/quan: "
      + iils.get(0).getItsId() + "/" + sp.getItem().getItsId() + "/"
    + iils.get(0).getAvailableQuantity() + "/" + sl.getQuant());
                } else {
                  iils.get(0).setAvailableQuantity(aq);
                  getSrvOrm().updateEntity(pRqVs, iils);
                }
              } else {
                pRqVs.remove("fieldsNames");
                throw new Exception("ItemInList WC for SERV: itId/count: "
                  + sp.getItem().getItsId() + "/" + iils.size());
              }
              pRqVs.remove("fieldsNames");
            }
          }
        }
      } else {
        tibs = true;
      }
    }
    pRqVs.remove(tbn + "itemdeepLevel");
    pRqVs.remove(tbn + "pickUpPlacedeepLevel");
    if (tibs) {
      tbn = SerBus.class.getSimpleName();
      Set<String> ndFl = new HashSet<String>();
      ndFl.add("itsId");
      ndFl.add("itsVersion");
      pRqVs.put(tbn + "neededFields", ndFl);
      List<SerBus> sbas = this.srvOrm.retrieveListWithConditions(pRqVs,
        SerBus.class, "where FRE=1");
      int i = 0;
      pRqVs.remove(tbn + "neededFields");
      for (CustOrderSrvLn sl : pCoOr.getServs()) {
        if (sl.getDt1() != null) {
          SerBus sb;
          if (i < sbas.size()) {
            sb = sbas.get(i);
            sb.setFre(false);
          } else {
            sb = new SerBus();
          }
          sb.setServ(sl.getService());
          sb.setFrTm(sl.getDt1());
          sb.setTiTm(sl.getDt2());
          if (i < sbas.size()) {
            getSrvOrm().updateEntity(pRqVs, sb);
            i++;
          } else {
            getSrvOrm().insertEntity(pRqVs, sb);
          }
        }
      }
    }
  }

  /**
   * <p>Lazy Getter for quOrGdChk.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  public final String lazyGetQuOrGdChk() throws IOException {
    if (this.quOrGdChk == null) {
      String flName = "/webstore/ordGdChk.sql";
      this.quOrGdChk = loadString(flName);
    }
    return this.quOrGdChk;
  }

  /**
   * <p>Lazy Getter for quOrSrChk.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  public final String lazyGetQuOrSrChk() throws IOException {
    if (this.quOrSrChk == null) {
      String flName = "/webstore/ordSrChk.sql";
      this.quOrSrChk = loadString(flName);
    }
    return this.quOrSrChk;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = AcpOrd.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = AcpOrd.class
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

  //Simple getters and setters:
  /**
   * <p>Geter for log.</p>
   * @return ILogger
   **/
  public final ILogger getLog() {
    return this.log;
  }

  /**
   * <p>Setter for log.</p>
   * @param pLogger reference
   **/
  public final void setLog(final ILogger pLogger) {
    this.log = pLogger;
  }

  /**
   * <p>Getter for srvOrm.</p>
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
   * <p>Getter for srvDb.</p>
   * @return ISrvDatabase<RS>
   **/
  public final ISrvDatabase<RS> getSrvDb() {
    return this.srvDb;
  }

  /**
   * <p>Setter for srvDb.</p>
   * @param pSrvDb reference
   **/
  public final void setSrvDb(final ISrvDatabase<RS> pSrvDb) {
    this.srvDb = pSrvDb;
  }

  /**
   * <p>Getter for fastLoc.</p>
   * @return boolean
   **/
  public final boolean getFastLoc() {
    return this.fastLoc;
  }

  /**
   * <p>Setter for fastLoc.</p>
   * @param pFastLoc reference
   **/
  public final void setFastLoc(final boolean pFastLoc) {
    this.fastLoc = pFastLoc;
  }
}
