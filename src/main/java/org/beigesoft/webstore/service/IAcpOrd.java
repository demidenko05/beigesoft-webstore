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

import org.beigesoft.model.IRequestData;
import org.beigesoft.webstore.model.Purch;
import org.beigesoft.webstore.persistable.OnlineBuyer;

/**
 * <p>It accepts all buyer's orders.
 * It changes item's availability and orders status to PENDING.
 * If any item is unavailable, then it throws exception.
 * And so does if there are several payees for online payment.</p>
 *
 * @author Yury Demidenko
 */
public interface IAcpOrd {

  /**
   * <p>It accepts all buyer's orders.
   * It changes item's availability and orders status to PENDING.
   * If any item is unavailable, then it throws exception.
   * And so does if there are several payees for online payment.</p>
   * @param pReqVars additional request scoped parameters
   * @param pReqDt Request Data
   * @param pBur Buyer
   * @return list of accepted orders
   * @throws Exception - an exception
   **/
  Purch accept(Map<String, Object> pReqVars,
    IRequestData pReqDt, OnlineBuyer pBur) throws Exception;
}
