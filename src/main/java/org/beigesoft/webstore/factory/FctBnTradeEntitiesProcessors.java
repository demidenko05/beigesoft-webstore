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

import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.settings.IMngSettings;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.orm.factory.FctBnEntitiesProcessors;
import org.beigesoft.orm.processor.PrcEntityRetrieve;
import org.beigesoft.webstore.service.ISrvSettingsAdd;
import org.beigesoft.webstore.service.ISrvTradingSettings;
import org.beigesoft.webstore.persistable.ServiceSpecifics;
import org.beigesoft.webstore.persistable.ServiceSpecificsId;
import org.beigesoft.webstore.persistable.GoodsSpecific;
import org.beigesoft.webstore.persistable.GoodsSpecificId;
import org.beigesoft.webstore.processor.PrcAdvisedGoodsForGoodsSave;
import org.beigesoft.webstore.processor.PrcGoodsCatalogsSave;
import org.beigesoft.webstore.processor.PrcServiceCatalogSave;
import org.beigesoft.webstore.processor.PrcSettingsAddSave;
import org.beigesoft.webstore.processor.PrcTradingSettingsSave;
import org.beigesoft.webstore.processor.PrcSubcatalogsCatalogsGsSave;
import org.beigesoft.webstore.processor.PrcGoodsAdviseCategoriesSave;
import org.beigesoft.webstore.processor.PrcServiceSpecificsSave;
import org.beigesoft.webstore.processor.PrcGoodsSpecificSave;
import org.beigesoft.webstore.processor.PrcServiceSpecificsRetrieve;
import org.beigesoft.webstore.processor.PrcGoodsSpecificRetrieve;
import org.beigesoft.webstore.processor.PrcServiceSpecificsDelete;
import org.beigesoft.webstore.processor.PrcGoodsSpecificDelete;

