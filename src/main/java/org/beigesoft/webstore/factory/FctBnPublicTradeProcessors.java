package org.beigesoft.webstore.factory;

/*
 * Copyright (c) 2017 Beigesoft™
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
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.holder.IHolderForClassByName;
import org.beigesoft.converter.IConverterToFromString;
import org.beigesoft.settings.IMngSettings;
import org.beigesoft.handler.ISpamHnd;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvPage;
import org.beigesoft.service.ISrvDate;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.webstore.processor.PrcWebstorePage;
import org.beigesoft.webstore.processor.PrcItemPage;
import org.beigesoft.webstore.processor.PrcDelItemFromCart;
import org.beigesoft.webstore.processor.PrcItemInCart;
import org.beigesoft.webstore.processor.PrCart;
import org.beigesoft.webstore.processor.PrcCheckOut;
import org.beigesoft.webstore.processor.PrPur;
import org.beigesoft.webstore.processor.PrBur;
import org.beigesoft.webstore.processor.PrBuOr;
import org.beigesoft.webstore.processor.PrLog;
import org.beigesoft.webstore.service.ISrvShoppingCart;
import org.beigesoft.webstore.service.IAcpOrd;
import org.beigesoft.webstore.service.ICncOrd;
import org.beigesoft.webstore.service.IBuySr;

/**
 * <p>Public trade processors factory.
 * All processors are public i.e. no need authorization.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class FctBnPublicTradeProcessors<RS>
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
   */
  private ISrvPage srvPage;

  /**
   * <p>Manager UVD settings.</p>
   **/
  private IMngSettings mngUvdSettings;

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>Logger security.</p>
   **/
  private ILogger secLog;

  /**
   * <p>Converters map "converter name"-"object' s converter".</p>
   **/
  private final Map<String, IProcessor> processorsMap =
    new HashMap<String, IProcessor>();

  /**
   * <p>Shopping Cart service.</p>
   **/
  private ISrvShoppingCart srvShoppingCart;

  /**
   * <p>Accept buyer's new orders service.</p>
   **/
  private IAcpOrd acpOrd;

  /**
   * <p>Cancel accepted buyer's orders service.</p>
   **/
  private ICncOrd cncOrd;

  /**
   * <p>Date service.</p>
   **/
  private ISrvDate srvDate;

  /**
   * <p>Field converter names holder.</p>
   **/
  private IHolderForClassByName<String> hldFldCnv;

  /**
   * <p>Fields converters factory.</p>
   **/
  private IFactoryAppBeansByName<IConverterToFromString<?>> facFldCnv;

  /**
   * <p>Buyer service.</p>
   **/
  private IBuySr buySr;

  /**
   * <p>Spam handler.</p>
   **/
  private ISpamHnd spamHnd;

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
    IProcessor proc = this.processorsMap.get(pBeanName);
    if (proc == null) {
      // locking:
      synchronized (this.processorsMap) {
        // make sure again whether it's null after locking:
        proc = this.processorsMap.get(pBeanName);
        if (proc == null) {
          if (pBeanName.equals(PrcDelItemFromCart.class.getSimpleName())) {
            proc = lazyGetPrcDelItemFromCart(pAddParam);
          } else if (pBeanName.equals(PrCart.class.getSimpleName())) {
            proc = lazyGetPrCart(pAddParam);
          } else if (pBeanName.equals(PrcItemInCart.class.getSimpleName())) {
            proc = lazyGetPrcItemInCart(pAddParam);
          } else if (pBeanName.equals(PrLog.class.getSimpleName())) {
            proc = lazyGetPrLog(pAddParam);
          } else if (pBeanName.equals(PrcCheckOut.class.getSimpleName())) {
            proc = lazyGetPrcCheckOut(pAddParam);
          } else if (pBeanName.equals(PrBuOr.class.getSimpleName())) {
            proc = lazyGetPrBuOr(pAddParam);
          } else if (pBeanName.equals(PrBur.class.getSimpleName())) {
            proc = lazyGetPrBur(pAddParam);
          } else if (pBeanName.equals(PrPur.class.getSimpleName())) {
            proc = lazyGetPrPur(pAddParam);
          } else if (pBeanName.equals(PrcItemPage.class.getSimpleName())) {
            proc = lazyGetPrcItemPage(pAddParam);
          } else if (pBeanName.equals(PrcWebstorePage.class.getSimpleName())) {
            proc = lazyGetPrcWebstorePage(pAddParam);
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
    final IProcessor pBean) throws Exception {
    //only PPL processor:
    if ("PrPpl".equals(pBeanName)) {
      this.processorsMap.put(pBeanName, pBean);
    } else {
      throw new Exception("Setting is not allowed!");
    }
  }

  /**
   * <p>Lazy get PrcDelItemFromCart.</p>
   * @param pAddParam additional param
   * @return requested PrcDelItemFromCart
   * @throws Exception - an exception
   */
  protected final PrcDelItemFromCart<RS> lazyGetPrcDelItemFromCart(
    final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcDelItemFromCart.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcDelItemFromCart<RS> proc = (PrcDelItemFromCart<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcDelItemFromCart<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvShoppingCart(getSrvShoppingCart());
      proc.setProcessorsFactory(this);
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnPublicTradeProcessors.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Lazy get PrBuOr.</p>
   * @param pAddParam additional param
   * @return requested PrBuOr
   * @throws Exception - an exception
   */
  protected final PrBuOr<RS> lazyGetPrBuOr(
    final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrBuOr.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrBuOr<RS> proc = (PrBuOr<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrBuOr<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvPage(getSrvPage());
      proc.setProcFac(this);
      proc.setMngUvd(getMngUvdSettings());
      proc.setSrvDate(getSrvDate());
      proc.setHldFldCnv(getHldFldCnv());
      proc.setFacFldCnv(getFacFldCnv());
      proc.setBuySr(getBuySr());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnPublicTradeProcessors.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Lazy get PrBur.</p>
   * @param pAddParam additional param
   * @return requested PrBur
   * @throws Exception - an exception
   */
  protected final PrBur<RS> lazyGetPrBur(
    final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrBur.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrBur<RS> proc = (PrBur<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrBur<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvDb(getSrvDatabase());
      proc.setLog(getLogger());
      proc.setProcFac(this);
      proc.setBuySr(getBuySr());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnPublicTradeProcessors.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Lazy get PrPur.</p>
   * @param pAddParam additional param
   * @return requested PrPur
   * @throws Exception - an exception
   */
  protected final PrPur<RS> lazyGetPrPur(
    final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrPur.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrPur<RS> proc = (PrPur<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrPur<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvDb(getSrvDatabase());
      proc.setSrvCart(getSrvShoppingCart());
      proc.setLog(getLogger());
      proc.setSecLog(getSecLog());
      proc.setAcpOrd(getAcpOrd());
      proc.setProcFac(this);
      proc.setBuySr(getBuySr());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnPublicTradeProcessors.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcCheckOut.</p>
   * @param pAddParam additional param
   * @return requested PrcCheckOut
   * @throws Exception - an exception
   */
  protected final PrcCheckOut<RS> lazyGetPrcCheckOut(
    final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcCheckOut.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcCheckOut<RS> proc = (PrcCheckOut<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcCheckOut<RS>();
      proc.setLog(getLogger());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvCart(getSrvShoppingCart());
      proc.setProcFac(this);
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnPublicTradeProcessors.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Lazy get PrCart.</p>
   * @param pAddParam additional param
   * @return requested PrCart
   * @throws Exception - an exception
   */
  protected final PrCart<RS> lazyGetPrCart(
    final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrCart.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrCart<RS> proc = (PrCart<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrCart<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvCart(getSrvShoppingCart());
      proc.setProcessorsFactory(this);
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnPublicTradeProcessors.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Lazy get PrLog.</p>
   * @param pAddParam additional param
   * @return requested PrLog
   * @throws Exception - an exception
   */
  protected final PrLog<RS> lazyGetPrLog(
    final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrLog.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrLog<RS> proc = (PrLog<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrLog<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvDb(getSrvDatabase());
      proc.setLog(getSecLog());
      proc.setSrvCart(getSrvShoppingCart());
      proc.setProcFac(this);
      proc.setBuySr(getBuySr());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnPublicTradeProcessors.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcItemInCart.</p>
   * @param pAddParam additional param
   * @return requested PrcItemInCart
   * @throws Exception - an exception
   */
  protected final PrcItemInCart<RS> lazyGetPrcItemInCart(
    final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcItemInCart.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcItemInCart<RS> proc = (PrcItemInCart<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcItemInCart<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvCart(getSrvShoppingCart());
      proc.setProcessorsFactory(this);
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnPublicTradeProcessors.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcItemPage.</p>
   * @param pAddParam additional param
   * @return requested PrcItemPage
   * @throws Exception - an exception
   */
  protected final PrcItemPage<RS> lazyGetPrcItemPage(
    final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcItemPage.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcItemPage<RS> proc = (PrcItemPage<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcItemPage<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvCart(getSrvShoppingCart());
      proc.setBuySr(getBuySr());
      proc.setLogger(getLogger());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnPublicTradeProcessors.class,
        beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcWebstorePage.</p>
   * @param pAddParam additional param
   * @return requested PrcWebstorePage
   * @throws Exception - an exception
   */
  protected final PrcWebstorePage<RS> lazyGetPrcWebstorePage(
    final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcWebstorePage.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcWebstorePage<RS> proc = (PrcWebstorePage<RS>)
      this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcWebstorePage<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setLogger(getLogger());
      proc.setSrvDatabase(getSrvDatabase());
      proc.setSrvShoppingCart(getSrvShoppingCart());
      proc.setSrvPage(getSrvPage());
      proc.setMngUvdSettings(getMngUvdSettings());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnPublicTradeProcessors.class,
        beanName + " has been created.");
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
   * <p>Getter for srvPage.</p>
   * @return ISrvPage
   **/
  public final ISrvPage getSrvPage() {
    return this.srvPage;
  }

  /**
   * <p>Setter for srvPage.</p>
   * @param pSrvPage reference
   **/
  public final void setSrvPage(final ISrvPage pSrvPage) {
    this.srvPage = pSrvPage;
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
   * <p>Getter for srvShoppingCart.</p>
   * @return ISrvShoppingCart
   **/
  public final ISrvShoppingCart getSrvShoppingCart() {
    return this.srvShoppingCart;
  }

  /**
   * <p>Setter for srvShoppingCart.</p>
   * @param pSrvShoppingCart reference
   **/
  public final void setSrvShoppingCart(
    final ISrvShoppingCart pSrvShoppingCart) {
    this.srvShoppingCart = pSrvShoppingCart;
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
   * <p>Getter for secLog.</p>
   * @return ILogger
   **/
  public final ILogger getSecLog() {
    return this.secLog;
  }

  /**
   * <p>Setter for secLog.</p>
   * @param pSecLog reference
   **/
  public final void setSecLog(final ILogger pSecLog) {
    this.secLog = pSecLog;
  }

  /**
   * <p>Getter for acpOrd.</p>
   * @return IAcpOrd
   **/
  public final IAcpOrd getAcpOrd() {
    return this.acpOrd;
  }

  /**
   * <p>Setter for acpOrd.</p>
   * @param pAcpOrd reference
   **/
  public final void setAcpOrd(final IAcpOrd pAcpOrd) {
    this.acpOrd = pAcpOrd;
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

  /**
   * <p>Getter for srvDate.</p>
   * @return ISrvDate
   **/
  public final ISrvDate getSrvDate() {
    return this.srvDate;
  }

  /**
   * <p>Setter for srvDate.</p>
   * @param pSrvDate reference
   **/
  public final void setSrvDate(final ISrvDate pSrvDate) {
    this.srvDate = pSrvDate;
  }

  /**
   * <p>Getter for hldFldCnv.</p>
   * @return IHolderForClassByName<String>
   **/
  public final IHolderForClassByName<String> getHldFldCnv() {
    return this.hldFldCnv;
  }

  /**
   * <p>Setter for hldFldCnv.</p>
   * @param pHldFldCnv reference
   **/
  public final void setHldFldCnv(
    final IHolderForClassByName<String> pHldFldCnv) {
    this.hldFldCnv = pHldFldCnv;
  }

  /**
   * <p>Getter for facFldCnv.</p>
   * @return IFactoryAppBeansByName<IConverterToFromString<?>>
   **/
  public final IFactoryAppBeansByName<IConverterToFromString<?>>
    getFacFldCnv() {
    return this.facFldCnv;
  }

  /**
   * <p>Setter for facFldCnv.</p>
   * @param pFacFldCnv reference
   **/
  public final void setFacFldCnv(
    final IFactoryAppBeansByName<IConverterToFromString<?>> pFacFldCnv) {
    this.facFldCnv = pFacFldCnv;
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

  /**
   * <p>Getter for spamHnd.</p>
   * @return ISpamHnd
   **/
  public final ISpamHnd getSpamHnd() {
    return this.spamHnd;
  }

  /**
   * <p>Setter for spamHnd.</p>
   * @param pSpamHnd reference
   **/
  public final void setSpamHnd(final ISpamHnd pSpamHnd) {
    this.spamHnd = pSpamHnd;
  }
}
