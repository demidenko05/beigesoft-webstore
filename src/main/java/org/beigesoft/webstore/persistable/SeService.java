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

import java.util.List;

import org.beigesoft.persistable.AHasNameIdLongVersion;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;

/**
 * <p>Model of S.E.Service.</pre>
 *
 * @author Yury Demidenko
 */
public class SeService extends AHasNameIdLongVersion
  implements IHasSeSeller<Long> {

  /**
   * <p>Seller.</p>
   **/
  private SeSeller seller;

  /**
   * <p>Origin tax category e.g. "NY: tax1 10%, tax2 5%".</p>
   **/
  private InvItemTaxCategory txCat;

  /**
   * <p>Destination taxes categories and rules.</p>
   **/
  private List<DestTaxSeServiceLn> destTaxes;

  /**
   * <p>Getter for seller.</p>
   * @return SeSeller
   **/
  @Override
  public final SeSeller getSeller() {
    return this.seller;
  }

  /**
   * <p>Setter for seller.</p>
   * @param pSeller reference
   **/
  @Override
  public final void setSeller(final SeSeller pSeller) {
    this.seller = pSeller;
  }

  //Simple getters and setters:
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

  /**
   * <p>Getter for destTaxes.</p>
   * @return List<DestTaxSeServiceLn>
   **/
  public final List<DestTaxSeServiceLn> getDestTaxes() {
    return this.destTaxes;
  }

  /**
   * <p>Setter for destTaxes.</p>
   * @param pDestTaxes reference
   **/
  public final void setDestTaxes(final List<DestTaxSeServiceLn> pDestTaxes) {
    this.destTaxes = pDestTaxes;
  }
}
