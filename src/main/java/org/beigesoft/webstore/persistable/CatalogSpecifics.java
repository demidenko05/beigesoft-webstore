package org.beigesoft.webstore.persistable;

/*
 * Copyright (c) 2018 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import org.beigesoft.model.IOwned;
import org.beigesoft.persistable.AHasIdLongVersion;

/**
 * <p>
 * Filterable/orderable specifics that are used for items
 * in that catalog and its sub-catalogs.
 * It's used to make filter/order for item's list.
 * It's made either by hand - admin add FO specifics to a catalog,
 * or by service that checked items with FO specifics and added to catalog.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CatalogSpecifics extends AHasIdLongVersion
  implements IOwned<CatalogGs> {

  /**
   * <p>Catalog.</p>
   **/
  private CatalogGs itsOwner;

  /**
   * <p>Used filterable/orderable specifics.</p>
   **/
  private SpecificsOfItem specifics;

  /**
   * <p>If used, means ID of customized filter, e.g. "231" means
   * using custom filter231.jsp for RAM size (set of size ranges)
   * instead of regular(usual/default) filter
   * integer (less, greater, from-to value1/2).</p>
   **/
  private Integer filterId;

  /**
   * <p>Getter for itsOwner.</p>
   * @return CatalogGs
   **/
  @Override
  public final CatalogGs getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final CatalogGs pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //SGS:
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
   * <p>Getter for filterId.</p>
   * @return Integer
   **/
  public final Integer getFilterId() {
    return this.filterId;
  }

  /**
   * <p>Setter for filterId.</p>
   * @param pFilterId reference
   **/
  public final void setFilterId(final Integer pFilterId) {
    this.filterId = pFilterId;
  }
}
