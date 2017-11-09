package org.beigesoft.webstore.service;

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

import org.beigesoft.webstore.persistable.TradingSettings;

/**
 * <p>Trading settings service.</p>
 *
 * @author Yury Demidenko
 */
public interface ISrvTradingSettings {

  /**
   * <p>Retrieve/get Trading settings.</p>
   * @param pAddParam additional param
   * @return Trading settings
   * @throws Exception - an exception
   **/
  TradingSettings lazyGetTradingSettings(
    Map<String, Object> pAddParam) throws Exception;

  /**
   * <p>Clear Trading settings to retrieve from
   * database new version.</p>
   * @param pAddParam additional param
   **/
  void clearTradingSettings(Map<String, Object> pAddParam);

  /**
   * <p>Save entity into DB.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @throws Exception - an exception
   **/
  void saveTradingSettings(Map<String, Object> pAddParam,
      TradingSettings pEntity) throws Exception;
}
