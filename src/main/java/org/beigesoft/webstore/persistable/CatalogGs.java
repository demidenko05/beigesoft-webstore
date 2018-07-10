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

import java.util.List;

import org.beigesoft.persistable.AHasNameIdLongVersion;

/**
 * <pre>
 * Model of catalog of goods/services.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class CatalogGs extends AHasNameIdLongVersion {

  /**
   * <p>If has subcatalogs, not null, false default.</p>
   **/
  private Boolean hasSubcatalogs = false;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Ordering.</p>
   **/
  private Integer itsIndex;

  /**
   * <p>Is it in the menu, default true, to quick switch on/off from menu
   * or for catalog that shows only on start.</p>
   **/
  private Boolean isInMenu = true;

  /**
   * <p>Use filter specifics for this catalog/sub-catalogs.</p>
   **/
  private Boolean useFilterSpecifics = false;

  /**
   * <p>Use filter sub-catalogs for this catalog/sub-catalogs.</p>
   **/
  private Boolean useFilterSubcatalog = false;

  /**
   * <p>Use pickup place filter for this catalog/sub-catalogs.</p>
   **/
  private Boolean usePickupPlaceFilter = false;

  /**
   * <p>Use availability filter for this catalog/sub-catalogs.</p>
   **/
  private Boolean useAvailableFilter = false;

  /**
   * <p>List of filterable/orderable specifics that are used for items
   * in that catalog and its sub-catalogs.
   * It's used to make filter/order for item's list.</p>
   **/
  private List<CatalogSpecifics> usedSpecifics;

  /**
   * <p>If used, means ID of customized filter, e.g. "231" means
   * using custom filterPrice231.jsp for used car (set of price ranges)
   * instead of regular(usual/default) filter
   * integer (less, greater, from-to value1/2).</p>
   **/
  private Integer filterPriceId;

  /**
   * <p>Contains of goods.</p>
   **/
  private Boolean hasGoods = false;

  /**
   * <p>Contains of services.</p>
   **/
  private Boolean hasServices = false;

  /**
   * <p>Contains of S.E. goods.</p>
   **/
  private Boolean hasSeGoods = false;

  /**
   * <p>Contains of S.E. services.</p>
   **/
  private Boolean hasSeServices = false;

  //Simple getters and setters:
  /**
   * <p>Getter for description.</p>
   * @return String
   **/
  public final String getDescription() {
    return this.description;
  }

  /**
   * <p>Setter for description.</p>
   * @param pDescription reference
   **/
  public final void setDescription(final String pDescription) {
    this.description = pDescription;
  }

  /**
   * <p>Getter for hasSubcatalogs.</p>
   * @return Boolean
   **/
  public final Boolean getHasSubcatalogs() {
    return this.hasSubcatalogs;
  }

  /**
   * <p>Setter for hasSubcatalogs.</p>
   * @param pHasSubcatalogs reference
   **/
  public final void setHasSubcatalogs(final Boolean pHasSubcatalogs) {
    this.hasSubcatalogs = pHasSubcatalogs;
  }

  /**
   * <p>Getter for itsIndex.</p>
   * @return Integer
   **/
  public final Integer getItsIndex() {
    return this.itsIndex;
  }

  /**
   * <p>Setter for itsIndex.</p>
   * @param pItsIndex reference
   **/
  public final void setItsIndex(final Integer pItsIndex) {
    this.itsIndex = pItsIndex;
  }

  /**
   * <p>Getter for isInMenu.</p>
   * @return Boolean
   **/
  public final Boolean getIsInMenu() {
    return this.isInMenu;
  }

  /**
   * <p>Setter for isInMenu.</p>
   * @param pIsInMenu reference
   **/
  public final void setIsInMenu(final Boolean pIsInMenu) {
    this.isInMenu = pIsInMenu;
  }

  /**
   * <p>Getter for useFilterSpecifics.</p>
   * @return Boolean
   **/
  public final Boolean getUseFilterSpecifics() {
    return this.useFilterSpecifics;
  }

  /**
   * <p>Setter for useFilterSpecifics.</p>
   * @param pUseFilterSpecifics reference
   **/
  public final void setUseFilterSpecifics(final Boolean pUseFilterSpecifics) {
    this.useFilterSpecifics = pUseFilterSpecifics;
  }

  /**
   * <p>Getter for useFilterSubcatalog.</p>
   * @return Boolean
   **/
  public final Boolean getUseFilterSubcatalog() {
    return this.useFilterSubcatalog;
  }

  /**
   * <p>Setter for useFilterSubcatalog.</p>
   * @param pUseFilterSubcatalog reference
   **/
  public final void setUseFilterSubcatalog(final Boolean pUseFilterSubcatalog) {
    this.useFilterSubcatalog = pUseFilterSubcatalog;
  }

  /**
   * <p>Getter for usePickupPlaceFilter.</p>
   * @return Boolean
   **/
  public final Boolean getUsePickupPlaceFilter() {
    return this.usePickupPlaceFilter;
  }

  /**
   * <p>Setter for usePickupPlaceFilter.</p>
   * @param pUsePickupPlaceFilter reference
   **/
  public final void setUsePickupPlaceFilter(
    final Boolean pUsePickupPlaceFilter) {
    this.usePickupPlaceFilter = pUsePickupPlaceFilter;
  }

  /**
   * <p>Getter for useAvailableFilter.</p>
   * @return Boolean
   **/
  public final Boolean getUseAvailableFilter() {
    return this.useAvailableFilter;
  }

  /**
   * <p>Setter for useAvailableFilter.</p>
   * @param pUseAvailableFilter reference
   **/
  public final void setUseAvailableFilter(final Boolean pUseAvailableFilter) {
    this.useAvailableFilter = pUseAvailableFilter;
  }

  /**
   * <p>Getter for usedSpecifics.</p>
   * @return List<CatalogSpecifics>
   **/
  public final List<CatalogSpecifics> getUsedSpecifics() {
    return this.usedSpecifics;
  }

  /**
   * <p>Setter for usedSpecifics.</p>
   * @param pUsedSpecifics reference
   **/
  public final void setUsedSpecifics(
    final List<CatalogSpecifics> pUsedSpecifics) {
    this.usedSpecifics = pUsedSpecifics;
  }

  /**
   * <p>Getter for filterPriceId.</p>
   * @return Integer
   **/
  public final Integer getFilterPriceId() {
    return this.filterPriceId;
  }

  /**
   * <p>Setter for filterPriceId.</p>
   * @param pFilterPriceId reference
   **/
  public final void setFilterPriceId(final Integer pFilterPriceId) {
    this.filterPriceId = pFilterPriceId;
  }

  /**
   * <p>Getter for hasGoods.</p>
   * @return Boolean
   **/
  public final Boolean getHasGoods() {
    return this.hasGoods;
  }

  /**
   * <p>Setter for hasGoods.</p>
   * @param pHasGoods reference
   **/
  public final void setHasGoods(final Boolean pHasGoods) {
    this.hasGoods = pHasGoods;
  }

  /**
   * <p>Getter for hasServices.</p>
   * @return Boolean
   **/
  public final Boolean getHasServices() {
    return this.hasServices;
  }

  /**
   * <p>Setter for hasServices.</p>
   * @param pHasServices reference
   **/
  public final void setHasServices(final Boolean pHasServices) {
    this.hasServices = pHasServices;
  }

  /**
   * <p>Getter for hasSeGoods.</p>
   * @return Boolean
   **/
  public final Boolean getHasSeGoods() {
    return this.hasSeGoods;
  }

  /**
   * <p>Setter for hasSeGoods.</p>
   * @param pHasSeGoods reference
   **/
  public final void setHasSeGoods(final Boolean pHasSeGoods) {
    this.hasSeGoods = pHasSeGoods;
  }

  /**
   * <p>Getter for hasSeServices.</p>
   * @return Boolean
   **/
  public final Boolean getHasSeServices() {
    return this.hasSeServices;
  }

  /**
   * <p>Setter for hasSeServices.</p>
   * @param pHasSeServices reference
   **/
  public final void setHasSeServices(final Boolean pHasSeServices) {
    this.hasSeServices = pHasSeServices;
  }
}
