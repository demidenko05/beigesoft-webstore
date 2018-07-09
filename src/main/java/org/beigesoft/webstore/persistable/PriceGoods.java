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

import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.webstore.persistable.base.AItemPrice;

/**
 * <p>
 * Model of goods price.
 * </p>
 *
 * @author Yury Demidenko
 */
public class PriceGoods extends AItemPrice<InvItem, PriceGoodsId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private PriceGoodsId itsId = new PriceGoodsId();

  /**
   * <p>Goods, not null.</p>
   **/
  private InvItem item;

  /**
   * <p>Price Category.</p>
   **/
  private PriceCategory priceCategory;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final PriceGoodsId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final PriceGoodsId pItsId) {
    this.itsId = pItsId;
    if (this.itsId != null) {
      this.priceCategory = this.itsId.getPriceCategory();
      this.item = this.itsId.getItem();
    } else {
      this.priceCategory = null;
      this.item = null;
    }
  }

  /**
   * <p>Getter for priceCategory.</p>
   * @return PriceCategory
   **/
  @Override
  public final PriceCategory getPriceCategory() {
    return this.priceCategory;
  }

  /**
   * <p>Setter for priceCategory.</p>
   * @param pPriceCategory reference
   **/
  @Override
  public final void setPriceCategory(final PriceCategory pPriceCategory) {
    this.priceCategory = pPriceCategory;
    if (this.itsId == null) {
      this.itsId = new PriceGoodsId();
    }
    this.itsId.setPriceCategory(this.priceCategory);
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
   * @param pGoods reference
   **/
  @Override
  public final void setItem(final InvItem pGoods) {
    this.item = pGoods;
    if (this.itsId == null) {
      this.itsId = new PriceGoodsId();
    }
    this.itsId.setItem(this.item);
  }
}
