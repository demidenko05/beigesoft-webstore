package org.beigesoft.webstore.processor;

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

import java.util.Date;
import java.util.Map;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.webstore.persistable.GoodsSpecific;
import org.beigesoft.webstore.persistable.GoodsSpecificId;
import org.beigesoft.webstore.service.ISrvTradingSettings;

/**
 * <p>Service that save GoodsSpecific into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcGoodsSpecificSave<RS>
  implements IEntityProcessor<GoodsSpecific, GoodsSpecificId> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

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
   * <p>Process entity request.</p>
   * @param pAddParam additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final GoodsSpecific process(
    final Map<String, Object> pAddParam,
      final GoodsSpecific pEntity,
        final IRequestData pRequestData) throws Exception {
    //Beige-ORM refresh:
    pEntity.setGoods(getSrvOrm().retrieveEntity(pAddParam, pEntity.getGoods()));
    pEntity.setSpecifics(getSrvOrm().retrieveEntity(pAddParam,
      pEntity.getSpecifics()));
    if (pEntity.getSpecifics().getChooseableSpecificsType() != null) {
      pEntity.setLongValue2(pEntity.getSpecifics().getChooseableSpecificsType()
        .getItsId());
      pEntity.setStringValue2(pEntity.getSpecifics()
        .getChooseableSpecificsType().getItsName());
    }
    if (!(InvItem.FINISHED_PRODUCT_ID.equals(pEntity.getGoods().getItsType()
      .getItsId()) || InvItem.MERCHANDISE_ID.equals(pEntity.getGoods()
        .getItsType().getItsId()))) {
      if (pAddParam.get("user") != null) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "type_must_be_material_or_merchandise",
            pAddParam.get("user").toString());
      } else {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "type_must_be_material_or_merchandise");
      }
    }
    if (pEntity.getIsNew()) {
      //only complex ID
      getSrvOrm().insertEntity(pAddParam, pEntity);
    } else {
      //if exist file name:
      String fileToUploadName = (String) pRequestData
        .getAttribute("fileToUploadName");
      if (fileToUploadName != null) {
        OutputStream outs = null;
        InputStream ins = null;
        try {
          String fileToUploadUrl = this.uploadDirectory + File.separator
            + new Date().getTime() + fileToUploadName;
          pEntity.setStringValue1(fileToUploadUrl);
          //fill file and filePath field:
          String filePath = this.webAppPath + File.separator + fileToUploadUrl;
          ins = (InputStream) pRequestData
            .getAttribute("fileToUploadInputStream");
          outs = new BufferedOutputStream(
            new FileOutputStream(filePath));
          byte[] data = new byte[1024];
          int count;
          while ((count = ins.read(data)) != -1) {
            outs.write(data, 0, count);
          }
          outs.flush();
          pEntity.setStringValue2(filePath);
        } finally {
          if (ins != null) {
            ins.close();
          }
          if (outs != null) {
            outs.close();
          }
        }
      }
      getSrvOrm().updateEntity(pAddParam, pEntity);
    }
    pRequestData.setAttribute("tradingSettings", srvTradingSettings
      .lazyGetTradingSettings(pAddParam));
    pRequestData.setAttribute("accSettings",
      this.srvAccSettings.lazyGetAccSettings(pAddParam));
    return pEntity;
  }


  //Simple getters and setters:
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
