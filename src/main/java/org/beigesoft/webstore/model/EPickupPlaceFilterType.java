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
 * Pickup Place Filter type.
 * </p>
 *
 * @author Yury Demidenko
 */
public enum EPickupPlaceFilterType {

  /**
   * <p>0, List of pickup places.</p>
   **/
  LIST,

  /**
   * <p>1, Distance around place.</p>
   **/
  DISTANCE,

  /**
   * <p>2, Distance around place.</p>
   **/
  TIME_ZONE;
}
