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

import org.beigesoft.persistable.AHasNameIdLongVersion;

/**
 * <p>Holds payment method data.</p>
 *
 * @author Yury Demidenko
 */
public class PayMd extends AHasNameIdLongVersion {

  /**
   * <p>Mode, e.g. PayPal "sandbox".</p>
   **/
  private String mde;

  /**
   * <p>Secret or not phrase 1, e.g. PayPal client ID.</p>
   **/
  private String sec1;

  /**
   * <p>Secret or not phrase 2, e.g. PayPal client secret.</p>
   **/
  private String sec2;

  //Simple getters and setters:
  /**
   * <p>Getter for mde.</p>
   * @return String
   **/
  public final String getMde() {
    return this.mde;
  }

  /**
   * <p>Setter for mde.</p>
   * @param pMde reference
   **/
  public final void setMde(final String pMde) {
    this.mde = pMde;
  }

  /**
   * <p>Getter for sec1.</p>
   * @return String
   **/
  public final String getSec1() {
    return this.sec1;
  }

  /**
   * <p>Setter for sec1.</p>
   * @param pSec1 reference
   **/
  public final void setSec1(final String pSec1) {
    this.sec1 = pSec1;
  }

  /**
   * <p>Getter for sec2.</p>
   * @return String
   **/
  public final String getSec2() {
    return this.sec2;
  }

  /**
   * <p>Setter for sec2.</p>
   * @param pSec2 reference
   **/
  public final void setSec2(final String pSec2) {
    this.sec2 = pSec2;
  }
}
