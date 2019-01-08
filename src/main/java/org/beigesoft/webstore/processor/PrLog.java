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
import java.util.Date;

import org.beigesoft.model.IRequestData;
import org.beigesoft.log.ILogger;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.webstore.persistable.OnlineBuyer;

/**
 * <p>Processor that registers, logins, logouts buyer.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrLog<RS> implements IProcessor {

  /**
   * <p>Logger.</p>
   **/
  private ILogger log;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Processors factory.</p>
   **/
  private IFactoryAppBeansByName<IProcessor> procFac;

  /**
   * <p>Process request.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    Long buyerId = null;
    String buyerIdStr = pRqDt.getCookieValue("cBuyerId");
    if (buyerIdStr != null && buyerIdStr.length() > 0) {
       buyerId = Long.valueOf(buyerIdStr);
    }
    OnlineBuyer buyer;
    if (buyerId == null) {
      buyer = new OnlineBuyer();
      buyer.setIsNew(true);
    } else {
      buyer = getSrvOrm().retrieveEntityById(pRqVs, OnlineBuyer.class, buyerId);
      if (buyer == null) { // deleted for any reason, so create new:
        buyer = new OnlineBuyer();
        buyer.setIsNew(true);
      }
    }
    String nm = pRqDt.getParameter("nm");
    String em = pRqDt.getParameter("em");
    String pw = pRqDt.getParameter("pw");
    String pwc = pRqDt.getParameter("pwc");
    if (buyer.getIsNew()) {
      //creating:
      if (nm != null && pw != null && pwc != null && em != null) {
        if (nm.length() > 5 && pw.length() > 7 && pw.equals(pwc)
          && em.length() > 5) {
          buyer.setItsName(nm);
          buyer.setRegisteredPassword(pw);
          buyer.setRegEmail(em);
          buyer.setLsTm(new Date().getTime());
          this.srvOrm.insertEntity(pRqVs, buyer);
          pRqDt.setCookieValue("cBuyerId", buyer.getItsId().toString());
        } else {
          pRqDt.setAttribute("errMsg", "buyCrRul");
        }
      } else {
        spam(pRqVs, pRqDt);
      }
    } else {
      //creating/cange all:
      if (nm != null && pw != null && pwc != null && em != null) {
        if (nm.length() > 5 && pw.length() > 7 && pw.equals(pwc)
          && em.length() > 5) {
          buyer.setItsName(nm);
          buyer.setRegisteredPassword(pw);
          buyer.setRegEmail(em);
          buyer.setLsTm(new Date().getTime());
          this.srvOrm.updateEntity(pRqVs, buyer);
        } else {
          pRqDt.setAttribute("errMsg", "buyCrRul");
        }
      } else if (nm != null && pw == null && em != null) {
        //change name, email:
        if (nm.length() > 5 && em.length() > 5) {
          buyer.setItsName(nm);
          buyer.setRegEmail(em);
          buyer.setLsTm(new Date().getTime());
          this.srvOrm.updateEntity(pRqVs, buyer);
        } else {
          pRqDt.setAttribute("errMsg", "buyEmRul");
        }
      } else if (pw != null && pwc != null) {
        //change password:
        if (pw.length() > 7 && pw.equals(pwc)) {
          buyer.setRegisteredPassword(pw);
          buyer.setLsTm(new Date().getTime());
          this.srvOrm.updateEntity(pRqVs, buyer);
        } else {
          pRqDt.setAttribute("errMsg", "buyPwdRul");
        }
      } else if (buyer.getRegisteredPassword() != null) {
        //login/logout action:
        long now = new Date().getTime();
        if (now - buyer.getLsTm() > 1800000L) { //login
          if (pw != null && pw.equals(buyer.getRegisteredPassword())) {
            buyer.setLsTm(now);
            this.srvOrm.updateEntity(pRqVs, buyer);
          } else {
            pRqDt.setAttribute("errMsg", "wrong_password");
          }
        } else { //logout
          buyer.setLsTm(0L);
          this.srvOrm.updateEntity(pRqVs, buyer);
        }
      } else {
        spam(pRqVs, pRqDt);
      }
    }
    String procNm = pRqDt.getParameter("nmPrcRed");
    IProcessor proc = this.procFac.lazyGet(pRqVs, procNm);
    proc.process(pRqVs, pRqDt);
  }


  /**
   * <p>Handles spam.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @throws Exception - an exception
   **/
  public final void spam(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    //TODO
  }

  //Simple getters and setters:
  /**
   * <p>Getter for log.</p>
   * @return ILogger
   **/
  public final ILogger getLog() {
    return this.log;
  }

  /**
   * <p>Setter for log.</p>
   * @param pLog reference
   **/
  public final void setLog(final ILogger pLog) {
    this.log = pLog;
  }

  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvDatabase<RS>
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
   * <p>Getter for procFac.</p>
   * @return IFactoryAppBeansByName<IProcessor>
   **/
  public final IFactoryAppBeansByName<IProcessor> getProcFac() {
    return this.procFac;
  }

  /**
   * <p>Setter for procFac.</p>
   * @param pProcFac reference
   **/
  public final void setProcFac(
    final IFactoryAppBeansByName<IProcessor> pProcFac) {
    this.procFac = pProcFac;
  }
}
