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

/**
 * <p>
 * Filter Integer.
 * </p>
 *
 * @author Yury Demidenko
 */
public class FilterInteger {

  /**
   * <p>Operator.</p>
   **/
  private EFilterOperator operator;

  /**
   * <p>Value#1.</p>
   **/
  private Integer value1;

  /**
   * <p>Value#2 if used.</p>
   **/
  private Integer value2;

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
   * <p>Getter for value1.</p>
   * @return Integer
   **/
  public final Integer getValue1() {
    return this.value1;
  }

  /**
   * <p>Setter for value1.</p>
   * @param pValue1 reference
   **/
  public final void setValue1(final Integer pValue1) {
    this.value1 = pValue1;
  }

  /**
   * <p>Getter for value2.</p>
   * @return Integer
   **/
  public final Integer getValue2() {
    return this.value2;
  }

  /**
   * <p>Setter for value2.</p>
   * @param pValue2 reference
   **/
  public final void setValue2(final Integer pValue2) {
    this.value2 = pValue2;
  }
}
