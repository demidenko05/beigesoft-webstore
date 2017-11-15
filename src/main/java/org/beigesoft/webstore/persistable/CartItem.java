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

import java.math.BigDecimal;

import org.beigesoft.model.IOwned;
import org.beigesoft.persistable.AHasNameIdLongVersion;
import org.beigesoft.webstore.model.EShopItemType;

/**
 * <p>
 * Model of CartItem.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CartItem extends AHasNameIdLongVersion
  implements IOwned<ShoppingCart> {

  /**
   * <p>Shopping Cart.</p>
   **/
  private ShoppingCart itsOwner;

  /**
   * <p>Do not show in cart, it's for performance,
   * old purchased cart emptied with this flag,
   * when buyer add new goods to cart then it's used any disabled
   * line (if exist) otherwise new line will be created.</p>
   **/
  private Boolean isDisabled;

  /**
   * <p>Shop Item Type, not null.</p>
   **/
  private EShopItemType itemType;

  /**
   * <p>Item ID, not null.</p>
   **/
  private Long itemId;

  /**
   * <p>Price, not null, grater than zero.</p>
   **/
  private BigDecimal itsPrice;

  /**
   * <p>Quantity, not null.</p>
   **/
  private BigDecimal itsQuantity;

  /**
   * <p>Subtotal without taxes.</p>
   **/
  private BigDecimal subtotal;

  /**
   * <p>Total taxes.</p>
   **/
  private BigDecimal totalTaxes;

  /**
   * <p>Taxes description, uneditable,
   * e.g. "tax1 10%=12, tax2 5%=6".</p>
   **/
  private String taxesDescription;

  /**
   * <p>Total, not null.</p>
   **/
  private BigDecimal itsTotal;

  /**
   * <p>Available quantity, not null.</p>
   **/
  private BigDecimal availableQuantity;

  /**
   * <p>Getter for itsOwner.</p>
   * @return ShoppingCart
   **/
  @Override
  public final ShoppingCart getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final ShoppingCart pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for isDisabled.</p>
   * @return Boolean
   **/
  public final Boolean getIsDisabled() {
    return this.isDisabled;
  }

  /**
   * <p>Setter for isDisabled.</p>
   * @param pIsDisabled reference
   **/
  public final void setIsDisabled(final Boolean pIsDisabled) {
    this.isDisabled = pIsDisabled;
  }

  /**
   * <p>Getter for itemType.</p>
   * @return EShopItemType
   **/
  public final EShopItemType getItemType() {
    return this.itemType;
  }

  /**
   * <p>Setter for itemType.</p>
   * @param pItemType reference
   **/
  public final void setItemType(final EShopItemType pItemType) {
    this.itemType = pItemType;
  }

  /**
   * <p>Getter for itemId.</p>
   * @return Long
   **/
  public final Long getItemId() {
    return this.itemId;
  }

  /**
   * <p>Setter for itemId.</p>
   * @param pItemId reference
   **/
  public final void setItemId(final Long pItemId) {
    this.itemId = pItemId;
  }

  /**
   * <p>Getter for itsPrice.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsPrice() {
    return this.itsPrice;
  }

  /**
   * <p>Setter for itsPrice.</p>
   * @param pItsPrice reference
   **/
  public final void setItsPrice(final BigDecimal pItsPrice) {
    this.itsPrice = pItsPrice;
  }

  /**
   * <p>Getter for itsQuantity.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsQuantity() {
    return this.itsQuantity;
  }

  /**
   * <p>Setter for itsQuantity.</p>
   * @param pItsQuantity reference
   **/
  public final void setItsQuantity(final BigDecimal pItsQuantity) {
    this.itsQuantity = pItsQuantity;
  }

  /**
   * <p>Getter for subtotal.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getSubtotal() {
    return this.subtotal;
  }

  /**
   * <p>Setter for subtotal.</p>
   * @param pSubtotal reference
   **/
  public final void setSubtotal(final BigDecimal pSubtotal) {
    this.subtotal = pSubtotal;
  }

  /**
   * <p>Getter for totalTaxes.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTotalTaxes() {
    return this.totalTaxes;
  }

  /**
   * <p>Setter for totalTaxes.</p>
   * @param pTotalTaxes reference
   **/
  public final void setTotalTaxes(final BigDecimal pTotalTaxes) {
    this.totalTaxes = pTotalTaxes;
  }

  /**
   * <p>Getter for taxesDescription.</p>
   * @return String
   **/
  public final String getTaxesDescription() {
    return this.taxesDescription;
  }

  /**
   * <p>Setter for taxesDescription.</p>
   * @param pTaxesDescription reference
   **/
  public final void setTaxesDescription(final String pTaxesDescription) {
    this.taxesDescription = pTaxesDescription;
  }

  /**
   * <p>Getter for itsTotal.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsTotal() {
    return this.itsTotal;
  }

  /**
   * <p>Setter for itsTotal.</p>
   * @param pItsTotal reference
   **/
  public final void setItsTotal(final BigDecimal pItsTotal) {
    this.itsTotal = pItsTotal;
  }

  /**
   * <p>Getter for availableQuantity.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getAvailableQuantity() {
    return this.availableQuantity;
  }

  /**
   * <p>Setter for availableQuantity.</p>
   * @param pAvailableQuantity reference
   **/
  public final void setAvailableQuantity(final BigDecimal pAvailableQuantity) {
    this.availableQuantity = pAvailableQuantity;
  }
}
