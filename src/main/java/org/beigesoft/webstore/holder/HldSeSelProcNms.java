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
 * <p>Service that assign IProcessor name for class
 * and action name for S.E. seller only.</p>
 *
 * @author Yury Demidenko
 */
public class HldSeSelProcNms implements IHolderForClassByName<String> {

  /**
   * <p>Get processor name for given class and action name.</p>
   * @param pClass a Class
   * @param pThingName Thing Name
   * @return a thing
   **/
  @Override
  public final String getFor(final Class<?> pClass, final String pThingName) {
    if ("list".equals(pThingName)) {
      return null;
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
}
