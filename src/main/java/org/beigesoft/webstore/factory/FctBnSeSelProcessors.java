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

import java.util.Map;
import java.util.HashMap;

import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IProcessor;
import org.beigesoft.orm.service.SrvEntitiesPage;
import org.beigesoft.orm.processor.PrcEntitiesPage;

/**
 * <p>S.E. Sellers processors factory.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class FctBnSeSelProcessors<RS>
  implements IFactoryAppBeansByName<IProcessor> {

  /**
   * <p>Converters map "converter name"-"object' s converter".</p>
   **/
  private final Map<String, IProcessor> processorsMap =
    new HashMap<String, IProcessor>();

  /**
   * <p>Page service.</p>
   **/
  private SrvEntitiesPage<RS> srvEntitiesPage;

  /**
   * <p>Get bean in lazy mode (if bean is null then initialize it).</p>
   * @param pAddParam additional param
   * @param pBeanName - bean name
   * @return requested bean
   * @throws Exception - an exception
   */
  @Override
  public final IProcessor lazyGet(
    final Map<String, Object> pAddParam,
      final String pBeanName) throws Exception {
    IProcessor proc =
      this.processorsMap.get(pBeanName);
    if (proc == null) {
      // locking:
      synchronized (this.processorsMap) {
        // make sure again whether it's null after locking:
        proc = this.processorsMap.get(pBeanName);
        if (proc == null && pBeanName
          .equals(PrcEntitiesPage.class.getSimpleName())) {
          proc = createPutPrcEntitiesPage();
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
  public final synchronized void set(final String pBeanName,
    final IProcessor pBean) throws Exception {
    //hard-coded
  }

  /**
   * <p>Get PrcEntitiesPage (create and put into map).</p>
   * @return requested PrcEntitiesPage
   * @throws Exception - an exception
   */
  protected final PrcEntitiesPage
    createPutPrcEntitiesPage() throws Exception {
    PrcEntitiesPage proc = new PrcEntitiesPage();
    proc.setSrvEntitiesPage(this.srvEntitiesPage);
    //assigning fully initialized object:
    this.processorsMap
      .put(PrcEntitiesPage.class.getSimpleName(), proc);
    return proc;
  }

  //Simple getters and setters:
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
}
