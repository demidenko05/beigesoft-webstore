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

/**
 * <pre>
 * Model of Catalog Of Goods/services to print on page.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class TradingCatalog  {

  /**
   * <p>ID.</p>
   **/
  private Long itsId;

  /**
   * <p>Name.</p>
   **/
  private String itsName;

  /**
   * <p>URL.</p>
   **/
  private String itsLink;

  /**
   * <p>Subcatalogs.</p>
   **/
  private List<TradingCatalog> subcatalogs;

  //Simple getters and setters:
  /**
   * <p>Getter for itsId.</p>
   * @return Long
   **/
  public final Long getItsId() {
    return this.itsId;
  }

  /**
   * <p>Setter for itsId.</p>
   * @param pItsId reference
   **/
  public final void setItsId(final Long pItsId) {
    this.itsId = pItsId;
  }

  /**
   * <p>Getter for itsName.</p>
   * @return String
   **/
  public final String getItsName() {
    return this.itsName;
  }

  /**
   * <p>Setter for itsName.</p>
   * @param pItsName reference
   **/
  public final void setItsName(final String pItsName) {
    this.itsName = pItsName;
  }

  /**
   * <p>Getter for itsLink.</p>
   * @return String
   **/
  public final String getItsLink() {
    return this.itsLink;
  }

  /**
   * <p>Setter for itsLink.</p>
   * @param pItsLink reference
   **/
  public final void setItsLink(final String pItsLink) {
    this.itsLink = pItsLink;
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
