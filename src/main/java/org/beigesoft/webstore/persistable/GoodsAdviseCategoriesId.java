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
 * Model of ID for Goods Advise Category.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class GoodsAdviseCategoriesId {

  /**
   * <p>Advise Category Of Goods, not null.</p>
   **/
  private AdviseCategoryOfGs adviseCategory;

  /**
   * <p>Goods, not null.</p>
   **/
  private InvItem goods;

  /**
   * <p>Minimal constructor.</p>
   **/
  public GoodsAdviseCategoriesId() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pAdviseCategory reference
   * @param pGoods reference
   **/
  public GoodsAdviseCategoriesId(final AdviseCategoryOfGs pAdviseCategory,
    final InvItem pGoods) {
    this.goods = pGoods;
    this.adviseCategory = pAdviseCategory;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for adviseCategory.</p>
   * @return AdviseCategoryOfGs
   **/
  public final AdviseCategoryOfGs getAdviseCategory() {
    return this.adviseCategory;
  }

  /**
   * <p>Getter for goods.</p>
   * @return InvItem
   **/
  public final InvItem getGoods() {
    return this.goods;
  }

  /**
   * <p>Setter for pAdviseCategory.</p>
   * @param pAdviseCategory reference
   **/
  public final void setAdviseCategory(
    final AdviseCategoryOfGs pAdviseCategory) {
    this.adviseCategory = pAdviseCategory;
  }

  /**
   * <p>Setter for goods.</p>
   * @param pGoods reference
   **/
  public final void setGoods(final InvItem pGoods) {
    this.goods = pGoods;
  }
}
