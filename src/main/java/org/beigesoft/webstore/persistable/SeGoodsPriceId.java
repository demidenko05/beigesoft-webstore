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

import org.beigesoft.webstore.persistable.base.AItemPriceId;

/**
 * <p>
 * Model of ID of SeGoodsPrice.
 * </p>
 *
 * @author Yury Demidenko
 */
public class SeGoodsPriceId extends AItemPriceId<SeGoods> {

  /**
   * <p>SeGoods, not null.</p>
   **/
  private SeGoods item;

  //Simple getters and setters:
  /**
   * <p>Getter for item.</p>
   * @return SeGoods
   **/
  public final SeGoods getItem() {
    return this.item;
  }

  /**
   * <p>Setter for item.</p>
   * @param pSeGoods reference
   **/
  public final void setItem(final SeGoods pSeGoods) {
    this.item = pSeGoods;
  }
}
