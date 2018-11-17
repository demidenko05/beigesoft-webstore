package org.beigesoft.webstore.model;

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

/**
 * <p>Delivering methods.</p>
 *
 * @author Yury Demidenko
 */
public enum EDelivering {

  /**
   * <p>0, buyer will pick up by itself or its deliverer.</p>
   **/
  PICKUP,

  /**
   * <p>1, ordinal shot distance delivering, e.g hot pizza delivering.</p>
   **/
  DELIVERING,

  /**
   * <p>2, in country shipping.</p>
   **/
  SHIPPING,

  /**
   * <p>3, overseas shipping.</p>
   **/
  OVERSEAS,
}
