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
 * http://www.gnu.org/licenses/old-licenses/psl-2.0.en.html
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Collections;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.beigesoft.comparator.CmprHasIdLong;
import org.beigesoft.model.Node;
import org.beigesoft.service.ICsvDataRetriever;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.model.TaxWr;
import org.beigesoft.accounting.model.TaxCategoryWr;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.webstore.persistable.ServicePrice;

/**
 * <p>Service Price List Retriever.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class ServicePriceListRetriever<RS> implements ICsvDataRetriever {

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

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
    List<List<Object>> result = new ArrayList<List<Object>>();
    Long priceCategoryId = (Long) pReqVars.get("priceCategoryId");
    BigDecimal unavailablePrice = null;
    if (pReqVars.get("unavailablePrice") != null) {
      unavailablePrice = (BigDecimal) pReqVars.get("unavailablePrice");
    }
    Set<String> ndFlPg = new HashSet<String>();
    ndFlPg.add("service");
    ndFlPg.add("itsPrice");
    Set<String> ndFlIdNm = new HashSet<String>();
    ndFlIdNm.add("itsId");
    ndFlIdNm.add("itsName");
    pReqVars.put("ServicePriceitemdeepLevel", 3);
    pReqVars.put("ServicePriceneededFields", ndFlPg);
    pReqVars.put("ServiceToSaleCategoryneededFields", ndFlIdNm);
    List<ServicePrice> psl = getSrvOrm().retrieveListWithConditions(pReqVars,
      ServicePrice.class, "where PRICECATEGORY=" + priceCategoryId);
    pReqVars.remove("ServicePriceitemdeepLevel");
    pReqVars.remove("ServicePriceneededFields");
    pReqVars.remove("ServiceToSaleCategoryneededFields");
    pReqVars.remove("UnitOfMeasureneededFields");
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
    BigDecimal bd1d2 = new BigDecimal("1.2");
    BigDecimal bd100 = new BigDecimal("100");
    for (ServicePrice ps : psl) {
      List<Object> row = new ArrayList<Object>();
      result.add(row);
      row.add(ps.getItem());
      row.add(ps.getItsPrice());
      row.add(ps.getItsPrice().divide(bd1d2, 2, RoundingMode.HALF_UP));
      BigDecimal quantity;
      Boolean isAvailable;
      if (unavailablePrice != null
        && ps.getItsPrice().compareTo(unavailablePrice) == 0) {
        quantity = BigDecimal.ZERO;
        isAvailable = Boolean.FALSE;
      } else {
        quantity = BigDecimal.ONE;
        isAvailable = Boolean.TRUE;
      }
      row.add(quantity);
      row.add(isAvailable);
      if (ps.getItem().getTaxCategory() != null) {
        for (InvItemTaxCategory txc : usedTaxCats) {
          if (txc.getItsId().equals(ps.getItem()
            .getTaxCategory().getItsId())) {
            //tax category with tax lines:
            ps.getItem().setTaxCategory(txc);
            break;
          }
        }
      }
      if (isOnlyTax) {
        TaxWr onlyTax = new TaxWr();
        if (ps.getItem().getTaxCategory() != null) {
          onlyTax.setTax(ps.getItem().getTaxCategory().getTaxes()
            .get(0).getTax());
          onlyTax.setIsUsed(true);
          onlyTax.setRate(BigDecimal.ONE.add(onlyTax.getTax().getItsPercentage()
            .divide(bd100, 4, RoundingMode.HALF_UP)));
        }
        row.add(onlyTax);
      } else { //multiply taxes case:
        TaxCategoryWr taxCat = new TaxCategoryWr();
        if (ps.getItem().getTaxCategory() != null) {
          taxCat.setTaxCategory(ps.getItem().getTaxCategory());
          taxCat.setIsUsed(true);
          for (InvItemTaxCategoryLine tl : taxCat.getTaxCategory().getTaxes()) {
            taxCat.setAggrPercent(taxCat.getAggrPercent()
              .add(tl.getItsPercentage()));
          }
          taxCat.setAggrRate(BigDecimal.ONE.add(taxCat.getAggrPercent()
            .divide(bd100, 4, RoundingMode.HALF_UP)));
        }
        row.add(taxCat);
        for (Tax tx : usedTaxes) {
          TaxWr txWr = new TaxWr();
          if (ps.getItem().getTaxCategory() != null) {
            for (InvItemTaxCategoryLine tl : ps.getItem()
              .getTaxCategory().getTaxes()) {
              if (tl.getTax().getItsId().equals(tx.getItsId())) {
                txWr.setTax(tl.getTax());
                txWr.setIsUsed(true);
                txWr.setRate(BigDecimal.ONE.add(txWr.getTax().getItsPercentage()
                  .divide(bd100, 4, RoundingMode.HALF_UP)));
                break;
              }
            }
          }
          row.add(txWr);
        }
        for (InvItemTaxCategory txc : usedTaxCats) {
          TaxCategoryWr txCtWr = new TaxCategoryWr();
          if (ps.getItem().getTaxCategory() != null && txc.getItsId()
            .equals(ps.getItem().getTaxCategory().getItsId())) {
            txCtWr.setTaxCategory(txc);
            txCtWr.setIsUsed(true);
            for (InvItemTaxCategoryLine tl : txCtWr
              .getTaxCategory().getTaxes()) {
              txCtWr.setAggrPercent(txCtWr.getAggrPercent()
                .add(tl.getItsPercentage()));
            }
            txCtWr.setAggrRate(BigDecimal.ONE.add(txCtWr.getAggrPercent()
              .divide(bd100, 4, RoundingMode.HALF_UP)));
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
    Node<String> nodeService = new Node<String>();
    result.add(nodeService);
    nodeService.setItsName(getSrvI18n().getMsg("service", lang));
    nodeService.setItsNodes(new ArrayList<Node<String>>());
    Node<String> nodeServiceName = new Node<String>();
    nodeService.getItsNodes().add(nodeServiceName);
    nodeServiceName.setItsName(getSrvI18n().getMsg("itsName", lang));
    nodeServiceName.setItsValue(idx.toString() + ";itsName");
    Node<String> nodeServiceId = new Node<String>();
    nodeService.getItsNodes().add(nodeServiceId);
    nodeServiceId.setItsName(getSrvI18n().getMsg("itsId", lang));
    nodeServiceId.setItsValue(idx.toString() + ";itsId");
    Node<String> nodeServiceItsCategory = new Node<String>();
    nodeService.getItsNodes().add(nodeServiceItsCategory);
    nodeServiceItsCategory.setItsName(getSrvI18n().getMsg("itsCategory", lang));
    nodeServiceItsCategory.setItsNodes(new ArrayList<Node<String>>());
    Node<String> nodeServiceItsCategoryName = new Node<String>();
    nodeServiceItsCategory.getItsNodes().add(nodeServiceItsCategoryName);
    nodeServiceItsCategoryName.setItsName(getSrvI18n().getMsg("itsName", lang));
    nodeServiceItsCategoryName
      .setItsValue(idx.toString() + ";itsCategory,itsName");
    Node<String> nodeServiceItsCategoryId = new Node<String>();
    nodeServiceItsCategory.getItsNodes().add(nodeServiceItsCategoryId);
    nodeServiceItsCategoryId.setItsName(getSrvI18n().getMsg("itsId", lang));
    nodeServiceItsCategoryId.setItsValue(idx.toString() + ";itsCategory,itsId");
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
}
