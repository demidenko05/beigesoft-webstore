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
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.persistable.CuOrSe;

/**
 * <p>It cancels all given buyer's orders.
 * E.g. buyer has not paid online after accepting (booking) orders.
 * It changes item's availability and orders status to given NEW or CANCELED.
 * </p>
 *
 * @author Yury Demidenko
 */
public interface ICncOrd {

  /**
   * <p>It cancels all given buyer's orders.
   * For example buyer had not paid online after accepting (booking) orders.
   * It changes item's availability and orders status to given NEW or CANCELED.
   * </p>
   * @param pRqVs additional request scoped parameters
   * @param pBuyr buyer
   * @param pPurId purchase ID
   * @param pStFr usually BOOKED
   * @param pStTo usually NEW
   * @throws Exception - an exception
   **/
  void cancel(Map<String, Object> pRqVs, OnlineBuyer pBuyr, Long pPurId,
    EOrdStat pStFr, EOrdStat pStTo) throws Exception;

  /**
   * <p>It cancels given buyer's order.</p>
   * @param pRqVs additional request scoped parameters
   * @param pCuOr order
   * @param pStat NEW or CANCELED
   * @throws Exception - an exception
   **/
  void cancel(Map<String, Object> pRqVs, CustOrder pCuOr,
    EOrdStat pStat) throws Exception;

  /**
   * <p>It cancels given buyer's S.E.order.</p>
   * @param pRqVs additional request scoped parameters
   * @param pCuOr order
   * @param pStat NEW or CANCELED
   * @throws Exception - an exception
   **/
  void cancel(Map<String, Object> pRqVs, CuOrSe pCuOr,
    EOrdStat pStat) throws Exception;
}
