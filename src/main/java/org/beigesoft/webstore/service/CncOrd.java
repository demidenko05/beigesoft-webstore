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

import org.beigesoft.log.ILogger;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
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
 * @param <RS> platform dependent RDBMS recordset
 * @author Yury Demidenko
 */
public class CncOrd<RS> implements ICncOrd {

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>Database service.</p>
   */
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   */
  private ISrvOrm<RS> srvOrm;

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

  //Simple getters and setters:
  /**
   * <p>Geter for logger.</p>
   * @return ILogger
   **/
  public final ILogger getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final void setLogger(final ILogger pLogger) {
    this.logger = pLogger;
  }

  /**
   * <p>Getter for srvDatabase.</p>
   * @return ISrvDatabase<RS>
   **/
  public final ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final void setSrvDatabase(
    final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }

  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }
}
