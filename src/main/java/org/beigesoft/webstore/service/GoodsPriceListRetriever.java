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

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Collections;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.comparator.CmprHasIdLong;
import org.beigesoft.model.Node;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.service.ICsvDataRetriever;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.accounting.model.WarehouseRestLineSm;
import org.beigesoft.accounting.model.TaxWr;
import org.beigesoft.accounting.model.TaxCategoryWr;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.persistable.WarehouseSite;
import org.beigesoft.webstore.persistable.PriceGoods;

/**
 * <p>Goods Price List Retriever.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class GoodsPriceListRetriever<RS> implements ICsvDataRetriever {

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Retrieves CSV data.
   * pReqVars must has:
   * <pre>
   *  priceCategoryId - Long
   * </pre>
   * pReqVars might has:
   * <pre>
   *  optimisticQuantity - BigDecimal
   *  unavailablePrice - BigDecimal
   * </pre>
   * </p>
   * @param pReqVars request parameters,
   * @return data table
   * @throws Exception an Exception
   **/
  @Override
  public final List<List<Object>> retrieveData(
    final Map<String, Object> pReqVars) throws Exception {
    AccSettings as = this.srvAccSettings.lazyGetAccSettings(pReqVars);
    List<List<Object>> result = new ArrayList<List<Object>>();
    Long priceCategoryId = (Long) pReqVars.get("priceCategoryId");
    BigDecimal unavailablePrice = null;
    if (pReqVars.get("unavailablePrice") != null) {
      unavailablePrice = (BigDecimal) pReqVars.get("unavailablePrice");
    }
    BigDecimal optimisticQuantity = null;
    if (pReqVars.get("optimisticQuantity") != null) {
      optimisticQuantity = (BigDecimal) pReqVars.get("optimisticQuantity");
    }
    Set<String> ndFlPg = new HashSet<String>();
    ndFlPg.add("item");
    ndFlPg.add("itsPrice");
    Set<String> ndFlIdNm = new HashSet<String>();
    ndFlIdNm.add("itsId");
    ndFlIdNm.add("itsName");
    pReqVars.put("PriceGoodsitemdeepLevel", 3);
    pReqVars.put("PriceGoodsneededFields", ndFlPg);
    pReqVars.put("InvItemCategoryneededFields", ndFlIdNm);
    pReqVars.put("UnitOfMeasureneededFields", ndFlIdNm);
    List<PriceGoods> gpl = getSrvOrm().retrieveListWithConditions(pReqVars,
      PriceGoods.class, "where PRICECATEGORY=" + priceCategoryId);
    pReqVars.remove("PriceGoodsitemdeepLevel");
    pReqVars.remove("PriceGoodsneededFields");
    pReqVars.remove("InvItemCategoryneededFields");
    pReqVars.remove("UnitOfMeasureneededFields");
    pReqVars.put("WarehouseSiteneededFields", ndFlIdNm);
    pReqVars.put("WarehouseneededFields", ndFlIdNm);
    List<WarehouseSite> allPlaces = getSrvOrm()
      .retrieveList(pReqVars, WarehouseSite.class);
    pReqVars.remove("WarehouseSiteneededFields");
    pReqVars.remove("WarehouseneededFields");
    ndFlIdNm.add("itsPercentage");
    pReqVars.put("TaxneededFields", ndFlIdNm);
    List<InvItemTaxCategoryLine> allTaxCatsLns = getSrvOrm()
      .retrieveList(pReqVars, InvItemTaxCategoryLine.class);
    pReqVars.remove("TaxneededFields");
    List<Tax> usedTaxes = new ArrayList<Tax>();
    List<InvItemTaxCategory> usedTaxCats = new ArrayList<InvItemTaxCategory>();
    for (InvItemTaxCategoryLine tcl : allTaxCatsLns) {
      boolean txListed = false;
      for (Tax tx : usedTaxes) {
        if (tx.getItsId().equals(tcl.getTax().getItsId())) {
          txListed = true;
          break;
        }
      }
      if (!txListed) {
        usedTaxes.add(tcl.getTax());
        tcl.getTax().setItsPercentage(tcl.getItsPercentage());
      }
      int tci = -1;
      for (InvItemTaxCategory tc : usedTaxCats) {
        if (tc.getItsId().equals(tcl.getItsOwner().getItsId())) {
          tci = usedTaxCats.indexOf(tc);
          break;
        }
      }
      if (tci == -1) {
        usedTaxCats.add(tcl.getItsOwner());
        tcl.getItsOwner().setTaxes(new ArrayList<InvItemTaxCategoryLine>());
        tcl.getItsOwner().getTaxes().add(tcl);
      } else {
        usedTaxCats.get(tci).getTaxes().add(tcl);
      }
    }
    Collections.sort(usedTaxes, new CmprHasIdLong<Tax>());
    Collections.sort(usedTaxCats, new CmprHasIdLong<InvItemTaxCategory>());
    boolean isOnlyTax = true;
    for (InvItemTaxCategory txc : usedTaxCats) {
      if (txc.getTaxes().size() > 1) {
        isOnlyTax = false;
        break;
      }
    }
    String queryRests = "select INVITEM,  sum(THEREST) as THEREST,"
  + " min(WAREHOUSESITE) as WAREHOUSESITE from WAREHOUSEREST group by INVITEM;";
    List<WarehouseRestLineSm> whRests = new ArrayList<WarehouseRestLineSm>();
    IRecordSet<RS> recordSet = null;
    try {
      recordSet = getSrvDatabase().retrieveRecords(queryRests);
      if (recordSet.moveToFirst()) {
        do {
          WarehouseRestLineSm wrl = new WarehouseRestLineSm();
          whRests.add(wrl);
          wrl.setInvItemId(recordSet.getLong("INVITEM"));
          wrl.setSiteId(recordSet.getLong("WAREHOUSESITE"));
          Double theRset = recordSet.getDouble("THEREST");
          wrl.setTheRest(BigDecimal.valueOf(theRset));
        } while (recordSet.moveToNext());
      }
    } finally {
      if (recordSet != null) {
        recordSet.close();
      }
    }
    BigDecimal bd1d2 = new BigDecimal("1.2");
    BigDecimal bd100 = new BigDecimal("100");
    for (PriceGoods pg : gpl) {
      List<Object> row = new ArrayList<Object>();
      result.add(row);
      row.add(pg.getItem());
      row.add(pg.getItsPrice());
      row.add(pg.getItsPrice().divide(bd1d2, 2, RoundingMode.HALF_UP));
      BigDecimal quantity;
      Boolean isAvailable;
      WarehouseSite ws = null;
      if (unavailablePrice != null
        && pg.getItsPrice().compareTo(unavailablePrice) == 0) {
        quantity = BigDecimal.ZERO;
        isAvailable = Boolean.FALSE;
      } else {
        WarehouseRestLineSm wr = findRest(pg.getItem().getItsId(), whRests);
        if (wr != null) {
          quantity = wr.getTheRest();
          isAvailable = Boolean.TRUE;
          ws = findSite(wr.getSiteId(), allPlaces);
        } else {
          if (optimisticQuantity == null) {
            quantity = BigDecimal.ZERO;
            isAvailable = Boolean.FALSE;
          } else {
            quantity = optimisticQuantity;
            isAvailable = Boolean.TRUE;
          }
        }
      }
      row.add(quantity);
      row.add(isAvailable);
      row.add(ws);
      if (pg.getItem().getTaxCategory() != null) {
        for (InvItemTaxCategory txc : usedTaxCats) {
          if (txc.getItsId().equals(pg.getItem()
            .getTaxCategory().getItsId())) {
            //tax category with tax lines:
            pg.getItem().setTaxCategory(txc);
            break;
          }
        }
      }
      if (isOnlyTax) {
        TaxWr onlyTax = new TaxWr();
        if (pg.getItem().getTaxCategory() != null) {
          onlyTax.setTax(pg.getItem().getTaxCategory().getTaxes()
            .get(0).getTax());
          onlyTax.setIsUsed(true);
          onlyTax.setRate(onlyTax.getTax().getItsPercentage()
            .divide(bd100, as.getTaxPrecision() + 2, RoundingMode.HALF_UP));
        }
        row.add(onlyTax);
      } else { //multiply taxes case:
        TaxCategoryWr taxCat = new TaxCategoryWr();
        if (pg.getItem().getTaxCategory() != null) {
          taxCat.setTaxCategory(pg.getItem().getTaxCategory());
          taxCat.setIsUsed(true);
          for (InvItemTaxCategoryLine tl : taxCat.getTaxCategory().getTaxes()) {
            taxCat.setAggrPercent(taxCat.getAggrPercent()
              .add(tl.getItsPercentage()));
          }
          taxCat.setAggrRate(taxCat.getAggrPercent()
            .divide(bd100, as.getTaxPrecision() + 2, RoundingMode.HALF_UP));
        }
        row.add(taxCat);
        for (Tax tx : usedTaxes) {
          TaxWr txWr = new TaxWr();
          if (pg.getItem().getTaxCategory() != null) {
            for (InvItemTaxCategoryLine tl : pg.getItem()
              .getTaxCategory().getTaxes()) {
              if (tl.getTax().getItsId().equals(tx.getItsId())) {
                txWr.setTax(tl.getTax());
                txWr.setIsUsed(true);
                txWr.setRate(txWr.getTax().getItsPercentage().divide(bd100,
                  as.getTaxPrecision() + 2, RoundingMode.HALF_UP));
                break;
              }
            }
          }
          row.add(txWr);
        }
        for (InvItemTaxCategory txc : usedTaxCats) {
          TaxCategoryWr txCtWr = new TaxCategoryWr();
          if (pg.getItem().getTaxCategory() != null && txc.getItsId()
            .equals(pg.getItem().getTaxCategory().getItsId())) {
            txCtWr.setTaxCategory(txc);
            txCtWr.setIsUsed(true);
            for (InvItemTaxCategoryLine tl : txCtWr
              .getTaxCategory().getTaxes()) {
              txCtWr.setAggrPercent(txCtWr.getAggrPercent()
                .add(tl.getItsPercentage()));
            }
            txCtWr.setAggrRate(txCtWr.getAggrPercent().divide(bd100,
              as.getTaxPrecision() + 2, RoundingMode.HALF_UP));
          }
          row.add(txCtWr);
        }
      }
    }
    return result;
  }

  /**
   * <p>Retrieves sample data row (tree) to make CSV column.</p>
   * @param pReqVars additional param
   * @return sample data row
   * @throws Exception an Exception
   **/
  @Override
  public final List<Node<String>> getSampleDataRow(
    final Map<String, Object> pReqVars) throws Exception {
    String lang = (String) pReqVars.get("lang");
    List<Node<String>> result = new ArrayList<Node<String>>();
    Integer idx = 1;
    Node<String> nodeGoods = new Node<String>();
    result.add(nodeGoods);
    nodeGoods.setItsName(getSrvI18n().getMsg("goods", lang));
    nodeGoods.setItsNodes(new ArrayList<Node<String>>());
    Node<String> nodeGoodsName = new Node<String>();
    nodeGoods.getItsNodes().add(nodeGoodsName);
    nodeGoodsName.setItsName(getSrvI18n().getMsg("itsName", lang));
    nodeGoodsName.setItsValue(idx.toString() + ";itsName");
    Node<String> nodeGoodsId = new Node<String>();
    nodeGoods.getItsNodes().add(nodeGoodsId);
    nodeGoodsId.setItsName(getSrvI18n().getMsg("itsId", lang));
    nodeGoodsId.setItsValue(idx.toString() + ";itsId");
    Node<String> nodeGoodsItsCategory = new Node<String>();
    nodeGoods.getItsNodes().add(nodeGoodsItsCategory);
    nodeGoodsItsCategory.setItsName(getSrvI18n().getMsg("itsCategory", lang));
    nodeGoodsItsCategory.setItsNodes(new ArrayList<Node<String>>());
    Node<String> nodeGoodsItsCategoryName = new Node<String>();
    nodeGoodsItsCategory.getItsNodes().add(nodeGoodsItsCategoryName);
    nodeGoodsItsCategoryName.setItsName(getSrvI18n().getMsg("itsName", lang));
    nodeGoodsItsCategoryName
      .setItsValue(idx.toString() + ";itsCategory,itsName");
    Node<String> nodeGoodsItsCategoryId = new Node<String>();
    nodeGoodsItsCategory.getItsNodes().add(nodeGoodsItsCategoryId);
    nodeGoodsItsCategoryId.setItsName(getSrvI18n().getMsg("itsId", lang));
    nodeGoodsItsCategoryId.setItsValue(idx.toString() + ";itsCategory,itsId");
    Node<String> nodeGoodsDefUnitOfMeasure = new Node<String>();
    nodeGoods.getItsNodes().add(nodeGoodsDefUnitOfMeasure);
    nodeGoodsDefUnitOfMeasure.setItsName(getSrvI18n()
      .getMsg("defUnitOfMeasure", lang));
    nodeGoodsDefUnitOfMeasure.setItsNodes(new ArrayList<Node<String>>());
    Node<String> nodeGoodsDefUnitOfMeasureName = new Node<String>();
    nodeGoodsDefUnitOfMeasure.getItsNodes().add(nodeGoodsDefUnitOfMeasureName);
    nodeGoodsDefUnitOfMeasureName
      .setItsName(getSrvI18n().getMsg("itsName", lang));
    nodeGoodsDefUnitOfMeasureName
      .setItsValue(idx.toString() + ";defUnitOfMeasure,itsName");
    Node<String> nodeGoodsDefUnitOfMeasureId = new Node<String>();
    nodeGoodsDefUnitOfMeasure.getItsNodes().add(nodeGoodsDefUnitOfMeasureId);
    nodeGoodsDefUnitOfMeasureId.setItsName(getSrvI18n().getMsg("itsId", lang));
    nodeGoodsDefUnitOfMeasureId
      .setItsValue(idx.toString() + ";defUnitOfMeasure,itsId");
    idx++;
    Node<String> nodePrice = new Node<String>();
    result.add(nodePrice);
    nodePrice.setItsName(getSrvI18n().getMsg("itsPrice", lang));
    nodePrice.setItsValue(idx.toString());
    idx++;
    Node<String> nodeCost = new Node<String>();
    result.add(nodeCost);
    nodeCost.setItsName(getSrvI18n().getMsg("itsCost", lang));
    nodeCost.setItsValue(idx.toString());
    idx++;
    Node<String> nodeQuantity = new Node<String>();
    result.add(nodeQuantity);
    nodeQuantity.setItsName(getSrvI18n().getMsg("itsQuantity", lang));
    nodeQuantity.setItsValue(idx.toString());
    idx++;
    Node<String> nodeIsAvailable = new Node<String>();
    result.add(nodeIsAvailable);
    nodeIsAvailable.setItsName(getSrvI18n().getMsg("isAvailable", lang));
    nodeIsAvailable.setItsValue(idx.toString());
    idx++;
    Node<String> nodeWarehouseSite = new Node<String>();
    result.add(nodeWarehouseSite);
    nodeWarehouseSite.setItsName(getSrvI18n().getMsg("WarehouseSite", lang));
    nodeWarehouseSite.setItsNodes(new ArrayList<Node<String>>());
    Node<String> nodeWarehouseSiteName = new Node<String>();
    nodeWarehouseSite.getItsNodes().add(nodeWarehouseSiteName);
    nodeWarehouseSiteName.setItsName(getSrvI18n().getMsg("itsName", lang));
    nodeWarehouseSiteName.setItsValue(idx.toString() + ";itsName");
    Node<String> nodeWarehouseSiteId = new Node<String>();
    nodeWarehouseSite.getItsNodes().add(nodeWarehouseSiteId);
    nodeWarehouseSiteId.setItsName(getSrvI18n().getMsg("itsId", lang));
    nodeWarehouseSiteId.setItsValue(idx.toString() + ";itsId");
    Node<String> nodeWarehouseSiteWarehouse = new Node<String>();
    nodeWarehouseSite.getItsNodes().add(nodeWarehouseSiteWarehouse);
    nodeWarehouseSiteWarehouse
      .setItsName(getSrvI18n().getMsg("warehouse", lang));
    nodeWarehouseSiteWarehouse.setItsNodes(new ArrayList<Node<String>>());
    Node<String> nodeWarehouseSiteWarehouseName = new Node<String>();
    nodeWarehouseSiteWarehouse.getItsNodes()
      .add(nodeWarehouseSiteWarehouseName);
    nodeWarehouseSiteWarehouseName
      .setItsName(getSrvI18n().getMsg("itsName", lang));
    nodeWarehouseSiteWarehouseName
      .setItsValue(idx.toString() + ";warehouse,itsName");
    Node<String> nodeWarehouseSiteWarehouseId = new Node<String>();
    nodeWarehouseSiteWarehouse.getItsNodes().add(nodeWarehouseSiteWarehouseId);
    nodeWarehouseSiteWarehouseId.setItsName(getSrvI18n().getMsg("itsId", lang));
    nodeWarehouseSiteWarehouseId
      .setItsValue(idx.toString() + ";warehouse,itsId");
    Set<String> ndFlIdNm = new HashSet<String>();
    ndFlIdNm.add("itsId");
    ndFlIdNm.add("itsName");
    pReqVars.put("InvItemTaxCategoryneededFields", ndFlIdNm);
    pReqVars.put("TaxneededFields", ndFlIdNm);
    List<InvItemTaxCategoryLine> allTaxCatsLns = getSrvOrm()
      .retrieveList(pReqVars, InvItemTaxCategoryLine.class);
    pReqVars.remove("InvItemTaxCategoryneededFields");
    pReqVars.remove("TaxneededFields");
    List<Tax> usedTaxes = new ArrayList<Tax>();
    List<InvItemTaxCategory> usedTaxCats = new ArrayList<InvItemTaxCategory>();
    for (InvItemTaxCategoryLine tcl : allTaxCatsLns) {
      boolean txListed = false;
      for (Tax tx : usedTaxes) {
        if (tx.getItsId().equals(tcl.getTax().getItsId())) {
          txListed = true;
          break;
        }
      }
      if (!txListed) {
        usedTaxes.add(tcl.getTax());
      }
      int tci = -1;
      for (InvItemTaxCategory tc : usedTaxCats) {
        if (tc.getItsId().equals(tcl.getItsOwner().getItsId())) {
          tci = usedTaxCats.indexOf(tc);
          break;
        }
      }
      if (tci == -1) {
        usedTaxCats.add(tcl.getItsOwner());
        tcl.getItsOwner().setTaxes(new ArrayList<InvItemTaxCategoryLine>());
        tcl.getItsOwner().getTaxes().add(tcl);
      } else {
        usedTaxCats.get(tci).getTaxes().add(tcl);
      }
    }
    boolean isOnlyTax = true;
    for (InvItemTaxCategory txc : usedTaxCats) {
      if (txc.getTaxes().size() > 1) {
        isOnlyTax = false;
        break;
      }
    }
    if (isOnlyTax) {
      idx++;
      addTaxWr(result, idx.toString(),
        getSrvI18n().getMsg("OnlyTax", lang), lang);
    } else {
      idx++;
      addTaxCatWr(result, idx.toString(),
        getSrvI18n().getMsg("taxCategory", lang), lang);
      Collections.sort(usedTaxes, new CmprHasIdLong<Tax>());
      for (Tax tx : usedTaxes) {
        idx++;
        addTaxWr(result, idx.toString(), tx.getItsName(), lang);
      }
      Collections.sort(usedTaxCats, new CmprHasIdLong<InvItemTaxCategory>());
      for (InvItemTaxCategory txc : usedTaxCats) {
        idx++;
        addTaxCatWr(result, idx.toString(), txc.getItsName(), lang);
      }
    }
    return result;
  }

  //Utils:
  /**
   * <p>Add tax wrapper 1-st level node.</p>
   * @param pTree tree nodes
   * @param pIndex Index
   * @param pName Name
   * @param pLang Language
   **/
  public final void addTaxWr(final List<Node<String>> pTree,
    final String pIndex, final String pName, final String pLang) {
    Node<String> nodeTaxWr = new Node<String>();
    pTree.add(nodeTaxWr);
    nodeTaxWr.setItsName(pName);
    nodeTaxWr.setItsNodes(new ArrayList<Node<String>>());
    Node<String> nodeTaxWrIsUsed = new Node<String>();
    nodeTaxWr.getItsNodes().add(nodeTaxWrIsUsed);
    nodeTaxWrIsUsed.setItsName(getSrvI18n().getMsg("isUsed", pLang));
    nodeTaxWrIsUsed.setItsValue(pIndex + ";isUsed");
    Node<String> nodeTaxWrRate = new Node<String>();
    nodeTaxWr.getItsNodes().add(nodeTaxWrRate);
    nodeTaxWrRate.setItsName(getSrvI18n().getMsg("rate", pLang));
    nodeTaxWrRate.setItsValue(pIndex + ";rate");
    Node<String> nodeTaxWrTax = new Node<String>();
    nodeTaxWr.getItsNodes().add(nodeTaxWrTax);
    nodeTaxWrTax.setItsName(getSrvI18n().getMsg("tax", pLang));
    nodeTaxWrTax.setItsNodes(new ArrayList<Node<String>>());
    Node<String> nodeTaxWrTaxName = new Node<String>();
    nodeTaxWrTax.getItsNodes().add(nodeTaxWrTaxName);
    nodeTaxWrTaxName.setItsName(getSrvI18n().getMsg("itsName", pLang));
    nodeTaxWrTaxName.setItsValue(pIndex + ";tax,itsName");
    Node<String> nodeTaxWrTaxId = new Node<String>();
    nodeTaxWrTax.getItsNodes().add(nodeTaxWrTaxId);
    nodeTaxWrTaxId.setItsName(getSrvI18n().getMsg("itsId", pLang));
    nodeTaxWrTaxId.setItsValue(pIndex + ";tax,itsId");
    Node<String> nodeTaxWrTaxPercentage = new Node<String>();
    nodeTaxWrTax.getItsNodes().add(nodeTaxWrTaxPercentage);
    nodeTaxWrTaxPercentage
      .setItsName(getSrvI18n().getMsg("itsPercentage", pLang));
    nodeTaxWrTaxPercentage.setItsValue(pIndex + ";tax,itsPercentage");
  }

  /**
   * <p>Add tax category wrapper 1-st level node.</p>
   * @param pTree tree nodes
   * @param pIndex Index
   * @param pName Name
   * @param pLang Language
   **/
  public final void addTaxCatWr(final List<Node<String>> pTree,
    final String pIndex, final String pName, final String pLang) {
    Node<String> nodeTaxCatWr = new Node<String>();
    pTree.add(nodeTaxCatWr);
    nodeTaxCatWr.setItsName(pName);
    nodeTaxCatWr.setItsNodes(new ArrayList<Node<String>>());
    Node<String> nodeTaxCatWrIsUsed = new Node<String>();
    nodeTaxCatWr.getItsNodes().add(nodeTaxCatWrIsUsed);
    nodeTaxCatWrIsUsed.setItsName(getSrvI18n().getMsg("isUsed", pLang));
    nodeTaxCatWrIsUsed.setItsValue(pIndex + ";isUsed");
    Node<String> nodeTaxCatWrPercent = new Node<String>();
    nodeTaxCatWr.getItsNodes().add(nodeTaxCatWrPercent);
    nodeTaxCatWrPercent.setItsName(getSrvI18n().getMsg("aggrPercent", pLang));
    nodeTaxCatWrPercent.setItsValue(pIndex + ";aggrPercent");
    Node<String> nodeTaxCatWrRate = new Node<String>();
    nodeTaxCatWr.getItsNodes().add(nodeTaxCatWrRate);
    nodeTaxCatWrRate.setItsName(getSrvI18n().getMsg("aggrRate", pLang));
    nodeTaxCatWrRate.setItsValue(pIndex + ";aggrRate");
    Node<String> nodeTaxCatWrTaxCat = new Node<String>();
    nodeTaxCatWr.getItsNodes().add(nodeTaxCatWrTaxCat);
    nodeTaxCatWrTaxCat.setItsName(getSrvI18n().getMsg("taxCategory", pLang));
    nodeTaxCatWrTaxCat.setItsNodes(new ArrayList<Node<String>>());
    Node<String> nodeTaxCatWrTaxCatName = new Node<String>();
    nodeTaxCatWrTaxCat.getItsNodes().add(nodeTaxCatWrTaxCatName);
    nodeTaxCatWrTaxCatName.setItsName(getSrvI18n().getMsg("itsName", pLang));
    nodeTaxCatWrTaxCatName.setItsValue(pIndex + ";taxCategory,itsName");
    Node<String> nodeTaxCatWrTaxCatId = new Node<String>();
    nodeTaxCatWrTaxCat.getItsNodes().add(nodeTaxCatWrTaxCatId);
    nodeTaxCatWrTaxCatId.setItsName(getSrvI18n().getMsg("itsId", pLang));
    nodeTaxCatWrTaxCatId.setItsValue(pIndex + ";taxCategory,itsId");
  }

  /**
   * <p>Finds warehouse rest line by item ID.</p>
   * @param pItemId Item ID
   * @param pRestList Rest List
   * @return Warehouse rest line or null if not found
   **/
  public final WarehouseRestLineSm findRest(final Long pItemId,
    final List<WarehouseRestLineSm> pRestList) {
    for (WarehouseRestLineSm wr : pRestList) {
      if (wr.getInvItemId().equals(pItemId)) {
        return wr;
      }
    }
    return null;
  }

  /**
   * <p>Finds warehouse site by ID.</p>
   * @param pSiteId Site ID
   * @param pSiteList Site List
   * @return WarehouseSite
   * @throws Exception if not found
   **/
  public final WarehouseSite findSite(final Long pSiteId,
    final List<WarehouseSite> pSiteList) throws Exception {
    for (WarehouseSite ws : pSiteList) {
      if (ws.getItsId().equals(pSiteId)) {
        return ws;
      }
    }
    throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
      "Can' t find_warehouse site for ID: " + pSiteId);
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
  public final void setSrvDatabase(final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }

  /**
   * <p>Getter for srvI18n.</p>
   * @return ISrvI18n
   **/
  public final ISrvI18n getSrvI18n() {
    return this.srvI18n;
  }

  /**
   * <p>Setter for srvI18n.</p>
   * @param pSrvI18n reference
   **/
  public final void setSrvI18n(final ISrvI18n pSrvI18n) {
    this.srvI18n = pSrvI18n;
  }

  /**
   * <p>Getter for srvAccSettings.</p>
   * @return ISrvAccSettings
   **/
  public final ISrvAccSettings getSrvAccSettings() {
    return this.srvAccSettings;
  }

  /**
   * <p>Setter for srvAccSettings.</p>
   * @param pSrvAccSettings reference
   **/
  public final void setSrvAccSettings(final ISrvAccSettings pSrvAccSettings) {
    this.srvAccSettings = pSrvAccSettings;
  }
}
