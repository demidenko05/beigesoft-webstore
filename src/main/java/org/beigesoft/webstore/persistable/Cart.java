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
}
