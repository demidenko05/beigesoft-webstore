package org.beigesoft.webstore.replicator;

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

import org.beigesoft.replicator.service.ISrvEntityFieldFiller;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.accounting.persistable.DebtorCreditor;
import org.beigesoft.webstore.persistable.SeSeller;
import org.beigesoft.webstore.persistable.SeGoods;

/**
 * <p>Service to fill S.E.Seller in SeGoods.</p>
 *
 * @author Yury Demidenko
 */
public class SeGoodSeSellerRepl implements ISrvEntityFieldFiller {

  /**
   * <p>
   * Fill given field of given entity according value represented as
   * string.
   * </p>
   * @param pAddParam additional params
   * @param pEntity Entity.
   * @param pFieldName Field Name
   * @param pFieldStrValue Field value
   * @throws Exception - an exception
   **/
  @Override
  public final void fill(final Map<String, Object> pAddParam,
    final Object pEntity, final String pFieldName,
      final String pFieldStrValue) throws Exception {
    if (SeGoods.class != pEntity.getClass()) {
      throw new ExceptionWithCode(ExceptionWithCode
        .CONFIGURATION_MISTAKE, "It's wrong service to fill that field: "
          + pEntity + "/" + pFieldName + "/" + pFieldStrValue);
    }
    SeGoods seGood = (SeGoods) pEntity;
    if ("NULL".equals(pFieldStrValue)) {
      seGood.setSeller(null);
      return;
    }
    try {
      SeSeller ownedEntity = new SeSeller();
      DebtorCreditor dk = new DebtorCreditor();
      dk.setItsId(Long.parseLong(pFieldStrValue));
      ownedEntity.setSeller(dk);
      seGood.setSeller(ownedEntity);
    } catch (Exception ex) {
      throw new ExceptionWithCode(ExceptionWithCode
        .WRONG_PARAMETER, "Can not fill field: " + pEntity + "/" + pFieldName
          + "/" + pFieldStrValue + ", " + ex.getMessage(), ex);
    }
  }
}
