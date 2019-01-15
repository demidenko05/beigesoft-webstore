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

import org.beigesoft.persistable.AHasNameIdLongVersion;
import org.beigesoft.accounting.persistable.DebtorCreditor;
import org.beigesoft.accounting.persistable.TaxDestination;

/**
 * <p>
 * Model of OnlineBuyer (autogenerated ID).
 * If registeredPassword is null and fre = false, then
 * it's free for assing to new buyer.
 * </p>
 *
 * @author Yury Demidenko
 */
public class OnlineBuyer extends AHasNameIdLongVersion {

  /**
   * <p>Registered Password, null for unregistered buyer.</p>
   **/
  private String registeredPassword;

  /**
   * <p>Registered customer, null for unregistered buyer.</p>
   **/
  private DebtorCreditor regCustomer;

  /**
   * <p>Registered email, if applied.</p>
   **/
  private String regEmail;

  /**
   * <p>Registered address1, if applied.</p>
   **/
  private String regAddress1;

  /**
   * <p>Registered address2, if applied.</p>
   **/
  private String regAddress2;

  /**
   * <p>Registered Zip, if applied.</p>
   **/
  private String regZip;

  /**
   * <p>Registered Country, if applied.</p>
   **/
  private String regCountry;

  /**
   * <p>Registered State, if applied.</p>
   **/
  private String regState;

  /**
   * <p>Registered City, if applied.</p>
   **/
  private String regCity;

  /**
   * <p>Registered Phone, if applied.</p>
   **/
  private String regPhone;

  //For S.E. sellers without DebtorCreditor:
  /**
   * <p>TIN.</p>
   **/
  private String tin;

  /**
   * <p>Not null, false default.
   * If sales taxes must be omitted for this buyer.</p>
   **/
  private Boolean foreig = Boolean.FALSE;

  /**
   * <p>Only for overseas/overstate buyers and S.E.items i.e.
   * without creating DebtorCreditor.</p>
   **/
  private TaxDestination taxDest;

  /**
   * <p>Last time login or logged change cart, logout means 0.
   * Buyer is logged then Now - lsTm not exceed 30 minutes.
   * </p>
   **/
  private Long lsTm = 0L;

  /**
   * <p>Not null, false default.
   * If registeredPassword=null and fre=true, then use it for new
   * buyer.</p>
   **/
  private Boolean fre = Boolean.FALSE;

  /**
   * <p>Buyer's last/current session ID.</p>
   **/
  private String buSeId;

  //Simple getters and setters:
  /**
   * <p>Getter for registeredPassword.</p>
   * @return String
   **/
  public final String getRegisteredPassword() {
    return this.registeredPassword;
  }

  /**
   * <p>Setter for registeredPassword.</p>
   * @param pRegisteredPassword reference
   **/
  public final void setRegisteredPassword(final String pRegisteredPassword) {
    this.registeredPassword = pRegisteredPassword;
  }

  /**
   * <p>Getter for regCustomer.</p>
   * @return DebtorCreditor
   **/
  public final DebtorCreditor getRegCustomer() {
    return this.regCustomer;
  }

  /**
   * <p>Setter for regCustomer.</p>
   * @param pRegCustomer reference
   **/
  public final void setRegCustomer(final DebtorCreditor pRegCustomer) {
    this.regCustomer = pRegCustomer;
  }

  /**
   * <p>Getter for regEmail.</p>
   * @return String
   **/
  public final String getRegEmail() {
    return this.regEmail;
  }

  /**
   * <p>Setter for regEmail.</p>
   * @param pRegEmail reference
   **/
  public final void setRegEmail(final String pRegEmail) {
    this.regEmail = pRegEmail;
  }

  /**
   * <p>Getter for regAddress1.</p>
   * @return String
   **/
  public final String getRegAddress1() {
    return this.regAddress1;
  }

  /**
   * <p>Setter for regAddress1.</p>
   * @param pRegAddress1 reference
   **/
  public final void setRegAddress1(final String pRegAddress1) {
    this.regAddress1 = pRegAddress1;
  }

