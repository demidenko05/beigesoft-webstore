package org.beigesoft.webstore.processor;

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
import java.util.List;
import java.util.ArrayList;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IProcessor;
import org.beigesoft.webstore.service.ILstnCatalogChanged;

/**
 * <p>Service that refresh web-store catalog.</p>
 *
 * @author Yury Demidenko
 */
public class PrcRefreshCatalog implements IProcessor {

  /**
   * <p>Listeners of catalog changed.</p>
   **/
  private List<ILstnCatalogChanged> listeners =
    new ArrayList<ILstnCatalogChanged>();


  /**
   * <p>Process refresh request.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pAddParam,
    final IRequestData pRequestData) throws Exception {
    for (ILstnCatalogChanged lstn : this.listeners) {
      lstn.onCatalogChanged();
    }
  }

  //Simple getters and setters:
  /**
   * <p>Getter for listeners.</p>
   * @return List<ILstnCatalogChanged>
   **/
  public final List<ILstnCatalogChanged> getListeners() {
    return this.listeners;
  }

  /**
   * <p>Setter for listeners.</p>
   * @param pListeners reference
   **/
  public final void setListeners(final List<ILstnCatalogChanged> pListeners) {
    this.listeners = pListeners;
  }
}
