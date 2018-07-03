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
import java.util.Map;

import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.webstore.model.ESpecificsItemType;
import org.beigesoft.webstore.persistable.CatalogGs;
import org.beigesoft.webstore.persistable.I18nCatalogGs;
import org.beigesoft.webstore.persistable.I18nWebStore;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.SettingsAdd;
import org.beigesoft.webstore.persistable.GoodsSpecific;
import org.beigesoft.webstore.persistable.SpecificsOfItemGroup;

/**
 * <p>Helper that is used in JSP. E.g. it makes catalog name by given CatalogGs,
 * List&lt;I18nCatalogGs&gt; and language.</p>
 *
 * @author Yury Demidenko
 */
public class UtlTradeJsp {

  /**
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  /**
   * <p>It makes goods specifics string.</p>
   * @param pReqVars additional param
   * @param pGoodsSpecific GoodsSpecific list
   * @return appearance
   **/
  public final String goodsSpecificsStr(
    final Map<String, Object> pReqVars,
      final List<GoodsSpecific> pGoodsSpecific) {
    SettingsAdd settingsAdd = (SettingsAdd) pReqVars.get("settingsAdd");
    StringBuffer sb = new StringBuffer();
    boolean wasGrStart = false;
    SpecificsOfItemGroup specificsOfItemGroupWas = null;
    if (settingsAdd.getSpecHtmlStart() !=  null) {
      sb.append(settingsAdd.getSpecHtmlStart());
    }
    for (GoodsSpecific gs : pGoodsSpecific) {
      if (gs.getSpecifics().getIsShowInList() && !gs.getSpecifics().getItsType().equals(ESpecificsItemType.IMAGE)) {
        if (gs.getSpecifics().getItsGroop() == null || specificsOfItemGroupWas == null
          || !gs.getSpecifics().getItsGroop().getItsId().equals(specificsOfItemGroupWas.getItsId())) {
          if (wasGrStart) {
            if (settingsAdd.getSpecGrHtmlEnd() != null) {
              sb.append(settingsAdd.getSpecGrHtmlEnd());
            }
            if (settingsAdd.getSpecGrSeparator() != null) {
              sb.append(settingsAdd.getSpecGrSeparator());
            }
          }
          wasGrStart = true;
          if (settingsAdd.getSpecGrHtmlStart() != null) {
            sb.append(settingsAdd.getSpecGrHtmlStart());
          }
          if (gs.getSpecifics().getItsGroop() != null && gs.getSpecifics().getItsGroop().getTemplateStart() != null) {
            String grst = gs.getSpecifics().getItsGroop().getTemplateStart()
              .getHtmlTemplate().replace(":SPECGRNM", gs.getSpecifics().getItsGroop().getItsName());
            sb.append(grst);
          }
        }
        String val1 = "";
        String val2 = "";
        if (gs.getSpecifics().getItsType().equals(ESpecificsItemType.TEXT)) {
          val1 = gs.getStringValue1();
        } else if (gs.getSpecifics().getItsType().equals(ESpecificsItemType.BIGDECIMAL)) {
          val1 = srvNumberToString.print(gs.getNumericValue1().toString(),
            (String) pReqVars.get("dseparatorv"), (String) pReqVars
              .get("dgseparatorv"), Integer.valueOf(gs.getLongValue1()
                .intValue()), (Integer) pReqVars.get("digitsInGroup"));
          if (gs.getStringValue1() != null) {
            val2 = gs.getStringValue1();
          }
        } else if (gs.getSpecifics().getItsType().equals(ESpecificsItemType.INTEGER)) {
          val1 = gs.getLongValue1().toString();
          if (gs.getStringValue1() != null) {
            val2 = gs.getStringValue1();
          }
        } else if (gs.getSpecifics().getItsType().equals(ESpecificsItemType.CHOOSEABLE_SPECIFICS)) {
          val1 =  gs.getStringValue1();
        } else {
          continue;
        }
        String templateDetail;
        if (gs.getSpecifics().getTempHtml() != null) {
          templateDetail = gs.getSpecifics().getTempHtml().getHtmlTemplate();
        } else if (gs.getSpecifics().getItsGroop() != null && gs.getSpecifics().getItsGroop().getTemplateDetail() != null) {
          templateDetail = gs.getSpecifics().getItsGroop().getTemplateDetail().getHtmlTemplate();
        } else {
          templateDetail = " <b>:SPECNM:</b> :VAL1:VAL2";
        }
        String spdet = templateDetail.replace(":SPECNM", gs.getSpecifics().getItsName());
        spdet = spdet.replace(":VAL1", val1);
        spdet = spdet.replace(":VAL2", val2);
        if (gs.getSpecifics().getItsGroop() != null && specificsOfItemGroupWas != null
          && gs.getSpecifics().getItsGroop().getItsId().equals(specificsOfItemGroupWas.getItsId())) {
          sb.append(settingsAdd.getSpecSeparator() + spdet);
        } else {
          sb.append(spdet);
        }
        specificsOfItemGroupWas = gs.getSpecifics().getItsGroop();
      }
    }
    if (settingsAdd.getSpecGrHtmlEnd() != null) {
      sb.append(settingsAdd.getSpecGrHtmlEnd());
    }
    if (settingsAdd.getSpecHtmlEnd() != null) {
      sb.append(settingsAdd.getSpecHtmlEnd());
    }
    return sb.toString();
  }

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

  /**
   * <p>Getter for srvNumberToString.</p>
   * @return ISrvNumberToString
   **/
  public final ISrvNumberToString getSrvNumberToString() {
    return this.srvNumberToString;
  }

  /**
   * <p>Setter for srvNumberToString.</p>
   * @param pSrvNumberToString reference
   **/
  public final void setSrvNumberToString(
    final ISrvNumberToString pSrvNumberToString) {
    this.srvNumberToString = pSrvNumberToString;
  }
}
