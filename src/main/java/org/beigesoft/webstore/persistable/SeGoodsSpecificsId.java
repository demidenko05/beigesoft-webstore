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

import org.beigesoft.webstore.persistable.base.AItemSpecificsId;

/**
 * <p>
 * Model of ID of Specifics values for a SeGoods.
 * </p>
 *
 * @author Yury Demidenko
 */
public class SeGoodsSpecificsId extends AItemSpecificsId<SeGoods> {

  /**
   * <p>SeGoods.</p>
   **/
  private SeGoods item;

  /**
   * <p>Minimal constructor.</p>
   **/
  public SeGoodsSpecificsId() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pSpecifics reference
   * @param pItem reference
   **/
  public SeGoodsSpecificsId(final SpecificsOfItem pSpecifics,
    final SeGoods pItem) {
    this.item = pItem;
    setSpecifics(pSpecifics);
  }

  /**
   * <p>Getter for item.</p>
   * @return SeGoods
   **/
  @Override
  public final SeGoods getItem() {
    return this.item;
  }

  /**
   * <p>Setter for item.</p>
   * @param pItem reference
   **/
  @Override
  public final void setItem(final SeGoods pItem) {
    this.item = pItem;
  }
}
