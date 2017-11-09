package org.beigesoft.webstore.persistable;

/*
 * Copyright (c) 2017 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import org.beigesoft.webstore.persistable.base.ACustomerOrderLine;
import org.beigesoft.accounting.persistable.ServiceToSale;

/**
 * <p>
 * Model of Customer Order Service line.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CustomerOrderService extends ACustomerOrderLine {

  /**
   * <p>Service, not null.</p>
   **/
  private ServiceToSale service;

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
}
