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

import org.beigesoft.webstore.persistable.base.AItemCatalog;
import org.beigesoft.accounting.persistable.InvItem;

/**
 * <p>
 * Model of Catalog that contains of Goods.
 * </p>
 *
 * @author Yury Demidenko
 */
public class GoodsCatalog extends AItemCatalog<InvItem, GoodsCatalogId> {

  /**
   * <p>ID.</p>
   **/
  private GoodsCatalogId itsId = new GoodsCatalogId();

  /**
   * <p>Goods.</p>
   **/
  private InvItem item;

  /**
   * <p>Item Catalog, not null, its hasSubitsCatalogs=false.</p>
   **/
  private CatalogGs itsCatalog;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return GoodsCatalogId model ID
   **/
  @Override
  public final GoodsCatalogId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final GoodsCatalogId pItsId) {
    this.itsId = pItsId;
    if (this.itsId != null) {
      this.itsCatalog = this.itsId.getItsCatalog();
      setItem(this.itsId.getItem());
    } else {
      this.itsCatalog = null;
      setItem(null);
    }
  }

  /**
   * <p>Setter for pCatalog.</p>
   * @param pCatalog reference
   **/
  public final void setItsCatalog(final CatalogGs pCatalog) {
    this.itsCatalog = pCatalog;
    if (this.itsId == null) {
      this.itsId = new GoodsCatalogId();
    }
    this.itsId.setItsCatalog(this.itsCatalog);
  }

  /**
   * <p>Getter for pCatalog.</p>
   * @return pCatalog reference
   **/
  public final CatalogGs getItsCatalog() {
    return this.itsCatalog;
  }

  /**
   * <p>Getter for item.</p>
   * @return InvItem
   **/
  @Override
  public final InvItem getItem() {
    return this.item;
  }

  /**
   * <p>Setter for item.</p>
   * @param pItem reference
   **/
  @Override
  public final void setItem(final InvItem pItem) {
    this.item = pItem;
    if (getItsId() == null) {
      setItsId(new GoodsCatalogId());
    }
    getItsId().setItem(this.item);
  }
}
