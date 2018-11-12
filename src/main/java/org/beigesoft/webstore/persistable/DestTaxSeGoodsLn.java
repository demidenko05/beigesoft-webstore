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
import org.beigesoft.persistable.AHasIdLongVersion;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.TaxDestination;

/**
 * <p>
 * Model of item destination tax line.
 * </p>
 *
 * @author Yury Demidenko
 */
public class DestTaxSeGoodsLn extends AHasIdLongVersion
  implements IOwned<SeGoods> {

  /**
   * <p>Owner.</p>
   **/
  private SeGoods itsOwner;

  /**
   * <p>Tax destination, not null.</p>
   **/
  private TaxDestination txDest;

  /**
   * <p>Tax category, null if no taxes applies for this place.</p>
   **/
  private InvItemTaxCategory txCat;

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

  //Simple getters and setters:
  /**
   * <p>Getter for txDest.</p>
   * @return TaxDestination
   **/
  public final TaxDestination getTxDest() {
    return this.txDest;
  }

  /**
   * <p>Setter for txDest.</p>
   * @param pTxDest reference
   **/
  public final void setTxDest(final TaxDestination pTxDest) {
    this.txDest = pTxDest;
  }

  /**
   * <p>Getter for txCat.</p>
   * @return InvItemTaxCategory
   **/
  public final InvItemTaxCategory getTxCat() {
    return this.txCat;
  }

  /**
   * <p>Setter for txCat.</p>
   * @param pTxCat reference
   **/
  public final void setTxCat(final InvItemTaxCategory pTxCat) {
    this.txCat = pTxCat;
  }
}
