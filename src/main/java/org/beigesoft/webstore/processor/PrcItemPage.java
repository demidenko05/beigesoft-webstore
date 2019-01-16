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
import java.math.BigDecimal;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.model.IRequestData;
import org.beigesoft.log.ILogger;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.ServiceToSale;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.model.ESpecificsItemType;
import org.beigesoft.webstore.persistable.base.AItemSpecifics;
import org.beigesoft.webstore.persistable.base.AItemPrice;
import org.beigesoft.webstore.persistable.GoodsSpecifics;
import org.beigesoft.webstore.persistable.ServiceSpecifics;
import org.beigesoft.webstore.persistable.SeGoods;
import org.beigesoft.webstore.persistable.SeGoodsPlace;
import org.beigesoft.webstore.persistable.SeGoodsSpecifics;
import org.beigesoft.webstore.persistable.ServicePlace;
import org.beigesoft.webstore.persistable.GoodsPlace;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.OnlineBuyer;
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
  private ISrvShoppingCart srvCart;

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
   * @param pRqVs additional param
   * @param pRqDt Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pRqVs,
    final IRequestData pRqDt) throws Exception {
    TradingSettings ts = (TradingSettings) pRqVs.get("tradSet");
    AccSettings as = (AccSettings) pRqVs.get("accSet");
    String itemTypeStr = pRqDt.getParameter("itemType");
    Long itemId = Long.valueOf(pRqDt.getParameter("itemId"));
    if (pRqDt.getAttribute("cart") == null) {
      Cart cart = this.srvCart
        .getShoppingCart(pRqVs, pRqDt, false, false);
      if (cart != null) {
        pRqDt.setAttribute("cart", cart);
        if (pRqDt.getAttribute("txRules") == null) {
          TaxDestination txRules = this.srvCart
            .revealTaxRules(pRqVs, cart, as);
          pRqDt.setAttribute("txRules", txRules);
        }
      }
    }
    OnlineBuyer buyr = null;
    if (pRqDt.getAttribute("cart") != null) {
      Cart cart = (Cart) pRqDt.getAttribute("cart");
      buyr = cart.getBuyer();
      if (cart.getTot().compareTo(BigDecimal.ZERO) == 0) {
        pRqDt.setAttribute("cart", null);
      } else {
        if (cart.getItems() != null) {
          for (CartLn ci : cart.getItems()) {
            if (!ci.getDisab() && ci.getItId().equals(itemId)
              && ci.getItTyp().toString().equals(itemTypeStr)) {
              pRqDt.setAttribute("cartItem", ci);
              break;
            }
          }
        }
      }
    } else {
      buyr = (OnlineBuyer) pRqDt.getAttribute("buyr");
      if (buyr == null) {
        String buyerIdStr = pRqDt.getCookieValue("cBuyerId");
        if (buyerIdStr != null && buyerIdStr.length() > 0) {
          Long buyerId = Long.valueOf(buyerIdStr);
          buyr = getSrvOrm()
            .retrieveEntityById(pRqVs, OnlineBuyer.class, buyerId);
        }
        pRqDt.setAttribute("buyr", buyr);
      }
    }
    if (EShopItemType.GOODS.toString().equals(itemTypeStr)) {
      processGoods(pRqVs, pRqDt, ts, buyr, itemId);
    } else if (EShopItemType.SERVICE.toString().equals(itemTypeStr)) {
      processService(pRqVs, pRqDt, ts, buyr, itemId);
    } else if (EShopItemType.SEGOODS.toString().equals(itemTypeStr)) {
      processSeGoods(pRqVs, pRqDt, ts, buyr, itemId);
    } else {
      throw new Exception(
        "Detail page not yet implemented for item type: " + itemTypeStr);
    }
    String listFltAp = new String(pRqDt.getParameter("listFltAp")
      .getBytes("ISO-8859-1"), "UTF-8");
    pRqDt.setAttribute("listFltAp", listFltAp);
  }

  /**
   * <p>Process a goods from our warehouse.</p>
   * @param pRqVs additional param
   * @param pRqDt Request Data
   * @param pTs TradingSettings
   * @param pBuyer Buyer
   * @param pItemId Item ID
   * @throws Exception - an exception
   **/
  public final void processGoods(final Map<String, Object> pRqVs,
    final IRequestData pRqDt, final TradingSettings pTs,
      final OnlineBuyer pBuyer, final Long pItemId) throws Exception {
    List<GoodsSpecifics> itemSpecLst;
    List<GoodsPlace> itemPlaceLst;
    itemSpecLst = retrieveItemSpecificsList(pRqVs, pTs, pItemId,
      GoodsSpecifics.class, InvItem.class.getSimpleName());
    //extract main image if exist:
    int miIdx = -1;
    for (int i = 0; i < itemSpecLst.size(); i++) {
      if (itemSpecLst.get(i).getSpecifics().getItsType()
        .equals(ESpecificsItemType.IMAGE)) {
        pRqDt.setAttribute("itemImage", itemSpecLst.get(i));
        miIdx = i;
        break;
      }
    }
    if (miIdx != -1) {
      itemSpecLst.remove(miIdx);
    }
    AItemPrice<?, ?> itemPrice = getSrvCart().revealItemPrice(pRqVs, pTs,
      pBuyer, EShopItemType.GOODS, pItemId);
    itemPlaceLst = getSrvOrm().retrieveListWithConditions(pRqVs,
        GoodsPlace.class, " where ITEM=" + pItemId);
    pRqDt.setAttribute("itemSpecLst", itemSpecLst);
    pRqDt.setAttribute("itemPlaceLst", itemPlaceLst);
    pRqDt.setAttribute("itemPrice", itemPrice);
  }

  /**
   * <p>Process a seGood.</p>
   * @param pRqVs additional param
   * @param pRqDt Request Data
   * @param pTs TradingSettings
   * @param pBuyer Buyer
   * @param pItemId Item ID
   * @throws Exception - an exception
   **/
  public final void processSeGoods(final Map<String, Object> pRqVs,
    final IRequestData pRqDt, final TradingSettings pTs,
      final OnlineBuyer pBuyer, final Long pItemId) throws Exception {
    List<SeGoodsSpecifics> itemSpecLst;
    List<SeGoodsPlace> itemPlaceLst;
    itemSpecLst = retrieveItemSpecificsList(pRqVs, pTs, pItemId,
      SeGoodsSpecifics.class, SeGoods.class.getSimpleName());
    //extract main image if exist:
    int miIdx = -1;
    for (int i = 0; i < itemSpecLst.size(); i++) {
      if (itemSpecLst.get(i).getSpecifics().getItsType()
        .equals(ESpecificsItemType.IMAGE)) {
        pRqDt.setAttribute("itemImage", itemSpecLst.get(i));
        miIdx = i;
        break;
      }
    }
    if (miIdx != -1) {
      itemSpecLst.remove(miIdx);
    }
    AItemPrice<?, ?> itemPrice = getSrvCart().revealItemPrice(pRqVs, pTs,
        pBuyer, EShopItemType.SEGOODS, pItemId);
    itemPlaceLst = getSrvOrm().retrieveListWithConditions(pRqVs,
        SeGoodsPlace.class, " where ITEM=" + pItemId);
    pRqDt.setAttribute("itemSpecLst", itemSpecLst);
    pRqDt.setAttribute("itemPlaceLst", itemPlaceLst);
    pRqDt.setAttribute("itemPrice", itemPrice);
  }

  /**
   * <p>Process a service.</p>
   * @param pRqVs additional param
   * @param pRqDt Request Data
   * @param pTs TradingSettings
   * @param pBuyer Buyer
   * @param pItemId Item ID
   * @throws Exception - an exception
   **/
  public final void processService(final Map<String, Object> pRqVs,
    final IRequestData pRqDt, final TradingSettings pTs,
      final OnlineBuyer pBuyer, final Long pItemId) throws Exception {
    List<ServiceSpecifics> itemSpecLst;
    List<ServicePlace> itemPlaceLst;
    itemSpecLst = retrieveItemSpecificsList(pRqVs, pTs, pItemId,
      ServiceSpecifics.class, ServiceToSale.class.getSimpleName());
    //extract main image if exist:
    int miIdx = -1;
    for (int i = 0; i < itemSpecLst.size(); i++) {
      if (itemSpecLst.get(i).getSpecifics().getItsType()
        .equals(ESpecificsItemType.IMAGE)) {
        pRqDt.setAttribute("itemImage", itemSpecLst.get(i));
        miIdx = i;
        break;
      }
    }
    if (miIdx != -1) {
      itemSpecLst.remove(miIdx);
    }
    AItemPrice<?, ?> itemPrice = getSrvCart().revealItemPrice(pRqVs, pTs,
      pBuyer, EShopItemType.SERVICE, pItemId);
    itemPlaceLst = getSrvOrm().retrieveListWithConditions(pRqVs,
        ServicePlace.class, " where ITEM=" + pItemId);
    pRqDt.setAttribute("itemSpecLst", itemSpecLst);
    pRqDt.setAttribute("itemPlaceLst", itemPlaceLst);
    pRqDt.setAttribute("itemPrice", itemPrice);
  }

  /**
   * <p>Retrieve Item Specifics list for item.</p>
   * @param <T> item type
   * @param pRqVs additional param
   * @param pTs TradingSettings
   * @param pItemId item ID
   * @param pItemSpecCl item specifics class
   * @param pItemSn item simple name
   * @return Item Specifics list
   * @throws Exception - an exception
   **/
  public final <T extends AItemSpecifics<?, ?>> List<T>
    retrieveItemSpecificsList(final Map<String, Object> pRqVs,
      final TradingSettings pTs, final Long pItemId, final Class<T> pItemSpecCl,
        final String pItemSn) throws Exception {
    //HTML templates full
    pRqVs.put(pItemSpecCl.getSimpleName() + "specificsdeepLevel", 3);
    HashSet<String> goodsFldNms = new HashSet<String>();
    goodsFldNms.add("itsId");
    goodsFldNms.add("itsName");
    pRqVs.put(pItemSn + "neededFields", goodsFldNms);
    HashSet<String> soiFldNms = new HashSet<String>();
    soiFldNms.add("itsId");
    soiFldNms.add("itsName");
    soiFldNms.add("isShowInList");
    soiFldNms.add("itsType");
    soiFldNms.add("itsGroop");
    soiFldNms.add("tempHtml");
    pRqVs.put("SpecificsOfItemneededFields", soiFldNms);
    HashSet<String> soigFldNms = new HashSet<String>();
    soigFldNms.add("itsId");
    soigFldNms.add("itsName");
    soigFldNms.add("templateStart");
    soigFldNms.add("templateEnd");
    soigFldNms.add("templateDetail");
    pRqVs.put("SpecificsOfItemGroupneededFields", soigFldNms);
    HashSet<String> htmTmFldNms = new HashSet<String>();
    htmTmFldNms.add("itsId");
    htmTmFldNms.add("htmlTemplate");
    pRqVs.put("HtmlTemplateneededFields", htmTmFldNms);
    List<T> result = null;
    if (pTs.getUseAdvancedI18n()) {
      String lang = (String) pRqVs.get("lang");
      String langDef = (String) pRqVs.get("langDef");
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
        result = getSrvOrm().retrieveListByQuery(pRqVs,
          pItemSpecCl, qd);
      }
    }
    if (result == null) {
      result = getSrvOrm().retrieveListWithConditions(pRqVs,
        pItemSpecCl, " where " + pItemSpecCl.getSimpleName()
          .toUpperCase() + ".ITEM=" + pItemId + " order by SPECIFICS.ITSINDEX");
    }
    pRqVs.remove(pItemSpecCl.getSimpleName() + "specificsdeepLevel");
    pRqVs.remove(pItemSn + "neededFields");
    pRqVs.remove("SpecificsOfItemneededFields");
    pRqVs.remove("SpecificsOfItemGroupneededFields");
    pRqVs.remove("HtmlTemplateneededFields");
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
   * <p>Getter for srvCart.</p>
   * @return ISrvShoppingCart
   **/
  public final ISrvShoppingCart getSrvCart() {
    return this.srvCart;
  }

  /**
   * <p>Setter for srvCart.</p>
   * @param pSrvCart reference
   **/
  public final void setSrvCart(
    final ISrvShoppingCart pSrvCart) {
    this.srvCart = pSrvCart;
  }
}
