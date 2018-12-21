package org.beigesoft.webstore.persistable;

/*
 * Copyright (c) 2018 Beigesof™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import java.util.Date;

import org.beigesoft.persistable.AHasIdLongVersion;

/**
 * <p>S.E.-service busy from till time.</p>
 *
 * @author Yury Demidenko
 */
public class SeSerBus extends AHasIdLongVersion implements IHasSeSeller<Long> {

  /**
   * <p>Service, not null.</p>
   **/
  private SeService serv;

  /**
   * <p>Not null, busy from time (include).</p>
   **/
  private Date frTm;

  /**
   * <p>Not null, busy till time (exclude).</p>
   **/
  private Date tiTm;

  /**
   * <p>Getter for seller.</p>
   * @return SeSeller
   **/
  @Override
  public final SeSeller getSeller() {
    return this.serv.getSeller();
  }

  /**
   * <p>Setter for seller.</p>
   * @param pSeller reference
   **/
  @Override
  public final void setSeller(final SeSeller pSeller) {
    this.serv.setSeller(pSeller);
  }

  //Simple getters and setters:
  /**
   * <p>Getter for serv.</p>
   * @return SeService
   **/
  public final SeService getServ() {
    return this.serv;
  }

  /**
   * <p>Setter for serv.</p>
   * @param pServ reference
   **/
  public final void setServ(final SeService pServ) {
    this.serv = pServ;
  }

  /**
   * <p>Getter for frTm.</p>
   * @return Date
   **/
  public final Date getFrTm() {
    return this.frTm;
  }

  /**
   * <p>Setter for frTm.</p>
   * @param pFrTm reference
   **/
  public final void setFrTm(final Date pFrTm) {
    this.frTm = pFrTm;
  }

  /**
   * <p>Getter for tiTm.</p>
   * @return Date
   **/
  public final Date getTiTm() {
    return this.tiTm;
  }

  /**
   * <p>Setter for tiTm.</p>
   * @param pTiTm reference
   **/
  public final void setTiTm(final Date pTiTm) {
    this.tiTm = pTiTm;
  }
}
