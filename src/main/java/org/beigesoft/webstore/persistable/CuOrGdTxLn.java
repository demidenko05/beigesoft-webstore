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

import org.beigesoft.model.IOwned;
import org.beigesoft.webstore.persistable.base.ATaxLn;

/**
 * <p>
 * Order item's tax line for item basis multi-taxes non-aggregate rate.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CuOrGdTxLn extends ATaxLn implements IOwned<CustOrderGdLn> {

  /**
   * <p>Shopping CustOrderGdLn.</p>
   **/
  private CustOrderGdLn itsOwner;

  /**
   * <p>Getter for itsOwner.</p>
   * @return CustOrderGdLn
   **/
  @Override
  public final CustOrderGdLn getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final CustOrderGdLn pItsOwner) {
    this.itsOwner = pItsOwner;
  }
}
