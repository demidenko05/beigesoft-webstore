package org.beigesoft.webstore.processor;

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

import java.util.Map;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.webstore.model.EOrdStat;
import org.beigesoft.webstore.persistable.CustOrder;
import org.beigesoft.webstore.service.ICncOrd;

/**
 * <p>Service that change customer order status. It's possible:
 * <ul>
 * <li>From BOOKED, PAYED, CLOSED to CANCELLED, action "act=cnc"</li>
 * <li>From BOOKED to PAYED, action "act=pyd"</li>
 * <li>From BOOKED, PAYED  to CLOSED, action "act=cls"</li>
 * </ul>. It checks derived from order invoice (if it exist).</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrCuOrSv<RS> implements IEntityProcessor<CustOrder, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Cancel order service.</p>
   **/
  private ICncOrd cncOrd;

  /**
   * <p>Process entity request.</p>
   * @param pRqVs additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRqDt Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final CustOrder process(final Map<String, Object> pRqVs,
    final CustOrder pEntity, final IRequestData pRqDt) throws Exception {
    String act = pRqDt.getParameter("act");
    if (!("cnc".equals(act) || "pyd".equals(act) || "cls".equals(act))) {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "Wrong action CO! " + act);
    }
    CustOrder oco = null;
    if (pEntity.getIsNew()) {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "Attempt creating CO!");
    } else {
      oco = this.srvOrm.retrieveEntityById(pRqVs, CustOrder.class,
        pEntity.getItsId());
      if (oco.getInId() != null) {
        throw new Exception("NEY CU with INV");
      }
      oco.setDescr(pEntity.getDescr());
      boolean isNdUp = true;
      if ("cnc".equals(act)) {
        if (!(oco.getStat().equals(EOrdStat.BOOKED)
          || oco.getStat().equals(EOrdStat.PAYED)
            || oco.getStat().equals(EOrdStat.CLOSED))) {
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "Wrong action CO for status ! " + act + "/" + oco.getStat());
        }
        this.cncOrd.cancel(pRqVs, oco, EOrdStat.CANCELED);
        isNdUp = false;
      } else if ("pyd".equals(act)) {
        if (!oco.getStat().equals(EOrdStat.BOOKED)) {
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "Wrong action CO for status ! " + act + "/" + oco.getStat());
        }
        oco.setStat(EOrdStat.PAYED);
      } else if ("cls".equals(act)) {
        if (!(oco.getStat().equals(EOrdStat.BOOKED)
          || oco.getStat().equals(EOrdStat.PAYED))) {
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "Wrong action CO for status ! " + act + "/" + oco.getStat());
        }
        oco.setStat(EOrdStat.CLOSED);
      }
      if (isNdUp) {
        String[] fieldsNames =
          new String[] {"itsId", "itsVersion", "stat", "descr"};
        pRqVs.put("fieldsNames", fieldsNames);
        this.srvOrm.updateEntity(pRqVs, oco);
        pRqVs.remove("fieldsNames");
      }
    }
    return oco;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }

  /**
   * <p>Getter for cncOrd.</p>
   * @return ICncOrd
   **/
  public final ICncOrd getCncOrd() {
    return this.cncOrd;
  }

  /**
   * <p>Setter for cncOrd.</p>
   * @param pCncOrd reference
   **/
  public final void setCncOrd(final ICncOrd pCncOrd) {
    this.cncOrd = pCncOrd;
  }
}
