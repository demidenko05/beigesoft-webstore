package org.beigesoft.webstore.handler;

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

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.model.IHasId;
import org.beigesoft.log.ILogger;
import org.beigesoft.service.IFillerObjectsFrom;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.factory.IFactorySimple;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.factory.IFactoryAppBeansByClass;
import org.beigesoft.holder.IHolderForClassByName;
import org.beigesoft.handler.IHandlerRequest;

/**
 * <p>Handler for any S.E.seller requests - just I18N or
 * entity operation - "create, update, delete, list".
 * </p>
 *
 * @param <RS> platform dependent RDBMS recordset
 * @author Yury Demidenko
 */
public class HndlSeSellerReq<RS> implements IHandlerRequest {

  /**
   * <p>I18N Request Handler.</p>
   **/
  private IHandlerRequest i18nRequestHandler;

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>Security logger.</p>
   **/
  private ILogger secureLogger;

  /**
   * <p>Database service.</p>
   */
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>Service that fill entity from request.</p>
   **/
  private IFillerObjectsFrom<IRequestData> fillEntityFromReq;

  /**
   * <p>Entities factories factory.</p>
   **/
  private IFactoryAppBeansByClass<IFactorySimple<?>> entitiesFactoriesFatory;

  /**
   * <p>Entities processors factory.</p>
   **/
  private IFactoryAppBeansByName<IEntityProcessor> entitiesProcessorsFactory;

  /**
   * <p>Entities processors names holder.</p>
   **/
  private IHolderForClassByName<String> entitiesProcessorsNamesHolder;

  /**
   * <p>Processors factory.</p>
   **/
  private IFactoryAppBeansByName<IProcessor> processorsFactory;

  /**
   * <p>Processors names holder.</p>
   **/
  private IHolderForClassByName<String> processorsNamesHolder;

  /**
   * <p>Entities map "EntitySimpleName"-"Class".</p>
   **/
  private Map<String, Class<?>> entitiesMap;

