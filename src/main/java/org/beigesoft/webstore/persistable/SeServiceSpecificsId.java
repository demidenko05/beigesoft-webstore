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

import org.beigesoft.webstore.persistable.base.AItemSpecificsId;

/**
 * <p>
 * Model of ID of Specifics values for a SeService.
 * </p>
 *
 * @author Yury Demidenko
 */
public class SeServiceSpecificsId extends AItemSpecificsId<SeService> {

  /**
   * <p>SeService.</p>
   **/
  private SeService item;

  /**
   * <p>Minimal constructor.</p>
   **/
  public SeServiceSpecificsId() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pSpecifics reference
   * @param pItem reference
   **/
  public SeServiceSpecificsId(final SpecificsOfItem pSpecifics,
    final SeService pItem) {
    this.item = pItem;
    setSpecifics(pSpecifics);
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
   * @param pItem reference
   **/
  @Override
  public final void setItem(final SeService pItem) {
    this.item = pItem;
  }
}
