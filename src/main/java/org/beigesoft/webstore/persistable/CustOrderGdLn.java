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

import java.util.List;

import org.beigesoft.webstore.persistable.base.ACustOrderLn;
import org.beigesoft.accounting.persistable.InvItem;

/**
 * <p>
 * Model of Customer Order Goods line.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CustOrderGdLn extends ACustOrderLn {

  /**
   * <p>Good, not null.</p>
   **/
  private InvItem good;

  /**
   * <p>Item taxes for item basis non-aggregate method.</p>
   **/
  private List<CuOrGdTxLn> itTxs;

  //Simple getters and setters:
  /**
   * <p>Getter for goods.</p>
   * @return InvItem
   **/
  public final InvItem getGood() {
    return this.good;
  }

  /**
   * <p>Setter for goods.</p>
   * @param pGood reference
   **/
  public final void setGood(final InvItem pGood) {
    this.good = pGood;
  }

  /**
   * <p>Getter for itTxs.</p>
   * @return List<CuOrGdTxLn>
   **/
  public final List<CuOrGdTxLn> getItTxs() {
    return this.itTxs;
  }

  /**
   * <p>Setter for itTxs.</p>
   * @param pItTxs reference
   **/
  public final void setItTxs(final List<CuOrGdTxLn> pItTxs) {
    this.itTxs = pItTxs;
  }
}
