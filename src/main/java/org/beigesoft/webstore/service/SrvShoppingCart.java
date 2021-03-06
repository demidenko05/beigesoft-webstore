package org.beigesoft.webstore.service;

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

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.log.ILog;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.model.CmprTaxCatLnRate;
import org.beigesoft.accounting.persistable.Currency;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.accounting.persistable.DebtorCreditor;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.I18nInvItem;
import org.beigesoft.accounting.persistable.ServiceToSale;
import org.beigesoft.accounting.persistable.I18nServiceToSale;
import org.beigesoft.accounting.persistable.DestTaxGoodsLn;
import org.beigesoft.accounting.persistable.DestTaxServSelLn;
import org.beigesoft.accounting.persistable.base.AItem;
import org.beigesoft.accounting.persistable.base.ADestTaxItemLn;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.model.EPaymentMethod;
import org.beigesoft.webstore.persistable.base.AItemPrice;
import org.beigesoft.webstore.persistable.IHasSeSeller;
import org.beigesoft.webstore.persistable.CartItTxLn;
import org.beigesoft.webstore.persistable.BuyerPriceCategory;
import org.beigesoft.webstore.persistable.PriceGoods;
import org.beigesoft.webstore.persistable.ServicePrice;
import org.beigesoft.webstore.persistable.SeService;
import org.beigesoft.webstore.persistable.I18nSeService;
import org.beigesoft.webstore.persistable.SeServicePrice;
import org.beigesoft.webstore.persistable.I18nSeGoods;
import org.beigesoft.webstore.persistable.SeGoods;
import org.beigesoft.webstore.persistable.SeGoodsPrice;
import org.beigesoft.webstore.persistable.SeSeller;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartTot;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.CartTxLn;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.CurrRate;
import org.beigesoft.webstore.persistable.DestTaxSeGoodsLn;
import org.beigesoft.webstore.persistable.DestTaxSeServiceLn;
import org.beigesoft.webstore.persistable.Deliv;

