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

import java.util.List;

import org.beigesoft.webstore.persistable.base.ACuOrSeLn;

/**
 * <p>
 * Model of Customer Order Goods line.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CuOrSeGdLn extends ACuOrSeLn {

  /**
   * <p>Good, not null.</p>
   **/
  private SeGoods good;

  /**
   * <p>Item taxes for item basis non-aggregate method.</p>
   **/
  private List<CuOrSeGdTxLn> itTxs;

  //Simple getters and setters:
  /**
   * <p>Getter for goods.</p>
   * @return SeGoods
   **/
  public final SeGoods getGood() {
    return this.good;
  }

  /**
   * <p>Setter for goods.</p>
   * @param pGood reference
   **/
  public final void setGood(final SeGoods pGood) {
    this.good = pGood;
  }

  /**
   * <p>Getter for itTxs.</p>
   * @return List<CuOrSeGdTxLn>
   **/
  public final List<CuOrSeGdTxLn> getItTxs() {
    return this.itTxs;
  }

  /**
   * <p>Setter for itTxs.</p>
   * @param pItTxs reference
   **/
  public final void setItTxs(final List<CuOrSeGdTxLn> pItTxs) {
    this.itTxs = pItTxs;
  }
}
