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
import java.util.Comparator;
import java.math.BigDecimal;
import java.util.Collections;
import java.math.RoundingMode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.model.CmprTaxCatLnRate;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.UnitOfMeasure;
import org.beigesoft.accounting.persistable.ServiceToSale;
import org.beigesoft.accounting.persistable.DebtorCreditor;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
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
import org.beigesoft.webstore.persistable.CartTxLn;
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
import org.beigesoft.webstore.persistable.SeSeller;
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
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  /**
   * <p>Query taxes invoice basis.</p>
   **/
  private String quTxInvBas;

  /**
   * <p>Query taxes item basis non-aggregate.</p>
   **/
  private String quTxItBas;

  /**
   * <p>Query taxes item basis aggregate.</p>
   **/
  private String quTxItBasAggr;

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
      pReqVars.get("tradSet");
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
    AccSettings as = (AccSettings) pReqVars.get("as");
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
    TaxDestination txRules = makeItemTax(pReqVars, ts, cartLn, as);
    makeCartTotals(pReqVars, ts, cart, as, txRules);
    pRequestData.setAttribute("cart", cart);
    String processorName = pRequestData.getParameter("nmPrcRed");
    IProcessor proc = this.processorsFactory.lazyGet(pReqVars, processorName);
    proc.process(pReqVars, pRequestData);
  }

  /**
   * <p>Refresh cart totals.</p>
   * @param pReqVars request scoped vars
   * @param pTs TradingSettings
   * @param pCart cart
   * @param pAs Accounting Settings
   * @param pTxRules NULL if not taxable
   * @throws Exception - an exception.
   **/
  public final void makeCartTotals(final Map<String, Object> pReqVars,
    final TradingSettings pTs, final Cart pCart, final AccSettings pAs,
      final TaxDestination pTxRules) throws Exception {
    if (pTxRules != null) {
      for (CartTxLn ctl : pCart.getTaxes()) {
        ctl.setDisab(false);
      }
      //data storage for any tax method,
      //for invoice basis it's used for farther adjusting invoice lines,
      //for item basis non-aggregate itId holds tax ID:
      List<CartLn> lnsDt = new ArrayList<CartLn>();
      String query;
      if (!pTxRules.getSalTaxIsInvoiceBase()
        && !pTxRules.getSalTaxUseAggregItBas()) {
        //item basis non-aggregate, taxes excluded:
        query = lazyGetQuTxItBas();
      } else if (!pTxRules.getSalTaxIsInvoiceBase()
        && pTxRules.getSalTaxUseAggregItBas()) { //item basis aggregate:
        query = lazyGetQuTxItBasAggr();
      } else { //invoice basis:
        query = lazyGetQuTxInvBas();
      }
      query = query.replace(":CARTID", pCart.getBuyer().getItsId().toString());
      IRecordSet<RS> recordSet = null;
      try {
        recordSet = getSrvDatabase().retrieveRecords(query);
        if (recordSet.moveToFirst()) {
          do {
            Long txId = recordSet.getLong("TAXID");
            Long sellerId = recordSet.getLong("SELLER");
            if (!pTxRules.getSalTaxIsInvoiceBase()
              && !pTxRules.getSalTaxUseAggregItBas()) {
              //item basis non-aggregate, taxes excluded:
              CartLn ctl = new CartLn();
              lnsDt.add(ctl);
              ctl.setTotTx(BigDecimal.valueOf(recordSet.getDouble("TOTALTAX")));
              ctl.setItId(txId);
              if (sellerId != null) {
                DebtorCreditor ds = new DebtorCreditor();
                ds.setItsId(sellerId);
                SeSeller se = new SeSeller();
                se.setItsId(ds);
                ctl.setSeller(se);
              }
            } else {
              Double percent = recordSet.getDouble("ITSPERCENTAGE");
              Long tcId = recordSet.getLong("TAXCATID");
              if (!pTxRules.getSalTaxIsInvoiceBase()
                && pTxRules.getSalTaxUseAggregItBas()) { //item basis aggregate
                Long clId = recordSet.getLong("CLID");
                CartLn tdl = makeTdLine(lnsDt, clId, tcId, txId, percent,
                  pAs, sellerId);
                tdl.setTotTx(BigDecimal.valueOf(recordSet
                  .getDouble("TOTALTAXES"))
                    .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
              } else { //invoice basis
                CartLn tdl = makeTdLine(lnsDt, tcId, tcId, txId, percent,
                  pAs, sellerId);
                tdl.setTot(BigDecimal.valueOf(recordSet
                  .getDouble("ITSTOTAL"))
                    .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
                tdl.setSubt(BigDecimal.valueOf(recordSet
                  .getDouble("SUBTOTAL"))
                    .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
              }
            }
          } while (recordSet.moveToNext());
        }
      } finally {
        if (recordSet != null) {
          recordSet.close();
        }
      }
      if (!pTxRules.getSalTaxIsInvoiceBase()
        && !pTxRules.getSalTaxUseAggregItBas()) {
        //item basis non-aggregate, taxes excluded:
        for (CartLn dtl : lnsDt) {
          Tax tax = new Tax();
          tax.setItsId(dtl.getItId());
          CartTxLn ctl = findCreateTaxLine(pReqVars, pCart, tax.getItsId(),
            dtl.getSeller());
          ctl.setTot(dtl.getTotTx()
            .setScale(pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
          if (ctl.getIsNew()) {
            getSrvOrm().insertEntity(pReqVars, ctl);
            ctl.setIsNew(false);
          } else {
            getSrvOrm().updateEntity(pReqVars, ctl);
          }
        }
      } else {
        BigDecimal bd100 = new BigDecimal("100.00");
        Comparator<InvItemTaxCategoryLine> cmpr = Collections
          .reverseOrder(new CmprTaxCatLnRate());
        for (CartLn dtl : lnsDt) {
          int ti = 0;
          //total taxes for tax category for updating cart lines:
          BigDecimal invBasTaxTot = null;
          //aggregate rate line scoped storages:
          BigDecimal taxAggegated = null;
          BigDecimal taxAggrAccum = BigDecimal.ZERO;
          if (pTxRules.getSalTaxUseAggregItBas()) {
            Collections.sort(dtl.getTxCat().getTaxes(), cmpr);
          }
          for (InvItemTaxCategoryLine itcl : dtl.getTxCat().getTaxes()) {
            ti++;
            if (taxAggegated == null && pTxRules.getSalTaxUseAggregItBas()) {
             if (pTxRules.getSalTaxIsInvoiceBase() && !pTs.getTxExcl()) {
               //invoice basis, aggregate/only rate, taxes included
                taxAggegated = dtl.getTot().subtract(dtl.getTot().divide(
              BigDecimal.ONE.add(dtl.getTxCat().getAggrOnlyPercent().divide(
            bd100)), pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
              } else if (!pTxRules.getSalTaxIsInvoiceBase()) {
               //item basis, aggregate/only rate
                taxAggegated = dtl.getTotTx();
              }
            }
            if (pTxRules.getSalTaxIsInvoiceBase()) {
              //total taxes for tax category for updating invoice lines:
              invBasTaxTot = dtl.getTotTx();
            }
            if (pTxRules.getSalTaxIsInvoiceBase() && !pTs.getTxExcl()
              && pTxRules.getSalTaxUseAggregItBas()) {
             if (dtl.getTxCat().getTaxes().size() == 1
                || ti < dtl.getTxCat().getTaxes().size()) {
                dtl.setTotTx(taxAggegated.multiply(itcl.getItsPercentage())
              .divide(dtl.getTxCat().getAggrOnlyPercent(),
            pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
                taxAggrAccum = taxAggrAccum.add(dtl.getTotTx());
              } else { //the rest:
                dtl.setTotTx(taxAggegated.subtract(taxAggrAccum));
              }
            } else if (pTxRules.getSalTaxIsInvoiceBase() && pTs.getTxExcl()) {
              dtl.setTotTx(dtl.getSubt().multiply(itcl
                .getItsPercentage()).divide(bd100, pAs
                  .getPricePrecision(), pTxRules.getSalTaxRoundMode()));
            } else if (!pTxRules.getSalTaxIsInvoiceBase()
              && pTxRules.getSalTaxUseAggregItBas()) {
              if (dtl.getTxCat().getTaxes().size() == 1
                || ti < dtl.getTxCat().getTaxes().size()) {
                dtl.setTotTx(taxAggegated.multiply(itcl
              .getItsPercentage()).divide(dtl.getTxCat()
            .getAggrOnlyPercent(), pAs.getPricePrecision(),
          pTxRules.getSalTaxRoundMode()));
                taxAggrAccum = taxAggrAccum.add(dtl.getTotTx());
              } else {
                dtl.setTotTx(taxAggegated.subtract(taxAggrAccum));
              }
            } else {
              throw new Exception("Algorithm error!!!");
            }
            CartTxLn ctl = findCreateTaxLine(pReqVars, pCart,
              itcl.getTax().getItsId(), dtl.getSeller());
            //makeCtl(pReqVars, ctl, dtl, pTxRules, pTs);
            if (pTxRules.getSalTaxIsInvoiceBase()) {
              //total taxes for tax category for updating invoice lines:
              dtl.setTotTx(invBasTaxTot.add(dtl.getTotTx()));
            }
          }
        }
      }
    } else {
      for (CartTxLn ctl : pCart.getTaxes()) {
        ctl.setDisab(true);
        getSrvOrm().updateEntity(pReqVars, ctl);
      }
      pReqVars.put("CartItTxLnitsOwnerdeepLevel", 1);
      List<CartItTxLn> citls = getSrvOrm().retrieveListWithConditions(
          pReqVars, CartItTxLn.class, " where DISAB=0 and CARTID="
            + pCart.getBuyer().getItsId());
      pReqVars.remove("CartItTxLnitsOwnerdeepLevel");
      for (CartItTxLn citl : citls) {
        citl.setDisab(true);
        getSrvOrm().updateEntity(pReqVars, citl);
      }
    }
    getSrvOrm().updateEntity(pReqVars, pCart);
  }

  /**
   * <p>Make cart line that stores taxes data in lines set
   * for invoice basis or item basis aggregate rate.</p>
   * @param pTdLns TD lines
   * @param pTdlId line ID
   * @param pCatId tax category ID
   * @param pTaxId tax ID
   * @param pPercent tax rate
   * @param pAs AS
   * @param pSellerId Seller ID
   * @return line
   **/
  public final CartLn makeTdLine(final List<CartLn> pTdLns, final Long pTdlId,
    final Long pCatId,  final Long pTaxId, final Double pPercent,
      final AccSettings pAs, final Long pSellerId) {
    CartLn tdLn = null;
    for (CartLn tdl : pTdLns) {
      if (tdl.getItsId().equals(pTdlId)
        && (pSellerId == null && tdl.getSeller() == null
        || pSellerId != null && tdl.getSeller() != null
          && tdl.getSeller().getItsId().getItsId().equals(pSellerId))) {
        tdLn = tdl;
      }
    }
    if (tdLn == null) {
      tdLn = new CartLn();
      tdLn.setItsId(pTdlId);
      InvItemTaxCategory tc = new InvItemTaxCategory();
      tc.setItsId(pCatId);
      tc.setTaxes(new ArrayList<InvItemTaxCategoryLine>());
      tdLn.setTxCat(tc);
      if (pSellerId != null) {
        DebtorCreditor ds = new DebtorCreditor();
        ds.setItsId(pSellerId);
        SeSeller se = new SeSeller();
        se.setItsId(ds);
        tdLn.setSeller(se);
      }
      pTdLns.add(tdLn);
    }
    InvItemTaxCategoryLine itcl = new InvItemTaxCategoryLine();
    Tax tax = new Tax();
    tax.setItsId(pTaxId);
    itcl.setTax(tax);
    itcl.setItsPercentage(BigDecimal.valueOf(pPercent)
      .setScale(pAs.getTaxPrecision(), RoundingMode.HALF_UP));
    tdLn.getTxCat().getTaxes().add(itcl);
    tdLn.getTxCat().setAggrOnlyPercent(tdLn.getTxCat()
      .getAggrOnlyPercent().add(itcl.getItsPercentage()));
    return tdLn;
  }

  /**
   * <p>Finds enabled line with same tax and seller or any disabled tax line
   * or creates one.</p>
   * @param pReqVars additional param
   * @param pCart cart
   * @param pTaxId tax ID
   * @param pSeller seller
   * @return line
   **/
  public final CartTxLn findCreateTaxLine(final Map<String, Object> pReqVars,
    final Cart pCart, final Long pTaxId, final SeSeller pSeller) {
    CartTxLn ctl = null;
    //find enabled line to add amount
    for (CartTxLn tl : pCart.getTaxes()) {
      if (!tl.getDisab() && tl.getTax().getItsId().equals(pTaxId)
        && (pSeller == null && tl.getSeller() == null
          || pSeller != null && tl.getSeller() != null && pSeller.getItsId()
            .getItsId().equals(tl.getSeller().getItsId().getItsId()))) {
        ctl = tl;
        break;
      }
    }
    if (ctl == null) {
      //find disabled line to initialize new tax
      for (CartTxLn tl : pCart.getTaxes()) {
        if (tl.getDisab()) {
          ctl = tl;
          Tax tax = new Tax();
          tax.setItsId(pTaxId);
          ctl.setTax(tax);
          ctl.setSeller(pSeller);
          break;
        }
      }
    }
    if (ctl == null) {
      ctl = new CartTxLn();
      ctl.setItsOwner(pCart);
      ctl.setIsNew(true);
      Tax tax = new Tax();
      tax.setItsId(pTaxId);
      ctl.setTax(tax);
      ctl.setSeller(pSeller);
      pCart.getTaxes().add(ctl);
    }
    return ctl;
  }

  /**
   * <p>Makes item's tax and cart totals.</p>
   * @param pReqVars request scoped vars
   * @param pTs TradingSettings
   * @param pCartLn cart line
   * @param pAs Accounting Settings
   * @return tax rules, NULL if not taxable
   * @throws Exception - an exception, e.g. if item has destination taxes
   * and buyer has ZIP, but its destination tax is empty.
   **/
  public final TaxDestination makeItemTax(final Map<String, Object> pReqVars,
    final TradingSettings pTs, final CartLn pCartLn,
      final AccSettings pAs) throws Exception {
    DebtorCreditor cust = pCartLn.getItsOwner().getBuyer().getRegCustomer();
    if (cust == null) {
      cust = new DebtorCreditor();
      cust.setRegZip(pCartLn.getItsOwner().getBuyer().getRegZip());
      cust.setTaxDestination(pCartLn.getItsOwner().getBuyer().getTaxDest());
    }
    TaxDestination txRules = null;
    if (pAs.getIsExtractSalesTaxFromSales() && !cust.getIsForeigner()) {
      txRules = new TaxDestination();
      txRules.setSalTaxIsInvoiceBase(pAs.getSalTaxIsInvoiceBase());
      txRules.setSalTaxUseAggregItBas(pAs.getSalTaxUseAggregItBas());
      txRules.setSalTaxRoundMode(pAs.getSalTaxRoundMode());
    }
    //using user passed values:
    BigDecimal totalTaxes = BigDecimal.ZERO;
    BigDecimal bd100 = new BigDecimal("100.00");
    List<CartItTxLn> itls = null;
    pCartLn.setTxCat(null);
    if (txRules != null) {
      Class<?> itemCl;
      Class<?> dstTxItLnCl;
      if (pCartLn.getItTyp().equals(EShopItemType.GOODS)) {
        itemCl = InvItem.class;
        dstTxItLnCl = DestTaxGoodsLn.class;
      } else if (pCartLn.getItTyp().equals(EShopItemType.SERVICE)) {
        itemCl = ServiceToSale.class;
        dstTxItLnCl = DestTaxServSelLn.class;
      } else if (pCartLn.getItTyp().equals(EShopItemType.SESERVICE)) {
        itemCl = SeService.class;
        dstTxItLnCl = DestTaxSeServiceLn.class;
      } else {
        itemCl = SeGoods.class;
        dstTxItLnCl = DestTaxSeGoodsLn.class;
      }
      AItem<?, ?> item = (AItem<?, ?>) getSrvOrm()
        .retrieveEntityById(pReqVars, itemCl, pCartLn.getItId());
      pCartLn.setTxCat(item.getTaxCategory());
      if (cust.getTaxDestination() != null) {
        //override tax method:
        txRules = cust.getTaxDestination();
        pReqVars.put(dstTxItLnCl.getSimpleName() + "itsOwnerdeepLevel", 1);
        List<ADestTaxItemLn<?>> dtls = (List<ADestTaxItemLn<?>>) getSrvOrm()
          .retrieveListWithConditions(pReqVars, dstTxItLnCl,
            "where ITSOWNER=" + pCartLn.getItId());
        pReqVars.remove(dstTxItLnCl.getSimpleName() + "itsOwnerdeepLevel");
        for (ADestTaxItemLn<?> dtl : dtls) {
          if (dtl.getTaxDestination().getItsId().equals(cust
            .getTaxDestination().getItsId())) {
            pCartLn.setTxCat(dtl.getTaxCategory()); //it may be null
            break;
          }
        }
      }
      if (pCartLn.getTxCat() != null && !txRules.getSalTaxIsInvoiceBase()) {
        if (!txRules.getSalTaxUseAggregItBas()) {
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
                  txRules.getSalTaxRoundMode());
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
  .divide(bd100)), pAs.getPricePrecision(), txRules.getSalTaxRoundMode()));
          } else {
            totalTaxes = pCartLn.getSubt().multiply(pCartLn.getTxCat()
          .getAggrOnlyPercent()).divide(bd100, pAs.getPricePrecision(),
        txRules.getSalTaxRoundMode());
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
    return txRules;
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
      (String) pReqVars.get("decSepv"), //TODO default I18N
        (String) pReqVars.get("decGrSepv"),
          (Integer) pReqVars.get("priceDp"),
            (Integer) pReqVars.get("digInGr"));
  }

  /**
   * <p>Lazy Getter for quTxInvBas.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  public final String lazyGetQuTxInvBas() throws IOException {
    if (this.quTxInvBas == null) {
      this.quTxInvBas = loadString("/webstore/cartTxInvBas.sql");
    }
    return this.quTxInvBas;
  }

  /**
   * <p>Lazy Getter for quTxItBas.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  public final String lazyGetQuTxItBas() throws IOException {
    if (this.quTxItBas == null) {
      this.quTxItBas = loadString("/webstore/cartTxItBas.sql");
    }
    return this.quTxItBas;
  }

  /**
   * <p>Lazy Getter for quTxItBasAggr.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  public final String lazyGetQuTxItBasAggr() throws IOException {
    if (this.quTxItBasAggr == null) {
      this.quTxItBasAggr = loadString("/webstore/cartTxItBasAggr.sql");
    }
    return this.quTxItBasAggr;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName) throws IOException {
    URL urlFile = PrcItemInCart.class.getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = PrcItemInCart.class.getResourceAsStream(pFileName);
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

  /**
   * <p>Setter for quTxInvBas.</p>
   * @param pQuTxInvBas reference
   **/
  public final void setQuTxInvBas(final String pQuTxInvBas) {
    this.quTxInvBas = pQuTxInvBas;
  }

  /**
   * <p>Setter for quTxItBas.</p>
   * @param pQuTxItBas reference
   **/
  public final void setQuTxItBas(final String pQuTxItBas) {
    this.quTxItBas = pQuTxItBas;
  }

  /**
   * <p>Setter for quTxItBasAggr.</p>
   * @param pQuTxItBasAggr reference
   **/
  public final void setQuTxItBasAggr(final String pQuTxItBasAggr) {
    this.quTxItBasAggr = pQuTxItBasAggr;
  }
}
