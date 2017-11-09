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

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;
import org.beigesoft.accounting.persistable.DebtorCreditor;

/**
 * <pre>
 * Model of SeSeller.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class SeSeller extends AEditableHasVersion
  implements IHasId<DebtorCreditor> {

  /**
   * <p>Seller, PK.</p>
   **/
  private DebtorCreditor seller;

  /**
   * <p>Email, not null.</p>
   **/
  private String itsEmail;

  /**
   * <p>Password, not null.</p>
   **/
  private String itsPassword;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final DebtorCreditor getItsId() {
    return this.seller;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pId model ID
   **/
  @Override
  public final void setItsId(final DebtorCreditor pItsId) {
    this.seller = pItsId;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for seller.</p>
   * @return DebtorCreditor
   **/
  public final DebtorCreditor getSeller() {
    return this.seller;
  }

  /**
   * <p>Setter for seller.</p>
   * @param pSeller reference
   **/
  public final void setSeller(final DebtorCreditor pSeller) {
    this.seller = pSeller;
  }

  /**
   * <p>Getter for itsEmail.</p>
   * @return String
   **/
  public final String getItsEmail() {
    return this.itsEmail;
  }

  /**
   * <p>Setter for itsEmail.</p>
   * @param pItsEmail reference
   **/
  public final void setItsEmail(final String pItsEmail) {
    this.itsEmail = pItsEmail;
  }

  /**
   * <p>Getter for itsPassword.</p>
   * @return String
   **/
  public final String getItsPassword() {
    return this.itsPassword;
  }

  /**
   * <p>Setter for itsPassword.</p>
   * @param pItsPassword reference
   **/
  public final void setItsPassword(final String pItsPassword) {
    this.itsPassword = pItsPassword;
  }
}
