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

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.webstore.persistable.ServiceCatalog;
import org.beigesoft.webstore.persistable.ServiceCatalogId;

/**
 * <p>Service that save ServiceCatalog into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcServiceCatalogSave<RS>
  implements IEntityProcessor<ServiceCatalog, ServiceCatalogId> {

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
  public final ServiceCatalog process(
    final Map<String, Object> pAddParam,
      final ServiceCatalog pEntity,
        final IRequestData pRequestData) throws Exception {
    if (!pEntity.getIsNew()) {
      if (pAddParam.get("user") != null) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "edit_not_allowed",
            pAddParam.get("user").toString());
      } else {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "edit_not_allowed");
      }
    }
    //Beige-ORM refresh:
    pEntity.setItsCatalog(getSrvOrm()
      .retrieveEntity(pAddParam, pEntity.getItsCatalog()));
    if (pEntity.getItsCatalog().getHasSubcatalogs()) {
      if (pAddParam.get("user") != null) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "catalog_must_be_for_goods",
            pAddParam.get("user").toString());
      } else {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "catalog_must_be_for_goods");
      }
    }
    if (pEntity.getIsNew()) {
      getSrvOrm().insertEntity(pAddParam, pEntity);
    } else {
      getSrvOrm().updateEntity(pAddParam, pEntity);
    }
    return pEntity;
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
