package org.beigesoft.webstore.model;

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

import java.util.List;

import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.persistable.CuOrSe;

/**
 * <p>Purchase bundle - just booked customer's orders and SE orders.</p>
 *
 * @author Yury Demidenko
 */
public class Purch  {

  /**
   * <p>Customer orders.</p>
   **/
  private List<CustOrder> ords;

  /**
   * <p>Customer S.E. orders.</p>
   **/
  private List<CuOrSe> sords;

  //Simple getters and setters:
  /**
   * <p>Getter for ords.</p>
   * @return List<CustOrder>
   **/
  public final List<CustOrder> getOrds() {
    return this.ords;
  }

  /**
   * <p>Setter for ords.</p>
   * @param pOrds reference
   **/
  public final void setOrds(final List<CustOrder> pOrds) {
    this.ords = pOrds;
  }

  /**
   * <p>Getter for sords.</p>
   * @return List<CuOrSe>
   **/
  public final List<CuOrSe> getSords() {
    return this.sords;
  }

  /**
   * <p>Setter for sords.</p>
   * @param pSords reference
   **/
  public final void setSords(final List<CuOrSe> pSords) {
    this.sords = pSords;
  }
}
