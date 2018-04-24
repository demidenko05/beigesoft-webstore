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
 * Filter List Operator.
 * </p>
 *
 * @author Yury Demidenko
 */
public enum EFilterListOperator {

  /**
   * <p>0, in set of items or equal one.</p>
   **/
  IN,

  /**
   * <p>1, not in set of items or not equal one.</p>
   **/
  NOT_IN;
}
