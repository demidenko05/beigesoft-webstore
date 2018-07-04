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
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.orm.processor.PrcEntityRetrieve;
import org.beigesoft.webstore.model.ESpecificsItemType;
import org.beigesoft.webstore.persistable.ServiceSpecifics;
import org.beigesoft.webstore.persistable.ServiceSpecificsId;

/**
 * <p>Service that retrieve ServiceSpecifics.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcServiceSpecificsRetrieve<RS>
  implements IEntityProcessor<ServiceSpecifics, ServiceSpecificsId> {

  /**
   * <p>Entity retrieve delegator.</p>
   **/
  private PrcEntityRetrieve<RS, ServiceSpecifics, ServiceSpecificsId>
    prcEntityRetrieve;

  /**
   * <p>Process entity request.</p>
   * @param pReqVars additional request scoped vars
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final ServiceSpecifics process(final Map<String, Object> pReqVars,
    final ServiceSpecifics pEntity,
      final IRequestData pRequestData) throws Exception {
    ServiceSpecifics entity = this.prcEntityRetrieve
      .process(pReqVars, pEntity, pRequestData);
    if (entity.getSpecifics().getItsType() != null
      && entity.getSpecifics().getItsType()
        .equals(ESpecificsItemType.BIGDECIMAL)) {
      if (entity.getNumericValue1() == null) {
        entity.setNumericValue1(BigDecimal.ZERO);
      }
      if (entity.getLongValue1() == null) {
        entity.setLongValue2(2L);
      }
      pReqVars.put("RSisUsePrecision" + entity.getLongValue2(), true);
    }
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcEntityRetrieve.</p>
   * @return PrcEntityRetrieve<RS, ServiceSpecifics, ServiceSpecificsId>
   **/
  public final PrcEntityRetrieve<RS, ServiceSpecifics, ServiceSpecificsId>
    getPrcEntityRetrieve() {
    return this.prcEntityRetrieve;
  }

  /**
   * <p>Setter for prcEntityRetrieve.</p>
   * @param pPrcEntityRetrieve reference
   **/
  public final void setPrcEntityRetrieve(
    final PrcEntityRetrieve<RS, ServiceSpecifics, ServiceSpecificsId>
      pPrcEntityRetrieve) {
    this.prcEntityRetrieve = pPrcEntityRetrieve;
  }
}
