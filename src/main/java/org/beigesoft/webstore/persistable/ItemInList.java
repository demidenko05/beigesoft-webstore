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

import java.math.BigDecimal;
import java.util.Date;

import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.persistable.AHasNameIdLongVersion;
import org.beigesoft.accounting.persistable.UnitOfMeasure;

/**
 * <pre>
 * Model of goods or service in list for improving performance.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class ItemInList extends AHasNameIdLongVersion {

  /**
   * <p>Goods/Service/SEGoods/SEService ID, not null.</p>
   **/
  private Long itemId;

  /**
   * <p>Goods/Service/SEGoods/SEService, not null.</p>
   **/
  private EShopItemType itsType;

  /**
   * <p>If exist addition to item name, max length = 500, this is usually
   * HTML string that briefly describes item.</p>
   **/
  private String specificInList;

  /**
   * <p>image URL if present, it usually present in specificInList.</p>
   **/
  private String imageUrl;

  /**
   * <p>Goods's rating if exist.</p>
   **/
  private Float itsRating;

  /**
   * <p>S.E. seller's rating if exist.</p>
   **/
  private Float seSellerRating;

  /**
   * <p>Null if auctioned, if TradingSettings.isUsePriceForCustomer=false
   * then it should be updated with changing GoodsPice,
   * otherwise it should be retrieved by additional
   * SQL query according BuyerPriceCategory when customer is requesting.</p>
   **/
  private BigDecimal itsPrice;

  /**
   * <p>Null if auctioned, if TradingSettings.isUsePriceForCustomer=false
   * then it should be updated with changing GoodsPice,
   * otherwise it should be retrieved by additional
   * SQL query according BuyerPriceCategory when customer is requesting.</p>
   **/
  private BigDecimal previousPrice;

  /**
   * <p>It's more or equals 0, it's sum of all GoodsAvailable,
   * so it's updated with changing GoodsAvailable. If it zero then row not
   * present in list. If customer use filter "available since" and/or
   * "pickup place" then it's used inner join - additional SQL query
   * of GoodsAvailable with place filter. Auctioning goods has quantity 1
   * settled by GoodsAuction, after some time (e.g. till next day) auction
   * is end, quantity will settled to 0.</p>
   **/
  private BigDecimal availableQuantity;

  // Auctioned fields:
  /**
   * <p>Date start auction.</p>
   **/
  private Date dateStartAuc;

  /**
   * <p>Date end auction.</p>
   **/
  private Date dateEndAuc;

  /**
   * <p>Start price auction.</p>
   **/
  private BigDecimal startPriceAuc;

  /**
   * <p>Minimum price auction.</p>
   **/
  private BigDecimal minimumPriceAuc;

  /**
   * <p>Buy it now price auction.</p>
   **/
  private BigDecimal buyItNowPriceAuc;

  /**
   * <p>Current price auction.</p>
   **/
  private BigDecimal currentPriceAuc;

  /**
   * <p>Sold price auction.</p>
   **/
  private BigDecimal soldPriceAuc;

  /**
   * <p>Total bids auction.</p>
   **/
  private Integer totalBidsAuc;

  /**
   * <p>Is ended auction.</p>
   **/
  private Boolean isEndedAuc;

  /**
   * <p>Is sold auction.</p>
   **/
  private Boolean isSoldAuc;

  //other:
  /**
   * <p>Method to render details (page), NULL - no detail page.</p>
   **/
  private Integer detailsMethod;

  /**
   * <p>Unit Of Measure, optional, e.g. per night or per hour.</p>
   **/
  private UnitOfMeasure unitOfMeasure;

  //Simple getters and setters:
  /**
   * <p>Getter for dateStartAuc.</p>
   * @return Date
   **/
  public final Date getDateStartAuc() {
    return this.dateStartAuc;
  }

  /**
   * <p>Setter for dateStartAuc.</p>
   * @param pDateStartAuc reference
   **/
  public final void setDateStartAuc(final Date pDateStartAuc) {
    this.dateStartAuc = pDateStartAuc;
  }

  /**
   * <p>Getter for dateEndAuc.</p>
   * @return Date
   **/
  public final Date getDateEndAuc() {
    return this.dateEndAuc;
  }

  /**
   * <p>Setter for dateEndAuc.</p>
   * @param pDateEndAuc reference
   **/
  public final void setDateEndAuc(final Date pDateEndAuc) {
    this.dateEndAuc = pDateEndAuc;
  }

  /**
   * <p>Getter for itemId.</p>
   * @return Long
   **/
  public final Long getItemId() {
    return this.itemId;
  }

  /**
   * <p>Setter for itemId.</p>
   * @param pItemId reference
   **/
  public final void setItemId(final Long pItemId) {
    this.itemId = pItemId;
  }

  /**
   * <p>Getter for itsType.</p>
   * @return EShopItemType
   **/
  public final EShopItemType getItsType() {
    return this.itsType;
  }

  /**
   * <p>Setter for itsType.</p>
   * @param pItsType reference
   **/
  public final void setItsType(final EShopItemType pItsType) {
    this.itsType = pItsType;
  }

  /**
   * <p>Getter for specificInList.</p>
   * @return String
   **/
  public final String getSpecificInList() {
    return this.specificInList;
  }

  /**
   * <p>Setter for specificInList.</p>
   * @param pSpecificInList reference
   **/
  public final void setSpecificInList(final String pSpecificInList) {
    this.specificInList = pSpecificInList;
  }

  /**
   * <p>Getter for imageUrl.</p>
   * @return String
   **/
  public final String getImageUrl() {
    return this.imageUrl;
  }

  /**
   * <p>Setter for imageUrl.</p>
   * @param pImageUrl reference
   **/
  public final void setImageUrl(final String pImageUrl) {
    this.imageUrl = pImageUrl;
  }

  /**
   * <p>Getter for itsRating.</p>
   * @return Float
   **/
  public final Float getItsRating() {
    return this.itsRating;
  }

  /**
   * <p>Setter for itsRating.</p>
   * @param pItsRating reference
   **/
  public final void setItsRating(final Float pItsRating) {
    this.itsRating = pItsRating;
  }

  /**
   * <p>Getter for seSellerRating.</p>
   * @return Float
   **/
  public final Float getSeSellerRating() {
    return this.seSellerRating;
  }

  /**
   * <p>Setter for seSellerRating.</p>
   * @param pSeSellerRating reference
   **/
  public final void setSeSellerRating(final Float pSeSellerRating) {
    this.seSellerRating = pSeSellerRating;
  }

  /**
   * <p>Getter for itsPrice.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsPrice() {
    return this.itsPrice;
  }

  /**
   * <p>Setter for itsPrice.</p>
   * @param pItsPrice reference
   **/
  public final void setItsPrice(final BigDecimal pItsPrice) {
    this.itsPrice = pItsPrice;
  }

  /**
   * <p>Getter for previousPrice.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getPreviousPrice() {
    return this.previousPrice;
  }

  /**
   * <p>Setter for previousPrice.</p>
   * @param pPreviousPrice reference
   **/
  public final void setPreviousPrice(final BigDecimal pPreviousPrice) {
    this.previousPrice = pPreviousPrice;
  }

  /**
   * <p>Getter for availableQuantity.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getAvailableQuantity() {
    return this.availableQuantity;
  }

  /**
   * <p>Setter for availableQuantity.</p>
   * @param pAvailableQuantity reference
   **/
  public final void setAvailableQuantity(final BigDecimal pAvailableQuantity) {
    this.availableQuantity = pAvailableQuantity;
  }

  /**
   * <p>Getter for startPriceAuc.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getStartPriceAuc() {
    return this.startPriceAuc;
  }

  /**
   * <p>Setter for startPriceAuc.</p>
   * @param pStartPriceAuc reference
   **/
  public final void setStartPriceAuc(final BigDecimal pStartPriceAuc) {
    this.startPriceAuc = pStartPriceAuc;
  }

  /**
   * <p>Getter for minimumPriceAuc.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getMinimumPriceAuc() {
    return this.minimumPriceAuc;
  }

  /**
   * <p>Setter for minimumPriceAuc.</p>
   * @param pMinimumPriceAuc reference
   **/
  public final void setMinimumPriceAuc(final BigDecimal pMinimumPriceAuc) {
    this.minimumPriceAuc = pMinimumPriceAuc;
  }

  /**
   * <p>Getter for buyItNowPriceAuc.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getBuyItNowPriceAuc() {
    return this.buyItNowPriceAuc;
  }

  /**
   * <p>Setter for buyItNowPriceAuc.</p>
   * @param pBuyItNowPriceAuc reference
   **/
  public final void setBuyItNowPriceAuc(final BigDecimal pBuyItNowPriceAuc) {
    this.buyItNowPriceAuc = pBuyItNowPriceAuc;
  }

  /**
   * <p>Getter for currentPriceAuc.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getCurrentPriceAuc() {
    return this.currentPriceAuc;
  }

  /**
   * <p>Setter for currentPriceAuc.</p>
   * @param pCurrentPriceAuc reference
   **/
  public final void setCurrentPriceAuc(final BigDecimal pCurrentPriceAuc) {
    this.currentPriceAuc = pCurrentPriceAuc;
  }

  /**
   * <p>Getter for soldPriceAuc.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getSoldPriceAuc() {
    return this.soldPriceAuc;
  }

  /**
   * <p>Setter for soldPriceAuc.</p>
   * @param pSoldPriceAuc reference
   **/
  public final void setSoldPriceAuc(final BigDecimal pSoldPriceAuc) {
    this.soldPriceAuc = pSoldPriceAuc;
  }

  /**
   * <p>Getter for totalBidsAuc.</p>
   * @return Integer
   **/
  public final Integer getTotalBidsAuc() {
    return this.totalBidsAuc;
  }

  /**
   * <p>Setter for totalBidsAuc.</p>
   * @param pTotalBidsAuc reference
   **/
  public final void setTotalBidsAuc(final Integer pTotalBidsAuc) {
    this.totalBidsAuc = pTotalBidsAuc;
  }

  /**
   * <p>Getter for isEndedAuc.</p>
   * @return Boolean
   **/
  public final Boolean getIsEndedAuc() {
    return this.isEndedAuc;
  }

  /**
   * <p>Setter for isEndedAuc.</p>
   * @param pIsEndedAuc reference
   **/
  public final void setIsEndedAuc(final Boolean pIsEndedAuc) {
    this.isEndedAuc = pIsEndedAuc;
  }

  /**
   * <p>Getter for isSoldAuc.</p>
   * @return Boolean
   **/
  public final Boolean getIsSoldAuc() {
    return this.isSoldAuc;
  }

  /**
   * <p>Setter for isSoldAuc.</p>
   * @param pIsSoldAuc reference
   **/
  public final void setIsSoldAuc(final Boolean pIsSoldAuc) {
    this.isSoldAuc = pIsSoldAuc;
  }

  /**
   * <p>Getter for detailsMethod.</p>
   * @return Integer
   **/
  public final Integer getDetailsMethod() {
    return this.detailsMethod;
  }

  /**
   * <p>Setter for detailsMethod.</p>
   * @param pDetailsMethod reference
   **/
  public final void setDetailsMethod(final Integer pDetailsMethod) {
    this.detailsMethod = pDetailsMethod;
  }

  /**
   * <p>Geter for unitOfMeasure.</p>
   * @return UnitOfMeasure
   **/
  public final UnitOfMeasure getUnitOfMeasure() {
    return this.unitOfMeasure;
  }

  /**
   * <p>Setter for unitOfMeasure.</p>
   * @param pUnitOfMeasure reference
   **/
  public final void setUnitOfMeasure(final UnitOfMeasure pUnitOfMeasure) {
    this.unitOfMeasure = pUnitOfMeasure;
  }
}
