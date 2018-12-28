package org.beigesoft.webstore.persistable;

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
import java.util.Date;

import org.beigesoft.webstore.persistable.base.ACuOrSeLn;

/**
 * <p>
 * Model of Customer Order Service line.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CuOrSeSrLn extends ACuOrSeLn {

  /**
   * <p>Service, not null.</p>
   **/
  private SeService service;

  /**
   * <p>Item taxes for item basis non-aggregate method.</p>
   **/
  private List<CuOrSeSrTxLn> itTxs;

  /**
   * <p>Nullable, booking from date1 (include) for bookable service only.</p>
   **/
  private Date dt1;

  /**
   * <p>Nullable, booking till date2 (exclude) for bookable service only.</p>
   **/
  private Date dt2;

  //Simple getters and setters:
  /**
   * <p>Getter for service.</p>
   * @return SeService
   **/
  public final SeService getService() {
    return this.service;
  }

  /**
   * <p>Setter for service.</p>
   * @param pService reference
   **/
  public final void setService(final SeService pService) {
    this.service = pService;
  }

  /**
   * <p>Getter for itTxs.</p>
   * @return List<CuOrSeSrTxLn>
   **/
  public final List<CuOrSeSrTxLn> getItTxs() {
    return this.itTxs;
  }

  /**
   * <p>Setter for itTxs.</p>
   * @param pItTxs reference
   **/
  public final void setItTxs(final List<CuOrSeSrTxLn> pItTxs) {
    this.itTxs = pItTxs;
  }

  /**
   * <p>Getter for dt1.</p>
   * @return Date
   **/
  public final Date getDt1() {
    return this.dt1;
  }

  /**
   * <p>Setter for dt1.</p>
   * @param pDt1 reference
   **/
  public final void setDt1(final Date pDt1) {
    this.dt1 = pDt1;
  }

  /**
   * <p>Getter for dt2.</p>
   * @return Date
   **/
  public final Date getDt2() {
    return this.dt2;
  }

  /**
   * <p>Setter for dt2.</p>
   * @param pDt2 reference
   **/
  public final void setDt2(final Date pDt2) {
    this.dt2 = pDt2;
  }
}
