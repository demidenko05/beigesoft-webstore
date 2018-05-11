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

import java.util.Date;
import java.math.BigDecimal;

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;
import org.beigesoft.accounting.persistable.InvItem;

/**
 * <pre>
 * Model of Goods Available - hold availability of a goods at pickup place.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class GoodsAvailable extends AEditableHasVersion
  implements IHasId<GoodsAvailableId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private GoodsAvailableId itsId = new GoodsAvailableId();

  /**
   * <p>Pick up (storage) place, not null.</p>
   **/
  private PickUpPlace pickUpPlace;

  /**
   * <p>Goods, not null.</p>
   **/
  private InvItem goods;

  /**
   * <p>Since date, not null.</p>
   **/
  private Date sinceDate;

  /**
   * <p>To switch method <b>Always available</b>.</p>
   **/
  private Boolean isAlways;

  /**
   * <p>It's more or equals zero, if isAlways=true then must be more than zero
   * cause performance optimization (filter only "quantity>0").</p>
   **/
  private BigDecimal itsQuantity;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final GoodsAvailableId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final GoodsAvailableId pItsId) {
    this.itsId = pItsId;
    if (this.itsId != null) {
      this.pickUpPlace = this.itsId.getPickUpPlace();
      this.goods = this.itsId.getGoods();
    } else {
      this.pickUpPlace = null;
      this.goods = null;
    }
  }

  /**
   * <p>Setter for pickUpPlace.</p>
   * @param pPickUpPlace reference
   **/
  public final void setPickUpPlace(final PickUpPlace pPickUpPlace) {
    this.pickUpPlace = pPickUpPlace;
    if (this.itsId == null) {
      this.itsId = new GoodsAvailableId();
    }
    this.itsId.setPickUpPlace(this.pickUpPlace);
  }

  /**
   * <p>Setter for goods.</p>
   * @param pGoods reference
   **/
  public final void setGoods(final InvItem pGoods) {
    this.goods = pGoods;
    if (this.itsId == null) {
      this.itsId = new GoodsAvailableId();
    }
    this.itsId.setGoods(this.goods);
  }

  //Hiding reference SGS:
  /**
   * <p>Getter for sinceDate.</p>
   * @return Date
   **/
  public final Date getSinceDate() {
    if (this.sinceDate == null) {
      return null;
    } else {
      return new Date(this.sinceDate.getTime());
    }
  }

  /**
   * <p>Setter for sinceDate.</p>
   * @param pSinceDate reference
   **/
  public final void setSinceDate(final Date pSinceDate) {
    if (pSinceDate == null) {
      this.sinceDate = null;
    } else {
      this.sinceDate = new Date(pSinceDate.getTime());
    }
  }

  //Simple getters and setters:
  /**
   * <p>Getter for pickUpPlace.</p>
   * @return PickUpPlace
   **/
  public final PickUpPlace getPickUpPlace() {
    return this.pickUpPlace;
  }

  /**
   * <p>Getter for goods.</p>
   * @return InvItem
   **/
  public final InvItem getGoods() {
    return this.goods;
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
