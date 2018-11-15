package org.beigesoft.webstore.persistable;

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

import org.beigesoft.model.IOwned;
import org.beigesoft.webstore.persistable.base.ATaxLn;

/**
 * <p>
 * Cart item's tax line for item basis multi-taxes non-aggregate rate.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CartItTxLn extends ATaxLn implements IOwned<CartLn> {

  /**
   * <p>Shopping CartLn.</p>
   **/
  private CartLn itsOwner;

  /**
   * <p>SeSeller ID which items presents in cart,
   * NULL means web-store owner's items.
   * It duplicates owner's seller for performance purposes.</p>
   **/
  private Long sellerId;

  /**
   * <p>Cart ID (to improve performance).</p>
   **/
  private Long cartId;

  /**
   * <p>Do not show in cart, it's for performance,
   * old purchased cart emptied with this flag,
   * when buyer add new goods to cart then it's used any disabled
   * line (if exist) otherwise new line will be created.</p>
   **/
  private Boolean disab;

  /**
   * <p>Getter for itsOwner.</p>
   * @return CartLn
   **/
  @Override
  public final CartLn getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final CartLn pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //SGS:
  /**
   * <p>Getter for disab.</p>
   * @return Boolean
   **/
  public final Boolean getDisab() {
    return this.disab;
  }

  /**
   * <p>Setter for disab.</p>
   * @param pDisab reference
   **/
  public final void setDisab(final Boolean pDisab) {
    this.disab = pDisab;
  }

  /**
   * <p>Getter for cartId.</p>
   * @return Long
   **/
  public final Long getCartId() {
    return this.cartId;
  }

  /**
   * <p>Setter for cartId.</p>
   * @param pCartId reference
   **/
  public final void setCartId(final Long pCartId) {
    this.cartId = pCartId;
  }

  /**
   * <p>Getter for sellerId.</p>
   * @return Long
   **/
  public final Long getSellerId() {
    return this.sellerId;
  }

  /**
   * <p>Setter for sellerId.</p>
   * @param pSellerId reference
   **/
  public final void setSellerId(final Long pSellerId) {
    this.sellerId = pSellerId;
  }
}
