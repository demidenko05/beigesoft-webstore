package org.beigesoft.webstore.persistable.base;

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

import java.math.BigDecimal;

import org.beigesoft.persistable.AHasNameIdLongVersion;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.UnitOfMeasure;

/**
 * <p>
 * Model of Customer Order Item line.
 * Item can be goods/service/S.E. goods/S.E.service.
 * Item has already I18N name same as in the cart.
 * </p>
 *
 * @author Yury Demidenko
 */
public class AOrdLn extends AHasNameIdLongVersion {

  /**
   * <p>Unit of measure, not null.</p>
   **/
  private UnitOfMeasure uom;

  /**
   * <p>Price, not null, grater than zero.</p>
   **/
  private BigDecimal price;

  /**
   * <p>Quantity, not null.</p>
   **/
  private BigDecimal quant;

  /**
   * <p>Subtotal without taxes.</p>
   **/
  private BigDecimal subt;

  /**
   * <p>Total taxes.</p>
   **/
  private BigDecimal totTx;

  /**
   * <p>Total, not null.</p>
   **/
  private BigDecimal tot;

  /**
   * <p>Tax category, NULL for non-taxable items.</p>
   **/
  private InvItemTaxCategory txCat;

  /**
   * <p>Taxes description, uneditable,
   * e.g. "tax1 10%=12, tax2 5%=6".</p>
   **/
  private String txDsc;

  /**
   * <p>Description - item details, dynamically,
   * e.g. " at Mon.19" for booking appointment.</p>
   **/
  private String descr;

  //Simple getters and setters:
  /**
   * <p>Getter for price.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getPrice() {
    return this.price;
  }

  /**
   * <p>Setter for price.</p>
   * @param pPrice reference
   **/
  public final void setPrice(final BigDecimal pPrice) {
    this.price = pPrice;
  }

  /**
   * <p>Getter for quant.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getQuant() {
    return this.quant;
  }

  /**
   * <p>Setter for quant.</p>
   * @param pQuant reference
   **/
  public final void setQuant(final BigDecimal pQuant) {
    this.quant = pQuant;
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
   * <p>Getter for txCat.</p>
   * @return InvItemTaxCategory
   **/
  public final InvItemTaxCategory getTxCat() {
    return this.txCat;
  }

  /**
   * <p>Setter for txCat.</p>
   * @param pTxCat reference
   **/
  public final void setTxCat(final InvItemTaxCategory pTxCat) {
    this.txCat = pTxCat;
  }

  /**
   * <p>Getter for txDsc.</p>
   * @return String
   **/
  public final String getTxDsc() {
    return this.txDsc;
  }

  /**
   * <p>Setter for txDsc.</p>
   * @param pTxDsc reference
   **/
  public final void setTxDsc(final String pTxDsc) {
    this.txDsc = pTxDsc;
  }

  /**
   * <p>Getter for uom.</p>
   * @return UnitOfMeasure
   **/
  public final UnitOfMeasure getUom() {
    return this.uom;
  }

  /**
   * <p>Setter for uom.</p>
   * @param pUom reference
   **/
  public final void setUom(final UnitOfMeasure pUom) {
    this.uom = pUom;
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
