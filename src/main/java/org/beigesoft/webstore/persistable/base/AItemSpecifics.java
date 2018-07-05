package org.beigesoft.webstore.persistable.base;

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

import java.math.BigDecimal;

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;
import org.beigesoft.model.IHasName;
import org.beigesoft.webstore.persistable.SpecificsOfItem;

/**
 * <p>
 * Model of Specifics values for a Item (Goods/Service/SeGoods/SeService).
 * BeigeORM not support generic fields!
 * </p>
 *
 * @param <T> item type
 * @param <ID> ID type
 * @author Yury Demidenko
 */
public abstract class
  AItemSpecifics<T extends IHasName, ID extends AItemSpecificsId<T>>
    extends AEditableHasVersion implements IHasId<ID> {

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
   * <p>String Value3 if present.</p>
   **/
  private String stringValue3;

  /**
   * <p>String Value4 if present.</p>
   **/
  private String stringValue4;

  /**
   * <p>Setter for specifics.</p>
   * @param pSpecifics reference
   **/
  public abstract void setSpecifics(final SpecificsOfItem pSpecifics);

  /**
   * <p>Getter for item.</p>
   * @return T
   **/
  public abstract T getItem();

  /**
   * <p>Setter for item.</p>
   * @param pItem reference
   **/
  public abstract void setItem(final T pItem);

  /**
   * <p>Getter for specifics.</p>
   * @return SpecificsOfItem
   **/
  public abstract SpecificsOfItem getSpecifics();

  //Simple getters and setters:

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

  /**
   * <p>Getter for stringValue3.</p>
   * @return String
   **/
  public final String getStringValue3() {
    return this.stringValue3;
  }

  /**
   * <p>Setter for stringValue3.</p>
   * @param pStringValue3 reference
   **/
  public final void setStringValue3(final String pStringValue3) {
    this.stringValue3 = pStringValue3;
  }

  /**
   * <p>Getter for stringValue4.</p>
   * @return String
   **/
  public final String getStringValue4() {
    return this.stringValue4;
  }

  /**
   * <p>Setter for stringValue4.</p>
   * @param pStringValue4 reference
   **/
  public final void setStringValue4(final String pStringValue4) {
    this.stringValue4 = pStringValue4;
  }
}
