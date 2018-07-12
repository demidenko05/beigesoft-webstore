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

import org.beigesoft.webstore.persistable.base.AItemPlaceId;

/**
 * <p>
 * Model of ID of SeServicePlace.
 * </p>
 *
 * @author Yury Demidenko
 */
public class SeServicePlaceId extends AItemPlaceId<SeService> {

  /**
   * <p>SeService, not null.</p>
   **/
  private SeService item;

  //Simple getters and setters:
  /**
   * <p>Getter for item.</p>
   * @return SeService
   **/
  public final SeService getItem() {
    return this.item;
  }

  /**
   * <p>Setter for item.</p>
   * @param pSeService reference
   **/
  public final void setItem(final SeService pSeService) {
    this.item = pSeService;
  }
}
