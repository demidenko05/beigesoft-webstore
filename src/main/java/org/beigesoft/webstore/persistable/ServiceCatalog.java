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

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;
import org.beigesoft.accounting.persistable.ServiceToSale;

/**
 * <p>
 * Model of Catalog that contains of Service.
 * </p>
 *
 * @author Yury Demidenko
 */
public class ServiceCatalog extends AEditableHasVersion
  implements IHasId<ServiceCatalogId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private ServiceCatalogId itsId = new ServiceCatalogId();

  /**
   * <p>Service Catalog, not null, its hasSubitsCatalog=false.</p>
   **/
  private CatalogGs itsCatalog;

  /**
   * <p>Service, not null.</p>
   **/
  private ServiceToSale service;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final ServiceCatalogId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final ServiceCatalogId pItsId) {
    this.itsId = pItsId;
    if (this.itsId != null) {
      this.itsCatalog = this.itsId.getItsCatalog();
      this.service = this.itsId.getService();
    } else {
      this.itsCatalog = null;
      this.service = null;
    }
  }

  /**
   * <p>Setter for pCatalog.</p>
   * @param pCatalog reference
   **/
  public final void setItsCatalog(final CatalogGs pCatalog) {
    this.itsCatalog = pCatalog;
    if (this.itsId == null) {
      this.itsId = new ServiceCatalogId();
    }
    this.itsId.setItsCatalog(this.itsCatalog);
  }

  /**
   * <p>Setter for service.</p>
   * @param pService reference
   **/
  public final void setService(final ServiceToSale pService) {
    this.service = pService;
    if (this.itsId == null) {
      this.itsId = new ServiceCatalogId();
    }
    this.itsId.setService(this.service);
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
   * <p>Getter for service.</p>
   * @return ServiceToSale
   **/
  public final ServiceToSale getService() {
    return this.service;
  }
}
