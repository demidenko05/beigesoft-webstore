package org.beigesoft.webstore.persistable;

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

import java.util.Date;

import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.webstore.persistable.base.AItemPlace;

/**
 * <p>
 * Model of Goods Available - hold availability of a goods at pickup place.
 * </p>
 *
 * @author Yury Demidenko
 */
public class GoodsPlace extends AItemPlace<InvItem, GoodsPlaceId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private GoodsPlaceId itsId = new GoodsPlaceId();

  /**
   * <p>Pick up (storage) place, not null.</p>
   **/
  private PickUpPlace pickUpPlace;

  /**
   * <p>Goods, not null.</p>
   **/
  private InvItem item;

  /**
   * <p>Since date, not null.</p>
   **/
  private Date sinceDate;

  /**
   * <p>To switch method <b>Always available</b>.</p>
   **/
  private Boolean isAlways;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final GoodsPlaceId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final GoodsPlaceId pItsId) {
    this.itsId = pItsId;
    if (this.itsId != null) {
      this.pickUpPlace = this.itsId.getPickUpPlace();
      this.item = this.itsId.getItem();
    } else {
      this.pickUpPlace = null;
      this.item = null;
    }
  }

  /**
   * <p>Getter for pickUpPlace.</p>
   * @return PickUpPlace
   **/
  @Override
  public final PickUpPlace getPickUpPlace() {
    return this.pickUpPlace;
  }

  /**
   * <p>Setter for pickUpPlace.</p>
   * @param pPickUpPlace reference
   **/
  @Override
  public final void setPickUpPlace(final PickUpPlace pPickUpPlace) {
    this.pickUpPlace = pPickUpPlace;
    if (this.itsId == null) {
      this.itsId = new GoodsPlaceId();
    }
    this.itsId.setPickUpPlace(this.pickUpPlace);
  }

  /**
   * <p>Getter for item.</p>
   * @return InvItem
   **/
  @Override
  public final InvItem getItem() {
    return this.item;
  }

  /**
   * <p>Setter for item.</p>
   * @param pGoods reference
   **/
  @Override
  public final void setItem(final InvItem pGoods) {
    this.item = pGoods;
    if (this.itsId == null) {
      this.itsId = new GoodsPlaceId();
    }
    this.itsId.setItem(this.item);
  }

  //SGS:
  /**
   * <p>Getter for sinceDate.</p>
   * @return Date
   **/
  public final Date getSinceDate() {
    return this.sinceDate;
  }

  /**
   * <p>Setter for sinceDate.</p>
   * @param pSinceDate reference
   **/
  public final void setSinceDate(final Date pSinceDate) {
    this.sinceDate = pSinceDate;
  }

  /**
   * <p>Getter for isAlways.</p>
   * @return Boolean
   **/
  public final Boolean getIsAlways() {
    return this.isAlways;
  }

  /**
   * <p>Setter for isAlways.</p>
   * @param pIsAlways reference
   **/
  public final void setIsAlways(final Boolean pIsAlways) {
    this.isAlways = pIsAlways;
  }
}
