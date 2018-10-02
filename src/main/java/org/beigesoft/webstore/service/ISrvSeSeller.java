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

import org.beigesoft.model.IRequestData;
import org.beigesoft.webstore.persistable.SeSeller;

/**
 * <p>S.E.Seller service.
 * all methods require opened transaction.</p>
 *
 * @author Yury Demidenko
 */
public interface ISrvSeSeller {

  /**
   * <p>Finds by name.</p>
   * @param pAddParam additional param
   * @param pName seller's
   * @return S.E. Seller or null
   * @throws Exception - an exception
   **/
  SeSeller find(Map<String, Object> pAddParam, String pName) throws Exception;

  /**
   * <p>Creates seller.</p>
   * @param pAddParam additional param
   * @param pSeller seller
   * @param pData request data
   * @throws Exception - an exception
   **/
  void create(Map<String, Object> pAddParam, SeSeller pSeller,
    IRequestData pData) throws Exception;

  /**
   * <p>Updates seller.</p>
   * @param pAddParam additional param
   * @param pSeller seller
   * @param pData request data
   * @throws Exception - an exception
   **/
  void update(Map<String, Object> pAddParam, SeSeller pSeller,
    IRequestData pData) throws Exception;

  /**
   * <p>Deletes seller.</p>
   * @param pAddParam additional param
   * @param pSeller seller
   * @param pData request data
   * @throws Exception - an exception
   **/
  void delete(Map<String, Object> pAddParam, SeSeller pSeller,
    IRequestData pData) throws Exception;
}
