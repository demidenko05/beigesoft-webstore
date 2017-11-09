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

import java.util.List;

import org.beigesoft.webstore.model.TradingCatalog;

/**
 * <p>Trading catalog service.</p>
 *
 * @author Yury Demidenko
 */
public interface ISrvTradingCatalog {

  /**
   * <p>Retrieve/get Trading catalog.</p>
   * @return Trading catalog
   * @throws Exception - an exception
   **/
  List<TradingCatalog> getTradingCatalog() throws Exception;
}
