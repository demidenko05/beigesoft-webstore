package org.beigesoft.webstore.model;

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

import org.beigesoft.filter.AFilter;
import org.beigesoft.webstore.persistable.CatalogSpecifics;

/**
 * <p>
 * Bundle of specifics and filter.
 * </p>
 *
 * @author Yury Demidenko
 */
public class SpecificsFilter  {

  /**
   * <p>Catalog-Specifics.</p>
   **/
  private CatalogSpecifics catSpec;

  /**
   * <p>Filter.</p>
   **/
  private AFilter filter;

  //Simple getters and setters:
  /**
   * <p>Getter for catSpec.</p>
   * @return CatalogSpecifics
   **/
  public final CatalogSpecifics getCatSpec() {
    return this.catSpec;
  }

  /**
   * <p>Setter for catSpec.</p>
   * @param pCatSpec reference
   **/
  public final void setCatSpec(final CatalogSpecifics pCatSpec) {
    this.catSpec = pCatSpec;
  }

  /**
   * <p>Getter for filter.</p>
   * @return AFilter
   **/
  public final AFilter getFilter() {
    return this.filter;
  }

  /**
   * <p>Setter for filter.</p>
   * @param pFilter reference
   **/
  public final void setFilter(final AFilter pFilter) {
    this.filter = pFilter;
  }
}
