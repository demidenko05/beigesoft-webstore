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

import org.beigesoft.webstore.persistable.base.AItemSpecificsId;
import org.beigesoft.accounting.persistable.ServiceToSale;

/**
 * <p>
 * Model of ID of Specifics values for a Service.
 * </p>
 *
 * @author Yury Demidenko
 */
public class ServiceSpecificsId extends AItemSpecificsId<ServiceToSale> {

  /**
   * <p>Service.</p>
   **/
  private ServiceToSale item;

  /**
   * <p>Minimal constructor.</p>
   **/
  public ServiceSpecificsId() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pSpecifics reference
   * @param pItem reference
   **/
  public ServiceSpecificsId(final SpecificsOfItem pSpecifics,
    final ServiceToSale pItem) {
    this.item = pItem;
    setSpecifics(pSpecifics);
  }

  /**
   * <p>Getter for item.</p>
   * @return ServiceToSale
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
