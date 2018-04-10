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
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.webstore.model.ESpecificsItemType;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.persistable.GoodsSpecific;
import org.beigesoft.webstore.persistable.GoodsPrice;
import org.beigesoft.webstore.persistable.GoodsAvailable;
//import org.beigesoft.webstore.persistable.GoodsRating;
import org.beigesoft.webstore.persistable.GoodsInListLuv;
import org.beigesoft.webstore.persistable.SettingsAdd;
import org.beigesoft.webstore.persistable.ItemInList;
import org.beigesoft.webstore.service.ISrvSettingsAdd;
import org.beigesoft.webstore.service.ISrvTradingSettings;

/**
 * <p>Service that refresh webstore goods in ItemInList according current
 * GoodsAvailiable, GoodsSpecific, GoodsPrice, GoodsRating.</p>
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
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Business service for additional settings.</p>
   **/
  private ISrvSettingsAdd srvSettingsAdd;

  /**
   * <p>Business service for trading settings.</p>
   **/
  private ISrvTradingSettings srvTradingSettings;

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
    pAddParam.remove("settingsAdd");
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
    pRequestData.setAttribute("accSettings",
      this.srvAccSettings.lazyGetAccSettings(pAddParam));
    pRequestData.setAttribute("tradingSettings", srvTradingSettings
      .lazyGetTradingSettings(pAddParam));
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
      SettingsAdd settingsAdd = getSrvSettingsAdd()
        .lazyGetSettingsAdd(pAddParam);
      GoodsInListLuv goodsInListLuv = getSrvOrm()
        .retrieveEntityById(pAddParam, GoodsInListLuv.class, 1L);
      if (goodsInListLuv == null) {
        goodsInListLuv = new GoodsInListLuv();
        goodsInListLuv.setItsId(1L);
        getSrvOrm().insertEntity(pAddParam, goodsInListLuv);
      }
      pAddParam.put("settingsAdd", settingsAdd);
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
      String tblNm = GoodsSpecific.class.getSimpleName().toUpperCase();
      String verCondGs = " where ISSHOWINLIST=1";
      if (pGoodsInListLuv.getGoodsSpecificLuv() != null) {
        verCondGs += " and " + tblNm + ".ITSVERSION>" + pGoodsInListLuv
          .getGoodsSpecificLuv().toString();
      }
      result = getSrvOrm().retrieveListWithConditions(pAddParam,
        GoodsSpecific.class, verCondGs + " order by " + tblNm + ".ITSVERSION");
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
   * <p>Update ItemInList with outdated GoodsSpecific.</p>
   * @param pAddParam additional param
   * @param pOutdGdSp outdated GoodsSpecific
   * @throws Exception - an exception
   **/
  public final void updateForGoodsSpecific(
    final Map<String, Object> pAddParam,
      final GoodsSpecific pOutdGdSp) throws Exception {
    String whereStr = "where ITSTYPE=0 and ITEMID=" + pOutdGdSp
      .getGoods().getItsId();
    ItemInList itemInList = getSrvOrm()
      .retrieveEntityWithConditions(pAddParam, ItemInList.class, whereStr);
    if (itemInList == null) {
      itemInList = createItemInList(pAddParam, pOutdGdSp.getGoods());
    }
    if (pOutdGdSp.getSpecifics().getItsType()
      .equals(ESpecificsItemType.IMAGE)) {
      itemInList.setImageUrl(pOutdGdSp.getStringValue1());
    } else if (pOutdGdSp.getSpecifics().getItsType()
      .equals(ESpecificsItemType.TEXT)) {
      itemInList.setSpecificInList(pOutdGdSp.getStringValue1());
    } else if (pOutdGdSp.getSpecifics().getChooseableSpecificsType() != null) {
      itemInList.setSpecificInList(" <b>" + pOutdGdSp.getStringValue2()
        + "</b>: " + pOutdGdSp.getStringValue1() + ".");
    }
    if (itemInList.getIsNew()) {
      getSrvOrm().insertEntity(pAddParam, itemInList);
    } else {
      getSrvOrm().updateEntity(pAddParam, itemInList);
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
    int steps = pOutdGdSpList.size() / pSettingsAdd
      .getRecordsPerTransaction();
    int currentStep = 1;
    Long lastUpdatedVersion = null;
    do {
      try {
        this.srvDatabase.setIsAutocommit(false);
        this.srvDatabase.
          setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
        this.srvDatabase.beginTransaction();
        int stepLen = Math.min(pOutdGdSpList.size(), currentStep
          * pSettingsAdd.getRecordsPerTransaction());
        for (int i = (currentStep - 1) * pSettingsAdd
          .getRecordsPerTransaction(); i < stepLen; i++) {
          GoodsSpecific goodsSpecific = pOutdGdSpList.get(i);
          updateForGoodsSpecific(pAddParam, goodsSpecific);
          lastUpdatedVersion = goodsSpecific.getItsVersion();
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

  /**
   * <p>Getter for srvSettingsAdd.</p>
   * @return ISrvSettingsAdd
   **/
  public final ISrvSettingsAdd getSrvSettingsAdd() {
    return this.srvSettingsAdd;
  }

  /**
   * <p>Setter for srvSettingsAdd.</p>
   * @param pSrvSettingsAdd reference
   **/
  public final void setSrvSettingsAdd(final ISrvSettingsAdd pSrvSettingsAdd) {
    this.srvSettingsAdd = pSrvSettingsAdd;
  }

  /**
   * <p>Getter for srvTradingSettings.</p>
   * @return ISrvTradingSettings
   **/
  public final ISrvTradingSettings getSrvTradingSettings() {
    return this.srvTradingSettings;
  }

  /**
   * <p>Setter for srvTradingSettings.</p>
   * @param pSrvTradingSettings reference
   **/
  public final void setSrvTradingSettings(
    final ISrvTradingSettings pSrvTradingSettings) {
    this.srvTradingSettings = pSrvTradingSettings;
  }
}
