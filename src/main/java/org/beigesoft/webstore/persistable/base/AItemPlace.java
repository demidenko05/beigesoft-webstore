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
import org.beigesoft.webstore.persistable.PickUpPlace;

/**
 * <p>
 * Model of Item availability at place (Goods/Service/SeGoods/SeService).
 * BeigeORM not support generic fields!
 * </p>
 *
 * @param <T> item type
 * @param <ID> ID type
 * @author Yury Demidenko
 */
public abstract class
  AItemPlace<T extends IHasIdLongVersion, ID extends AItemPlaceId<T>>
    extends AEditableHasVersion implements IHasId<ID> {

  /**
   * <p>It's more or equals zero, if available then must be more than zero
   * cause performance optimization (filter only "quantity>0").
   * Set it to zero to get any item (goods/service) out of list.</p>
   **/
  private BigDecimal itsQuantity;

  /**
   * <p>Setter for pPlace.</p>
   * @param pPlace reference
   **/
  public abstract void setPickUpPlace(PickUpPlace pPlace);

  /**
   * <p>Getter for itsPlace.</p>
   * @return PickUpPlace
   **/
  public abstract PickUpPlace getPickUpPlace();

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
   * <p>Getter for itsQuantity.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsQuantity() {
    return this.itsQuantity;
  }

  /**
   * <p>Setter for itsQuantity.</p>
   * @param pItsQuantity reference
   **/
  public final void setItsQuantity(final BigDecimal pItsQuantity) {
    this.itsQuantity = pItsQuantity;
  }
}