  /**
   * <p>Getter for regAddress2.</p>
   * @return String
   **/
  public final String getRegAddress2() {
    return this.regAddress2;
  }

  /**
   * <p>Setter for regAddress2.</p>
   * @param pRegAddress2 reference
   **/
  public final void setRegAddress2(final String pRegAddress2) {
    this.regAddress2 = pRegAddress2;
  }

  /**
   * <p>Getter for regZip.</p>
   * @return String
   **/
  public final String getRegZip() {
    return this.regZip;
  }

  /**
   * <p>Setter for regZip.</p>
   * @param pRegZip reference
   **/
  public final void setRegZip(final String pRegZip) {
    this.regZip = pRegZip;
  }

  /**
   * <p>Getter for regCountry.</p>
   * @return String
   **/
  public final String getRegCountry() {
    return this.regCountry;
  }

  /**
   * <p>Setter for regCountry.</p>
   * @param pRegCountry reference
   **/
  public final void setRegCountry(final String pRegCountry) {
    this.regCountry = pRegCountry;
  }

  /**
   * <p>Getter for regState.</p>
   * @return String
   **/
  public final String getRegState() {
    return this.regState;
  }

  /**
   * <p>Setter for regState.</p>
   * @param pRegState reference
   **/
  public final void setRegState(final String pRegState) {
    this.regState = pRegState;
  }

  /**
   * <p>Getter for regCity.</p>
   * @return String
   **/
  public final String getRegCity() {
    return this.regCity;
  }

  /**
   * <p>Setter for regCity.</p>
   * @param pRegCity reference
   **/
  public final void setRegCity(final String pRegCity) {
    this.regCity = pRegCity;
  }

  /**
   * <p>Getter for regPhone.</p>
   * @return Long
   **/
  public final String getRegPhone() {
    return this.regPhone;
  }

  /**
   * <p>Setter for regPhone.</p>
   * @param pRegPhone reference
   **/
  public final void setRegPhone(final String pRegPhone) {
    this.regPhone = pRegPhone;
  }

  /**
   * <p>Getter for tin.</p>
   * @return String
   **/
  public final String getTin() {
    return this.tin;
  }

  /**
   * <p>Setter for tin.</p>
   * @param pTin reference
   **/
  public final void setTin(final String pTin) {
    this.tin = pTin;
  }

  /**
   * <p>Getter for foreig.</p>
   * @return Boolean
   **/
  public final Boolean getForeig() {
    return this.foreig;
  }

  /**
   * <p>Setter for foreig.</p>
   * @param pForeig reference
   **/
  public final void setForeig(final Boolean pForeig) {
    this.foreig = pForeig;
  }

  /**
   * <p>Getter for taxDest.</p>
   * @return TaxDestination
   **/
  public final TaxDestination getTaxDest() {
    return this.taxDest;
  }

  /**
   * <p>Setter for taxDest.</p>
   * @param pTaxDest reference
   **/
  public final void setTaxDest(final TaxDestination pTaxDest) {
    this.taxDest = pTaxDest;
  }
  /**
   * <p>Getter for lsTm.</p>
   * @return Long
   **/
  public final Long getLsTm() {
    return this.lsTm;
  }

  /**
   * <p>Setter for lsTm.</p>
   * @param pLsTm reference
   **/
  public final void setLsTm(final Long pLsTm) {
    this.lsTm = pLsTm;
  }

  /**
   * <p>Getter for fre.</p>
   * @return Boolean
   **/
  public final Boolean getFre() {
    return this.fre;
  }

  /**
   * <p>Setter for fre.</p>
   * @param pFre reference
   **/
  public final void setFre(final Boolean pFre) {
    this.fre = pFre;
  }

  /**
   * <p>Getter for buSeId.</p>
   * @return String
   **/
  public final String getBuSeId() {
    return this.buSeId;
  }

  /**
   * <p>Setter for buSeId.</p>
   * @param pBuSeId reference
   **/
  public final void setBuSeId(final String pBuSeId) {
    this.buSeId = pBuSeId;
  }
}
