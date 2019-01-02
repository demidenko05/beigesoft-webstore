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

import java.util.Map;

import org.beigesoft.webstore.model.EOrdStat;
import org.beigesoft.webstore.model.Purch;
import org.beigesoft.webstore.persistable.OnlineBuyer;

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
   * @param pPurch orders
   * @param pStat NEW or CANCELED
   * @throws Exception - an exception
   **/
  @Override
  public final void cancel(final Map<String, Object> pRqVs,
    final Purch pPurch, final EOrdStat pStat) throws Exception {
  }


  /**
   * <p>It cancels all buyer's orders in single transaction.
   * For example it's arise error during final phase online payment execution.
   * It changes item's availability and orders status to given NEW or CANCELED.
   * Request handler must be non-transactional,
   * i.e. it mustn't be started transaction.</p>
   * @param pRqVs additional request scoped parameters
   * @param pBuyr buyer
   * @param pStFr usually BOOKED
   * @param pStTo usually NEW
   * @throws Exception - an exception
   **/
  @Override
  public final void cancel(final Map<String, Object> pRqVs,
    final OnlineBuyer pBuyr, final EOrdStat pStFr,
      final EOrdStat pStTo) throws Exception {
  }

  /**
   * <p>It cancels all buyer's orders with given purchase ID in single
   * transaction.
   * For example it's arise error during final phase online payment execution.
   * It changes item's availability and orders status to given NEW or CANCELED.
   * Request handler must be non-transactional,
   * i.e. it mustn't be started transaction.</p>
   * @param pRqVs additional request scoped parameters
   * @param pBuyr buyer
   * @param pPurId purchase ID
   * @param pStFr usually BOOKED
   * @param pStTo usually NEW
   * @throws Exception - an exception
   **/
  @Override
  public final void cancel(final Map<String, Object> pRqVs,
    final OnlineBuyer pBuyr, final Long pPurId,
      final EOrdStat pStFr, final EOrdStat pStTo) throws Exception {
  }
}