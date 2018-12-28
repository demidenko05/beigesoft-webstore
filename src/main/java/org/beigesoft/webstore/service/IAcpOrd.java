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
 * <p>It accepts all buyer's orders in single transaction.
 * It changes item's availability and orders status to PENDING.
 * If any item is unavailable, then that transaction will be rolled back,
 * and it returns result NULL. And so does if there are several payees for
 * online payment. Request handler must be non-transactional,
 * i.e. it mustn't be started transaction.</p>
 *
 * @author Yury Demidenko
 */
public interface IAcpOrd {

  /**
   * <p>It accepts all buyer's orders in single transaction.
   * It changes item's availability and orders status to PENDING.
   * If any item is unavailable, then that transaction will be rolled back,
   * and it returns result NULL. And so does if there are several payees for
   * online payment. Request handler must be non-transactional,
   * i.e. it mustn't be started transaction.</p>
   * @param pReqVars additional request scoped parameters
   * @param pReqDt Request Data
   * @param pBur Buyer
   * @return list of accepted orders or NULL
   * @throws Exception - an exception
   **/
  Purch accept(Map<String, Object> pReqVars,
    IRequestData pReqDt, OnlineBuyer pBur) throws Exception;
}
