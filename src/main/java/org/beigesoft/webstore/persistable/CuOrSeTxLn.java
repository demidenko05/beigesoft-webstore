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
 * S.E. Customer Order Tax Line model.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CuOrSeTxLn extends ATaxLn implements IOwned<CuOrSe> {

  /**
   * <p>Customer Order.</p>
   **/
  private CuOrSe itsOwner;

  /**
   * <p>Taxable amount for invoice basis, 0 - item basis..</p>
   **/
  private BigDecimal taxab = BigDecimal.ZERO;

  /**
   * <p>Getter for itsOwner.</p>
   * @return CuOrSe
   **/
  @Override
  public final CuOrSe getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final CuOrSe pItsOwner) {
    this.itsOwner = pItsOwner;
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
