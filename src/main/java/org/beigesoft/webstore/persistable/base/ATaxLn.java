package org.beigesoft.webstore.persistable.base;

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
public abstract class ATaxLn extends AHasIdLongVersion {

  /**
   * <p>Tax.</p>
   **/
  private Tax tax;

  /**
   * <p>Total taxes.</p>
   **/
  private BigDecimal tot = BigDecimal.ZERO;

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
