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

import org.beigesoft.accounting.persistable.InvItem;

/**
 * <pre>
 * Model of ID for Price for Goods.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class GoodsPriceId {

  /**
   * <p>Price Category.</p>
   **/
  private PriceCategory priceCategory;

  /**
   * <p>Goods, not null.</p>
   **/
  private InvItem goods;

  /**
   * <p>Minimal constructor.</p>
   **/
  public GoodsPriceId() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pPriceCategory reference
   * @param pGoods reference
   **/
  public GoodsPriceId(final PriceCategory pPriceCategory,
    final InvItem pGoods) {
    this.goods = pGoods;
    this.priceCategory = pPriceCategory;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for priceCategory.</p>
   * @return PriceCategory
   **/
  public final PriceCategory getPriceCategory() {
    return this.priceCategory;
  }

  /**
   * <p>Getter for goods.</p>
   * @return InvItem
   **/
  public final InvItem getGoods() {
    return this.goods;
  }

  /**
   * <p>Setter for pPriceCategory.</p>
   * @param pPriceCategory reference
   **/
  public final void setPriceCategory(final PriceCategory pPriceCategory) {
    this.priceCategory = pPriceCategory;
  }

  /**
   * <p>Setter for goods.</p>
   * @param pGoods reference
   **/
  public final void setGoods(final InvItem pGoods) {
    this.goods = pGoods;
  }
}
