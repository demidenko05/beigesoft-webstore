package org.beigesoft.webstore.service;

/*
 * Copyright (c) 2019 Beigesoft™
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
import org.beigesoft.webstore.persistable.OnlineBuyer;

/**
 * <p>Buyer's service.</p>
 *
 * @author Yury Demidenko
 */
public interface IBuySr {

  /**
   * <p>Get authorized buyer.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @return authorized buyer or null
   * @throws Exception - an exception
   **/
  OnlineBuyer getAuthBuyr(Map<String, Object> pRqVs,
    IRequestData pRqDt) throws Exception;

  /**
   * <p>Get authorized or not buyer by cookie.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @return buyer or null
   * @throws Exception - an exception
   **/
  OnlineBuyer getBuyr(Map<String, Object> pRqVs,
    IRequestData pRqDt) throws Exception;

  /**
   * <p>Creates buyer.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @return created buyer will be unsaved into DB!
   * @throws Exception - an exception
   **/
  OnlineBuyer createBuyr(Map<String, Object> pRqVs,
    IRequestData pRqDt) throws Exception;
}
