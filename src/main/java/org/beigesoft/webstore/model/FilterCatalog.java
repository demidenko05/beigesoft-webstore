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

import java.util.List;
import java.util.ArrayList;

import org.beigesoft.webstore.persistable.CatalogGs;

/**
 * <p>
 * Filter of catalog/s.
 * </p>
 *
 * @author Yury Demidenko
 */
public class FilterCatalog {

  /**
   * <p>Operator.</p>
   **/
  private EFilterOperator operator;

  /**
   * <p>All available catalogs.</p>
   **/
  private List<CatalogGs> catalogsAll = new ArrayList<CatalogGs>();

  /**
   * <p>Chosen catalogs.</p>
   **/
  private List<CatalogGs> catalogs = new ArrayList<CatalogGs>();

  //Simple getters and setters:
  /**
   * <p>Getter for operator.</p>
   * @return EFilterOperator
   **/
  public final EFilterOperator getOperator() {
    return this.operator;
  }

  /**
   * <p>Setter for operator.</p>
   * @param pOperator reference
   **/
  public final void setOperator(final EFilterOperator pOperator) {
    this.operator = pOperator;
  }

  /**
   * <p>Getter for catalogsAll.</p>
   * @return List<CatalogGs>
   **/
  public final List<CatalogGs> getCatalogsAll() {
    return this.catalogsAll;
  }

  /**
   * <p>Setter for catalogsAll.</p>
   * @param pCatalogsAll reference
   **/
  public final void setCatalogsAll(final List<CatalogGs> pCatalogsAll) {
    this.catalogsAll = pCatalogsAll;
  }

  /**
   * <p>Getter for catalogs.</p>
   * @return List<CatalogGs>
   **/
  public final List<CatalogGs> getCatalogs() {
    return this.catalogs;
  }

  /**
   * <p>Setter for catalogs.</p>
   * @param pCatalogs reference
   **/
  public final void setCatalogs(final List<CatalogGs> pCatalogs) {
    this.catalogs = pCatalogs;
  }
}
