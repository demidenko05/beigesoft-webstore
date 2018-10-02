package org.beigesoft.webstore.holder;

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

import java.util.Set;

import org.beigesoft.persistable.IPersistableBase;
import org.beigesoft.holder.IHolderForClassByName;
import org.beigesoft.orm.processor.PrcEntityFfolDelete;
import org.beigesoft.orm.processor.PrcEntityFfolSave;
import org.beigesoft.orm.processor.PrcEntityRetrieve;
import org.beigesoft.orm.processor.PrcEntityPbEditDelete;
import org.beigesoft.orm.processor.PrcEntityDelete;
import org.beigesoft.orm.processor.PrcEntityFolDelete;
import org.beigesoft.orm.processor.PrcEntityPbDelete;
import org.beigesoft.orm.processor.PrcEntityCreate;
import org.beigesoft.orm.processor.PrcEntityFolSave;
import org.beigesoft.orm.processor.PrcEntityPbCopy;
import org.beigesoft.orm.processor.PrcEntitySave;
import org.beigesoft.orm.processor.PrcEntityPbSave;
import org.beigesoft.orm.processor.PrcEntityCopy;
import org.beigesoft.webstore.persistable.SettingsAdd;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.SubcatalogsCatalogsGs;
import org.beigesoft.webstore.persistable.AdvisedGoodsForGoods;
import org.beigesoft.webstore.persistable.GoodsAdviseCategories;
import org.beigesoft.webstore.persistable.GoodsSpecifics;
import org.beigesoft.webstore.persistable.ServiceSpecifics;
import org.beigesoft.webstore.persistable.GoodsCatalog;
import org.beigesoft.webstore.persistable.ServiceCatalog;
import org.beigesoft.webstore.processor.PrcAdvisedGoodsForGoodsSave;
import org.beigesoft.webstore.processor.PrcItemCatalogSave;
import org.beigesoft.webstore.processor.PrcSubcatalogsCatalogsGsSave;
import org.beigesoft.webstore.processor.PrcGoodsAdviseCategoriesSave;
import org.beigesoft.webstore.processor.PrcItemSpecificsSave;
import org.beigesoft.webstore.processor.PrcItemSpecificsRetrieve;
import org.beigesoft.webstore.processor.PrcItemSpecificsDelete;
import org.beigesoft.webstore.processor.PrcSettingsAddSave;
import org.beigesoft.webstore.processor.PrcTradingSettingsSave;

/**
 * <p>Standalone service that assign entities processor name for class
 * and action name for web-store administrator.</p>
 *
 * @author Yury Demidenko
 */
