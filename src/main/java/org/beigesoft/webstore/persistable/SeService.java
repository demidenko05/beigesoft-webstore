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

import org.beigesoft.accounting.persistable.base.AItem;

/**
 * <p>Model of S.E.Service.</p>
 *
 * @author Yury Demidenko
 */
public class SeService extends AItem<SeService, DestTaxSeServiceLn>
  implements IHasSeSeller<Long> {

  /**
   * <p>Seller.</p>
   **/
  private SeSeller seller;

  /**
   * <p>Destination taxes categories and rules.</p>
   **/
  private List<DestTaxSeServiceLn> destinationTaxes;

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

  /**
   * <p>Getter for destTaxes.</p>
   * @return List<DestTaxSeServiceLn>
   **/
  @Override
  public final List<DestTaxSeServiceLn> getDestinationTaxes() {
    return this.destinationTaxes;
  }

  /**
   * <p>Setter for destTaxes.</p>
   * @param pDestTaxes reference
   **/
  @Override
  public final void setDestinationTaxes(
    final List<DestTaxSeServiceLn> pDestTaxes) {
    this.destinationTaxes = pDestTaxes;
  }
}
