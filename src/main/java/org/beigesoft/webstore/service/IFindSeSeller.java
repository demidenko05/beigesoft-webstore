package org.beigesoft.webstore.service;

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

import org.beigesoft.webstore.persistable.SeSeller;

/**
 * <p>S.E.Seller finder service. It usually cashes S.E.Sellers.</p>
 *
 * @author Yury Demidenko
 */
public interface IFindSeSeller {

  /**
   * <p>Finds by name.</p>
   * @param pAddParam additional param
   * @param pName seller's
   * @return S.E. Seller or null
   * @throws Exception - an exception
   **/
  SeSeller find(Map<String, Object> pAddParam, String pName) throws Exception;

  /**
   * <p>Handle S.E. seller changed.</p>
   * @param pAddParam additional param
   * @param pName seller's, null means "refresh all"
   * @throws Exception - an exception
   **/
  void handleSeSellerChanged(Map<String, Object> pAddParam,
    String pName) throws Exception;
}
