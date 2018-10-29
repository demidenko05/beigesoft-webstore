package org.beigesoft.webstore.processor;

/*
 * Copyright (c) 2017 Beigesoft™
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
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.BuyerPriceCategory;
import org.beigesoft.webstore.persistable.PriceGoodsId;
import org.beigesoft.webstore.persistable.PriceGoods;
import org.beigesoft.webstore.service.ISrvShoppingCart;

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
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Shopping Cart service.</p>
   **/
  private ISrvShoppingCart srvShoppingCart;

  /**
   * <p>Processors factory.</p>
   **/
  private IFactoryAppBeansByName<IProcessor> processorsFactory;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pAddParam,
    final IRequestData pRequestData) throws Exception {
    TradingSettings tradingSettings = (TradingSettings)
      pAddParam.get("tradingSettings");
    Cart shoppingCart = this.srvShoppingCart
      .getShoppingCart(pAddParam, pRequestData, true);
    CartLn cartItem = null;
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
      if (shoppingCart.getItems() == null) {
        shoppingCart.setItems(new ArrayList<CartLn>());
        cartItem = createCartItem(shoppingCart);
      } else {
        for (CartLn ci : shoppingCart.getItems()) {
          //check for duplicate
          if (!ci.getDisab() && ci.getItTyp().equals(cartItemType)
            && ci.getItId().equals(cartItemId)) {
            cartItem = ci;
            break;
          }
        }
        if (cartItem == null) {
          for (CartLn ci : shoppingCart.getItems()) {
            if (ci.getDisab()) {
              cartItem = ci;
              cartItem.setDisab(false);
              break;
            }
          }
        }
        if (cartItem == null) {
          cartItem = createCartItem(shoppingCart);
        }
      }
      cartItem.setItId(cartItemId);
      cartItem.setItTyp(cartItemType);
      BigDecimal price = null;
      if (tradingSettings.getIsUsePriceForCustomer()) {
        //try to reveal price dedicated for customer:
        List<BuyerPriceCategory> buyerPrCats = this.getSrvOrm()
          .retrieveListWithConditions(pAddParam, BuyerPriceCategory.class,
            "where BUYER=" + shoppingCart.getBuyer().getItsId());
        for (BuyerPriceCategory buyerPrCat : buyerPrCats) {
          if (cartItemType.equals(EShopItemType.GOODS)) {
            InvItem goods = new InvItem();
            goods.setItsId(cartItemId);
            PriceGoodsId gpId = new PriceGoodsId();
            gpId.setItem(goods);
            gpId.setPriceCategory(buyerPrCat.getPriceCategory());
            PriceGoods goodsPrice = new PriceGoods();
            goodsPrice.setItsId(gpId);
            goodsPrice = this.getSrvOrm().
              retrieveEntity(pAddParam, goodsPrice);
            if (goodsPrice != null) {
              price = goodsPrice.getItsPrice();
              cartItem.setItsName(goodsPrice.getItem().getItsName());
              break;
            }
          }
          //TODO Services, SE G/S
        }
      }
      if (price == null) {
        //retrieve price for all:
        if (cartItemType.equals(EShopItemType.GOODS)) {
          List<PriceGoods> goodsPrices = this.getSrvOrm().
            retrieveListWithConditions(pAddParam, PriceGoods.class,
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
          cartItem.setItsName(goodsPrices.get(0).getItem().getItsName());
        } else {
            throw new ExceptionWithCode(ExceptionWithCode.NOT_YET_IMPLEMENTED,
              "add_service_se_not_impl");
          //TODO Services, SE G/S
        }
      }
      cartItem.setPrice(price);
    }
    cartItem.setTotTx(BigDecimal.ZERO);
    cartItem.setQuant(cartItemQuantity);
    cartItem.setAvQuan(cartItemAvailableQuantity);
    cartItem.setSubt(cartItem.getPrice().multiply(cartItem.getQuant()));
    cartItem.setTot(cartItem.getSubt().add(cartItem.getTotTx()));
    if (cartItem.getIsNew()) {
      this.getSrvOrm().insertEntity(pAddParam, cartItem);
    } else {
      this.getSrvOrm().updateEntity(pAddParam, cartItem);
    }
    String query = lazyGetQueryCartTotals();
    query = query.replace(":CARTID", shoppingCart.getItsId()
      .getItsId().toString());
    String[] columns = new String[]{"ITSTOTAL"};
    Double[] totals = this.getSrvDatabase()
      .evalDoubleResults(query, columns);
    if (totals[0] == null) {
      totals[0] = 0d;
    }
    AccSettings accSettings = (AccSettings) pAddParam.get("accSettings");
    shoppingCart.setTot(BigDecimal.valueOf(totals[0]).
      setScale(accSettings.getPricePrecision(), accSettings.getRoundingMode()));
    this.getSrvOrm().updateEntity(pAddParam, shoppingCart);
    pRequestData.setAttribute("shoppingCart", shoppingCart);
    String processorName = pRequestData.getParameter("nmPrcRedirect");
    IProcessor proc = this.processorsFactory.lazyGet(pAddParam, processorName);
    proc.process(pAddParam, pRequestData);
  }

  /**
   * <p>Find cart item by ID.</p>
   * @param pShoppingCart cart
   * @param pCartItemItsId cart item ID
   * @return cart item
   * @throws Exception - an exception
   **/
  public final CartLn findCartItemById(final Cart pShoppingCart,
    final Long pCartItemItsId) throws Exception {
    CartLn cartItem = null;
    for (CartLn ci : pShoppingCart.getItems()) {
      if (ci.getItsId().equals(pCartItemItsId)) {
        if (ci.getDisab()) {
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
  public final CartLn createCartItem(
    final Cart pShoppingCart) {
    CartLn cartItem = new CartLn();
    cartItem.setIsNew(true);
    cartItem.setDisab(false);
    cartItem.setItsOwner(pShoppingCart);
    pShoppingCart.getItems().add(cartItem);
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
   * <p>Getter for srvDatabase.</p>
   * @return ISrvDatabase<RS>
   **/
  public final ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final void setSrvDatabase(final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
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

  /**
   * <p>Getter for processorsFactory.</p>
   * @return IFactoryAppBeansByName<IProcessor>
   **/
  public final IFactoryAppBeansByName<IProcessor> getProcessorsFactory() {
    return this.processorsFactory;
  }

  /**
   * <p>Setter for processorsFactory.</p>
   * @param pProcessorsFactory reference
   **/
  public final void setProcessorsFactory(
    final IFactoryAppBeansByName<IProcessor> pProcessorsFactory) {
    this.processorsFactory = pProcessorsFactory;
  }
}
