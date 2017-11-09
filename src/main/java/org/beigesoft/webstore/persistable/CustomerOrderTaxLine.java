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

import org.beigesoft.model.IOwned;
import org.beigesoft.webstore.persistable.base.ATaxLine;

/**
 * <p>
 * Customer Order Tax Line model.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CustomerOrderTaxLine extends ATaxLine
  implements IOwned<CustomerOrder> {

  /**
   * <p>Customer Order.</p>
   **/
  private CustomerOrder itsOwner;

  /**
   * <p>Getter for itsOwner.</p>
   * @return CustomerOrder
   **/
  @Override
  public final CustomerOrder getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final CustomerOrder pItsOwner) {
    this.itsOwner = pItsOwner;
  }
}
