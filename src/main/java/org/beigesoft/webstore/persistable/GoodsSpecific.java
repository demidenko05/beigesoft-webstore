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
 * <pre>
 * Model of Specifics values for a Goods.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class GoodsSpecific extends AEditableHasVersion
  implements IHasId<GoodsSpecificId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private GoodsSpecificId itsId = new GoodsSpecificId();

  /**
   * <p>Goods specifics.</p>
   **/
  private SpecificsOfItem specifics;

  /**
   * <p>Goods, not null.</p>
   **/
  private InvItem goods;

  /**
   * <p>Numeric Value1 if present.</p>
   **/
  private BigDecimal numericValue1;

  /**
   * <p>Numeric Value2 if present.</p>
   **/
  private BigDecimal numericValue2;

  /**
   * <p>Long Value1 if present.</p>
   **/
  private Long longValue1;

  /**
   * <p>Long Value2 if present.</p>
   **/
  private Long longValue2;

  /**
   * <p>String Value1 if present.</p>
   **/
  private String stringValue1;

  /**
   * <p>String Value2 if present.</p>
   **/
  private String stringValue2;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final GoodsSpecificId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pId model ID
   **/
  @Override
  public final void setItsId(final GoodsSpecificId pItsId) {
    this.itsId = pItsId;
    if (this.itsId != null) {
      this.specifics = this.itsId.getSpecifics();
      this.goods = this.itsId.getGoods();
    } else {
      this.specifics = null;
      this.goods = null;
    }
  }

  /**
   * <p>Setter for specifics.</p>
   * @param pSpecifics reference
   **/
  public final void setSpecifics(final SpecificsOfItem pSpecifics) {
    this.specifics = pSpecifics;
    if (this.itsId == null) {
      this.itsId = new GoodsSpecificId();
    }
    this.itsId.setSpecifics(this.specifics);
  }

  /**
   * <p>Setter for goods.</p>
   * @param pGoods reference
   **/
  public final void setGoods(final InvItem pGoods) {
    this.goods = pGoods;
    if (this.itsId == null) {
      this.itsId = new GoodsSpecificId();
    }
    this.itsId.setGoods(this.goods);
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
   * <p>Getter for goods.</p>
   * @return InvItem
   **/
  public final InvItem getGoods() {
    return this.goods;
  }

  /**
   * <p>Getter for numericValue1.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getNumericValue1() {
    return this.numericValue1;
  }

  /**
   * <p>Setter for numericValue1.</p>
   * @param pNumericValue1 reference
   **/
  public final void setNumericValue1(final BigDecimal pNumericValue1) {
    this.numericValue1 = pNumericValue1;
  }

  /**
   * <p>Getter for numericValue2.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getNumericValue2() {
    return this.numericValue2;
  }

  /**
   * <p>Setter for numericValue2.</p>
   * @param pNumericValue2 reference
   **/
  public final void setNumericValue2(final BigDecimal pNumericValue2) {
    this.numericValue2 = pNumericValue2;
  }

  /**
   * <p>Getter for longValue1.</p>
   * @return Long
   **/
  public final Long getLongValue1() {
    return this.longValue1;
  }

  /**
   * <p>Setter for longValue1.</p>
   * @param pLongValue1 reference
   **/
  public final void setLongValue1(final Long pLongValue1) {
    this.longValue1 = pLongValue1;
  }

  /**
   * <p>Getter for longValue2.</p>
   * @return Long
   **/
  public final Long getLongValue2() {
    return this.longValue2;
  }

  /**
   * <p>Setter for longValue2.</p>
   * @param pLongValue2 reference
   **/
  public final void setLongValue2(final Long pLongValue2) {
    this.longValue2 = pLongValue2;
  }

  /**
   * <p>Getter for stringValue1.</p>
   * @return String
   **/
  public final String getStringValue1() {
    return this.stringValue1;
  }

  /**
   * <p>Setter for stringValue1.</p>
   * @param pStringValue1 reference
   **/
  public final void setStringValue1(final String pStringValue1) {
    this.stringValue1 = pStringValue1;
  }

  /**
   * <p>Getter for stringValue2.</p>
   * @return String
   **/
  public final String getStringValue2() {
    return this.stringValue2;
  }

  /**
   * <p>Setter for stringValue2.</p>
   * @param pStringValue2 reference
   **/
  public final void setStringValue2(final String pStringValue2) {
    this.stringValue2 = pStringValue2;
  }
}
