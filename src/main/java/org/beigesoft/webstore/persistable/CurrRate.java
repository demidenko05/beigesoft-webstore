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

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;
import org.beigesoft.accounting.persistable.Currency;

/**
 * <p>
 * Holds accepted foreign currency rates.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CurrRate extends AEditableHasVersion implements IHasId<Currency> {

  /**
   * <p>Currency, PK.</p>
   **/
  private Currency curr;

  /**
   * <p>Rate, not null.</p>
   **/
  private BigDecimal rate;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final Currency getItsId() {
    return this.curr;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final Currency pItsId) {
    this.curr = pItsId;
  }

  //Simple getters and setters:
  /**
   * <p>Setter for curr.</p>
   * @param pCurr reference
   **/
  public final void setCurr(final Currency pCurr) {
    this.curr = pCurr;
  }

  /**
   * <p>Getter for curr.</p>
   * @return Currency
   **/
  public final Currency getCurr() {
    return this.curr;
  }

  /**
   * <p>Getter for rate.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getRate() {
    return this.rate;
  }

  /**
   * <p>Setter for rate.</p>
   * @param pRate reference
   **/
  public final void setRate(final BigDecimal pRate) {
    this.rate = pRate;
  }
}
