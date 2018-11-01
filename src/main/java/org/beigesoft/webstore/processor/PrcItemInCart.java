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
import org.beigesoft.accounting.persistable.UnitOfMeasure;
import org.beigesoft.accounting.persistable.ServiceToSale;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.persistable.base.AItemPrice;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.BuyerPriceCategory;
import org.beigesoft.webstore.persistable.PriceGoodsId;
import org.beigesoft.webstore.persistable.PriceGoods;
import org.beigesoft.webstore.persistable.ServicePrice;
import org.beigesoft.webstore.persistable.ServicePriceId;
import org.beigesoft.webstore.persistable.SeService;
import org.beigesoft.webstore.persistable.SeServicePrice;
import org.beigesoft.webstore.persistable.SeServicePriceId;
import org.beigesoft.webstore.persistable.SeGoods;
import org.beigesoft.webstore.persistable.SeGoodsPrice;
import org.beigesoft.webstore.persistable.SeGoodsPriceId;
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
   * @param pReqVars request scoped vars
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    TradingSettings ts = (TradingSettings)
      pReqVars.get("tradingSettings");
    Cart cart = this.srvShoppingCart
      .getShoppingCart(pReqVars, pRequestData, true);
    CartLn cartLn = null;
    String lnIdStr = pRequestData.getParameter("lnId");
    String quantStr = pRequestData.getParameter("quant");
    String avQuanStr = pRequestData.getParameter("avQuan");
    BigDecimal quant = new BigDecimal(quantStr);
    BigDecimal avQuan = new BigDecimal(avQuanStr);
    String itIdStr = pRequestData.getParameter("itId");
    String itTypStr = pRequestData.getParameter("itTyp");
    Long itId = Long.valueOf(itIdStr);
    EShopItemType itTyp = EShopItemType.class.
      getEnumConstants()[Integer.parseInt(itTypStr)];
    if (lnIdStr != null) { //change quantity
      Long lnId = Long.valueOf(lnIdStr);
      cartLn = findCartItemById(cart, lnId);
    } else { //add
      String uomIdStr = pRequestData.getParameter("uomId");
      Long uomId = Long.valueOf(uomIdStr);
      if (cart.getItems() == null) {
        cart.setItems(new ArrayList<CartLn>());
        cartLn = createCartItem(cart);
      } else {
        for (CartLn ci : cart.getItems()) {
          //check for duplicate
          if (!ci.getDisab() && ci.getItTyp().equals(itTyp)
            && ci.getItId().equals(itId)) {
            cartLn = ci;
            break;
          }
        }
        if (cartLn == null) {
          for (CartLn ci : cart.getItems()) {
            if (ci.getDisab()) {
              cartLn = ci;
              cartLn.setDisab(false);
              break;
            }
          }
        }
        if (cartLn == null) {
          cartLn = createCartItem(cart);
        }
      }
      UnitOfMeasure uom = new UnitOfMeasure();
      uom.setItsId(uomId);
      cartLn.setUom(uom);
      cartLn.setItId(itId);
      cartLn.setItTyp(itTyp);
      AItemPrice<?, ?> itPrice = revialItemPrice(pReqVars, ts, cart,
        itTyp, itId);
      cartLn.setItsName(itPrice.getItem().getItsName());
      BigDecimal qosr = quant.remainder(itPrice.getUnStep());
      if (qosr.compareTo(BigDecimal.ZERO) != 0) {
        quant = quant.subtract(qosr);
      }
      cartLn.setPrice(itPrice.getItsPrice());
    }
    cartLn.setTotTx(BigDecimal.ZERO);
    cartLn.setQuant(quant);
    cartLn.setAvQuan(avQuan);
    cartLn.setSubt(cartLn.getPrice().multiply(cartLn.getQuant()));
    cartLn.setTot(cartLn.getSubt().add(cartLn.getTotTx()));
    if (cartLn.getIsNew()) {
      this.getSrvOrm().insertEntity(pReqVars, cartLn);
    } else {
      this.getSrvOrm().updateEntity(pReqVars, cartLn);
    }
    //TODO total from already loaded cart:
    String query = lazyGetQueryCartTotals();
    query = query.replace(":CARTID", cart.getItsId()
      .getItsId().toString());
    String[] columns = new String[]{"ITSTOTAL"};
    Double[] totals = this.getSrvDatabase()
      .evalDoubleResults(query, columns);
    if (totals[0] == null) {
      totals[0] = 0d;
    }
    AccSettings accSettings = (AccSettings) pReqVars.get("accSettings");
    cart.setTot(BigDecimal.valueOf(totals[0]).
      setScale(accSettings.getPricePrecision(), accSettings.getRoundingMode()));
    this.getSrvOrm().updateEntity(pReqVars, cart);
    pRequestData.setAttribute("cart", cart);
    String processorName = pRequestData.getParameter("nmPrcRed");
    IProcessor proc = this.processorsFactory.lazyGet(pReqVars, processorName);
    proc.process(pReqVars, pRequestData);
  }

  /**
   * <p>Reveals item's price descriptor.</p>
   * @param pReqVars request scoped vars
   * @param pTs TradingSettings
   * @param pCart cart
   * @param pItType Item Type
   * @param pItId Item ID
   * @return item's price descriptor or exception
   * @throws Exception - an exception
   **/
  public final AItemPrice<?, ?> revialItemPrice(
    final Map<String, Object> pReqVars, final TradingSettings pTs,
      final Cart pCart, final EShopItemType pItType,
        final Long pItId) throws Exception {
    AItemPrice<?, ?> itPrice = null;
    if (pTs.getIsUsePriceForCustomer()) {
      //try to reveal price dedicated to customer:
      List<BuyerPriceCategory> buyerPrCats = this.getSrvOrm()
        .retrieveListWithConditions(pReqVars, BuyerPriceCategory.class,
          "where BUYER=" + pCart.getBuyer().getItsId());
      for (BuyerPriceCategory buyerPrCat : buyerPrCats) {
        if (pItType.equals(EShopItemType.GOODS)) {
          InvItem item = new InvItem();
          item.setItsId(pItId);
          PriceGoodsId pIpId = new PriceGoodsId();
          pIpId.setItem(item);
          pIpId.setPriceCategory(buyerPrCat.getPriceCategory());
          PriceGoods itPr = new PriceGoods();
          itPr.setItsId(pIpId);
          itPr = this.getSrvOrm().retrieveEntity(pReqVars, itPr);
          if (itPr != null) {
            itPrice = itPr;
            break;
          }
        } else if (pItType.equals(EShopItemType.SERVICE)) {
          ServiceToSale item = new ServiceToSale();
          item.setItsId(pItId);
          ServicePriceId pIpId = new ServicePriceId();
          pIpId.setItem(item);
          pIpId.setPriceCategory(buyerPrCat.getPriceCategory());
          ServicePrice itPr = new ServicePrice();
          itPr.setItsId(pIpId);
          itPr = this.getSrvOrm().retrieveEntity(pReqVars, itPr);
          if (itPr != null) {
            itPrice = itPr;
            break;
          }
        } else if (pItType.equals(EShopItemType.SESERVICE)) {
          SeService item = new SeService();
          item.setItsId(pItId);
          SeServicePriceId pIpId = new SeServicePriceId();
          pIpId.setItem(item);
          pIpId.setPriceCategory(buyerPrCat.getPriceCategory());
          SeServicePrice itPr = new SeServicePrice();
          itPr.setItsId(pIpId);
          itPr = this.getSrvOrm().retrieveEntity(pReqVars, itPr);
          if (itPr != null) {
            itPrice = itPr;
            break;
          }
        } else {
          SeGoods item = new SeGoods();
          item.setItsId(pItId);
          SeGoodsPriceId pIpId = new SeGoodsPriceId();
          pIpId.setItem(item);
          pIpId.setPriceCategory(buyerPrCat.getPriceCategory());
          SeGoodsPrice itPr = new SeGoodsPrice();
          itPr.setItsId(pIpId);
          itPr = this.getSrvOrm().retrieveEntity(pReqVars, itPr);
          if (itPr != null) {
            itPrice = itPr;
            break;
          }
        }
      }
    }
    if (itPrice == null) {
      //retrieve price for all:
      Class<?> itepPriceCl;
      if (pItType.equals(EShopItemType.GOODS)) {
        itepPriceCl = PriceGoods.class;
      } else if (pItType.equals(EShopItemType.SERVICE)) {
        itepPriceCl = ServicePrice.class;
      } else if (pItType.equals(EShopItemType.SESERVICE)) {
        itepPriceCl = SeServicePrice.class;
      } else {
        itepPriceCl = SeGoodsPrice.class;
      }
      @SuppressWarnings("unchecked")
      List<AItemPrice<?, ?>> itPrices = (List<AItemPrice<?, ?>>)
        this.getSrvOrm().retrieveListWithConditions(pReqVars, itepPriceCl,
          "where ITEM=" + pItId);
      if (itPrices.size() == 0) {
        throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
          "requested_item_has_no_price");
      }
      if (itPrices.size() > 1) {
        throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
          "requested_item_has_several_prices");
      }
      itPrice = itPrices.get(0);
    }
    return itPrice;
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
    CartLn cartLn = null;
    for (CartLn ci : pShoppingCart.getItems()) {
      if (ci.getItsId().equals(pCartItemItsId)) {
        if (ci.getDisab()) {
          throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
            "requested_item_disabled");
        }
        cartLn = ci;
        break;
      }
    }
    if (cartLn == null) {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "requested_item_not_found");
    }
    return cartLn;
  }

  /**
   * <p>Create cart item.</p>
   * @param pShoppingCart cart
   * @return cart item
   **/
  public final CartLn createCartItem(final Cart pShoppingCart) {
    CartLn cartLn = new CartLn();
    cartLn.setIsNew(true);
    cartLn.setDisab(false);
    cartLn.setItsOwner(pShoppingCart);
    pShoppingCart.getItems().add(cartLn);
    return cartLn;
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
