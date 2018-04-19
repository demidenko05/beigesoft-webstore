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

import org.beigesoft.log.ILogger;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvEntitiesPage;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.webstore.service.ISrvSettingsAdd;
import org.beigesoft.webstore.processor.PrcAssignGoodsToCatalog;
import org.beigesoft.webstore.processor.PrcRefreshGoodsInList;
import org.beigesoft.webstore.service.ISrvTradingSettings;

/**
 * <p>Non-public trade processors factory.
 * It is inner inside ACC-PF.</p>
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
  private ISrvEntitiesPage srvEntitiesPage;

  /**
   * <p>Business service for additional settings.</p>
   **/
  private ISrvSettingsAdd srvSettingsAdd;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Business service for trading settings.</p>
   **/
  private ISrvTradingSettings srvTradingSettings;

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>Converters map "converter name"-"object' s converter".</p>
   **/
  private final Map<String, IProcessor>
    processorsMap =
      new HashMap<String, IProcessor>();

  /**
   * <p>Get bean in lazy mode (if bean is null then initialize it).</p>
   * @param pAddParam additional param
   * @param pBeanName - bean name
   * @return requested bean
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
          if (pBeanName.equals(PrcRefreshGoodsInList
            .class.getSimpleName())) {
            proc = lazyGetPrcRefreshGoodsInList(pAddParam);
          } else if (pBeanName.equals(PrcAssignGoodsToCatalog
            .class.getSimpleName())) {
            proc = lazyGetPrcAssignGoodsToCatalog(pAddParam);
          }
        }
      }
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
  public final synchronized void set(final String pBeanName,
    final IProcessor pBean) throws Exception {
    this.processorsMap.put(pBeanName, pBean);
  }

  /**
   * <p>Lazy get PrcRefreshGoodsInList.</p>
   * @param pAddParam additional param
   * @return requested PrcRefreshGoodsInList
   * @throws Exception - an exception
   */
  protected final PrcRefreshGoodsInList<RS>
    lazyGetPrcRefreshGoodsInList(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcRefreshGoodsInList<RS> proc = (PrcRefreshGoodsInList<RS>)
      this.processorsMap
        .get(PrcRefreshGoodsInList.class.getSimpleName());
    if (proc == null) {
      proc = new PrcRefreshGoodsInList<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvDatabase(getSrvDatabase());
      proc.setSrvTradingSettings(getSrvTradingSettings());
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvSettingsAdd(getSrvSettingsAdd());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcRefreshGoodsInList.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcAssignGoodsToCatalog.</p>
   * @param pAddParam additional param
   * @return requested PrcAssignGoodsToCatalog
   * @throws Exception - an exception
   */
  protected final PrcAssignGoodsToCatalog<RS>
    lazyGetPrcAssignGoodsToCatalog(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAssignGoodsToCatalog<RS> proc = (PrcAssignGoodsToCatalog<RS>)
      this.processorsMap
        .get(PrcAssignGoodsToCatalog.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAssignGoodsToCatalog<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvEntitiesPage(getSrvEntitiesPage());
      proc.setSrvTradingSettings(getSrvTradingSettings());
      proc.setSrvAccSettings(getSrvAccSettings());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcAssignGoodsToCatalog.class.getSimpleName(), proc);
    }
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
   * @return ISrvEntitiesPage
   **/
  public final ISrvEntitiesPage getSrvEntitiesPage() {
    return this.srvEntitiesPage;
  }

  /**
   * <p>Setter for srvEntitiesPage.</p>
   * @param pSrvEntitiesPage reference
   **/
  public final void setSrvEntitiesPage(
    final ISrvEntitiesPage pSrvEntitiesPage) {
    this.srvEntitiesPage = pSrvEntitiesPage;
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
}
