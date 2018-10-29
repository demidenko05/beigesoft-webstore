package org.beigesoft.webstore.persistable;

/*
 * Copyright (c) 2017 Beigesof ™
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

import org.beigesoft.persistable.AHasIdLongVersion;
import org.beigesoft.webstore.model.EPaymentMethod;

/**
 * <p>
 * Model of Customer Order for web-store owner's items.
 * It's used to create Sales Invoice.
 * Customer order does neither accounting nor warehouse entries,
 * but it reduces "Goods Available in Place". Canceled order increases
 * back "Goods Available in Place".
 * </p>
 *
 * @author Yury Demidenko
 */
public class CustOrder extends AHasIdLongVersion {

  /**
   * <p>Buyer, not null.</p>
   **/
  private OnlineBuyer buyer;

  /**
   * <p>Payment Method, not null, ANY default.</p>
   **/
  private EPaymentMethod paymentMethod;

  /**
   * <p>Ordered goods.</p>
   **/
  private List<CustOrderGdLn> goodsList;

  /**
   * <p>Ordered services.</p>
   **/
  private List<CustOrderSrvLn> serviceList;

  /**
   * <p>Order's taxes summary.</p>
   **/
  private List<CustOrderTxLn> taxesList;

  //Simple getters and setters:
  /**
   * <p>Getter for buyer.</p>
   * @return OnlineBuyer
   **/
  public final OnlineBuyer getBuyer() {
    return this.buyer;
  }

  /**
   * <p>Setter for buyer.</p>
   * @param pBuyer reference
   **/
  public final void setBuyer(final OnlineBuyer pBuyer) {
    this.buyer = pBuyer;
  }

  /**
   * <p>Getter for paymentMethod.</p>
   * @return EPaymentMethod
   **/
  public final EPaymentMethod getPaymentMethod() {
    return this.paymentMethod;
  }

  /**
   * <p>Setter for paymentMethod.</p>
   * @param pPaymentMethod reference
   **/
  public final void setPaymentMethod(final EPaymentMethod pPaymentMethod) {
    this.paymentMethod = pPaymentMethod;
  }

  /**
   * <p>Getter for goodsList.</p>
   * @return List<CustOrderGdLn>
   **/
  public final List<CustOrderGdLn> getGoodsList() {
    return this.goodsList;
  }

  /**
   * <p>Setter for goodsList.</p>
   * @param pGoodsList reference
   **/
  public final void setGoodsList(final List<CustOrderGdLn> pGoodsList) {
    this.goodsList = pGoodsList;
  }

  /**
   * <p>Getter for serviceList.</p>
   * @return List<CustOrderSrvLn>
   **/
  public final List<CustOrderSrvLn> getServiceList() {
    return this.serviceList;
  }

  /**
   * <p>Setter for serviceList.</p>
   * @param pServiceList reference
   **/
  public final void setServiceList(
    final List<CustOrderSrvLn> pServiceList) {
    this.serviceList = pServiceList;
  }

  /**
   * <p>Getter for taxesList.</p>
   * @return List<CustOrderTxLn>
   **/
  public final List<CustOrderTxLn> getTaxesList() {
    return this.taxesList;
  }

  /**
   * <p>Setter for taxesList.</p>
   * @param pTaxesList reference
   **/
  public final void setTaxesList(final List<CustOrderTxLn> pTaxesList) {
    this.taxesList = pTaxesList;
  }
}
