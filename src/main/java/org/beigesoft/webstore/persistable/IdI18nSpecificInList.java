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


import org.beigesoft.persistable.Languages;
import org.beigesoft.webstore.model.EShopItemType;

/**
 * <p>
 * Model of ID of I18N specifics in list.
 * </p>
 *
 * @author Yury Demidenko
 */
public class IdI18nSpecificInList {

  /**
   * <p>Goods/Service/SEGoods/SEService ID, not null.</p>
   **/
  private Long itemId;

  /**
   * <p>Goods/Service/SEGoods/SEService, not null.</p>
   **/
  private EShopItemType itsType;

  /**
   * <p>The language.</p>
   **/
  private Languages lang;

  //Simple getters and setters:
  /**
   * <p>Getter for itemId.</p>
   * @return Long
   **/
  public final Long getItemId() {
    return this.itemId;
  }

  /**
   * <p>Setter for itemId.</p>
   * @param pItemId reference
   **/
  public final void setItemId(final Long pItemId) {
    this.itemId = pItemId;
  }

  /**
   * <p>Getter for itsType.</p>
   * @return EShopItemType
   **/
  public final EShopItemType getItsType() {
    return this.itsType;
  }

  /**
   * <p>Setter for itsType.</p>
   * @param pItsType reference
   **/
  public final void setItsType(final EShopItemType pItsType) {
    this.itsType = pItsType;
  }

  /**
   * <p>Getter for lang.</p>
   * @return Languages
   **/
  public final Languages getLang() {
    return this.lang;
  }

  /**
   * <p>Setter for lang.</p>
   * @param pLang reference
   **/
  public final void setLang(final Languages pLang) {
    this.lang = pLang;
  }
}
