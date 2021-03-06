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

import java.math.BigDecimal;
import java.util.Date;

import org.beigesoft.model.IOwned;
import org.beigesoft.persistable.AHasNameIdLongVersion;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.UnitOfMeasure;
import org.beigesoft.webstore.model.EShopItemType;

/**
 * <p>
 * Model of CartLn.
 * </p>
 *
 * @author Yury Demidenko
 */
public class CartLn extends AHasNameIdLongVersion
  implements IOwned<Cart>, IHasSeSeller<Long> {

  /**
   * <p>Shopping Cart.</p>
   **/
  private Cart itsOwner;

  /**
   * <p>Do not show in cart, it's for performance,
   * old purchased cart emptied with this flag,
   * when buyer add new goods to cart then it's used any disabled
   * line (if exist) otherwise new line will be created.</p>
   **/
  private Boolean disab = Boolean.FALSE;

  /**
   * <p>Shop Item Type, not null.</p>
   **/
  private EShopItemType itTyp;

  /**
   * <p>S.E.Seller which item presents in cart,
   * NULL means web-store owner's item.</p>
   **/
  private SeSeller sel;

  /**
   * <p>Item ID, not null.</p>
   **/
  private Long itId;

  /**
   * <p>Price, not null, grater than zero.</p>
   **/
  private BigDecimal price = BigDecimal.ZERO;

  /**
   * <p>Quantity, not null.</p>
   **/
  private BigDecimal quant = BigDecimal.ZERO;

  /**
   * <p>Unit of measure, not null.</p>
   **/
  private UnitOfMeasure uom;

  /**
   * <p>Subtotal without taxes.</p>
   **/
  private BigDecimal subt = BigDecimal.ZERO;

  /**
   * <p>Total taxes.</p>
   **/
  private BigDecimal totTx = BigDecimal.ZERO;

  /**
   * <p>Taxes description, uneditable,
   * e.g. "tax1 10%=12, tax2 5%=6".</p>
   **/
  private String txDsc;

  /**
   * <p>Total, not null.</p>
   **/
  private BigDecimal tot = BigDecimal.ZERO;

  /**
   * <p>Available quantity, not null.</p>
   **/
  private BigDecimal avQuan = BigDecimal.ZERO;

  /**
   * <p>Tax category, NULL for non-taxable items.</p>
   **/
  private InvItemTaxCategory txCat;

  /**
   * <p>Quantity step, 1 default,
   * e.g. 12USD per 0.5ft, UOM ft, ST=0.5, so
   * buyer can order 0.5/1.0/1.5/2.0/etc. units of item.</p>
   **/
  private BigDecimal unStep = BigDecimal.ONE;

  /**
   * <p>forced (user can't change/delete it) item,
   * e.g. service "delivering by mail".</p>
   **/
  private Boolean forc = Boolean.FALSE;

  /**
   * <p>Nullable, booking from date1 (include) for bookable service only.</p>
   **/
  private Date dt1;

  /**
   * <p>Nullable, booking till date2 (exclude) for bookable service only.</p>
   **/
  private Date dt2;

  /**
   * <p>Description - item details, dynamically,
   * e.g. " at Mon.19" for booking appointment.</p>
   **/
  private String descr;

  /**
   * <p>Getter for itsOwner.</p>
   * @return Cart
   **/
  @Override
  public final Cart getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final Cart pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  /**
   * <p>Getter for seller.</p>
   * @return SeSeller
   **/
  @Override
  public final SeSeller getSeller() {
    return this.sel;
  }

  /**
   * <p>Setter for seller.</p>
   * @param pSeller reference
   **/
  @Override
  public final void setSeller(final SeSeller pSeller) {
    this.sel = pSeller;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for disab.</p>
   * @return Boolean
   **/
  public final Boolean getDisab() {
    return this.disab;
  }

  /**
   * <p>Setter for disab.</p>
   * @param pDisab reference
   **/
  public final void setDisab(final Boolean pDisab) {
    this.disab = pDisab;
  }

  /**
   * <p>Getter for itTyp.</p>
   * @return EShopItemType
   **/
  public final EShopItemType getItTyp() {
    return this.itTyp;
  }

  /**
   * <p>Setter for itTyp.</p>
   * @param pItTyp reference
   **/
  public final void setItTyp(final EShopItemType pItTyp) {
    this.itTyp = pItTyp;
  }

  /**
   * <p>Getter for itId.</p>
   * @return Long
   **/
  public final Long getItId() {
    return this.itId;
  }

  /**
   * <p>Setter for itId.</p>
   * @param pItId reference
   **/
  public final void setItId(final Long pItId) {
    this.itId = pItId;
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
   * <p>Getter for avQuan.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getAvQuan() {
    return this.avQuan;
  }

  /**
   * <p>Setter for avQuan.</p>
   * @param pAvQuan reference
   **/
  public final void setAvQuan(final BigDecimal pAvQuan) {
    this.avQuan = pAvQuan;
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
   * <p>Getter for unStep.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getUnStep() {
    return this.unStep;
  }

  /**
   * <p>Setter for unStep.</p>
   * @param pUnStep reference
   **/
  public final void setUnStep(final BigDecimal pUnStep) {
    this.unStep = pUnStep;
  }

  /**
   * <p>Getter for forc.</p>
   * @return Boolean
   **/
  public final Boolean getForc() {
    return this.forc;
  }

  /**
   * <p>Setter for forc.</p>
   * @param pForc reference
   **/
  public final void setForc(final Boolean pForc) {
    this.forc = pForc;
  }

  /**
   * <p>Getter for dt1.</p>
   * @return Date
   **/
  public final Date getDt1() {
    return this.dt1;
  }

  /**
   * <p>Setter for dt1.</p>
   * @param pDt1 reference
   **/
  public final void setDt1(final Date pDt1) {
    this.dt1 = pDt1;
  }

  /**
   * <p>Getter for dt2.</p>
   * @return Date
   **/
  public final Date getDt2() {
    return this.dt2;
  }

  /**
   * <p>Setter for dt2.</p>
   * @param pDt2 reference
   **/
  public final void setDt2(final Date pDt2) {
    this.dt2 = pDt2;
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
   * <p>Getter for sel.</p>
   * @return SeSeller
   **/
  public final SeSeller getSel() {
    return this.sel;
  }

  /**
   * <p>Setter for sel.</p>
   * @param pSel reference
   **/
  public final void setSel(final SeSeller pSel) {
    this.sel = pSel;
  }
}
