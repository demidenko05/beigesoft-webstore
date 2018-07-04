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
 * Model of ID of Catalog that contains of Service.
 * </p>
 *
 * @author Yury Demidenko
 */
public class ServiceCatalogId {

  /**
   * <p>Service Catalog, not null, its hasSubcatalogs=false.</p>
   **/
  private CatalogGs itsCatalog;

  /**
   * <p>Service, not null.</p>
   **/
  private ServiceToSale service;

  /**
   * <p>Minimal constructor.</p>
   **/
  public ServiceCatalogId() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pCatalog reference
   * @param pService reference
   **/
  public ServiceCatalogId(final CatalogGs pCatalog,
    final ServiceToSale pService) {
    this.service = pService;
    this.itsCatalog = pCatalog;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for itsCatalog.</p>
   * @return CatalogGs
   **/
  public final CatalogGs getItsCatalog() {
    return this.itsCatalog;
  }

  /**
   * <p>Setter for itsCatalog.</p>
   * @param pCatalog reference
   **/
  public final void setItsCatalog(final CatalogGs pCatalog) {
    this.itsCatalog = pCatalog;
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
