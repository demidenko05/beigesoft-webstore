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

import java.util.Map;
import java.util.List;

import org.beigesoft.service.ISrvOrm;
import org.beigesoft.webstore.persistable.SeSeller;

/**
 * <p>S.E.Seller finder service. Standard implementation
 * for small number of S.E. Sellers. It requires opened transaction.
 * Methods are synchronized.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class FindSeSeller<RS> implements IFindSeSeller {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Cache of all S.E.Sellers.</p>
   **/
  private List<SeSeller> sesellers;

  /**
   * <p>Finds by name.</p>
   * @param pAddParam additional param
   * @param pName seller's
   * @return S.E. Seller or null
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized SeSeller find(final Map<String, Object> pAddParam,
    final String pName) throws Exception {
    if (this.sesellers == null) {
      this.sesellers = this.srvOrm.retrieveList(pAddParam, SeSeller.class);
    }
    for (SeSeller ses : this.sesellers) {
      if (ses.getUserAuth().getItsUser().equals(pName)) {
        return ses;
      }
    }
    return null;
  }

  /**
   * <p>Handle S.E. seller changed.
   * Any change leads to refreshing whole list.</p>
   * @param pAddParam additional param
   * @param pName seller's, null means "refresh all"
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized void handleSeSellerChanged(
    final Map<String, Object> pAddParam,
      final String pName) throws Exception {
    this.sesellers = null;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final synchronized ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final synchronized void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }

  /**
   * <p>Getter for sesellers.</p>
   * @return List<SeSeller>
   **/
  public final synchronized List<SeSeller> getSesellers() {
    return this.sesellers;
  }

  /**
   * <p>Setter for sesellers.</p>
   * @param pSesellers reference
   **/
  public final synchronized void setSesellers(final List<SeSeller> pSesellers) {
    this.sesellers = pSesellers;
  }
}
