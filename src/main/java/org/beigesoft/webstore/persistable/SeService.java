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

import org.beigesoft.model.IService;
import org.beigesoft.model.EServTime;
import org.beigesoft.accounting.persistable.base.AItem;

/**
 * <p>Model of S.E.Service.</p>
 *
 * @author Yury Demidenko
 */
public class SeService extends AItem<SeService, DestTaxSeServiceLn>
  implements IService, IHasSeSeller<Long> {

  /**
   * <p>Seller.</p>
   **/
  private SeSeller seller;

  /**
   * <p>Destination taxes categories and rules.</p>
   **/
  private List<DestTaxSeServiceLn> destinationTaxes;

  /**
   * <p>Not null, default TIME, booking time method.</p>
   **/
  private EServTime tmMe = EServTime.TIME;

  /**
   * <p>Additional time method,
   * e.g. step from zero in minutes (5/10/15/20/30) for tmMe=="*TIME*".</p>
   **/
  private Integer tmAd;

  /**
   * <p>Getter for seller.</p>
   * @return SeSeller
   **/
  @Override
  public final SeSeller getSeller() {
    return this.seller;
  }

  /**
   * <p>Setter for seller.</p>
   * @param pSeller reference
   **/
  @Override
  public final void setSeller(final SeSeller pSeller) {
    this.seller = pSeller;
  }

  /**
   * <p>Getter for destTaxes.</p>
   * @return List<DestTaxSeServiceLn>
   **/
  @Override
  public final List<DestTaxSeServiceLn> getDestinationTaxes() {
    return this.destinationTaxes;
  }

  /**
   * <p>Setter for destTaxes.</p>
   * @param pDestTaxes reference
   **/
  @Override
  public final void setDestinationTaxes(
    final List<DestTaxSeServiceLn> pDestTaxes) {
    this.destinationTaxes = pDestTaxes;
  }

  /**
   * <p>Getter for tmMe.</p>
   * @return EServTime
   **/
  @Override
  public final EServTime getTmMe() {
    return this.tmMe;
  }

  /**
   * <p>Setter for tmMe.</p>
   * @param pTmMe reference
   **/
  @Override
  public final void setTmMe(final EServTime pTmMe) {
    this.tmMe = pTmMe;
  }

  /**
   * <p>Getter for tmAd.</p>
   * @return Integer
   **/
  @Override
  public final Integer getTmAd() {
    return this.tmAd;
  }

  /**
   * <p>Setter for tmAd.</p>
   * @param pTmAd reference
   **/
  @Override
  public final void setTmAd(final Integer pTmAd) {
    this.tmAd = pTmAd;
  }
}
