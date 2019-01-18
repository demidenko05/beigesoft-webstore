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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.beigesoft.model.IRequestData;
import org.beigesoft.model.Page;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.holder.IHolderForClassByName;
import org.beigesoft.converter.IConverterToFromString;
import org.beigesoft.settings.IMngSettings;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvPage;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDate;
import org.beigesoft.webstore.persistable.CuOrSe;
import org.beigesoft.webstore.persistable.CuOrSeTxLn;
import org.beigesoft.webstore.persistable.CuOrSeSrLn;
import org.beigesoft.webstore.persistable.CuOrSeGdLn;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.persistable.CustOrderTxLn;
import org.beigesoft.webstore.persistable.CustOrderSrvLn;
import org.beigesoft.webstore.persistable.CustOrderGdLn;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.service.IBuySr;

/**
 * <p>Service that retrieve buyer's orders to print.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrBuOr<RS> implements IProcessor {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Page service.</p>
   */
  private ISrvPage srvPage;

  /**
   * <p>Processors factory.</p>
   **/
  private IFactoryAppBeansByName<IProcessor> procFac;

  /**
   * <p>Manager UVD settings.</p>
   **/
  private IMngSettings mngUvd;

  /**
   * <p>Date service.</p>
   **/
  private ISrvDate srvDate;

  /**
   * <p>Field converter names holder.</p>
   **/
  private IHolderForClassByName<String> hldFldCnv;

  /**
   * <p>Fields converters factory.</p>
   **/
  private IFactoryAppBeansByName<IConverterToFromString<?>> facFldCnv;

  /**
   * <p>Buyer service.</p>
   **/
  private IBuySr buySr;

  /**
   * <p>Process entity request.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    OnlineBuyer buyer = this.buySr.getAuthBuyr(pRqVs, pRqDt);
    if (buyer == null) {
      String procNm = pRqDt.getParameter("nmPrcRed");
      IProcessor proc = this.procFac.lazyGet(pRqVs, procNm);
      proc.process(pRqVs, pRqDt);
      return;
    }
    String orIdSt = pRqDt.getParameter("orId");
    String sorIdSt = pRqDt.getParameter("sorId");
    if (orIdSt != null || sorIdSt != null) { //print:
     if (orIdSt != null) { //order
        Long orId = Long.valueOf(orIdSt);
        CustOrder or = this.srvOrm.retrieveEntityById(pRqVs,
          CustOrder.class, orId);
        String tbn = CustOrderGdLn.class.getSimpleName();
        pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
        or.setGoods(this.srvOrm.retrieveListWithConditions(
            pRqVs, CustOrderGdLn.class, "where ITSOWNER=" + orId));
        pRqVs.remove(tbn + "itsOwnerdeepLevel");
        tbn = CustOrderSrvLn.class.getSimpleName();
        pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
        or.setServs(this.srvOrm.retrieveListWithConditions(
            pRqVs, CustOrderSrvLn.class, "where ITSOWNER=" + orId));
        pRqVs.remove(tbn + "itsOwnerdeepLevel");
        tbn = CustOrderTxLn.class.getSimpleName();
        pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
        or.setTaxes(this.srvOrm.retrieveListWithConditions(
            pRqVs, CustOrderTxLn.class, "where ITSOWNER=" + orId));
        pRqVs.remove(tbn + "itsOwnerdeepLevel");
        pRqDt.setAttribute("entity", or);
        pRqDt.setAttribute("mngUvds", this.mngUvd);
        pRqDt.setAttribute("srvOrm", this.srvOrm);
        pRqDt.setAttribute("srvDate", this.srvDate);
        pRqDt.setAttribute("hldCnvFtfsNames", this.hldFldCnv);
        pRqDt.setAttribute("fctCnvFtfs", this.facFldCnv);
        Map<Class<?>, List<?>> olm = new LinkedHashMap<Class<?>, List<?>>();
        pRqDt.setAttribute("ownedListsMap", olm);
        olm.put(CustOrderGdLn.class, or.getGoods());
        olm.put(CustOrderSrvLn.class, or.getServs());
        olm.put(CustOrderTxLn.class, or.getTaxes());
      } else { //S.E. order:
        Long orId = Long.valueOf(sorIdSt);
        CuOrSe or = this.srvOrm.retrieveEntityById(pRqVs,
          CuOrSe.class, orId);
        String tbn = CuOrSeGdLn.class.getSimpleName();
        pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
        or.setGoods(this.srvOrm.retrieveListWithConditions(
            pRqVs, CuOrSeGdLn.class, "where ITSOWNER=" + orId));
        pRqVs.remove(tbn + "itsOwnerdeepLevel");
        tbn = CuOrSeSrLn.class.getSimpleName();
        pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
        or.setServs(this.srvOrm.retrieveListWithConditions(
            pRqVs, CuOrSeSrLn.class, "where ITSOWNER=" + orId));
        pRqVs.remove(tbn + "itsOwnerdeepLevel");
        tbn = CuOrSeTxLn.class.getSimpleName();
        pRqVs.put(tbn + "itsOwnerdeepLevel", 1);
        or.setTaxes(this.srvOrm.retrieveListWithConditions(
            pRqVs, CuOrSeTxLn.class, "where ITSOWNER=" + orId));
        pRqVs.remove(tbn + "itsOwnerdeepLevel");
        pRqDt.setAttribute("entity", or);
        pRqDt.setAttribute("mngUvds", this.mngUvd);
        pRqDt.setAttribute("srvOrm", this.srvOrm);
        pRqDt.setAttribute("srvDate", this.srvDate);
        pRqDt.setAttribute("hldCnvFtfsNames", this.hldFldCnv);
        pRqDt.setAttribute("fctCnvFtfs", this.facFldCnv);
        Map<Class<?>, List<?>> olm = new LinkedHashMap<Class<?>, List<?>>();
        pRqDt.setAttribute("ownedListsMap", olm);
        olm.put(CuOrSeGdLn.class, or.getGoods());
        olm.put(CuOrSeSrLn.class, or.getServs());
        olm.put(CuOrSeTxLn.class, or.getTaxes());
      }
    } else { //page:
      page(pRqVs, pRqDt, buyer);
    }
  }

  /**
   * <p>Retrieve page.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @param pBuyr buyer
   * @throws Exception - an exception
   **/
  public final void page(final Map<String, Object> pRqVs,
    final IRequestData pRqDt, final OnlineBuyer pBuyr) throws Exception {
    TradingSettings ts = (TradingSettings) pRqVs.get("tradSet");
    //orders:
    int page;
    String pgSt = pRqDt.getParameter("pg");
    if (pgSt != null) {
      page = Integer.parseInt(pgSt);
    } else {
      page = 1;
    }
    String wheBr = "BUYER=" + pBuyr.getItsId();
    Integer rowCount = this.srvOrm.evalRowCountWhere(pRqVs,
      CustOrder.class, wheBr);
    Integer itemsPerPage = ts.getItemsPerPage();
    int totalPages = this.srvPage.evalPageCount(rowCount, itemsPerPage);
    if (page > totalPages) {
      page = totalPages;
    }
    int firstResult = (page - 1) * itemsPerPage; //0-20,20-40
    Integer paginationTail = Integer.valueOf(mngUvd
      .getAppSettings().get("paginationTail"));
    List<Page> pages = this.srvPage.evalPages(1, totalPages,
      paginationTail);
    pRqDt.setAttribute("pgs", pages);
    String tbn = CustOrder.class.getSimpleName();
    Set<String> ndFlNm = new HashSet<String>();
    ndFlNm.add("itsId");
    ndFlNm.add("itsName");
    pRqVs.put("PickUpPlaceneededFields", ndFlNm);
    pRqVs.put(tbn + "buyerdeepLevel", 1);
    List<CustOrder> orders = getSrvOrm().retrievePageWithConditions(pRqVs,
      CustOrder.class, "where " + wheBr, firstResult, itemsPerPage);
    pRqVs.remove(tbn + "buyerdeepLevel");
    pRqDt.setAttribute("ords", orders);
    //S.E. orders:
    pgSt = pRqDt.getParameter("spg");
    if (pgSt != null) {
      page = Integer.parseInt(pgSt);
    } else {
      page = 1;
    }
    rowCount = this.srvOrm.evalRowCountWhere(pRqVs, CuOrSe.class, wheBr);
    itemsPerPage = ts.getItemsPerPage();
    totalPages = this.srvPage.evalPageCount(rowCount, itemsPerPage);
    if (page > totalPages) {
      page = totalPages;
    }
    firstResult = (page - 1) * itemsPerPage; //0-20,20-40
    pages = this.srvPage.evalPages(1, totalPages, paginationTail);
    pRqDt.setAttribute("spgs", pages);
    tbn = CuOrSe.class.getSimpleName();
    Set<String> ndFlDc = new HashSet<String>();
    ndFlDc.add("seller");
    pRqVs.put("DebtorCreditorneededFields", ndFlNm);
    pRqVs.put("SeSellerneededFields", ndFlDc);
    pRqVs.put("SeSellersellerdeepLevel", 2);
    pRqVs.put(tbn + "buyerdeepLevel", 1);
    List<CuOrSe> sorders = getSrvOrm().retrievePageWithConditions(pRqVs,
      CuOrSe.class, "where " + wheBr, firstResult, itemsPerPage);
    pRqVs.remove(tbn + "buyerdeepLevel");
    pRqVs.remove("DebtorCreditorneededFields");
    pRqVs.remove("SeSellerneededFields");
    pRqVs.remove("SeSellersellerdeepLevel");
    pRqVs.remove("PickUpPlaceneededFields");
    pRqDt.setAttribute("sords", sorders);
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
   * <p>Getter for srvPage.</p>
   * @return ISrvPage
   **/
  public final ISrvPage getSrvPage() {
    return this.srvPage;
  }

  /**
   * <p>Setter for srvPage.</p>
   * @param pSrvPage reference
   **/
  public final void setSrvPage(final ISrvPage pSrvPage) {
    this.srvPage = pSrvPage;
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

  /**
   * <p>Getter for mngUvd.</p>
   * @return IMngSettings
   **/
  public final IMngSettings getMngUvd() {
    return this.mngUvd;
  }

  /**
   * <p>Setter for mngUvd.</p>
   * @param pMngUvd reference
   **/
  public final void setMngUvd(final IMngSettings pMngUvd) {
    this.mngUvd = pMngUvd;
  }

  /**
   * <p>Getter for srvDate.</p>
   * @return ISrvDate
   **/
  public final ISrvDate getSrvDate() {
    return this.srvDate;
  }

  /**
   * <p>Setter for srvDate.</p>
   * @param pSrvDate reference
   **/
  public final void setSrvDate(final ISrvDate pSrvDate) {
    this.srvDate = pSrvDate;
  }

  /**
   * <p>Getter for hldFldCnv.</p>
   * @return IHolderForClassByName<String>
   **/
  public final IHolderForClassByName<String> getHldFldCnv() {
    return this.hldFldCnv;
  }

  /**
   * <p>Setter for hldFldCnv.</p>
   * @param pHldFldCnv reference
   **/
  public final void setHldFldCnv(
    final IHolderForClassByName<String> pHldFldCnv) {
    this.hldFldCnv = pHldFldCnv;
  }

  /**
   * <p>Getter for facFldCnv.</p>
   * @return IFactoryAppBeansByName<IConverterToFromString<?>>
   **/
  public final IFactoryAppBeansByName<IConverterToFromString<?>>
    getFacFldCnv() {
    return this.facFldCnv;
  }

  /**
   * <p>Setter for facFldCnv.</p>
   * @param pFacFldCnv reference
   **/
  public final void setFacFldCnv(
    final IFactoryAppBeansByName<IConverterToFromString<?>> pFacFldCnv) {
    this.facFldCnv = pFacFldCnv;
  }

  /**
   * <p>Getter for buySr.</p>
   * @return IBuySr
   **/
  public final IBuySr getBuySr() {
    return this.buySr;
  }

  /**
   * <p>Setter for buySr.</p>
   * @param pBuySr reference
   **/
  public final void setBuySr(final IBuySr pBuySr) {
    this.buySr = pBuySr;
  }
}
