package org.beigesoft.webstore.factory;

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

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import org.beigesoft.log.ILogger;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.settings.IMngSettings;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.persistable.Languages;
import org.beigesoft.persistable.Countries;
import org.beigesoft.persistable.DecimalSeparator;
import org.beigesoft.persistable.DecimalGroupSeparator;
import org.beigesoft.orm.factory.FctBnEntitiesProcessors;
import org.beigesoft.webstore.persistable.SeGoods;
import org.beigesoft.webstore.persistable.SeGoodsPlace;
import org.beigesoft.webstore.persistable.SeGoodsPrice;
import org.beigesoft.webstore.persistable.SeGoodsSpecifics;
import org.beigesoft.webstore.persistable.SeService;
import org.beigesoft.webstore.persistable.SeServicePlace;
import org.beigesoft.webstore.persistable.SeServicePrice;
import org.beigesoft.webstore.persistable.SeServiceSpecifics;
import org.beigesoft.webstore.persistable.I18nSeGoods;
import org.beigesoft.webstore.persistable.I18nSeService;

/**
 * <p>S.E.Seller's entities processors factory.
 * These are non-public processors.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class FctBnSeSelEntityProcs<RS>
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
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>Converters map "converter name"-"object' s converter".</p>
   **/
  private final Map<String, IEntityProcessor> processorsMap =
    new HashMap<String, IEntityProcessor>();

  /**
   * <p>Web-store entities.</p>
   **/
  private final Set<Class<?>> seEntities;

  /**
   * <p>Shared entities. Only <b>list</b> operation is allowed, no "modify".</p>
   **/
  private final Set<Class<?>> sharedEntities;

  /**
   * <p>Only constructor.</p>
   **/
  public FctBnSeSelEntityProcs() {
    this.sharedEntities = new HashSet<Class<?>>();
    this.sharedEntities.add(Languages.class);
    this.sharedEntities.add(Countries.class);
    this.sharedEntities.add(DecimalSeparator.class);
    this.sharedEntities.add(DecimalGroupSeparator.class);
    this.seEntities = new HashSet<Class<?>>();
    this.seEntities.add(SeGoods.class);
    this.seEntities.add(SeGoodsPlace.class);
    this.seEntities.add(SeGoodsPrice.class);
    this.seEntities.add(SeGoodsSpecifics.class);
    this.seEntities.add(SeService.class);
    this.seEntities.add(SeServicePlace.class);
    this.seEntities.add(SeServicePrice.class);
    this.seEntities.add(SeServiceSpecifics.class);
    this.seEntities.add(I18nSeGoods.class);
    this.seEntities.add(I18nSeService.class);
  }

  /**
   * <p>Get bean in lazy mode (if bean is null then initialize it).</p>
   * @param pAddParam additional param
   * @param pBeanName - bean name
   * @return requested bean
   * @throws Exception - an exception
   */
  @Override
  public final IEntityProcessor lazyGet(final Map<String, Object> pAddParam,
    final String pBeanName) throws Exception {
    IEntityProcessor proc = this.processorsMap.get(pBeanName);
    if (proc == null) {
      // locking:
      synchronized (this.processorsMap) {
        // make sure again whether it's null after locking:
        proc = this.processorsMap.get(pBeanName);
        if (proc == null) {
          /*if (pBeanName.equals(Prc.class.getSimpleName())) {
            proc = lazyGetPrc(pAddParam);
          } else {*/
          proc = this.fctBnEntitiesProcessors.lazyGet(pAddParam, pBeanName);
          //}
        }
      }
    }
    if (proc == null) {
      throw new ExceptionWithCode(ExceptionWithCode.CONFIGURATION_MISTAKE,
        "There is no processor with name " + pBeanName);
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
    final IEntityProcessor pBean) throws Exception {
    //nothing
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
   * <p>Getter for seEntities.</p>
   * @return final Set<Class<?>>
   **/
  public final Set<Class<?>> getSeEntities() {
    return this.seEntities;
  }


  /**
   * <p>Getter for sharedEntities.</p>
   * @return final Set<Class<?>>
   **/
  public final Set<Class<?>> getSharedEntities() {
    return this.sharedEntities;
  }

}
