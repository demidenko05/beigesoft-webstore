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

import org.beigesoft.model.IRequestData;
import org.beigesoft.handler.IHandlerRequestDch;
import org.beigesoft.log.ILogger;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.Currency;
import org.beigesoft.webstore.persistable.I18nWebStore;
import org.beigesoft.webstore.persistable.I18nCatalogGs;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.CurrRate;

/**
 * <p>It handles webstore request for setting trading variables
 * and additional internationalization.
 * It's invoked by the accounting I18N handler.</p>
 *
 * @param <RS> platform dependent RDBMS recordset
 * @author Yury Demidenko
 */
public class HndlTradeVarsRequest<RS> implements IHandlerRequestDch {

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>Database service.</p>
   */
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   */
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for trading settings.</p>
   **/
  private ISrvTradingSettings srvTradingSettings;

  /**
   * <p>Business service for additional settings.</p>
   **/
  private ISrvSettingsAdd srvSettingsAdd;

  /**
   * <p>Helper that is used in JSP.</p>
   **/
  private UtlTradeJsp utlTradeJsp;

  /**
   * <p>Cached common trading I18N parameters.</p>
   */
  private List<I18nWebStore> i18nWebStoreList;

  /**
   * <p>Cached I18N catalogs.</p>
   */
  private List<I18nCatalogGs> i18nCatalogs;

  /**
   * <p>Cached foreign currency rates.</p>
   */
  private List<CurrRate> currRates;

  /**
   * <p>Handle request.</p>
   * @param pReqVars Request scoped variables
   * @param pRequestData Request Data
   * @throws Exception - an exception
   */
  @Override
  public final void handle(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    TradingSettings ts = srvTradingSettings
      .lazyGetTradingSettings(pReqVars);
    pReqVars.put("tradSet", ts);
    pReqVars.put("setAdd", srvSettingsAdd.lazyGetSettingsAdd(pReqVars));
    pRequestData.setAttribute("utlTradeJsp", this.utlTradeJsp);
    List<CurrRate> curRatesTmp = null;
    if (ts.getUseAdvancedI18n()) {
      String lang = (String) pReqVars.get("lang");
      String langDef = (String) pReqVars.get("langDef");
      if (lang != null && langDef != null && !lang.equals(langDef)) {
        List<I18nWebStore> i18nTrTmp = null;
        List<I18nCatalogGs> i18nCtTmp;
        synchronized (this) {
          if (this.i18nWebStoreList == null) {
            try {
              this.logger.info(null, HndlTradeVarsRequest.class,
                "Refreshing I18N data...");
              this.srvDatabase.setIsAutocommit(false);
              this.srvDatabase.setTransactionIsolation(ISrvDatabase
                .TRANSACTION_READ_UNCOMMITTED);
              this.srvDatabase.beginTransaction();
              List<I18nWebStore> i18ntr = this.srvOrm.retrieveList(pReqVars,
                I18nWebStore.class);
              List<I18nCatalogGs> i18nct = this.srvOrm.retrieveList(pReqVars,
                I18nCatalogGs.class);
              List<CurrRate> cRnct = this.srvOrm.retrieveList(pReqVars,
                CurrRate.class);
              this.srvDatabase.commitTransaction();
              //assigning fully initialized data:
              this.i18nWebStoreList = i18ntr;
              this.i18nCatalogs = i18nct;
              this.currRates = cRnct;
            } catch (Exception ex) {
              if (!this.srvDatabase.getIsAutocommit()) {
                this.srvDatabase.rollBackTransaction();
              }
              throw ex;
            } finally {
              this.srvDatabase.releaseResources();
            }
          }
          i18nTrTmp = this.i18nWebStoreList;
          i18nCtTmp = this.i18nCatalogs;
          curRatesTmp = this.currRates;
        }
        pReqVars.put("i18nCatalogs", i18nCtTmp);
        pReqVars.put("i18nWebStoreList", i18nTrTmp);
      }
    }
    if (curRatesTmp == null) {
      synchronized (this) {
        try {
          this.srvDatabase.setIsAutocommit(false);
          this.srvDatabase.setTransactionIsolation(ISrvDatabase
            .TRANSACTION_READ_UNCOMMITTED);
          this.srvDatabase.beginTransaction();
          List<CurrRate> cRnct = this.srvOrm.retrieveList(pReqVars,
            CurrRate.class);
          this.srvDatabase.commitTransaction();
          //assigning fully initialized data:
          this.currRates = cRnct;
        } catch (Exception ex) {
          if (!this.srvDatabase.getIsAutocommit()) {
            this.srvDatabase.rollBackTransaction();
          }
          throw ex;
        } finally {
          this.srvDatabase.releaseResources();
        }
        curRatesTmp = this.currRates;
      }
    }
    pReqVars.put("currRates", curRatesTmp);
    Currency wscurr = null;
    if (curRatesTmp.size() > 0) {
      String wscurrs = pRequestData.getParameter("wscurr");
      String  wscurrsc = pRequestData.getCookieValue("wscurr");
      if (wscurrs != null) {
        Long wscurrl = Long.parseLong(wscurrs);
        for (CurrRate cr : curRatesTmp) {
          if (cr.getCurr().getItsId().equals(wscurrl)) {
            wscurr = cr.getCurr();
            break;
          }
        }
        if (wscurr != null && wscurrsc != null && !wscurrs.equals(wscurrsc)) {
          pRequestData.setCookieValue("wscurr", wscurr.getItsId().toString());
        }
      }
      if (wscurr == null && wscurrsc != null) {
        Long wscurrl = Long.parseLong(wscurrsc);
        for (CurrRate cr : curRatesTmp) {
          if (cr.getCurr().getItsId().equals(wscurrl)) {
            wscurr = cr.getCurr();
            break;
          }
        }
      }
    }
    if (wscurr == null) {
      AccSettings as = (AccSettings) pReqVars.get("accSet");
      wscurr = as.getCurrency();
      pRequestData.setCookieValue("wscurr", wscurr.getItsId().toString());
    }
    pReqVars.put("wscurr", wscurr);
  }

