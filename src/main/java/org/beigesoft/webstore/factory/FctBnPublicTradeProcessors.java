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
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvPage;
import org.beigesoft.settings.IMngSettings;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.webstore.processor.PrcWebstorePage;
import org.beigesoft.webstore.processor.PrcItemPage;
import org.beigesoft.webstore.processor.PrcDelItemFromCart;
import org.beigesoft.webstore.processor.PrcItemInCart;
import org.beigesoft.webstore.service.ISrvShoppingCart;

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
   * <p>Converters map "converter name"-"object' s converter".</p>
   **/
  private final Map<String, IProcessor> processorsMap =
    new HashMap<String, IProcessor>();

  /**
   * <p>Shopping Cart service.</p>
   **/
  private ISrvShoppingCart srvShoppingCart;

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
          if (pBeanName.equals(PrcDelItemFromCart
            .class.getSimpleName())) {
            proc = lazyGetPrcDelItemFromCart(pAddParam);
          } else if (pBeanName.equals(PrcItemInCart
            .class.getSimpleName())) {
            proc = lazyGetPrcItemInCart(pAddParam);
          } else if (pBeanName.equals(PrcItemPage
            .class.getSimpleName())) {
            proc = lazyGetPrcItemPage(pAddParam);
          } else if (pBeanName.equals(PrcWebstorePage
            .class.getSimpleName())) {
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
    throw new Exception("Setting is not allowed!");
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
      proc.setSrvDatabase(getSrvDatabase());
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
      proc.setSrvDatabase(getSrvDatabase());
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
      proc.setSrvShoppingCart(getSrvShoppingCart());
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
}
