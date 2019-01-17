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
import java.util.Map;
import java.util.Date;

import org.beigesoft.log.ILogger;
import org.beigesoft.model.ColumnsValues;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.webstore.model.EOrdStat;
import org.beigesoft.webstore.model.Purch;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.SeGoodsPlace;
import org.beigesoft.webstore.persistable.GoodsPlace;
import org.beigesoft.webstore.persistable.ServicePlace;
import org.beigesoft.webstore.persistable.SeServicePlace;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.persistable.CustOrderGdLn;
import org.beigesoft.webstore.persistable.CustOrderSrvLn;
import org.beigesoft.webstore.persistable.SerBus;
import org.beigesoft.webstore.persistable.SeSerBus;
import org.beigesoft.webstore.persistable.CuOrSe;
import org.beigesoft.webstore.persistable.CuOrSeGdLn;
import org.beigesoft.webstore.persistable.CuOrSeSrLn;

/**
 * <p>It cancels all given buyer's orders.
 * E.g. buyer has not paid online after accepting (booking) orders.
 * It changes item's availability and orders status to given NEW or CANCELED.
 * </p>
 *
 * @param <RS> platform dependent RDBMS recordset
 * @author Yury Demidenko
 */
public class CncOrd<RS> implements ICncOrd {

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
   * <p>It cancels all given buyer's orders.
   * For example buyer had not paid online after accepting (booking) orders.
   * It changes item's availability and orders status to given NEW or CANCELED.
   * </p>
   * @param pRqVs additional request scoped parameters
   * @param pPurch orders
   * @param pStat NEW or CANCELED
   * @throws Exception - an exception
   **/
  @Override
  public final void cancel(final Map<String, Object> pRqVs,
    final Purch pPurch, final EOrdStat pStat) throws Exception {
    throw new Exception("NEI");
  }


  /**
   * <p>It cancels all given buyer's orders.
   * For example buyer had not paid online after accepting (booking) orders.
   * It changes item's availability and orders status to given NEW or CANCELED.
   * </p>
   * @param pRqVs additional request scoped parameters
   * @param pBuyr buyer
   * @param pStFr usually BOOKED
   * @param pStTo usually NEW
   * @throws Exception - an exception
   **/
  @Override
  public final void cancel(final Map<String, Object> pRqVs,
    final OnlineBuyer pBuyr, final EOrdStat pStFr,
      final EOrdStat pStTo) throws Exception {
    throw new Exception("NEI");
  }

  /**
   * <p>It cancels all given buyer's orders.
   * For example buyer had not paid online after accepting (booking) orders.
   * It changes item's availability and orders status to given NEW or CANCELED.
   * </p>
   * @param pRqVs additional request scoped parameters
   * @param pBuyr buyer
   * @param pPurId purchase ID
   * @param pStFr usually BOOKED
   * @param pStTo usually NEW
   * @throws Exception - an exception
   **/
  @Override
  public final void cancel(final Map<String, Object> pRqVs,
    final OnlineBuyer pBuyr, final Long pPurId,
      final EOrdStat pStFr, final EOrdStat pStTo) throws Exception {
    throw new Exception("NEI");
  }

