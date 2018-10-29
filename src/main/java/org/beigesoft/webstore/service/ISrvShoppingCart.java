package org.beigesoft.webstore.service;

/*
 * Copyright (c) 2018 Beigesoft ™
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
import org.beigesoft.webstore.persistable.Cart;

/**
 * <p>Abstraction of service that retrieve/create buyer's shopping cart.
 * Implementation must be non-transactional.</p>
 *
 * @author Yury Demidenko
 */
public interface ISrvShoppingCart {

  /**
   * <p>Get/Create Cart.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @param pIsNeedToCreate Is Need To Create cart
   * @return shopping cart or null
   * @throws Exception - an exception
   **/
  Cart getShoppingCart(Map<String, Object> pAddParam,
    IRequestData pRequestData,
      boolean pIsNeedToCreate) throws Exception;
}
