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

import org.beigesoft.model.IRequestData;
import org.beigesoft.log.ILogger;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.persistable.GoodsSpecific;
import org.beigesoft.webstore.persistable.GoodsPrice;
import org.beigesoft.webstore.persistable.GoodsAvailable;
import org.beigesoft.webstore.persistable.CartItem;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.ShoppingCart;
import org.beigesoft.webstore.service.ISrvTradingSettings;
import org.beigesoft.webstore.service.ISrvShoppingCart;

/**
 * <p>Service that retrieve goods/service details. It passes itemPrice=null
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
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Business service for trading settings.</p>
   **/
  private ISrvTradingSettings srvTradingSettings;

  /**
   * <p>Shopping Cart service.</p>
   **/
  private ISrvShoppingCart srvShoppingCart;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pAddParam,
    final IRequestData pRequestData) throws Exception {
    String itemTypeStr = pRequestData.getParameter("itemType");
    if (EShopItemType.GOODS.toString().equals(itemTypeStr)) {
      processInvItem(pAddParam, pRequestData);
    } else {
      throw new Exception(
        "Detail page not yet implemented for item type: " + itemTypeStr);
    }
    pRequestData.setAttribute("accSettings",
      this.srvAccSettings.lazyGetAccSettings(pAddParam));
    pRequestData.setAttribute("tradingSettings", this.srvTradingSettings
      .lazyGetTradingSettings(pAddParam));
  }

  /**
   * <p>Process a goods from our warehouse.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  public final void processInvItem(final Map<String, Object> pAddParam,
    final IRequestData pRequestData) throws Exception {
    Long itemId = Long.valueOf(pRequestData.getParameter("itemId"));
    List<GoodsSpecific> specificsList;
    List<GoodsAvailable> availabilityList;
    GoodsPrice itemPrice;
    specificsList = retrieveGoodsSpecifics(pAddParam, itemId);
    itemPrice = retrieveGoodsPrice(pAddParam, itemId);
    availabilityList = getSrvOrm().retrieveListWithConditions(pAddParam,
        GoodsAvailable.class, " where goods=" + itemId);
    if (pRequestData.getAttribute("shoppingCart") == null) {
      ShoppingCart shoppingCart = this.srvShoppingCart
        .getShoppingCart(pAddParam, pRequestData, false);
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
    pRequestData.setAttribute("specificsList", specificsList);
    pRequestData.setAttribute("availabilityList", availabilityList);
    pRequestData.setAttribute("itemPrice", itemPrice);
  }

  /**
   * <p>Retrieve goods price.</p>
   * @param pAddParam additional param
   * @param pItemId goods ID
   * @return Goods price, null in case of price mistakes
   * @throws Exception - an exception
   **/
  public final GoodsPrice retrieveGoodsPrice(
    final Map<String, Object> pAddParam, final Long pItemId) throws Exception {
    TradingSettings ts = this.srvTradingSettings
      .lazyGetTradingSettings(pAddParam);
    if (ts.getIsUsePriceForCustomer()) {
      throw new Exception(
        "Method price depends of customer's category not yet implemented!");
    }
    // same price for all customers - only record exist:
    List<GoodsPrice> lst = getSrvOrm().retrieveListWithConditions(pAddParam,
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
   * @param pAddParam additional param
   * @param pItemId goods ID
   * @return GoodsSpecific list
   * @throws Exception - an exception
   **/
  public final List<GoodsSpecific> retrieveGoodsSpecifics(
    final Map<String, Object> pAddParam, final Long pItemId) throws Exception {
    pAddParam.put("GoodsSpecificspecificsdeepLevel", 4); //HTML templates full
    HashSet<String> goodsFldNms = new HashSet<String>();
    goodsFldNms.add("itsId");
    goodsFldNms.add("itsName");
    pAddParam.put("InvItemneededFields", goodsFldNms);
    HashSet<String> soiFldNms = new HashSet<String>();
    soiFldNms.add("itsId");
    soiFldNms.add("itsName");
    soiFldNms.add("isShowInList");
    soiFldNms.add("itsType");
    soiFldNms.add("itsGroop");
    soiFldNms.add("chooseableSpecificsType");
    pAddParam.put("SpecificsOfItemneededFields", soiFldNms);
    HashSet<String> soigFldNms = new HashSet<String>();
    soigFldNms.add("itsId");
    soigFldNms.add("itsName");
    soigFldNms.add("templateStart");
    soigFldNms.add("templateEnd");
    soigFldNms.add("templateDetail");
    pAddParam.put("SpecificsOfItemGroupneededFields", soigFldNms);
    HashSet<String> chsptpFldNms = new HashSet<String>();
    chsptpFldNms.add("itsId");
    chsptpFldNms.add("htmlTemplate");
    pAddParam.put("ChooseableSpecificsTypeneededFields", chsptpFldNms);
    List<GoodsSpecific> result = getSrvOrm()
      .retrieveListWithConditions(pAddParam, GoodsSpecific.class,
        " where GOODSSPECIFIC.GOODS =" + pItemId
          + " order by SPECIFICS.ITSINDEX");
    pAddParam.remove("GoodsSpecificspecificsdeepLevel");
    pAddParam.remove("InvItemneededFields");
    pAddParam.remove("SpecificsOfItemneededFields");
    pAddParam.remove("SpecificsOfItemGroupneededFields");
    pAddParam.remove("ChooseableSpecificsTypeneededFields");
    return result;
  }

  //Simple getters and setters:
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
   * <p>Getter for srvAccSettings.</p>
   * @return ISrvAccSettings
   **/
  public final ISrvAccSettings getSrvAccSettings() {
    return this.srvAccSettings;
  }

  /**
   * <p>Setter for srvAccSettings.</p>
   * @param pSrvAccSettings reference
   **/
  public final void setSrvAccSettings(final ISrvAccSettings pSrvAccSettings) {
    this.srvAccSettings = pSrvAccSettings;
  }

  /**
   * <p>Getter for srvTradingSettings.</p>
   * @return ISrvTradingSettings
   **/
  public final ISrvTradingSettings getSrvTradingSettings() {
    return this.srvTradingSettings;
  }

  /**
   * <p>Setter for srvTradingSettings.</p>
   * @param pSrvTradingSettings reference
   **/
  public final void setSrvTradingSettings(
    final ISrvTradingSettings pSrvTradingSettings) {
    this.srvTradingSettings = pSrvTradingSettings;
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
