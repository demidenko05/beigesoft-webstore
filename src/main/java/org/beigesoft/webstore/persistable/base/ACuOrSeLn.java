package org.beigesoft.webstore.persistable.base;

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
import org.beigesoft.webstore.persistable.CuOrSe;

/**
 * <p>
 * Model of Customer Order Item line.
 * Item can be S.E.goods/service.
 * </p>
 *
 * @author Yury Demidenko
 */
public class ACuOrSeLn extends AOrdLn implements IOwned<CuOrSe> {

  /**
   * <p>Customer Order.</p>
   **/
  private CuOrSe itsOwner;

  /**
   * <p>Getter for itsOwner.</p>
   * @return CuOrSe
   **/
  @Override
  public final CuOrSe getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final CuOrSe pItsOwner) {
    this.itsOwner = pItsOwner;
  }
}
