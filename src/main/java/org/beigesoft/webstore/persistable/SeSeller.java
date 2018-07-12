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

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;
import org.beigesoft.persistable.UserTomcat;
import org.beigesoft.accounting.persistable.DebtorCreditor;

/**
 * <p>
 * Model of SeSeller.
 * </p>
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
   * <p>User from JEE JDBC based authentication, not null.</p>
   **/
  private UserTomcat userAuth;

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
   * @param pItsId model ID
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
   * <p>Getter for userAuth.</p>
   * @return UserTomcat
   **/
  public final UserTomcat getUserAuth() {
    return this.userAuth;
  }

  /**
   * <p>Setter for userAuth.</p>
   * @param pUserTomcat reference
   **/
  public final void setUserAuth(final UserTomcat pUserTomcat) {
    this.userAuth = pUserTomcat;
  }
}
