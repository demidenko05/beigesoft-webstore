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

import org.beigesoft.persistable.AI18nName;
import org.beigesoft.persistable.Languages;

/**
 * <p>
 * Model of I18N name of S.E. goods.
 * </p>
 *
 * @author Yury Demidenko
 */
public class I18nSeGoods extends AI18nName<SeGoods, IdI18nSeGoods>
 implements IHasSeSeller<IdI18nSeGoods> {

  /**
   * <p>ID.</p>
   **/
  private IdI18nSeGoods itsId = new IdI18nSeGoods();

  /**
   * <p>Internationalized thing.</p>
   **/
  private SeGoods hasName;

  /**
   * <p>The language.</p>
   **/
  private Languages lang;

  /**
   * <p>Getter for itsId.</p>
   * @return IdI18nSeGoods
   **/
  @Override
  public final IdI18nSeGoods getItsId() {
    return this.itsId;
  }

  /**
   * <p>Setter for itsId.</p>
   * @param pItsId reference
   **/
  @Override
  public final void setItsId(final IdI18nSeGoods pItsId) {
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
      this.itsId = new IdI18nSeGoods();
    }
    this.itsId.setLang(this.lang);
  }

  /**
   * <p>Setter for hasName.</p>
   * @param pHasName reference
   **/
  @Override
  public final void setHasName(final SeGoods pHasName) {
    this.hasName = pHasName;
    if (this.itsId == null) {
      this.itsId = new IdI18nSeGoods();
    }
    this.itsId.setHasName(this.hasName);
  }

  /**
   * <p>Getter for hasName.</p>
   * @return SeGoods
   **/
  @Override
  public final SeGoods getHasName() {
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

  /**
   * <p>Getter for seller.</p>
   * @return SeSeller
   **/
  @Override
  public final SeSeller getSeller() {
    return this.hasName.getSeller();
  }

  /**
   * <p>Setter for seller.</p>
   * @param pSeller reference
   **/
  @Override
  public final void setSeller(final SeSeller pSeller) {
    this.hasName.setSeller(pSeller);
  }
}
