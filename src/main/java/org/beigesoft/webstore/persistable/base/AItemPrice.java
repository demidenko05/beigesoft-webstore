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
import org.beigesoft.model.IHasIdLongVersion;
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
  AItemPrice<T extends IHasIdLongVersion, ID extends AItemPriceId<T>>
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
}
