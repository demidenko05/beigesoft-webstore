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

import org.beigesoft.persistable.AI18nName;
import org.beigesoft.persistable.Languages;

/**
 * <p>
 * Model of I18N name of goods/services specifics.
 * </p>
 *
 * @author Yury Demidenko
 */
public class I18nSpecificsOfItem
  extends AI18nName<SpecificsOfItem, IdI18nSpecificsOfItem> {

  /**
   * <p>ID.</p>
   **/
  private IdI18nSpecificsOfItem itsId = new IdI18nSpecificsOfItem();

  /**
   * <p>Internationalized thing.</p>
   **/
  private SpecificsOfItem hasName;

  /**
   * <p>The language.</p>
   **/
  private Languages lang;

  /**
   * <p>Getter for itsId.</p>
   * @return IdI18nSpecificsOfItem
   **/
  @Override
  public final IdI18nSpecificsOfItem getItsId() {
    return this.itsId;
  }

  /**
   * <p>Setter for itsId.</p>
   * @param pItsId reference
   **/
  @Override
  public final void setItsId(final IdI18nSpecificsOfItem pItsId) {
    this.itsId = pItsId;
    if (this.itsId == null) {
      this.lang = null;
      this.hasName = null;
    } else {
      this.lang = this.itsId.getLang();
      this.hasName = this.itsId.getHasName();
    }
  }

  /**
   * <p>Setter for lang.</p>
   * @param pLang reference
   **/
  @Override
  public final void setLang(final Languages pLang) {
    this.lang = pLang;
    if (this.itsId == null) {
      this.itsId = new IdI18nSpecificsOfItem();
    }
    this.itsId.setLang(this.lang);
  }

  /**
   * <p>Setter for hasName.</p>
   * @param pHasName reference
   **/
  @Override
  public final void setHasName(final SpecificsOfItem pHasName) {
    this.hasName = pHasName;
    if (this.itsId == null) {
      this.itsId = new IdI18nSpecificsOfItem();
    }
    this.itsId.setHasName(this.hasName);
  }

  /**
   * <p>Getter for hasName.</p>
   * @return SpecificsOfItem
   **/
  @Override
  public final SpecificsOfItem getHasName() {
    return this.hasName;
  }

  /**
   * <p>Getter for lang.</p>
   * @return Languages
   **/
  @Override
  public final Languages getLang() {
    return this.lang;
  }
}