/**
 * <p>Service that retrieve/create buyer's shopping cart, make cart totals
 * after any line action, etc.
 * This is shared non-transactional service.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class SrvShoppingCart<RS> implements ISrvShoppingCart {

  /**
   * <p>Logger.</p>
   **/
  private ILog logger;

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for trading settings.</p>
   **/
  private ISrvTradingSettings srvTradingSettings;

  /**
   * <p>Query taxes invoice basis non-aggregate.</p>
   **/
  private String quTxInvBas;

  /**
   * <p>Query taxes invoice basis aggregate.</p>
   **/
  private String quTxInvBasAggr;

  /**
   * <p>Query taxes item basis non-aggregate.</p>
   **/
  private String quTxItBas;

  /**
   * <p>Query taxes item basis aggregate.</p>
   **/
  private String quTxItBasAggr;

  /**
   * <p>Query item price.</p>
   **/
  private String quItemPrice;

  /**
   * <p>Query S.E. item price.</p>
   **/
  private String quItemSePrice;

  /**
   * <p>Query item price for category.</p>
   **/
  private String quItemPriceCat;

  /**
   * <p>Query S.E. item price for category.</p>
   **/
  private String quItemSePriceCat;

  /**
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  /**
   * <p>PPL ready class-flag.</p>
   **/
  private Class<?> pplCl;

  /**
   * <p>Buyer service.</p>
   **/
  private IBuySr buySr;

  /**
   * <p>Get/Create Cart.</p>
   * @param pRqVs additional param
   * @param pRqDt Request Data
   * @param pIsNeedToCreate  if need to create, e.g. "NO" for deleting item from
   *  cart, "YES" for adding one.
   * @param pIsBuAuth buyer must be authorized
   * @return shopping cart or null
   * @throws Exception - an exception
   **/
  @Override
  public final Cart getShoppingCart(final Map<String, Object> pRqVs,
    final IRequestData pRqDt, final boolean pIsNeedToCreate,
      final boolean pIsBuAuth) throws Exception {
    OnlineBuyer buyer = null;
    boolean burNew = false;
    if (pIsBuAuth) {
      buyer = this.buySr.getAuthBuyr(pRqVs, pRqDt);
    } else {
      buyer = (OnlineBuyer) pRqDt.getAttribute("buyr");
      if (buyer == null) {
        buyer = this.buySr.getBuyr(pRqVs, pRqDt);
      }
      if (buyer == null && pIsNeedToCreate) {
        buyer = this.buySr.createBuyr(pRqVs, pRqDt);
        if (buyer.getIsNew()) {
          getSrvOrm().insertEntity(pRqVs, buyer);
          buyer.setIsNew(false);
        } else {
          getSrvOrm().updateEntity(pRqVs, buyer);
        }
        pRqDt.setCookieValue("cBuyerId", buyer.getItsId().toString());
        burNew = true;
      }
    }
    TradingSettings ts = srvTradingSettings.lazyGetTradingSettings(pRqVs);
    pRqDt.setAttribute("buyr", buyer);
    Cart cart = (Cart) pRqDt.getAttribute("cart");
    if (cart == null && buyer !=  null) {
      cart = retrCart(pRqVs, buyer, false);
      if (cart == null && pIsNeedToCreate) {
        cart = new Cart();
        Currency curr = (Currency) pRqVs.get("wscurr");
        cart.setPayMeth(ts.getDefaultPaymentMethod());
        cart.setCurr(curr);
        cart.setItems(new ArrayList<CartLn>());
        cart.setTaxes(new ArrayList<CartTxLn>());
        cart.setTotals(new ArrayList<CartTot>());
        cart.setItsId(buyer);
        getSrvOrm().insertEntity(pRqVs, cart);
      } else if (cart != null && burNew) {
        emptyCart(pRqVs, cart);
      }
      if (cart != null) {
        pRqDt.setAttribute("cart", cart);
      }
    }
    if (cart != null) {
      if (EPaymentMethod.ANY.equals(cart.getPayMeth())
        || EPaymentMethod.PARTIAL_ONLINE.equals(cart.getPayMeth())
          || EPaymentMethod.ONLINE.equals(cart.getPayMeth())) {
        cart.setPayMeth(EPaymentMethod.PAY_CASH);
      }
      if ((EPaymentMethod.PAYPAL.equals(cart.getPayMeth())
        || EPaymentMethod.PAYPAL_ANY.equals(cart.getPayMeth()))
          && pplCl == null) {
        try {
          pplCl = Class.forName("com.paypal.api.payments.Item");
        } catch (ClassNotFoundException e) {
          cart.setPayMeth(EPaymentMethod.PAY_CASH);
        }
      }
      List<EPaymentMethod> payMts = new ArrayList<EPaymentMethod>();
      pRqVs.put("payMts", payMts);
      payMts.add(EPaymentMethod.PAY_CASH);
      payMts.add(EPaymentMethod.BANK_TRANSFER);
      payMts.add(EPaymentMethod.BANK_CHEQUE);
      if (pplCl != null) {
        payMts.add(EPaymentMethod.PAYPAL);
      }
    }
    return cart;
  }

  /**
   * <p>Refresh cart totals by seller cause line inserted/changed/deleted.</p>
   * @param pRqVs request scoped vars
   * @param pTs TradingSettings
   * @param pCartLn affected cart line
   * @param pAs Accounting Settings
   * @param pTxRules NULL if not taxable
   * @throws Exception - an exception.
   **/
  @Override
  public final void makeCartTotals(final Map<String, Object> pRqVs,
    final TradingSettings pTs, final CartLn pCartLn, final AccSettings pAs,
      final TaxDestination pTxRules) throws Exception {
    BigDecimal txTot = BigDecimal.ZERO;
    BigDecimal txTotSe = BigDecimal.ZERO;
    String descr = null;
    if (pTxRules != null) {
        descr = "Tax rules: aggregate/invoice basis/zip/RM = " + pTxRules
    .getSalTaxUseAggregItBas() + "/" + pTxRules.getSalTaxIsInvoiceBase()
  + "/" + pTxRules.getRegZip() + "/" + pTxRules.getSalTaxRoundMode();
      boolean isDbgSh = getLogger().getDbgSh(this.getClass())
        && getLogger().getDbgFl() < 13001 && getLogger().getDbgCl() > 13003;
      if (isDbgSh) {
        String txCat;
        if (pCartLn.getTxCat() != null) {
          txCat = pCartLn.getTxCat().getItsName();
        } else {
          txCat = "-";
        }
        getLogger().debug(pRqVs, SrvShoppingCart.class,
          "Item: name/tax category/disabled = " + pCartLn.getItsName() + "/"
            + txCat + "/" + pCartLn.getDisab());
      }
      for (CartTxLn ctl : pCartLn.getItsOwner().getTaxes()) {
        if (!ctl.getDisab() && (ctl.getSeller() == null
          && pCartLn.getSeller() == null || ctl.getSeller() != null
            && pCartLn.getSeller() != null && pCartLn.getSeller().getItsId()
              .getItsId().equals(ctl.getSeller().getItsId().getItsId()))) {
          ctl.setDisab(true);
        }
      }
      //data storage for aggregate rate
      //and non-aggregate invoice basis taxes included:
      List<CartLn> txdLns = null;
      //data storages for non-aggregate rate
      //except invoice basis with included taxes:
      List<Tax> txs = null; //taxes
      List<Double> txTotTaxb = null; //tax's totals/taxables
      List<Double> txPerc = null; //tax's percents for invoice basis
      String query;
      if (!pTxRules.getSalTaxUseAggregItBas()
        && !(pTxRules.getSalTaxIsInvoiceBase() && !pTs.getTxExcl())) {
        //non-aggregate except invoice basis with included taxes:
        txs = new ArrayList<Tax>();
        txTotTaxb = new ArrayList<Double>();
        if (!pTxRules.getSalTaxIsInvoiceBase()) {
          //item basis:
          query = lazyGetQuTxItBas();
        } else {
          //invoice basis, taxes excluded:
          txPerc = new ArrayList<Double>();
          query = lazyGetQuTxInvBas();
        }
      } else { //non-aggregate invoice basis with included taxes
        //and aggregate for others:
        txdLns = new ArrayList<CartLn>();
        if (!pTxRules.getSalTaxIsInvoiceBase()) { //item basis
          query = lazyGetQuTxItBasAggr();
        } else { //invoice basis:
          query = lazyGetQuTxInvBasAggr();
        }
      }
      String condSel;
      if (pCartLn.getSeller() == null) {
        condSel = " is null";
      } else {
        condSel = "=" + pCartLn.getSeller().getItsId().getItsId();
      }
      query = query.replace(":CARTID", pCartLn.getItsOwner().getBuyer()
        .getItsId().toString()).replace(":CONDSEL", condSel);
      IRecordSet<RS> recordSet = null;
      try {
        recordSet = getSrvDatabase().retrieveRecords(query);
        if (recordSet.moveToFirst()) {
          do {
            Long txId = recordSet.getLong("TAXID");
            String txNm = recordSet.getString("TAXNAME");
            Tax tax = new Tax();
            tax.setItsId(txId);
            tax.setItsName(txNm);
            if (!pTxRules.getSalTaxUseAggregItBas()
              && !(pTxRules.getSalTaxIsInvoiceBase() && !pTs.getTxExcl())) {
              //non-aggregate except invoice basis with included taxes:
              txs.add(tax);
              if (!pTxRules.getSalTaxIsInvoiceBase()) {
                //item basis, taxes excluded/included:
                txTotTaxb.add(recordSet.getDouble("TOTALTAX"));
              } else {
                //invoice basis, taxes excluded:
                txPerc.add(recordSet.getDouble("ITSPERCENTAGE"));
                txTotTaxb.add(recordSet.getDouble("SUBTOTAL"));
              }
            } else { //non-aggregate invoice basis with included taxes
              //and aggregate for others:
              Double percent = recordSet.getDouble("ITSPERCENTAGE");
              Long tcId = recordSet.getLong("TAXCATID");
              if (!pTxRules.getSalTaxIsInvoiceBase()) { //item basis:
                Long clId = recordSet.getLong("CLID");
                CartLn txdLn = makeTxdLine(txdLns, clId, tcId, tax, percent,
                  pAs);
                txdLn.setTotTx(BigDecimal.valueOf(recordSet
                  .getDouble("TOTALTAXES"))
                    .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
              } else { //invoice basis:
                CartLn txdLn = makeTxdLine(txdLns, tcId, tcId, tax, percent,
                  pAs);
                txdLn.setTot(BigDecimal.valueOf(recordSet
                  .getDouble("ITSTOTAL"))
                    .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
                txdLn.setSubt(BigDecimal.valueOf(recordSet
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
      if (!pTxRules.getSalTaxUseAggregItBas()
        && !(pTxRules.getSalTaxIsInvoiceBase() && !pTs.getTxExcl())) {
        //non-aggregate except invoice basis with included taxes:
        for (int i = 0; i < txs.size(); i++) {
          CartTxLn ctl = findCreateTaxLine(pRqVs, pCartLn.getItsOwner(),
            txs.get(i), pCartLn.getSeller(), false);
          Double txTotd;
          if (!pTxRules.getSalTaxIsInvoiceBase()) {
            //item basis, taxes excluded/included:
            txTotd = txTotTaxb.get(i);
          } else {
            //invoice basis, taxes excluded:
            txTotd = txTotTaxb.get(i) * txPerc.get(i) / 100.0;
            ctl.setTaxab(BigDecimal.valueOf(txTotTaxb.get(i)));
          }
          ctl.setTot(BigDecimal.valueOf(txTotd).setScale(pAs.
            getPricePrecision(), pTxRules.getSalTaxRoundMode()));
          if (ctl.getIsNew()) {
            getSrvOrm().insertEntity(pRqVs, ctl);
            ctl.setIsNew(false);
          } else {
            getSrvOrm().updateEntity(pRqVs, ctl);
          }
        }
      } else { //non-aggregate invoice basis with included taxes
        //and aggregate for others:
        BigDecimal bd100 = new BigDecimal("100.00");
        Comparator<InvItemTaxCategoryLine> cmpr = Collections
          .reverseOrder(new CmprTaxCatLnRate());
        for (CartLn txdLn : txdLns) {
          int ti = 0;
          //aggregate rate line scoped storages:
          BigDecimal taxAggegated = null;
          BigDecimal taxAggrAccum = BigDecimal.ZERO;
          Collections.sort(txdLn.getTxCat().getTaxes(), cmpr);
          for (InvItemTaxCategoryLine itcl : txdLn.getTxCat().getTaxes()) {
            ti++;
            if (taxAggegated == null) {
              if (pTxRules.getSalTaxIsInvoiceBase()) { //invoice basis:
                if (!pTs.getTxExcl()) {
                  taxAggegated = txdLn.getTot().subtract(txdLn.getTot().divide(
                BigDecimal.ONE.add(txdLn.getTxCat().getAggrOnlyPercent().divide(
              bd100)), pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
                } else {
                  taxAggegated = txdLn.getSubt().multiply(txdLn.getTxCat()
                .getAggrOnlyPercent()).divide(bd100, pAs.getPricePrecision(),
              pTxRules.getSalTaxRoundMode());
                }
              } else {
                //item basis, taxes included/excluded
                taxAggegated = txdLn.getTotTx();
              }
            }
            if (ti < txdLn.getTxCat().getTaxes().size()) {
              txdLn.setTotTx(taxAggegated.multiply(itcl.getItsPercentage())
                .divide(txdLn.getTxCat().getAggrOnlyPercent(),
                  pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
              taxAggrAccum = taxAggrAccum.add(txdLn.getTotTx());
            } else { //the rest or only tax:
              txdLn.setTotTx(taxAggegated.subtract(taxAggrAccum));
            }
            CartTxLn ctl = findCreateTaxLine(pRqVs, pCartLn.getItsOwner(),
              itcl.getTax(), pCartLn.getSeller(), true);
            ctl.setTot(ctl.getTot().add(txdLn.getTotTx()));
            if (pTxRules.getSalTaxIsInvoiceBase()) {
              if (pTs.getTxExcl()) {
                ctl.setTaxab(ctl.getTaxab().add(txdLn.getSubt()));
              } else {
                ctl.setTaxab(ctl.getTaxab().add(txdLn.getTot()));
              }
            }
            if (ctl.getIsNew()) {
              getSrvOrm().insertEntity(pRqVs, ctl);
              ctl.setIsNew(false);
            } else {
              getSrvOrm().updateEntity(pRqVs, ctl);
            }
          }
        }
      }
      for (CartTxLn ctl : pCartLn.getItsOwner().getTaxes()) {
        if (!ctl.getDisab()) {
          if (ctl.getSeller() == null && pCartLn.getSeller() == null
            || ctl.getSeller() != null && pCartLn.getSeller() != null
              && pCartLn.getSeller().getItsId().getItsId()
                .equals(ctl.getSeller().getItsId().getItsId())) {
            txTotSe = txTotSe.add(ctl.getTot());
          }
          txTot = txTot.add(ctl.getTot());
        } else if (ctl.getSeller() == null && pCartLn.getSeller() == null
          || ctl.getSeller() != null && pCartLn.getSeller() != null
            && pCartLn.getSeller().getItsId().getItsId()
              .equals(ctl.getSeller().getItsId().getItsId())) {
          getSrvOrm().updateEntity(pRqVs, ctl);
        }
      }
    }
    BigDecimal tot = BigDecimal.ZERO;
    BigDecimal totSe = BigDecimal.ZERO;
    for (CartLn cl : pCartLn.getItsOwner().getItems()) {
      if (!cl.getDisab()) {
        if (cl.getSeller() == null && pCartLn.getSeller() == null
          || cl.getSeller() != null && pCartLn.getSeller() != null
            && pCartLn.getSeller().getItsId().getItsId()
              .equals(cl.getSeller().getItsId().getItsId())) {
          totSe = totSe.add(cl.getTot());
        }
        tot = tot.add(cl.getTot());
      }
    }
    pCartLn.getItsOwner().setTotTx(txTot);
    pCartLn.getItsOwner().setSubt(tot.subtract(txTot));
    pCartLn.getItsOwner().setTot(tot);
    pCartLn.getItsOwner().setDescr(descr);
    getSrvOrm().updateEntity(pRqVs, pCartLn.getItsOwner());
    CartTot cartTot = null;
    for (CartTot ct : pCartLn.getItsOwner().getTotals()) {
      if (!ct.getDisab() && (ct.getSeller() == null && pCartLn
        .getSeller() == null || ct.getSeller() != null && pCartLn
          .getSeller() != null && pCartLn.getSeller().getItsId().getItsId()
            .equals(ct.getSeller().getItsId().getItsId()))) {
        cartTot = ct;
        break;
      }
    }
    if (totSe.compareTo(BigDecimal.ZERO) == 0 && cartTot != null) {
      //last seller's line has been deleted, disable enabled:
      cartTot.setDisab(true);
      getSrvOrm().updateEntity(pRqVs, cartTot);
    } else if (totSe.compareTo(BigDecimal.ZERO) == 1) {
      if (cartTot == null) {
        for (CartTot ct : pCartLn.getItsOwner().getTotals()) {
          if (ct.getDisab()) {
            cartTot = ct;
            cartTot.setDisab(false);
            break;
          }
        }
      }
      if (cartTot == null) {
        cartTot = new CartTot();
        cartTot.setItsOwner(pCartLn.getItsOwner());
        cartTot.setIsNew(true);
      }
      cartTot.setSeller(pCartLn.getSeller());
      cartTot.setTotTx(txTotSe);
      cartTot.setSubt(totSe.subtract(txTotSe));
      cartTot.setTot(totSe);
      if (cartTot.getIsNew()) {
        getSrvOrm().insertEntity(pRqVs, cartTot);
      } else {
        getSrvOrm().updateEntity(pRqVs, cartTot);
      }
    }
  }

  /**
   * <p>Reveal shared tax rules for cart. It also makes buyer-regCustomer.</p>
   * @param pRqVs request scoped vars
   * @param pCart cart
   * @param pAs Accounting Settings
   * @return tax rules, NULL if not taxable
   * @throws Exception - an exception.
   **/
  @Override
  public final TaxDestination revealTaxRules(final Map<String, Object> pRqVs,
    final Cart pCart, final AccSettings pAs) throws Exception {
    if (pCart.getBuyer().getRegCustomer() == null) {
      //copy buyer info into non-persistable customer.
      pCart.getBuyer().setRegCustomer(new DebtorCreditor());
      pCart.getBuyer().getRegCustomer()
        .setIsForeigner(pCart.getBuyer().getForeig());
      pCart.getBuyer().getRegCustomer()
        .setRegZip(pCart.getBuyer().getRegZip());
      pCart.getBuyer().getRegCustomer()
        .setTaxDestination(pCart.getBuyer().getTaxDest());
    }
    TaxDestination txRules = null;
    if (pAs.getIsExtractSalesTaxFromSales()
      && !pCart.getBuyer().getRegCustomer().getIsForeigner()) {
      if (pCart.getBuyer().getRegCustomer().getTaxDestination() != null) {
        //override tax method:
        txRules = pCart.getBuyer().getRegCustomer().getTaxDestination();
      } else {
        txRules = new TaxDestination();
        txRules.setSalTaxIsInvoiceBase(pAs.getSalTaxIsInvoiceBase());
        txRules.setSalTaxUseAggregItBas(pAs.getSalTaxUseAggregItBas());
        txRules.setSalTaxRoundMode(pAs.getSalTaxRoundMode());
      }
    }
    return txRules;
  }

  /**
   * <p>Handle event cart currency changed.</p>
   * @param pRqVs request scoped vars
   * @param pCart cart
   * @param pAs Accounting Settings
   * @param pTs TradingSettings
   * @throws Exception - an exception.
   **/
  @Override
  public final void handleCurrencyChanged(final Map<String, Object> pRqVs,
    final Cart pCart, final AccSettings pAs,
      final TradingSettings pTs) throws Exception {
    TaxDestination txRules = revealTaxRules(pRqVs, pCart, pAs);
    CartLn clf = null;
    for (CartLn cl : pCart.getItems()) {
      if (!cl.getDisab()) {
        if (cl.getForc()) {
          clf = cl;
          delLine(pRqVs, cl, txRules);
        } else {
          makeCartLine(pRqVs, cl, pAs, pTs, txRules, true, true);
          makeCartTotals(pRqVs, pTs, cl, pAs, txRules);
        }
      }
    }
    if (clf != null) {
      hndCartChan(pRqVs, pCart, txRules);
    }
  }

  /**
   * <p>Deletes cart line.</p>
   * @param pRqVs request scoped vars
   * @param pCartLn cart line
   * @param pTxRules Tax Rules
   * @throws Exception - an exception.
   **/
  @Override
  public final void delLine(final Map<String, Object> pRqVs,
    final CartLn pCartLn, final TaxDestination pTxRules) throws Exception {
    AccSettings as = (AccSettings) pRqVs.get("accSet");
    TradingSettings ts = (TradingSettings) pRqVs.get("tradSet");
    pCartLn.setDisab(true);
    String[] fieldsNames = new String[] {"itsId", "itsVersion", "disab"};
    pRqVs.put("fieldsNames", fieldsNames);
    getSrvOrm().updateEntity(pRqVs, pCartLn);
    if (pTxRules != null && pCartLn.getTxCat() != null && !pTxRules
      .getSalTaxIsInvoiceBase() && !pTxRules.getSalTaxUseAggregItBas()) {
      pRqVs.put("CartItTxLnitsOwnerdeepLevel", 1);
      List<CartItTxLn> itls = getSrvOrm().retrieveListWithConditions(
          pRqVs, CartItTxLn.class, "where DISAB=0 and ITSOWNER="
            + pCartLn.getItsId());
      pRqVs.remove("CartItTxLnitsOwnerdeepLevel");
      for (CartItTxLn itl : itls) {
        if (!itl.getDisab() && itl.getItsOwner().getItsId()
          .equals(pCartLn.getItsId())) {
          itl.setDisab(true);
          getSrvOrm().updateEntity(pRqVs, itl);
        }
      }
    }
    pRqVs.remove("fieldsNames");
    makeCartTotals(pRqVs, ts, pCartLn, as, pTxRules);
  }

  /**
   * <p>Handle event cart delivering or line changed
   * and redone forced service if need.</p>
   * @param pRqVs request scoped vars
   * @param pCart cart
   * @param pTxRules Tax Rules
   * @throws Exception - an exception.
   **/
  @Override
  public final void hndCartChan(final Map<String, Object> pRqVs,
    final Cart pCart, final TaxDestination pTxRules) throws Exception {
    @SuppressWarnings("unchecked")
    List<Deliv> dlvMts = (List<Deliv>) pRqVs.get("dlvMts");
    Deliv cdl = null;
    for (Deliv dl : dlvMts) {
      if (dl.getItsId().equals(pCart.getDeliv())) {
        cdl = dl;
        break;
      }
    }
    if (cdl == null) {
      throw new Exception("wrong delivering!");
    }
    //it must be at least one item to add forced service:
    boolean crtEmpty = true;
    CartLn clFrc = null;
    CartLn clEm = null;
    for (CartLn cl : pCart.getItems()) {
      if (cl.getDisab()) {
        clEm = cl;
      } else if (!cl.getDisab() && cl.getForc()) {
        clFrc = cl;
      } else if (!cl.getDisab() && !cl.getForc()) {
        crtEmpty = false;
      }
    }
    if (clFrc == null && cdl.getFrcSr() == null || cdl.getApMt() == null) {
      return;
    }
    if (crtEmpty) {
      if (clFrc != null) {
        delLine(pRqVs, clFrc, pTxRules);
      }
      return;
    }
    int cartTot;
    AccSettings as = (AccSettings) pRqVs.get("accSet");
    TradingSettings ts = (TradingSettings) pRqVs.get("tradSet");
    BigDecimal ct = pCart.getTot();
    if (clFrc != null && clFrc.getTot().compareTo(BigDecimal.ZERO) == 1) {
      ct = ct.subtract(clFrc.getTot());
    }
    if (pCart.getExcRt().compareTo(BigDecimal.ONE) == 0) {
      cartTot = ct.intValue();
    } else {
      cartTot = ct.divide(pCart.getExcRt(), as.getPricePrecision(),
        as.getRoundingMode()).intValue();
    }
    if (cartTot >= cdl.getApMt()) {
      if (clFrc != null && clFrc.getTot().compareTo(BigDecimal.ZERO) == 1) {
        clFrc.setPrice(BigDecimal.ZERO);
        clFrc.setTot(BigDecimal.ZERO);
        clFrc.setTotTx(BigDecimal.ZERO);
        clFrc.setSubt(BigDecimal.ZERO);
        clFrc.setTxDsc(null);
        clFrc.setTxCat(null);
        this.srvOrm.updateEntity(pRqVs, clFrc);
        makeCartTotals(pRqVs, ts, clFrc, as, pTxRules);
      }
    } else {
      if (clFrc == null) {
        if (clEm == null) {
          clFrc = new CartLn();
          clFrc.setIsNew(true);
          clFrc.setItsOwner(pCart);
          pCart.getItems().add(clFrc);
        } else {
          clFrc = clEm;
        }
        clFrc.setSel(null);
        clFrc.setForc(true);
        clFrc.setDisab(false);
        clFrc.setItTyp(EShopItemType.SERVICE);
        clFrc.setItId(cdl.getFrcSr().getItsId());
        clFrc.setItsName(cdl.getFrcSr().getItsName());
        clFrc.setUom(cdl.getFrcSr().getDefUnitOfMeasure());
        clFrc.setAvQuan(BigDecimal.ONE);
        clFrc.setQuant(BigDecimal.ONE);
        clFrc.setUnStep(BigDecimal.ONE);
        makeCartLine(pRqVs, clFrc, as, ts, pTxRules, true, true);
        makeCartTotals(pRqVs, ts, clFrc, as, pTxRules);
      } else if (clFrc.getTot().compareTo(BigDecimal.ZERO) == 0) {
        makeCartLine(pRqVs, clFrc, as, ts, pTxRules, true, true);
        makeCartTotals(pRqVs, ts, clFrc, as, pTxRules);
      }
    }
  }

  /**
   * <p>Makes cart line.</p>
   * @param pRqVs request scoped vars
   * @param pCartLn cart line
   * @param pAs Accounting Settings
   * @param pTs TradingSettings
   * @param pTxRules NULL if not taxable
   * @param pRedoPr redo price
   * @param pRedoTxc redo tax category
   * @throws Exception - an exception.
   **/
  @Override
  public final void makeCartLine(final Map<String, Object> pRqVs,
    final CartLn pCartLn, final AccSettings pAs, final TradingSettings pTs,
      final TaxDestination pTxRules, final boolean pRedoPr,
        final boolean pRedoTxc) throws Exception {
    AItemPrice<?, ?> itPrice = null;
    if (pRedoPr || pRedoTxc) {
      itPrice = revealItemPrice(pRqVs, pTs, pCartLn.getItsOwner().getBuyer(),
        pCartLn.getItTyp(), pCartLn.getItId());
      if (pCartLn.getItTyp().equals(EShopItemType.SESERVICE)
        || pCartLn.getItTyp().equals(EShopItemType.SEGOODS)) {
        IHasSeSeller seItPr = (IHasSeSeller) itPrice;
        pCartLn.setSeller(seItPr.getSeller());
      }
      pCartLn.setPrice(itPrice.getItsPrice());
      pCartLn.setItsName(itPrice.getItem().getItsName());
      BigDecimal qosr = pCartLn.getQuant().remainder(itPrice.getUnStep());
      if (qosr.compareTo(BigDecimal.ZERO) != 0) {
        pCartLn.setQuant(pCartLn.getQuant().subtract(qosr));
      }
      @SuppressWarnings("unchecked")
      List<CurrRate> currRates = (List<CurrRate>) pRqVs.get("currRates");
      for (CurrRate cr: currRates) {
        if (cr.getCurr().getItsId().equals(pCartLn.getItsOwner()
          .getCurr().getItsId())) {
          BigDecimal exchRate = cr.getRate();
          if (exchRate.compareTo(BigDecimal.ZERO) == -1) {
            exchRate = BigDecimal.ONE.divide(exchRate.negate(), 15,
              RoundingMode.HALF_UP);
          }
          pCartLn.getItsOwner().setExcRt(exchRate);
          pCartLn.setPrice(pCartLn.getPrice().multiply(exchRate)
            .setScale(pAs.getPricePrecision(), pAs.getRoundingMode()));
          break;
        }
      }
      BigDecimal amount = pCartLn.getPrice().multiply(pCartLn.getQuant()).
        setScale(pAs.getPricePrecision(), pAs.getRoundingMode());
      if (pTs.getTxExcl()) {
        pCartLn.setSubt(amount);
      } else {
        pCartLn.setTot(amount);
      }
    }
    if (pRedoTxc && pTxRules != null) {
      AItem<?, ?> item = (AItem<?, ?>) itPrice.getItem();
      pCartLn.setTxCat(item.getTaxCategory());
      if (pTs.getTxDests() && pCartLn.getItsOwner().getBuyer()
        .getRegCustomer().getTaxDestination() != null) {
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
        pRqVs.put(dstTxItLnCl.getSimpleName() + "itsOwnerdeepLevel", 1);
        @SuppressWarnings("unchecked")
        List<ADestTaxItemLn<?>> dtls = (List<ADestTaxItemLn<?>>) getSrvOrm()
          .retrieveListWithConditions(pRqVs, dstTxItLnCl,
            "where ITSOWNER=" + pCartLn.getItId());
        pRqVs.remove(dstTxItLnCl.getSimpleName() + "itsOwnerdeepLevel");
        for (ADestTaxItemLn<?> dtl : dtls) {
          if (dtl.getTaxDestination().getItsId().equals(pCartLn.getItsOwner()
            .getBuyer().getRegCustomer().getTaxDestination().getItsId())) {
            pCartLn.setTxCat(dtl.getTaxCategory()); //it may be null
            break;
          }
        }
      }
    }
    BigDecimal totalTaxes = BigDecimal.ZERO;
    List<CartItTxLn> itls = null;
    if (pTxRules != null && pCartLn.getTxCat() != null) {
      if (!pTxRules.getSalTaxIsInvoiceBase()) {
        BigDecimal bd100 = new BigDecimal("100.00");
        if (!pTxRules.getSalTaxUseAggregItBas()) {
          itls = new ArrayList<CartItTxLn>();
          pRqVs.put("InvItemTaxCategoryLineitsOwnerdeepLevel", 1);
          List<InvItemTaxCategoryLine> itcls = getSrvOrm()
        .retrieveListWithConditions(pRqVs, InvItemTaxCategoryLine.class,
      "where ITSOWNER=" + pCartLn.getTxCat().getItsId()
    + " order by INVITEMTAXCATEGORYLINE.ITSPERCENTAGE");
          pRqVs.remove("InvItemTaxCategoryLineitsOwnerdeepLevel");
          BigDecimal taxTot = null;
          BigDecimal taxRest = null;
          if (!pTs.getTxExcl()) {
            taxTot = pCartLn.getTot().subtract(pCartLn.getTot()
        .divide(BigDecimal.ONE.add(pCartLn.getTxCat().getAggrOnlyPercent()
      .divide(bd100)), pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
            taxRest = taxTot;
          }
          StringBuffer sb = new StringBuffer();
          int i = 0;
          for (InvItemTaxCategoryLine itcl : itcls) {
           if (ETaxType.SALES_TAX_OUTITEM.equals(itcl.getTax().getItsType())
          || ETaxType.SALES_TAX_INITEM.equals(itcl.getTax().getItsType())) {
              if (i++ > 0) {
                sb.append(", ");
              }
              CartItTxLn itl = new CartItTxLn();
              itl.setIsNew(true);
              itl.setTax(itcl.getTax());
              itls.add(itl);
              BigDecimal addTx;
              if (!pTs.getTxExcl()) {
                if (i < itcls.size()) {
                  addTx = taxTot.multiply(itcl.getItsPercentage()).divide(
                    pCartLn.getTxCat().getAggrOnlyPercent(),
                      pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode());
                  taxRest = taxRest.subtract(addTx);
                } else {
                  addTx = taxRest;
                }
              } else {
                addTx = pCartLn.getSubt().multiply(itcl.getItsPercentage())
        .divide(bd100, pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode());
              }
              totalTaxes = totalTaxes.add(addTx);
              itl.setTot(addTx);
              sb.append(itl.getTax().getItsName() + " " + prn(pRqVs, addTx));
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
      } else {
        pCartLn.setTxDsc(pCartLn.getTxCat().getItsName());
      }
    }
    pCartLn.setTotTx(totalTaxes);
    if (pTs.getTxExcl()) {
      pCartLn.setTot(pCartLn.getSubt().add(pCartLn.getTotTx()));
    } else {
      pCartLn.setSubt(pCartLn.getTot().subtract(pCartLn.getTotTx()));
    }
    if (pCartLn.getIsNew()) {
      getSrvOrm().insertEntity(pRqVs, pCartLn);
    } else {
      getSrvOrm().updateEntity(pRqVs, pCartLn);
    }
    if (itls != null) {
      pRqVs.put("CartItTxLnitsOwnerdeepLevel", 1);
      List<CartItTxLn> itlsr = getSrvOrm().retrieveListWithConditions(
          pRqVs, CartItTxLn.class, "where CARTID="
            + pCartLn.getItsOwner().getBuyer().getItsId());
      pRqVs.remove("CartItTxLnitsOwnerdeepLevel");
      for (CartItTxLn itlrt : itlsr) {
        if (!itlrt.getDisab() && itlrt.getItsOwner().getItsId()
          .equals(pCartLn.getItsId())) {
          itlrt.setDisab(true);
        }
      }
      for (CartItTxLn itl : itls) {
        CartItTxLn itlr = null;
        for (CartItTxLn itlrt : itlsr) {
          if (itlrt.getDisab()) {
            itlr = itlrt;
            itlr.setDisab(false);
            break;
          }
        }
        if (itlr == null) {
          itl.setItsOwner(pCartLn);
          if (pCartLn.getSeller() != null) {
            itl.setSellerId(pCartLn.getSeller().getItsId().getItsId());
          }
          itl.setCartId(pCartLn.getItsOwner().getBuyer().getItsId());
          itl.setItsOwner(pCartLn);
          getSrvOrm().insertEntity(pRqVs, itl);
          itl.setIsNew(false);
        } else {
          itlr.setTax(itl.getTax());
          itlr.setTot(itl.getTot());
          itlr.setItsOwner(pCartLn);
          if (pCartLn.getSeller() == null) {
            itlr.setSellerId(null);
          } else {
            itlr.setSellerId(pCartLn.getSeller().getItsId().getItsId());
          }
          itlr.setCartId(pCartLn.getItsOwner().getBuyer().getItsId());
          getSrvOrm().updateEntity(pRqVs, itlr);
        }
      }
      for (CartItTxLn itlrt : itlsr) {
        if (itlrt.getDisab() && itlrt.getItsOwner().getItsId()
          .equals(pCartLn.getItsId())) {
          getSrvOrm().updateEntity(pRqVs, itlrt);
        }
      }
    }
  }

  /**
   * <p>Reveals item's price descriptor.</p>
   * @param pRqVs request scoped vars
   * @param pTs TradingSettings
   * @param pBuyer Buyer
   * @param pItType Item Type
   * @param pItId Item ID
   * @return item's price descriptor or exception
   * @throws Exception - an exception
   **/
  @Override
  public final AItemPrice<?, ?> revealItemPrice(
    final Map<String, Object> pRqVs, final TradingSettings pTs,
      final OnlineBuyer pBuyer, final EShopItemType pItType,
        final Long pItId) throws Exception {
    String lang = (String) pRqVs.get("lang");
    AItemPrice<?, ?> itPrice = null;
    String query;
    Class<?> itemI18nCl;
    Class<?> itemCl;
    Class<?> itemPriceCl;
    if (pItType.equals(EShopItemType.GOODS)) {
      itemCl = InvItem.class;
      itemI18nCl = I18nInvItem.class;
      itemPriceCl = PriceGoods.class;
    } else if (pItType.equals(EShopItemType.SERVICE)) {
      itemCl = ServiceToSale.class;
      itemI18nCl = I18nServiceToSale.class;
      itemPriceCl = ServicePrice.class;
    } else if (pItType.equals(EShopItemType.SESERVICE)) {
      itemCl = SeService.class;
      itemI18nCl = I18nSeService.class;
      itemPriceCl = SeServicePrice.class;
    } else {
      itemCl = SeGoods.class;
      itemI18nCl = I18nSeGoods.class;
      itemPriceCl = SeGoodsPrice.class;
    }
    Set<String> ndFlItPr = new HashSet<String>();
    ndFlItPr.add("item");
    ndFlItPr.add("priceCategory");
    ndFlItPr.add("itsPrice");
    ndFlItPr.add("unStep");
    pRqVs.put(itemPriceCl.getSimpleName() + "neededFields", ndFlItPr);
    pRqVs.put(itemPriceCl.getSimpleName() + "priceCategorydeepLevel", 1);
    Set<String> ndFlIt = new HashSet<String>();
    ndFlIt.add("itsId");
    ndFlIt.add("itsName");
    ndFlIt.add("taxCategory");
    if (pItType.equals(EShopItemType.SEGOODS)
      || pItType.equals(EShopItemType.SESERVICE)) {
      Set<String> ndFlNm = new HashSet<String>();
      ndFlNm.add("itsId");
      ndFlNm.add("itsName");
      ndFlIt.add("seller");
      Set<String> ndFlSe = new HashSet<String>();
      ndFlSe.add("seller");
      pRqVs.put("SeSellerneededFields", ndFlSe);
      pRqVs.put("DebtorCreditorneededFields", ndFlNm);
      pRqVs.put(itemCl.getSimpleName() + "sellerdeepLevel", 3);
    }
    pRqVs.put(itemCl.getSimpleName() + "neededFields", ndFlIt);
    Set<String> ndFlTc = new HashSet<String>();
    ndFlTc.add("itsId");
    ndFlTc.add("itsName");
    ndFlTc.add("aggrOnlyPercent");
    pRqVs.put(InvItemTaxCategory.class.getSimpleName() + "neededFields",
      ndFlTc);
    pRqVs.put(itemPriceCl.getSimpleName() + "itemdeepLevel", 3);
    if (pTs.getIsUsePriceForCustomer() && pBuyer != null) {
      //try to reveal price dedicated to customer:
      List<BuyerPriceCategory> buyerPrCats = getSrvOrm()
        .retrieveListWithConditions(pRqVs, BuyerPriceCategory.class,
          "where BUYER=" + pBuyer.getItsId());
      if (buyerPrCats.size() > 1) {
        this.logger.error(pRqVs, SrvShoppingCart.class,
          "Several price category for same buyer! buyer ID="
            + pBuyer.getItsId());
        throw new ExceptionWithCode(ExceptionWithCode.CONFIGURATION_MISTAKE,
          "several_price_category_for_same_buyer");
      }
      if (buyerPrCats.size() == 1) {
        if (pItType.equals(EShopItemType.GOODS)
          || pItType.equals(EShopItemType.SERVICE)) {
          query = lazyGetQuItemPriceCat();
        } else {
          query = lazyGetQuItemSePriceCat();
        }
        query = query.replace(":ITEMID", pItId.toString());
        query = query.replace(":LANG", lang);
        query = query.replace(":TITEMPRICE", itemPriceCl.getSimpleName()
          .toUpperCase());
        query = query.replace(":TITEM", itemCl.getSimpleName().toUpperCase());
        query = query.replace(":TI18NITEM", itemI18nCl.getSimpleName()
          .toUpperCase());
        StringBuffer pccnd = new StringBuffer("");
        pccnd.append("=" + buyerPrCats.get(0).getPriceCategory().getItsId());
        query = query.replace(":PRCATIDCOND", pccnd);
        itPrice = (AItemPrice<?, ?>) getSrvOrm().retrieveEntity(pRqVs,
          itemPriceCl, query);
      }
    }
    if (itPrice == null) {
      //retrieve price for all:
      if (pItType.equals(EShopItemType.GOODS)
        || pItType.equals(EShopItemType.SERVICE)) {
        query = lazyGetQuItemPrice();
      } else {
        query = lazyGetQuItemSePrice();
      }
      query = query.replace(":ITEMID", pItId.toString());
      query = query.replace(":LANG", lang);
      query = query.replace(":TITEMPRICE", itemPriceCl.getSimpleName()
        .toUpperCase());
      query = query.replace(":TITEM", itemCl.getSimpleName().toUpperCase());
      query = query.replace(":TI18NITEM", itemI18nCl.getSimpleName()
        .toUpperCase());
      @SuppressWarnings("unchecked")
      List<AItemPrice<?, ?>> itPrices = (List<AItemPrice<?, ?>>)
        getSrvOrm().retrieveListByQuery(pRqVs, itemPriceCl, query);
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
    pRqVs.remove(itemCl.getSimpleName() + "neededFields");
    pRqVs.remove(itemPriceCl.getSimpleName() + "priceCategorydeepLevel");
    pRqVs.remove(itemPriceCl.getSimpleName() + "neededFields");
    pRqVs.remove(InvItemTaxCategory.class.getSimpleName() + "neededFields");
    pRqVs.remove(itemPriceCl.getSimpleName() + "itemdeepLevel");
    if (pItType.equals(EShopItemType.SEGOODS)
      || pItType.equals(EShopItemType.SESERVICE)) {
      pRqVs.remove("SeSellerneededFields");
      pRqVs.remove(itemCl.getSimpleName() + "sellerdeepLevel");
      pRqVs.remove("DebtorCreditorneededFields");
    }
    return itPrice;
  }

  /**
   * <p>Empties Cart.</p>
   * @param pRqVs request scoped vars
   * @param pBuyr buyer
   * @throws Exception - an exception
   **/
  @Override
  public final void emptyCart(final Map<String, Object> pRqVs,
    final OnlineBuyer pBuyr) throws Exception {
    Cart cart = retrCart(pRqVs, pBuyr, true);
    if (cart != null) {
      emptyCart(pRqVs, cart);
    }
  }

  /**
   * <p>Empties Cart.</p>
   * @param pRqVs request scoped vars
   * @param pCart cart
   * @throws Exception - an exception
   **/
  public final void emptyCart(final Map<String, Object> pRqVs,
    final Cart pCart) throws Exception {
    String[] fieldsNames = new String[] {"itsId", "itsVersion", "disab"};
    pRqVs.put("fieldsNames", fieldsNames);
    for (CartLn l : pCart.getItems()) {
      if (!l.getDisab()) {
        l.setDisab(true);
        getSrvOrm().updateEntity(pRqVs, l);
      }
    }
    for (CartTxLn l : pCart.getTaxes()) {
      if (!l.getDisab()) {
        l.setDisab(true);
        getSrvOrm().updateEntity(pRqVs, l);
      }
    }
    for (CartTot l : pCart.getTotals()) {
      if (!l.getDisab()) {
        l.setDisab(true);
        getSrvOrm().updateEntity(pRqVs, l);
      }
    }
    pRqVs.remove("fieldsNames");
    if (pCart.getTot().compareTo(BigDecimal.ZERO) == 1) {
      pCart.setTot(BigDecimal.ZERO);
      getSrvOrm().updateEntity(pRqVs, pCart);
    }
  }

  /**
   * <p>Retrieves Cart from DB.</p>
   * @param pRqVs additional param
   * @param pBuyr buyer
   * @param pForEmpty for emptying
   * @return shopping cart or null
   * @throws Exception - an exception
   **/
  public final Cart retrCart(final Map<String, Object> pRqVs,
    final OnlineBuyer pBuyr, final boolean pForEmpty) throws Exception {
    Cart cart = getSrvOrm().retrieveEntityById(pRqVs, Cart.class, pBuyr);
    if (cart != null) {
      Set<String> ndFlDe = null;
      Set<String> ndFlNm = null;
      Set<String> ndFlDc = null;
      if (!pForEmpty) {
        ndFlNm = new HashSet<String>();
        ndFlNm.add("itsId");
        ndFlNm.add("itsName");
        ndFlDc = new HashSet<String>();
        ndFlDc.add("seller");
        pRqVs.put("UnitOfMeasureneededFields", ndFlNm);
        pRqVs.put("DebtorCreditorneededFields", ndFlNm);
        pRqVs.put("SeSellerneededFields", ndFlDc);
        pRqVs.put("CartLntxCatdeepLevel", 1);
        pRqVs.put("CartLnseldeepLevel", 3);
        pRqVs.put("CartLnitsOwnerdeepLevel", 1);
      } else {
        ndFlDe = new HashSet<String>();
        ndFlDe.add("itsId");
        ndFlDe.add("itsVersion");
        pRqVs.put("CartLnneededFields", ndFlDe);
      }
      cart.setItems(getSrvOrm().retrieveListWithConditions(pRqVs,
        CartLn.class, "where ITSOWNER=" + cart.getBuyer().getItsId()));
      for (CartLn clt : cart.getItems()) {
        clt.setItsOwner(cart);
      }
      if (!pForEmpty) {
        pRqVs.remove("CartLnitsOwnerdeepLevel");
        pRqVs.remove("CartLntxCatdeepLevel");
        pRqVs.remove("CartLnseldeepLevel");
        pRqVs.remove("UnitOfMeasureneededFields");
        pRqVs.put("TaxneededFields", ndFlNm);
        pRqVs.put("CartTxLnseldeepLevel", 4);
        pRqVs.put("CartTxLnitsOwnerdeepLevel", 1);
      } else {
        pRqVs.remove("CartLnneededFields");
        pRqVs.put("CartTxLnneededFields", ndFlDe);
      }
      cart.setTaxes(getSrvOrm().retrieveListWithConditions(pRqVs,
        CartTxLn.class, "where ITSOWNER=" + cart.getBuyer().getItsId()));
      for (CartTxLn ctl : cart.getTaxes()) {
        ctl.setItsOwner(cart);
      }
      if (!pForEmpty) {
        pRqVs.remove("CartTxLnitsOwnerdeepLevel");
        pRqVs.remove("TaxneededFields");
        pRqVs.remove("CartTxLnseldeepLevel");
        pRqVs.put("CartTotseldeepLevel", 4);
        pRqVs.put("CartTotitsOwnerdeepLevel", 1);
      } else {
        pRqVs.remove("CartTxLnneededFields");
        pRqVs.put("CartTotneededFields", ndFlDe);
      }
      cart.setTotals(getSrvOrm().retrieveListWithConditions(pRqVs,
        CartTot.class, "where ITSOWNER=" + cart.getBuyer().getItsId()));
      for (CartTot cttl : cart.getTotals()) {
        cttl.setItsOwner(cart);
      }
      if (!pForEmpty) {
        pRqVs.remove("CartTotitsOwnerdeepLevel");
        pRqVs.remove("DebtorCreditorneededFields");
        pRqVs.remove("SeSellerneededFields");
        pRqVs.remove("CartTotseldeepLevel");
      } else {
        pRqVs.remove("CartTotneededFields");
      }
      cart.setBuyer(pBuyr);
    }
    return cart;
  }

  /**
   * <p>Make cart line that stores taxes data in lines set
   * for invoice basis or item basis aggregate rate.</p>
   * @param pTxdLns TD lines
   * @param pTdlId line ID
   * @param pCatId tax category ID
   * @param pTax tax
   * @param pPercent tax rate
   * @param pAs AS
   * @return line
   **/
  public final CartLn makeTxdLine(final List<CartLn> pTxdLns, final Long pTdlId,
    final Long pCatId,  final Tax pTax, final Double pPercent,
      final AccSettings pAs) {
    CartLn txdLn = null;
    for (CartLn tdl : pTxdLns) {
      if (tdl.getItsId().equals(pTdlId)) {
        txdLn = tdl;
      }
    }
    if (txdLn == null) {
      txdLn = new CartLn();
      txdLn.setItsId(pTdlId);
      InvItemTaxCategory tc = new InvItemTaxCategory();
      tc.setItsId(pCatId);
      tc.setTaxes(new ArrayList<InvItemTaxCategoryLine>());
      txdLn.setTxCat(tc);
      pTxdLns.add(txdLn);
    }
    InvItemTaxCategoryLine itcl = new InvItemTaxCategoryLine();
    itcl.setTax(pTax);
    itcl.setItsPercentage(BigDecimal.valueOf(pPercent)
      .setScale(pAs.getTaxPrecision(), RoundingMode.HALF_UP));
    txdLn.getTxCat().getTaxes().add(itcl);
    txdLn.getTxCat().setAggrOnlyPercent(txdLn.getTxCat()
      .getAggrOnlyPercent().add(itcl.getItsPercentage()));
    return txdLn;
  }

  /**
   * <p>Finds (if need) enabled line with same tax and seller or any
   * disabled tax line or creates one.</p>
   * @param pRqVs additional param
   * @param pCart cart
   * @param pTax tax
   * @param pSeller seller
   * @param pNeedFind if need to find enabled
   * @return line
   * @throws Exception if no need to find but line is found
   **/
  public final CartTxLn findCreateTaxLine(final Map<String, Object> pRqVs,
    final Cart pCart, final Tax pTax, final SeSeller pSeller,
      final boolean pNeedFind) throws Exception {
    CartTxLn ctl = null;
    //find enabled line to add amount
    for (CartTxLn tl : pCart.getTaxes()) {
      if (!tl.getDisab() && tl.getTax().getItsId().equals(pTax.getItsId())
        && (pSeller == null && tl.getSeller() == null
          || pSeller != null && tl.getSeller() != null && pSeller.getItsId()
            .getItsId().equals(tl.getSeller().getItsId().getItsId()))) {
        if (!pNeedFind) {
          throw new Exception("Algorithm error!!!");
        }
        ctl = tl;
        break;
      }
    }
    if (ctl == null) {
      //find disabled line to initialize new tax
      for (CartTxLn tl : pCart.getTaxes()) {
        if (tl.getDisab()) {
          ctl = tl;
          ctl.setDisab(false);
          ctl.setTot(BigDecimal.ZERO);
          ctl.setTaxab(BigDecimal.ZERO);
          ctl.setTax(pTax);
          ctl.setSeller(pSeller);
          break;
        }
      }
    }
    if (ctl == null) {
      ctl = new CartTxLn();
      ctl.setItsOwner(pCart);
      ctl.setIsNew(true);
      ctl.setTax(pTax);
      ctl.setSeller(pSeller);
      pCart.getTaxes().add(ctl);
    }
    return ctl;
  }

  /**
   * <p>Create OnlineBuyer.</p>
   * @param pRqVs additional param
   * @param pRqDt Request Data
   * @return buyer
   * @throws Exception - an exception
   **/
  public final OnlineBuyer createOnlineBuyer(
    final Map<String, Object> pRqVs,
      final IRequestData pRqDt) throws Exception {
    OnlineBuyer buyer = null;
    List<OnlineBuyer> brs = getSrvOrm().retrieveListWithConditions(pRqVs,
      OnlineBuyer.class, "where FRE=1 and REGISTEREDPASSWORD is null");
    if (brs.size() > 0) {
      double rd = Math.random();
      if (rd > 0.5) {
        buyer = brs.get(brs.size() - 1);
      } else {
        buyer = brs.get(0);
      }
      buyer.setFre(false);
      getSrvOrm().updateEntity(pRqVs, buyer);
    }
    if (buyer == null) {
      buyer = new OnlineBuyer();
      buyer.setIsNew(true);
      buyer.setItsName("newbe" + new Date().getTime());
      getSrvOrm().insertEntity(pRqVs, buyer);
    }
    return buyer;
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
   * <p>Lazy Getter for quTxInvBasAggr.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  public final String lazyGetQuTxInvBasAggr() throws IOException {
    if (this.quTxInvBasAggr == null) {
      this.quTxInvBasAggr = loadString("/webstore/cartTxInvBasAggr.sql");
    }
    return this.quTxInvBasAggr;
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
   * <p>Lazy Getter for quItemPrice.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  public final String lazyGetQuItemPrice() throws IOException {
    if (this.quItemPrice == null) {
      this.quItemPrice = loadString("/webstore/itemPrice.sql");
    }
    return this.quItemPrice;
  }

  /**
   * <p>Lazy Getter for quItemSePrice.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  public final String lazyGetQuItemSePrice() throws IOException {
    if (this.quItemSePrice == null) {
      this.quItemSePrice = loadString("/webstore/itemSePrice.sql");
    }
    return this.quItemSePrice;
  }

  /**
   * <p>Lazy Getter for quItemPriceCat.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  public final String lazyGetQuItemPriceCat() throws IOException {
    if (this.quItemPriceCat == null) {
      this.quItemPriceCat = loadString("/webstore/itemPriceCat.sql");
    }
    return this.quItemPriceCat;
  }

  /**
   * <p>Lazy Getter for quItemSePriceCat.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  public final String lazyGetQuItemSePriceCat() throws IOException {
    if (this.quItemSePriceCat == null) {
      this.quItemSePriceCat = loadString("/webstore/itemSePriceCat.sql");
    }
    return this.quItemSePriceCat;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName) throws IOException {
    URL urlFile = SrvShoppingCart.class.getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = SrvShoppingCart.class.getResourceAsStream(pFileName);
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
   * <p>Simple delegator to print number.</p>
   * @param pRqVs additional param
   * @param pVal value
   * @return String
   **/
  public final String prn(final Map<String, Object> pRqVs,
    final BigDecimal pVal) {
    return this.srvNumberToString.print(pVal.toString(),
      (String) pRqVs.get("decSepv"), //user's preferences
        (String) pRqVs.get("decGrSepv"),
          (Integer) pRqVs.get("priceDp"),
            (Integer) pRqVs.get("digInGr"));
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
   * <p>Geter for logger.</p>
   * @return ILog
   **/
  public final ILog getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final void setLogger(final ILog pLogger) {
    this.logger = pLogger;
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
   * <p>Setter for quTxInvBasAggr.</p>
   * @param pQuTxInvBasAggr reference
   **/
  public final void setQuTxInvBasAggr(final String pQuTxInvBasAggr) {
    this.quTxInvBasAggr = pQuTxInvBasAggr;
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

  /**
   * <p>Setter for quItemPrice.</p>
   * @param pQuItemPrice reference
   **/
  public final void setQuItemPrice(final String pQuItemPrice) {
    this.quItemPrice = pQuItemPrice;
  }

  /**
   * <p>Setter for quItemSePrice.</p>
   * @param pQuItemSePrice reference
   **/
  public final void setQuItemSePrice(final String pQuItemSePrice) {
    this.quItemSePrice = pQuItemSePrice;
  }

  /**
   * <p>Setter for quItemPriceCat.</p>
   * @param pQuItemPriceCat reference
   **/
  public final void setQuItemPriceCat(final String pQuItemPriceCat) {
    this.quItemPriceCat = pQuItemPriceCat;
  }

  /**
   * <p>Setter for quItemSePriceCat.</p>
   * @param pQuItemSePriceCat reference
   **/
  public final void setQuItemSePriceCat(final String pQuItemSePriceCat) {
    this.quItemSePriceCat = pQuItemSePriceCat;
  }

  /**
   * <p>Getter for pplCl.</p>
   * @return Class<?>
   **/
  public final Class<?> getPplCl() {
    return this.pplCl;
  }

  /**
   * <p>Setter for pplCl.</p>
   * @param pPplCl reference
   **/
  public final void setPplCl(final Class<?> pPplCl) {
    this.pplCl = pPplCl;
  }

  /**
   * <p>Getter for buySr.</p>
   * @return IBuySr
   **/
  public final IBuySr getBuySr() {
    return this.buySr;
  }

  /**
   * <p>Setter for buySr.</p>
   * @param pBuySr reference
   **/
  public final void setBuySr(final IBuySr pBuySr) {
    this.buySr = pBuySr;
  }
}
