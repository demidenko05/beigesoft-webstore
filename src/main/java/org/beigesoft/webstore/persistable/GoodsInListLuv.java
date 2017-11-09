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

import org.beigesoft.persistable.AHasIdLongVersion;

/**
 * <pre>
 * Hold last updated versions of goods characteristics.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class GoodsInListLuv extends AHasIdLongVersion {

  /**
   * <p>Last version of GoodsSpecific updated ItemInList.</p>
   **/
  private Long goodsSpecificLuv;

  /**
   * <p>Last version of GoodsPrice updated ItemInList.</p>
   **/
  private Long goodsPriceLuv;

  /**
   * <p>Last version of GoodsAvailable updated ItemInList.</p>
   **/
  private Long goodsAvailableLuv;

  /**
   * <p>Last version of GoodsRating updated ItemInList.</p>
   **/
  private Long goodsRatingLuv;

  //Simple getters and setters:
  /**
   * <p>Getter for goodsSpecificLuv.</p>
   * @return Long
   **/
  public final Long getGoodsSpecificLuv() {
    return this.goodsSpecificLuv;
  }

  /**
   * <p>Setter for goodsSpecificLuv.</p>
   * @param pGoodsSpecificLuv reference
   **/
  public final void setGoodsSpecificLuv(final Long pGoodsSpecificLuv) {
    this.goodsSpecificLuv = pGoodsSpecificLuv;
  }

  /**
   * <p>Getter for goodsPriceLuv.</p>
   * @return Long
   **/
  public final Long getGoodsPriceLuv() {
    return this.goodsPriceLuv;
  }

  /**
   * <p>Setter for goodsPriceLuv.</p>
   * @param pGoodsPriceLuv reference
   **/
  public final void setGoodsPriceLuv(final Long pGoodsPriceLuv) {
    this.goodsPriceLuv = pGoodsPriceLuv;
  }

  /**
   * <p>Getter for goodsAvailableLuv.</p>
   * @return Long
   **/
  public final Long getGoodsAvailableLuv() {
    return this.goodsAvailableLuv;
  }

  /**
   * <p>Setter for goodsAvailableLuv.</p>
   * @param pGoodsAvailableLuv reference
   **/
  public final void setGoodsAvailableLuv(final Long pGoodsAvailableLuv) {
    this.goodsAvailableLuv = pGoodsAvailableLuv;
  }

  /**
   * <p>Getter for goodsRatingLuv.</p>
   * @return Long
   **/
  public final Long getGoodsRatingLuv() {
    return this.goodsRatingLuv;
  }

  /**
   * <p>Setter for goodsRatingLuv.</p>
   * @param pGoodsRatingLuv reference
   **/
  public final void setGoodsRatingLuv(final Long pGoodsRatingLuv) {
    this.goodsRatingLuv = pGoodsRatingLuv;
  }
}
