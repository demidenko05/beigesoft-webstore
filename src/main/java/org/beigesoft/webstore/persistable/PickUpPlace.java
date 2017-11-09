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

import org.beigesoft.persistable.AHasNameIdLongVersion;

/**
 * <pre>
 * Model of Pick-Up Place for goods means where it is located,
 * e.g. for small store there is only place e.g. "shop".
 * For a service it means either where is service performed
 * (e.g. haircut saloon) or service maker/s location
 * (for services that performed in the buyer territory
 * e.g. fix faucet by plumber). It's used for goods/service availability
 * and can be used for buyer that prefer pick up goods or
 * get service at chosen place.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class PickUpPlace extends AHasNameIdLongVersion {

  /**
   * <p>Description.</p>
   **/
  private String description;

  //Simple getters and setters:
  /**
   * <p>Getter for description.</p>
   * @return String
   **/
  public final String getDescription() {
    return this.description;
  }

  /**
   * <p>Setter for description.</p>
   * @param pDescription reference
   **/
  public final void setDescription(final String pDescription) {
    this.description = pDescription;
  }
}
