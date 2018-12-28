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
import org.beigesoft.log.ILogger;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
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

/**
 * <p>It accepts all buyer's orders in single transaction.
 * It changes item's availability and orders status to PENDING.
 * If any item is unavailable, then that transaction will be rolled back,
 * and it returns result NULL. And so does if there are several payees for
 * online payment. Request handler must be non-transactional,
 * i.e. it mustn't be started transaction.</p>
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
   * <p>Database service.</p>
   */
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   */
  private ISrvOrm<RS> srvOrm;

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
   * <p>It accepts all buyer's orders in single transaction.
   * It changes item's availability and orders status to PENDING.
   * If any item is unavailable, then that transaction will be rolled back,
   * and it returns result NULL. And so does if there are several payees for
   * online payment. Request handler must be non-transactional,
   * i.e. it mustn't be started transaction.</p>
   * @param pReqVars additional request scoped parameters
   * @param pReqDt Request Data
   * @param pBur Buyer
   * @return list of accepted orders or NULL
   * @throws Exception - an exception
   **/
  @Override
  public final Purch accept(final Map<String, Object> pReqVars,
    final IRequestData pReqDt, final OnlineBuyer pBur) throws Exception {
    Purch rez = null;
    List<CustOrder> ords = null;
    List<CuOrSe> sords = null;
    try {
      this.srvDatabase.setIsAutocommit(false);
      this.srvDatabase.setTransactionIsolation(ISrvDatabase
        .TRANSACTION_READ_COMMITTED);
      this.srvDatabase.beginTransaction();
      String tbn = CustOrder.class.getSimpleName();
      pReqVars.put(tbn + "buyerdeepLevel", 1);
      pReqVars.put(tbn + "placedeepLevel", 1);
      pReqVars.put(tbn + "currdeepLevel", 1);
      ords = this.srvOrm.retrieveListWithConditions(pReqVars,
        CustOrder.class, "where STAT=0 and BUYER=" + pBur.getItsId());
      pReqVars.remove(tbn + "buyerdeepLevel");
      pReqVars.remove(tbn + "placedeepLevel");
      pReqVars.remove(tbn + "currdeepLevel");
      tbn = CuOrSe.class.getSimpleName();
      pReqVars.put(tbn + "seldeepLevel", 1);
      pReqVars.put(tbn + "buyerdeepLevel", 1);
      pReqVars.put(tbn + "placedeepLevel", 1);
      pReqVars.put(tbn + "currdeepLevel", 1);
      sords = this.srvOrm.retrieveListWithConditions(pReqVars,
        CuOrSe.class, "where STAT=0 and BUYER=" + pBur.getItsId());
      pReqVars.remove(tbn + "seldeepLevel");
      pReqVars.remove(tbn + "buyerdeepLevel");
      pReqVars.remove(tbn + "placedeepLevel");
      pReqVars.remove(tbn + "currdeepLevel");
      boolean isComplete = true;
      //checking for several online payees:
      if (sords.size() > 0) {
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
              isComplete = false;
              break;
            } else if (selOnl == null) {
              selOnl = co.getSel();
            } else if (!selOnl.getItsId().getItsId()
              .equals(co.getSel().getItsId().getItsId())) {
              isComplete = false;
              break;
            }
          }
        }
      }
      if (isComplete) {
        //consolidated order with bookable items for farther booking:
        CustOrder cor = null;
        if (ords.size() > 0) {
          cor = check1(pReqVars, ords);
          if (cor == null) {
            isComplete = false;
          }
        }
        if (isComplete) {
          isComplete = adChekBook(pReqVars, cor.getGoods(), cor.getServs());
        }
      }
      if (isComplete && ords.size() > 0) {
        //change orders status:
        for (CustOrder co : ords) {
          co.setStat(EOrdStat.BOOKED);
          getSrvOrm().updateEntity(pReqVars, co);
        }
      }
      if (isComplete) {
        this.srvDatabase.commitTransaction();
        rez = new Purch();
        if (ords.size() > 0) {
          rez.setOrds(ords);
        }
        if (sords.size() > 0) {
          rez.setSords(sords);
        }
      } else {
        this.srvDatabase.rollBackTransaction();
      }
    } catch (Exception ex) {
      if (!this.srvDatabase.getIsAutocommit()) {
        this.srvDatabase.rollBackTransaction();
      }
      throw ex;
    } finally {
      this.srvDatabase.releaseResources();
    }
    return rez;
  }

  //utils:
  /**
   * <p>It half-checks items.</p>
   * @param pReqVars additional request scoped parameters
   * @param pOrds orders
   * @return consolidated order with items or NULL if checking fail
   * @throws Exception - an exception
   **/
  public final CustOrder check1(final Map<String, Object> pReqVars,
    final List<CustOrder> pOrds) throws Exception {
    Map<Long, StringBuffer> plOrIds = new HashMap<Long, StringBuffer>();
    for (CustOrder co : pOrds) {
      StringBuffer ordIds = plOrIds.get(co.getPlace().getItsId());
      if (ordIds == null) {
        ordIds = new StringBuffer();
        plOrIds.put(co.getPlace().getItsId(), ordIds);
        ordIds.append(co.getItsId().toString());
      } else {
        ordIds.append("," + co.getItsId());
      }
    }
    Set<String> ndFl = new HashSet<String>();
    ndFl.add("itsId");
    ndFl.add("quant");
    ndFl.add("good");
    String tbn = CustOrderGdLn.class.getSimpleName();
    pReqVars.put(tbn + "neededFields", ndFl);
    pReqVars.put(tbn + "gooddeepLevel", 1);
    boolean isComplete = true;
    List<CustOrderGdLn> allGoods = new ArrayList<CustOrderGdLn>();
    for (Map.Entry<Long, StringBuffer> ent : plOrIds.entrySet()) {
      String quer = lazyGetQuOrGdChk().replace(":ORIDS", ent.getValue()
        .toString()).replace(":PLACE", ent.getKey().toString());
      List<CustOrderGdLn> allGds = this.srvOrm.retrieveListByQuery(
        pReqVars, CustOrderGdLn.class, quer);
      for (CustOrderGdLn gl : allGds) {
        //UOM holds place ID for additional checking and booking:
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setItsId(ent.getKey());
        gl.setUom(uom);
        if (gl.getQuant().compareTo(BigDecimal.ZERO) == 0) {
          isComplete = false;
          break;
        }
      }
      if (isComplete) {
        allGoods.addAll(allGds);
      }
    }
    pReqVars.remove(tbn + "gooddeepLevel");
    pReqVars.remove(tbn + "neededFields");
    if (isComplete) {
      ndFl.remove("good");
      ndFl.add("service");
      tbn = CustOrderSrvLn.class.getSimpleName();
      pReqVars.put(tbn + "neededFields", ndFl);
      pReqVars.put(tbn + "servicedeepLevel", 1);
      for (Map.Entry<Long, StringBuffer> ent : plOrIds.entrySet()) {
        String quer = lazyGetQuOrSrNbChk().replace(":ORIDS", ent.getValue()
          .toString()).replace(":PLACE", ent.getKey().toString());
        List<CustOrderSrvLn> allSrvs = this.srvOrm.retrieveListByQuery(
          pReqVars, CustOrderSrvLn.class, quer);
        for (CustOrderSrvLn sl : allSrvs) {
          if (sl.getQuant().compareTo(BigDecimal.ZERO) == 0) {
            isComplete = false;
            break;
          }
        }
      }
      if (!isComplete) {
        pReqVars.remove(tbn + "servicedeepLevel");
        pReqVars.remove(tbn + "neededFields");
      }
    }
    List<CustOrderSrvLn> allBkSrvs = new ArrayList<CustOrderSrvLn>();
    if (isComplete) {
      //bookable services half-checkout:
      ndFl.add("dt1");
      ndFl.add("dt2");
      for (Map.Entry<Long, StringBuffer> ent : plOrIds.entrySet()) {
        String quer = lazyGetQuOrSrBkChk().replace(":ORIDS", ent.getValue()
          .toString()).replace(":PLACE", ent.getKey().toString());
        List<CustOrderSrvLn> allSrvs = this.srvOrm.retrieveListByQuery(
          pReqVars, CustOrderSrvLn.class, quer);
        for (CustOrderSrvLn sl : allSrvs) {
          //UOM holds place ID for final checkout and booking:
          UnitOfMeasure uom = new UnitOfMeasure();
          uom.setItsId(ent.getKey());
          sl.setUom(uom);
          if (sl.getQuant().compareTo(BigDecimal.ZERO) == 0) {
            isComplete = false;
            break;
          }
        }
        if (isComplete) {
          allBkSrvs.addAll(allSrvs);
        } else {
          break;
        }
      }
      pReqVars.remove(tbn + "servicedeepLevel");
      pReqVars.remove(tbn + "neededFields");
    }
    CustOrder cor = null;
    if (isComplete) {
      cor = new CustOrder();
      cor.setGoods(allGoods);
      cor.setServs(allBkSrvs);
    }
    return cor;
  }

  /**
   * <p>It checks additionally and books items.</p>
   * @param pReqVars additional request scoped parameters
   * @param pGoods Goods
   * @param pBkServs bookable services
   * @return if complete
   * @throws Exception - an exception
   **/
  public final boolean adChekBook(final Map<String, Object> pReqVars,
    final List<CustOrderGdLn> pGoods,
      final List<CustOrderSrvLn> pBkServs) throws Exception {
    //additional checking:
    boolean isComplete = true;
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
            + " and PLACE=" + gl.getUom().getItsId() + " and ITSQUANTITY>="
              + gl.getQuant());
        if (gp == null) {
            isComplete = false;
            break;
        }
      }
      pReqVars.remove(tbn + "itemdeepLevel");
      pReqVars.remove(tbn + "pickUpPlacedeepLevel");
    }
    if (isComplete) {
      //bookable services final-checkout:
      String cond;
      tbn = ServicePlace.class.getSimpleName();
      pReqVars.put(tbn + "itemdeepLevel", 1); //only ID
      pReqVars.put(tbn + "pickUpPlacedeepLevel", 1);
      for (CustOrderSrvLn sl : pBkServs) {
        cond = "left join (select distinct SERV from SERBUS where SERV="
      + sl.getService().getItsId() + " and FRTM>=" + sl.getDt1().getTime()
    + " and TITM<" + sl.getDt2().getTime()
  + ") as SERBUS on SERBUS.SERV=SERVICEPLACE.ITEM where ITEM=" + sl
.getService() + " and PLACE=" + sl.getUom().getItsId()
  + " and ITSQUANTITY>0 and SERBUS.SERV is null";
        ServicePlace sp = getSrvOrm()
          .retrieveEntityWithConditions(pReqVars, ServicePlace.class, cond);
        if (sp == null) {
            isComplete = false;
            break;
        }
      }
      pReqVars.remove(tbn + "itemdeepLevel");
      pReqVars.remove(tbn + "pickUpPlacedeepLevel");
    }
    //booking:
    if (isComplete) {
      //changing availability (booking):
      tbn = GoodsPlace.class.getSimpleName();
      pReqVars.put(tbn + "itemdeepLevel", 1); //only ID
      pReqVars.put(tbn + "pickUpPlacedeepLevel", 1);
      for (CustOrderGdLn gl : pGoods) {
        GoodsPlace gp = getSrvOrm().retrieveEntityWithConditions(pReqVars,
          GoodsPlace.class, "where ITEM=" + gl.getGood().getItsId()
            + " and PLACE=" + gl.getUom().getItsId());
        gp.setItsQuantity(gp.getItsQuantity().subtract(gl.getQuant()));
        if (gp.getItsQuantity().compareTo(BigDecimal.ZERO) == -1) {
          isComplete = false;
          break;
        } else {
          getSrvOrm().updateEntity(pReqVars, gp);
        }
      }
      pReqVars.remove(tbn + "itemdeepLevel");
      pReqVars.remove(tbn + "pickUpPlacedeepLevel");
    }
    if (isComplete) {
      for (CustOrderSrvLn sl : pBkServs) {
        SerBus sb = new SerBus();
        sb.setFrTm(sl.getDt1());
        sb.setTiTm(sl.getDt2());
        getSrvOrm().insertEntity(pReqVars, sb);
      }
    }
    return isComplete;
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
   * <p>Getter for srvDatabase.</p>
   * @return ISrvDatabase<RS>
   **/
  public final ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final void setSrvDatabase(
    final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
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
}
