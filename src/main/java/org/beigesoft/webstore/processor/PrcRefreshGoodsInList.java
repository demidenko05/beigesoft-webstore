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
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.comparator.CmprHasVersion;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.I18nInvItem;
import org.beigesoft.accounting.persistable.I18nUnitOfMeasure;
import org.beigesoft.accounting.persistable.UnitOfMeasure;
import org.beigesoft.webstore.model.ESpecificsItemType;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.persistable.Languages;
import org.beigesoft.webstore.persistable.GoodsSpecific;
import org.beigesoft.webstore.persistable.SpecificsOfItem;
import org.beigesoft.webstore.persistable.ChooseableSpecifics;
import org.beigesoft.webstore.persistable.SpecificsOfItemGroup;
import org.beigesoft.webstore.persistable.HtmlTemplate;
import org.beigesoft.webstore.persistable.GoodsPrice;
import org.beigesoft.webstore.persistable.GoodsAvailable;
//import org.beigesoft.webstore.persistable.GoodsRating;
import org.beigesoft.webstore.persistable.GoodsInListLuv;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.SettingsAdd;
import org.beigesoft.webstore.persistable.ItemInList;
import org.beigesoft.webstore.persistable.I18nChooseableSpecifics;
import org.beigesoft.webstore.persistable.I18nSpecificsOfItem;
import org.beigesoft.webstore.persistable.I18nSpecificsOfItemGroup;
import org.beigesoft.webstore.persistable.I18nSpecificInList;

