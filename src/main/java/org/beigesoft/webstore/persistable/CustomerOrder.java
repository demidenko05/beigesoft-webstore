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

import java.util.List;

import org.beigesoft.persistable.AHasIdLongVersion;
import org.beigesoft.webstore.model.EPaymentMethod;

/**
 * <p>
 * Model of Customer Order. If buyer buy something then CustomerOrder(s)
 * will be created, if buyer order items belonging different owners
 * (S.E.sellers, web-store owner) then orders will be created for
 * each goods/service owner. If items has different payment method
 * e.g. a goods must payment full price online but another one - with cash,
 * then order will be divided. Many online payment providers do not allow
 * partial payment to avoid some problems. So divide cart by
 * two orders - part items will be payed full online and
 * other part- with cash, is suitable method
 * </p>
 *
 * @author Yury Demidenko
 */
public class CustomerOrder extends AHasIdLongVersion {

  /**
   * <p>Buyer, not null.</p>
   **/
  private OnlineBuyer buyer;

  /**
   * <p>SE Seller, if sold by S.E. seller,
   * otherwise seller is webstore owner.</p>
   **/
  private SeSeller seSeller;

  /**
   * <p>Payment Method, not null, ANY default.</p>
   **/
  private EPaymentMethod paymentMethod;

  /**
   * <p>Ordered goods.</p>
   **/
  private List<CustomerOrderGoods> goodsList;

  /**
   * <p>Ordered services.</p>
   **/
  private List<CustomerOrderService> serviceList;

  /**
   * <p>Ordered S.E.goods.</p>
   **/
  private List<CustomerOrderSeGoods> seGoodsList;

  /**
   * <p>Ordered S.E.services.</p>
   **/
  private List<CustomerOrderSeService> seServiceList;

  /**
   * <p>Order's taxes summary.</p>
   **/
  private List<CustomerOrderTaxLine> taxesList;

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
   * <p>Getter for seSeller.</p>
   * @return SeSeller
   **/
  public final SeSeller getSeSeller() {
    return this.seSeller;
  }

  /**
   * <p>Setter for seSeller.</p>
   * @param pSeSeller reference
   **/
  public final void setSeSeller(final SeSeller pSeSeller) {
    this.seSeller = pSeSeller;
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
   * @return List<CustomerOrderGoods>
   **/
  public final List<CustomerOrderGoods> getGoodsList() {
    return this.goodsList;
  }

  /**
   * <p>Setter for goodsList.</p>
   * @param pGoodsList reference
   **/
  public final void setGoodsList(final List<CustomerOrderGoods> pGoodsList) {
    this.goodsList = pGoodsList;
  }

  /**
   * <p>Getter for serviceList.</p>
   * @return List<CustomerOrderService>
   **/
  public final List<CustomerOrderService> getServiceList() {
    return this.serviceList;
  }

  /**
   * <p>Setter for serviceList.</p>
   * @param pServiceList reference
   **/
  public final void setServiceList(
    final List<CustomerOrderService> pServiceList) {
    this.serviceList = pServiceList;
  }

  /**
   * <p>Getter for seGoodsList.</p>
   * @return List<CustomerOrderSeGoods>
   **/
  public final List<CustomerOrderSeGoods> getSeGoodsList() {
    return this.seGoodsList;
  }

  /**
   * <p>Setter for seGoodsList.</p>
   * @param pSeGoodsList reference
   **/
  public final void setSeGoodsList(
    final List<CustomerOrderSeGoods> pSeGoodsList) {
    this.seGoodsList = pSeGoodsList;
  }

  /**
   * <p>Getter for seServiceList.</p>
   * @return List<CustomerOrderSeService>
   **/
  public final List<CustomerOrderSeService> getSeServiceList() {
    return this.seServiceList;
  }

  /**
   * <p>Setter for seServiceList.</p>
   * @param pSeServiceList reference
   **/
  public final void setSeServiceList(
    final List<CustomerOrderSeService> pSeServiceList) {
    this.seServiceList = pSeServiceList;
  }

  /**
   * <p>Getter for taxesList.</p>
   * @return List<CustomerOrderTaxLine>
   **/
  public final List<CustomerOrderTaxLine> getTaxesList() {
    return this.taxesList;
  }

  /**
   * <p>Setter for taxesList.</p>
   * @param pTaxesList reference
   **/
  public final void setTaxesList(final List<CustomerOrderTaxLine> pTaxesList) {
    this.taxesList = pTaxesList;
  }
}
