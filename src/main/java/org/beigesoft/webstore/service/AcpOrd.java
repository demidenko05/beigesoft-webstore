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
import org.beigesoft.webstore.persistable.SerBus;
import org.beigesoft.webstore.persistable.GoodsPlace;
import org.beigesoft.webstore.persistable.ServicePlace;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.CuOrSe;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.persistable.CustOrderGdLn;
import org.beigesoft.webstore.persistable.CustOrderSrvLn;
import org.beigesoft.webstore.persistable.SettingsAdd;

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
  private ILogger logger;

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
   * <p>Query non-bookable services availability checkout.</p>
   **/
  private String quOrSrNbChk;

  /**
   * <p>Query bookable services availability half-checkout.</p>
   **/
  private String quOrSrBkChk;

  /**
   * <p>It accepts all buyer's orders.
   * It changes item's availability and orders status to PENDING.
   * If any item is unavailable, then it throws exception.
   * And so does if there are several payees for online payment.</p>
   * @param pReqVars additional request scoped parameters
   * @param pReqDt Request Data
   * @param pBur Buyer
   * @return list of accepted orders
   * @throws Exception - an exception
   **/
  @Override
  public final Purch accept(final Map<String, Object> pReqVars,
    final IRequestData pReqDt, final OnlineBuyer pBur) throws Exception {
    Purch rez = null;
    SettingsAdd setAdd = (SettingsAdd) pReqVars.get("setAdd");
    List<CustOrder> ords = null;
    List<CuOrSe> sords = null;
    String tbn = CustOrder.class.getSimpleName();
    String wheStBr = "where STAT=0 and BUYER=" + pBur.getItsId();
    pReqVars.put(tbn + "buyerdeepLevel", 1);
    pReqVars.put(tbn + "placedeepLevel", 1);
    ords = this.srvOrm.retrieveListWithConditions(pReqVars,
      CustOrder.class, wheStBr);
    pReqVars.remove(tbn + "buyerdeepLevel");
    pReqVars.remove(tbn + "placedeepLevel");
    tbn = CuOrSe.class.getSimpleName();
    pReqVars.put(tbn + "seldeepLevel", 1);
    pReqVars.put(tbn + "buyerdeepLevel", 1);
    pReqVars.put(tbn + "placedeepLevel", 1);
    sords = this.srvOrm.retrieveListWithConditions(pReqVars,
      CuOrSe.class, wheStBr);
    pReqVars.remove(tbn + "seldeepLevel");
    pReqVars.remove(tbn + "buyerdeepLevel");
    pReqVars.remove(tbn + "placedeepLevel");
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
    CustOrder cor = null;
    if (ords.size() > 0) {
      cor = check1(pReqVars, ords);
      adChekBook(pReqVars, cor.getGoods(), cor.getServs());
    }
    //change orders status:
    if (setAdd.getOpMd() == 0) {
      String[] fieldsNames = new String[] {"itsId", "itsVersion", "stat"};
      pReqVars.put("fieldsNames", fieldsNames);
      for (CustOrder co : ords) {
        co.setStat(EOrdStat.BOOKED);
        getSrvOrm().updateEntity(pReqVars, co);
      }
      pReqVars.remove("fieldsNames");
    } else {
      ColumnsValues cvs = new ColumnsValues();
      cvs.setIdColumnsNames(new String[] {"itsId"});
      cvs.put("itsVersion", new Date().getTime());
      cvs.put("stat", EOrdStat.BOOKED.ordinal());
      this.srvDb.executeUpdate("CUSTORDER", cvs, "STAT=0 and BUYER="
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
   * @param pReqVars additional request scoped parameters
   * @param pOrds orders
   * @return consolidated order with bookable items
   * @throws Exception - an exception if checking fail
   **/
  public final CustOrder check1(final Map<String, Object> pReqVars,
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
    pReqVars.put(tbn + "neededFields", ndFl);
    pReqVars.put(tbn + "gooddeepLevel", 1);
    pReqVars.put(tbn + "itsOwnerLevel", 1);
    pReqVars.put(tbnUom + "neededFields", ndFlNm);
    List<CustOrderGdLn> allGoods = new ArrayList<CustOrderGdLn>();
    List<CustOrderSrvLn> allServs = new ArrayList<CustOrderSrvLn>();
    for (Map.Entry<Long, StringBuffer> ent : plOrIds.entrySet()) {
      String quer = lazyGetQuOrGdChk().replace(":ORIDS", ent.getValue()
        .toString()).replace(":PLACE", ent.getKey().toString());
      List<CustOrderGdLn> allGds = this.srvOrm.retrieveListByQuery(
        pReqVars, CustOrderGdLn.class, quer);
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
    pReqVars.remove(tbn + "gooddeepLevel");
    pReqVars.remove(tbn + "neededFields");
    pReqVars.remove(tbn + "itsOwnerLevel");
    ndFl.remove("good");
    ndFl.add("service");
    tbn = CustOrderSrvLn.class.getSimpleName();
    pReqVars.put(tbn + "neededFields", ndFl);
    pReqVars.put(tbn + "servicedeepLevel", 1);
    pReqVars.put(tbn + "itsOwnerLevel", 1);
    for (Map.Entry<Long, StringBuffer> ent : plOrIds.entrySet()) {
      String quer = lazyGetQuOrSrNbChk().replace(":ORIDS", ent.getValue()
        .toString()).replace(":PLACE", ent.getKey().toString());
      List<CustOrderSrvLn> allSrvs = this.srvOrm.retrieveListByQuery(
        pReqVars, CustOrderSrvLn.class, quer);
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
        //UOM holds place ID for additional checking and booking:
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setItsId(ent.getKey());
        csl.setUom(uom);
        allServs.add(csl);
      }
    }
    ndFl.add("dt1");
    ndFl.add("dt2");
    //bookable services half-checkout:
    for (Map.Entry<Long, StringBuffer> ent : plOrIds.entrySet()) {
      String quer = lazyGetQuOrSrBkChk().replace(":ORIDS", ent.getValue()
        .toString()).replace(":PLACE", ent.getKey().toString());
      List<CustOrderSrvLn> allSrvs = this.srvOrm.retrieveListByQuery(
        pReqVars, CustOrderSrvLn.class, quer);
      for (CustOrderSrvLn sl : allSrvs) {
        if (sl.getQuant().compareTo(BigDecimal.ZERO) == 0) {
          throw new Exception("Bookable service is not available #"
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
    pReqVars.remove(tbn + "servicedeepLevel");
    pReqVars.remove(tbn + "neededFields");
    pReqVars.remove(tbn + "itsOwnerLevel");
    pReqVars.remove(tbnUom + "neededFields");
    CustOrder cor = new CustOrder();
    cor.setGoods(allGoods);
    cor.setServs(allServs);
    return cor;
  }

  /**
   * <p>It checks additionally and books items.</p>
   * @param pReqVars additional request scoped parameters
   * @param pGoods Goods
   * @param pServices services
   * @throws Exception - an exception if incomplete
   **/
  public final void adChekBook(final Map<String, Object> pReqVars,
    final List<CustOrderGdLn> pGoods,
      final List<CustOrderSrvLn> pServices) throws Exception {
    //additional checking:
    String tbn;
    //check availability and booking for same good in different orders:
    List<CustOrderGdLn> gljs = null;
    List<CustOrderGdLn> glrs = null;
    for (CustOrderGdLn gl : pGoods) {
      //join lines with same item:
      for (CustOrderGdLn gl0 : pGoods) {
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
        pGoods.remove(glr);
      }
      tbn = GoodsPlace.class.getSimpleName();
      pReqVars.put(tbn + "itemdeepLevel", 1); //only ID
      pReqVars.put(tbn + "pickUpPlacedeepLevel", 1);
      for (CustOrderGdLn gl : gljs) {
        GoodsPlace gp = getSrvOrm().retrieveEntityWithConditions(pReqVars,
          GoodsPlace.class, "where ITEM=" + gl.getGood().getItsId()
            + " and PICKUPPLACE=" + gl.getUom().getItsId()
              + " and ITSQUANTITY>=" + gl.getQuant());
        if (gp == null) {
          throw new Exception("AC. Good is not available #"
            + gl.getGood().getItsId());
        }
      }
      pReqVars.remove(tbn + "itemdeepLevel");
      pReqVars.remove(tbn + "pickUpPlacedeepLevel");
    }
    //bookable services final-checkout:
    String cond;
    tbn = ServicePlace.class.getSimpleName();
    pReqVars.put(tbn + "itemdeepLevel", 1); //only ID
    pReqVars.put(tbn + "pickUpPlacedeepLevel", 1);
    for (CustOrderSrvLn sl : pServices) {
      cond = "left join (select distinct SERV from SERBUS where SERV="
    + sl.getService().getItsId() + " and FRTM>=" + sl.getDt1().getTime()
  + " and TITM<" + sl.getDt2().getTime()
+ ") as SERBUS on SERBUS.SERV=SERVICEPLACE.ITEM where ITEM=" + sl
  .getService() + " and PLACE=" + sl.getUom().getItsId()
    + " and ITSQUANTITY>0 and SERBUS.SERV is null";
      ServicePlace sp = getSrvOrm()
        .retrieveEntityWithConditions(pReqVars, ServicePlace.class, cond);
      if (sp == null) {
        throw new Exception("AC. Service is not available #"
          + sl.getService().getItsId());
      }
    }
    pReqVars.remove(tbn + "itemdeepLevel");
    pReqVars.remove(tbn + "pickUpPlacedeepLevel");
    //booking:
    //changing availability (booking):
    tbn = GoodsPlace.class.getSimpleName();
    pReqVars.put(tbn + "itemdeepLevel", 1); //only ID
    pReqVars.put(tbn + "pickUpPlacedeepLevel", 1);
    for (CustOrderGdLn gl : pGoods) {
      GoodsPlace gp = getSrvOrm().retrieveEntityWithConditions(pReqVars,
        GoodsPlace.class, "where ITEM=" + gl.getGood().getItsId()
          + " and PICKUPPLACE=" + gl.getUom().getItsId());
      gp.setItsQuantity(gp.getItsQuantity().subtract(gl.getQuant()));
      if (gp.getItsQuantity().compareTo(BigDecimal.ZERO) == -1) {
        throw new Exception("AC. Good is not available #"
          + gl.getGood().getItsId());
      } else {
        if (!gp.getIsAlways()) {
          getSrvOrm().updateEntity(pReqVars, gp);
        }
      }
    }
    pReqVars.remove(tbn + "itemdeepLevel");
    pReqVars.remove(tbn + "pickUpPlacedeepLevel");
    tbn = ServicePlace.class.getSimpleName();
    pReqVars.put(tbn + "itemdeepLevel", 1); //only ID
    pReqVars.put(tbn + "pickUpPlacedeepLevel", 1);
    for (CustOrderSrvLn sl : pServices) {
      if (sl.getDt1() == null) {
        ServicePlace sp = getSrvOrm().retrieveEntityWithConditions(pReqVars,
          ServicePlace.class, "where ITEM=" + sl.getService().getItsId()
            + " and PICKUPPLACE=" + sl.getUom().getItsId());
        sp.setItsQuantity(sp.getItsQuantity().subtract(sl.getQuant()));
        if (sp.getItsQuantity().compareTo(BigDecimal.ZERO) == -1) {
          throw new Exception("NBK service is not available #"
            + sl.getService().getItsId());
        } else {
          if (!sp.getIsAlways()) {
            getSrvOrm().updateEntity(pReqVars, sp);
          }
        }
      }
    }
    pReqVars.remove(tbn + "itemdeepLevel");
    pReqVars.remove(tbn + "pickUpPlacedeepLevel");
    for (CustOrderSrvLn sl : pServices) {
      if (sl.getDt1() != null) {
        SerBus sb = new SerBus();
        sb.setServ(sl.getService());
        sb.setFrTm(sl.getDt1());
        sb.setTiTm(sl.getDt2());
        getSrvOrm().insertEntity(pReqVars, sb);
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
   * <p>Lazy Getter for quOrSrNbChk.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  public final String lazyGetQuOrSrNbChk() throws IOException {
    if (this.quOrSrNbChk == null) {
      String flName = "/webstore/ordSrNbChk.sql";
      this.quOrSrNbChk = loadString(flName);
    }
    return this.quOrSrNbChk;
  }

  /**
   * <p>Lazy Getter for quOrSrBkChk.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  public final String lazyGetQuOrSrBkChk() throws IOException {
    if (this.quOrSrBkChk == null) {
      String flName = "/webstore/ordSrBkChk.sql";
      this.quOrSrBkChk = loadString(flName);
    }
    return this.quOrSrBkChk;
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
   * <p>Geter for logger.</p>
   * @return ILogger
   **/
  public final ILogger getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final void setLogger(final ILogger pLogger) {
    this.logger = pLogger;
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
}
