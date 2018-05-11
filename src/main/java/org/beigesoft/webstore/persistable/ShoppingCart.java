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
import java.math.BigDecimal;

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;

/**
 * <pre>
 * Model of average buyer rating.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class ShoppingCart extends AEditableHasVersion
  implements IHasId<OnlineBuyer> {

  /**
   * <p>Buyer, PK.</p>
   **/
  private OnlineBuyer buyer;

  /**
   * <p>Total, not null.</p>
   **/
  private BigDecimal itsTotal;

  /**
   * <p>Total items, not null.</p>
   **/
  private Integer totalItems;

  /**
   * <p>Recipient name, if buyer bought for someone else i.e. for this recipient
   * or buyer want to receive goods at different address.</p>
   **/
  private String recipientName;

  /**
   * <p>Recipient Address1, if applied.</p>
   **/
  private String recipientAddress1;

  /**
   * <p>Recipient Address2, if applied.</p>
   **/
  private String recipientAddress2;

  /**
   * <p>Recipient Zip, if applied.</p>
   **/
  private String recipientZip;

  /**
   * <p>Recipient Country, if applied.</p>
   **/
  private String recipientCountry;

  /**
   * <p>Recipient State, if applied.</p>
   **/
  private String recipientState;

  /**
   * <p>Recipient City, if applied.</p>
   **/
  private String recipientCity;

  /**
   * <p>Recipient Phone, if applied.</p>
   **/
  private String recipientPhone;

  /**
   * <p>Items.</p>
   **/
  private List<CartItem> itsItems;

  /**
   * <p>Taxes.</p>
   **/
  private List<CartTaxLine> taxes;

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
   * <p>Getter for itsTotal.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsTotal() {
    return this.itsTotal;
  }

  /**
   * <p>Setter for itsTotal.</p>
   * @param pItsTotal reference
   **/
  public final void setItsTotal(final BigDecimal pItsTotal) {
    this.itsTotal = pItsTotal;
  }

  /**
   * <p>Getter for totalItems.</p>
   * @return Integer
   **/
  public final Integer getTotalItems() {
    return this.totalItems;
  }

  /**
   * <p>Setter for totalItems.</p>
   * @param pTotalItems reference
   **/
  public final void setTotalItems(final Integer pTotalItems) {
    this.totalItems = pTotalItems;
  }

  /**
   * <p>Getter for recipientName.</p>
   * @return String
   **/
  public final String getRecipientName() {
    return this.recipientName;
  }

  /**
   * <p>Setter for recipientName.</p>
   * @param pRecipientName reference
   **/
  public final void setRecipientName(final String pRecipientName) {
    this.recipientName = pRecipientName;
  }

  /**
   * <p>Getter for recipientAddress1.</p>
   * @return String
   **/
  public final String getRecipientAddress1() {
    return this.recipientAddress1;
  }

  /**
   * <p>Setter for recipientAddress1.</p>
   * @param pRecipientAddress1 reference
   **/
  public final void setRecipientAddress1(final String pRecipientAddress1) {
    this.recipientAddress1 = pRecipientAddress1;
  }

  /**
   * <p>Getter for recipientAddress2.</p>
   * @return String
   **/
  public final String getRecipientAddress2() {
    return this.recipientAddress2;
  }

  /**
   * <p>Setter for recipientAddress2.</p>
   * @param pRecipientAddress2 reference
   **/
  public final void setRecipientAddress2(final String pRecipientAddress2) {
    this.recipientAddress2 = pRecipientAddress2;
  }

  /**
   * <p>Getter for recipientZip.</p>
   * @return String
   **/
  public final String getRecipientZip() {
    return this.recipientZip;
  }

  /**
   * <p>Setter for recipientZip.</p>
   * @param pRecipientZip reference
   **/
  public final void setRecipientZip(final String pRecipientZip) {
    this.recipientZip = pRecipientZip;
  }

  /**
   * <p>Getter for recipientCountry.</p>
   * @return String
   **/
  public final String getRecipientCountry() {
    return this.recipientCountry;
  }

  /**
   * <p>Setter for recipientCountry.</p>
   * @param pRecipientCountry reference
   **/
  public final void setRecipientCountry(final String pRecipientCountry) {
    this.recipientCountry = pRecipientCountry;
  }

  /**
   * <p>Getter for recipientState.</p>
   * @return String
   **/
  public final String getRecipientState() {
    return this.recipientState;
  }

  /**
   * <p>Setter for recipientState.</p>
   * @param pRecipientState reference
   **/
  public final void setRecipientState(final String pRecipientState) {
    this.recipientState = pRecipientState;
  }

  /**
   * <p>Getter for recipientCity.</p>
   * @return String
   **/
  public final String getRecipientCity() {
    return this.recipientCity;
  }

  /**
   * <p>Setter for recipientCity.</p>
   * @param pRecipientCity reference
   **/
  public final void setRecipientCity(final String pRecipientCity) {
    this.recipientCity = pRecipientCity;
  }

  /**
   * <p>Getter for recipientPhone.</p>
   * @return String
   **/
  public final String getRecipientPhone() {
    return this.recipientPhone;
  }

  /**
   * <p>Setter for recipientPhone.</p>
   * @param pRecipientPhone reference
   **/
  public final void setRecipientPhone(final String pRecipientPhone) {
    this.recipientPhone = pRecipientPhone;
  }

  /**
   * <p>Getter for itsItems.</p>
   * @return List<CartItem>
   **/
  public final List<CartItem> getItsItems() {
    return this.itsItems;
  }

  /**
   * <p>Setter for itsItems.</p>
   * @param pItsItems reference
   **/
  public final void setItsItems(final List<CartItem> pItsItems) {
    this.itsItems = pItsItems;
  }

  /**
   * <p>Getter for taxes.</p>
   * @return List<CartTaxLine>
   **/
  public final List<CartTaxLine> getTaxes() {
    return this.taxes;
  }

  /**
   * <p>Setter for taxes.</p>
   * @param pTaxes reference
   **/
  public final void setTaxes(final List<CartTaxLine> pTaxes) {
    this.taxes = pTaxes;
  }
}
