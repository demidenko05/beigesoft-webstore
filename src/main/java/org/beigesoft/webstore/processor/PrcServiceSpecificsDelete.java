package org.beigesoft.webstore.processor;

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

import java.util.Map;
import java.io.File;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.webstore.model.ESpecificsItemType;
import org.beigesoft.webstore.persistable.ServiceSpecifics;
import org.beigesoft.webstore.persistable.ServiceSpecificsId;

/**
 * <p>Service that delete ServiceSpecifics include uploaded file if exist
 * from DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcServiceSpecificsDelete<RS>
  implements IEntityProcessor<ServiceSpecifics, ServiceSpecificsId> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final ServiceSpecifics process(
    final Map<String, Object> pAddParam,
      final ServiceSpecifics pEntity,
        final IRequestData pRequestData) throws Exception {
    //Refresh, TODO Web-MVC should handle pass subentity fields:
    ServiceSpecifics entity = getSrvOrm().retrieveEntity(pAddParam, pEntity);
    if (entity.getStringValue2() != null && (entity.getSpecifics()
      .getItsType().equals(ESpecificsItemType.FILE)
        || entity.getSpecifics().getItsType()
          .equals(ESpecificsItemType.FILE_EMBEDDED)
          || entity.getSpecifics().getItsType()
            .equals(ESpecificsItemType.IMAGE)
              || entity.getSpecifics().getItsType()
                .equals(ESpecificsItemType.IMAGE_IN_SET))) {
      File fileToDel = new File(entity.getStringValue2());
      if (fileToDel.exists() && !fileToDel.delete()) {
        throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
          "Can not delete file: " + fileToDel);
      }
    }
    this.srvOrm.deleteEntity(pAddParam, entity);
    return entity;
  }


  //Simple getters and setters:
  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }
}
