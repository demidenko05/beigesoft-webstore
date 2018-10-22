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

import java.util.Date;
import java.util.Map;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.webstore.persistable.SeSeller;
import org.beigesoft.webstore.persistable.SeServiceSpecifics;
import org.beigesoft.webstore.persistable.SeServiceSpecificsId;
import org.beigesoft.webstore.service.IFindSeSeller;

/**
 * <p>It saves S.E.Seller's ServiceSpecifics of type FILE_EMBEDDED into DB
 * and file into uploads. Because of I18N such specifics can has multiply
 * uploaded files, e.g. "127673111fordred.html,127673111fordred_ru.html".
 * For second, third, etc. file it expects parameter "fileLang".</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSeSrvSpecEmbFlSave<RS>
    implements IEntityProcessor<SeServiceSpecifics, SeServiceSpecificsId> {

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
  public final SeServiceSpecifics process(
    final Map<String, Object> pReqVars,
      final SeServiceSpecifics pEntity,
        final IRequestData pRequestData) throws Exception {
    SeSeller ses = this.findSeSeller.find(pReqVars, pRequestData.getUserName());
    pEntity.setSeller(ses);
    SeServiceSpecifics entOld = this.srvOrm.retrieveEntity(pReqVars, pEntity);
    if (!entOld.getSeller().getItsId().getItsId()
      .equals(pEntity.getSeller().getItsId().getItsId())) {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
      "Attempt to update smb. else's entity: user/entity/EID/SEOLDID/SEID - "
        + pRequestData.getUserName() + "/" + pEntity.getClass()
         .getSimpleName() + "/" + pEntity.getItsId() + "/" + entOld
          .getSeller().getItsId().getItsId() + "/" + pEntity.getSeller()
            .getItsId().getItsId());
    }
    String fileToUploadName = (String) pRequestData
      .getAttribute("fileToUploadName");
    OutputStream outs = null;
    InputStream ins = null;
    try {
      String filePath;
      if (pEntity.getStringValue1() == null) { //in base language:
        String ft = String.valueOf(new Date().getTime());
        filePath = this.webAppPath + File.separator
          + this.uploadDirectory + File.separator
            + ft + fileToUploadName;
        pEntity.setStringValue2(filePath);
        pEntity.setStringValue1(this.uploadDirectory
              + "/" + ft + fileToUploadName);
      } else { //I18N files:
        String fileLang = pRequestData.getParameter("fileLang");
        if (pEntity.getStringValue3() == null
          || !pEntity.getStringValue3().contains(fileLang)) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "notset_language");
        }
        int idhHtml = pEntity.getStringValue1().indexOf(".html");
        String urlWithoutHtml = pEntity.getStringValue1().substring(0, idhHtml);
        filePath = this.webAppPath + File.separator
          + urlWithoutHtml + "_" + fileLang + ".html";
      }
      ins = (InputStream) pRequestData.getAttribute("fileToUploadInputStream");
      outs = new BufferedOutputStream(new FileOutputStream(filePath));
      byte[] data = new byte[1024];
      int count;
      while ((count = ins.read(data)) != -1) {
        outs.write(data, 0, count);
      }
      outs.flush();
    } finally {
      if (ins != null) {
        ins.close();
      }
      if (outs != null) {
        outs.close();
      }
    }
    getSrvOrm().updateEntity(pReqVars, pEntity);
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
