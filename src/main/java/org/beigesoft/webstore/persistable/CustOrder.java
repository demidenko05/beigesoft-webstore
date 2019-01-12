package org.beigesoft.webstore.persistable;

/*
 * Copyright (c) 2017 Beigesof ™
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
 * Model of Customer Order for web-store owner's items.
 * It's used to create Sales Invoice.
 * Customer order does neither accounting nor warehouse entries,
 * but it reduces "Goods Available in Place". Canceled order increases
 * back "Goods Available in Place".
 * </p>
 *
 * @author Yury Demidenko
 */
public class CustOrder extends ACuOr {

  /**
   * <p>Ordered goods.</p>
   **/
  private List<CustOrderGdLn> goods;

  /**
   * <p>Ordered services.</p>
   **/
  private List<CustOrderSrvLn> servs;

  /**
   * <p>Order's taxes summary.</p>
   **/
  private List<CustOrderTxLn> taxes;

  /**
   * <p>Invoice ID (if it was derived).</p>
   **/
  private Long inId;

  //Simple getters and setters:
  /**
   * <p>Getter for goods.</p>
   * @return List<CustOrderGdLn>
   **/
  public final List<CustOrderGdLn> getGoods() {
    return this.goods;
  }

  /**
   * <p>Setter for goods.</p>
   * @param pGoods reference
   **/
  public final void setGoods(final List<CustOrderGdLn> pGoods) {
    this.goods = pGoods;
  }

  /**
   * <p>Getter for servs.</p>
   * @return List<CustOrderSrvLn>
   **/
  public final List<CustOrderSrvLn> getServs() {
    return this.servs;
  }

  /**
   * <p>Setter for servs.</p>
   * @param pServs reference
   **/
  public final void setServs(final List<CustOrderSrvLn> pServs) {
    this.servs = pServs;
  }

  /**
   * <p>Getter for taxes.</p>
   * @return List<CustOrderTxLn>
   **/
  public final List<CustOrderTxLn> getTaxes() {
    return this.taxes;
  }

  /**
   * <p>Setter for taxes.</p>
   * @param pTaxes reference
   **/
  public final void setTaxes(final List<CustOrderTxLn> pTaxes) {
    this.taxes = pTaxes;
  }

  /**
   * <p>Getter for inId.</p>
   * @return Long
   **/
  public final Long getInId() {
    return this.inId;
  }

  /**
   * <p>Setter for inId.</p>
   * @param pInId reference
   **/
  public final void setInId(final Long pInId) {
    this.inId = pInId;
  }
}
