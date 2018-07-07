package org.beigesoft.webstore.persistable.base;

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

import org.beigesoft.model.IHasIdLongVersion;
import org.beigesoft.webstore.persistable.CatalogGs;

/**
 * <p>
 * Model of Catalog that contains of Item (Goods/Service/SeGoods/SeService).
 * </p>
 *
 * @param <T> item type
 * @author Yury Demidenko
 */
public abstract class AItemCatalogId<T extends IHasIdLongVersion> {

  /**
   * <p>Item Catalog, not null, its hasSubcatalogs=false.</p>
   **/
  private CatalogGs itsCatalog;

  /**
   * <p>Getter for item.</p>
   * @return T
   **/
  public abstract T getItem();
  /**
   * <p>Setter for item.</p>
   * @param pItem reference
   **/
  public abstract void setItem(final T pItem);

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
}
