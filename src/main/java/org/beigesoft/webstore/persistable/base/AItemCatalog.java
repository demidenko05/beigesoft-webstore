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
import org.beigesoft.model.IHasIdName;
import org.beigesoft.webstore.persistable.CatalogGs;

/**
 * <p>
 * Model of Catalog that contains of Item (Goods/Service/SeGoods/SeService).
 * BeigeORM not support generic fields!
 * </p>
 *
 * @param <T> item type
 * @param <ID> ID type
 * @author Yury Demidenko
 */
public abstract class
  AItemCatalog<T extends IHasIdName<Long>, ID extends AItemCatalogId<T>>
    extends AEditableHasVersion implements IHasId<ID> {

  /**
   * <p>Setter for pCatalog.</p>
   * @param pCatalog reference
   **/
  public abstract void setItsCatalog(CatalogGs pCatalog);

  /**
   * <p>Getter for itsCatalog.</p>
   * @return CatalogGs
   **/
  public abstract CatalogGs getItsCatalog();

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
}
