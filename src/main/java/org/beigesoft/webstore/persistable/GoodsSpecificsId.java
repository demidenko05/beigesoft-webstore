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
import org.beigesoft.accounting.persistable.InvItem;

/**
 * <p>
 * Model of ID of Specifics values for a Goods.
 * </p>
 *
 * @author Yury Demidenko
 */
public class GoodsSpecificsId extends AItemSpecificsId<InvItem> {

  /**
   * <p>Goods.</p>
   **/
  private InvItem item;

  /**
   * <p>Minimal constructor.</p>
   **/
  public GoodsSpecificsId() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pSpecifics reference
   * @param pItem reference
   **/
  public GoodsSpecificsId(final SpecificsOfItem pSpecifics,
    final InvItem pItem) {
    this.item = pItem;
    setSpecifics(pSpecifics);
  }

  /**
   * <p>Getter for item.</p>
   * @return InvItem
   **/
  @Override
  public final InvItem getItem() {
    return this.item;
  }

  /**
   * <p>Setter for item.</p>
   * @param pItem reference
   **/
  @Override
  public final void setItem(final InvItem pItem) {
    this.item = pItem;
  }
}
