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

import org.beigesoft.accounting.persistable.InvItem;

/**
 * <pre>
 * Model of ID of Catalog that contains of Goods or its subcatalogs.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class GoodsCatalogsId {

  /**
   * <p>Goods Catalog, not null, its hasSubcatalogs=false.</p>
   **/
  private CatalogGs itsCatalog;

  /**
   * <p>Goods, not null.</p>
   **/
  private InvItem goods;

  /**
   * <p>Minimal constructor.</p>
   **/
  public GoodsCatalogsId() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pCatalog reference
   * @param pGoods reference
   **/
  public GoodsCatalogsId(final CatalogGs pCatalog,
    final InvItem pGoods) {
    this.goods = pGoods;
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
   * <p>Getter for goods.</p>
   * @return InvItem
   **/
  public final InvItem getGoods() {
    return this.goods;
  }

  /**
   * <p>Setter for goods.</p>
   * @param pGoods reference
   **/
  public final void setGoods(final InvItem pGoods) {
    this.goods = pGoods;
  }
}
