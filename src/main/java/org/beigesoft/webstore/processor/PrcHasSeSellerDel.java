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

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.webstore.persistable.IHasSeSeller;
import org.beigesoft.webstore.persistable.SeSeller;
import org.beigesoft.webstore.service.IFindSeSeller;

/**
 * <p>Service that deletes S.E. seller's data model into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @param <T> item type
 * @param <ID> ID type
 * @author Yury Demidenko
 */
public class PrcHasSeSellerDel<RS, T extends IHasSeSeller<ID>, ID>
  implements IEntityProcessor<T, ID> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Find S.E.Seller service.</p>
   **/
  private IFindSeSeller findSeSeller;

  /**
   * <p>Process entity request.</p>
   * @param pReqVars additional request scoped parameters
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final T process(final Map<String, Object> pReqVars,
    final T pEntity, final IRequestData pRequestData) throws Exception {
    SeSeller ses = findSeSeller.find(pReqVars, pRequestData.getUserName());
    pEntity.setSeller(ses);
    T entOld = this.srvOrm.retrieveEntity(pReqVars, pEntity);
    if (!entOld.getSeller().getItsId()
      .equals(pEntity.getSeller().getItsId())) {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "Attempt to delete smb. else's entity: user/entity/EID - "
          + pRequestData.getUserName() + "/" + pEntity.getClass()
            .getSimpleName() + "/" + pEntity.getItsId());
    }
    this.srvOrm.deleteEntity(pReqVars, pEntity);
    return null;
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
   * <p>Getter for findSeSeller.</p>
   * @return IFindSeSeller<RS>
   **/
  public final IFindSeSeller getFindSeSeller() {
    return this.findSeSeller;
  }

  /**
   * <p>Setter for findSeSeller.</p>
   * @param pFindSeSeller reference
   **/
  public final void setFindSeSeller(final IFindSeSeller pFindSeSeller) {
    this.findSeSeller = pFindSeSeller;
  }
}
