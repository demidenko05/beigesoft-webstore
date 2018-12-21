package org.beigesoft.webstore.persistable;

/*
 * Copyright (c) 2017 Beigesoft™
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

import org.beigesoft.webstore.persistable.base.ACustOrderLn;
import org.beigesoft.accounting.persistable.ServiceToSale;

/**
 * <p>
 * Model of Customer Order Service line.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CustOrderSrvLn extends ACustOrderLn {

  /**
   * <p>Service, not null.</p>
   **/
  private ServiceToSale service;

  /**
   * <p>Item taxes for item basis non-aggregate method.</p>
   **/
  private List<CuOrSrTxLn> itTxs;

  //Simple getters and setters:
  /**
   * <p>Getter for service.</p>
   * @return ServiceToSale
   **/
  public final ServiceToSale getService() {
    return this.service;
  }

  /**
   * <p>Setter for service.</p>
   * @param pService reference
   **/
  public final void setService(final ServiceToSale pService) {
    this.service = pService;
  }

  /**
   * <p>Getter for itTxs.</p>
   * @return List<CuOrSrTxLn>
   **/
  public final List<CuOrSrTxLn> getItTxs() {
    return this.itTxs;
  }

  /**
   * <p>Setter for itTxs.</p>
   * @param pItTxs reference
   **/
  public final void setItTxs(final List<CuOrSrTxLn> pItTxs) {
    this.itTxs = pItTxs;
  }
}
