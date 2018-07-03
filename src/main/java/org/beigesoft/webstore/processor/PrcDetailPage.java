package org.beigesoft.webstore.processor;

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

import java.util.Map;
import java.util.List;
import java.util.HashSet;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.model.IRequestData;
import org.beigesoft.log.ILogger;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.model.ESpecificsItemType;
import org.beigesoft.webstore.persistable.GoodsSpecific;
import org.beigesoft.webstore.persistable.GoodsPrice;
import org.beigesoft.webstore.persistable.GoodsAvailable;
import org.beigesoft.webstore.persistable.CartItem;
import org.beigesoft.webstore.persistable.ShoppingCart;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.service.ISrvShoppingCart;

/**
 * <p>Service that retrieve goods/service details. It passes goodsPrice=null
 * in case of outdated or inconsistent price data.
 * JSP should handle wrong price or availability data.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcDetailPage<RS> implements IProcessor {

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Shopping Cart service.</p>
   **/
  private ISrvShoppingCart srvShoppingCart;

  /**
   * <p>I18N query goods specifics for goods.</p>
   **/
  private String querySpecificsGoodsDetailI18n;

  /**
   * <p>Process entity request.</p>
   * @param pReqVars additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    String itemTypeStr = pRequestData.getParameter("itemType");
    if (EShopItemType.GOODS.toString().equals(itemTypeStr)) {
      processInvItem(pReqVars, pRequestData);
    } else {
      throw new Exception(
        "Detail page not yet implemented for item type: " + itemTypeStr);
    }
  }

  /**
   * <p>Process a goods from our warehouse.</p>
   * @param pReqVars additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  public final void processInvItem(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    Long itemId = Long.valueOf(pRequestData.getParameter("itemId"));
    List<GoodsSpecific> gsList;
    List<GoodsAvailable> gaList;
    GoodsPrice goodsPrice;
    gsList = retrieveGoodsSpecifics(pReqVars, itemId);
    //extract main image if exist:
    int miIdx = -1;
    for (int i = 0; i < gsList.size(); i++) {
      if (gsList.get(i).getSpecifics().getItsType()
        .equals(ESpecificsItemType.IMAGE)) {
        pRequestData.setAttribute("gsMainImage", gsList.get(i));
        miIdx = i;
        break;
      }
    }
    if (miIdx != -1) {
      gsList.remove(miIdx);
    }
    goodsPrice = retrieveGoodsPrice(pReqVars, itemId);
    gaList = getSrvOrm().retrieveListWithConditions(pReqVars,
        GoodsAvailable.class, " where goods=" + itemId);
    if (pRequestData.getAttribute("shoppingCart") == null) {
      ShoppingCart shoppingCart = this.srvShoppingCart
        .getShoppingCart(pReqVars, pRequestData, false);
      if (shoppingCart != null) {
        pRequestData.setAttribute("shoppingCart", shoppingCart);
      }
    }
    if (pRequestData.getAttribute("shoppingCart") != null) {
      ShoppingCart shoppingCart = (ShoppingCart) pRequestData
        .getAttribute("shoppingCart");
      if (shoppingCart.getItsItems() != null) {
        String itemTypeStr = pRequestData.getParameter("itemType");
        for (CartItem ci : shoppingCart.getItsItems()) {
          if (!ci.getIsDisabled() && ci.getItemId().equals(itemId)
            && ci.getItemType().toString().equals(itemTypeStr)) {
            pRequestData.setAttribute("cartItem", ci);
            break;
          }
        }
      }
    }
    pRequestData.setAttribute("gsList", gsList);
    pRequestData.setAttribute("gaList", gaList);
    pRequestData.setAttribute("goodsPrice", goodsPrice);
  }

  /**
   * <p>Retrieve goods price.</p>
   * @param pReqVars additional param
   * @param pItemId goods ID
   * @return Goods price, null in case of price mistakes
   * @throws Exception - an exception
   **/
  public final GoodsPrice retrieveGoodsPrice(
    final Map<String, Object> pReqVars, final Long pItemId) throws Exception {
    TradingSettings ts = (TradingSettings) pReqVars.get("tradingSettings");
    if (ts.getIsUsePriceForCustomer()) {
      throw new Exception(
        "Method price depends of customer's category not yet implemented!");
    }
    // same price for all customers - only record exist:
    List<GoodsPrice> lst = getSrvOrm().retrieveListWithConditions(pReqVars,
          GoodsPrice.class, " where goods=" + pItemId);
    if (lst.size() == 1) {
      return lst.get(0);
    } else {
      String itemName;
      if (lst.size() > 0) {
        itemName = lst.get(0).getGoods().getItsName();
      } else {
        itemName = "?";
      }
      this.logger.error(null, PrcDetailPage.class,
        "It must be only goods price for goods: ID/Name/prices count"
          + pItemId + "/" + itemName + "/" + lst.size());
      return null;
    }
  }

  /**
   * <p>Retrieve GoodsSpecific list for goods.</p>
   * @param pReqVars additional param
   * @param pItemId goods ID
   * @return GoodsSpecific list
   * @throws Exception - an exception
   **/
  public final List<GoodsSpecific> retrieveGoodsSpecifics(
    final Map<String, Object> pReqVars, final Long pItemId) throws Exception {
    pReqVars.put("GoodsSpecificspecificsdeepLevel", 3); //HTML templates full
    HashSet<String> goodsFldNms = new HashSet<String>();
    goodsFldNms.add("itsId");
    goodsFldNms.add("itsName");
    pReqVars.put("InvItemneededFields", goodsFldNms);
    HashSet<String> soiFldNms = new HashSet<String>();
    soiFldNms.add("itsId");
    soiFldNms.add("itsName");
    soiFldNms.add("isShowInList");
    soiFldNms.add("itsType");
    soiFldNms.add("itsGroop");
    soiFldNms.add("tempHtml");
    //soiFldNms.add("chooseableSpecificsType");
    pReqVars.put("SpecificsOfItemneededFields", soiFldNms);
    HashSet<String> soigFldNms = new HashSet<String>();
    soigFldNms.add("itsId");
    soigFldNms.add("itsName");
    soigFldNms.add("templateStart");
    soigFldNms.add("templateEnd");
    soigFldNms.add("templateDetail");
    pReqVars.put("SpecificsOfItemGroupneededFields", soigFldNms);
    HashSet<String> htmTmFldNms = new HashSet<String>();
    htmTmFldNms.add("itsId");
    htmTmFldNms.add("htmlTemplate");
    pReqVars.put("HtmlTemplateneededFields", htmTmFldNms);
    //HashSet<String> chsptpFldNms = new HashSet<String>();
    //chsptpFldNms.add("itsId");
    //pReqVars.put("ChooseableSpecificsTypeneededFields", chsptpFldNms);
    TradingSettings tradingSettings = (TradingSettings)
      pReqVars.get("tradingSettings");
    List<GoodsSpecific> result = null;
    if (tradingSettings.getUseAdvancedI18n()) {
      String lang = (String) pReqVars.get("lang");
      String langDef = (String) pReqVars.get("langDef");
      if (!lang.equals(langDef)) {
        String qd = lazyGetQuerySpecificsGoodsDetailI18n()
          .replace(":GOODSID", pItemId.toString()).replace(":LANG", lang);
        result = getSrvOrm().retrieveListByQuery(pReqVars,
          GoodsSpecific.class, qd);
      }
    }
    if (result == null) {
      result = getSrvOrm().retrieveListWithConditions(pReqVars,
        GoodsSpecific.class, " where GOODSSPECIFIC.GOODS=" + pItemId
          + " order by SPECIFICS.ITSINDEX");
    }
    pReqVars.remove("GoodsSpecificspecificsdeepLevel");
    pReqVars.remove("InvItemneededFields");
    pReqVars.remove("SpecificsOfItemneededFields");
    pReqVars.remove("SpecificsOfItemGroupneededFields");
    //pReqVars.remove("ChooseableSpecificsTypeneededFields");
    pReqVars.remove("HtmlTemplateneededFields");
    return result;
  }

  /**
   * <p>Lazy Get querySpecificsGoodsDetailI18n.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String
    lazyGetQuerySpecificsGoodsDetailI18n() throws Exception {
    if (this.querySpecificsGoodsDetailI18n == null) {
      String flName = "/webstore/specificsGoodsDetailI18n.sql";
      this.querySpecificsGoodsDetailI18n = loadString(flName);
    }
    return this.querySpecificsGoodsDetailI18n;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = PrcDetailPage.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = PrcDetailPage.class
          .getResourceAsStream(pFileName);
        byte[] bArray = new byte[inputStream.available()];
        inputStream.read(bArray, 0, inputStream.available());
        return new String(bArray, "UTF-8");
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
      }
    }
    return null;
  }

  //Simple getters and setters:
  /**
   * <p>Setter for querySpecificsGoodsDetailI18n.</p>
   * @param pQuerySpecificsGoodsDetailI18n reference
   **/
  public final void setQuerySpecificsGoodsDetailI18n(
    final String pQuerySpecificsGoodsDetailI18n) {
    this.querySpecificsGoodsDetailI18n = pQuerySpecificsGoodsDetailI18n;
  }

  /**
   * <p>Geter for logger.</p>
   * @return ILogger
   **/
  public final ILogger getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final void setLogger(final ILogger pLogger) {
    this.logger = pLogger;
  }

  /**
   * <p>Geter for srvOrm.</p>
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
   * <p>Getter for srvShoppingCart.</p>
   * @return ISrvShoppingCart
   **/
  public final ISrvShoppingCart getSrvShoppingCart() {
    return this.srvShoppingCart;
  }

  /**
   * <p>Setter for srvShoppingCart.</p>
   * @param pSrvShoppingCart reference
   **/
  public final void setSrvShoppingCart(
    final ISrvShoppingCart pSrvShoppingCart) {
    this.srvShoppingCart = pSrvShoppingCart;
  }
}
