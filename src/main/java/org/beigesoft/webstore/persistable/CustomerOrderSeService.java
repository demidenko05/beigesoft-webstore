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

/**
 * <p>
 * Model of Customer Order S.E.Service line.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CustomerOrderSeService extends ACustomerOrderLine {

  /**
   * <p>Service, not null.</p>
   **/
  private SeService service;

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
}
