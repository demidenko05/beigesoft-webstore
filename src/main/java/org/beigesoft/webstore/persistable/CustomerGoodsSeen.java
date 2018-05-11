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

import java.util.Date;

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;
import org.beigesoft.accounting.persistable.DebtorCreditor;
import org.beigesoft.accounting.persistable.InvItem;

/**
 * <pre>
 * Model of register of goods that recently seen by customer.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class CustomerGoodsSeen extends AEditableHasVersion
  implements IHasId<CustomerGoodsSeenId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private CustomerGoodsSeenId itsId = new CustomerGoodsSeenId();

  /**
   * <p>Price Category.</p>
   **/
  private InvItem goods;

  /**
   * <p>Customer, not null.</p>
   **/
  private DebtorCreditor customer;

  /**
   * <p>Date seen, not null.</p>
   **/
  private Date dateSeen;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final CustomerGoodsSeenId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final CustomerGoodsSeenId pItsId) {
    this.itsId = pItsId;
    if (this.itsId != null) {
      this.goods = this.itsId.getGoods();
      this.customer = this.itsId.getCustomer();
    } else {
      this.goods = null;
      this.customer = null;
    }
  }

  /**
   * <p>Setter for pGoods.</p>
   * @param pGoods reference
   **/
  public final void setGoods(final InvItem pGoods) {
    this.goods = pGoods;
    if (this.itsId == null) {
      this.itsId = new CustomerGoodsSeenId();
    }
    this.itsId.setGoods(this.goods);
  }

  /**
   * <p>Setter for customer.</p>
   * @param pCustomer reference
   **/
  public final void setCustomer(final DebtorCreditor pCustomer) {
    this.customer = pCustomer;
    if (this.itsId == null) {
      this.itsId = new CustomerGoodsSeenId();
    }
    this.itsId.setCustomer(this.customer);
  }

  //Hiding references getters and setters:
  /**
   * <p>Geter for dateSeen.</p>
   * @return Date
   **/
  public final Date getDateSeen() {
    if (this.dateSeen == null) {
      return null;
    }
    return new Date(this.dateSeen.getTime());
  }

  /**
   * <p>Setter for dateSeen.</p>
   * @param pDateSeen reference
   **/
  public final void setDateSeen(final Date pDateSeen) {
    if (pDateSeen == null) {
      this.dateSeen = null;
    } else {
      this.dateSeen = new Date(pDateSeen.getTime());
    }
  }

  //Simple getters and setters:
  /**
   * <p>Getter for goods.</p>
   * @return InvItem
   **/
  public final InvItem getGoods() {
    return this.goods;
  }

  /**
   * <p>Getter for customer.</p>
   * @return DebtorCreditor
   **/
  public final DebtorCreditor getCustomer() {
    return this.customer;
  }
}
