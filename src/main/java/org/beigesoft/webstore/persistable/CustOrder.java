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
import java.util.Date;
import java.math.BigDecimal;

import org.beigesoft.persistable.AHasIdLongVersion;
import org.beigesoft.accounting.persistable.Currency;
import org.beigesoft.webstore.model.EPaymentMethod;
import org.beigesoft.webstore.model.EOrdStat;

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
   * <p>Its date, not null.</p>
   **/
  private Date dat;

  /**
   * <p>Buyer, not null.</p>
   **/
  private OnlineBuyer buyer;

  /**
   * <p>Place where goods is stored or service is performed, not null.</p>
   **/
  private PickUpPlace place;

  /**
   * <p>Payment Method, not null, ANY default.</p>
   **/
  private EPaymentMethod payMeth;

  /**
   * <p>Order status, not null, NEW default.</p>
   **/
  private EOrdStat stat = EOrdStat.NEW;

  /**
   * <p>Currency, not null, that buyer opted.</p>
   **/
  private Currency curr;

  /**
   * <p>Exchange rate for foreign currency, not null, default 1.</p>
   **/
  private BigDecimal excRt = BigDecimal.ONE;

  /**
   * <p>Subtotal, not null.</p>
   **/
  private BigDecimal subt = BigDecimal.ZERO;

  /**
   * <p>Total, not null.</p>
   **/
  private BigDecimal tot = BigDecimal.ZERO;

  /**
   * <p>Total taxes, not null.</p>
   **/
  private BigDecimal totTx = BigDecimal.ZERO;

  /**
   * <p>Ordered goods.</p>
   **/
  private List<CustOrderGdLn> goods;

  /**
   * <p>Ordered services.</p>
   **/
  private List<CustOrderSrvLn> servs;

  /**
   * <p>Order's taxes summary.</p>
   **/
  private List<CustOrderTxLn> taxes;

  /**
   * <p>Description.</p>
   **/
  private String descr;

  //Simple getters and setters:
  /**
   * <p>Getter for dat.</p>
   * @return Date
   **/
  public final Date getDat() {
    return this.dat;
  }

  /**
   * <p>Setter for dat.</p>
   * @param pDat reference
   **/
  public final void setDat(final Date pDat) {
    this.dat = pDat;
  }

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
   * <p>Getter for place.</p>
   * @return PickUpPlace
   **/
  public final PickUpPlace getPlace() {
    return this.place;
  }

  /**
   * <p>Setter for place.</p>
   * @param pPlace reference
   **/
  public final void setPlace(final PickUpPlace pPlace) {
    this.place = pPlace;
  }

  /**
   * <p>Getter for payMeth.</p>
   * @return EPaymentMethod
   **/
  public final EPaymentMethod getPayMeth() {
    return this.payMeth;
  }

  /**
   * <p>Setter for payMeth.</p>
   * @param pPayMeth reference
   **/
  public final void setPayMeth(final EPaymentMethod pPayMeth) {
    this.payMeth = pPayMeth;
  }

  /**
   * <p>Getter for stat.</p>
   * @return EOrdStat
   **/
  public final EOrdStat getStat() {
    return this.stat;
  }

  /**
   * <p>Setter for stat.</p>
   * @param pStat reference
   **/
  public final void setStat(final EOrdStat pStat) {
    this.stat = pStat;
  }

  /**
   * <p>Getter for curr.</p>
   * @return Currency
   **/
  public final Currency getCurr() {
    return this.curr;
  }

  /**
   * <p>Setter for curr.</p>
   * @param pCurr reference
   **/
  public final void setCurr(final Currency pCurr) {
    this.curr = pCurr;
  }

  /**
   * <p>Getter for excRt.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getExcRt() {
    return this.excRt;
  }

  /**
   * <p>Setter for excRt.</p>
   * @param pExcRt reference
   **/
  public final void setExcRt(final BigDecimal pExcRt) {
    this.excRt = pExcRt;
  }

  /**
   * <p>Getter for subt.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getSubt() {
    return this.subt;
  }

  /**
   * <p>Setter for subt.</p>
   * @param pSubt reference
   **/
  public final void setSubt(final BigDecimal pSubt) {
    this.subt = pSubt;
  }

  /**
   * <p>Getter for tot.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTot() {
    return this.tot;
  }

  /**
   * <p>Setter for tot.</p>
   * @param pTot reference
   **/
  public final void setTot(final BigDecimal pTot) {
    this.tot = pTot;
  }

  /**
   * <p>Getter for totTx.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTotTx() {
    return this.totTx;
  }

  /**
   * <p>Setter for totTx.</p>
   * @param pTotTx reference
   **/
  public final void setTotTx(final BigDecimal pTotTx) {
    this.totTx = pTotTx;
  }

  /**
   * <p>Getter for goods.</p>
   * @return List<CustOrderGdLn>
   **/
  public final List<CustOrderGdLn> getGoods() {
    return this.goods;
  }

  /**
   * <p>Setter for goods.</p>
   * @param pGoods reference
   **/
  public final void setGoods(final List<CustOrderGdLn> pGoods) {
    this.goods = pGoods;
  }

  /**
   * <p>Getter for servs.</p>
   * @return List<CustOrderSrvLn>
   **/
  public final List<CustOrderSrvLn> getServs() {
    return this.servs;
  }

  /**
   * <p>Setter for servs.</p>
   * @param pServs reference
   **/
  public final void setServs(final List<CustOrderSrvLn> pServs) {
    this.servs = pServs;
  }

  /**
   * <p>Getter for taxes.</p>
   * @return List<CustOrderTxLn>
   **/
  public final List<CustOrderTxLn> getTaxes() {
    return this.taxes;
  }

  /**
   * <p>Setter for taxes.</p>
   * @param pTaxes reference
   **/
  public final void setTaxes(final List<CustOrderTxLn> pTaxes) {
    this.taxes = pTaxes;
  }

  /**
   * <p>Getter for descr.</p>
   * @return String
   **/
  public final String getDescr() {
    return this.descr;
  }

  /**
   * <p>Setter for descr.</p>
   * @param pDescr reference
   **/
  public final void setDescr(final String pDescr) {
    this.descr = pDescr;
  }
}