  /**
   * <p>Handle request.</p>
   * @param pReqVars Request scoped variables
   * @param pRequestData Request Data
   * @throws Exception - an exception
   */
  @Override
  public final void handle(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    this.i18nRequestHandler.handle(pReqVars, pRequestData);
    String nmEnt = pRequestData.getParameter("nmEnt");
    if (nmEnt != null) {
      int detLev = this.logger.getDetailLevel();
      boolean isShowDebMsg = this.logger.getIsShowDebugMessages()
        && this.logger.getIsShowDebugMessagesFor(this.getClass());
      try {
        String[] actionsArr = pRequestData
          .getParameter("nmsAct").split(",");
        Class entityClass = this.entitiesMap.get(nmEnt);
        if (entityClass == null) {
          this.secureLogger.error(null, HndlSeSellerReq.class,
            "Trying to work with forbidden entity/seseller: " + nmEnt + "/"
              + pRequestData.getUserName());
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "Forbidden!");
        }
        this.srvDatabase.setIsAutocommit(false);
        this.srvDatabase.
          setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
        this.srvDatabase.beginTransaction();
        IHasId<?> entity = null;
        if (actionsArr[0].startsWith("entity")) {
          // actions like "save", "delete"
          @SuppressWarnings("unchecked")
          IFactorySimple<IHasId<?>> entFac = (IFactorySimple<IHasId<?>>)
            this.entitiesFactoriesFatory.lazyGet(pReqVars, entityClass);
          entity = entFac.create(pReqVars);
          this.fillEntityFromReq.fill(pReqVars, entity, pRequestData);
        }
        for (String actionNm : actionsArr) {
          if (isShowDebMsg && detLev > 100) {
            this.logger.debug(pReqVars, HndlSeSellerReq.class,
              "Action: " + actionNm);
          }
          if (actionNm.startsWith("entity")) {
            if (entity == null) { // it's may be change entity to owner:
             entity = (IHasId<?>) pReqVars.get("nextEntity");
             if (entity == null) {
                throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
                  "wrong_request_entity_not_filled");
              }
             entityClass = entity.getClass();
            }
            String entProcNm = this.entitiesProcessorsNamesHolder
              .getFor(entityClass, actionNm);
            @SuppressWarnings("unchecked")
            IEntityProcessor<IHasId<?>, ?> ep =
              (IEntityProcessor<IHasId<?>, ?>)
              this.entitiesProcessorsFactory.lazyGet(pReqVars, entProcNm);
            if (isShowDebMsg && detLev > 100) {
              this.logger.debug(pReqVars, HndlSeSellerReq.class,
                "It's used entProcNm/IEntityProcessor: " + entProcNm + "/"
                  + ep.getClass());
            }
            entity = ep.process(pReqVars, entity, pRequestData);
          } else { // else actions like "list" (page)
            String procNm = this.processorsNamesHolder
              .getFor(entityClass, actionNm);
            IProcessor proc = this.processorsFactory
              .lazyGet(pReqVars, procNm);
            if (isShowDebMsg && detLev > 100) {
              this.logger.debug(pReqVars, HndlSeSellerReq.class,
                "It's used procNm/IProcessor: " + procNm + "/"
                  + proc.getClass());
            }
            proc.process(pReqVars, pRequestData);
          }
        }
        this.srvDatabase.commitTransaction();
      } catch (Exception ex) {
        if (!this.srvDatabase.getIsAutocommit()) {
          this.srvDatabase.rollBackTransaction();
        }
        throw ex;
      } finally {
        this.srvDatabase.releaseResources();
      }
    }
  }

  //Simple getters and setters:
  /**
   * <p>Getter for i18nRequestHandler.</p>
   * @return IHandlerRequest
   **/
  public final IHandlerRequest getI18nRequestHandler() {
    return this.i18nRequestHandler;
  }

  /**
   * <p>Setter for i18nRequestHandler.</p>
   * @param pI18nRequestHandler reference
   **/
  public final void setI18nRequestHandler(
    final IHandlerRequest pI18nRequestHandler) {
    this.i18nRequestHandler = pI18nRequestHandler;
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
   * <p>Getter for fillEntityFromReq.</p>
   * @return IFillerObjectsFrom<IRequestData>
   **/
  public final IFillerObjectsFrom<IRequestData> getFillEntityFromReq() {
    return this.fillEntityFromReq;
  }

  /**
   * <p>Setter for fillEntityFromReq.</p>
   * @param pFillEntityFromReq reference
   **/
  public final void setFillEntityFromReq(
    final IFillerObjectsFrom<IRequestData> pFillEntityFromReq) {
    this.fillEntityFromReq = pFillEntityFromReq;
  }

  /**
   * <p>Getter for entitiesFactoriesFatory.</p>
   * @return IFactoryAppBeansByClass<IFactorySimple<?>>
   **/
  public final IFactoryAppBeansByClass<IFactorySimple<?>>
    getEntitiesFactoriesFatory() {
    return this.entitiesFactoriesFatory;
  }

  /**
   * <p>Setter for entitiesFactoriesFatory.</p>
   * @param pEntitiesFactoriesFatory reference
   **/
  public final void setEntitiesFactoriesFatory(
    final IFactoryAppBeansByClass<IFactorySimple<?>> pEntitiesFactoriesFatory) {
    this.entitiesFactoriesFatory = pEntitiesFactoriesFatory;
  }

  /**
   * <p>Getter for entitiesProcessorsFactory.</p>
   * @return IFactoryAppBeansByName<IEntityProcessor>
   **/
  public final IFactoryAppBeansByName<IEntityProcessor>
    getEntitiesProcessorsFactory() {
    return this.entitiesProcessorsFactory;
  }

  /**
   * <p>Setter for entitiesProcessorsFactory.</p>
   * @param pEntitiesProcessorsFactory reference
   **/
  public final void setEntitiesProcessorsFactory(
    final IFactoryAppBeansByName<IEntityProcessor>
      pEntitiesProcessorsFactory) {
    this.entitiesProcessorsFactory = pEntitiesProcessorsFactory;
  }

  /**
   * <p>Getter for entitiesProcessorsNamesHolder.</p>
   * @return IHolderForClassByName<String>
   **/
  public final IHolderForClassByName<String>
    getEntitiesProcessorsNamesHolder() {
    return this.entitiesProcessorsNamesHolder;
  }

  /**
   * <p>Setter for entitiesProcessorsNamesHolder.</p>
   * @param pEntitiesProcessorsNamesHolder reference
   **/
  public final void setEntitiesProcessorsNamesHolder(
    final IHolderForClassByName<String> pEntitiesProcessorsNamesHolder) {
    this.entitiesProcessorsNamesHolder = pEntitiesProcessorsNamesHolder;
  }

  /**
   * <p>Getter for processorsFactory.</p>
   * @return IFactoryAppBeansByName<IProcessor>
   **/
  public final IFactoryAppBeansByName<IProcessor> getProcessorsFactory() {
    return this.processorsFactory;
  }

  /**
   * <p>Setter for processorsFactory.</p>
   * @param pProcessorsFactory reference
   **/
  public final void setProcessorsFactory(
    final IFactoryAppBeansByName<IProcessor> pProcessorsFactory) {
    this.processorsFactory = pProcessorsFactory;
  }

  /**
   * <p>Getter for processorsNamesHolder.</p>
   * @return IHolderForClassByName<String>
   **/
  public final IHolderForClassByName<String> getProcessorsNamesHolder() {
    return this.processorsNamesHolder;
  }

  /**
   * <p>Setter for processorsNamesHolder.</p>
   * @param pProcessorsNamesHolder reference
   **/
  public final void setProcessorsNamesHolder(
    final IHolderForClassByName<String> pProcessorsNamesHolder) {
    this.processorsNamesHolder = pProcessorsNamesHolder;
  }

  /**
   * <p>Getter for entitiesMap.</p>
   * @return Map<String, Class<?>>
   **/
  public final Map<String, Class<?>> getEntitiesMap() {
    return this.entitiesMap;
  }

  /**
   * <p>Setter for entitiesMap.</p>
   * @param pEntitiesMap reference
   **/
  public final void setEntitiesMap(final Map<String, Class<?>> pEntitiesMap) {
    this.entitiesMap = pEntitiesMap;
  }

  /**
   * <p>Getter for secureLogger.</p>
   * @return ILogger
   **/
  public final ILogger getSecureLogger() {
    return this.secureLogger;
  }

  /**
   * <p>Setter for secureLogger.</p>
   * @param pSecureLogger reference
   **/
  public final void setSecureLogger(final ILogger pSecureLogger) {
    this.secureLogger = pSecureLogger;
  }
}