/**
 * <p>Webstore entities processors factory.
 * It is inner inside ACC-Processor factory.
 * These are non-public processors.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class FctBnTradeEntitiesProcessors<RS>
  implements IFactoryAppBeansByName<IEntityProcessor> {

  /**
   * <p>Factory non-acc entity processors.
   * Concrete factory for concrete bean name that is bean class
   * simple name. Any way any such factory must be no abstract.</p>
   **/
  private FctBnEntitiesProcessors<RS> fctBnEntitiesProcessors;

  /**
   * <p>Manager UVD settings.</p>
   **/
  private IMngSettings mngUvdSettings;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for additional settings.</p>
   **/
  private ISrvSettingsAdd srvSettingsAdd;

  /**
   * <p>Business service for trading settings.</p>
   **/
  private ISrvTradingSettings srvTradingSettings;

  /**
   * <p>Upload directory relative to WEB-APP path
   * without start and end separator, e.g. "static/uploads".</p>
   **/
  private String uploadDirectory;

  /**
   * <p>Full WEB-APP path without end separator,
   * revealed from servlet context and used for upload files.</p>
   **/
  private String webAppPath;

  /**
   * <p>Converters map "converter name"-"object' s converter".</p>
   **/
  private final Map<String, IEntityProcessor>
    processorsMap =
      new HashMap<String, IEntityProcessor>();

  /**
   * <p>Get bean in lazy mode (if bean is null then initialize it).</p>
   * @param pAddParam additional param
   * @param pBeanName - bean name
   * @return requested bean
   * @throws Exception - an exception
   */
  @Override
  public final IEntityProcessor lazyGet(
    final Map<String, Object> pAddParam,
      final String pBeanName) throws Exception {
    IEntityProcessor proc =
      this.processorsMap.get(pBeanName);
    if (proc == null) {
      // locking:
      synchronized (this.processorsMap) {
        // make sure again whether it's null after locking:
        proc = this.processorsMap.get(pBeanName);
        if (proc == null) {
          if (pBeanName
            .equals(PrcAdvisedGoodsForGoodsSave.class.getSimpleName())) {
            proc = lazyGetPrcAdvisedGoodsForGoodsSave(pAddParam);
          } else if (pBeanName
            .equals(PrcSettingsAddSave.class.getSimpleName())) {
            proc = lazyGetPrcSettingsAddSave(pAddParam);
          } else if (pBeanName
            .equals(PrcTradingSettingsSave.class.getSimpleName())) {
            proc = lazyGetPrcTradingSettingsSave(pAddParam);
          } else if (pBeanName
            .equals(PrcServiceCatalogSave.class.getSimpleName())) {
            proc = lazyGetPrcServiceCatalogSave(pAddParam);
          } else if (pBeanName
            .equals(PrcGoodsCatalogsSave.class.getSimpleName())) {
            proc = lazyGetPrcGoodsCatalogsSave(pAddParam);
          } else if (pBeanName
            .equals(PrcSubcatalogsCatalogsGsSave.class.getSimpleName())) {
            proc = lazyGetPrcSubcatalogsCatalogsGsSave(pAddParam);
          } else if (pBeanName
            .equals(PrcGoodsAdviseCategoriesSave.class.getSimpleName())) {
            proc = lazyGetPrcGoodsAdviseCategoriesSave(pAddParam);
          } else if (pBeanName
            .equals(PrcServiceSpecificsDelete.class.getSimpleName())) {
            proc = lazyGetPrcServiceSpecificsDelete(pAddParam);
          } else if (pBeanName
            .equals(PrcGoodsSpecificDelete.class.getSimpleName())) {
            proc = lazyGetPrcGoodsSpecificDelete(pAddParam);
          } else if (pBeanName
            .equals(PrcServiceSpecificsRetrieve.class.getSimpleName())) {
            proc = lazyGetPrcServiceSpecificsRetrieve(pAddParam);
          } else if (pBeanName
            .equals(PrcGoodsSpecificRetrieve.class.getSimpleName())) {
            proc = lazyGetPrcGoodsSpecificRetrieve(pAddParam);
          } else if (pBeanName
            .equals(PrcServiceSpecificsSave.class.getSimpleName())) {
            proc = lazyGetPrcServiceSpecificsSave(pAddParam);
          } else if (pBeanName
            .equals(PrcGoodsSpecificSave.class.getSimpleName())) {
            proc = lazyGetPrcGoodsSpecificSave(pAddParam);
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
    final IEntityProcessor pBean) throws Exception {
    this.processorsMap.put(pBeanName, pBean);
  }

  /**
   * <p>Get PrcAdvisedGoodsForGoodsSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAdvisedGoodsForGoodsSave
   * @throws Exception - an exception
   */
  protected final PrcAdvisedGoodsForGoodsSave<RS>
    lazyGetPrcAdvisedGoodsForGoodsSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAdvisedGoodsForGoodsSave<RS> proc =
      (PrcAdvisedGoodsForGoodsSave<RS>)
      this.processorsMap
        .get(PrcAdvisedGoodsForGoodsSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAdvisedGoodsForGoodsSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcAdvisedGoodsForGoodsSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcSettingsAddSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSettingsAddSave
   * @throws Exception - an exception
   */
  protected final PrcSettingsAddSave<RS>
    lazyGetPrcSettingsAddSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcSettingsAddSave<RS> proc =
      (PrcSettingsAddSave<RS>)
      this.processorsMap
        .get(PrcSettingsAddSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcSettingsAddSave<RS>();
      proc.setSrvSettingsAdd(getSrvSettingsAdd());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcSettingsAddSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcTradingSettingsSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcTradingSettingsSave
   * @throws Exception - an exception
   */
  protected final PrcTradingSettingsSave<RS>
    lazyGetPrcTradingSettingsSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcTradingSettingsSave<RS> proc =
      (PrcTradingSettingsSave<RS>)
      this.processorsMap
        .get(PrcTradingSettingsSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcTradingSettingsSave<RS>();
      proc.setSrvTradingSettings(getSrvTradingSettings());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcTradingSettingsSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcServiceCatalogSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcServiceCatalogSave
   * @throws Exception - an exception
   */
  protected final PrcServiceCatalogSave<RS>
    lazyGetPrcServiceCatalogSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcServiceCatalogSave<RS> proc =
      (PrcServiceCatalogSave<RS>)
      this.processorsMap
        .get(PrcServiceCatalogSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcServiceCatalogSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcServiceCatalogSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcGoodsCatalogsSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcGoodsCatalogsSave
   * @throws Exception - an exception
   */
  protected final PrcGoodsCatalogsSave<RS>
    lazyGetPrcGoodsCatalogsSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcGoodsCatalogsSave<RS> proc =
      (PrcGoodsCatalogsSave<RS>)
      this.processorsMap
        .get(PrcGoodsCatalogsSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcGoodsCatalogsSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcGoodsCatalogsSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcSubcatalogsCatalogsGsSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSubcatalogsCatalogsGsSave
   * @throws Exception - an exception
   */
  protected final PrcSubcatalogsCatalogsGsSave<RS>
    lazyGetPrcSubcatalogsCatalogsGsSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcSubcatalogsCatalogsGsSave<RS> proc =
      (PrcSubcatalogsCatalogsGsSave<RS>)
      this.processorsMap
        .get(PrcSubcatalogsCatalogsGsSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcSubcatalogsCatalogsGsSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcSubcatalogsCatalogsGsSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcGoodsAdviseCategoriesSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcGoodsAdviseCategoriesSave
   * @throws Exception - an exception
   */
  protected final PrcGoodsAdviseCategoriesSave<RS>
    lazyGetPrcGoodsAdviseCategoriesSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcGoodsAdviseCategoriesSave<RS> proc =
      (PrcGoodsAdviseCategoriesSave<RS>)
      this.processorsMap
        .get(PrcGoodsAdviseCategoriesSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcGoodsAdviseCategoriesSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcGoodsAdviseCategoriesSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcServiceSpecificsDelete (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcServiceSpecificsDelete
   * @throws Exception - an exception
   */
  protected final PrcServiceSpecificsDelete<RS>
    lazyGetPrcServiceSpecificsDelete(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcServiceSpecificsDelete<RS> proc =
      (PrcServiceSpecificsDelete<RS>)
      this.processorsMap
        .get(PrcServiceSpecificsDelete.class.getSimpleName());
    if (proc == null) {
      proc = new PrcServiceSpecificsDelete<RS>();
      proc.setSrvOrm(getSrvOrm());
       //assigning fully initialized object:
      this.processorsMap
        .put(PrcServiceSpecificsDelete.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcGoodsSpecificDelete (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcGoodsSpecificDelete
   * @throws Exception - an exception
   */
  protected final PrcGoodsSpecificDelete<RS>
    lazyGetPrcGoodsSpecificDelete(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcGoodsSpecificDelete<RS> proc =
      (PrcGoodsSpecificDelete<RS>)
      this.processorsMap
        .get(PrcGoodsSpecificDelete.class.getSimpleName());
    if (proc == null) {
      proc = new PrcGoodsSpecificDelete<RS>();
      proc.setSrvOrm(getSrvOrm());
       //assigning fully initialized object:
      this.processorsMap
        .put(PrcGoodsSpecificDelete.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcServiceSpecificsRetrieve (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcServiceSpecificsRetrieve
   * @throws Exception - an exception
   */
  protected final PrcServiceSpecificsRetrieve<RS>
    lazyGetPrcServiceSpecificsRetrieve(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcServiceSpecificsRetrieve<RS> proc =
      (PrcServiceSpecificsRetrieve<RS>)
      this.processorsMap
        .get(PrcServiceSpecificsRetrieve.class.getSimpleName());
    if (proc == null) {
      proc = new PrcServiceSpecificsRetrieve<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityRetrieve<RS, ServiceSpecifics, ServiceSpecificsId> procDlg =
        (PrcEntityRetrieve<RS, ServiceSpecifics, ServiceSpecificsId>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityRetrieve.class.getSimpleName());
      proc.setPrcEntityRetrieve(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcServiceSpecificsRetrieve.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcGoodsSpecificRetrieve (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcGoodsSpecificRetrieve
   * @throws Exception - an exception
   */
  protected final PrcGoodsSpecificRetrieve<RS>
    lazyGetPrcGoodsSpecificRetrieve(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcGoodsSpecificRetrieve<RS> proc =
      (PrcGoodsSpecificRetrieve<RS>)
      this.processorsMap
        .get(PrcGoodsSpecificRetrieve.class.getSimpleName());
    if (proc == null) {
      proc = new PrcGoodsSpecificRetrieve<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityRetrieve<RS, GoodsSpecific, GoodsSpecificId> procDlg =
        (PrcEntityRetrieve<RS, GoodsSpecific, GoodsSpecificId>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityRetrieve.class.getSimpleName());
      proc.setPrcEntityRetrieve(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcGoodsSpecificRetrieve.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcServiceSpecificsSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcServiceSpecificsSave
   * @throws Exception - an exception
   */
  protected final PrcServiceSpecificsSave<RS>
    lazyGetPrcServiceSpecificsSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcServiceSpecificsSave<RS> proc =
      (PrcServiceSpecificsSave<RS>)
      this.processorsMap
        .get(PrcServiceSpecificsSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcServiceSpecificsSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setUploadDirectory(getUploadDirectory());
      proc.setWebAppPath(getWebAppPath());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcServiceSpecificsSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcGoodsSpecificSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcGoodsSpecificSave
   * @throws Exception - an exception
   */
  protected final PrcGoodsSpecificSave<RS>
    lazyGetPrcGoodsSpecificSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcGoodsSpecificSave<RS> proc =
      (PrcGoodsSpecificSave<RS>)
      this.processorsMap
        .get(PrcGoodsSpecificSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcGoodsSpecificSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setUploadDirectory(getUploadDirectory());
      proc.setWebAppPath(getWebAppPath());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcGoodsSpecificSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  //Simple getters and setters:

  /**
   * <p>Getter for fctBnEntitiesProcessors.</p>
   * @return FctBnEntitiesProcessors<RS>
   **/
  public final FctBnEntitiesProcessors<RS> getFctBnEntitiesProcessors() {
    return this.fctBnEntitiesProcessors;
  }

  /**
   * <p>Setter for fctBnEntitiesProcessors.</p>
   * @param pFctBnEntitiesProcessors reference
   **/
  public final void setFctBnEntitiesProcessors(
    final FctBnEntitiesProcessors<RS> pFctBnEntitiesProcessors) {
    this.fctBnEntitiesProcessors = pFctBnEntitiesProcessors;
  }

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
   * <p>Getter for mngUvdSettings.</p>
   * @return IMngSettings
   **/
  public final IMngSettings getMngUvdSettings() {
    return this.mngUvdSettings;
  }

  /**
   * <p>Setter for mngUvdSettings.</p>
   * @param pMngUvdSettings reference
   **/
  public final void setMngUvdSettings(final IMngSettings pMngUvdSettings) {
    this.mngUvdSettings = pMngUvdSettings;
  }

  /**
   * <p>Getter for uploadDirectory.</p>
   * @return String
   **/
  public final String getUploadDirectory() {
    return this.uploadDirectory;
  }

  /**
   * <p>Setter for uploadDirectory.</p>
   * @param pUploadDirectory reference
   **/
  public final void setUploadDirectory(final String pUploadDirectory) {
    this.uploadDirectory = pUploadDirectory;
  }

  /**
   * <p>Getter for webAppPath.</p>
   * @return String
   **/
  public final String getWebAppPath() {
    return this.webAppPath;
  }

  /**
   * <p>Setter for webAppPath.</p>
   * @param pWebAppPath reference
   **/
  public final void setWebAppPath(final String pWebAppPath) {
    this.webAppPath = pWebAppPath;
  }
}
