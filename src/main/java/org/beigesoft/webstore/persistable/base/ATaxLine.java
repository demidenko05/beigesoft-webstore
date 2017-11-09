package org.beigesoft.webstore.persistable.base;

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

import org.beigesoft.persistable.AHasIdLongVersion;
import org.beigesoft.accounting.persistable.Tax;

/**
 * <p>
 * Abstraction of cart/order tax Line.
 * Version, reliable autoincrement algorithm.
 * </p>
 *
 * @author Yury Demidenko
 */
public abstract class ATaxLine extends AHasIdLongVersion {

  /**
   * <p>Tax.</p>
   **/
  private Tax tax;

  /**
   * <p>Total taxes.</p>
   **/
  private BigDecimal itsTotal = BigDecimal.ZERO;

  //Simple getters and setters:
  /**
   * <p>Getter for tax.</p>
   * @return Tax
   **/
  public final Tax getTax() {
    return this.tax;
  }

  /**
   * <p>Setter for tax.</p>
   * @param pTax reference
   **/
  public final void setTax(final Tax pTax) {
    this.tax = pTax;
  }

  /**
   * <p>Geter for itsTotal.</p>
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
}
