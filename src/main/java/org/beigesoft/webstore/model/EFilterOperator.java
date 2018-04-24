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
 * Filter Operator.
 * </p>
 *
 * @author Yury Demidenko
 */
public enum EFilterOperator {

  /**
   * <p>0, in set of items.</p>
   **/
  IN,

  /**
   * <p>1, not in set of items.</p>
   **/
  NOT_IN,

  /**
   * <p>2, equal.</p>
   **/
  EQUAL,

  /**
   * <p>3, not equal.</p>
   **/
  NOT_EQUAL,

  /**
   * <p>4, greater than.</p>
   **/
  GREATER_THAN,

  /**
   * <p>5, greater than or equal.</p>
   **/
  GREATER_THAN_EQUAL,

  /**
   * <p>6, less than.</p>
   **/
  LESS_THAN,

  /**
   * <p>7, less than or equal.</p>
   **/
  LESS_THAN_EQUAL,

  /**
   * <p>8, like.</p>
   **/
  LIKE;
}
