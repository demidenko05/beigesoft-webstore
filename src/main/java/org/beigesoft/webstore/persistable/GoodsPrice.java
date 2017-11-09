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

import java.math.BigDecimal;

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;
import org.beigesoft.accounting.persistable.InvItem;

/**
 * <p>
 * Model of Price for sell Goods.
 * </p>
 *
 * @author Yury Demidenko
 */
public class GoodsPrice extends AEditableHasVersion
  implements IHasId<GoodsPriceId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private GoodsPriceId itsId = new GoodsPriceId();

  /**
   * <p>Price Category.</p>
   **/
  private PriceCategory priceCategory;

  /**
   * <p>Goods, not null.</p>
   **/
  private InvItem goods;

  /**
   * <p>Its price.</p>
   **/
  private BigDecimal itsPrice;

  /**
   * <p>It can be used to implements widely
   * used method "Price down",
   * i.e. previousPrice = 60 against itsPrice = 45, nullable.</p>
   **/
  private BigDecimal previousPrice;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final GoodsPriceId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pId model ID
   **/
  @Override
  public final void setItsId(final GoodsPriceId pItsId) {
    this.itsId = pItsId;
    if (this.itsId != null) {
      this.priceCategory = this.itsId.getPriceCategory();
      this.goods = this.itsId.getGoods();
    } else {
      this.priceCategory = null;
      this.goods = null;
    }
  }

  /**
   * <p>Setter for pPriceCategory.</p>
   * @param pPriceCategory reference
   **/
  public final void setPriceCategory(final PriceCategory pPriceCategory) {
    this.priceCategory = pPriceCategory;
    if (this.itsId == null) {
      this.itsId = new GoodsPriceId();
    }
    this.itsId.setPriceCategory(this.priceCategory);
  }

  /**
   * <p>Setter for goods.</p>
   * @param pGoods reference
   **/
  public final void setGoods(final InvItem pGoods) {
    this.goods = pGoods;
    if (this.itsId == null) {
      this.itsId = new GoodsPriceId();
    }
    this.itsId.setGoods(this.goods);
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
   * <p>Getter for itsPrice.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsPrice() {
    return this.itsPrice;
  }

  /**
   * <p>Setter for itsPrice.</p>
   * @param pItsPrice reference
   **/
  public final void setItsPrice(final BigDecimal pItsPrice) {
    this.itsPrice = pItsPrice;
  }

  /**
   * <p>Getter for previousPrice.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getPreviousPrice() {
    return this.previousPrice;
  }

  /**
   * <p>Setter for previousPrice.</p>
   * @param pPreviousPrice reference
   **/
  public final void setPreviousPrice(final BigDecimal pPreviousPrice) {
    this.previousPrice = pPreviousPrice;
  }
}
