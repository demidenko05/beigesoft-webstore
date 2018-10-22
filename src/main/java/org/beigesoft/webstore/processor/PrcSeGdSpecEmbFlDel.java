package org.beigesoft.webstore.processor;

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
import java.io.File;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.webstore.persistable.SeSeller;
import org.beigesoft.webstore.persistable.SeGoodsSpecifics;
import org.beigesoft.webstore.persistable.SeGoodsSpecificsId;
import org.beigesoft.webstore.service.IFindSeSeller;

/**
 * <p>Service that deletes S.E.Seller's GoodSpecifics of type FILE_EMBEDDED
 * from DB and FS. Because of I18N such specifics can has multiply
 * uploaded files, e.g. "127673111fordred.html,127673111fordred_ru.html".</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSeGdSpecEmbFlDel<RS>
    implements IEntityProcessor<SeGoodsSpecifics, SeGoodsSpecificsId> {

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
   * <p>Find S.E.Seller service.</p>
   **/
  private IFindSeSeller findSeSeller;

  /**
   * <p>Process entity request.</p>
   * @param pReqVars additional request scoped parameters
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final SeGoodsSpecifics process(
    final Map<String, Object> pReqVars,
      final SeGoodsSpecifics pEntity,
        final IRequestData pRequestData) throws Exception {
    SeSeller ses = this.findSeSeller.find(pReqVars, pRequestData.getUserName());
    pEntity.setSeller(ses);
    SeGoodsSpecifics entOld = this.srvOrm.retrieveEntity(pReqVars, pEntity);
    if (!entOld.getSeller().getItsId().getItsId()
      .equals(pEntity.getSeller().getItsId().getItsId())) {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
      "Attempt to delete smb. else's entity: user/entity/EID/SEOLDID/SEID - "
        + pRequestData.getUserName() + "/" + pEntity.getClass()
         .getSimpleName() + "/" + pEntity.getItsId() + "/" + entOld
          .getSeller().getItsId().getItsId() + "/" + pEntity.getSeller()
            .getItsId().getItsId());
    }
    File fileToDel;
    if (pEntity.getStringValue2() != null) {
      fileToDel = new File(pEntity.getStringValue2());
      if (fileToDel.exists() && !fileToDel.delete()) {
        throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
          "Can not delete file: " + fileToDel);
      }
    }
    if (pEntity.getStringValue3() != null) {
      int idhHtml = pEntity.getStringValue1().indexOf(".html");
      String urlWithoutHtml = pEntity.getStringValue1().substring(0, idhHtml);
      for (String lang : pEntity.getStringValue3().split(",")) {
        String filePath = this.webAppPath + File.separator
          + urlWithoutHtml + "_" + lang + ".html";
        fileToDel = new File(filePath);
        if (fileToDel.exists() && !fileToDel.delete()) {
          throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
            "Can not delete file: " + fileToDel);
        }
      }
    }
    getSrvOrm().deleteEntity(pReqVars, pEntity);
    return null;
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
}
