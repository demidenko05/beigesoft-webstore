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
import java.math.BigDecimal;

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;
import org.beigesoft.accounting.persistable.ServiceToSale;

/**
 * <p>
 * Model of Service Available - hold availability of a service at pickup place.
 * </p>
 *
 * @author Yury Demidenko
 */
public class ServicePlace extends AEditableHasVersion
  implements IHasId<ServicePlaceId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private ServicePlaceId itsId = new ServicePlaceId();

  /**
   * <p>Pick up (storage) place, not null.</p>
   **/
  private PickUpPlace pickUpPlace;

  /**
   * <p>Service, not null.</p>
   **/
  private ServiceToSale service;

  /**
   * <p>if present in hundred meters, i.e. 1 means 100meters.</p>
   **/
  private Long distance;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final ServicePlaceId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final ServicePlaceId pItsId) {
    this.itsId = pItsId;
    if (this.itsId != null) {
      this.pickUpPlace = this.itsId.getPickUpPlace();
      this.service = this.itsId.getService();
    } else {
      this.pickUpPlace = null;
      this.service = null;
    }
  }

  /**
   * <p>Setter for pickUpPlace.</p>
   * @param pPickUpPlace reference
   **/
  public final void setPickUpPlace(final PickUpPlace pPickUpPlace) {
    this.pickUpPlace = pPickUpPlace;
    if (this.itsId == null) {
      this.itsId = new ServicePlaceId();
    }
    this.itsId.setPickUpPlace(this.pickUpPlace);
  }

  /**
   * <p>Setter for service.</p>
   * @param pService reference
   **/
  public final void setService(final ServiceToSale pService) {
    this.service = pService;
    if (this.itsId == null) {
      this.itsId = new ServicePlaceId();
    }
    this.itsId.setService(this.service);
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
