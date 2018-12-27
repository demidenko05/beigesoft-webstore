package org.beigesoft.webstore.service;

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

import java.util.List;
import java.util.Map;

import org.beigesoft.webstore.model.EOrdStat;
import org.beigesoft.webstore.persistable.CustOrder;

/**
 * <p>It cancels all given buyer's orders in single transaction.
 * E.g. buyer has not paid online after accepting (booking) orders.
 * It changes item's availability and orders status to given NEW or CANCELED.
 * Request handler must be non-transactional,
 * i.e. it mustn't be started transaction.</p>
 *
 * @author Yury Demidenko
 */
public class CncOrd implements ICncOrd {

  /**
   * <p>It cancels all given buyer's orders in single transaction.
   * For example buyer had not paid online after accepting (booking) orders.
   * It changes item's availability and orders status to given NEW or CANCELED.
   * Request handler must be non-transactional,
   * i.e. it mustn't be started transaction.</p>
   * @param pRqVs additional request scoped parameters
   * @param pOrds orders
   * @param pStat NEW or CANCELED
   * @throws Exception - an exception
   **/
  public final void cancel(final Map<String, Object> pRqVs,
    final List<CustOrder> pOrds, final EOrdStat pStat) throws Exception {
  }
}
