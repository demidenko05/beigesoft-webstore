package org.beigesoft.webstore.persistable;

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

import org.beigesoft.persistable.AHasIdLongVersion;

/**
 * <pre>
 * Hold additional settings.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class SettingsAdd extends AHasIdLongVersion {

  /**
   * <p>Records per transactions for bulk operations.</p>
   **/
  private Integer recordsPerTransaction;

  /**
   * <p>Minimum list size for enable ordering, 20 default.</p>
   **/
  private Integer minimumListSizeForOrdering;

  /**
   * <p>null default, HTML template for whole specifics start,
   * e.g. "&lt;ul&gt;".</p>
   **/
  private String specHtmlStart;

  /**
   * <p>null default, HTML template for whole specifics start,
   * e.g. "&lt;/ul&gt;".</p>
   **/
  private String specHtmlEnd;

  /**
   * <p>null default, HTML template for group specifics start,
   * e.g. "&lt;li&gt;".</p>
   **/
  private String specGrHtmlStart;

  /**
   * <p>null default, HTML template for group specifics start,
   * e.g. "&lt;/li&gt;".</p>
   **/
  private String specGrHtmlEnd;

  /**
   * <p>not null, specifics separator, default ",".</p>
   **/
  private String specSeparator;

  /**
   * <p>null default, specifics groups separator, e.g ";".</p>
   **/
  private String specGrSeparator;

  //Simple getters and setters:
  /**
   * <p>Getter for recordsPerTransaction.</p>
   * @return Integer
   **/
  public final Integer getRecordsPerTransaction() {
    return this.recordsPerTransaction;
  }

  /**
   * <p>Setter for recordsPerTransaction.</p>
   * @param pRecordsPerTransaction reference
   **/
  public final void setRecordsPerTransaction(
    final Integer pRecordsPerTransaction) {
    this.recordsPerTransaction = pRecordsPerTransaction;
  }

  /**
   * <p>Getter for minimumListSizeForOrdering.</p>
   * @return Integer
   **/
  public final Integer getMinimumListSizeForOrdering() {
    return this.minimumListSizeForOrdering;
  }

  /**
   * <p>Setter for minimumListSizeForOrdering.</p>
   * @param pMinimumListSizeForOrdering reference
   **/
  public final void setMinimumListSizeForOrdering(
    final Integer pMinimumListSizeForOrdering) {
    this.minimumListSizeForOrdering = pMinimumListSizeForOrdering;
  }

  /**
   * <p>Getter for specHtmlStart.</p>
   * @return String
   **/
  public final String getSpecHtmlStart() {
    return this.specHtmlStart;
  }

  /**
   * <p>Setter for specHtmlStart.</p>
   * @param pSpecHtmlStart reference
   **/
  public final void setSpecHtmlStart(final String pSpecHtmlStart) {
    this.specHtmlStart = pSpecHtmlStart;
  }

  /**
   * <p>Getter for specHtmlEnd.</p>
   * @return String
   **/
  public final String getSpecHtmlEnd() {
    return this.specHtmlEnd;
  }

  /**
   * <p>Setter for specHtmlEnd.</p>
   * @param pSpecHtmlEnd reference
   **/
  public final void setSpecHtmlEnd(final String pSpecHtmlEnd) {
    this.specHtmlEnd = pSpecHtmlEnd;
  }

  /**
   * <p>Getter for specGrHtmlStart.</p>
   * @return String
   **/
  public final String getSpecGrHtmlStart() {
    return this.specGrHtmlStart;
  }

  /**
   * <p>Setter for specGrHtmlStart.</p>
   * @param pSpecGrHtmlStart reference
   **/
  public final void setSpecGrHtmlStart(final String pSpecGrHtmlStart) {
    this.specGrHtmlStart = pSpecGrHtmlStart;
  }

  /**
   * <p>Getter for specGrHtmlEnd.</p>
   * @return String
   **/
  public final String getSpecGrHtmlEnd() {
    return this.specGrHtmlEnd;
  }

  /**
   * <p>Setter for specGrHtmlEnd.</p>
   * @param pSpecGrHtmlEnd reference
   **/
  public final void setSpecGrHtmlEnd(final String pSpecGrHtmlEnd) {
    this.specGrHtmlEnd = pSpecGrHtmlEnd;
  }

  /**
   * <p>Getter for specSeparator.</p>
   * @return String
   **/
  public final String getSpecSeparator() {
    return this.specSeparator;
  }

  /**
   * <p>Setter for specSeparator.</p>
   * @param pSpecSeparator reference
   **/
  public final void setSpecSeparator(final String pSpecSeparator) {
    this.specSeparator = pSpecSeparator;
  }

  /**
   * <p>Getter for specGrSeparator.</p>
   * @return String
   **/
  public final String getSpecGrSeparator() {
    return this.specGrSeparator;
  }

  /**
   * <p>Setter for specGrSeparator.</p>
   * @param pSpecGrSeparator reference
   **/
  public final void setSpecGrSeparator(final String pSpecGrSeparator) {
    this.specGrSeparator = pSpecGrSeparator;
  }
}
