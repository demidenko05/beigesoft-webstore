package org.beigesoft.webstore.persistable.base;

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

import java.math.BigDecimal;

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;
import org.beigesoft.model.IHasIdLongVersionName;
import org.beigesoft.accounting.persistable.UnitOfMeasure;
import org.beigesoft.webstore.persistable.PriceCategory;

/**
 * <p>
 * Model of Item's price (Goods/Service/SeGoods/SeService).
 * BeigeORM not support generic fields!
 * </p>
 *
 * @param <T> item type
 * @param <ID> ID type
 * @author Yury Demidenko
 */
public abstract class
  AItemPrice<T extends IHasIdLongVersionName, ID extends AItemPriceId<T>>
    extends AEditableHasVersion implements IHasId<ID> {

  /**
   * <p>Its price.</p>
   **/
  private BigDecimal itsPrice;

  /**
   * <p>It can be used to implements widely
   * used method "Price down",
   * i.e. previousPrice = 60 against itsPrice = 45, nullable.</p>
   **/
  private BigDecimal previousPrice;

  /**
   * <p>Unit Of Measure, optional, e.g. per night or per hour.</p>
   **/
  private UnitOfMeasure unitOfMeasure;

  /**
   * <p>Number of decimal places, it's used together with UOM,
   * e.g. 4.5 hours, 0.123lb, etc, 0 default.</p>
   **/
  private Integer decPlaces = 0;

  /**
   * <p>Price per quantity of item, 1 default,
   * e.g. 12USD per 0.5ft, UOM ft, DP=1.</p>
   **/
  private BigDecimal perUnit = BigDecimal.ONE;

  /**
   * <p>Quantity step, 1 default,
   * e.g. 12USD per 0.5ft, UOM ft, DP=1, ST=0.5, so
   * buyer can order 0.5/1.0/1.5/2.0/etc. units of item.</p>
   **/
  private BigDecimal uStep = BigDecimal.ONE;

  /**
   * <p>Setter for pPriceCategory.</p>
   * @param pPriceCategory reference
   **/
  public abstract void setPriceCategory(PriceCategory pPriceCategory);

  /**
   * <p>Getter for priceCategory.</p>
   * @return PriceCategory
   **/
  public abstract PriceCategory getPriceCategory();

  /**
   * <p>Getter for item.</p>
   * @return T
   **/
  public abstract T getItem();

  /**
   * <p>Setter for item.</p>
   * @param pItem reference
   **/
  public abstract void setItem(final T pItem);

  //SGS:
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

  /**
   * <p>Getter for decPlaces.</p>
   * @return Integer
   **/
  public final Integer getDecPlaces() {
    return this.decPlaces;
  }

  /**
   * <p>Setter for decPlaces.</p>
   * @param pDecPlaces reference
   **/
  public final void setDecPlaces(final Integer pDecPlaces) {
    this.decPlaces = pDecPlaces;
  }

  /**
   * <p>Getter for perUnit.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getPerUnit() {
    return this.perUnit;
  }

  /**
   * <p>Setter for perUnit.</p>
   * @param pPerUnit reference
   **/
  public final void setPerUnit(final BigDecimal pPerUnit) {
    this.perUnit = pPerUnit;
  }

  /**
   * <p>Getter for uStep.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getUStep() {
    return this.uStep;
  }

  /**
   * <p>Setter for uStep.</p>
   * @param pUStep reference
   **/
  public final void setUStep(final BigDecimal pUStep) {
    this.uStep = pUStep;
  }
}
