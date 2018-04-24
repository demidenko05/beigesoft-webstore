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

/**
 * <p>Abstraction of listener of catalog changed event.</p>
 *
 * @author Yury Demidenko
 */
public interface ILstnCatalogChanged {

  /**
   * <p>Handle catalog changed event.</p>
   * @throws Exception an Exception
   **/
  void onCatalogChanged() throws Exception;
}
