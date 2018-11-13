package org.beigesoft.webstore.persistable;

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

import java.util.List;

import org.beigesoft.accounting.persistable.base.AItem;

/**
 * <p>Model of S.E. Goods.</p>
 *
 * @author Yury Demidenko
 */
public class SeGoods extends AItem<SeGoods, DestTaxSeGoodsLn>
  implements IHasSeSeller<Long> {

  /**
   * <p>Seller.</p>
   **/
  private SeSeller seller;

  /**
   * <p>Destination taxes categories and rules.</p>
   **/
  private List<DestTaxSeGoodsLn> destinationTaxes;

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
   * @return List<DestTaxSeGoodsLn>
   **/
  @Override
  public final List<DestTaxSeGoodsLn> getDestinationTaxes() {
    return this.destinationTaxes;
  }

  /**
   * <p>Setter for destTaxes.</p>
   * @param pDestTaxes reference
   **/
  @Override
  public final void setDestinationTaxes(
    final List<DestTaxSeGoodsLn> pDestTaxes) {
    this.destinationTaxes = pDestTaxes;
  }
}
