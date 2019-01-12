package org.beigesoft.webstore.persistable;

/*
 * Copyright (c) 2018 Beigesof™
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

import org.beigesoft.webstore.persistable.base.ACuOr;

/**
 * <p>
 * Model of Customer Order for S.E. seller's items.
 * It's used to create Sales Invoice.
 * Customer order does neither accounting nor warehouse entries,
 * but it reduces "Goods Available in Place". Canceled order increases
 * back "Goods Available in Place".
 * </p>
 *
 * @author Yury Demidenko
 */
public class CuOrSe extends ACuOr {

  /**
   * <p>S.E. seller, not null.</p>
   **/
  private SeSeller sel;

  /**
   * <p>Ordered goods.</p>
   **/
  private List<CuOrSeGdLn> goods;

  /**
   * <p>Ordered services.</p>
   **/
  private List<CuOrSeSrLn> servs;

  /**
   * <p>Order's taxes summary.</p>
   **/
  private List<CuOrSeTxLn> taxes;

  //Simple getters and setters:
  /**
   * <p>Getter for sel.</p>
   * @return SeSeller
   **/
  public final SeSeller getSel() {
    return this.sel;
  }

  /**
   * <p>Setter for sel.</p>
   * @param pSel reference
   **/
  public final void setSel(final SeSeller pSel) {
    this.sel = pSel;
  }

  /**
   * <p>Getter for goods.</p>
   * @return List<CuOrSeGdLn>
   **/
  public final List<CuOrSeGdLn> getGoods() {
    return this.goods;
  }

  /**
   * <p>Setter for goods.</p>
   * @param pGoods reference
   **/
  public final void setGoods(final List<CuOrSeGdLn> pGoods) {
    this.goods = pGoods;
  }

  /**
   * <p>Getter for servs.</p>
   * @return List<CuOrSeSrLn>
   **/
  public final List<CuOrSeSrLn> getServs() {
    return this.servs;
  }

  /**
   * <p>Setter for servs.</p>
   * @param pServs reference
   **/
  public final void setServs(final List<CuOrSeSrLn> pServs) {
    this.servs = pServs;
  }

  /**
   * <p>Getter for taxes.</p>
   * @return List<CuOrSeTxLn>
   **/
  public final List<CuOrSeTxLn> getTaxes() {
    return this.taxes;
  }

  /**
   * <p>Setter for taxes.</p>
   * @param pTaxes reference
   **/
  public final void setTaxes(final List<CuOrSeTxLn> pTaxes) {
    this.taxes = pTaxes;
  }
}
