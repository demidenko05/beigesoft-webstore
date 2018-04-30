package org.beigesoft.webstore.model;

/*
 * Copyright (c) 2018 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import java.util.Set;
import java.math.BigDecimal;

import org.beigesoft.webstore.persistable.SpecificsOfItem;

/**
 * <p>
 * Filter of item's specifics.
 * </p>
 *
 * @author Yury Demidenko
 */
public class FilterSpecifics {

  /**
   * <p>Operator.</p>
   **/
  private EFilterOperator operator;

  /**
   * <p>Specifics.</p>
   **/
  private SpecificsOfItem specifics;

  /**
   * <p>If used. Set of ID of choosable specifics.</p>
   **/
  private Set<Long> ids;

  /**
   * <p>Integer value#1, if used.</p>
   **/
  private Long longValue1;

  /**
   * <p>Integer value#2, if used.</p>
   **/
  private Long longValue2;

  /**
   * <p>Float value#1, if used.</p>
   **/
  private BigDecimal numericValue1;

  /**
   * <p>Float value#2, if used.</p>
   **/
  private BigDecimal numericValue2;

  /**
   * <p>String value, if used.</p>
   **/
  private String stringValue;

  //Simple getters and setters:
  /**
   * <p>Getter for operator.</p>
   * @return EFilterOperator
   **/
  public final EFilterOperator getOperator() {
    return this.operator;
  }

  /**
   * <p>Setter for operator.</p>
   * @param pOperator reference
   **/
  public final void setOperator(final EFilterOperator pOperator) {
    this.operator = pOperator;
  }

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
   * <p>Getter for ids.</p>
   * @return Set<Long>
   **/
  public final Set<Long> getIds() {
    return this.ids;
  }

  /**
   * <p>Setter for ids.</p>
   * @param pIds reference
   **/
  public final void setIds(final Set<Long> pIds) {
    this.ids = pIds;
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
   * <p>Getter for stringValue.</p>
   * @return String
   **/
  public final String getStringValue() {
    return this.stringValue;
  }

  /**
   * <p>Setter for stringValue.</p>
   * @param pStringValue reference
   **/
  public final void setStringValue(final String pStringValue) {
    this.stringValue = pStringValue;
  }
}
