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
public class SeServiceSpecifics
  extends AItemSpecifics<SeService, SeServiceSpecificsId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private SeServiceSpecificsId itsId = new SeServiceSpecificsId();

  /**
   * <p>Item specifics.</p>
   **/
  private SpecificsOfItem specifics;

  /**
   * <p>SeService.</p>
   **/
  private SeService item;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final SeServiceSpecificsId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final SeServiceSpecificsId pItsId) {
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
      this.itsId = new SeServiceSpecificsId();
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
  public final SeService getItem() {
    return this.item;
  }

  /**
   * <p>Setter for item.</p>
   * @param pItem reference
   **/
  @Override
  public final void setItem(final SeService pItem) {
    this.item = pItem;
    if (getItsId() == null) {
      setItsId(new SeServiceSpecificsId());
    }
    getItsId().setItem(this.item);
  }
}
