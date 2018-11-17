package org.beigesoft.webstore.persistable;

/*
 * Copyright (c) 2017 Beigesoft™
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
import java.math.BigDecimal;

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;
import org.beigesoft.accounting.persistable.Currency;
import org.beigesoft.webstore.model.EDelivering;

/**
 * <p>
 * Model of average buyer rating.
 * </p>
 *
 * @author Yury Demidenko
 */
public class Cart extends AEditableHasVersion implements IHasId<OnlineBuyer> {

  /**
   * <p>Buyer, PK.</p>
   **/
  private OnlineBuyer buyer;

  /**
   * <p>Currency, not null, that buyer opted.</p>
   **/
  private Currency curr;

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
   * <p>Items.</p>
   **/
  private List<CartLn> items;

  /**
   * <p>Taxes.</p>
   **/
  private List<CartTxLn> taxes;

  /**
   * <p>Totals grouped by seller.</p>
   **/
  private List<CartTot> totals;

  /**
   * <p>Delivering, not null, buyer pickup itself default.</p>
   **/
  private EDelivering deliv = EDelivering.PICKUP;

  /**
   * <p>Buyer waiting to resolve problem, e.g. tax destination can't
   * be revealed automatically.</p>
   **/
  private Boolean err = Boolean.FALSE;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final OnlineBuyer getItsId() {
    return this.buyer;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final OnlineBuyer pItsId) {
    this.buyer = pItsId;
  }

  //Simple getters and setters:
  /**
   * <p>Setter for buyer.</p>
   * @param pBuyer reference
   **/
  public final void setBuyer(final OnlineBuyer pBuyer) {
    this.buyer = pBuyer;
  }

  /**
   * <p>Getter for buyer.</p>
   * @return OnlineBuyer
   **/
  public final OnlineBuyer getBuyer() {
    return this.buyer;
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
   * <p>Getter for items.</p>
   * @return List<CartLn>
   **/
  public final List<CartLn> getItems() {
    return this.items;
  }

  /**
   * <p>Setter for items.</p>
   * @param pItems reference
   **/
  public final void setItems(final List<CartLn> pItems) {
    this.items = pItems;
  }

  /**
   * <p>Getter for taxes.</p>
   * @return List<CartTxLn>
   **/
  public final List<CartTxLn> getTaxes() {
    return this.taxes;
  }

  /**
   * <p>Setter for taxes.</p>
   * @param pTaxes reference
   **/
  public final void setTaxes(final List<CartTxLn> pTaxes) {
    this.taxes = pTaxes;
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
   * <p>Getter for totals.</p>
   * @return List<CartTot>
   **/
  public final List<CartTot> getTotals() {
    return this.totals;
  }

  /**
   * <p>Setter for totals.</p>
   * @param pTotals reference
   **/
  public final void setTotals(final List<CartTot> pTotals) {
    this.totals = pTotals;
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

  /**
   * <p>Getter for err.</p>
   * @return Boolean
   **/
  public final Boolean getErr() {
    return this.err;
  }

  /**
   * <p>Setter for err.</p>
   * @param pErr reference
   **/
  public final void setErr(final Boolean pErr) {
    this.err = pErr;
  }
}
