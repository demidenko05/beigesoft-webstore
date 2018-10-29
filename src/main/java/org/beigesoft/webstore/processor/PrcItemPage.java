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
import java.util.List;
import java.util.HashSet;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.model.IRequestData;
import org.beigesoft.log.ILogger;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.ServiceToSale;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.model.ESpecificsItemType;
import org.beigesoft.webstore.persistable.base.AItemSpecifics;
import org.beigesoft.webstore.persistable.base.AItemPrice;
import org.beigesoft.webstore.persistable.GoodsSpecifics;
import org.beigesoft.webstore.persistable.ServiceSpecifics;
import org.beigesoft.webstore.persistable.SeGoods;
import org.beigesoft.webstore.persistable.SeGoodsPlace;
import org.beigesoft.webstore.persistable.SeGoodsPrice;
import org.beigesoft.webstore.persistable.SeGoodsSpecifics;
import org.beigesoft.webstore.persistable.ServicePlace;
import org.beigesoft.webstore.persistable.ServicePrice;
import org.beigesoft.webstore.persistable.PriceGoods;
import org.beigesoft.webstore.persistable.GoodsPlace;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.service.ISrvShoppingCart;

/**
 * <p>Service that retrieve goods/service details. It passes itemPrice=null
 * in case of outdated or inconsistent price data.
 * JSP should handle wrong price or availability data.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcItemPage<RS> implements IProcessor {

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
   * <p>I18N query service specifics for service.</p>
   **/
  private String querySpecificsServiceDetailI18n;

  /**
   * <p>I18N query SeGoods specifics for SeGoods.</p>
   **/
  private String querySpecificsSeGoodsDetailI18n;

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
      processGoods(pReqVars, pRequestData);
    } else if (EShopItemType.SERVICE.toString().equals(itemTypeStr)) {
      processService(pReqVars, pRequestData);
    } else if (EShopItemType.SEGOODS.toString().equals(itemTypeStr)) {
      processSeGoods(pReqVars, pRequestData);
    } else {
      throw new Exception(
        "Detail page not yet implemented for item type: " + itemTypeStr);
    }
    String listFltAp = new String(pRequestData.getParameter("listFltAp")
      .getBytes("ISO-8859-1"), "UTF-8");
    pRequestData.setAttribute("listFltAp", listFltAp);
  }

  /**
   * <p>Process a goods from our warehouse.</p>
   * @param pReqVars additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  public final void processGoods(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    Long itemId = Long.valueOf(pRequestData.getParameter("itemId"));
    List<GoodsSpecifics> itemSpecLst;
    List<GoodsPlace> itemPlaceLst;
    PriceGoods itemPrice;
    itemSpecLst = retrieveItemSpecificsList(pReqVars, itemId,
      GoodsSpecifics.class, InvItem.class.getSimpleName());
    //extract main image if exist:
    int miIdx = -1;
    for (int i = 0; i < itemSpecLst.size(); i++) {
      if (itemSpecLst.get(i).getSpecifics().getItsType()
        .equals(ESpecificsItemType.IMAGE)) {
        pRequestData.setAttribute("itemImage", itemSpecLst.get(i));
        miIdx = i;
        break;
      }
    }
    if (miIdx != -1) {
      itemSpecLst.remove(miIdx);
    }
    itemPrice = retrieveItemPrice(pReqVars, itemId, PriceGoods.class);
    itemPlaceLst = getSrvOrm().retrieveListWithConditions(pReqVars,
        GoodsPlace.class, " where ITEM=" + itemId);
    if (pRequestData.getAttribute("shoppingCart") == null) {
      Cart shoppingCart = this.srvShoppingCart
        .getShoppingCart(pReqVars, pRequestData, false);
      if (shoppingCart != null) {
        pRequestData.setAttribute("shoppingCart", shoppingCart);
      }
    }
    if (pRequestData.getAttribute("shoppingCart") != null) {
      Cart shoppingCart = (Cart) pRequestData
        .getAttribute("shoppingCart");
      if (shoppingCart.getItems() != null) {
        String itemTypeStr = pRequestData.getParameter("itemType");
        for (CartLn ci : shoppingCart.getItems()) {
          if (!ci.getDisab() && ci.getItId().equals(itemId)
            && ci.getItTyp().toString().equals(itemTypeStr)) {
            pRequestData.setAttribute("cartItem", ci);
            break;
          }
        }
      }
    }
    pRequestData.setAttribute("itemSpecLst", itemSpecLst);
    pRequestData.setAttribute("itemPlaceLst", itemPlaceLst);
    pRequestData.setAttribute("itemPrice", itemPrice);
  }

  /**
   * <p>Process a seGood.</p>
   * @param pReqVars additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  public final void processSeGoods(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    Long itemId = Long.valueOf(pRequestData.getParameter("itemId"));
    List<SeGoodsSpecifics> itemSpecLst;
    List<SeGoodsPlace> itemPlaceLst;
    SeGoodsPrice itemPrice;
    itemSpecLst = retrieveItemSpecificsList(pReqVars, itemId,
      SeGoodsSpecifics.class, SeGoods.class.getSimpleName());
    //extract main image if exist:
    int miIdx = -1;
    for (int i = 0; i < itemSpecLst.size(); i++) {
      if (itemSpecLst.get(i).getSpecifics().getItsType()
        .equals(ESpecificsItemType.IMAGE)) {
        pRequestData.setAttribute("itemImage", itemSpecLst.get(i));
        miIdx = i;
        break;
      }
    }
    if (miIdx != -1) {
      itemSpecLst.remove(miIdx);
    }
    itemPrice = retrieveItemPrice(pReqVars, itemId, SeGoodsPrice.class);
    itemPlaceLst = getSrvOrm().retrieveListWithConditions(pReqVars,
        SeGoodsPlace.class, " where ITEM=" + itemId);
    if (pRequestData.getAttribute("shoppingCart") == null) {
      Cart shoppingCart = this.srvShoppingCart
        .getShoppingCart(pReqVars, pRequestData, false);
      if (shoppingCart != null) {
        pRequestData.setAttribute("shoppingCart", shoppingCart);
      }
    }
    if (pRequestData.getAttribute("shoppingCart") != null) {
      Cart shoppingCart = (Cart) pRequestData
        .getAttribute("shoppingCart");
      if (shoppingCart.getItems() != null) {
        String itemTypeStr = pRequestData.getParameter("itemType");
        for (CartLn ci : shoppingCart.getItems()) {
          if (!ci.getDisab() && ci.getItId().equals(itemId)
            && ci.getItTyp().toString().equals(itemTypeStr)) {
            pRequestData.setAttribute("cartItem", ci);
            break;
          }
        }
      }
    }
    pRequestData.setAttribute("itemSpecLst", itemSpecLst);
    pRequestData.setAttribute("itemPlaceLst", itemPlaceLst);
    pRequestData.setAttribute("itemPrice", itemPrice);
  }

  /**
   * <p>Process a service.</p>
   * @param pReqVars additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  public final void processService(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    Long itemId = Long.valueOf(pRequestData.getParameter("itemId"));
    List<ServiceSpecifics> itemSpecLst;
    List<ServicePlace> itemPlaceLst;
    ServicePrice itemPrice;
    itemSpecLst = retrieveItemSpecificsList(pReqVars, itemId,
      ServiceSpecifics.class, ServiceToSale.class.getSimpleName());
    //extract main image if exist:
    int miIdx = -1;
    for (int i = 0; i < itemSpecLst.size(); i++) {
      if (itemSpecLst.get(i).getSpecifics().getItsType()
        .equals(ESpecificsItemType.IMAGE)) {
        pRequestData.setAttribute("itemImage", itemSpecLst.get(i));
        miIdx = i;
        break;
      }
    }
    if (miIdx != -1) {
      itemSpecLst.remove(miIdx);
    }
    itemPrice = retrieveItemPrice(pReqVars, itemId, ServicePrice.class);
    itemPlaceLst = getSrvOrm().retrieveListWithConditions(pReqVars,
        ServicePlace.class, " where ITEM=" + itemId);
    if (pRequestData.getAttribute("shoppingCart") == null) {
      Cart shoppingCart = this.srvShoppingCart
        .getShoppingCart(pReqVars, pRequestData, false);
      if (shoppingCart != null) {
        pRequestData.setAttribute("shoppingCart", shoppingCart);
      }
    }
    if (pRequestData.getAttribute("shoppingCart") != null) {
      Cart shoppingCart = (Cart) pRequestData
        .getAttribute("shoppingCart");
      if (shoppingCart.getItems() != null) {
        String itemTypeStr = pRequestData.getParameter("itemType");
        for (CartLn ci : shoppingCart.getItems()) {
          if (!ci.getDisab() && ci.getItId().equals(itemId)
            && ci.getItTyp().toString().equals(itemTypeStr)) {
            pRequestData.setAttribute("cartItem", ci);
            break;
          }
        }
      }
    }
    pRequestData.setAttribute("itemSpecLst", itemSpecLst);
    pRequestData.setAttribute("itemPlaceLst", itemPlaceLst);
    pRequestData.setAttribute("itemPrice", itemPrice);
  }

  /**
   * <p>Retrieve item price.</p>
   * @param <T> item price type
   * @param pReqVars additional param
   * @param pItemId item ID
   * @param pItemPriceCl item price class
   * @return item price, null in case of price mistakes
   * @throws Exception - an exception
   **/
  public final <T extends AItemPrice<?, ?>> T retrieveItemPrice(
    final Map<String, Object> pReqVars, final Long pItemId,
      final Class<T> pItemPriceCl) throws Exception {
    TradingSettings ts = (TradingSettings) pReqVars.get("tradingSettings");
    if (ts.getIsUsePriceForCustomer()) {
      throw new Exception(
        "Method price depends of customer's category not yet implemented!");
    }
    // same price for all customers - only record exist:
    List<T> lst = getSrvOrm().retrieveListWithConditions(pReqVars,
          pItemPriceCl, " where ITEM=" + pItemId);
    if (lst.size() == 1) {
      return lst.get(0);
    } else {
      String itemName;
      if (lst.size() > 0) {
        itemName = lst.get(0).getItem().getItsName();
      } else {
        itemName = "?";
      }
      this.logger.error(null, PrcItemPage.class,
        "It must be only price for goods/service: ID/Name/prices count/class"
          + pItemId + "/" + itemName + "/" + lst.size() + "/" + pItemPriceCl);
      return null;
    }
  }

  /**
   * <p>Retrieve Item Specifics list for item.</p>
   * @param <T> item type
   * @param pReqVars additional param
   * @param pItemId item ID
   * @param pItemSpecCl item specifics class
   * @param pItemSn item simple name
   * @return Item Specifics list
   * @throws Exception - an exception
   **/
  public final <T extends AItemSpecifics<?, ?>> List<T>
    retrieveItemSpecificsList(final Map<String, Object> pReqVars,
      final Long pItemId, final Class<T> pItemSpecCl,
        final String pItemSn) throws Exception {
    //HTML templates full
    pReqVars.put(pItemSpecCl.getSimpleName() + "specificsdeepLevel", 3);
    HashSet<String> goodsFldNms = new HashSet<String>();
    goodsFldNms.add("itsId");
    goodsFldNms.add("itsName");
    pReqVars.put(pItemSn + "neededFields", goodsFldNms);
    HashSet<String> soiFldNms = new HashSet<String>();
    soiFldNms.add("itsId");
    soiFldNms.add("itsName");
    soiFldNms.add("isShowInList");
    soiFldNms.add("itsType");
    soiFldNms.add("itsGroop");
    soiFldNms.add("tempHtml");
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
    TradingSettings tradingSettings = (TradingSettings)
      pReqVars.get("tradingSettings");
    List<T> result = null;
    if (tradingSettings.getUseAdvancedI18n()) {
      String lang = (String) pReqVars.get("lang");
      String langDef = (String) pReqVars.get("langDef");
      if (!lang.equals(langDef)) {
        String qd;
        if (pItemSpecCl == GoodsSpecifics.class) {
          qd = lazyGetQuerySpecificsGoodsDetailI18n()
            .replace(":ITEMID", pItemId.toString()).replace(":LANG", lang);
        } else if (pItemSpecCl == ServiceSpecifics.class) {
          qd = lazyGetQuerySpecificsServiceDetailI18n()
            .replace(":ITEMID", pItemId.toString()).replace(":LANG", lang);
        } else if (pItemSpecCl == SeGoodsSpecifics.class) {
          qd = lazyGetQuerySpecificsSeGoodsDetailI18n()
            .replace(":ITEMID", pItemId.toString()).replace(":LANG", lang);
        } else {
          throw new Exception("NYI for " +  pItemSpecCl);
        }
        result = getSrvOrm().retrieveListByQuery(pReqVars,
          pItemSpecCl, qd);
      }
    }
    if (result == null) {
      result = getSrvOrm().retrieveListWithConditions(pReqVars,
        pItemSpecCl, " where " + pItemSpecCl.getSimpleName()
          .toUpperCase() + ".ITEM=" + pItemId + " order by SPECIFICS.ITSINDEX");
    }
    pReqVars.remove(pItemSpecCl.getSimpleName() + "specificsdeepLevel");
    pReqVars.remove(pItemSn + "neededFields");
    pReqVars.remove("SpecificsOfItemneededFields");
    pReqVars.remove("SpecificsOfItemGroupneededFields");
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
   * <p>Lazy Get querySpecificsSeGoodsDetailI18n.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String
    lazyGetQuerySpecificsSeGoodsDetailI18n() throws Exception {
    if (this.querySpecificsSeGoodsDetailI18n == null) {
      String flName = "/webstore/seGdSpecDetI18n.sql";
      this.querySpecificsSeGoodsDetailI18n = loadString(flName);
    }
    return this.querySpecificsSeGoodsDetailI18n;
  }

  /**
   * <p>Lazy Get querySpecificsServiceDetailI18n.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String
    lazyGetQuerySpecificsServiceDetailI18n() throws Exception {
    if (this.querySpecificsServiceDetailI18n == null) {
      String flName = "/webstore/serviceSpecificsDetailI18n.sql";
      this.querySpecificsServiceDetailI18n = loadString(flName);
    }
    return this.querySpecificsServiceDetailI18n;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = PrcItemPage.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = PrcItemPage.class
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
   * <p>Setter for querySpecificsServiceDetailI18n.</p>
   * @param pQuerySpecificsServiceDetailI18n reference
   **/
  public final void setQuerySpecificsServiceDetailI18n(
    final String pQuerySpecificsServiceDetailI18n) {
    this.querySpecificsServiceDetailI18n = pQuerySpecificsServiceDetailI18n;
  }

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
