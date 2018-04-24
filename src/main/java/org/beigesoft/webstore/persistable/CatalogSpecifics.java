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
import org.beigesoft.persistable.AHasIdLong;

/**
 * <p>
 * Filterable/orderable specifics that are used for items
 * in that catalog and its sub-catalogs.
 * It's used to make filter/order for item's list.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CatalogSpecifics extends AHasIdLong
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
}
