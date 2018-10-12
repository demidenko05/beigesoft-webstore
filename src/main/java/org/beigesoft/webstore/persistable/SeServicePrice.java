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

import org.beigesoft.webstore.persistable.base.AItemPrice;

/**
 * <p>
 * Model of service price.
 * </p>
 *
 * @author Yury Demidenko
 */
public class SeServicePrice extends AItemPrice<SeService, SeServicePriceId>
 implements IHasSeSeller<SeServicePriceId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private SeServicePriceId itsId = new SeServicePriceId();

  /**
   * <p>Price Category.</p>
   **/
  private PriceCategory priceCategory;

  /**
   * <p>SeService, not null.</p>
   **/
  private SeService item;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final SeServicePriceId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final SeServicePriceId pItsId) {
    this.itsId = pItsId;
    if (this.itsId != null) {
      this.priceCategory = this.itsId.getPriceCategory();
      this.item = this.itsId.getItem();
    } else {
      this.priceCategory = null;
      this.item = null;
    }
  }

  /**
   * <p>Getter for priceCategory.</p>
   * @return PriceCategory
   **/
  @Override
  public final PriceCategory getPriceCategory() {
    return this.priceCategory;
  }

  /**
   * <p>Setter for priceCategory.</p>
   * @param pPriceCategory reference
   **/
  @Override
  public final void setPriceCategory(final PriceCategory pPriceCategory) {
    this.priceCategory = pPriceCategory;
    if (this.itsId == null) {
      this.itsId = new SeServicePriceId();
    }
    this.itsId.setPriceCategory(this.priceCategory);
  }

  /**
   * <p>Getter for item.</p>
   * @return SeService
   **/
  @Override
  public final SeService getItem() {
    return this.item;
  }

  /**
   * <p>Setter for item.</p>
   * @param pSeService reference
   **/
  @Override
  public final void setItem(final SeService pSeService) {
    this.item = pSeService;
    if (this.itsId == null) {
      this.itsId = new SeServicePriceId();
    }
    this.itsId.setItem(this.item);
  }

  /**
   * <p>Getter for seller.</p>
   * @return SeSeller
   **/
  @Override
  public final SeSeller getSeller() {
    return this.item.getSeller();
  }

  /**
   * <p>Setter for seller.</p>
   * @param pSeller reference
   **/
  @Override
  public final void setSeller(final SeSeller pSeller) {
    this.item.setSeller(pSeller);
  }
}
