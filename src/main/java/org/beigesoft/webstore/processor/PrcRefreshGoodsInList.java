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
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.webstore.model.ESpecificsItemType;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.persistable.GoodsSpecific;
import org.beigesoft.webstore.persistable.SpecificsOfItemGroup;
import org.beigesoft.webstore.persistable.HtmlTemplate;
import org.beigesoft.webstore.persistable.GoodsPrice;
import org.beigesoft.webstore.persistable.GoodsAvailable;
//import org.beigesoft.webstore.persistable.GoodsRating;
import org.beigesoft.webstore.persistable.GoodsInListLuv;
import org.beigesoft.webstore.persistable.SettingsAdd;
import org.beigesoft.webstore.persistable.ItemInList;

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
   * <p>Process entity request.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pAddParam,
    final IRequestData pRequestData) throws Exception {
    retrieveStartData(pAddParam);
    SettingsAdd settingsAdd = (SettingsAdd) pAddParam.get("settingsAdd");
    GoodsInListLuv goodsInListLuv =
      (GoodsInListLuv) pAddParam.get("goodsInListLuv");
    pAddParam.remove("goodsInListLuv");
    List<GoodsSpecific> outdatedGoodsSpecific =
      retrieveOutdatedGoodsSpecific(pAddParam, goodsInListLuv);
    updateForGoodsSpecificList(pAddParam, outdatedGoodsSpecific,
      settingsAdd, goodsInListLuv);
    pRequestData.setAttribute("totalUpdatedGdSp", outdatedGoodsSpecific.size());
    List<GoodsPrice> outdatedGoodsPrice =
      retrieveOutdatedGoodsPrice(pAddParam, goodsInListLuv);
    updateForGoodsPriceList(pAddParam, outdatedGoodsPrice,
      settingsAdd, goodsInListLuv);
    pRequestData.setAttribute("totalUpdatedGdPr", outdatedGoodsPrice.size());
    List<GoodsAvailable> outdatedGoodsAvailable =
      retrieveOutdatedGoodsAvailable(pAddParam, goodsInListLuv);
    updateForGoodsAvailableList(pAddParam, outdatedGoodsAvailable,
      settingsAdd, goodsInListLuv);
    pRequestData.setAttribute("totalUpdatedGdAv",
      outdatedGoodsAvailable.size());
  }

  /**
   * <p>Retrieve start data.</p>
   * @param pAddParam additional param
   * @throws Exception - an exception
   **/
  public final void retrieveStartData(
    final Map<String, Object> pAddParam) throws Exception {
    try {
      this.srvDatabase.setIsAutocommit(false);
      this.srvDatabase.
        setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
      this.srvDatabase.beginTransaction();
      GoodsInListLuv goodsInListLuv = getSrvOrm()
        .retrieveEntityById(pAddParam, GoodsInListLuv.class, 1L);
      if (goodsInListLuv == null) {
        goodsInListLuv = new GoodsInListLuv();
        goodsInListLuv.setItsId(1L);
        getSrvOrm().insertEntity(pAddParam, goodsInListLuv);
      }
      pAddParam.put("goodsInListLuv", goodsInListLuv);
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
   * @param pAddParam additional param
   * @param pGoodsInListLuv GoodsInListLuv
   * @return Outdated GoodsAvailable list
   * @throws Exception - an exception
   **/
  public final List<GoodsAvailable> retrieveOutdatedGoodsAvailable(
    final Map<String, Object> pAddParam,
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
      result = getSrvOrm().retrieveListWithConditions(pAddParam,
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
   * @param pAddParam additional param
   * @param pOutdGdAv outdated GoodsAvailable
   * @throws Exception - an exception
   **/
  public final void updateForGoodsAvailable(
    final Map<String, Object> pAddParam,
      final GoodsAvailable pOutdGdAv) throws Exception {
    String whereStr = "where ITSTYPE=0 and ITEMID=" + pOutdGdAv
      .getGoods().getItsId();
    ItemInList itemInList = getSrvOrm()
      .retrieveEntityWithConditions(pAddParam, ItemInList.class, whereStr);
    if (itemInList == null) {
      itemInList = createItemInList(pAddParam, pOutdGdAv.getGoods());
    }
    itemInList.setAvailableQuantity(pOutdGdAv.getItsQuantity());
    if (itemInList.getIsNew()) {
      getSrvOrm().insertEntity(pAddParam, itemInList);
    } else {
      getSrvOrm().updateEntity(pAddParam, itemInList);
    }
  }

  /**
   * <p>Update ItemInList with outdated GoodsAvailable list.
   * It does it with [N]-records per transaction method.</p>
   * @param pAddParam additional param
   * @param pOutdGdAvList outdated GoodsAvailable list
   * @param pSettingsAdd settings Add
   * @param pGoodsInListLuv goodsInListLuv
   * @throws Exception - an exception
   **/
  public final void updateForGoodsAvailableList(
    final Map<String, Object> pAddParam,
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
          updateForGoodsAvailable(pAddParam, goodsSpecific);
          lastUpdatedVersion = goodsSpecific.getItsVersion();
        }
        pGoodsInListLuv.setGoodsAvailableLuv(lastUpdatedVersion);
        getSrvOrm().updateEntity(pAddParam, pGoodsInListLuv);
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
   * @param pAddParam additional param
   * @param pGoodsInListLuv GoodsInListLuv
   * @return Outdated GoodsPrice list
   * @throws Exception - an exception
   **/
  public final List<GoodsPrice> retrieveOutdatedGoodsPrice(
    final Map<String, Object> pAddParam,
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
      result = getSrvOrm().retrieveListWithConditions(pAddParam,
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
   * @param pAddParam additional param
   * @param pOutdGdPr outdated GoodsPrice
   * @throws Exception - an exception
   **/
  public final void updateForGoodsPrice(
    final Map<String, Object> pAddParam,
      final GoodsPrice pOutdGdPr) throws Exception {
    String whereStr = "where ITSTYPE=0 and ITEMID=" + pOutdGdPr
      .getGoods().getItsId();
    ItemInList itemInList = getSrvOrm()
      .retrieveEntityWithConditions(pAddParam, ItemInList.class, whereStr);
    if (itemInList == null) {
      itemInList = createItemInList(pAddParam, pOutdGdPr.getGoods());
    }
    itemInList.setItsPrice(pOutdGdPr.getItsPrice());
    itemInList.setPreviousPrice(pOutdGdPr.getPreviousPrice());
    if (itemInList.getIsNew()) {
      getSrvOrm().insertEntity(pAddParam, itemInList);
    } else {
      getSrvOrm().updateEntity(pAddParam, itemInList);
    }
  }

  /**
   * <p>Update ItemInList with outdated GoodsPrice list.
   * It does it with [N]-records per transaction method.</p>
   * @param pAddParam additional param
   * @param pOutdGdPrList outdated GoodsPrice list
   * @param pSettingsAdd settings Add
   * @param pGoodsInListLuv goodsInListLuv
   * @throws Exception - an exception
   **/
  public final void updateForGoodsPriceList(
    final Map<String, Object> pAddParam,
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
          updateForGoodsPrice(pAddParam, goodsSpecific);
          lastUpdatedVersion = goodsSpecific.getItsVersion();
        }
        pGoodsInListLuv.setGoodsPriceLuv(lastUpdatedVersion);
        getSrvOrm().updateEntity(pAddParam, pGoodsInListLuv);
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
   * @param pAddParam additional param
   * @param pGoodsInListLuv GoodsInListLuv
   * @return Outdated GoodsSpecific list
   * @throws Exception - an exception
   **/
  public final List<GoodsSpecific> retrieveOutdatedGoodsSpecific(
    final Map<String, Object> pAddParam,
      final GoodsInListLuv pGoodsInListLuv) throws Exception {
    List<GoodsSpecific> result = null;
    try {
      this.srvDatabase.setIsAutocommit(false);
      this.srvDatabase.
        setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
      this.srvDatabase.beginTransaction();
      String verCondGs = "";
      if (pGoodsInListLuv.getGoodsSpecificLuv() != null) {
        verCondGs = " where GOODSSPECIFIC.GOODS in "
  + " (select distinct  GOODS from GOODSSPECIFIC join SPECIFICSOFITEM on GOODSSPECIFIC.SPECIFICS=SPECIFICSOFITEM.ITSID where GOODSSPECIFIC.ITSVERSION>"
  + pGoodsInListLuv.getGoodsSpecificLuv().toString() + ")";
      }
      pAddParam.put("GoodsSpecificspecificsdeepLevel", 3); //HTML templates only ID
      pAddParam.put("SpecificsOfItemtempHtmldeepLevel", 1); //HTML templates only ID
      HashSet<String> goodsFldNms = new HashSet<String>();
      goodsFldNms.add("itsId");
      goodsFldNms.add("itsName");
      pAddParam.put("InvItemneededFields", goodsFldNms);
      HashSet<String> soiFldNms = new HashSet<String>();
      soiFldNms.add("itsId");
      soiFldNms.add("itsName");
      soiFldNms.add("isShowInList");
      soiFldNms.add("itsType");
      soiFldNms.add("itsGroop");
      soiFldNms.add("tempHtml");
      soiFldNms.add("chooseableSpecificsType");
      pAddParam.put("SpecificsOfItemneededFields", soiFldNms);
      HashSet<String> soigFldNms = new HashSet<String>();
      soigFldNms.add("itsId");
      soigFldNms.add("itsName");
      soigFldNms.add("templateStart");
      soigFldNms.add("templateEnd");
      soigFldNms.add("templateDetail");
      pAddParam.put("SpecificsOfItemGroupneededFields", soigFldNms);
      HashSet<String> chsptpFldNms = new HashSet<String>();
      chsptpFldNms.add("itsId");
      chsptpFldNms.add("htmlTemplate");
      pAddParam.put("ChooseableSpecificsTypeneededFields", chsptpFldNms);
      result = getSrvOrm().retrieveListWithConditions(pAddParam,
        GoodsSpecific.class, verCondGs
          + " order by GOODS.ITSID, SPECIFICS.ITSINDEX");
      pAddParam.remove("GoodsSpecificspecificsdeepLevel");
      pAddParam.remove("SpecificsOfItemtempHtmldeepLevel");
      pAddParam.remove("InvItemneededFields");
      pAddParam.remove("SpecificsOfItemneededFields");
      pAddParam.remove("SpecificsOfItemGroupneededFields");
      pAddParam.remove("ChooseableSpecificsTypeneededFields");
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
      if (gs.getSpecifics().getChooseableSpecificsType() != null
        && gs.getSpecifics().getChooseableSpecificsType().getHtmlTemplate() != null) {
        htmlTemplatesIds.add(gs.getSpecifics().getChooseableSpecificsType().getHtmlTemplate().getItsId());
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
    pAddParam.put("goodsForSpecifics", goodsForSpecifics);
    pAddParam.put("htmlTemplatesIds", htmlTemplatesIds);
    return result;
  }

  /**
   * <p>Update ItemInList with outdated GoodsSpecific.</p>
   * @param pAddParam additional param
   * @param pSettingsAdd SettingsAdd
   * @param pOutdGdSp outdated GoodsSpecific
   * @param pItemInList ItemInList
   * @param pSpecificsOfItemGroupWas SpecificsOfItemGroup previous
   * @throws Exception - an exception
   **/
  public final void updateForGoodsSpecific(
    final Map<String, Object> pAddParam,
      final SettingsAdd pSettingsAdd, final GoodsSpecific pOutdGdSp,
      final ItemInList pItemInList,
        final SpecificsOfItemGroup pSpecificsOfItemGroupWas) throws Exception {
    if ((pSpecificsOfItemGroupWas != null && pOutdGdSp.getSpecifics().getItsGroop() == null
      || pSpecificsOfItemGroupWas != null && pOutdGdSp.getSpecifics().getItsGroop() != null
        && !pOutdGdSp.getSpecifics().getItsGroop().getItsId().equals(pSpecificsOfItemGroupWas.getItsId()))
          && pSpecificsOfItemGroupWas.getTemplateStart() != null) {
      pItemInList.setSpecificInList(pItemInList.getSpecificInList() + pSpecificsOfItemGroupWas.getTemplateEnd().getHtmlTemplate());
    }
    if ((pSpecificsOfItemGroupWas == null && pOutdGdSp.getSpecifics().getItsGroop() != null
      || pSpecificsOfItemGroupWas != null && pOutdGdSp.getSpecifics().getItsGroop() != null
        && !pOutdGdSp.getSpecifics().getItsGroop().getItsId().equals(pSpecificsOfItemGroupWas.getItsId()))
          && pOutdGdSp.getSpecifics().getItsGroop().getTemplateStart() != null) {
      if (pItemInList.getSpecificInList() == null) {
        pItemInList.setSpecificInList(pOutdGdSp.getSpecifics().getItsGroop().getTemplateStart().getHtmlTemplate());
      } else {
        pItemInList.setSpecificInList(pItemInList.getSpecificInList() + pOutdGdSp.getSpecifics().getItsGroop().getTemplateStart().getHtmlTemplate());
      }
    }
    String spNm = null;
    String spVal = null;
    String spFull = null;
    if (pOutdGdSp.getSpecifics().getItsType()
      .equals(ESpecificsItemType.IMAGE)) {
      pItemInList.setImageUrl(pOutdGdSp.getStringValue1());
    } else if (pOutdGdSp.getSpecifics().getItsType()
      .equals(ESpecificsItemType.TEXT)) {
      spNm = pOutdGdSp.getSpecifics().getItsName();
      spVal = pOutdGdSp.getStringValue1();
    } else if (pOutdGdSp.getSpecifics().getItsType()
      .equals(ESpecificsItemType.BIGDECIMAL)) {
      spNm = pOutdGdSp.getSpecifics().getItsName();
      spVal = pOutdGdSp.getNumericValue1().toString();
      if (pOutdGdSp.getStringValue1() != null) {
        spVal += " " + pOutdGdSp.getStringValue1();
      }
    } else if (pOutdGdSp.getSpecifics().getItsType()
      .equals(ESpecificsItemType.INTEGER)) {
      spNm = pOutdGdSp.getSpecifics().getItsName();
      spVal = pOutdGdSp.getLongValue1().toString();
      if (pOutdGdSp.getStringValue1() != null) {
        spVal += " " + pOutdGdSp.getStringValue1();
      }
    } else if (pOutdGdSp.getSpecifics().getChooseableSpecificsType() != null) {
      if (pOutdGdSp.getSpecifics().getChooseableSpecificsType()
        .getHtmlTemplate() != null) {
        spFull = pOutdGdSp.getSpecifics().getChooseableSpecificsType()
          .getHtmlTemplate().getHtmlTemplate()
            .replace(":VALUE1", pOutdGdSp.getStringValue2())
              .replace(":VALUE2", pOutdGdSp.getStringValue1());
      } else {
        spNm =  pOutdGdSp.getStringValue2();
        spVal =  pOutdGdSp.getStringValue1();
      }
    }
    if (spVal != null) {
      if (pOutdGdSp.getSpecifics().getItsGroop() != null
        && pOutdGdSp.getSpecifics().getItsGroop().getTemplateDetail() != null) {
        spFull = pOutdGdSp.getSpecifics().getItsGroop().getTemplateDetail()
          .getHtmlTemplate().replace(":VALUE1", spNm).replace(":VALUE2", spVal);
      } else {
        spFull = "<b> " + spNm + ": </b>" + spVal + ".";
      }
    }
    if (spFull != null) {
      if (pItemInList.getSpecificInList() == null) {
        pItemInList.setSpecificInList(spFull);
      } else {
        pItemInList.setSpecificInList(pItemInList.getSpecificInList() + spFull);
      }
    }
  }

  /**
   * <p>Update ItemInList with outdated GoodsSpecific list.
   * It does it with [N]-records per transaction method.</p>
   * @param pAddParam additional param
   * @param pOutdGdSpList outdated GoodsSpecific list
   * @param pSettingsAdd settings Add
   * @param pGoodsInListLuv goodsInListLuv
   * @throws Exception - an exception
   **/
  public final void updateForGoodsSpecificList(
    final Map<String, Object> pAddParam,
      final List<GoodsSpecific> pOutdGdSpList,
        final SettingsAdd pSettingsAdd,
          final GoodsInListLuv pGoodsInListLuv) throws Exception {
    if (pOutdGdSpList.size() == 0) {
      //Beige ORM may return empty list
      return;
    }
    @SuppressWarnings("unchecked")
    List<InvItem> goodsForSpecifics = (List<InvItem>) pAddParam.get("goodsForSpecifics");
    pAddParam.remove("goodsForSpecifics");
    @SuppressWarnings("unchecked")
    Set<Long> htmlTemplatesIds = (Set<Long>) pAddParam.get("htmlTemplatesIds");
    pAddParam.remove("htmlTemplatesIds");
    List<HtmlTemplate> htmlTemplates = null;
    List<ItemInList> itemsInList = null;
    try {
      this.srvDatabase.setIsAutocommit(false);
      this.srvDatabase.
        setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
      this.srvDatabase.beginTransaction();
      StringBuffer whereStr = new StringBuffer("where ITSTYPE=0 and ITEMID in (");
      boolean isFirst = true;
      for (InvItem gd : goodsForSpecifics) {
        if (isFirst) {
          isFirst = false;
        } else {
          whereStr.append(", ");
        }
        whereStr.append(gd.getItsId().toString());
      }
      whereStr.append(")");
      itemsInList = getSrvOrm().retrieveListWithConditions(pAddParam, ItemInList.class, whereStr.toString());
      if (htmlTemplatesIds.size() > 0) {
        whereStr = new StringBuffer("where ITSID in (");
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
        htmlTemplates = getSrvOrm().retrieveListWithConditions(pAddParam, HtmlTemplate.class, whereStr.toString());
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
        if (gs.getSpecifics().getChooseableSpecificsType() != null
          && gs.getSpecifics().getChooseableSpecificsType().getHtmlTemplate() != null) {
          gs.getSpecifics().getChooseableSpecificsType().setHtmlTemplate(
            findTemplate(htmlTemplates, gs.getSpecifics().getChooseableSpecificsType().getHtmlTemplate().getItsId()));
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
            itemInList = createItemInList(pAddParam, goods);
          }
          int j = findFirstIdxFor(pOutdGdSpList, goods);
          SpecificsOfItemGroup specificsOfItemGroupWas = null;
          itemInList.setDetailsMethod(null); //reset any way
          do {
            if (pOutdGdSpList.get(j).getSpecifics().getIsShowInList()) {
              updateForGoodsSpecific(pAddParam, pSettingsAdd, pOutdGdSpList.get(j), itemInList, specificsOfItemGroupWas);
              specificsOfItemGroupWas = pOutdGdSpList.get(j).getSpecifics().getItsGroop();
            } else {
              itemInList.setDetailsMethod(1);
            }
            j++;
          } while (j < pOutdGdSpList.size() && pOutdGdSpList.get(j).getGoods().getItsId().equals(goods.getItsId()));
          if (itemInList.getIsNew()) {
            getSrvOrm().insertEntity(pAddParam, itemInList);
          } else {
            getSrvOrm().updateEntity(pAddParam, itemInList);
          }
          lastUpdatedVersion = goods.getItsVersion();
        }
        pGoodsInListLuv.setGoodsSpecificLuv(lastUpdatedVersion);
        getSrvOrm().updateEntity(pAddParam, pGoodsInListLuv);
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
   * @param pAddParam additional param
   * @param pGoods Goods
   * @return ItemInList
   * @throws Exception - an exception
   **/
  protected final ItemInList createItemInList(
    final Map<String, Object> pAddParam,
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
}
