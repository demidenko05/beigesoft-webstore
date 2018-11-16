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

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.UnitOfMeasure;
import org.beigesoft.accounting.persistable.ServiceToSale;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.persistable.DestTaxGoodsLn;
import org.beigesoft.accounting.persistable.DestTaxServSelLn;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.accounting.persistable.base.AItem;
import org.beigesoft.accounting.persistable.base.ADestTaxItemLn;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.persistable.base.AItemPrice;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.CartItTxLn;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.BuyerPriceCategory;
import org.beigesoft.webstore.persistable.PriceGoodsId;
import org.beigesoft.webstore.persistable.PriceGoods;
import org.beigesoft.webstore.persistable.ServicePrice;
import org.beigesoft.webstore.persistable.ServicePriceId;
import org.beigesoft.webstore.persistable.SeService;
import org.beigesoft.webstore.persistable.SeServicePrice;
import org.beigesoft.webstore.persistable.SeServicePriceId;
import org.beigesoft.webstore.persistable.CurrRate;
import org.beigesoft.webstore.persistable.SeGoods;
import org.beigesoft.webstore.persistable.SeGoodsPrice;
import org.beigesoft.webstore.persistable.SeGoodsPriceId;
import org.beigesoft.webstore.persistable.DestTaxSeGoodsLn;
import org.beigesoft.webstore.persistable.DestTaxSeServiceLn;
import org.beigesoft.webstore.persistable.IHasSeSeller;
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
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  /**
   * <p>Process entity request.</p>
   * @param pReqVars request scoped vars
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    TradingSettings ts = (TradingSettings) pReqVars.get("tradSet");
    Cart cart = this.srvShoppingCart
      .getShoppingCart(pReqVars, pRequestData, true);
    CartLn cartLn = null;
    String lnIdStr = pRequestData.getParameter("lnId");
    String quantStr = pRequestData.getParameter("quant");
    String avQuanStr = pRequestData.getParameter("avQuan");
    String unStepStr = pRequestData.getParameter("unStep");
    BigDecimal quant = new BigDecimal(quantStr);
    BigDecimal avQuan = new BigDecimal(avQuanStr);
    BigDecimal unStep = new BigDecimal(unStepStr);
    String itIdStr = pRequestData.getParameter("itId");
    String itTypStr = pRequestData.getParameter("itTyp");
    Long itId = Long.valueOf(itIdStr);
    AccSettings as = (AccSettings) pReqVars.get("accSet");
    TaxDestination txRules = this.srvShoppingCart
      .revealTaxRules(pReqVars, cart, as);
    AItem<?, ?> item = null;
    EShopItemType itTyp = EShopItemType.class.
      getEnumConstants()[Integer.parseInt(itTypStr)];
    if (lnIdStr != null) { //change quantity
      Long lnId = Long.valueOf(lnIdStr);
      cartLn = findCartItemById(cart, lnId);
    } else { //add
      String uomIdStr = pRequestData.getParameter("uomId");
      Long uomId = Long.valueOf(uomIdStr);
      for (CartLn ci : cart.getItems()) {
        //check for duplicate cause "weird" but accepted request
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
      UnitOfMeasure uom = new UnitOfMeasure();
      uom.setItsId(uomId);
      cartLn.setUom(uom);
      cartLn.setItId(itId);
      cartLn.setItTyp(itTyp);
      //price, tax category and seller is set only for new line,
      //they will be examined additionally during creating customer order:
      AItemPrice<?, ?> itPrice = revealItemPrice(pReqVars, ts, cart,
        itTyp, itId);
      cartLn.setItsName(itPrice.getItem().getItsName());
      BigDecimal qosr = quant.remainder(itPrice.getUnStep());
      if (qosr.compareTo(BigDecimal.ZERO) != 0) {
        quant = quant.subtract(qosr);
      }
      cartLn.setPrice(itPrice.getItsPrice());
      if (!as.getCurrency().getItsId().equals(cart.getCurr().getItsId())) {
        List<CurrRate> currRates = (List<CurrRate>) pReqVars.get("currRates");
        for (CurrRate cr: currRates) {
          if (cr.getCurr().getItsId().equals(cart.getCurr().getItsId())) {
            cartLn.setPrice(cartLn.getPrice().multiply(cr.getRate())
              .setScale(as.getPricePrecision(), as.getRoundingMode()));
            break;
          }
        }
      }
      if (txRules != null || cartLn.getItTyp().equals(EShopItemType.SESERVICE)
        || cartLn.getItTyp().equals(EShopItemType.SEGOODS)) {
        Class<?> itemCl;
        boolean isSeSeller = false;
        if (cartLn.getItTyp().equals(EShopItemType.GOODS)) {
          itemCl = InvItem.class;
        } else if (cartLn.getItTyp().equals(EShopItemType.SERVICE)) {
          itemCl = ServiceToSale.class;
        } else if (cartLn.getItTyp().equals(EShopItemType.SESERVICE)) {
          itemCl = SeService.class;
          isSeSeller = true;
        } else {
          itemCl = SeGoods.class;
          isSeSeller = true;
        }
        item = (AItem<?, ?>) getSrvOrm()
          .retrieveEntityById(pReqVars, itemCl, cartLn.getItId());
        if (txRules != null) {
          cartLn.setTxCat(item.getTaxCategory());
        }
        if (isSeSeller) {
          IHasSeSeller<Long> seitem = (IHasSeSeller<Long>) item;
          cartLn.setSeller(seitem.getSeller());
        }
      }
    }
    cartLn.setQuant(quant);
    cartLn.setAvQuan(avQuan);
    cartLn.setUnStep(unStep);
    BigDecimal amount = cartLn.getPrice().multiply(cartLn.getQuant()).
      setScale(as.getPricePrecision(), as.getRoundingMode());
    if (ts.getTxExcl()) {
      cartLn.setSubt(amount);
    } else {
      cartLn.setTot(amount);
    }
    makeItemTax(pReqVars, ts, cartLn, as, txRules);
    this.srvShoppingCart.makeCartTotals(pReqVars, ts, cartLn, as, txRules);
    pRequestData.setAttribute("cart", cart);
    String processorName = pRequestData.getParameter("nmPrcRed");
    IProcessor proc = this.processorsFactory.lazyGet(pReqVars, processorName);
    proc.process(pReqVars, pRequestData);
  }

  /**
   * <p>Makes item's tax.</p>
   * @param pReqVars request scoped vars
   * @param pTs TradingSettings
   * @param pCartLn cart line
   * @param pAs Accounting Settings
   * @param pTxRules Tax Rules, NULL if not taxable
   * @throws Exception - an exception, e.g. if item has destination taxes
   * and buyer has ZIP, but its destination tax is empty.
   **/
  public final void makeItemTax(final Map<String, Object> pReqVars,
    final TradingSettings pTs, final CartLn pCartLn,
      final AccSettings pAs, final TaxDestination pTxRules) throws Exception {
    //using user passed values:
    BigDecimal totalTaxes = BigDecimal.ZERO;
    BigDecimal bd100 = new BigDecimal("100.00");
    List<CartItTxLn> itls = null;
    pCartLn.setTxCat(null);
    if (pTxRules != null) {
      if (pCartLn.getItsOwner().getBuyer().getRegCustomer()
        .getTaxDestination() != null) {
        Class<?> dstTxItLnCl;
        if (pCartLn.getItTyp().equals(EShopItemType.GOODS)) {
          dstTxItLnCl = DestTaxGoodsLn.class;
        } else if (pCartLn.getItTyp().equals(EShopItemType.SERVICE)) {
          dstTxItLnCl = DestTaxServSelLn.class;
        } else if (pCartLn.getItTyp().equals(EShopItemType.SESERVICE)) {
          dstTxItLnCl = DestTaxSeServiceLn.class;
        } else {
          dstTxItLnCl = DestTaxSeGoodsLn.class;
        }
        //override tax method:
        pReqVars.put(dstTxItLnCl.getSimpleName() + "itsOwnerdeepLevel", 1);
        List<ADestTaxItemLn<?>> dtls = (List<ADestTaxItemLn<?>>) getSrvOrm()
          .retrieveListWithConditions(pReqVars, dstTxItLnCl,
            "where ITSOWNER=" + pCartLn.getItId());
        pReqVars.remove(dstTxItLnCl.getSimpleName() + "itsOwnerdeepLevel");
        for (ADestTaxItemLn<?> dtl : dtls) {
          if (dtl.getTaxDestination().getItsId().equals(pCartLn.getItsOwner()
            .getBuyer().getRegCustomer().getTaxDestination().getItsId())) {
            pCartLn.setTxCat(dtl.getTaxCategory()); //it may be null
            break;
          }
        }
      }
      if (pCartLn.getTxCat() != null && !pTxRules.getSalTaxIsInvoiceBase()) {
        if (!pTxRules.getSalTaxUseAggregItBas()) {
          if (!pTs.getTxExcl()) {
            throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
              "price_inc_tax_multi_not_imp");
          }
          itls = new ArrayList<CartItTxLn>();
          pReqVars.put("InvItemTaxCategoryLineitsOwnerdeepLevel", 1);
          List<InvItemTaxCategoryLine> itcls = getSrvOrm()
            .retrieveListWithConditions(pReqVars,
              InvItemTaxCategoryLine.class, "where ITSOWNER="
                + pCartLn.getTxCat().getItsId());
          pReqVars.remove("InvItemTaxCategoryLineitsOwnerdeepLevel");
          StringBuffer sb = new StringBuffer();
          int i = 0;
          for (InvItemTaxCategoryLine itcl : itcls) {
           if (ETaxType.SALES_TAX_OUTITEM.equals(itcl.getTax().getItsType())
          || ETaxType.SALES_TAX_INITEM.equals(itcl.getTax().getItsType())) {
              if (i++ > 0) {
                sb.append(", ");
              }
              BigDecimal addTx = pCartLn.getSubt().multiply(itcl
                .getItsPercentage()).divide(bd100, pAs.getPricePrecision(),
                  pTxRules.getSalTaxRoundMode());
              totalTaxes = totalTaxes.add(addTx);
              CartItTxLn itl = new CartItTxLn();
              itl.setIsNew(true);
              itl.setTot(addTx);
              itl.setTax(itcl.getTax());
              itls.add(itl);
              sb.append(itl.getTax().getItsName() + " " + prn(pReqVars, addTx));
            }
          }
          pCartLn.setTxDsc(sb.toString());
        } else {
          if (!pTs.getTxExcl()) {
        totalTaxes = pCartLn.getTot().subtract(pCartLn.getTot()
    .divide(BigDecimal.ONE.add(pCartLn.getTxCat().getAggrOnlyPercent()
  .divide(bd100)), pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
          } else {
            totalTaxes = pCartLn.getSubt().multiply(pCartLn.getTxCat()
          .getAggrOnlyPercent()).divide(bd100, pAs.getPricePrecision(),
        pTxRules.getSalTaxRoundMode());
          }
        pCartLn.setTxDsc(pCartLn.getTxCat().getItsName());
        }
      } else if (pCartLn.getTxCat() != null) {
        pCartLn.setTxDsc(pCartLn.getTxCat().getItsName());
      }
    }
    pCartLn.setTotTx(totalTaxes);
    if (pTs.getTxExcl()) {
      pCartLn.setTot(pCartLn.getSubt().add(pCartLn.getTotTx()));
    } else {
      pCartLn.setSubt(pCartLn.getTot().subtract(pCartLn.getTotTx()));
    }
    List<CartLn> cartLns = pCartLn.getItsOwner().getItems();
    if (pCartLn.getIsNew()) {
      this.getSrvOrm().insertEntity(pReqVars, pCartLn);
      cartLns.add(pCartLn);
    } else {
      this.getSrvOrm().updateEntity(pReqVars, pCartLn);
      for (int i = 0; i < cartLns.size(); i++) {
        if (cartLns.get(i).getItId().equals(pCartLn.getItId())
          && cartLns.get(i).getItTyp().equals(pCartLn.getItTyp())) {
          cartLns.set(i, pCartLn);
          break;
        }
      }
    }
    if (itls != null) {
      pReqVars.put("CartItTxLnitsOwnerdeepLevel", 1);
      List<CartItTxLn> itlsr = getSrvOrm().retrieveListWithConditions(
          pReqVars, CartItTxLn.class, "where DISAB=1 and CARTID="
            + pCartLn.getItsOwner().getBuyer().getItsId());
      pReqVars.remove("CartItTxLnitsOwnerdeepLevel");
      for (CartItTxLn itl : itls) {
        CartItTxLn itlr = null;
        if (itlsr.size() > 0) {
          for (CartItTxLn itlrt : itlsr) {
            if (itlrt.getDisab()) {
              itlr = itlrt;
              itlr.setDisab(false);
              break;
            }
          }
        }
        if (itlr == null) {
          itl.setItsOwner(pCartLn);
          itl.setCartId(pCartLn.getItsOwner().getBuyer().getItsId());
          getSrvOrm().insertEntity(pReqVars, itl);
          itl.setIsNew(false);
        } else {
          itlr.setTax(itl.getTax());
          itlr.setTot(itl.getTot());
          itlr.setItsOwner(pCartLn);
          itlr.setCartId(pCartLn.getItsOwner().getBuyer().getItsId());
          getSrvOrm().updateEntity(pReqVars, itlr);
        }
      }
    }
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
  public final AItemPrice<?, ?> revealItemPrice(
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
   * <p>Simple delegator to print number.</p>
   * @param pReqVars additional param
   * @param pVal value
   * @return String
   **/
  public final String prn(final Map<String, Object> pReqVars,
    final BigDecimal pVal) {
    return this.srvNumberToString.print(pVal.toString(),
      (String) pReqVars.get("decSepv"), //user's preferences
        (String) pReqVars.get("decGrSepv"),
          (Integer) pReqVars.get("priceDp"),
            (Integer) pReqVars.get("digInGr"));
  }

  //Simple getters and setters:
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
