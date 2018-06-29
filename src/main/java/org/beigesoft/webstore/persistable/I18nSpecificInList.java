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


import org.beigesoft.model.IHasId;
import org.beigesoft.model.IHasName;
import org.beigesoft.model.IHasVersion;
import org.beigesoft.model.AEditable;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.persistable.Languages;

/**
 * <p>
 * Model of I18N specifics in list.
 * </p>
 *
 * @author Yury Demidenko
 */
public class I18nSpecificInList extends AEditable
  implements IHasId<IdI18nSpecificInList>, IHasVersion, IHasName {

  /**
   * <p>ID.</p>
   **/
  private IdI18nSpecificInList itsId = new IdI18nSpecificInList();

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

  /**
   * <p>HTML string that briefly describes item in the language.</p>
   **/
  private String specificInList;

  /**
   * <p>Version to check dirty or replication.</p>
   **/
  private Long itsVersion;

  /**
   * <p>Name.</p>
   **/
  private String itsName;

  /**
   * <p>Geter for itsName.</p>
   * @return String
   **/
  @Override
  public final String getItsName() {
    return this.itsName;
  }

  /**
   * <p>Setter for itsName.</p>
   * @param pItsName reference
   **/
  @Override
  public final void setItsName(final String pItsName) {
    this.itsName = pItsName;
  }

  /**
   * <p>Geter for itsVersion.</p>
   * @return Long
   **/
  @Override
  public final Long getItsVersion() {
    return this.itsVersion;
  }

  /**
   * <p>Setter for itsVersion.</p>
   * @param pItsVersion reference
   **/
  @Override
  public final void setItsVersion(final Long pItsVersion) {
    this.itsVersion = pItsVersion;
  }

  /**
   * <p>Getter for itsId.</p>
   * @return IdI18nSpecificInList
   **/
  @Override
  public final IdI18nSpecificInList getItsId() {
    return this.itsId;
  }

  /**
   * <p>Setter for itsId.</p>
   * @param pItsId reference
   **/
  @Override
  public final void setItsId(final IdI18nSpecificInList pItsId) {
    this.itsId = pItsId;
    if (this.itsId == null) {
      this.lang = null;
      this.itemId = null;
      this.itsType = null;
    } else {
      this.lang = this.itsId.getLang();
      this.itemId = this.itsId.getItemId();
      this.itsType = this.itsId.getItsType();
    }
  }

  /**
   * <p>Setter for itemId.</p>
   * @param pItemId reference
   **/
  public final void setItemId(final Long pItemId) {
    this.itemId = pItemId;
    if (this.itsId == null) {
      this.itsId = new IdI18nSpecificInList();
    }
    this.itsId.setItemId(this.itemId);
  }

  /**
   * <p>Setter for itsType.</p>
   * @param pItsType reference
   **/
  public final void setItsType(final EShopItemType pItsType) {
    this.itsType = pItsType;
    if (this.itsId == null) {
      this.itsId = new IdI18nSpecificInList();
    }
    this.itsId.setItsType(this.itsType);
  }

  /**
   * <p>Setter for lang.</p>
   * @param pLang reference
   **/
  public final void setLang(final Languages pLang) {
    this.lang = pLang;
    if (this.itsId == null) {
      this.itsId = new IdI18nSpecificInList();
    }
    this.itsId.setLang(this.lang);
  }

  //Simple getters and setters:
  /**
   * <p>Getter for itemId.</p>
   * @return Long
   **/
  public final Long getItemId() {
    return this.itemId;
  }

  /**
   * <p>Getter for itsType.</p>
   * @return EShopItemType
   **/
  public final EShopItemType getItsType() {
    return this.itsType;
  }

  /**
   * <p>Getter for specificInList.</p>
   * @return String
   **/
  public final String getSpecificInList() {
    return this.specificInList;
  }

  /**
   * <p>Setter for specificInList.</p>
   * @param pSpecificInList reference
   **/
  public final void setSpecificInList(final String pSpecificInList) {
    this.specificInList = pSpecificInList;
  }

  /**
   * <p>Getter for lang.</p>
   * @return Languages
   **/
  public final Languages getLang() {
    return this.lang;
  }
}
