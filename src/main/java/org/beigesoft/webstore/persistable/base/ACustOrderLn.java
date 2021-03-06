package org.beigesoft.webstore.persistable.base;

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

import org.beigesoft.model.IOwned;
import org.beigesoft.webstore.persistable.CustOrder;

/**
 * <p>
 * Model of Customer Order Item line.
 * Item can be goods/service.
 * </p>
 *
 * @author Yury Demidenko
 */
public class ACustOrderLn extends AOrdLn implements IOwned<CustOrder> {

  /**
   * <p>Customer Order.</p>
   **/
  private CustOrder itsOwner;

  /**
   * <p>Getter for itsOwner.</p>
   * @return CustOrder
   **/
  @Override
  public final CustOrder getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final CustOrder pItsOwner) {
    this.itsOwner = pItsOwner;
  }
}
