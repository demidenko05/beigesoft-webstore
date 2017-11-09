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
 * Model of Chooseable Specifics of Items, it's picked from list.
 * ID auto-generated.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class ChooseableSpecifics extends AHasNameIdLongVersion {

  /**
   * <p>Its type, e.g. "Operation systems" for "Linux".</p>
   **/
  private ChooseableSpecificsType itsType;

  //Simple getters and setters:
  /**
   * <p>Getter for itsType.</p>
   * @return ChooseableSpecificsType
   **/
  public final ChooseableSpecificsType getItsType() {
    return this.itsType;
  }

  /**
   * <p>Setter for itsType.</p>
   * @param pItsType reference
   **/
  public final void setItsType(final ChooseableSpecificsType pItsType) {
    this.itsType = pItsType;
  }
}
