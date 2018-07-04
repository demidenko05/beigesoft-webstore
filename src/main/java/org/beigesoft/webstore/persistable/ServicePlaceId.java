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

import org.beigesoft.accounting.persistable.ServiceToSale;

/**
 * <p>
 * Model of ID of ServicePlace.
 * </p>
 *
 * @author Yury Demidenko
 */
public class ServicePlaceId {

  /**
   * <p>Service pickUpPlace.</p>
   **/
  private PickUpPlace pickUpPlace;

  /**
   * <p>Service, not null.</p>
   **/
  private ServiceToSale service;


  /**
   * <p>Minimal constructor.</p>
   **/
  public ServicePlaceId() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pPickUpPlace reference
   * @param pService reference
   **/
  public ServicePlaceId(final PickUpPlace pPickUpPlace,
    final ServiceToSale pService) {
    this.service = pService;
    this.pickUpPlace = pPickUpPlace;
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
   * <p>Setter for pickUpPlace.</p>
   * @param pPickUpPlace reference
   **/
  public final void setPickUpPlace(final PickUpPlace pPickUpPlace) {
    this.pickUpPlace = pPickUpPlace;
  }

  /**
   * <p>Getter for service.</p>
   * @return ServiceToSale
   **/
  public final ServiceToSale getService() {
    return this.service;
  }

  /**
   * <p>Setter for service.</p>
   * @param pService reference
   **/
  public final void setService(final ServiceToSale pService) {
    this.service = pService;
  }
}
