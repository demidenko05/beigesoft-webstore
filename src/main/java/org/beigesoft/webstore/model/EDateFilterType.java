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
 * Date Filter type.
 * </p>
 *
 * @author Yury Demidenko
 */
public enum EDateFilterType {

  /**
   * <p>0, Since day.</p>
   **/
  SINCE_DAY,

  /**
   * <p>1, At day.</p>
   **/
  AT_DAY,

  /**
   * <p>2, At time (day, hour, minutes.</p>
   **/
  AT_TIME,

  /**
   * <p>3, From day1 till day2.</p>
   **/
  FROM_TILL_DAY,

  /**
   * <p>4, From day-hour-minute#1 till day-hour-minute#2.</p>
   **/
  FROM_TILL_TIME;
}
