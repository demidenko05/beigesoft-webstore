package org.beigesoft.webstore.processor;

/*
 * Copyright (c) 2017 Beigesoft ™
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

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvEntitiesPage;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.webstore.persistable.GoodsCatalogs;
import org.beigesoft.webstore.persistable.CatalogGs;
import org.beigesoft.webstore.persistable.TradingSettings;

/**
 * <p>Service that add/remove filtered goods to/from chosen catalog.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcAssignGoodsToCatalog<RS> implements IProcessor {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Page service.</p>
   **/
  private ISrvEntitiesPage srvEntitiesPage;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pAddParam,
    final IRequestData pRequestData) throws Exception {
    Set<String> filterAppearance = new HashSet<String>();
    pAddParam.put("filterAppearance", filterAppearance);
    this.srvEntitiesPage
      .revealPageFilterData(pAddParam, pRequestData, InvItem.class);
    StringBuffer sbWhere = (StringBuffer) pAddParam.get("sbWhere");
    if (sbWhere.length() == 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "filter_must_be_not_empty");
    }
    String goodsCatalogAction = pRequestData
      .getParameter("goodsCatalogAction");
    if (!("add".equals(goodsCatalogAction)
      || "remove".equals(goodsCatalogAction))) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "wrong_action");
    }
    Long catalogId = Long.valueOf(pRequestData
      .getParameter(CatalogGs.class.getSimpleName() + ".itsId"));
    if (catalogId == null) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "catalog_must_be_not_empty");
    }
    CatalogGs catalogOfGoods = this.srvOrm
      .retrieveEntityById(pAddParam, CatalogGs.class, catalogId);
    if (catalogOfGoods == null) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "catalog_must_be_not_empty");
    }
    if (catalogOfGoods.getHasSubcatalogs()) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "catalog_must_not_has_subcatalog");
    }
    String whereStr = sbWhere.toString();
    Integer rowCount = this.srvOrm.evalRowCountWhere(pAddParam, InvItem.class,
        whereStr);
    TradingSettings ts = (TradingSettings) pAddParam.get("tradingSettings");
    if (rowCount > ts.getMaxQuantityOfBulkItems()) {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "filtered_list_size_exceed_max_bulk");
    }
    Integer totalItems = Integer
      .valueOf(pRequestData.getParameter("totalItems"));
    if (!rowCount.equals(totalItems)) {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "filtered_list_has_changed");
    }
    List<InvItem> goodsList = this.srvOrm.retrieveListWithConditions(pAddParam,
      InvItem.class, "where " + whereStr);
    for (InvItem goods : goodsList) {
      GoodsCatalogs gc = new GoodsCatalogs();
      gc.setItsCatalog(catalogOfGoods);
      gc.setGoods(goods);
      if ("add".equals(goodsCatalogAction)) {
        // add goods to catalog
        this.srvOrm.insertEntity(pAddParam, gc);
      } else {
        // remove goods from catalog
        this.srvOrm.deleteEntity(pAddParam, gc);
      }
    }
    pRequestData.setAttribute("goodsCatalogAction", goodsCatalogAction);
    pRequestData.setAttribute("filterAppearance", filterAppearance);
    pRequestData.setAttribute("totalItems", totalItems);
    pRequestData.setAttribute("goodsList", goodsList);
    pRequestData.setAttribute("catalogOfGoods", catalogOfGoods);
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvOrm.</p>
   * @return ASrvOrm<RS>
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
   * <p>Getter for srvEntitiesPage.</p>
   * @return ISrvEntitiesPage
   **/
  public final ISrvEntitiesPage getSrvEntitiesPage() {
    return this.srvEntitiesPage;
  }

  /**
   * <p>Setter for srvEntitiesPage.</p>
   * @param pSrvEntitiesPage reference
   **/
  public final void setSrvEntitiesPage(
    final ISrvEntitiesPage pSrvEntitiesPage) {
    this.srvEntitiesPage = pSrvEntitiesPage;
  }
}