  /**
   * <p>It cancels given buyer's order.</p>
   * @param pRqVs additional request scoped parameters
   * @param pCuOr order
   * @throws Exception - an exception
   **/
  @Override
  public final void cancel(final Map<String, Object> pRqVs,
    final CustOrder pCuOr) throws Exception {
    Set<String> ndFl = new HashSet<String>();
    String tbn = CustOrderGdLn.class.getSimpleName();
    ndFl.add("itsId");
    ndFl.add("quant");
    ndFl.add("good");
    pRqVs.put(tbn + "neededFields", ndFl);
    pRqVs.put(tbn + "gooddeepLevel", 1);
    List<CustOrderGdLn> gds = this.srvOrm.retrieveListWithConditions(
      pRqVs, CustOrderGdLn.class, "where ITSOWNER=" + pCuOr.getItsId());
    pRqVs.remove(tbn + "neededFields");
    pRqVs.remove(tbn + "gooddeepLevel");
    ColumnsValues cvsIil = new ColumnsValues();
    cvsIil.getFormula().add("availableQuantity");
    for (CustOrderGdLn gl : gds) {
      GoodsPlace gp = getSrvOrm().retrieveEntityWithConditions(pRqVs,
        GoodsPlace.class, "where ISALWAYS=0 and ITEM=" + gl.getGood().getItsId()
          + " and PICKUPPLACE=" + pCuOr.getPlace().getItsId());
      if (gp != null) {
        gp.setItsQuantity(gp.getItsQuantity().add(gl.getQuant()));
        getSrvOrm().updateEntity(pRqVs, gp);
        cvsIil.put("itsVersion", new Date().getTime());
        cvsIil.put("availableQuantity", "AVAILABLEQUANTITY+" + gl.getQuant());
        this.srvDb.executeUpdate("ITEMINLIST", cvsIil,
          "ITSTYPE=0 and ITEMID=" + gp.getItem().getItsId());
      }
    }
    tbn = CustOrderSrvLn.class.getSimpleName();
    ndFl.remove("good");
    ndFl.add("service");
    ndFl.add("dt1");
    ndFl.add("dt2");
    pRqVs.put(tbn + "neededFields", ndFl);
    pRqVs.put(tbn + "gooddeepLevel", 1);
    List<CustOrderSrvLn> sls = this.srvOrm.retrieveListWithConditions(
      pRqVs, CustOrderSrvLn.class, "where ITSOWNER=" + pCuOr.getItsId());
    pRqVs.remove(tbn + "neededFields");
    pRqVs.remove(tbn + "gooddeepLevel");
    for (CustOrderSrvLn sl : sls) {
      if (sl.getDt1() == null) { //non-bookable:
        ServicePlace sp = getSrvOrm().retrieveEntityWithConditions(pRqVs,
          ServicePlace.class, "where ISALWAYS=0 and ITEM=" + sl.getService()
            .getItsId() + " and PICKUPPLACE=" + pCuOr.getPlace().getItsId());
        if (sp != null) {
          sp.setItsQuantity(sp.getItsQuantity().add(sl.getQuant()));
          getSrvOrm().updateEntity(pRqVs, sp);
          cvsIil.put("itsVersion", new Date().getTime());
          cvsIil.put("availableQuantity", "AVAILABLEQUANTITY+" + sl.getQuant());
          this.srvDb.executeUpdate("ITEMINLIST", cvsIil,
            "ITSTYPE=1 and ITEMID=" + sp.getItem().getItsId());
        }
      } else { //bookable:
        List<SerBus> sebs = getSrvOrm().retrieveListWithConditions(pRqVs,
      SerBus.class, "where FRE=0 and SERV=" + sl.getService().getItsId()
+ " and FRTM=" + sl.getDt1().getTime() + " and TITM=" + sl.getDt1().getTime());
        if (sebs.size() == 1) {
          sebs.get(0).setFre(true);
          getSrvOrm().updateEntity(pRqVs, sebs.get(0));
        } else if (sebs.size() > 1) {
          this.log.error(pRqVs, CncOrd.class,
        "Several SERBUS for booked service: " + sl.getService().getItsId()
      + "/"  + sl.getDt1().getTime() + "/" + sl.getDt1().getTime());
          for (SerBus seb : sebs) {
            seb.setFre(true);
            getSrvOrm().updateEntity(pRqVs, seb);
          }
        } else {
          this.log.error(pRqVs, CncOrd.class,
        "There is no SERBUS for booked service: " + sl.getService().getItsId()
      + "/"  + sl.getDt1().getTime() + "/" + sl.getDt1().getTime());
        }
      }
    }
    pCuOr.setStat(EOrdStat.CANCELED);
    getSrvOrm().updateEntity(pRqVs, pCuOr);
  }

