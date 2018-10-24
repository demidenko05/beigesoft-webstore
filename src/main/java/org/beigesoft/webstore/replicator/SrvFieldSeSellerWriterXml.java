package org.beigesoft.webstore.replicator;

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
import java.io.Writer;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.webstore.persistable.SeSeller;
import org.beigesoft.replicator.service.ISrvFieldWriter;

/**
 * <p>Service to write SeSeller owned entity as ID.</p>
 *
 * @author Yury Demidenko
 */
public class SrvFieldSeSellerWriterXml implements ISrvFieldWriter {

  /**
   * <p>
   * Write standard field of entity into a stream
   * (writer - file or pass it through network).
   * </p>
   * @param pAddParam additional params (e.g. exclude fields set)
   * @param pField value
   * @param pFieldName Field Name
   * @param pWriter writer
   * @throws Exception - an exception
   **/
  @Override
  public final void write(final Map<String, Object> pAddParam,
    final Object pField, final String pFieldName,
      final Writer pWriter) throws Exception {
    String fieldValue;
    if (pField == null) {
      fieldValue = "NULL";
    } else {
      if (SeSeller.class != pField.getClass()) {
        throw new ExceptionWithCode(ExceptionWithCode
          .CONFIGURATION_MISTAKE, "It's wrong service to write that field: "
            + pField + "/" + pFieldName);
      }
      fieldValue = ((SeSeller) pField).getSeller().getItsId().toString();
    }
    pWriter.write(" " + pFieldName + "=\"" + fieldValue
      + "\"\n");
  }
}
