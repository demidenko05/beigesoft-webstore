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
 * <p>Customer order status - new/pending/closed/canceled.</p>
 *
 * @author Yury Demidenko
 */
public enum EOrdStat {

  /**
   * <p>0, new.</p>
   **/
  NEW,

  /**
   * <p>1, pending.</p>
   **/
  PENDING,

  /**
   * <p>2, closed.</p>
   **/
  CLOSED,

  /**
   * <p>3, canceled.</p>
   **/
  CANCELED,
}
