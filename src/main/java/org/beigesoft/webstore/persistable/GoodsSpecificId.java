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
 * Model of ID of Specifics values for a Goods.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class GoodsSpecificId {

  /**
   * <p>Goods specifics.</p>
   **/
  private SpecificsOfItem specifics;

  /**
   * <p>Goods, not null.</p>
   **/
  private InvItem goods;


  /**
   * <p>Minimal constructor.</p>
   **/
  public GoodsSpecificId() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pSpecifics reference
   * @param pGoods reference
   **/
  public GoodsSpecificId(final SpecificsOfItem pSpecifics,
    final InvItem pGoods) {
    this.goods = pGoods;
    this.specifics = pSpecifics;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for specifics.</p>
   * @return SpecificsOfItem
   **/
  public final SpecificsOfItem getSpecifics() {
    return this.specifics;
  }

  /**
   * <p>Setter for specifics.</p>
   * @param pSpecifics reference
   **/
  public final void setSpecifics(final SpecificsOfItem pSpecifics) {
    this.specifics = pSpecifics;
  }

  /**
   * <p>Getter for goods.</p>
   * @return InvItem
   **/
  public final InvItem getGoods() {
    return this.goods;
  }

  /**
   * <p>Setter for goods.</p>
   * @param pGoods reference
   **/
  public final void setGoods(final InvItem pGoods) {
    this.goods = pGoods;
  }
}
