package org.beigesoft.webstore.persistable;

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

import org.beigesoft.webstore.persistable.base.ACustomerOrderLine;
import org.beigesoft.accounting.persistable.InvItem;

/**
 * <p>
 * Model of Customer Order Goods line.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CustOrderGdLn extends ACustomerOrderLine {

  /**
   * <p>Goods, not null.</p>
   **/
  private InvItem goods;

  //Simple getters and setters:
  /**
   * <p>Getter for goods.</p>
   * @return InvItem
   **/
  public final InvItem getGoods() {
    return this.goods;
  }

  /**
   * <p>Setter for goods.</p>
   * @param pGoods reference
   **/
  public final void setGoods(final InvItem pGoods) {
    this.goods = pGoods;
  }
}
