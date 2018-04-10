package org.beigesoft.webstore.persistable;

/*
 * Copyright (c) 2018 Beigesoft ™
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
 * <p>
 * HTML template to render goods/service in HTML page.
 * </p>
 *
 * @author Yury Demidenko
 */
public class HtmlTemplate extends AHasNameIdLongVersion {

  /**
   * <p>HTML template, not null, HTML template e.g.
  *"<div class="gs-spec"><span class="gl-spec-spec">:VALUE1</span>:VALUE2</div>"
   * where :VALUE1/2 will be changed to actual ones.</p>
   **/
  private String htmlTemplate;

  //Simple getters and setters:
  /**
   * <p>Getter for htmlTemplate.</p>
   * @return String
   **/
  public final String getHtmlTemplate() {
    return this.htmlTemplate;
  }

  /**
   * <p>Setter for htmlTemplate.</p>
   * @param pHtmlTemplate reference
   **/
  public final void setHtmlTemplate(final String pHtmlTemplate) {
    this.htmlTemplate = pHtmlTemplate;
  }
}
