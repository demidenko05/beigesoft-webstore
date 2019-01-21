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

import java.util.Set;
import java.util.Map;

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
   * <p>Se entities. Only <b>list</b> operation is allowed, no "modify".</p>
   **/
  private Set<Class<?>> seEntities;

  /**
   * <p>Evaluates S.E.Seller filter
   * or throws "SOMETHING_WRONG" if not found.
   * For se entities it return NULL.</p>
   * @param pReqVars additional request scoped parameters
   * @param pData data
   * @return S.E.Seller filter
   * @throws Exception - if not S.E.Seller
   **/
  public final String evaluate(final Map<String, Object> pReqVars,
    final IRequestData pData) throws Exception {
    SeSeller seSeller = this.findSeSeller.find(pReqVars, pData.getUserName());
    if (seSeller == null) {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "It's not S.E.Seller - " + pData.getUserName());
    }
    String nmEnt = pData.getParameter("nmEnt");
    boolean isSe = false;
    for (Class<?> cl : this.seEntities) {
      if (cl.getSimpleName().equals(nmEnt)) {
        isSe = true;
        break;
      }
    }
    if (isSe) {
      //simple-hummer implementation:
      String wheSe;
      if (nmEnt.startsWith("I18n")) {
        wheSe = "HASNAME.SELLER=";
      } else if (nmEnt.endsWith("Price") || nmEnt.endsWith("Place")
        || nmEnt.endsWith("Specifics")) {
        wheSe = "ITEM.SELLER=";
      } else if (nmEnt.equals("CuOrSe")) {
        wheSe = "CUORSE.SEL=";
      } else { //good/service/paymd
        wheSe = nmEnt.toUpperCase() + ".SELLER=";
      }
      return wheSe + seSeller.getItsId().getItsId();
    } else {
      return null;
    }
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

  /**
   * <p>Getter for seEntities.</p>
   * @return Set<Class<?>>
   **/
  public final Set<Class<?>> getSeEntities() {
    return this.seEntities;
  }

  /**
   * <p>Setter for seEntities.</p>
   * @param pSeEntities reference
   **/
  public final void setSeEntities(final Set<Class<?>> pSeEntities) {
    this.seEntities = pSeEntities;
  }
}
