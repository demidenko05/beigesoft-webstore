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

import org.beigesoft.webstore.persistable.base.AItemSpecifics;

/**
 * <p>
 * Model of Specifics values for a item.
 * </p>
 *
 * @author Yury Demidenko
 */
public class SeGoodsSpecifics
 extends AItemSpecifics<SeGoods, SeGoodsSpecificsId>
    implements IHasSeSeller<SeGoodsSpecificsId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private SeGoodsSpecificsId itsId = new SeGoodsSpecificsId();

  /**
   * <p>Item specifics.</p>
   **/
  private SpecificsOfItem specifics;

  /**
   * <p>SeGoods.</p>
   **/
  private SeGoods item;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final SeGoodsSpecificsId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final SeGoodsSpecificsId pItsId) {
    this.itsId = pItsId;
    if (this.itsId != null) {
      setSpecifics(this.itsId.getSpecifics());
      setItem(this.itsId.getItem());
    } else {
      setSpecifics(null);
      setItem(null);
    }
  }

  /**
   * <p>Setter for specifics.</p>
   * @param pSpecifics reference
   **/
  @Override
  public final void setSpecifics(final SpecificsOfItem pSpecifics) {
    this.specifics = pSpecifics;
    if (this.itsId == null) {
      this.itsId = new SeGoodsSpecificsId();
    }
    this.itsId.setSpecifics(this.specifics);
  }

  /**
   * <p>Getter for specifics.</p>
   * @return SpecificsOfItem
   **/
  @Override
  public final SpecificsOfItem getSpecifics() {
    return this.specifics;
  }

  /**
   * <p>Getter for item.</p>
   * @return T
   **/
  @Override
  public final SeGoods getItem() {
    return this.item;
  }

  /**
   * <p>Setter for item.</p>
   * @param pItem reference
   **/
  @Override
  public final void setItem(final SeGoods pItem) {
    this.item = pItem;
    if (getItsId() == null) {
      setItsId(new SeGoodsSpecificsId());
    }
    getItsId().setItem(this.item);
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
