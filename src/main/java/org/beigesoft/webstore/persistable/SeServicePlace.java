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
 * It holds availability of a s.e. service at pickup place.
 * </p>
 *
 * @author Yury Demidenko
 */
public class SeServicePlace extends AItemPlace<SeService, SeServicePlaceId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private SeServicePlaceId itsId = new SeServicePlaceId();

  /**
   * <p>Pick up (storage) place, not null.</p>
   **/
  private PickUpPlace pickUpPlace;

  /**
   * <p>SeService, not null.</p>
   **/
  private SeService item;

  /**
   * <p>if present in hundred meters, i.e. 1 means 100meters.</p>
   **/
  private Long distance;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final SeServicePlaceId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final SeServicePlaceId pItsId) {
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
      this.itsId = new SeServicePlaceId();
    }
    this.itsId.setPickUpPlace(this.pickUpPlace);
  }

  /**
   * <p>Getter for item.</p>
   * @return SeService
   **/
  @Override
  public final SeService getItem() {
    return this.item;
  }

  /**
   * <p>Setter for item.</p>
   * @param pSeService reference
   **/
  @Override
  public final void setItem(final SeService pSeService) {
    this.item = pSeService;
    if (this.itsId == null) {
      this.itsId = new SeServicePlaceId();
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
