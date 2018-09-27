package org.beigesoft.webstore.holder;

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

import org.beigesoft.holder.IHolderForClassByName;

/**
 * <p>Service that assign IEntityProcessor and IProcessor name for class
 * and action name for S.E. seller only.</p>
 *
 * @author Yury Demidenko
 */
public class HldSeSelEntityProcNms implements IHolderForClassByName<String> {

  /**
   * <p>Get processor name for given class and action name.</p>
   * @param pClass a Class
   * @param pThingName Thing Name
   * @return a thing
   **/
  @Override
  public final String getFor(final Class<?> pClass, final String pThingName) {
    if ("entityEdit".equals(pThingName)
      || "entityConfirmDelete".equals(pThingName)) {
      return getForRetrieveForEditDelete(pClass, pThingName);
    } else if ("entityCopy".equals(pThingName)) {
      return getForCopy(pClass);
    } else if ("entityPrint".equals(pThingName)) {
      return getForPrint(pClass);
    } else if ("entitySave".equals(pThingName)) {
      return getForSave(pClass);
    } else if ("entityFfolDelete".equals(pThingName)) {
      return getForFfolDelete(pClass);
    } else if ("entityFfolSave".equals(pThingName)) {
      return getForFfolSave(pClass);
    } else if ("entityFolDelete".equals(pThingName)) {
      return getForFolDelete(pClass);
    } else if ("entityFolSave".equals(pThingName)) {
      return getForFolSave(pClass);
    } else if ("entityDelete".equals(pThingName)) {
      return getForDelete(pClass);
    } else if ("entityCreate".equals(pThingName)) {
      return getForCreate(pClass);
    }
    return null;
  }

  /**
   * <p>Set thing for given class and thing name.</p>
   * @param pThing Thing
   * @param pClass Class
   * @param pThingName Thing Name
   **/
  @Override
  public final void setFor(final String pThing,
    final Class<?> pClass, final String pThingName) {
    //hard-coded
  }

  /**
   * <p>Get processor name for copy.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForCopy(final Class<?> pClass) {
    return null;
  }

  /**
   * <p>Get processor name for print.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForPrint(final Class<?> pClass) {
    return null;
  }

  /**
   * <p>Get processor name for save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForSave(final Class<?> pClass) {
    return null;
  }

  /**
   * <p>Get processor name for FFOL delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFfolDelete(final Class<?> pClass) {
    return null;
  }

  /**
   * <p>Get processor name for FFOL save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFfolSave(final Class<?> pClass) {
    return null;
  }

  /**
   * <p>Get processor name for FOL delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFolDelete(final Class<?> pClass) {
    return null;
  }

  /**
   * <p>Get processor name for FOL save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFolSave(final Class<?> pClass) {
    return null;
  }

  /**
   * <p>Get processor name for delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForDelete(final Class<?> pClass) {
    return null;
  }

  /**
   * <p>Get processor name for create.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForCreate(final Class<?> pClass) {
    return null;
  }

  /**
   * <p>Get processor name for retrieve to edit/delete.</p>
   * @param pClass a Class
   * @param pAction Action
   * @return a thing
   **/
  protected final String getForRetrieveForEditDelete(final Class<?> pClass,
    final String pAction) {
    return null;
  }
}
