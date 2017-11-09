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

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;

/**
 * <pre>
 * Model of Catalog that contains of Subcatalogs of Goods/Services.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class SubcatalogsCatalogsGs extends AEditableHasVersion
  implements IHasId<SubcatalogsCatalogsGsId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private SubcatalogsCatalogsGsId itsId = new SubcatalogsCatalogsGsId();

  /**
   * <p>Subcatalogs Catalog, not null, its hasSubcatalogs=true.</p>
   **/
  private CatalogGs itsCatalog;

  /**
   * <p>Subatalog, not null.</p>
   **/
  private CatalogGs subcatalog;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final SubcatalogsCatalogsGsId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pId model ID
   **/
  @Override
  public final void setItsId(final SubcatalogsCatalogsGsId pItsId) {
    this.itsId = pItsId;
    if (this.itsId != null) {
      this.itsCatalog = this.itsId.getItsCatalog();
      this.subcatalog = this.itsId.getSubcatalog();
    } else {
      this.itsCatalog = null;
      this.subcatalog = null;
    }
  }

  /**
   * <p>Setter for itsCatalog.</p>
   * @param pCatalog reference
   **/
  public final void setItsCatalog(final CatalogGs pCatalog) {
    this.itsCatalog = pCatalog;
    if (this.itsId == null) {
      this.itsId = new SubcatalogsCatalogsGsId();
    }
    this.itsId.setItsCatalog(this.itsCatalog);
  }

  /**
   * <p>Setter for subcatalog.</p>
   * @param pSubcatalog reference
   **/
  public final void setSubcatalog(final CatalogGs pSubcatalog) {
    this.subcatalog = pSubcatalog;
    if (this.itsId == null) {
      this.itsId = new SubcatalogsCatalogsGsId();
    }
    this.itsId.setSubcatalog(this.subcatalog);
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
   * <p>Getter for subcatalog.</p>
   * @return CatalogGs
   **/
  public final CatalogGs getSubcatalog() {
    return this.subcatalog;
  }
}
