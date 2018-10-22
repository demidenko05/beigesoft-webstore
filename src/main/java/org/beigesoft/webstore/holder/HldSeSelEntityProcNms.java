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

import java.util.Set;

import org.beigesoft.holder.IHolderForClassByName;
import org.beigesoft.orm.processor.PrcEntityRetrieve;
import org.beigesoft.orm.processor.PrcEntityCreate;
import org.beigesoft.orm.processor.PrcEntityFDelete;
import org.beigesoft.orm.processor.PrcEntityFSave;
import org.beigesoft.webstore.persistable.IHasSeSeller;
import org.beigesoft.webstore.persistable.SeGoodsSpecifics;
import org.beigesoft.webstore.persistable.SeServiceSpecifics;
import org.beigesoft.webstore.processor.PrcHasSeSellerSave;
import org.beigesoft.webstore.processor.PrcHasSeSellerDel;
import org.beigesoft.webstore.processor.PrcSeGoodsSpecSave;
import org.beigesoft.webstore.processor.PrcSeServiceSpecSave;
import org.beigesoft.webstore.processor.PrcSeGdSpecEmbFlSave;
import org.beigesoft.webstore.processor.PrcSeGdSpecEmbFlDel;
import org.beigesoft.webstore.processor.PrcSeSrvSpecEmbFlSave;
import org.beigesoft.webstore.processor.PrcSeSrvSpecEmbFlDel;

/**
 * <p>Service that assign IEntityProcessor and IProcessor name for class
 * and action name for S.E. seller only.</p>
 *
 * @author Yury Demidenko
 */
public class HldSeSelEntityProcNms implements IHolderForClassByName<String> {

  /**
   * <p>Shared entities. Only <b>list</b> operation is allowed, no "modify".</p>
   **/
  private Set<Class<?>> sharedEntities;

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
    } else if ("entityFSave".equals(pThingName)) {
      return getForFSave(pClass);
    } else if ("entityEFSave".equals(pThingName)) {
      return getForEFSave(pClass);
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
    } else if ("entityFDelete".equals(pThingName)) {
      return getForFDelete(pClass);
    } else if ("entityEFDelete".equals(pThingName)) {
      return getForEFDelete(pClass);
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
    throw new RuntimeException("Setting is not allowed!");
  }

  /**
   * <p>Get processor name for copy.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForCopy(final Class<?> pClass) {
    if (this.sharedEntities.contains(pClass)) {
      return null;
    } else if (IHasSeSeller.class.isAssignableFrom(pClass)) {
      return PrcEntityRetrieve.class.getSimpleName();
    }
    return null;
  }

  /**
   * <p>Get processor name for print.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForPrint(final Class<?> pClass) {
    if (this.sharedEntities.contains(pClass)) {
      return null;
    } else if (IHasSeSeller.class.isAssignableFrom(pClass)) {
      return PrcEntityRetrieve.class.getSimpleName();
    }
    return null;
  }

  /**
   * <p>Get processor name for save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForSave(final Class<?> pClass) {
    if (this.sharedEntities.contains(pClass)) {
      return null;
    } else if (SeServiceSpecifics.class == pClass) {
      return PrcSeServiceSpecSave.class.getSimpleName();
    } else if (SeGoodsSpecifics.class == pClass) {
      return PrcSeGoodsSpecSave.class.getSimpleName();
    } else if (IHasSeSeller.class.isAssignableFrom(pClass)) {
      return PrcHasSeSellerSave.class.getSimpleName();
    }
    return null;
  }

  /**
   * <p>Get processor name for Entity with file save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFSave(final Class<?> pClass) {
    if (SeGoodsSpecifics.class == pClass
      || SeServiceSpecifics.class == pClass) {
      return PrcEntityFSave.class.getSimpleName();
    }
    return null;
  }

  /**
   * <p>Get processor name for specifics with embed file save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForEFSave(final Class<?> pClass) {
    if (SeGoodsSpecifics.class == pClass) {
      return PrcSeGdSpecEmbFlSave.class.getSimpleName();
    } else if (SeServiceSpecifics.class == pClass) {
      return PrcSeSrvSpecEmbFlSave.class.getSimpleName();
    }
    return null;
  }

  /**
   * <p>Get processor name for Entity with file delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFDelete(final Class<?> pClass) {
    if (SeGoodsSpecifics.class == pClass
      || SeServiceSpecifics.class == pClass) {
      return PrcEntityFDelete.class.getSimpleName();
    }
    return null;
  }

  /**
   * <p>Get processor name for item specifics with embed HTML for delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForEFDelete(final Class<?> pClass) {
    if (SeGoodsSpecifics.class == pClass) {
      return PrcSeGdSpecEmbFlDel.class.getSimpleName();
    } else if (SeServiceSpecifics.class == pClass) {
      return PrcSeSrvSpecEmbFlDel.class.getSimpleName();
    }
    return null;
  }

  /**
   * <p>Get processor name for FFOL delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFfolDelete(final Class<?> pClass) {
    if (this.sharedEntities.contains(pClass)) {
      return null;
    }
    return null;
  }

  /**
   * <p>Get processor name for FFOL save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFfolSave(final Class<?> pClass) {
    if (this.sharedEntities.contains(pClass)) {
      return null;
    }
    return null;
  }

  /**
   * <p>Get processor name for FOL delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFolDelete(final Class<?> pClass) {
    if (this.sharedEntities.contains(pClass)) {
      return null;
    }
    return null;
  }

  /**
   * <p>Get processor name for FOL save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFolSave(final Class<?> pClass) {
    if (this.sharedEntities.contains(pClass)) {
      return null;
    }
    return null;
  }

  /**
   * <p>Get processor name for delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForDelete(final Class<?> pClass) {
    if (this.sharedEntities.contains(pClass)) {
      return null;
    } else if (IHasSeSeller.class.isAssignableFrom(pClass)) {
      return PrcHasSeSellerDel.class.getSimpleName();
    }
    return null;
  }

  /**
   * <p>Get processor name for create.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForCreate(final Class<?> pClass) {
    if (this.sharedEntities.contains(pClass)) {
      return null;
    } else if (IHasSeSeller.class.isAssignableFrom(pClass)) {
      return PrcEntityCreate.class.getSimpleName();
    }
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
    if (this.sharedEntities.contains(pClass)) {
      return null;
    } else if (IHasSeSeller.class.isAssignableFrom(pClass)) {
      return PrcEntityRetrieve.class.getSimpleName();
    }
    return null;
  }

  //Simple getters and setters:

  /**
   * <p>Getter for sharedEntities.</p>
   * @return Set<Class<?>>
   **/
  public final Set<Class<?>> getSharedEntities() {
    return this.sharedEntities;
  }

  /**
   * <p>Setter for sharedEntities.</p>
   * @param pSharedEntities reference
   **/
  public final void setSharedEntities(final Set<Class<?>> pSharedEntities) {
    this.sharedEntities = pSharedEntities;
  }
}
