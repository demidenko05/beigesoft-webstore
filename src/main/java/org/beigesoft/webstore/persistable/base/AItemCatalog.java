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

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;
import org.beigesoft.model.IHasName;
import org.beigesoft.webstore.persistable.CatalogGs;

/**
 * <p>
 * Model of Catalog that contains of Item (Goods/Service/SeGoods/SeService).
 * BeigeORM not support generic fields!
 * </p>
 *
 * @param <T> item type
 * @author Yury Demidenko
 */
public abstract class AItemCatalog<T extends IHasName>
  extends AEditableHasVersion implements IHasId<ItemCatalogId<T>> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private ItemCatalogId<T> itsId = new ItemCatalogId<T>();

  /**
   * <p>Item Catalog, not null, its hasSubitsCatalogs=false.</p>
   **/
  private CatalogGs itsCatalog;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final ItemCatalogId<T> getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final ItemCatalogId<T> pItsId) {
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
      this.itsId = new ItemCatalogId<T>();
    }
    this.itsId.setItsCatalog(this.itsCatalog);
  }

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
}
