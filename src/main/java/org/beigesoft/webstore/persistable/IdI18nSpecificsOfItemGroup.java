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
 * <p>ID of I18N name of goods/services specifics group.</p>
 *
 * @author Yury Demidenko
 */
public class IdI18nSpecificsOfItemGroup
  extends AI18nNameId<SpecificsOfItemGroup> {

  /**
   * <p>Internationalized thing.</p>
   **/
  private SpecificsOfItemGroup hasName;

  /**
   * <p>Getter for hasName.</p>
   * @return SpecificsOfItemGroup
   **/
  @Override
  public final SpecificsOfItemGroup getHasName() {
    return this.hasName;
  }

  /**
   * <p>Setter for hasName.</p>
   * @param pHasName reference
   **/
  @Override
  public final void setHasName(final SpecificsOfItemGroup pHasName) {
    this.hasName = pHasName;
  }
}
