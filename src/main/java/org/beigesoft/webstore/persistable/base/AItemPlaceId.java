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

import org.beigesoft.model.IHasIdLongVersion;
import org.beigesoft.webstore.persistable.PickUpPlace;

/**
 * <p>
 * ID of Item availability at place (Goods/Service/SeGoods/SeService).
 * BeigeORM not support generic fields!
 * </p>
 *
 * @param <T> item type
 * @author Yury Demidenko
 */
public abstract class AItemPlaceId<T extends IHasIdLongVersion> {

  /**
   * <p>Item's pick-up place.</p>
   **/
  private PickUpPlace pickUpPlace;

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
   * <p>Getter for pickUpPlace.</p>
   * @return PickUpPlace
   **/
  public final PickUpPlace getPickUpPlace() {
    return this.pickUpPlace;
  }

  /**
   * <p>Setter for pickUpPlace.</p>
   * @param pPickUpPlace reference
   **/
  public final void setPickUpPlace(final PickUpPlace pPickUpPlace) {
    this.pickUpPlace = pPickUpPlace;
  }
}
