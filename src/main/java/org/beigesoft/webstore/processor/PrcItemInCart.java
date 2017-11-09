package org.beigesoft.webstore.processor;

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

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IProcessor;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.persistable.ShoppingCart;
import org.beigesoft.webstore.persistable.CartItem;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.BuyerPriceCategory;
import org.beigesoft.webstore.persistable.GoodsPriceId;
import org.beigesoft.webstore.persistable.GoodsPrice;

/**
 * <p>Service that add item to cart or change quantity
 * (from modal dialog for single item).</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcItemInCart<RS> implements IProcessor {

  /**
   * <p>Query cart totals.</p>
   **/
  private String queryCartTotals;

  /**
   * <p>Processor Webstore Page.</p>
   **/
  private PrcWebstorePage<RS> prcWebstorePage;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pAddParam,
    final IRequestData pRequestData) throws Exception {
    TradingSettings tradingSettings = this.prcWebstorePage
      .getSrvTradingSettings().lazyGetTradingSettings(pAddParam);
    ShoppingCart shoppingCart = this.prcWebstorePage
      .getShoppingCart(pAddParam, pRequestData, true);
    CartItem cartItem = null;
    String cartItemItsIdStr = pRequestData.getParameter("cartItemItsId");
    String cartItemQuantityStr = pRequestData
      .getParameter("cartItemQuantity");
    String cartItemAvailableQuantityStr = pRequestData
      .getParameter("cartItemAvailableQuantity");
    BigDecimal cartItemQuantity = new BigDecimal(cartItemQuantityStr);
    BigDecimal cartItemAvailableQuantity =
      new BigDecimal(cartItemAvailableQuantityStr);
    String cartItemIdStr = pRequestData.getParameter("cartItemId");
    String cartItemTypeStr = pRequestData.getParameter("cartItemType");
    Long cartItemId = Long.valueOf(cartItemIdStr);
    EShopItemType cartItemType = EShopItemType.class.
      getEnumConstants()[Integer.parseInt(cartItemTypeStr)];
    if (cartItemItsIdStr != null) { //change quantity
      Long cartItemItsId = Long.valueOf(cartItemItsIdStr);
      cartItem = findCartItemById(shoppingCart, cartItemItsId);
    } else { //add
      if (shoppingCart.getItsItems() == null) {
        shoppingCart.setItsItems(new ArrayList<CartItem>());
        cartItem = createCartItem(shoppingCart);
      } else {
        for (CartItem ci : shoppingCart.getItsItems()) {
          //check for duplicate
          if (!ci.getIsDisabled() && ci.getItemType().equals(cartItemType)
            && ci.getItemId().equals(cartItemId)) {
            cartItem = ci;
            break;
          }
        }
        if (cartItem == null) {
          for (CartItem ci : shoppingCart.getItsItems()) {
            if (ci.getIsDisabled()) {
              cartItem = ci;
              cartItem.setIsDisabled(false);
              break;
            }
          }
        }
        if (cartItem == null) {
          cartItem = createCartItem(shoppingCart);
        }
      }
      cartItem.setItemId(cartItemId);
      cartItem.setItemType(cartItemType);
      BigDecimal price = null;
      if (tradingSettings.getIsUsePriceForCustomer()) {
        //try to reveal price dedicated for customer:
        List<BuyerPriceCategory> buyerPrCats = this.prcWebstorePage.getSrvOrm()
          .retrieveListWithConditions(pAddParam, BuyerPriceCategory.class,
            "where BUYER=" + shoppingCart.getBuyer().getItsId());
        for (BuyerPriceCategory buyerPrCat : buyerPrCats) {
          if (cartItemType.equals(EShopItemType.GOODS)) {
            InvItem goods = new InvItem();
            goods.setItsId(cartItemId);
            GoodsPriceId gpId = new GoodsPriceId();
            gpId.setGoods(goods);
            gpId.setPriceCategory(buyerPrCat.getPriceCategory());
            GoodsPrice goodsPrice = new GoodsPrice();
            goodsPrice.setItsId(gpId);
            goodsPrice = this.prcWebstorePage.getSrvOrm().
              retrieveEntity(pAddParam, goodsPrice);
            if (goodsPrice != null) {
              price = goodsPrice.getItsPrice();
              cartItem.setItsName(goodsPrice.getGoods().getItsName());
              break;
            }
          }
          //TODO Services, SE G/S
        }
      }
      if (price == null) {
        //retrieve price for all:
        if (cartItemType.equals(EShopItemType.GOODS)) {
          List<GoodsPrice> goodsPrices = this.prcWebstorePage.getSrvOrm().
            retrieveListWithConditions(pAddParam, GoodsPrice.class,
              "where GOODS=" + cartItemId);
          if (goodsPrices.size() == 0) {
            throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
              "requested_item_has_no_price");
          }
          if (goodsPrices.size() > 1) {
            throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
              "requested_item_has_several_prices");
          }
          price = goodsPrices.get(0).getItsPrice();
          cartItem.setItsName(goodsPrices.get(0).getGoods().getItsName());
        } else {
            throw new ExceptionWithCode(ExceptionWithCode.NOT_YET_IMPLEMENTED,
              "add_service_se_not_impl");
          //TODO Services, SE G/S
        }
      }
      cartItem.setItsPrice(price);
    }
    cartItem.setItsQuantity(cartItemQuantity);
    cartItem.setAvailableQuantity(cartItemAvailableQuantity);
    cartItem.setItsTotal(cartItem.getItsPrice()
      .multiply(cartItem.getItsQuantity()));
    if (cartItem.getIsNew()) {
      this.prcWebstorePage.getSrvOrm().insertEntity(pAddParam, cartItem);
    } else {
      this.prcWebstorePage.getSrvOrm().updateEntity(pAddParam, cartItem);
    }
    String query = lazyGetQueryCartTotals();
    query = query.replace(":CARTID", shoppingCart.getItsId()
      .getItsId().toString());
    String[] columns = new String[]{"ITSTOTAL", "TOTALITEMS"};
    Double[] totals = this.prcWebstorePage.getSrvDatabase()
      .evalDoubleResults(query, columns);
    if (totals[0] == null) {
      totals[0] = 0d;
    }
    if (totals[1] == null) {
      totals[1] = 0d;
    }
    AccSettings accSettings = this.prcWebstorePage.getSrvAccSettings()
      .lazyGetAccSettings(pAddParam);
    shoppingCart.setItsTotal(BigDecimal.valueOf(totals[0]).
      setScale(accSettings.getPricePrecision(), accSettings.getRoundingMode()));
    shoppingCart.setTotalItems(totals[1].intValue());
    this.prcWebstorePage.getSrvOrm().updateEntity(pAddParam, shoppingCart);
    pRequestData.setAttribute("shoppingCart", shoppingCart);
    this.prcWebstorePage.process(pAddParam, pRequestData);
  }

  /**
   * <p>Find cart item by ID.</p>
   * @param pShoppingCart cart
   * @param pCartItemItsId cart item ID
   * @return cart item
   * @throws Exception - an exception
   **/
  public final CartItem findCartItemById(final ShoppingCart pShoppingCart,
    final Long pCartItemItsId) throws Exception {
    CartItem cartItem = null;
    for (CartItem ci : pShoppingCart.getItsItems()) {
      if (ci.getItsId().equals(pCartItemItsId)) {
        if (ci.getIsDisabled()) {
          throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
            "requested_item_disabled");
        }
        cartItem = ci;
        break;
      }
    }
    if (cartItem == null) {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "requested_item_not_found");
    }
    return cartItem;
  }

  /**
   * <p>Create cart item.</p>
   * @param pShoppingCart cart
   * @return cart item
   **/
  public final CartItem createCartItem(
    final ShoppingCart pShoppingCart) {
    CartItem cartItem = new CartItem();
    cartItem.setIsNew(true);
    cartItem.setIsDisabled(false);
    cartItem.setItsOwner(pShoppingCart);
    pShoppingCart.getItsItems().add(cartItem);
    return cartItem;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = PrcItemInCart.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = PrcItemInCart.class
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

  /**
   * <p>Lazy Get queryCartTotals.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String
    lazyGetQueryCartTotals() throws Exception {
    if (this.queryCartTotals == null) {
      String flName = "/webstore/cartTotals.sql";
      this.queryCartTotals = loadString(flName);
    }
    return this.queryCartTotals;
  }

  //Simple getters and setters:
  /**
   * <p>Setter for queryCartTotals.</p>
   * @param pQueryCartTotals reference
   **/
  public final void setQueryCartTotals(final String pQueryCartTotals) {
    this.queryCartTotals = pQueryCartTotals;
  }

  /**
   * <p>Getter for prcWebstorePage.</p>
   * @return PrcWebstorePage<RS>
   **/
  public final PrcWebstorePage<RS> getPrcWebstorePage() {
    return this.prcWebstorePage;
  }

  /**
   * <p>Setter for prcWebstorePage.</p>
   * @param pPrcWebstorePage reference
   **/
  public final void setPrcWebstorePage(
    final PrcWebstorePage<RS> pPrcWebstorePage) {
    this.prcWebstorePage = pPrcWebstorePage;
  }
}
