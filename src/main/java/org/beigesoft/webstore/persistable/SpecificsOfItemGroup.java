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

import org.beigesoft.persistable.AHasNameIdLongVersion;

/**
 * <pre>
 * Model of Group of Specifics e.g. "Monitor" for its size, web-cam,
 * LED-type in a notebook.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class SpecificsOfItemGroup extends AHasNameIdLongVersion {

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>HTML template start to render it, if assigned.</p>
   **/
  private HtmlTemplate templateStart;

  /**
   * <p>HTML template end to render it, if assigned.</p>
   **/
  private HtmlTemplate templateEnd;

  /**
   * <p>HTML template start to render it, if assigned.</p>
   **/
  private HtmlTemplate templateDetail;

  //Simple getters and setters:
  /**
   * <p>Getter for description.</p>
   * @return String
   **/
  public final String getDescription() {
    return this.description;
  }

  /**
   * <p>Setter for description.</p>
   * @param pDescription reference
   **/
  public final void setDescription(final String pDescription) {
    this.description = pDescription;
  }

  /**
   * <p>Getter for templateStart.</p>
   * @return HtmlTemplate
   **/
  public final HtmlTemplate getTemplateStart() {
    return this.templateStart;
  }

  /**
   * <p>Setter for templateStart.</p>
   * @param pTemplateStart reference
   **/
  public final void setTemplateStart(final HtmlTemplate pTemplateStart) {
    this.templateStart = pTemplateStart;
  }

  /**
   * <p>Getter for templateEnd.</p>
   * @return HtmlTemplate
   **/
  public final HtmlTemplate getTemplateEnd() {
    return this.templateEnd;
  }

  /**
   * <p>Setter for templateEnd.</p>
   * @param pTemplateEnd reference
   **/
  public final void setTemplateEnd(final HtmlTemplate pTemplateEnd) {
    this.templateEnd = pTemplateEnd;
  }

  /**
   * <p>Getter for templateDetail.</p>
   * @return HtmlTemplate
   **/
  public final HtmlTemplate getTemplateDetail() {
    return this.templateDetail;
  }

  /**
   * <p>Setter for templateDetail.</p>
   * @param pTemplateDetail reference
   **/
  public final void setTemplateDetail(final HtmlTemplate pTemplateDetail) {
    this.templateDetail = pTemplateDetail;
  }
}
