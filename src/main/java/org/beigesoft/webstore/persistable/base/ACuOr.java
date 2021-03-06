package org.beigesoft.webstore.persistable.base;

/*
 * Copyright (c) 2019 Beigesof™
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
import java.util.List;
import java.math.BigDecimal;

import org.beigesoft.persistable.AHasIdLongVersion;
import org.beigesoft.accounting.persistable.Currency;
import org.beigesoft.webstore.model.EPaymentMethod;
import org.beigesoft.webstore.model.EOrdStat;
import org.beigesoft.webstore.model.EDelivering;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.PickUpPlace;

/**
 * <p>Abstract model of webstore owner's/S.E.seller customer order.</p>
 *
 * @param <GL> good line type
 * @param <SL> service line type
 * @author Yury Demidenko
 */
public abstract class ACuOr<GL extends AOrdLn, SL extends AOrdLn>
  extends AHasIdLongVersion {

  /**
   * <p>Its date, not null.</p>
   **/
  private Date dat;

  /**
   * <p>Purchase ID is cart version, not null.</p>
   **/
  private Long pur;

  /**
   * <p>Buyer, not null.</p>
   **/
  private OnlineBuyer buyer;

  /**
   * <p>Place where goods is stored or service is performed,
   * null if method "pickup by buyer from several places"
   * is not implemented/used.</p>
   **/
  private PickUpPlace place;

  /**
   * <p>Payment Method, not null.</p>
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
   * <p>Description.</p>
   **/
  private String descr;

  /**
   * <p>Delivering, not null.</p>
   **/
  private EDelivering deliv;

  /**
   * <p>Getter for goods.</p>
   * @return List<CustOrderGdLn>
   **/
  public abstract List<GL> getGoods();

  /**
   * <p>Setter for goods.</p>
   * @param pGoods reference
   **/
  public abstract void setGoods(final List<GL> pGoods);

  /**
   * <p>Getter for servs.</p>
   * @return List<CustOrderSrvLn>
   **/
  public abstract List<SL> getServs();

  /**
   * <p>Setter for servs.</p>
   * @param pServs reference
   **/
  public abstract void setServs(final List<SL> pServs);

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
   * <p>Getter for pur.</p>
   * @return Long
   **/
  public final Long getPur() {
    return this.pur;
  }

  /**
   * <p>Setter for pur.</p>
   * @param pPur reference
   **/
  public final void setPur(final Long pPur) {
    this.pur = pPur;
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

  /**
   * <p>Getter for deliv.</p>
   * @return EDelivering
   **/
  public final EDelivering getDeliv() {
    return this.deliv;
  }

  /**
   * <p>Setter for deliv.</p>
   * @param pDeliv reference
   **/
  public final void setDeliv(final EDelivering pDeliv) {
    this.deliv = pDeliv;
  }
}