public class HldTradeEntitiesProcessorNames
  implements IHolderForClassByName<String> {

  /**
   * <p>S.E.Seller's entities.</p>
   **/
  private Set<Class<?>> seEntities;

  /**
   * <p>Get thing for given class and thing name.</p>
   * @param pClass a Class
   * @param pThingName Thing Name
   * @return a thing
   **/
  @Override
  public final String getFor(final Class<?> pClass, final String pThingName) {
    if ("entityEdit".equals(pThingName)
      || "entityConfirmDelete".equals(pThingName)) {
      return getForRetrieveForEditDelete(pClass);
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
    //nothing
  }

  /**
   * <p>Get processor name for copy.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForCopy(final Class<?> pClass) {
    if (this.seEntities.contains(pClass)) {
      return null;
    }
    if (pClass == ServiceSpecifics.class || pClass == GoodsSpecifics.class) {
      return PrcItemSpecificsRetrieve.class.getSimpleName();
    } else if (IPersistableBase.class.isAssignableFrom(pClass)) {
      return PrcEntityPbCopy.class.getSimpleName();
    }
    return PrcEntityCopy.class.getSimpleName();
  }

  /**
   * <p>Get processor name for print.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForPrint(final Class<?> pClass) {
    if (pClass == ServiceSpecifics.class || pClass == GoodsSpecifics.class) {
      return PrcItemSpecificsRetrieve.class.getSimpleName();
    }
    return PrcEntityRetrieve.class.getSimpleName();
  }

  /**
   * <p>Get processor name for save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForSave(final Class<?> pClass) {
    if (this.seEntities.contains(pClass)) {
      return null;
    }
    if (pClass == AdvisedGoodsForGoods.class) {
      return PrcAdvisedGoodsForGoodsSave.class.getSimpleName();
    } else if (pClass == SettingsAdd.class) {
      return PrcSettingsAddSave.class.getSimpleName();
    } else if (pClass == TradingSettings.class) {
      return PrcTradingSettingsSave.class.getSimpleName();
    } else if (pClass == ServiceCatalog.class || pClass == GoodsCatalog.class) {
      return PrcItemCatalogSave.class.getSimpleName();
    } else if (pClass == SubcatalogsCatalogsGs.class) {
      return PrcSubcatalogsCatalogsGsSave.class.getSimpleName();
    } else if (pClass == GoodsAdviseCategories.class) {
      return PrcGoodsAdviseCategoriesSave.class.getSimpleName();
    } else if (pClass == ServiceSpecifics.class
      || pClass == GoodsSpecifics.class) {
      return PrcItemSpecificsSave.class.getSimpleName();
    } else if (IPersistableBase.class.isAssignableFrom(pClass)) {
      return PrcEntityPbSave.class.getSimpleName();
    }
    return PrcEntitySave.class.getSimpleName();
  }

  /**
   * <p>Get processor name for FFOL delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFfolDelete(final Class<?> pClass) {
    if (this.seEntities.contains(pClass)) {
      return null;
    }
    return PrcEntityFfolDelete.class.getSimpleName();
  }

  /**
   * <p>Get processor name for FFOL save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFfolSave(final Class<?> pClass) {
    if (this.seEntities.contains(pClass)) {
      return null;
    }
    return PrcEntityFfolSave.class.getSimpleName();
  }

  /**
   * <p>Get processor name for FOL delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFolDelete(final Class<?> pClass) {
    if (this.seEntities.contains(pClass)) {
      return null;
    }
    return PrcEntityFolDelete.class.getSimpleName();
  }

  /**
   * <p>Get processor name for FOL save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFolSave(final Class<?> pClass) {
    if (this.seEntities.contains(pClass)) {
      return null;
    }
    return PrcEntityFolSave.class.getSimpleName();
  }

  /**
   * <p>Get processor name for delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForDelete(final Class<?> pClass) {
    if (this.seEntities.contains(pClass)) {
      return null;
    }
    if (pClass == ServiceSpecifics.class || pClass == GoodsSpecifics.class) {
      return PrcItemSpecificsDelete.class.getSimpleName();
    } else if (IPersistableBase.class.isAssignableFrom(pClass)) {
      return PrcEntityPbDelete.class.getSimpleName();
    }
    return PrcEntityDelete.class.getSimpleName();
  }

  /**
   * <p>Get processor name for create.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForCreate(final Class<?> pClass) {
    if (this.seEntities.contains(pClass)) {
      return null;
    }
    return PrcEntityCreate.class.getSimpleName();
  }

  /**
   * <p>Get processor name for retrieve to edit/delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForRetrieveForEditDelete(final Class<?> pClass) {
    if (this.seEntities.contains(pClass)) {
      return null;
    } else if (pClass == ServiceSpecifics.class
      || pClass == GoodsSpecifics.class) {
      return PrcItemSpecificsRetrieve.class.getSimpleName();
    } else if (IPersistableBase.class.isAssignableFrom(pClass)) {
      return PrcEntityPbEditDelete.class.getSimpleName();
    }
    return PrcEntityRetrieve.class.getSimpleName();
  }

  //Simple getters and setters:
  /**
   * <p>Getter for seEntities.</p>
   * @return Set<Class<?>>
   **/
  public final Set<Class<?>> getSeEntities() {
    return this.seEntities;
  }

  /**
   * <p>Setter for seEntities.</p>
   * @param pSeEntitiesMap reference
   **/
  public final void setSeEntities(final Set<Class<?>> pSeEntities) {
    this.seEntities = pSeEntities;
  }
}
