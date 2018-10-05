package org.beigesoft.webstore.factory;

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

import java.util.Map;
import java.util.HashMap;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.log.ILogger;
import org.beigesoft.handler.IHandlerRequestDch;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.service.ICsvDataRetriever;
import org.beigesoft.processor.PrcCsvSampleDataRow;
import org.beigesoft.orm.service.SrvEntitiesPage;
import org.beigesoft.orm.processor.PrcEntitiesPage;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.webstore.service.GoodsPriceListRetriever;
import org.beigesoft.webstore.service.ServicePriceListRetriever;
import org.beigesoft.webstore.processor.PrcAssignItemsToCatalog;
import org.beigesoft.webstore.processor.PrcRefreshItemsInList;
import org.beigesoft.webstore.processor.PrcRefreshCatalog;

/**
 * <p>Non-public trade processors factory.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class FctBnTradeProcessors<RS>
  implements IFactoryAppBeansByName<IProcessor> {

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Page service.</p>
   **/
  private SrvEntitiesPage<RS> srvEntitiesPage;

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>FctBnPublicTradeProcessors.</p>
   **/
  private FctBnPublicTradeProcessors<RS> fctBnPublicTradeProcessors;

  /**
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>Retrievers (web-store) map.</p>
   **/
  private Map<String, ICsvDataRetriever> retrievers;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Converters map "converter name"-"object' s converter".</p>
   **/
  private final Map<String, IProcessor> processorsMap =
    new HashMap<String, IProcessor>();

  /**
   * <p>Get bean in lazy mode (if bean is null then initialize it).</p>
   * @param pAddParam additional param
   * @param pBeanName - bean name
   * @return requested bean or exception if not found
   * @throws Exception - an exception
   */
  @Override
  public final IProcessor lazyGet(//NOPMD
    final Map<String, Object> pAddParam,
      final String pBeanName) throws Exception {
    IProcessor proc =
      this.processorsMap.get(pBeanName);
    if (proc == null) {
      // locking:
      synchronized (this.processorsMap) {
        // make sure again whether it's null after locking:
        proc = this.processorsMap.get(pBeanName);
        if (proc == null) {
          if (pBeanName.equals(PrcRefreshItemsInList
            .class.getSimpleName())) {
            proc = lazyGetPrcRefreshItemsInList(pAddParam);
          } else if (pBeanName.equals(PrcRefreshCatalog
            .class.getSimpleName())) {
            proc = lazyGetPrcRefreshCatalog(pAddParam);
          } else if (pBeanName.equals(PrcCsvSampleDataRow
            .class.getSimpleName())) { //only web-store
            proc = lazyGetPrcCsvSampleDataRow(pAddParam);
          } else if (pBeanName.equals(PrcAssignItemsToCatalog
            .class.getSimpleName())) {
            proc = lazyGetPrcAssignItemsToCatalog(pAddParam);
          } else if (pBeanName.equals("waPrcEntitiesPage")) {
            proc = createPutPrcWaEntitiesPage();
          }
        }
      }
    }
    if (proc == null) {
      throw new ExceptionWithCode(ExceptionWithCode.CONFIGURATION_MISTAKE,
        pBeanName + " not found!");
    }
    return proc;
  }

  /**
   * <p>Set bean.</p>
   * @param pBeanName - bean name
   * @param pBean bean
   * @throws Exception - an exception
   */
  @Override
  public final void set(final String pBeanName,
    final IProcessor pBean) throws Exception {
    //nothing
  }

  /**
   * <p>Lazy get PrcRefreshCatalog.</p>
   * @param pAddParam additional param
   * @return requested PrcRefreshCatalog
   * @throws Exception - an exception
   */
  protected final PrcRefreshCatalog
    lazyGetPrcRefreshCatalog(
      final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcRefreshCatalog.class.getSimpleName();
    PrcRefreshCatalog proc = (PrcRefreshCatalog) this.processorsMap
      .get(beanName);
    if (proc == null) {
      proc = new PrcRefreshCatalog();
      proc.getListeners().add(this.fctBnPublicTradeProcessors
        .lazyGetPrcWebstorePage(pAddParam));
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeProcessors.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcRefreshItemsInList.</p>
   * @param pAddParam additional param
   * @return requested PrcRefreshItemsInList
   * @throws Exception - an exception
   */
  protected final PrcRefreshItemsInList<RS>
    lazyGetPrcRefreshItemsInList(
      final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcRefreshItemsInList.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcRefreshItemsInList<RS> proc = (PrcRefreshItemsInList<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcRefreshItemsInList<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvDatabase(getSrvDatabase());
      proc.setSrvNumberToString(getSrvNumberToString());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeProcessors.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Lazy initialize retrievers.</p>
   * @param pAddParam additional param
   * @throws Exception - an exception
   */
  protected final void lazyInitRetrievers(
      final Map<String, Object> pAddParam) throws Exception {
    if (this.retrievers == null) {
      this.retrievers = new HashMap<String, ICsvDataRetriever>();
      GoodsPriceListRetriever<RS> gpr = new GoodsPriceListRetriever<RS>();
      gpr.setSrvI18n(getSrvI18n());
      gpr.setSrvOrm(getSrvOrm());
      gpr.setSrvDatabase(getSrvDatabase());
      gpr.setSrvAccSettings(getSrvAccSettings());
      this.retrievers.put("GoodsPriceListRetriever", gpr);
      ServicePriceListRetriever<RS> spr = new ServicePriceListRetriever<RS>();
      spr.setSrvI18n(getSrvI18n());
      spr.setSrvOrm(getSrvOrm());
      spr.setSrvAccSettings(getSrvAccSettings());
      this.retrievers.put("ServicePriceListRetriever", spr);
    }
  }

  /**
   * <p>Lazy get PrcCsvSampleDataRow.</p>
   * @param pAddParam additional param
   * @return requested PrcCsvSampleDataRow
   * @throws Exception - an exception
   */
  protected final PrcCsvSampleDataRow
    lazyGetPrcCsvSampleDataRow(
      final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcCsvSampleDataRow.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcCsvSampleDataRow proc = (PrcCsvSampleDataRow)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcCsvSampleDataRow();
      lazyInitRetrievers(pAddParam);
      proc.setRetrievers(this.retrievers);
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeProcessors.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcAssignItemsToCatalog.</p>
   * @param pAddParam additional param
   * @return requested PrcAssignItemsToCatalog
   * @throws Exception - an exception
   */
  protected final PrcAssignItemsToCatalog<RS>
    lazyGetPrcAssignItemsToCatalog(
      final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcAssignItemsToCatalog.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcAssignItemsToCatalog<RS> proc = (PrcAssignItemsToCatalog<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcAssignItemsToCatalog<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvEntitiesPage(getSrvEntitiesPage());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeProcessors.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcEntitiesPage (create and put into map).</p>
   * @return requested PrcEntitiesPage
   * @throws Exception - an exception
   */
  protected final PrcEntitiesPage
    createPutPrcWaEntitiesPage() throws Exception {
    PrcEntitiesPage proc = new PrcEntitiesPage();
    proc.setSrvEntitiesPage(getSrvEntitiesPage());
    //assigning fully initialized object:
    this.processorsMap
      .put(PrcEntitiesPage.class.getSimpleName(), proc);
    return proc;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvDatabase.</p>
   * @return ISrvDatabase<RS>
   **/
  public final ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final void setSrvDatabase(final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }

  /**
   * <p>Getter for srvOrm.</p>
   * @return ASrvOrm<RS>
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
   * <p>Getter for srvEntitiesPage.</p>
   * @return SrvEntitiesPage<RS>
   **/
  public final SrvEntitiesPage<RS> getSrvEntitiesPage() {
    return this.srvEntitiesPage;
  }

  /**
   * <p>Setter for srvEntitiesPage.</p>
   * @param pSrvEntitiesPage reference
   **/
  public final void setSrvEntitiesPage(
    final SrvEntitiesPage<RS> pSrvEntitiesPage) {
    this.srvEntitiesPage = pSrvEntitiesPage;
  }

  /**
   * <p>Geter for logger.</p>
   * @return ILogger
   **/
  public final ILogger getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final void setLogger(final ILogger pLogger) {
    this.logger = pLogger;
  }

  /**
   * <p>Getter for fctBnPublicTradeProcessors.</p>
   * @return FctBnPublicTradeProcessors
   **/
  public final FctBnPublicTradeProcessors<RS> getFctBnPublicTradeProcessors() {
    return this.fctBnPublicTradeProcessors;
  }

  /**
   * <p>Setter for fctBnPublicTradeProcessors.</p>
   * @param pFctBnPublicTradeProcessors reference
   **/
  public final void setFctBnPublicTradeProcessors(
    final FctBnPublicTradeProcessors<RS> pFctBnPublicTradeProcessors) {
    this.fctBnPublicTradeProcessors = pFctBnPublicTradeProcessors;
  }

  /**
   * <p>Getter for srvNumberToString.</p>
   * @return ISrvNumberToString
   **/
  public final ISrvNumberToString getSrvNumberToString() {
    return this.srvNumberToString;
  }

  /**
   * <p>Setter for srvNumberToString.</p>
   * @param pSrvNumberToString reference
   **/
  public final void setSrvNumberToString(
    final ISrvNumberToString pSrvNumberToString) {
    this.srvNumberToString = pSrvNumberToString;
  }

  /**
   * <p>Getter for retrievers.</p>
   * @return Map<String, ICsvDataRetriever>
   **/
  public final Map<String, ICsvDataRetriever> getRetrievers() {
    return this.retrievers;
  }

  /**
   * <p>Setter for retrievers.</p>
   * @param pRetrievers reference
   **/
  public final void setRetrievers(
    final Map<String, ICsvDataRetriever> pRetrievers) {
    this.retrievers = pRetrievers;
  }

  /**
   * <p>Getter for srvI18n.</p>
   * @return ISrvI18n
   **/
  public final ISrvI18n getSrvI18n() {
    return this.srvI18n;
  }

  /**
   * <p>Setter for srvI18n.</p>
   * @param pSrvI18n reference
   **/
  public final void setSrvI18n(final ISrvI18n pSrvI18n) {
    this.srvI18n = pSrvI18n;
  }

  /**
   * <p>Getter for srvAccSettings.</p>
   * @return ISrvAccSettings
   **/
  public final ISrvAccSettings getSrvAccSettings() {
    return this.srvAccSettings;
  }

  /**
   * <p>Setter for srvAccSettings.</p>
   * @param pSrvAccSettings reference
   **/
  public final void setSrvAccSettings(final ISrvAccSettings pSrvAccSettings) {
    this.srvAccSettings = pSrvAccSettings;
  }
}
