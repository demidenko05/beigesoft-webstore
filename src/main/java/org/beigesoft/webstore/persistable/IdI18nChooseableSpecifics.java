package org.beigesoft.webstore.persistable;

/*
 * Copyright (c) 2018 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import org.beigesoft.persistable.AI18nNameId;

/**
 * <p>
 * ID of I18N name of goods/services chooseable specifics.
 * </p>
 *
 * @author Yury Demidenko
 */
public class IdI18nChooseableSpecifics
  extends AI18nNameId<ChooseableSpecifics> {

  /**
   * <p>Internationalized thing.</p>
   **/
  private ChooseableSpecifics hasName;

  /**
   * <p>Getter for hasName.</p>
   * @return ChooseableSpecifics
   **/
  @Override
  public final ChooseableSpecifics getHasName() {
    return this.hasName;
  }

  /**
   * <p>Setter for hasName.</p>
   * @param pHasName reference
   **/
  @Override
  public final void setHasName(final ChooseableSpecifics pHasName) {
    this.hasName = pHasName;
  }
}
