package org.beigesoft.webstore.service;

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

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.delegate.IDelegateEvaluate;
import org.beigesoft.model.IRequestData;
import org.beigesoft.webstore.persistable.SeSeller;

/**
 * <p>Evaluates S.E.Seller filter
 * or throws "SOMETHING_WRONG" if not found.</p>
 * @author Yury Demidenko
 */
public class SeSellerFilter implements IDelegateEvaluate<IRequestData, String> {

  /**
   * <p>S.E.Seller find service.</p>
   **/
  private IFindSeSeller findSeSeller;

  /**
   * <p>Evaluates S.E.Seller filter
   * or throws "SOMETHING_WRONG" if not found.</p>
   * @param pData data
   * @return S.E.Seller filter
   * @throws Exception - if not S.E.Seller
   **/
  public final String evaluate(final IRequestData pData) throws Exception {
    SeSeller seSeller = this.findSeSeller.find(null, pData.getUserName());
    if (seSeller == null) {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "It's not S.E.Seller - " + pData.getUserName());
    }
    return "SELLER=" + seSeller.getItsId().getItsId();
  }

  //Simple getters and setters:
  /**
   * <p>Getter for findSeSeller.</p>
   * @return IFindSeSeller
   **/
  public final IFindSeSeller getFindSeSeller() {
    return this.findSeSeller;
  }

  /**
   * <p>Setter for findSeSeller.</p>
   * @param pFindSeSeller reference
   **/
  public final void setFindSeSeller(final IFindSeSeller pFindSeSeller) {
    this.findSeSeller = pFindSeSeller;
  }
}
