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

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.UUID;

import org.beigesoft.model.IRequestData;
import org.beigesoft.model.ColumnsValues;
import org.beigesoft.log.ILogger;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.service.ISrvShoppingCart;
import org.beigesoft.webstore.service.IBuySr;

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
   * <p>DB service.</p>
   */
  private ISrvDatabase<RS> srvDb;

  /**
   * <p>Processors factory.</p>
   **/
  private IFactoryAppBeansByName<IProcessor> procFac;

  /**
   * <p>Shopping Cart service.</p>
   **/
  private ISrvShoppingCart srvCart;

  /**
   * <p>Buyer service.</p>
   **/
  private IBuySr buySr;

  /**
   * <p>Process request.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    OnlineBuyer buyer = this.buySr.getBuyr(pRqVs, pRqDt);
    if (buyer == null) {
      buyer = this.buySr.createBuyr(pRqVs, pRqDt);
    }
    String nm = pRqDt.getParameter("nm");
    String em = pRqDt.getParameter("em");
    String pw = pRqDt.getParameter("pw");
    String pwc = pRqDt.getParameter("pwc");
    long now = new Date().getTime();
    String tbn = OnlineBuyer.class.getSimpleName();
    pRqDt.setAttribute("buyr", buyer);
    if (buyer.getRegEmail() == null) { //unregistered:
      if (nm != null && pw != null && pwc != null && em != null) {
        //creating:
        if (nm.length() > 2 && pw.length() > 7 && pw.equals(pwc)
          && em.length() > 5) {
          Set<String> ndFl = new HashSet<String>();
          ndFl.add("itsId");
          pRqVs.put(tbn + "neededFields", ndFl);
          List<OnlineBuyer> brs = getSrvOrm().retrieveListWithConditions(pRqVs,
            OnlineBuyer.class, "where REGEMAIL='" + em + "'");
          pRqVs.remove(tbn + "neededFields");
          if (brs.size() == 0) {
            buyer.setItsName(nm);
            buyer.setRegisteredPassword(pw);
            buyer.setRegEmail(em);
            buyer.setLsTm(now);
            UUID buseid = UUID.randomUUID();
            buyer.setBuSeId(buseid.toString());
            if (buyer.getIsNew()) {
              this.srvOrm.insertEntity(pRqVs, buyer);
            } else {
              this.srvOrm.updateEntity(pRqVs, buyer);
            }
            pRqDt.setCookieValue("cBuyerId", buyer.getItsId().toString());
            pRqDt.setCookieValue("buSeId", buyer.getBuSeId());
          } else if (brs.size() == 1) {
            pRqDt.setAttribute("errMsg", "emBusy");
          } else {
            getLog().error(pRqVs, PrLog.class,
              "Several users with same email!: " + em);
          }
        } else {
          pRqDt.setAttribute("errMsg", "buyCrRul");
        }
      } else if (pw != null && em != null) {
        //login from new browser
        pRqVs.put(tbn + "regCustomerdeepLevel", 1);
        pRqVs.put(tbn + "taxDestplacedeepLevel", 1);
        List<OnlineBuyer> brs = getSrvOrm().retrieveListWithConditions(
          pRqVs, OnlineBuyer.class, "where REGISTEREDPASSWORD='" + pw
            + "' and REGEMAIL='" + em + "'");
        pRqVs.remove(tbn + "regCustomerdeepLevel");
        pRqVs.remove(tbn + "taxDestplacedeepLevel");
        if (brs.size() == 1) {
          //free buyer and moving its cart by fast updates:
          mkFreBuyr(pRqVs,  pRqDt, buyer, brs.get(0));
        } else if (brs.size() == 0) {
          pRqDt.setAttribute("errMsg", "wrong_em_password");
        } else {
          getLog().error(pRqVs, PrLog.class,
            "Several users with same password and email!: " + em);
        }
      } else {
        spam(pRqVs, pRqDt);
      }
    } else { //registered:
      if (now - buyer.getLsTm() < 1800000L && buyer.getBuSeId() != null) {
        //there is opened session:
        String buSeId = pRqDt.getCookieValue("buSeId");
        if (buyer.getBuSeId().equals(buSeId)) {
          //authorized requests:
          if (nm != null && pw == null && em == null) {
            //change name:
            if (nm.length() > 2) {
              buyer.setItsName(nm);
              buyer.setLsTm(now);
              this.srvOrm.updateEntity(pRqVs, buyer);
            } else {
              pRqDt.setAttribute("errMsg", "buyEmRul");
            }
          } else if (pw != null && pwc != null) {
            //change password:
            if (pw.length() > 7 && pw.equals(pwc)) {
              buyer.setRegisteredPassword(pw);
              buyer.setLsTm(now);
              this.srvOrm.updateEntity(pRqVs, buyer);
            } else {
              pRqDt.setAttribute("errMsg", "buyPwdRul");
            }
          } else {
            //logout action:
            buyer.setLsTm(0L);
            this.srvOrm.updateEntity(pRqVs, buyer);
          }
        } else { //either spam or buyer login from other browser without logout
          spam(pRqVs, pRqDt);
        }
      } else {
        //unauthorized requests:
        if (pw != null) {
          //login action:
          if (pw.equals(buyer.getRegisteredPassword())) {
            buyer.setLsTm(now);
            UUID buseid = UUID.randomUUID();
            buyer.setBuSeId(buseid.toString());
            pRqDt.setCookieValue("buSeId", buyer.getBuSeId());
            this.srvOrm.updateEntity(pRqVs, buyer);
          } else {
            pRqDt.setAttribute("errMsg", "wrong_password");
          }
        } else {
          spam(pRqVs, pRqDt);
        }
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

  /**
   * <p>Makes free buyer and moving its cart by fast updates.</p>
   * @param pRqVs request scoped vars
   * @param pRqDt Request Data
   * @param pBuTmp buyer unregistered
   * @param pBuyr buyer registered
   * @throws Exception - an exception
   **/
  public final void mkFreBuyr(final Map<String, Object> pRqVs,
    final IRequestData pRqDt, final OnlineBuyer pBuTmp,
      final OnlineBuyer pBuyr) throws Exception {
    long now = new Date().getTime();
    pBuTmp.setFre(true);
    pBuTmp.setRegEmail(null);
    pBuTmp.setRegisteredPassword(null);
    pBuTmp.setLsTm(0L);
    this.srvOrm.updateEntity(pRqVs, pBuTmp);
    Long obid = pBuTmp.getItsId();
    ColumnsValues cvs = new ColumnsValues();
    cvs.setIdColumnsNames(new String[] {"itsId"});
    cvs.put("itsOwner", pBuyr.getItsId());
    pBuyr.setLsTm(now);
    UUID buseid = UUID.randomUUID();
    pBuyr.setBuSeId(buseid.toString());
    this.srvOrm.updateEntity(pRqVs, pBuyr);
    pRqDt.setCookieValue("buSeId", pBuyr.getBuSeId());
    pRqDt.setCookieValue("cBuyerId", pBuyr.getItsId().toString());
    pRqDt.setAttribute("buyr", pBuyr);
    int clc = this.srvDb.executeUpdate("CARTLN", cvs, "ITSOWNER=" + obid);
    if (clc > 0) {
      this.srvDb.executeUpdate("CARTTXLN", cvs, "ITSOWNER=" + obid);
      this.srvDb.executeUpdate("CARTTOT", cvs, "ITSOWNER=" + obid);
      Cart cart = this.srvCart.getShoppingCart(pRqVs, pRqDt, true, false);
      TradingSettings ts = (TradingSettings) pRqVs.get("tradSet");
      AccSettings as = (AccSettings) pRqVs.get("accSet");
      TaxDestination txRules = this.srvCart.revealTaxRules(pRqVs, cart, as);
      if (txRules != null) {
        pRqDt.setAttribute("txRules", txRules);
      }
      //redo prices and taxes:
      for (CartLn cl : cart.getItems()) {
        if (!cl.getDisab()) {
          this.srvCart.makeCartLine(pRqVs, cl, as, ts, txRules, true, true);
          this.srvCart.makeCartTotals(pRqVs, ts, cl, as, txRules);
        }
      }
    }
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
   * <p>Getter for srvDb.</p>
   * @return ISrvDatabase<RS>
   **/
  public final ISrvDatabase<RS> getSrvDb() {
    return this.srvDb;
  }

  /**
   * <p>Setter for srvDb.</p>
   * @param pSrvDb reference
   **/
  public final void setSrvDb(final ISrvDatabase<RS> pSrvDb) {
    this.srvDb = pSrvDb;
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

  /**
   * <p>Getter for srvCart.</p>
   * @return ISrvShoppingCart
   **/
  public final ISrvShoppingCart getSrvCart() {
    return this.srvCart;
  }

  /**
   * <p>Setter for srvCart.</p>
   * @param pSrvCart reference
   **/

  public final void setSrvCart(final ISrvShoppingCart pSrvCart) {
    this.srvCart = pSrvCart;
  }
  /**
   * <p>Getter for buySr.</p>
   * @return IBuySr
   **/
  public final IBuySr getBuySr() {
    return this.buySr;
  }

  /**
   * <p>Setter for buySr.</p>
   * @param pBuySr reference
   **/
  public final void setBuySr(final IBuySr pBuySr) {
    this.buySr = pBuySr;
  }
}
