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
   * <p>Not Null, default true, is it store of goods,
   * it will reflect to menu and which data will be
   * requested from database.</p>
   **/
  private Boolean isGoodsStore;

  /**
   * <p>Not Null, default false, is it store of services,
   * it will reflect to menu and which data will be
   * requested from database.</p>
   **/
  private Boolean isServiceStore;

  /**
   * <p>Not Null, default false, is it store of S.E. goods,
   * it will reflect to menu and which data will be
   * requested from database.</p>
   **/
  private Boolean isSeGoodsStore;

  /**
   * <p>Not Null, default false, is it store of S.E. services,
   * it will reflect to menu and which data will be requested
   * from database.</p>
   **/
  private Boolean isSeServiceStore;

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
  private Boolean isUsedSeveralPaymentMethods;

  //Simple getters and setters:
  /**
   * <p>Getter for isGoodsStore.</p>
   * @return Boolean
   **/
  public final Boolean getIsGoodsStore() {
    return this.isGoodsStore;
  }

  /**
   * <p>Setter for isGoodsStore.</p>
   * @param pIsGoodsStore reference
   **/
  public final void setIsGoodsStore(final Boolean pIsGoodsStore) {
    this.isGoodsStore = pIsGoodsStore;
  }

  /**
   * <p>Getter for isServiceStore.</p>
   * @return Boolean
   **/
  public final Boolean getIsServiceStore() {
    return this.isServiceStore;
  }

  /**
   * <p>Setter for isServiceStore.</p>
   * @param pIsServiceStore reference
   **/
  public final void setIsServiceStore(final Boolean pIsServiceStore) {
    this.isServiceStore = pIsServiceStore;
  }

  /**
   * <p>Getter for isSeGoodsStore.</p>
   * @return Boolean
   **/
  public final Boolean getIsSeGoodsStore() {
    return this.isSeGoodsStore;
  }

  /**
   * <p>Setter for isSeGoodsStore.</p>
   * @param pIsSeGoodsStore reference
   **/
  public final void setIsSeGoodsStore(final Boolean pIsSeGoodsStore) {
    this.isSeGoodsStore = pIsSeGoodsStore;
  }

  /**
   * <p>Getter for isSeServiceStore.</p>
   * @return Boolean
   **/
  public final Boolean getIsSeServiceStore() {
    return this.isSeServiceStore;
  }

  /**
   * <p>Setter for isSeServiceStore.</p>
   * @param pIsSeServiceStore reference
   **/
  public final void setIsSeServiceStore(final Boolean pIsSeServiceStore) {
    this.isSeServiceStore = pIsSeServiceStore;
  }

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
}
