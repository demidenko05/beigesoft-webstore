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

import java.math.BigDecimal;

import org.beigesoft.model.IOwned;
import org.beigesoft.persistable.AHasIdLongVersion;

/**
 * <p>
 * Model of Cart totals grouped by seller.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CartTot extends AHasIdLongVersion implements IOwned<Cart> {

  /**
   * <p>Shopping Cart.</p>
   **/
  private Cart itsOwner;

  /**
   * <p>Do not show in cart, it's for performance,
   * old purchased cart emptied with this flag,
   * when buyer add new goods to cart then it's used any disabled
   * line (if exist) otherwise new line will be created.</p>
   **/
  private Boolean disab;

  /**
   * <p>S.E.Seller which item presents in cart,
   * NULL means web-store owner's item.</p>
   **/
  private SeSeller seller;

  /**
   * <p>Subtotal without taxes.</p>
   **/
  private BigDecimal subt;

  /**
   * <p>Total taxes.</p>
   **/
  private BigDecimal totTx;

  /**
   * <p>Total, not null.</p>
   **/
  private BigDecimal tot;

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

  //Simple getters and setters:
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
   * <p>Getter for subt.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getSubt() {
    return this.subt;
  }

  /**
   * <p>Setter for subt.</p>
   * @param pSubt reference
   **/
  public final void setSubt(final BigDecimal pSubt) {
    this.subt = pSubt;
  }

  /**
   * <p>Getter for totTx.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTotTx() {
    return this.totTx;
  }

  /**
   * <p>Setter for totTx.</p>
   * @param pTotTx reference
   **/
  public final void setTotTx(final BigDecimal pTotTx) {
    this.totTx = pTotTx;
  }

  /**
   * <p>Getter for tot.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTot() {
    return this.tot;
  }

  /**
   * <p>Setter for tot.</p>
   * @param pTot reference
   **/
  public final void setTot(final BigDecimal pTot) {
    this.tot = pTot;
  }
}
