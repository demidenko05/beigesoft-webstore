package org.beigesoft.webstore.persistable;

/*
 * Copyright (c) 2019 Beigesoft™
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
import org.beigesoft.accounting.persistable.ServiceToSale;
import org.beigesoft.webstore.model.EDelivering;

/**
 * <p>
 * Model of used delivering methods.
 * </p>
 *
 * @author Yury Demidenko
 */
public class Deliv extends AEditableHasVersion implements IHasId<EDelivering> {

  /**
   * <p>Delivering, PK.</p>
   **/
  private EDelivering itsId;

  /**
   * <p>Forced service, if applied.</p>
   **/
  private ServiceToSale frcSr;

  /**
   * <p>Forced service applying method,
   * e.g. minimum cart total to omit delivering fee.</p>
   **/
  private Integer apMt;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final EDelivering getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pItsId model ID
   **/
  @Override
  public final void setItsId(final EDelivering pItsId) {
    this.itsId = pItsId;
  }

  /**
   * <p>Getter for frcSr.</p>
   * @return ServiceToSale
   **/
  public final ServiceToSale getFrcSr() {
    return this.frcSr;
  }

  /**
   * <p>Setter for frcSr.</p>
   * @param pFrcSr reference
   **/
  public final void setFrcSr(final ServiceToSale pFrcSr) {
    this.frcSr = pFrcSr;
  }

  /**
   * <p>Getter for apMt.</p>
   * @return Integer
   **/
  public final Integer getApMt() {
    return this.apMt;
  }

  /**
   * <p>Setter for apMt.</p>
   * @param pApMt reference
   **/
  public final void setApMt(final Integer pApMt) {
    this.apMt = pApMt;
  }
}
