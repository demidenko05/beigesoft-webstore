package org.beigesoft.webstore.model;

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

import java.util.List;
import java.util.ArrayList;

import org.beigesoft.webstore.persistable.CatalogGs;

/**
 * <pre>
 * Model of Catalog Of Goods/services to print on page.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class TradingCatalog  {

  /**
   * <p>Persistable catalog.</p>
   **/
  private CatalogGs catalog;

  /**
   * <p>Subcatalogs.</p>
   **/
  private List<TradingCatalog> subcatalogs = new ArrayList<TradingCatalog>();

  //Simple getters and setters:
  /**
   * <p>Getter for catalog.</p>
   * @return CatalogGs
   **/
  public final CatalogGs getCatalog() {
    return this.catalog;
  }

  /**
   * <p>Setter for catalog.</p>
   * @param pCatalog reference
   **/
  public final void setCatalog(final CatalogGs pCatalog) {
    this.catalog = pCatalog;
  }

  /**
   * <p>Getter for subcatalogs.</p>
   * @return List<TradingCatalog>
   **/
  public final List<TradingCatalog> getSubcatalogs() {
    return this.subcatalogs;
  }

  /**
   * <p>Setter for subcatalogs.</p>
   * @param pSubcatalogs reference
   **/
  public final void setSubcatalogs(final List<TradingCatalog> pSubcatalogs) {
    this.subcatalogs = pSubcatalogs;
  }
}
