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
 * Model of ID of Specifics values for a Service.
 * </p>
 *
 * @author Yury Demidenko
 */
public class ServiceSpecificsId {

  /**
   * <p>Service specifics.</p>
   **/
  private SpecificsOfItem specifics;

  /**
   * <p>Service, not null.</p>
   **/
  private ServiceToSale service;


  /**
   * <p>Minimal constructor.</p>
   **/
  public ServiceSpecificsId() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pSpecifics reference
   * @param pService reference
   **/
  public ServiceSpecificsId(final SpecificsOfItem pSpecifics,
    final ServiceToSale pService) {
    this.service = pService;
    this.specifics = pSpecifics;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for specifics.</p>
   * @return SpecificsOfItem
   **/
  public final SpecificsOfItem getSpecifics() {
    return this.specifics;
  }

  /**
   * <p>Setter for specifics.</p>
   * @param pSpecifics reference
   **/
  public final void setSpecifics(final SpecificsOfItem pSpecifics) {
    this.specifics = pSpecifics;
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
