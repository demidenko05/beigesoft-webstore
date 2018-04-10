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
 * Model of Type Chooseable Specifics Goods, e.g. "Operation system".
 * </pre>
 *
 * @author Yury Demidenko
 */
public class ChooseableSpecificsType extends AHasNameIdLongVersion {

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>HTML template to render it, if assigned.</p>
   **/
  private HtmlTemplate htmlTemplate;

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
   * <p>Getter for htmlTemplate.</p>
   * @return HtmlTemplate
   **/
  public final HtmlTemplate getHtmlTemplate() {
    return this.htmlTemplate;
  }

  /**
   * <p>Setter for htmlTemplate.</p>
   * @param pHtmlTemplate reference
   **/
  public final void setHtmlTemplate(final HtmlTemplate pHtmlTemplate) {
    this.htmlTemplate = pHtmlTemplate;
  }
}
