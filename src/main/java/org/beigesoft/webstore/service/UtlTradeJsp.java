package org.beigesoft.webstore.service;

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

import java.util.List;

import org.beigesoft.webstore.persistable.CatalogGs;
import org.beigesoft.webstore.persistable.I18nCatalogGs;
import org.beigesoft.webstore.persistable.I18nWebStore;
import org.beigesoft.webstore.persistable.TradingSettings;

/**
 * <p>Helper that is used in JSP. E.g. it makes catalog name by given CatalogGs,
 * List&lt;I18nCatalogGs&gt; and language.</p>
 *
 * @author Yury Demidenko
 */
public class UtlTradeJsp {

  /**
   * <p>It makes catalog name.</p>
   * @param pCatalog Catalog
   * @param pI18nCatalogs I18n Catalogs
   * @param pLang language
   * @return catalog name
   **/
  public final String catalogToStr(
    final CatalogGs pCatalog,
      final List<I18nCatalogGs> pI18nCatalogs, final String pLang) {
    if (pI18nCatalogs != null) {
      for (I18nCatalogGs icat : pI18nCatalogs) {
        if (icat.getLang().getItsId().equals(pLang) && pCatalog.getItsId()
          .equals(icat.getHasName().getItsId())) {
          return icat.getItsName();
        }
      }
    }
    return pCatalog.getItsName();
  }

  /**
   * <p>It makes webstore name.</p>
   * @param pTradingSettings Trading Settings
   * @param pI18nWebStoreList I18n WebStore List
   * @param pLang language
   * @return catalog name
   **/
  public final String webstoreName(
    final TradingSettings pTradingSettings,
      final List<I18nWebStore> pI18nWebStoreList, final String pLang) {
    if (pI18nWebStoreList != null) {
      for (I18nWebStore iws : pI18nWebStoreList) {
        if (iws.getLang().getItsId().equals(pLang)) {
          return iws.getWebStoreName();
        }
      }
    }
    return pTradingSettings.getWebStoreName();
  }
}
