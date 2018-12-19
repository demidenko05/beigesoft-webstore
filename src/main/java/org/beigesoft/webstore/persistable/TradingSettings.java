package org.beigesoft.webstore.persistable;

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

import org.beigesoft.persistable.AHasIdLongVersion;
import org.beigesoft.accounting.persistable.DebtorCreditorCategory;
import org.beigesoft.webstore.model.EPaymentMethod;
import org.beigesoft.webstore.model.EDelivering;

/**
 * <pre>
 * Trading settings.
 * Version changed time algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class TradingSettings extends AHasIdLongVersion {

  /**
   * <p>Not null, default false, is use auction.</p>
   **/
  private Boolean isUseAuction;

  /**
   * <p>Not Null, default true,
   * is show file static/img/logo-web-store.png in the top menu.</p>
   **/
  private Boolean isShowLogo;

  /**
   * <p>Name that will be appeared in the top menu (if present).</p>
   **/
  private String webStoreName;

  /**
   * <p>Not Null, default false, is use goods advising.</p>
   **/
  private Boolean isUseGoodsAdvising;

  /**
   * <p>Not Null, default false, is use services advising.</p>
   **/
  private Boolean isUseServicesAdvising;

  /**
   * <p>Not Null, default false, is use "goods/services seen history".</p>
   **/
  private Boolean isUseSeenHistory;

  /**
   * <p>Not Null, default false,
   * if use <b>Price for customer</b> method.</p>
   **/
  private Boolean isUsePriceForCustomer;

  /**
   * <p>Not null, default category for new created DebtorCreditor
   * for new OnlineBuyer.</p>
   **/
  private DebtorCreditorCategory defaultCustomerCategory;

  /**
   * <p>Remember unauthorized user for N days, 0 default,
   * not null - for BI and advertising.</p>
   **/
  private Integer rememberUnauthorizedUserFor;

  /**
   * <p>not null, default 5, maximum quantity
   * of top level catalogs shown in menu,
   * others will be in drop-down menu "others".</p>
   **/
  private Integer maxQuantityOfTopLevelCatalogs;

  /**
   * <p>Items per page, Not null, default 50.</p>
   **/
  private Integer itemsPerPage;

  /**
   * <p>not null, default 50, maximum quantity
   * of bulk operated items.</p>
   **/
  private Integer maxQuantityOfBulkItems;

  /**
   * <p>Catalog Of Goods/Services, nullable, In case of little catalog to list
   * all goods on start without clicking on "menu-[catalog]",
   * or it's catalog that offers different goods/services for all on start.</p>
   **/
  private CatalogGs catalogOnStart;

  /**
   * <p>not null, default 2, items list columns count.</p>
   **/
  private Integer columnsCount;

  /**
   * <p>Not null, whether create online user on first visit if there is no
   * cookie "cUserId" and of course user unauthorized. False by default,
   * otherwise it used for BI and tracking unauthorized users who even did not
   * added any item to cart for farther suggesting goods.</p>
   **/
  private Boolean isCreateOnlineUserOnFirstVisit;

  /**
   * <p>Not null, false default, false means that web-store used only payment
   * method, and consequently no need to divide customer order.
   * Usually it's online payment. For small shop or service maker
   * payment usually made by cash.</p>
   **/
  private Boolean isUsedSeveralPaymentMethods = Boolean.FALSE;

  /**
   * <p>Not null, false default, Use advanced internalization.</p>
   **/
  private Boolean useAdvancedI18n;

  /**
   * <p>Default payment method, not null, ONLINE default.</p>
   **/
  private EPaymentMethod defaultPaymentMethod = EPaymentMethod.ONLINE;

  /**
   * <p>If taxes excluded, default FALSE (included).</p>
   **/
  private Boolean txExcl = Boolean.FALSE;

  /**
   * <p>Not null, false default, Use overseas shipping.</p>
   **/
  private Boolean overseas = Boolean.FALSE;

  /**
   * <p>Not null, false default, Use "in country" tax destinations.</p>
   **/
  private Boolean txDests = Boolean.FALSE;

  /**
   * <p>Optional, only delivering for all items.</p>
   **/
  private EDelivering onlyDeliv;

  /**
   * <p>Not null, If any item can be in several place,
   * default FALSE (only place).</p>
   **/
  private Boolean sevPlac = Boolean.FALSE;

  //Simple getters and setters:
  /**
   * <p>Getter for isUseGoodsAdvising.</p>
   * @return Boolean
   **/
  public final Boolean getIsUseGoodsAdvising() {
    return this.isUseGoodsAdvising;
  }

  /**
   * <p>Setter for isUseGoodsAdvising.</p>
   * @param pIsUseGoodsAdvising reference
   **/
  public final void setIsUseGoodsAdvising(final Boolean pIsUseGoodsAdvising) {
    this.isUseGoodsAdvising = pIsUseGoodsAdvising;
  }

  /**
   * <p>Getter for isUseServicesAdvising.</p>
   * @return Boolean
   **/
  public final Boolean getIsUseServicesAdvising() {
    return this.isUseServicesAdvising;
  }

  /**
   * <p>Setter for isUseServicesAdvising.</p>
   * @param pIsUseServicesAdvising reference
   **/
  public final void setIsUseServicesAdvising(
    final Boolean pIsUseServicesAdvising) {
    this.isUseServicesAdvising = pIsUseServicesAdvising;
  }

  /**
   * <p>Getter for isUseSeenHistory.</p>
   * @return Boolean
   **/
  public final Boolean getIsUseSeenHistory() {
    return this.isUseSeenHistory;
  }

  /**
   * <p>Setter for isUseSeenHistory.</p>
   * @param pIsUseSeenHistory reference
   **/
  public final void setIsUseSeenHistory(final Boolean pIsUseSeenHistory) {
    this.isUseSeenHistory = pIsUseSeenHistory;
  }

  /**
   * <p>Getter for isUsePriceForCustomer.</p>
   * @return Boolean
   **/
  public final Boolean getIsUsePriceForCustomer() {
    return this.isUsePriceForCustomer;
  }

  /**
   * <p>Setter for isUsePriceForCustomer.</p>
   * @param pIsUsePriceForCustomer reference
   **/
  public final void setIsUsePriceForCustomer(
    final Boolean pIsUsePriceForCustomer) {
    this.isUsePriceForCustomer = pIsUsePriceForCustomer;
  }

  /**
   * <p>Getter for isShowLogo.</p>
   * @return Boolean
   **/
  public final Boolean getIsShowLogo() {
    return this.isShowLogo;
  }

  /**
   * <p>Setter for isShowLogo.</p>
   * @param pIsShowLogo reference
   **/
  public final void setIsShowLogo(final Boolean pIsShowLogo) {
    this.isShowLogo = pIsShowLogo;
  }

  /**
   * <p>Getter for webStoreName.</p>
   * @return String
   **/
  public final String getWebStoreName() {
    return this.webStoreName;
  }

  /**
   * <p>Setter for webStoreName.</p>
   * @param pWebStoreName reference
   **/
  public final void setWebStoreName(final String pWebStoreName) {
    this.webStoreName = pWebStoreName;
  }

  /**
   * <p>Getter for defaultCustomerCategory.</p>
   * @return DebtorCreditorCategory
   **/
  public final DebtorCreditorCategory getDefaultCustomerCategory() {
    return this.defaultCustomerCategory;
  }

  /**
   * <p>Setter for defaultCustomerCategory.</p>
   * @param pDefaultCustomerCategory reference
   **/
  public final void setDefaultCustomerCategory(
    final DebtorCreditorCategory pDefaultCustomerCategory) {
    this.defaultCustomerCategory = pDefaultCustomerCategory;
  }

  /**
   * <p>Getter for rememberUnauthorizedUserFor.</p>
   * @return Integer
   **/
  public final Integer getRememberUnauthorizedUserFor() {
    return this.rememberUnauthorizedUserFor;
  }

  /**
   * <p>Setter for rememberUnauthorizedUserFor.</p>
   * @param pRememberUnauthorizedUserFor reference
   **/
  public final void setRememberUnauthorizedUserFor(
    final Integer pRememberUnauthorizedUserFor) {
    this.rememberUnauthorizedUserFor = pRememberUnauthorizedUserFor;
  }

  /**
   * <p>Getter for maxQuantityOfTopLevelCatalogs.</p>
   * @return Integer
   **/
  public final Integer getMaxQuantityOfTopLevelCatalogs() {
    return this.maxQuantityOfTopLevelCatalogs;
  }

  /**
   * <p>Setter for maxQuantityOfTopLevelCatalogs.</p>
   * @param pMaxQuantityOfTopLevelCatalogs reference
   **/
  public final void setMaxQuantityOfTopLevelCatalogs(
    final Integer pMaxQuantityOfTopLevelCatalogs) {
    this.maxQuantityOfTopLevelCatalogs = pMaxQuantityOfTopLevelCatalogs;
  }

  /**
   * <p>Getter for itemsPerPage.</p>
   * @return Integer
   **/
  public final Integer getItemsPerPage() {
    return this.itemsPerPage;
  }

  /**
   * <p>Setter for itemsPerPage.</p>
   * @param pItemsPerPage reference
   **/
  public final void setItemsPerPage(final Integer pItemsPerPage) {
    this.itemsPerPage = pItemsPerPage;
  }

  /**
   * <p>Getter for isUseAuction.</p>
   * @return Boolean
   **/
  public final Boolean getIsUseAuction() {
    return this.isUseAuction;
  }

  /**
   * <p>Setter for isUseAuction.</p>
   * @param pIsUseAuction reference
   **/
  public final void setIsUseAuction(final Boolean pIsUseAuction) {
    this.isUseAuction = pIsUseAuction;
  }

  /**
   * <p>Getter for maxQuantityOfBulkItems.</p>
   * @return Integer
   **/
  public final Integer getMaxQuantityOfBulkItems() {
    return this.maxQuantityOfBulkItems;
  }

  /**
   * <p>Setter for maxQuantityOfBulkItems.</p>
   * @param pMaxQuantityOfBulkItems reference
   **/
  public final void setMaxQuantityOfBulkItems(
    final Integer pMaxQuantityOfBulkItems) {
    this.maxQuantityOfBulkItems = pMaxQuantityOfBulkItems;
  }

  /**
   * <p>Getter for catalogOnStart.</p>
   * @return CatalogGs
   **/
  public final CatalogGs getCatalogOnStart() {
    return this.catalogOnStart;
  }

  /**
   * <p>Setter for catalogOnStart.</p>
   * @param pCatalogOnStart reference
   **/
  public final void setCatalogOnStart(final CatalogGs pCatalogOnStart) {
    this.catalogOnStart = pCatalogOnStart;
  }

  /**
   * <p>Getter for columnsCount.</p>
   * @return Integer
   **/
  public final Integer getColumnsCount() {
    return this.columnsCount;
  }

  /**
   * <p>Setter for columnsCount.</p>
   * @param pColumnsCount reference
   **/
  public final void setColumnsCount(final Integer pColumnsCount) {
    this.columnsCount = pColumnsCount;
  }

  /**
   * <p>Getter for isCreateOnlineUserOnFirstVisit.</p>
   * @return Boolean
   **/
  public final Boolean getIsCreateOnlineUserOnFirstVisit() {
    return this.isCreateOnlineUserOnFirstVisit;
  }

  /**
   * <p>Setter for isCreateOnlineUserOnFirstVisit.</p>
   * @param pIsCreateOnlineUserOnFirstVisit reference
   **/
  public final void setIsCreateOnlineUserOnFirstVisit(
    final Boolean pIsCreateOnlineUserOnFirstVisit) {
    this.isCreateOnlineUserOnFirstVisit = pIsCreateOnlineUserOnFirstVisit;
  }

  /**
   * <p>Getter for isUsedSeveralPaymentMethods.</p>
   * @return Boolean
   **/
  public final Boolean getIsUsedSeveralPaymentMethods() {
    return this.isUsedSeveralPaymentMethods;
  }

  /**
   * <p>Setter for isUsedSeveralPaymentMethods.</p>
   * @param pIsUsedSeveralPaymentMethods reference
   **/
  public final void setIsUsedSeveralPaymentMethods(
    final Boolean pIsUsedSeveralPaymentMethods) {
    this.isUsedSeveralPaymentMethods = pIsUsedSeveralPaymentMethods;
  }

  /**
   * <p>Getter for defaultPaymentMethod.</p>
   * @return EPaymentMethod
   **/
  public final EPaymentMethod getDefaultPaymentMethod() {
    return this.defaultPaymentMethod;
  }

  /**
   * <p>Setter for defaultPaymentMethod.</p>
   * @param pDefaultPaymentMethod reference
   **/
  public final void setDefaultPaymentMethod(
    final EPaymentMethod pDefaultPaymentMethod) {
    this.defaultPaymentMethod = pDefaultPaymentMethod;
  }


  /**
   * <p>Getter for useAdvancedI18n.</p>
   * @return Boolean
   **/
  public final Boolean getUseAdvancedI18n() {
    return this.useAdvancedI18n;
  }

  /**
   * <p>Setter for useAdvancedI18n.</p>
   * @param pUseAdvancedI18n reference
   **/
  public final void setUseAdvancedI18n(final Boolean pUseAdvancedI18n) {
    this.useAdvancedI18n = pUseAdvancedI18n;
  }

  /**
   * <p>Getter for txExcl.</p>
   * @return Boolean
   **/
  public final Boolean getTxExcl() {
    return this.txExcl;
  }

  /**
   * <p>Setter for txExcl.</p>
   * @param pTxExcl reference
   **/
  public final void setTxExcl(final Boolean pTxExcl) {
    this.txExcl = pTxExcl;
  }

  /**
   * <p>Getter for overseas.</p>
   * @return Boolean
   **/
  public final Boolean getOverseas() {
    return this.overseas;
  }

  /**
   * <p>Setter for overseas.</p>
   * @param pOverseas reference
   **/
  public final void setOverseas(final Boolean pOverseas) {
    this.overseas = pOverseas;
  }

  /**
   * <p>Getter for txDests.</p>
   * @return Boolean
   **/
  public final Boolean getTxDests() {
    return this.txDests;
  }

  /**
   * <p>Setter for txDests.</p>
   * @param pTxDests reference
   **/
  public final void setTxDests(final Boolean pTxDests) {
    this.txDests = pTxDests;
  }

  /**
   * <p>Getter for onlyDeliv.</p>
   * @return EDelivering
   **/
  public final EDelivering getOnlyDeliv() {
    return this.onlyDeliv;
  }

  /**
   * <p>Setter for onlyDeliv.</p>
   * @param pOnlyDeliv reference
   **/
  public final void setOnlyDeliv(final EDelivering pOnlyDeliv) {
    this.onlyDeliv = pOnlyDeliv;
  }

  /**
   * <p>Getter for sevPlac.</p>
   * @return Boolean
   **/
  public final Boolean getSevPlac() {
    return this.sevPlac;
  }

  /**
   * <p>Setter for sevPlac.</p>
   * @param pSevPlac reference
   **/
  public final void setSevPlac(final Boolean pSevPlac) {
    this.sevPlac = pSevPlac;
  }
}
