package org.beigesoft.webstore.replicator;

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

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.webstore.persistable.ShoppingCart;
import org.beigesoft.webstore.persistable.CartItem;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.replicator.service.ISrvEntityFieldFiller;

/**
 * <p>Service to fill ItsOwner (shopping cart) in CartItem.</p>
 *
 * @author Yury Demidenko
 */
public class SrvCartItemItsOwnerFiller implements ISrvEntityFieldFiller {

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
    if (!CartItem.class.isAssignableFrom(pEntity.getClass())) {
      throw new ExceptionWithCode(ExceptionWithCode
        .CONFIGURATION_MISTAKE, "It's wrong service to fill that field: "
          + pEntity + "/" + pFieldName + "/" + pFieldStrValue);
    }
    try {
      CartItem cartItem = (CartItem) pEntity;
      ShoppingCart ownedEntity = new ShoppingCart();
      OnlineBuyer buyer = new OnlineBuyer();
      buyer.setItsId(Long.valueOf(pFieldStrValue));
      ownedEntity.setBuyer(buyer);
      cartItem.setItsOwner(ownedEntity);
    } catch (Exception ex) {
      throw new ExceptionWithCode(ExceptionWithCode
        .WRONG_PARAMETER, "Can not fill field: " + pEntity + "/" + pFieldName
          + "/" + pFieldStrValue + ", " + ex.getMessage(), ex);
    }
  }
}
