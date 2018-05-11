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
 * Bundle of specifics filter where clause and count of conditions.
 * </p>
 *
 * @author Yury Demidenko
 */
public class SpecificsFiltersWhere  {

  /**
   * <p>Where clouse e.g.:
   * "(SPECIFICS=3 and LONGVALUE1 in (3, 14))
   * or (SPECIFICS=4 and NUMERICVALUE1&lt;2.33)".</p>
   **/
  private String where;

  /**
   * <p>Count of where conditions e.g. 2 for:
   * (SPECIFICS=3 and LONGVALUE1 in (3, 14))
   * or (SPECIFICS=4 and NUMERICVALUE1&lt;2.33).</p>
   **/
  private Integer whereCount = 0;

  //Simple getters and setters:
  /**
   * <p>Getter for where.</p>
   * @return String
   **/
  public final String getWhere() {
    return this.where;
  }

  /**
   * <p>Setter for where.</p>
   * @param pWhere reference
   **/
  public final void setWhere(final String pWhere) {
    this.where = pWhere;
  }

  /**
   * <p>Getter for whereCount.</p>
   * @return Integer
   **/
  public final Integer getWhereCount() {
    return this.whereCount;
  }

  /**
   * <p>Setter for whereCount.</p>
   * @param pWhereCount reference
   **/
  public final void setWhereCount(final Integer pWhereCount) {
    this.whereCount = pWhereCount;
  }
}
