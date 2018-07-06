package org.beigesoft.webstore.persistable;

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

import org.beigesoft.webstore.persistable.base.AItemCatalogId;
import org.beigesoft.accounting.persistable.ServiceToSale;

/**
 * <p>
 * Model of ID of Catalog that contains of Service.
 * </p>
 *
 * @author Yury Demidenko
 */
public class ServiceCatalogId extends AItemCatalogId<ServiceToSale> {

  /**
   * <p>Service.</p>
   **/
  private ServiceToSale item;

  /**
   * <p>Getter for item.</p>
   * @return T
   **/
  @Override
  public final ServiceToSale getItem() {
    return this.item;
  }

  /**
   * <p>Setter for item.</p>
   * @param pItem reference
   **/
  @Override
  public final void setItem(final ServiceToSale pItem) {
    this.item = pItem;
  }
}
