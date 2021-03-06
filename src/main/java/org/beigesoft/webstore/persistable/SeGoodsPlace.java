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

import org.beigesoft.webstore.persistable.base.AItemPlace;

/**
 * <p>
 * It holds availability of a s.e. goods at pickup place.
 * </p>
 *
 * @author Yury Demidenko
 */
public class SeGoodsPlace extends AItemPlace<SeGoods, SeGoodsPlaceId>
  implements IHasSeSeller<SeGoodsPlaceId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private SeGoodsPlaceId itsId = new SeGoodsPlaceId();

  /**
   * <p>Pick up (storage) place, not null.</p>
   **/
  private PickUpPlace pickUpPlace;

  /**
   * <p>SeGoods, not null.</p>
   **/
  private SeGoods item;

  /**
   * <p>if present in hundred meters, i.e. 1 means 100meters.</p>
   **/
  private Long distance;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final SeGoodsPlaceId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final SeGoodsPlaceId pItsId) {
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
   * <p>Getter for seller.</p>
   * @return SeSeller
   **/
  @Override
  public final SeSeller getSeller() {
    return this.item.getSeller();
  }

  /**
   * <p>Setter for seller.</p>
   * @param pSeller reference
   **/
  @Override
  public final void setSeller(final SeSeller pSeller) {
    this.item.setSeller(pSeller);
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
      this.itsId = new SeGoodsPlaceId();
    }
    this.itsId.setPickUpPlace(this.pickUpPlace);
  }

  /**
   * <p>Getter for item.</p>
   * @return SeGoods
   **/
  @Override
  public final SeGoods getItem() {
    return this.item;
  }

  /**
   * <p>Setter for item.</p>
   * @param pSeGoods reference
   **/
  @Override
  public final void setItem(final SeGoods pSeGoods) {
    this.item = pSeGoods;
    if (this.itsId == null) {
      this.itsId = new SeGoodsPlaceId();
    }
    this.itsId.setItem(this.item);
  }

  //SGS:
  /**
   * <p>Getter for distance.</p>
   * @return Long
   **/
  public final Long getDistance() {
    return this.distance;
  }

  /**
   * <p>Setter for distance.</p>
   * @param pDistance reference
   **/
  public final void setDistance(final Long pDistance) {
    this.distance = pDistance;
  }
}