/**
 * <p>Service that refresh webstore goods in ItemInList according current
 * GoodsAvailiable, GoodsSpecific, GoodsPrice, GoodsRating.
 * This is non-public processor.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcRefreshGoodsInList<RS> implements IProcessor {

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  /**
   * <p>Process entity request.</p>
   * @param pReqVars additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    retrieveStartData(pReqVars);
    SettingsAdd settingsAdd = (SettingsAdd) pReqVars.get("settingsAdd");
    TradingSettings tradingSettings = (TradingSettings) pReqVars.get("tradingSettings");
    GoodsInListLuv goodsInListLuv =
      (GoodsInListLuv) pReqVars.get("goodsInListLuv");
    pReqVars.remove("goodsInListLuv");
    String refreshAllGs = pRequestData.getParameter("refreshAllGs");
    List<GoodsSpecific> outdatedGoodsSpecific;
    if (refreshAllGs != null) {
      outdatedGoodsSpecific = retrieveGoodsSpecific(pReqVars, "");
    } else {
      outdatedGoodsSpecific =
        retrieveOutdatedGoodsSpecific(pReqVars, goodsInListLuv);
    }
    updateForGoodsSpecificList(pReqVars, outdatedGoodsSpecific,
      settingsAdd, goodsInListLuv, tradingSettings);
    pRequestData.setAttribute("totalUpdatedGdSp", outdatedGoodsSpecific.size());
    List<GoodsPrice> outdatedGoodsPrice =
      retrieveOutdatedGoodsPrice(pReqVars, goodsInListLuv);
    updateForGoodsPriceList(pReqVars, outdatedGoodsPrice,
      settingsAdd, goodsInListLuv);
    pRequestData.setAttribute("totalUpdatedGdPr", outdatedGoodsPrice.size());
    List<GoodsAvailable> outdatedGoodsAvailable =
      retrieveOutdatedGoodsAvailable(pReqVars, goodsInListLuv);
    updateForGoodsAvailableList(pReqVars, outdatedGoodsAvailable,
      settingsAdd, goodsInListLuv);
    pRequestData.setAttribute("totalUpdatedGdAv",
      outdatedGoodsAvailable.size());
  }

  /**
   * <p>Retrieve start data.</p>
   * @param pReqVars additional param
   * @throws Exception - an exception
   **/
  public final void retrieveStartData(
    final Map<String, Object> pReqVars) throws Exception {
    try {
      this.srvDatabase.setIsAutocommit(false);
      this.srvDatabase.
        setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
      this.srvDatabase.beginTransaction();
      GoodsInListLuv goodsInListLuv = getSrvOrm()
        .retrieveEntityById(pReqVars, GoodsInListLuv.class, 1L);
      if (goodsInListLuv == null) {
        goodsInListLuv = new GoodsInListLuv();
        goodsInListLuv.setItsId(1L);
        getSrvOrm().insertEntity(pReqVars, goodsInListLuv);
      }
      pReqVars.put("goodsInListLuv", goodsInListLuv);
      this.srvDatabase.commitTransaction();
    } catch (Exception ex) {
      if (!this.srvDatabase.getIsAutocommit()) {
        this.srvDatabase.rollBackTransaction();
      }
      throw ex;
    } finally {
      this.srvDatabase.releaseResources();
    }
  }

  /**
   * <p>Retrieve outdated GoodsAvailable.</p>
   * @param pReqVars additional param
   * @param pGoodsInListLuv GoodsInListLuv
   * @return Outdated GoodsAvailable list
   * @throws Exception - an exception
   **/
  public final List<GoodsAvailable> retrieveOutdatedGoodsAvailable(
    final Map<String, Object> pReqVars,
      final GoodsInListLuv pGoodsInListLuv) throws Exception {
    List<GoodsAvailable> result = null;
    try {
      this.srvDatabase.setIsAutocommit(false);
      this.srvDatabase.
        setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
      this.srvDatabase.beginTransaction();
      String tblNm = GoodsAvailable.class.getSimpleName().toUpperCase();
      String verCond;
      if (pGoodsInListLuv.getGoodsAvailableLuv() != null) {
        verCond = " where " + tblNm + ".ITSVERSION>" + pGoodsInListLuv
          .getGoodsAvailableLuv().toString();
      } else {
        verCond = "";
      }
      result = getSrvOrm().retrieveListWithConditions(pReqVars,
        GoodsAvailable.class, verCond + " order by " + tblNm + ".ITSVERSION");
      this.srvDatabase.commitTransaction();
    } catch (Exception ex) {
      if (!this.srvDatabase.getIsAutocommit()) {
        this.srvDatabase.rollBackTransaction();
      }
      throw ex;
    } finally {
      this.srvDatabase.releaseResources();
    }
    return result;
  }

  /**
   * <p>Update ItemInList with outdated GoodsAvailable.</p>
   * @param pReqVars additional param
   * @param pOutdGdAv outdated GoodsAvailable
   * @throws Exception - an exception
   **/
  public final void updateForGoodsAvailable(
    final Map<String, Object> pReqVars,
      final GoodsAvailable pOutdGdAv) throws Exception {
    String whereStr = "where ITSTYPE=0 and ITEMID=" + pOutdGdAv
      .getGoods().getItsId();
    ItemInList itemInList = getSrvOrm()
      .retrieveEntityWithConditions(pReqVars, ItemInList.class, whereStr);
    if (itemInList == null) {
      itemInList = createItemInList(pReqVars, pOutdGdAv.getGoods());
    }
    itemInList.setAvailableQuantity(pOutdGdAv.getItsQuantity());
    if (itemInList.getIsNew()) {
      getSrvOrm().insertEntity(pReqVars, itemInList);
    } else {
      getSrvOrm().updateEntity(pReqVars, itemInList);
    }
  }

  /**
   * <p>Update ItemInList with outdated GoodsAvailable list.
   * It does it with [N]-records per transaction method.</p>
   * @param pReqVars additional param
   * @param pOutdGdAvList outdated GoodsAvailable list
   * @param pSettingsAdd settings Add
   * @param pGoodsInListLuv goodsInListLuv
   * @throws Exception - an exception
   **/
  public final void updateForGoodsAvailableList(
    final Map<String, Object> pReqVars,
      final List<GoodsAvailable> pOutdGdAvList,
        final SettingsAdd pSettingsAdd,
          final GoodsInListLuv pGoodsInListLuv) throws Exception {
    if (pOutdGdAvList.size() == 0) {
      //Beige ORM may return empty list
      return;
    }
    int steps = pOutdGdAvList.size() / pSettingsAdd
      .getRecordsPerTransaction();
    int currentStep = 1;
    Long lastUpdatedVersion = null;
    do {
      try {
        this.srvDatabase.setIsAutocommit(false);
        this.srvDatabase.
          setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
        this.srvDatabase.beginTransaction();
        int stepLen = Math.min(pOutdGdAvList.size(), currentStep
          * pSettingsAdd.getRecordsPerTransaction());
        for (int i = (currentStep - 1) * pSettingsAdd
          .getRecordsPerTransaction(); i < stepLen; i++) {
          GoodsAvailable goodsSpecific = pOutdGdAvList.get(i);
          updateForGoodsAvailable(pReqVars, goodsSpecific);
          lastUpdatedVersion = goodsSpecific.getItsVersion();
        }
        pGoodsInListLuv.setGoodsAvailableLuv(lastUpdatedVersion);
        getSrvOrm().updateEntity(pReqVars, pGoodsInListLuv);
        this.srvDatabase.commitTransaction();
      } catch (Exception ex) {
        if (!this.srvDatabase.getIsAutocommit()) {
          this.srvDatabase.rollBackTransaction();
        }
        throw ex;
      } finally {
        this.srvDatabase.releaseResources();
      }
    } while (currentStep++ < steps);
  }

  /**
   * <p>Retrieve outdated GoodsPrice.</p>
   * @param pReqVars additional param
   * @param pGoodsInListLuv GoodsInListLuv
   * @return Outdated GoodsPrice list
   * @throws Exception - an exception
   **/
  public final List<GoodsPrice> retrieveOutdatedGoodsPrice(
    final Map<String, Object> pReqVars,
      final GoodsInListLuv pGoodsInListLuv) throws Exception {
    List<GoodsPrice> result = null;
    try {
      this.srvDatabase.setIsAutocommit(false);
      this.srvDatabase.
        setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
      this.srvDatabase.beginTransaction();
      String tblNm = GoodsPrice.class.getSimpleName().toUpperCase();
      String verCond;
      if (pGoodsInListLuv.getGoodsPriceLuv() != null) {
        verCond = " where " + tblNm + ".ITSVERSION>" + pGoodsInListLuv
          .getGoodsPriceLuv().toString();
      } else {
        verCond = "";
      }
      result = getSrvOrm().retrieveListWithConditions(pReqVars,
        GoodsPrice.class, verCond + " order by " + tblNm + ".ITSVERSION");
      this.srvDatabase.commitTransaction();
    } catch (Exception ex) {
      if (!this.srvDatabase.getIsAutocommit()) {
        this.srvDatabase.rollBackTransaction();
      }
      throw ex;
    } finally {
      this.srvDatabase.releaseResources();
    }
    return result;
  }

  /**
   * <p>Update ItemInList with outdated GoodsPrice.</p>
   * @param pReqVars additional param
   * @param pOutdGdPr outdated GoodsPrice
   * @throws Exception - an exception
   **/
  public final void updateForGoodsPrice(
    final Map<String, Object> pReqVars,
      final GoodsPrice pOutdGdPr) throws Exception {
    String whereStr = "where ITSTYPE=0 and ITEMID=" + pOutdGdPr
      .getGoods().getItsId();
    ItemInList itemInList = getSrvOrm()
      .retrieveEntityWithConditions(pReqVars, ItemInList.class, whereStr);
    if (itemInList == null) {
      itemInList = createItemInList(pReqVars, pOutdGdPr.getGoods());
    }
    itemInList.setItsPrice(pOutdGdPr.getItsPrice());
    itemInList.setPreviousPrice(pOutdGdPr.getPreviousPrice());
    if (itemInList.getIsNew()) {
      getSrvOrm().insertEntity(pReqVars, itemInList);
    } else {
      getSrvOrm().updateEntity(pReqVars, itemInList);
    }
  }

  /**
   * <p>Update ItemInList with outdated GoodsPrice list.
   * It does it with [N]-records per transaction method.</p>
   * @param pReqVars additional param
   * @param pOutdGdPrList outdated GoodsPrice list
   * @param pSettingsAdd settings Add
   * @param pGoodsInListLuv goodsInListLuv
   * @throws Exception - an exception
   **/
  public final void updateForGoodsPriceList(
    final Map<String, Object> pReqVars,
      final List<GoodsPrice> pOutdGdPrList,
        final SettingsAdd pSettingsAdd,
          final GoodsInListLuv pGoodsInListLuv) throws Exception {
    if (pOutdGdPrList.size() == 0) {
      //Beige ORM may return empty list
      return;
    }
    int steps = pOutdGdPrList.size() / pSettingsAdd
      .getRecordsPerTransaction();
    int currentStep = 1;
    Long lastUpdatedVersion = null;
    do {
      try {
        this.srvDatabase.setIsAutocommit(false);
        this.srvDatabase.
          setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
        this.srvDatabase.beginTransaction();
        int stepLen = Math.min(pOutdGdPrList.size(), currentStep
          * pSettingsAdd.getRecordsPerTransaction());
        for (int i = (currentStep - 1) * pSettingsAdd
          .getRecordsPerTransaction(); i < stepLen; i++) {
          GoodsPrice goodsSpecific = pOutdGdPrList.get(i);
          updateForGoodsPrice(pReqVars, goodsSpecific);
          lastUpdatedVersion = goodsSpecific.getItsVersion();
        }
        pGoodsInListLuv.setGoodsPriceLuv(lastUpdatedVersion);
        getSrvOrm().updateEntity(pReqVars, pGoodsInListLuv);
        this.srvDatabase.commitTransaction();
      } catch (Exception ex) {
        if (!this.srvDatabase.getIsAutocommit()) {
          this.srvDatabase.rollBackTransaction();
        }
        throw ex;
      } finally {
        this.srvDatabase.releaseResources();
      }
    } while (currentStep++ < steps);
  }

  /**
   * <p>Retrieve outdated GoodsSpecific.</p>
   * @param pReqVars additional param
   * @param pGoodsInListLuv GoodsInListLuv
   * @return Outdated GoodsSpecific list
   * @throws Exception - an exception
   **/
  public final List<GoodsSpecific> retrieveOutdatedGoodsSpecific(
    final Map<String, Object> pReqVars,
      final GoodsInListLuv pGoodsInListLuv) throws Exception {
    String verCondGs = " where GOODSSPECIFIC.GOODS in "
    + " (select distinct  GOODS from GOODSSPECIFIC join SPECIFICSOFITEM on GOODSSPECIFIC.SPECIFICS=SPECIFICSOFITEM.ITSID where GOODSSPECIFIC.ITSVERSION>"
    + pGoodsInListLuv.getGoodsSpecificLuv().toString() + ")";
    return retrieveGoodsSpecific(pReqVars, verCondGs);
  }

  /**
   * <p>Retrieve GoodsSpecific.</p>
   * @param pReqVars additional param
   * @param pWhere empty string "" or WHERE clause, e.g. " where ..."
   * @return GoodsSpecific list
   * @throws Exception - an exception
   **/
  public final List<GoodsSpecific> retrieveGoodsSpecific(
    final Map<String, Object> pReqVars,
      final String pWhere) throws Exception {
    List<GoodsSpecific> result = null;
    try {
      this.srvDatabase.setIsAutocommit(false);
      this.srvDatabase.
        setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
      this.srvDatabase.beginTransaction();
      pReqVars.put("GoodsSpecificspecificsdeepLevel", 2); //HTML templates only ID
      pReqVars.put("SpecificsOfItemtempHtmldeepLevel", 1); //HTML templates only ID
      HashSet<String> goodsFldNms = new HashSet<String>();
      goodsFldNms.add("itsId");
      goodsFldNms.add("itsName");
      pReqVars.put("InvItemneededFields", goodsFldNms);
      HashSet<String> soiFldNms = new HashSet<String>();
      soiFldNms.add("itsId");
      soiFldNms.add("itsName");
      soiFldNms.add("isShowInList");
      soiFldNms.add("itsType");
      soiFldNms.add("itsGroop");
      soiFldNms.add("tempHtml");
      pReqVars.put("SpecificsOfItemneededFields", soiFldNms);
      HashSet<String> soigFldNms = new HashSet<String>();
      soigFldNms.add("itsId");
      soigFldNms.add("itsName");
      soigFldNms.add("templateStart");
      soigFldNms.add("templateEnd");
      soigFldNms.add("templateDetail");
      pReqVars.put("SpecificsOfItemGroupneededFields", soigFldNms);
      result = getSrvOrm().retrieveListWithConditions(pReqVars,
        GoodsSpecific.class, pWhere
          + " order by GOODS.ITSID, SPECIFICS.ITSINDEX");
      pReqVars.remove("GoodsSpecificspecificsdeepLevel");
      pReqVars.remove("SpecificsOfItemtempHtmldeepLevel");
      pReqVars.remove("InvItemneededFields");
      pReqVars.remove("SpecificsOfItemneededFields");
      pReqVars.remove("SpecificsOfItemGroupneededFields");
      this.srvDatabase.commitTransaction();
    } catch (Exception ex) {
      if (!this.srvDatabase.getIsAutocommit()) {
        this.srvDatabase.rollBackTransaction();
      }
      throw ex;
    } finally {
      this.srvDatabase.releaseResources();
    }
    //make list of goods ordered by max(last updated specific) for that goods
    //goods itsVersion holds max(last updated specific):
    List<InvItem> goodsForSpecifics = new ArrayList<InvItem>();
    //also list of all used HTML templates ID to retrieve their full-filled list
    Set<Long> htmlTemplatesIds = new HashSet<Long>();
    InvItem currItem = null;
    for (GoodsSpecific gs : result) {
      if (gs.getSpecifics().getTempHtml() != null) {
        htmlTemplatesIds.add(gs.getSpecifics().getTempHtml().getItsId());
      }
      if (gs.getSpecifics().getItsGroop() != null) {
        if (gs.getSpecifics().getItsGroop().getTemplateStart() != null) {
          htmlTemplatesIds.add(gs.getSpecifics().getItsGroop().getTemplateStart().getItsId());
        }
        if (gs.getSpecifics().getItsGroop().getTemplateEnd() != null) {
          htmlTemplatesIds.add(gs.getSpecifics().getItsGroop().getTemplateEnd().getItsId());
        }
        if (gs.getSpecifics().getItsGroop().getTemplateStart() != null) {
          htmlTemplatesIds.add(gs.getSpecifics().getItsGroop().getTemplateDetail().getItsId());
        }
      }
      if (currItem == null || !gs.getGoods().getItsId().equals(currItem.getItsId())) {
        currItem = gs.getGoods();
        currItem.setItsVersion(gs.getItsVersion());
        goodsForSpecifics.add(currItem);
      } else { //2-nd, 3-d... specifics of this goods
        if (currItem.getItsVersion() < gs.getItsVersion()) {
          currItem.setItsVersion(gs.getItsVersion());
        }
      }
    }
    CmprHasVersion<InvItem> cmp = new CmprHasVersion<InvItem>();
    Collections.sort(goodsForSpecifics, cmp);
    pReqVars.put("goodsForSpecifics", goodsForSpecifics);
    pReqVars.put("htmlTemplatesIds", htmlTemplatesIds);
    return result;
  }

  /**
   * <p>Update ItemInList.SpecificInList with outdated GoodsSpecific.</p>
   * @param pReqVars additional param
   * @param pSettingsAdd SettingsAdd
   * @param pOutdGdSp outdated GoodsSpecific
   * @param pItemInList ItemInList
   * @param pSpecificsOfItemGroupWas SpecificsOfItemGroup previous
   * @param pI18nSpecInListLst I18nSpecificInList list
   * @param pI18nSpecLst I18nSpecificsOfItem list
   * @param pI18nChSpecLst I18nChooseableSpecifics list
   * @param pI18nUomLst I18nUnitOfMeasure list
   * @throws Exception - an exception
   **/
  public final void updateGoodsSpecificInList(
    final Map<String, Object> pReqVars,
      final SettingsAdd pSettingsAdd, final GoodsSpecific pOutdGdSp,
      final ItemInList pItemInList,
        final SpecificsOfItemGroup pSpecificsOfItemGroupWas,
          final List<I18nSpecificInList> pI18nSpecInListLst,
            final List<I18nSpecificsOfItem> pI18nSpecLst,
              final List<I18nChooseableSpecifics> pI18nChSpecLst,
                final List<I18nUnitOfMeasure> pI18nUomLst) throws Exception {
    String val1 = "";
    String val2 = "";
    if (pOutdGdSp.getSpecifics().getItsType()
      .equals(ESpecificsItemType.TEXT)) {
      //i18n not implemented, use chooseable specifics instead
      val1 = pOutdGdSp.getStringValue1();
    } else if (pOutdGdSp.getSpecifics().getItsType()
      .equals(ESpecificsItemType.BIGDECIMAL)) {
      val1 = srvNumberToString.print(pOutdGdSp.getNumericValue1().toString(),
        (String) pReqVars.get("dseparatorv"), (String) pReqVars
          .get("dgseparatorv"), Integer.valueOf(pOutdGdSp.getLongValue2()
            .intValue()), (Integer) pReqVars.get("digitsInGroup"));
      if (pOutdGdSp.getStringValue1() != null) {
        val2 = pOutdGdSp.getStringValue1();
      }
    } else if (pOutdGdSp.getSpecifics().getItsType()
      .equals(ESpecificsItemType.INTEGER)) {
      val1 = pOutdGdSp.getLongValue1().toString();
      if (pOutdGdSp.getStringValue1() != null) {
        val2 = pOutdGdSp.getStringValue1();
      }
    } else if (pOutdGdSp.getSpecifics().getItsType()
      .equals(ESpecificsItemType.CHOOSEABLE_SPECIFICS)) {
      val1 =  pOutdGdSp.getStringValue1();
    } else {
      return;
    }
    String templateDetail;
    if (pOutdGdSp.getSpecifics().getTempHtml() != null) {
      templateDetail = pOutdGdSp.getSpecifics().getTempHtml().getHtmlTemplate();
    } else if (pOutdGdSp.getSpecifics().getItsGroop() != null && pOutdGdSp.getSpecifics().getItsGroop().getTemplateDetail() != null) {
      templateDetail = pOutdGdSp.getSpecifics().getItsGroop().getTemplateDetail().getHtmlTemplate();
    } else {
      templateDetail = "<b>:SPECNM:</b> :VAL1 :VAL2";
    }
    String spdet = templateDetail.replace(":SPECNM", pOutdGdSp.getSpecifics().getItsName());
    spdet = spdet.replace(":VAL1", val1);
    spdet = spdet.replace(":VAL2", val2);
    if (pOutdGdSp.getSpecifics().getItsGroop() != null && pSpecificsOfItemGroupWas != null
      && pOutdGdSp.getSpecifics().getItsGroop().getItsId().equals(pSpecificsOfItemGroupWas.getItsId())) {
      pItemInList.setSpecificInList(pItemInList.getSpecificInList() + pSettingsAdd.getSpecSeparator() + spdet);
    } else {
      pItemInList.setSpecificInList(pItemInList.getSpecificInList() + spdet);
    }
    if (pI18nSpecInListLst != null) {
      for (I18nSpecificInList i18nspl : pI18nSpecInListLst) {
        if (pOutdGdSp.getSpecifics().getItsType().equals(ESpecificsItemType.BIGDECIMAL)
          || pOutdGdSp.getSpecifics().getItsType().equals(ESpecificsItemType.INTEGER)
            && pOutdGdSp.getLongValue1() != null) {
          UnitOfMeasure uom = new UnitOfMeasure();
          uom.setItsName(pOutdGdSp.getStringValue1());
          uom.setItsId(pOutdGdSp.getLongValue1());
          val2 = findUomName(pI18nUomLst, uom, i18nspl.getLang());
        } else if (pOutdGdSp.getSpecifics().getItsType()
          .equals(ESpecificsItemType.CHOOSEABLE_SPECIFICS)) {
          ChooseableSpecifics sp = new ChooseableSpecifics();
          sp.setItsName(pOutdGdSp.getStringValue1());
          sp.setItsId(pOutdGdSp.getLongValue1());
          val1 =  findChSpecName(pI18nChSpecLst, sp, i18nspl.getLang());
        }
        spdet = templateDetail.replace(":SPECNM", findSpecName(pI18nSpecLst, pOutdGdSp.getSpecifics(), i18nspl.getLang()));
        spdet = spdet.replace(":VAL1", val1);
        spdet = spdet.replace(":VAL2", val2);
        if (pOutdGdSp.getSpecifics().getItsGroop() != null && pSpecificsOfItemGroupWas != null
          && pOutdGdSp.getSpecifics().getItsGroop().getItsId().equals(pSpecificsOfItemGroupWas.getItsId())) {
          i18nspl.setSpecificInList(i18nspl.getSpecificInList() + pSettingsAdd.getSpecSeparator() + spdet);
        } else {
          i18nspl.setSpecificInList(i18nspl.getSpecificInList() + spdet);
        }
      }
    }
  }

  /**
   * <p>Update ItemInList with outdated GoodsSpecific list.
   * It does it with [N]-records per transaction method.</p>
   * @param pReqVars additional param
   * @param pOutdGdSpList outdated GoodsSpecific list
   * @param pSettingsAdd settings Add
   * @param pGoodsInListLuv goodsInListLuv
   * @param pTradingSettings trading settings
   * @throws Exception - an exception
   **/
  public final void updateForGoodsSpecificList(
    final Map<String, Object> pReqVars,
      final List<GoodsSpecific> pOutdGdSpList,
        final SettingsAdd pSettingsAdd,
          final GoodsInListLuv pGoodsInListLuv,
            final TradingSettings pTradingSettings) throws Exception {
    if (pOutdGdSpList.size() == 0) {
      //Beige ORM may return empty list
      return;
    }
    @SuppressWarnings("unchecked")
    List<InvItem> goodsForSpecifics = (List<InvItem>) pReqVars.get("goodsForSpecifics");
    pReqVars.remove("goodsForSpecifics");
    @SuppressWarnings("unchecked")
    Set<Long> htmlTemplatesIds = (Set<Long>) pReqVars.get("htmlTemplatesIds");
    pReqVars.remove("htmlTemplatesIds");
    List<HtmlTemplate> htmlTemplates = null;
    List<ItemInList> itemsInList = null;
    List<I18nChooseableSpecifics> i18nChooseableSpecificsLst = null;
    List<I18nSpecificsOfItemGroup> i18nSpecificsOfItemGroupLst = null;
    List<I18nSpecificsOfItem> i18nSpecificsOfItemLst = null;
    List<I18nInvItem> i18nInvItemLst = null;
    List<I18nSpecificInList> i18nSpecificInListLst = null;
    List<I18nUnitOfMeasure> i18nUomLst = null;
    try {
      this.srvDatabase.setIsAutocommit(false);
      this.srvDatabase.
        setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
      this.srvDatabase.beginTransaction();
      StringBuffer goodsIdIn = new StringBuffer("(");
      boolean isFirst = true;
      for (InvItem gd : goodsForSpecifics) {
        if (isFirst) {
          isFirst = false;
        } else {
          goodsIdIn.append(", ");
        }
        goodsIdIn.append(gd.getItsId().toString());
      }
      goodsIdIn.append(")");
      itemsInList = getSrvOrm().retrieveListWithConditions(pReqVars, ItemInList.class, "where ITSTYPE=0 and ITEMID in " + goodsIdIn.toString());
      if (htmlTemplatesIds.size() > 0) {
        StringBuffer whereStr = new StringBuffer("where ITSID in (");
        isFirst = true;
        for (Long id : htmlTemplatesIds) {
          if (isFirst) {
            isFirst = false;
          } else {
            whereStr.append(", ");
          }
          whereStr.append(id.toString());
        }
        whereStr.append(")");
        htmlTemplates = getSrvOrm().retrieveListWithConditions(pReqVars, HtmlTemplate.class, whereStr.toString());
      }
      if (pTradingSettings.getUseAdvancedI18n()) {
        pReqVars.put("I18nInvItemhasNamedeepLevel", 1);
        pReqVars.put("I18nInvItemlangdeepLevel", 1);
        i18nInvItemLst = getSrvOrm().retrieveListWithConditions(pReqVars, I18nInvItem.class, "where HASNAME in " + goodsIdIn.toString());
        pReqVars.remove("I18nInvItemhasNamedeepLevel");
        pReqVars.remove("I18nInvItemlangdeepLevel");
        if (i18nInvItemLst.size() > 0) {
          i18nSpecificInListLst = getSrvOrm().retrieveListWithConditions(pReqVars, I18nSpecificInList.class, "where ITSTYPE=0 and ITEMID in " + goodsIdIn.toString());
          StringBuffer specGrIdIn = null;
          StringBuffer specChIdIn = null;
          StringBuffer specIdIn = new StringBuffer("(");
          isFirst = true;
          boolean isFirstGr = true;
          boolean isFirstCh = true;
          for (GoodsSpecific gs : pOutdGdSpList) {
            if (isFirst) {
              isFirst = false;
            } else {
              specIdIn.append(", ");
            }
            specIdIn.append(gs.getSpecifics().getItsId().toString());
            if (gs.getSpecifics().getItsGroop() != null) {
              if (isFirstGr) {
                specGrIdIn = new StringBuffer("(");
                isFirstGr = false;
              } else {
                specGrIdIn.append(", ");
              }
              specGrIdIn.append(gs.getSpecifics().getItsGroop().getItsId().toString());
            }
            if (gs.getSpecifics().getItsType().equals(ESpecificsItemType.CHOOSEABLE_SPECIFICS)) {
              if (isFirstCh) {
                specChIdIn = new StringBuffer("(");
                isFirstCh = false;
              } else {
                specChIdIn.append(", ");
              }
              specChIdIn.append(gs.getLongValue1().toString());
            }
          }
          specIdIn.append(")");
          if (specGrIdIn != null) {
            specGrIdIn.append(")");
          }
          if (specChIdIn != null) {
            specChIdIn.append(")");
          }
          pReqVars.put("I18nSpecificsOfItemhasNamedeepLevel", 1);
          pReqVars.put("I18nSpecificsOfItemlangdeepLevel", 1);
          i18nSpecificsOfItemLst = getSrvOrm().retrieveListWithConditions(pReqVars, I18nSpecificsOfItem.class, "where HASNAME in " + specIdIn.toString());
          pReqVars.remove("I18nSpecificsOfItemhasNamedeepLevel");
          pReqVars.remove("I18nSpecificsOfItemlangdeepLevel");
          pReqVars.put("I18nUnitOfMeasurehasNamedeepLevel", 1);
          pReqVars.put("I18nUnitOfMeasurelangdeepLevel", 1);
          i18nUomLst = getSrvOrm().retrieveList(pReqVars, I18nUnitOfMeasure.class);
          pReqVars.remove("I18nUnitOfMeasurehasNamedeepLevel");
          pReqVars.remove("I18nUnitOfMeasurelangdeepLevel");
          if (specGrIdIn != null) {
            pReqVars.put("I18nSpecificsOfItemGrouphasNamedeepLevel", 1);
            pReqVars.put("I18nSpecificsOfItemGrouplangdeepLevel", 1);
            i18nSpecificsOfItemGroupLst = getSrvOrm().retrieveListWithConditions(pReqVars, I18nSpecificsOfItemGroup.class, "where HASNAME in " + specGrIdIn.toString());
            pReqVars.remove("I18nSpecificsOfItemGrouphasNamedeepLevel");
            pReqVars.remove("I18nSpecificsOfItemGrouplangdeepLevel");
          }
          if (specChIdIn != null) {
            pReqVars.put("I18nChooseableSpecificshasNamedeepLevel", 1);
            pReqVars.put("I18nChooseableSpecificslangdeepLevel", 1);
            i18nChooseableSpecificsLst = getSrvOrm().retrieveListWithConditions(pReqVars, I18nChooseableSpecifics.class, "where HASNAME in " + specChIdIn.toString());
            pReqVars.remove("I18nChooseableSpecificshasNamedeepLevel");
            pReqVars.remove("I18nChooseableSpecificslangdeepLevel");
          }
        }
      }
    } catch (Exception ex) {
      if (!this.srvDatabase.getIsAutocommit()) {
        this.srvDatabase.rollBackTransaction();
      }
      throw ex;
    } finally {
      this.srvDatabase.releaseResources();
    }
    if (htmlTemplates != null && htmlTemplates.size() > 0) {
      for (GoodsSpecific gs : pOutdGdSpList) {
        if (gs.getSpecifics().getTempHtml() != null) {
          gs.getSpecifics().setTempHtml(
          findTemplate(htmlTemplates, gs.getSpecifics().getTempHtml().getItsId()));
        }
        if (gs.getSpecifics().getItsGroop() != null) {
          if (gs.getSpecifics().getItsGroop().getTemplateStart() != null) {
            gs.getSpecifics().getItsGroop().setTemplateStart(
              findTemplate(htmlTemplates, gs.getSpecifics().getItsGroop().getTemplateStart().getItsId()));
          }
          if (gs.getSpecifics().getItsGroop().getTemplateEnd() != null) {
            gs.getSpecifics().getItsGroop().setTemplateEnd(
              findTemplate(htmlTemplates, gs.getSpecifics().getItsGroop().getTemplateEnd().getItsId()));
          }
          if (gs.getSpecifics().getItsGroop().getTemplateStart() != null) {
            gs.getSpecifics().getItsGroop().setTemplateDetail(
              findTemplate(htmlTemplates, gs.getSpecifics().getItsGroop().getTemplateDetail().getItsId()));
          }
        }
      }
    }
    int steps = goodsForSpecifics.size() / pSettingsAdd
      .getRecordsPerTransaction();
    int currentStep = 1;
    Long lastUpdatedVersion = null;
    do {
      try {
        this.srvDatabase.setIsAutocommit(false);
        this.srvDatabase.
          setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
        this.srvDatabase.beginTransaction();
        int stepLen = Math.min(goodsForSpecifics.size(), currentStep
          * pSettingsAdd.getRecordsPerTransaction());
        for (int i = (currentStep - 1) * pSettingsAdd
          .getRecordsPerTransaction(); i < stepLen; i++) {
          InvItem goods = goodsForSpecifics.get(i);
          ItemInList itemInList = findItemInListFor(itemsInList, goods);
          if (itemInList == null) {
            itemInList = createItemInList(pReqVars, goods);
          }
          int j = findFirstIdxFor(pOutdGdSpList, goods);
          SpecificsOfItemGroup specificsOfItemGroupWas = null;
          //reset any way:
          itemInList.setItsName(goods.getItsName());
          itemInList.setDetailsMethod(null);
          itemInList.setImageUrl(null);
          //i18n:
          List<I18nSpecificInList> i18nSpInLsLstFg = null;
          if (i18nInvItemLst != null) {
            for (I18nInvItem i18nInvItem : i18nInvItemLst) {
              if (i18nSpInLsLstFg == null) {
                i18nSpInLsLstFg = new ArrayList<I18nSpecificInList>();
              }
              if (i18nInvItem.getHasName().getItsId().equals(goods.getItsId())) {
                I18nSpecificInList i18nspInLs = findI18nSpecificInListFor(i18nSpecificInListLst, goods, i18nInvItem.getLang());
                if (i18nspInLs == null) {
                  i18nspInLs = new I18nSpecificInList();
                  i18nspInLs.setIsNew(true);
                  i18nspInLs.setItsType(EShopItemType.GOODS);
                  i18nspInLs.setItemId(goods.getItsId());
                  i18nspInLs.setLang(i18nInvItem.getLang());
                }
                i18nspInLs.setItsName(i18nInvItem.getItsName());
                i18nSpInLsLstFg.add(i18nspInLs);
              }
            }
          }
          if (pSettingsAdd.getSpecHtmlStart() !=  null) {
            itemInList.setSpecificInList(pSettingsAdd.getSpecHtmlStart());
            if (i18nSpInLsLstFg != null) {
              for (I18nSpecificInList i18nspInLs : i18nSpInLsLstFg) {
                i18nspInLs.setSpecificInList(pSettingsAdd.getSpecHtmlStart());
              }
            }
          } else {
            itemInList.setSpecificInList("");
            if (i18nSpInLsLstFg != null) {
              for (I18nSpecificInList i18nspInLs : i18nSpInLsLstFg) {
                i18nspInLs.setSpecificInList("");
              }
            }
          }
          boolean wasGrStart = false;
          do {
            if (pOutdGdSpList.get(j).getSpecifics().getIsShowInList()) {
              if (pOutdGdSpList.get(j).getSpecifics().getItsType()
                .equals(ESpecificsItemType.IMAGE)) {
                itemInList.setImageUrl(pOutdGdSpList.get(j).getStringValue1());
              } else { // build ItemInList.specificInList:
                if (wasGrStart && (pOutdGdSpList.get(j).getSpecifics().getItsGroop() == null
                    || specificsOfItemGroupWas != null
                      && !pOutdGdSpList.get(j).getSpecifics().getItsGroop().getItsId().equals(specificsOfItemGroupWas.getItsId()))) {
                  if (pSettingsAdd.getSpecGrSeparator() != null && pSettingsAdd.getSpecGrHtmlEnd() != null) {
                    itemInList.setSpecificInList(itemInList.getSpecificInList() + pSettingsAdd.getSpecGrHtmlEnd() + pSettingsAdd.getSpecGrSeparator());
                  } else if (pSettingsAdd.getSpecGrHtmlEnd() != null) {
                    itemInList.setSpecificInList(itemInList.getSpecificInList() + pSettingsAdd.getSpecGrHtmlEnd());
                  } else if (pSettingsAdd.getSpecGrSeparator() != null) {
                    itemInList.setSpecificInList(itemInList.getSpecificInList() + pSettingsAdd.getSpecGrSeparator());
                  }
                }
                if (pOutdGdSpList.get(j).getSpecifics().getItsGroop() == null || specificsOfItemGroupWas != null
                      && !pOutdGdSpList.get(j).getSpecifics().getItsGroop().getItsId().equals(specificsOfItemGroupWas.getItsId())) {
                  wasGrStart = true;
                  if (pSettingsAdd.getSpecGrHtmlStart() != null) {
                    itemInList.setSpecificInList(itemInList.getSpecificInList() + pSettingsAdd.getSpecGrHtmlStart());
                    if (i18nSpInLsLstFg != null) {
                      for (I18nSpecificInList i18nspInLs : i18nSpInLsLstFg) {
                        i18nspInLs.setSpecificInList(i18nspInLs.getSpecificInList() + pSettingsAdd.getSpecGrHtmlStart());
                      }
                    }
                  }
                  if (pOutdGdSpList.get(j).getSpecifics().getItsGroop() != null && pOutdGdSpList.get(j).getSpecifics().getItsGroop().getTemplateStart() != null) {
                    String grst = pOutdGdSpList.get(j).getSpecifics().getItsGroop().getTemplateStart()
                      .getHtmlTemplate().replace(":SPECGRNM", pOutdGdSpList.get(j).getSpecifics().getItsGroop().getItsName());
                    itemInList.setSpecificInList(itemInList.getSpecificInList() + grst);
                    if (i18nSpInLsLstFg != null) {
                      for (I18nSpecificInList i18nspInLs : i18nSpInLsLstFg) {
                        String gn = findI18nSpecGrName(i18nSpecificsOfItemGroupLst, pOutdGdSpList.get(j).getSpecifics().getItsGroop(), i18nspInLs.getLang());
                        grst = pOutdGdSpList.get(j).getSpecifics().getItsGroop().getTemplateStart()
                          .getHtmlTemplate().replace(":SPECGRNM", gn);
                        i18nspInLs.setSpecificInList(i18nspInLs.getSpecificInList() + grst);
                      }
                    }
                  }
                }
                updateGoodsSpecificInList(pReqVars, pSettingsAdd, pOutdGdSpList.get(j), itemInList, specificsOfItemGroupWas, i18nSpInLsLstFg, i18nSpecificsOfItemLst, i18nChooseableSpecificsLst, i18nUomLst);
                specificsOfItemGroupWas = pOutdGdSpList.get(j).getSpecifics().getItsGroop();
              }
            } else {
              itemInList.setDetailsMethod(1);
            }
            j++;
          } while (j < pOutdGdSpList.size() && pOutdGdSpList.get(j).getGoods().getItsId().equals(goods.getItsId()));
          if (pSettingsAdd.getSpecGrHtmlEnd() != null) {
            itemInList.setSpecificInList(itemInList.getSpecificInList() + pSettingsAdd.getSpecGrHtmlEnd());
            if (i18nSpInLsLstFg != null) {
              for (I18nSpecificInList i18nspInLs : i18nSpInLsLstFg) {
                i18nspInLs.setSpecificInList(i18nspInLs.getSpecificInList() + pSettingsAdd.getSpecGrHtmlEnd());
              }
            }
          }
          if (pSettingsAdd.getSpecHtmlEnd() != null) {
            itemInList.setSpecificInList(itemInList.getSpecificInList() + pSettingsAdd.getSpecHtmlEnd());
            if (i18nSpInLsLstFg != null) {
              for (I18nSpecificInList i18nspInLs : i18nSpInLsLstFg) {
                i18nspInLs.setSpecificInList(i18nspInLs.getSpecificInList() + pSettingsAdd.getSpecHtmlEnd());
              }
            }
          }
          if (itemInList.getIsNew()) {
            getSrvOrm().insertEntity(pReqVars, itemInList);
          } else {
            getSrvOrm().updateEntity(pReqVars, itemInList);
          }
          if (i18nSpInLsLstFg != null) {
            for (I18nSpecificInList i18nspInLs : i18nSpInLsLstFg) {
              if (i18nspInLs.getIsNew()) {
                getSrvOrm().insertEntity(pReqVars, i18nspInLs);
              } else {
                getSrvOrm().updateEntity(pReqVars, i18nspInLs);
              }
            }
          }
          lastUpdatedVersion = goods.getItsVersion();
        }
        pGoodsInListLuv.setGoodsSpecificLuv(lastUpdatedVersion);
        getSrvOrm().updateEntity(pReqVars, pGoodsInListLuv);
        this.srvDatabase.commitTransaction();
      } catch (Exception ex) {
        if (!this.srvDatabase.getIsAutocommit()) {
          this.srvDatabase.rollBackTransaction();
        }
        throw ex;
      } finally {
        this.srvDatabase.releaseResources();
      }
    } while (currentStep++ < steps);
  }

  /**
   * <p>Find find I18n Specific Group Name with given specifics group and lang.</p>
   * @param pSpecificsOfItemGroupLst list
   * @param pSpecGr Specific Group
   * @param pLang lang
   * @return group name
   **/
  protected final String findI18nSpecGrName(final List<I18nSpecificsOfItemGroup> pSpecificsOfItemGroupLst,
    final SpecificsOfItemGroup pSpecGr, final Languages pLang) {
    if (pSpecificsOfItemGroupLst != null) {
      for (I18nSpecificsOfItemGroup i18nspg : pSpecificsOfItemGroupLst) {
        if (i18nspg.getHasName().getItsId().equals(pSpecGr.getItsId())
          && i18nspg.getLang().getItsId().equals(pLang.getItsId())) {
          return i18nspg.getItsName();
        }
      }
    }
    return pSpecGr.getItsName();
  }

  /**
   * <p>Find find I18n UOM Name with given UOM and lang.</p>
   * @param pI18nUomLst list
   * @param pUom UOM
   * @param pLang lang
   * @return UOM name
   **/
  protected final String findUomName(final List<I18nUnitOfMeasure> pI18nUomLst,
    final UnitOfMeasure pUom, final Languages pLang) {
    for (I18nUnitOfMeasure i18uom : pI18nUomLst) {
      if (i18uom.getHasName().getItsId().equals(pUom.getItsId())
        && i18uom.getLang().getItsId().equals(pLang.getItsId())) {
        return i18uom.getItsName();
      }
    }
    return pUom.getItsName();
  }

  /**
   * <p>Find find I18n Chooseable Specific Name with given specifics and lang.</p>
   * @param pSpecificsOfItemLst list
   * @param pSpec Specific Group
   * @param pLang lang
   * @return specifics name
   **/
  protected final String findChSpecName(final List<I18nChooseableSpecifics> pSpecificsOfItemLst,
    final ChooseableSpecifics pSpec, final Languages pLang) {
    if (pSpecificsOfItemLst != null) {
      for (I18nChooseableSpecifics i18nsp : pSpecificsOfItemLst) {
        if (i18nsp.getHasName().getItsId().equals(pSpec.getItsId())
          && i18nsp.getLang().getItsId().equals(pLang.getItsId())) {
          return i18nsp.getItsName();
        }
      }
    }
    return pSpec.getItsName();
  }

  /**
   * <p>Find find I18n Specific Name with given specifics and lang.</p>
   * @param pSpecificsOfItemLst list
   * @param pSpec Specific Group
   * @param pLang lang
   * @return specifics name
   **/
  protected final String findSpecName(final List<I18nSpecificsOfItem> pSpecificsOfItemLst,
    final SpecificsOfItem pSpec, final Languages pLang) {
    for (I18nSpecificsOfItem i18nsp : pSpecificsOfItemLst) {
      if (i18nsp.getHasName().getItsId().equals(pSpec.getItsId())
        && i18nsp.getLang().getItsId().equals(pLang.getItsId())) {
        return i18nsp.getItsName();
      }
    }
    return pSpec.getItsName();
  }

  /**
   * <p>Find I18nSpecificInList with given goods and lang.</p>
   * @param pSpecificInListLst list
   * @param pGoods Goods
   * @param pLang lang
   * @return I18nSpecificInList with given goods and lang if exist or null
   **/
  protected final I18nSpecificInList findI18nSpecificInListFor(final List<I18nSpecificInList> pSpecificInListLst,
    final InvItem pGoods, final Languages pLang) {
    int j = 0;
    while (j < pSpecificInListLst.size()) {
      if (pSpecificInListLst.get(j).getItemId().equals(pGoods.getItsId())
        && pSpecificInListLst.get(j).getLang().getItsId().equals(pLang.getItsId())) {
        return pSpecificInListLst.get(j);
      }
      j++;
    }
    return null;
  }

  /**
   * <p>Find ItemInList with given goods.</p>
   * @param pGoodsList Goods list
   * @param pGoods Goods
   * @return ItemInList with given goods if exist or null
   **/
  protected final ItemInList findItemInListFor(final List<ItemInList> pGoodsList, final InvItem pGoods) {
    int j = 0;
    while (j < pGoodsList.size()) {
      if (pGoodsList.get(j).getItemId().equals(pGoods.getItsId())) {
        return pGoodsList.get(j);
      }
      j++;
    }
    return null;
  }

  /**
   * <p>Find the first index of specific with given goods.</p>
   * @param pOutdGdSpList GS list
   * @param pGoods Goods
   * @return the first index of specific with given goods
   * it throws Exception - if out of bounds
   **/
  protected final int findFirstIdxFor(final List<GoodsSpecific> pOutdGdSpList, final InvItem pGoods) {
    int j = 0;
    while (!pOutdGdSpList.get(j).getGoods().getItsId().equals(pGoods.getItsId())) {
      j++;
    }
    return j;
  }

  /**
   * <p>Find Html Template with given ID.</p>
   * @param pHtmlTemplates Html Templates
   * @param pId ID
   * @return template with given ID
   * @throws Exception - if not found
   **/
  protected final HtmlTemplate findTemplate(final List<HtmlTemplate> pHtmlTemplates, final Long pId)
    throws Exception {
    for (HtmlTemplate tmpl : pHtmlTemplates) {
      if (tmpl.getItsId().equals(pId)) {
        return tmpl;
      }
    }
    throw new Exception("Algorithm error/or template deleted: template not found for ID" + pId);
  }

  /**
   * <p>Create ItemInList.</p>
   * @param pReqVars additional param
   * @param pGoods Goods
   * @return ItemInList
   * @throws Exception - an exception
   **/
  protected final ItemInList createItemInList(
    final Map<String, Object> pReqVars,
      final InvItem pGoods) throws Exception {
    ItemInList itemInList = new ItemInList();
    itemInList.setIsNew(true);
    itemInList.setItsType(EShopItemType.GOODS);
    itemInList.setItemId(pGoods.getItsId());
    itemInList.setItsName(pGoods.getItsName());
    itemInList.setAvailableQuantity(BigDecimal.ZERO);
    return itemInList;
  }

  //Simple getters and setters:
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
   * <p>Getter for srvNumberToString.</p>
   * @return ISrvNumberToString
   **/
  public final ISrvNumberToString getSrvNumberToString() {
    return this.srvNumberToString;
  }

  /**
   * <p>Setter for srvNumberToString.</p>
   * @param pSrvNumberToString reference
   **/
  public final void setSrvNumberToString(
    final ISrvNumberToString pSrvNumberToString) {
    this.srvNumberToString = pSrvNumberToString;
  }
}