  /**
   * <p>Handle data changed event.</p>
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized void handleDataChanged() throws Exception {
    this.i18nWebStoreList = null;
    this.i18nCatalogs = null;
    this.currRates = null;
    this.logger.info(null, HndlTradeVarsRequest.class,
      "I18N changes are handled.");
  }

  //Simple getters and setters:
  /**
   * <p>Geter for logger.</p>
   * @return ILogger
   **/
  public final synchronized ILogger getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final synchronized void setLogger(final ILogger pLogger) {
    this.logger = pLogger;
  }

  /**
   * <p>Getter for srvDatabase.</p>
   * @return ISrvDatabase<RS>
   **/
  public final synchronized ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final synchronized void setSrvDatabase(
    final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }

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
   * <p>Getter for srvTradingSettings.</p>
   * @return ISrvTradingSettings
   **/
  public final ISrvTradingSettings getSrvTradingSettings() {
    return this.srvTradingSettings;
  }

  /**
   * <p>Setter for srvTradingSettings.</p>
   * @param pSrvTradingSettings reference
   **/
  public final void setSrvTradingSettings(
    final ISrvTradingSettings pSrvTradingSettings) {
    this.srvTradingSettings = pSrvTradingSettings;
  }

  /**
   * <p>Getter for utlTradeJsp.</p>
   * @return UtlTradeJsp
   **/
  public final UtlTradeJsp getUtlTradeJsp() {
    return this.utlTradeJsp;
  }

  /**
   * <p>Setter for utlTradeJsp.</p>
   * @param pUtlTradeJsp reference
   **/
  public final void setUtlTradeJsp(final UtlTradeJsp pUtlTradeJsp) {
    this.utlTradeJsp = pUtlTradeJsp;
  }

  /**
   * <p>Getter for srvSettingsAdd.</p>
   * @return ISrvSettingsAdd
   **/
  public final ISrvSettingsAdd getSrvSettingsAdd() {
    return this.srvSettingsAdd;
  }

  /**
   * <p>Setter for srvSettingsAdd.</p>
   * @param pSrvSettingsAdd reference
   **/
  public final void setSrvSettingsAdd(final ISrvSettingsAdd pSrvSettingsAdd) {
    this.srvSettingsAdd = pSrvSettingsAdd;
  }
}
