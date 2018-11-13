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

import org.beigesoft.accounting.persistable.base.ADestTaxItemLn;

/**
 * <p>
 * Model of item destination tax line.
 * </p>
 *
 * @author Yury Demidenko
 */
public class DestTaxSeGoodsLn extends ADestTaxItemLn<SeGoods> {

  /**
   * <p>Owner.</p>
   **/
  private SeGoods itsOwner;

  /**
   * <p>Geter for itsOwner.</p>
   * @return InvItemTaxCategory
   **/
  @Override
  public final SeGoods getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final SeGoods pItsOwner) {
    this.itsOwner = pItsOwner;
  }
}