  /**
   * <p>It cancels given buyer's S.E.order.</p>
   * @param pRqVs additional request scoped parameters
   * @param pCuOr order
   * @throws Exception - an exception
   **/
  @Override
  public final void cancel(final Map<String, Object> pRqVs,
    final CuOrSe pCuOr) throws Exception {
    Set<String> ndFl = new HashSet<String>();
    String tbn = CuOrSeGdLn.class.getSimpleName();
    ndFl.add("itsId");
    ndFl.add("quant");
    ndFl.add("good");
    pRqVs.put(tbn + "neededFields", ndFl);
    pRqVs.put(tbn + "gooddeepLevel", 1);
    List<CuOrSeGdLn> gds = this.srvOrm.retrieveListWithConditions(
      pRqVs, CuOrSeGdLn.class, "where ITSOWNER=" + pCuOr.getItsId());
    pRqVs.remove(tbn + "neededFields");
    pRqVs.remove(tbn + "gooddeepLevel");
    ColumnsValues cvsIil = new ColumnsValues();
    cvsIil.getFormula().add("availableQuantity");
    for (CuOrSeGdLn gl : gds) {
      SeGoodsPlace gp = getSrvOrm().retrieveEntityWithConditions(pRqVs,
        SeGoodsPlace.class, "where ISALWAYS=0 and ITEM=" + gl.getGood()
          .getItsId() + " and PICKUPPLACE=" + pCuOr.getPlace().getItsId());
      if (gp != null) {
        gp.setItsQuantity(gp.getItsQuantity().add(gl.getQuant()));
        getSrvOrm().updateEntity(pRqVs, gp);
        cvsIil.put("itsVersion", new Date().getTime());
        cvsIil.put("availableQuantity", "AVAILABLEQUANTITY+" + gl.getQuant());
        this.srvDb.executeUpdate("ITEMINLIST", cvsIil,
          "ITSTYPE=2 and ITEMID=" + gp.getItem().getItsId());
      }
    }
    tbn = CuOrSeSrLn.class.getSimpleName();
    ndFl.remove("good");
    ndFl.add("service");
    ndFl.add("dt1");
    ndFl.add("dt2");
    pRqVs.put(tbn + "neededFields", ndFl);
    pRqVs.put(tbn + "gooddeepLevel", 1);
    List<CuOrSeSrLn> sls = this.srvOrm.retrieveListWithConditions(
      pRqVs, CuOrSeSrLn.class, "where ITSOWNER=" + pCuOr.getItsId());
    pRqVs.remove(tbn + "neededFields");
    pRqVs.remove(tbn + "gooddeepLevel");
    for (CuOrSeSrLn sl : sls) {
      if (sl.getDt1() == null) { //non-bookable:
        SeServicePlace sp = getSrvOrm().retrieveEntityWithConditions(pRqVs,
          SeServicePlace.class, "where ISALWAYS=0 and ITEM=" + sl.getService()
            .getItsId() + " and PICKUPPLACE=" + pCuOr.getPlace().getItsId());
        if (sp != null) {
          sp.setItsQuantity(sp.getItsQuantity().add(sl.getQuant()));
          getSrvOrm().updateEntity(pRqVs, sp);
          cvsIil.put("itsVersion", new Date().getTime());
          cvsIil.put("availableQuantity", "AVAILABLEQUANTITY+" + sl.getQuant());
          this.srvDb.executeUpdate("ITEMINLIST", cvsIil,
            "ITSTYPE=3 and ITEMID=" + sp.getItem().getItsId());
        }
      } else { //bookable:
        List<SeSerBus> sebs = getSrvOrm().retrieveListWithConditions(pRqVs,
      SeSerBus.class, "where FRE=0 and SERV=" + sl.getService().getItsId()
+ " and FRTM=" + sl.getDt1().getTime() + " and TITM=" + sl.getDt1().getTime());
        if (sebs.size() == 1) {
          sebs.get(0).setFre(true);
          getSrvOrm().updateEntity(pRqVs, sebs.get(0));
        } else if (sebs.size() > 1) {
          this.log.error(pRqVs, CncOrd.class,
        "Several SESERBUS for booked SeService: " + sl.getService().getItsId()
      + "/"  + sl.getDt1().getTime() + "/" + sl.getDt1().getTime());
          for (SeSerBus seb : sebs) {
            seb.setFre(true);
            getSrvOrm().updateEntity(pRqVs, seb);
          }
        } else {
          this.log.error(pRqVs, CncOrd.class,
      "There is no SESERBUS for booked SeService: " + sl.getService().getItsId()
    + "/"  + sl.getDt1().getTime() + "/" + sl.getDt1().getTime());
        }
      }
    }
    pCuOr.setStat(EOrdStat.CANCELED);
    getSrvOrm().updateEntity(pRqVs, pCuOr);
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
}
