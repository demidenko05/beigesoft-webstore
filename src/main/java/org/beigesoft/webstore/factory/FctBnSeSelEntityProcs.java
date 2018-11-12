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
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.persistable.Languages;
import org.beigesoft.persistable.Countries;
import org.beigesoft.persistable.DecimalSeparator;
import org.beigesoft.persistable.DecimalGroupSeparator;
import org.beigesoft.orm.factory.FctBnEntitiesProcessors;
import org.beigesoft.webstore.service.IFindSeSeller;
import org.beigesoft.accounting.persistable.UnitOfMeasure;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.webstore.persistable.SeGoods;
import org.beigesoft.webstore.persistable.DestTaxSeGoodsLn;
import org.beigesoft.webstore.persistable.SeGoodsPlace;
import org.beigesoft.webstore.persistable.SeGoodsPrice;
import org.beigesoft.webstore.persistable.SeGoodsSpecifics;
import org.beigesoft.webstore.persistable.SeService;
import org.beigesoft.webstore.persistable.DestTaxSeServiceLn;
import org.beigesoft.webstore.persistable.SeServicePlace;
import org.beigesoft.webstore.persistable.SeServicePrice;
import org.beigesoft.webstore.persistable.SeServiceSpecifics;
import org.beigesoft.webstore.persistable.I18nSeGoods;
import org.beigesoft.webstore.persistable.I18nSeService;
import org.beigesoft.webstore.persistable.IHasSeSeller;
import org.beigesoft.webstore.persistable.PickUpPlace;
import org.beigesoft.webstore.persistable.SpecificsOfItem;
import org.beigesoft.webstore.persistable.PriceCategory;
import org.beigesoft.webstore.persistable.ChooseableSpecifics;
import org.beigesoft.webstore.processor.PrcHasSeSellerSave;
import org.beigesoft.webstore.processor.PrcHasSeSellerDel;
import org.beigesoft.webstore.processor.PrcSeGoodsSpecSave;
import org.beigesoft.webstore.processor.PrcSeServiceSpecSave;
import org.beigesoft.webstore.processor.PrcSeGdSpecEmbFlSave;
import org.beigesoft.webstore.processor.PrcSeGdSpecEmbFlDel;
import org.beigesoft.webstore.processor.PrcSeSrvSpecEmbFlSave;
import org.beigesoft.webstore.processor.PrcSeSrvSpecEmbFlDel;

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
   * <p>Factory non-acc entity processors. They are used either as
   * delegates in wrappers or directly.</p>
   **/
  private FctBnEntitiesProcessors<RS> fctBnEntitiesProcessors;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>Find S.E.Seller service.</p>
   **/
  private IFindSeSeller findSeSeller;

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
   * <p>Only constructor.</p>
   **/
  public FctBnSeSelEntityProcs() {
    this.sharedEntities = new HashSet<Class<?>>();
    this.sharedEntities.add(Languages.class);
    this.sharedEntities.add(Countries.class);
    this.sharedEntities.add(DecimalSeparator.class);
    this.sharedEntities.add(DecimalGroupSeparator.class);
    this.sharedEntities.add(SpecificsOfItem.class);
    this.sharedEntities.add(PickUpPlace.class);
    this.sharedEntities.add(UnitOfMeasure.class);
    this.sharedEntities.add(PriceCategory.class);
    this.sharedEntities.add(ChooseableSpecifics.class);
    this.sharedEntities.add(InvItemTaxCategory.class);
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
    this.seEntities.add(DestTaxSeGoodsLn.class);
    this.seEntities.add(DestTaxSeServiceLn.class);
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
          if (pBeanName.equals(PrcHasSeSellerSave.class.getSimpleName())) {
            proc = lazyGetPrcHasSeSellerSave(pAddParam);
          } else if (pBeanName.equals(PrcHasSeSellerDel
            .class.getSimpleName())) {
            proc = lazyGetPrcHasSeSellerDel(pAddParam);
          } else if (pBeanName.equals(PrcSeGoodsSpecSave
            .class.getSimpleName())) {
            proc = lazyGetPrcSeGoodsSpecSave(pAddParam);
          } else if (pBeanName.equals(PrcSeServiceSpecSave
            .class.getSimpleName())) {
            proc = lazyGetPrcSeServiceSpecSave(pAddParam);
          } else if (pBeanName.equals(PrcSeGdSpecEmbFlSave
            .class.getSimpleName())) {
            proc = lazyGetPrcSeGdSpecEmbFlSave(pAddParam);
          } else if (pBeanName.equals(PrcSeGdSpecEmbFlDel
            .class.getSimpleName())) {
            proc = lazyGetPrcSeGdSpecEmbFlDel(pAddParam);
          } else if (pBeanName.equals(PrcSeSrvSpecEmbFlSave
            .class.getSimpleName())) {
            proc = lazyGetPrcSeSrvSpecEmbFlSave(pAddParam);
          } else if (pBeanName.equals(PrcSeSrvSpecEmbFlDel
            .class.getSimpleName())) {
            proc = lazyGetPrcSeSrvSpecEmbFlDel(pAddParam);
          } else {
            proc = this.fctBnEntitiesProcessors.lazyGet(pAddParam, pBeanName);
          }
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
    throw new Exception("Setting is not allowed!");
  }

  /**
   * <p>Get PrcSeSrvSpecEmbFlDel (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSeSrvSpecEmbFlDel
   * @throws Exception - an exception
   */
  protected final PrcSeSrvSpecEmbFlDel<RS>
    lazyGetPrcSeSrvSpecEmbFlDel(
      final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcSeSrvSpecEmbFlDel.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcSeSrvSpecEmbFlDel<RS> proc = (PrcSeSrvSpecEmbFlDel<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcSeSrvSpecEmbFlDel<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setFindSeSeller(getFindSeSeller());
      proc.setWebAppPath(getWebAppPath());
      proc.setUploadDirectory(getUploadDirectory());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnSeSelEntityProcs.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcSeSrvSpecEmbFlSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSeSrvSpecEmbFlSave
   * @throws Exception - an exception
   */
  protected final PrcSeSrvSpecEmbFlSave<RS>
    lazyGetPrcSeSrvSpecEmbFlSave(
      final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcSeSrvSpecEmbFlSave.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcSeSrvSpecEmbFlSave<RS> proc = (PrcSeSrvSpecEmbFlSave<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcSeSrvSpecEmbFlSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setFindSeSeller(getFindSeSeller());
      proc.setWebAppPath(getWebAppPath());
      proc.setUploadDirectory(getUploadDirectory());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnSeSelEntityProcs.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcSeGdSpecEmbFlDel (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSeGdSpecEmbFlDel
   * @throws Exception - an exception
   */
  protected final PrcSeGdSpecEmbFlDel<RS>
    lazyGetPrcSeGdSpecEmbFlDel(
      final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcSeGdSpecEmbFlDel.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcSeGdSpecEmbFlDel<RS> proc = (PrcSeGdSpecEmbFlDel<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcSeGdSpecEmbFlDel<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setFindSeSeller(getFindSeSeller());
      proc.setWebAppPath(getWebAppPath());
      proc.setUploadDirectory(getUploadDirectory());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnSeSelEntityProcs.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcSeGdSpecEmbFlSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSeGdSpecEmbFlSave
   * @throws Exception - an exception
   */
  protected final PrcSeGdSpecEmbFlSave<RS>
    lazyGetPrcSeGdSpecEmbFlSave(
      final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcSeGdSpecEmbFlSave.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcSeGdSpecEmbFlSave<RS> proc = (PrcSeGdSpecEmbFlSave<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcSeGdSpecEmbFlSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setFindSeSeller(getFindSeSeller());
      proc.setWebAppPath(getWebAppPath());
      proc.setUploadDirectory(getUploadDirectory());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnSeSelEntityProcs.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcSeServiceSpecSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSeServiceSpecSave
   * @throws Exception - an exception
   */
  protected final PrcSeServiceSpecSave<RS>
    lazyGetPrcSeServiceSpecSave(
      final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcSeServiceSpecSave.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcSeServiceSpecSave<RS> proc = (PrcSeServiceSpecSave<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcSeServiceSpecSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setFindSeSeller(getFindSeSeller());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnSeSelEntityProcs.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcSeGoodsSpecSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSeGoodsSpecSave
   * @throws Exception - an exception
   */
  protected final PrcSeGoodsSpecSave<RS>
    lazyGetPrcSeGoodsSpecSave(
      final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcSeGoodsSpecSave.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcSeGoodsSpecSave<RS> proc = (PrcSeGoodsSpecSave<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcSeGoodsSpecSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setFindSeSeller(getFindSeSeller());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnSeSelEntityProcs.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcHasSeSellerDel (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcHasSeSellerDel
   * @throws Exception - an exception
   */
  protected final PrcHasSeSellerDel<RS, IHasSeSeller<Object>, Object>
    lazyGetPrcHasSeSellerDel(
      final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcHasSeSellerDel.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcHasSeSellerDel<RS, IHasSeSeller<Object>, Object> proc =
      (PrcHasSeSellerDel<RS, IHasSeSeller<Object>, Object>)
        this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcHasSeSellerDel<RS, IHasSeSeller<Object>, Object>();
      proc.setSrvOrm(getSrvOrm());
      proc.setFindSeSeller(getFindSeSeller());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnSeSelEntityProcs.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcHasSeSellerSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcHasSeSellerSave
   * @throws Exception - an exception
   */
  protected final PrcHasSeSellerSave<RS, IHasSeSeller<Object>, Object>
    lazyGetPrcHasSeSellerSave(
      final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcHasSeSellerSave.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcHasSeSellerSave<RS, IHasSeSeller<Object>, Object> proc =
      (PrcHasSeSellerSave<RS, IHasSeSeller<Object>, Object>)
        this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcHasSeSellerSave<RS, IHasSeSeller<Object>, Object>();
      proc.setSrvOrm(getSrvOrm());
      proc.setFindSeSeller(getFindSeSeller());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnSeSelEntityProcs.class,
        beanName + " has been created.");
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

  /**
   * <p>Getter for findSeSeller.</p>
   * @return IFindSeSeller<RS>
   **/
  public final IFindSeSeller getFindSeSeller() {
    return this.findSeSeller;
  }

  /**
   * <p>Setter for findSeSeller.</p>
   * @param pFindSeSeller reference
   **/
  public final void setFindSeSeller(final IFindSeSeller pFindSeSeller) {
    this.findSeSeller = pFindSeSeller;
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
