package org.beigesoft.webstore.processor;

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

import java.util.Map;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.webstore.persistable.SubcatalogsCatalogsGs;
import org.beigesoft.webstore.persistable.SubcatalogsCatalogsGsId;
import org.beigesoft.webstore.service.ISrvTradingSettings;

/**
 * <p>Service that save SubcatalogsCatalogsGs into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSubcatalogsCatalogsGsSave<RS>
  implements
    IEntityProcessor<SubcatalogsCatalogsGs, SubcatalogsCatalogsGsId> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Business service for trading settings.</p>
   **/
  private ISrvTradingSettings srvTradingSettings;

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
  public final SubcatalogsCatalogsGs process(
    final Map<String, Object> pAddParam,
      final SubcatalogsCatalogsGs pEntity,
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
    if (pEntity.getItsCatalog().getItsId().equals(pEntity
      .getSubcatalog().getItsId())) {
      if (pAddParam.get("user") != null) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "catalog_and_subacatlog_must_be_different",
            pAddParam.get("user").toString());
      } else {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "catalog_and_subacatlog_must_be_different");
      }
    }
    //Beige-ORM refresh:
    pEntity.setItsCatalog(getSrvOrm()
      .retrieveEntity(pAddParam, pEntity.getItsCatalog()));
    if (!pEntity.getItsCatalog().getHasSubcatalogs()) {
      if (pAddParam.get("user") != null) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "catalog_must_be_for_subcatalogs",
            pAddParam.get("user").toString());
      } else {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "catalog_must_be_for_subcatalogs");
      }
    }
    if (pEntity.getIsNew()) {
      getSrvOrm().insertEntity(pAddParam, pEntity);
    } else {
      getSrvOrm().updateEntity(pAddParam, pEntity);
    }
    pRequestData.setAttribute("tradingSettings", srvTradingSettings
      .lazyGetTradingSettings(pAddParam));
    pRequestData.setAttribute("accSettings",
      this.srvAccSettings.lazyGetAccSettings(pAddParam));
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

  /**
   * <p>Getter for srvAccSettings.</p>
   * @return ISrvAccSettings
   **/
  public final ISrvAccSettings getSrvAccSettings() {
    return this.srvAccSettings;
  }

  /**
   * <p>Setter for srvAccSettings.</p>
   * @param pSrvAccSettings reference
   **/
  public final void setSrvAccSettings(final ISrvAccSettings pSrvAccSettings) {
    this.srvAccSettings = pSrvAccSettings;
  }

  /**
   * <p>Getter for srvTradingSettings.</p>
   * @return ISrvTradingSettings
   **/
  public final ISrvTradingSettings getSrvTradingSettings() {
    return this.srvTradingSettings;
  }

  /**
   * <p>Setter for srvTradingSettings.</p>
   * @param pSrvTradingSettings reference
   **/
  public final void setSrvTradingSettings(
    final ISrvTradingSettings pSrvTradingSettings) {
    this.srvTradingSettings = pSrvTradingSettings;
  }
}
