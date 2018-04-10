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
import org.beigesoft.webstore.model.ESpecificsItemType;

/**
 * <pre>
 * Model of item specifics e.g. size, form notebook - web-cam, LED-type.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class SpecificsOfItem extends AHasNameIdLongVersion {

  /**
   * <p>Specifics Group, if exist
   * e.g. "Monitor" for its size, web-cam, LED-type.</p>
   **/
  private SpecificsOfItemGroup itsGroop;

  /**
   * <p>Specifics Type described how to treat (edit/print/filter)
   * specifics, not null, default ESpecificsItemType.STRING.</p>
   **/
  private ESpecificsItemType itsType;

  /**
   * <p>If used in filter, default false.</p>
   **/
  private Boolean isUseInFilter;

  /**
   * <p>If show in list, default false - show only in goods page.</p>
   **/
  private Boolean isShowInList;

  /**
   * <p>Index, not null, used for ordering when printing.</p>
   **/
  private Integer itsIndex;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>ChooseableSpecificsType if it is chooseable.</p>
   **/
  private ChooseableSpecificsType chooseableSpecificsType;

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

  /**
   * <p>Getter for itsGroop.</p>
   * @return SpecificsOfItemGroup
   **/
  public final SpecificsOfItemGroup getItsGroop() {
    return this.itsGroop;
  }

  /**
   * <p>Setter for itsGroop.</p>
   * @param pItsGroop reference
   **/
  public final void setItsGroop(final SpecificsOfItemGroup pItsGroop) {
    this.itsGroop = pItsGroop;
  }

  /**
   * <p>Getter for itsType.</p>
   * @return ESpecificsItemType
   **/
  public final ESpecificsItemType getItsType() {
    return this.itsType;
  }

  /**
   * <p>Setter for itsType.</p>
   * @param pItsType reference
   **/
  public final void setItsType(final ESpecificsItemType pItsType) {
    this.itsType = pItsType;
  }

  /**
   * <p>Getter for isUseInFilter.</p>
   * @return Boolean
   **/
  public final Boolean getIsUseInFilter() {
    return this.isUseInFilter;
  }

  /**
   * <p>Setter for isUseInFilter.</p>
   * @param pIsUseInFilter reference
   **/
  public final void setIsUseInFilter(final Boolean pIsUseInFilter) {
    this.isUseInFilter = pIsUseInFilter;
  }

  /**
   * <p>Getter for isShowInList.</p>
   * @return Boolean
   **/
  public final Boolean getIsShowInList() {
    return this.isShowInList;
  }

  /**
   * <p>Setter for isShowInList.</p>
   * @param pIsShowInList reference
   **/
  public final void setIsShowInList(final Boolean pIsShowInList) {
    this.isShowInList = pIsShowInList;
  }

  /**
   * <p>Getter for itsIndex.</p>
   * @return Integer
   **/
  public final Integer getItsIndex() {
    return this.itsIndex;
  }

  /**
   * <p>Setter for itsIndex.</p>
   * @param pItsIndex reference
   **/
  public final void setItsIndex(final Integer pItsIndex) {
    this.itsIndex = pItsIndex;
  }


  /**
   * <p>Getter for chooseableSpecificsType.</p>
   * @return ChooseableSpecificsType
   **/
  public final ChooseableSpecificsType getChooseableSpecificsType() {
    return this.chooseableSpecificsType;
  }

  /**
   * <p>Setter for chooseableSpecificsType.</p>
   * @param pChooseableSpecificsType reference
   **/
  public final void setChooseableSpecificsType(
    final ChooseableSpecificsType pChooseableSpecificsType) {
    this.chooseableSpecificsType = pChooseableSpecificsType;
  }
}
