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

import org.beigesoft.persistable.AHasNameIdLongVersion;

/**
 * <pre>
 * Model of Price Category for Goods/Service and Customer.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class PriceCategory extends AHasNameIdLongVersion {

  /**
   * <p>Price Category for Goods/Service, not null, e.g. "cheap goods".</p>
   **/
  private PriceCategoryOfItems priceCategoryOfItems;

  /**
   * <p>Price Category for Customer,
   * e.g. "rich", if null then used "Price for all".</p>
   **/
  private PriceCategoryOfBuyers priceCategoryOfBuyers;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Only for ordinal shops - non-web.</p>
   **/
  private Boolean isRetailOnly = false;

  //Simple getters and setters:
  /**
   * <p>Getter for priceCategoryOfItems.</p>
   * @return PriceCategoryOfItems
   **/
  public final PriceCategoryOfItems getPriceCategoryOfItems() {
    return this.priceCategoryOfItems;
  }

  /**
   * <p>Setter for priceCategoryOfItems.</p>
   * @param pPriceCategoryOfItems reference
   **/
  public final void setPriceCategoryOfItems(
    final PriceCategoryOfItems pPriceCategoryOfItems) {
    this.priceCategoryOfItems = pPriceCategoryOfItems;
  }

  /**
   * <p>Getter for priceCategoryOfBuyers.</p>
   * @return PriceCategoryOfBuyers
   **/
  public final PriceCategoryOfBuyers getPriceCategoryOfBuyers() {
    return this.priceCategoryOfBuyers;
  }

  /**
   * <p>Setter for priceCategoryOfBuyers.</p>
   * @param pPriceCategoryOfBuyers reference
   **/
  public final void setPriceCategoryOfBuyers(
    final PriceCategoryOfBuyers pPriceCategoryOfBuyers) {
    this.priceCategoryOfBuyers = pPriceCategoryOfBuyers;
  }

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
   * <p>Getter for isRetailOnly.</p>
   * @return Boolean
   **/
  public final Boolean getIsRetailOnly() {
    return this.isRetailOnly;
  }

  /**
   * <p>Setter for isRetailOnly.</p>
   * @param pIsRetailOnly reference
   **/
  public final void setIsRetailOnly(final Boolean pIsRetailOnly) {
    this.isRetailOnly = pIsRetailOnly;
  }
}
