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

import org.beigesoft.model.IHasName;
import org.beigesoft.webstore.persistable.CatalogGs;

/**
 * <p>
 * Model of Catalog that contains of Item (Goods/Service/SeGoods/SeService).
 * </p>
 *
 * @param <T> item type
 * @author Yury Demidenko
 */
public class ItemCatalogId<T extends IHasName> {

  /**
   * <p>Item Catalog, not null, its hasSubcatalogs=false.</p>
   **/
  private CatalogGs itsCatalog;

  /**
   * <p>Item, not null.</p>
   **/
  private T item;

  /**
   * <p>Minimal constructor.</p>
   **/
  public ItemCatalogId() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pCatalog reference
   * @param pItem reference
   **/
  public ItemCatalogId(final CatalogGs pCatalog,
    final T pItem) {
    this.item = pItem;
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
   * <p>Getter for item.</p>
   * @return T
   **/
  public final T getItem() {
    return this.item;
  }

  /**
   * <p>Setter for item.</p>
   * @param pItem reference
   **/
  public final void setItem(final T pItem) {
    this.item = pItem;
  }
}
