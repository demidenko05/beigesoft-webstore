package org.beigesoft.webstore.persistable;

/*
 * Copyright (c) 2017 Beigesoft™
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
import org.beigesoft.webstore.persistable.base.ATaxLn;

/**
 * <p>
 * Shoping Cart Tax Line model.
 * It holds total taxes grouped by invoice (seller).
 * </p>
 *
 * @author Yury Demidenko
 */
public class CartTxLn extends ATaxLn implements IOwned<Cart> {

  /**
   * <p>Shopping Cart.</p>
   **/
  private Cart itsOwner;

  /**
   * <p>SeSeller which items presents in cart,
   * NULL means web-store owner's items.</p>
   **/
  private SeSeller seller;

  /**
   * <p>Taxable amount for invoice basis, 0 - item basis..</p>
   **/
  private BigDecimal taxab = BigDecimal.ZERO;

  /**
   * <p>Getter for itsOwner.</p>
   * @return Cart
   **/
  @Override
  public final Cart getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final Cart pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //SGS:
  /**
   * <p>Getter for seller.</p>
   * @return SeSeller
   **/
  public final SeSeller getSeller() {
    return this.seller;
  }

  /**
   * <p>Setter for seller.</p>
   * @param pSeller reference
   **/
  public final void setSeller(final SeSeller pSeller) {
    this.seller = pSeller;
  }

  /**
   * <p>Getter for taxab.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTaxab() {
    return this.taxab;
  }

  /**
   * <p>Setter for taxab.</p>
   * @param pTaxab reference
   **/
  public final void setTaxab(final BigDecimal pTaxab) {
    this.taxab = pTaxab;
  }
}
