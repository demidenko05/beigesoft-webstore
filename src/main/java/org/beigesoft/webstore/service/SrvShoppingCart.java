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
import org.beigesoft.log.ILogger;
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
import org.beigesoft.accounting.persistable.ServiceToSale;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.persistable.base.AItemPrice;
import org.beigesoft.webstore.persistable.CartItTxLn;
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
import org.beigesoft.webstore.persistable.SeSeller;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.Cart;
import org.beigesoft.webstore.persistable.CartTot;
import org.beigesoft.webstore.persistable.CartLn;
import org.beigesoft.webstore.persistable.CartTxLn;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.CurrRate;

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
  private ILogger logger;

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
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  /**
   * <p>Get/Create Cart.</p>
   * @param pReqVars additional param
   * @param pRequestData Request Data
   * @param pIsNeedToCreate Is Need To Create cart
   * @return shopping cart or null
   * @throws Exception - an exception
   **/
  @Override
  public final Cart getShoppingCart(final Map<String, Object> pReqVars,
    final IRequestData pRequestData,
      final boolean pIsNeedToCreate) throws Exception {
    Long buyerId = null;
    String buyerIdStr = pRequestData.getCookieValue("cBuyerId");
    if (buyerIdStr != null && buyerIdStr.length() > 0) {
       buyerId = Long.valueOf(buyerIdStr);
    }
    OnlineBuyer buyer;
    TradingSettings ts = srvTradingSettings.lazyGetTradingSettings(pReqVars);
    if (buyerId == null) {
      if (pIsNeedToCreate
        || ts.getIsCreateOnlineUserOnFirstVisit()) {
        buyer = createOnlineBuyer(pReqVars, pRequestData);
        pRequestData.setCookieValue("cBuyerId", buyer.getItsId()
          .toString());
      } else {
        return null;
      }
    } else {
      buyer = getSrvOrm()
        .retrieveEntityById(pReqVars, OnlineBuyer.class, buyerId);
      if (buyer == null) { // deleted for any reason, so create new:
        buyer = createOnlineBuyer(pReqVars, pRequestData);
        pRequestData.setCookieValue("cBuyerId", buyer.getItsId()
          .toString());
      }
    }
    Cart cart = getSrvOrm().retrieveEntityById(pReqVars, Cart.class, buyer);
    if (cart != null) {
      pReqVars.put("CartLnitsOwnerdeepLevel", 1);
      List<CartLn> cartItems = getSrvOrm().retrieveListWithConditions(pReqVars,
        CartLn.class, "where ITSOWNER=" + cart.getBuyer().getItsId());
      cart.setItems(cartItems);
      pReqVars.remove("CartLnitsOwnerdeepLevel");
      boolean isSeIt = false;
      for (CartLn clt : cart.getItems()) {
        clt.setItsOwner(cart);
        if (!clt.getDisab() && clt.getItTyp().equals(EShopItemType.SESERVICE)
          || clt.getItTyp().equals(EShopItemType.SEGOODS)) {
          isSeIt = true;
        }
      }
      if (cart.getTotTx().compareTo(BigDecimal.ZERO) == 1) {
        pReqVars.put("CartTxLnitsOwnerdeepLevel", 1);
        List<CartTxLn> ctls = getSrvOrm().retrieveListWithConditions(pReqVars,
          CartTxLn.class, "where ITSOWNER=" + cart.getBuyer().getItsId());
        pReqVars.remove("CartTxLnitsOwnerdeepLevel");
        cart.setTaxes(ctls);
        for (CartTxLn ctl : cart.getTaxes()) {
          ctl.setItsOwner(cart);
        }
      }
      if (isSeIt) {
        List<CartTot> ctts = getSrvOrm().retrieveListWithConditions(pReqVars,
          CartTot.class, "where ITSOWNER=" + cart.getBuyer().getItsId());
        pReqVars.remove("CartTotitsOwnerdeepLevel");
        cart.setTotals(ctts);
        for (CartTot cttl : cart.getTotals()) {
          cttl.setItsOwner(cart);
        }
      }
    } else if (pIsNeedToCreate) {
      cart = new Cart();
      Currency curr = (Currency) pReqVars.get("wscurr");
      cart.setCurr(curr);
      cart.setItems(new ArrayList<CartLn>());
      cart.setTaxes(new ArrayList<CartTxLn>());
      cart.setItsId(buyer);
      getSrvOrm().insertEntity(pReqVars, cart);
    }
    if (cart != null) {
      cart.setBuyer(buyer);
      if (ts.getOnlyDeliv() != null) {
        cart.setDeliv(ts.getOnlyDeliv());
      }
    }
    return cart;
  }

  /**
   * <p>Refresh cart totals by seller cause line inserted/changed/deleted.</p>
   * @param pReqVars request scoped vars
   * @param pTs TradingSettings
   * @param pCartLn affected cart line
   * @param pAs Accounting Settings
   * @param pTxRules NULL if not taxable
   * @throws Exception - an exception.
   **/
  @Override
  public final void makeCartTotals(final Map<String, Object> pReqVars,
    final TradingSettings pTs, final CartLn pCartLn, final AccSettings pAs,
      final TaxDestination pTxRules) throws Exception {
    BigDecimal txTot = BigDecimal.ZERO;
    BigDecimal txTotSe = BigDecimal.ZERO;
    if (pTxRules != null) {
      if (getLogger().getIsShowDebugMessagesFor(getClass())
        && getLogger().getDetailLevel() > 40000) {
        getLogger().debug(pReqVars, SrvShoppingCart.class,
      "Tax rules: aggregate/invoice basis/zip/RM = " + pTxRules
    .getSalTaxUseAggregItBas() + "/" + pTxRules.getSalTaxIsInvoiceBase()
  + "/" + pTxRules.getRegZip() + "/" + pTxRules.getSalTaxRoundMode());
        String txCat;
        if (pCartLn.getTxCat() != null) {
          txCat = pCartLn.getTxCat().getItsName();
        } else {
          txCat = "-";
        }
        getLogger().debug(pReqVars, SrvShoppingCart.class,
          "Item: name/tax category/disabled = " + pCartLn.getItsName() + "/"
            + txCat + "/" + pCartLn.getDisab());
      }
      if (pCartLn.getItsOwner().getTaxes() == null) {
        pReqVars.put("CartTxLnitsOwnerdeepLevel", 1);
        List<CartTxLn> ctls = getSrvOrm().retrieveListWithConditions(pReqVars,
          CartTxLn.class, "where ITSOWNER=" + pCartLn.getItsOwner()
            .getBuyer().getItsId());
        pReqVars.remove("CartTxLnitsOwnerdeepLevel");
        pCartLn.getItsOwner().setTaxes(ctls);
        for (CartTxLn ctl : pCartLn.getItsOwner().getTaxes()) {
          ctl.setItsOwner(pCartLn.getItsOwner());
        }
      }
      for (CartTxLn ctl : pCartLn.getItsOwner().getTaxes()) {
        if (!ctl.getDisab() && (ctl.getSeller() == null
          && pCartLn.getSeller() == null || ctl.getSeller() != null
            && pCartLn.getSeller() != null && pCartLn.getSeller().getItsId()
              .getItsId().equals(ctl.getSeller().getItsId().getItsId()))) {
          ctl.setDisab(true);
        }
      }
      //data storage for aggregate rate:
      List<CartLn> txdLns = null;
      //data storages for non-aggregate rate:
      List<Tax> txs = null; //taxes
      List<Double> txTotTaxb = null; //tax's totals/taxables
      List<Double> txPerc = null; //tax's percents for invoice basis
      String query;
      if (!pTxRules.getSalTaxUseAggregItBas()) { //non-aggregate:
        txs = new ArrayList<Tax>();
        txTotTaxb = new ArrayList<Double>();
        if (!pTxRules.getSalTaxIsInvoiceBase()) {
          //item basis, taxes excluded:
          query = lazyGetQuTxItBas();
        } else {
          //invoice basis, taxes excluded/included:
          txPerc = new ArrayList<Double>();
          query = lazyGetQuTxInvBas();
        }
      } else { // any aggregate:
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
            if (!pTxRules.getSalTaxUseAggregItBas()) { //non-aggregate:
              txs.add(tax);
              if (!pTxRules.getSalTaxIsInvoiceBase()) {
                //item basis , taxes excluded:
                txTotTaxb.add(recordSet.getDouble("TOTALTAX"));
              } else {
                //invoice basis, taxes excluded/included:
                txPerc.add(recordSet.getDouble("ITSPERCENTAGE"));
                if (pTs.getTxExcl()) {
                  txTotTaxb.add(recordSet.getDouble("SUBTOTAL"));
                } else {
                  txTotTaxb.add(recordSet.getDouble("ITSTOTAL"));
                }
              }
            } else { //any aggregate:
              Double percent = recordSet.getDouble("ITSPERCENTAGE");
              Long tcId = recordSet.getLong("TAXCATID");
              if (!pTxRules.getSalTaxIsInvoiceBase()) { //item basis:
                Long clId = recordSet.getLong("CLID");
                CartLn txdLn = makeTxdLine(txdLns, clId, tcId, tax, percent,
                  pAs, pCartLn.getSeller());
                txdLn.setTotTx(BigDecimal.valueOf(recordSet
                  .getDouble("TOTALTAXES"))
                    .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
              } else { //invoice basis:
                CartLn txdLn = makeTxdLine(txdLns, tcId, tcId, tax, percent,
                  pAs, pCartLn.getSeller());
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
      if (!pTxRules.getSalTaxUseAggregItBas()) { //non-aggregate:
        for (int i = 0; i < txs.size(); i++) {
          CartTxLn ctl = findCreateTaxLine(pReqVars, pCartLn.getItsOwner(),
            txs.get(i), pCartLn.getSeller(), false);
          Double txTotd;
          if (!pTxRules.getSalTaxIsInvoiceBase()) {
            //item basis, taxes excluded:
            txTotd = txTotTaxb.get(i);
          } else {
            //invoice basis, taxes excluded/included:
            if (pTs.getTxExcl()) {
              txTotd = txTotTaxb.get(i) * txPerc.get(i) / 100.0;
            } else {
              txTotd = txTotTaxb.get(i)
                - (txTotTaxb.get(i) / (1 + txPerc.get(i) / 100.0));
            }
          }
          ctl.setTot(BigDecimal.valueOf(txTotd).setScale(pAs.
            getPricePrecision(), pTxRules.getSalTaxRoundMode()));
          if (ctl.getIsNew()) {
            getSrvOrm().insertEntity(pReqVars, ctl);
            ctl.setIsNew(false);
          } else {
            getSrvOrm().updateEntity(pReqVars, ctl);
          }
        }
      } else { //any aggregate:
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
            CartTxLn ctl = findCreateTaxLine(pReqVars, pCartLn.getItsOwner(),
              itcl.getTax(), txdLn.getSeller(), true);
            ctl.setTot(ctl.getTot().add(txdLn.getTotTx()));
            if (pTxRules.getSalTaxIsInvoiceBase()) {
              if (pTs.getTxExcl()) {
                ctl.setTaxab(ctl.getTaxab().add(txdLn.getSubt()));
              } else {
                ctl.setTaxab(ctl.getTaxab().add(txdLn.getTot()));
              }
            }
            if (ctl.getIsNew()) {
              getSrvOrm().insertEntity(pReqVars, ctl);
              ctl.setIsNew(false);
            } else {
              getSrvOrm().updateEntity(pReqVars, ctl);
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
          getSrvOrm().updateEntity(pReqVars, ctl);
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
    getSrvOrm().updateEntity(pReqVars, pCartLn.getItsOwner());
    if (pCartLn.getItsOwner().getTotals() != null
      || pCartLn.getItTyp().equals(EShopItemType.SESERVICE)
        || pCartLn.getItTyp().equals(EShopItemType.SEGOODS)) {
      if (pCartLn.getItsOwner().getTotals() == null) {
        List<CartTot> ctts = getSrvOrm().retrieveListWithConditions(pReqVars,
          CartTot.class, "where ITSOWNER=" + pCartLn.getItsOwner()
            .getBuyer().getItsId());
        pReqVars.remove("CartTotitsOwnerdeepLevel");
        pCartLn.getItsOwner().setTotals(ctts);
        for (CartTot cttl : pCartLn.getItsOwner().getTotals()) {
          cttl.setItsOwner(pCartLn.getItsOwner());
        }
      }
      CartTot cartTot = null;
      for (CartTot ct : pCartLn.getItsOwner().getTotals()) {
        if (!ct.getDisab() && ct.getSeller() == null && pCartLn
          .getSeller() == null || ct.getSeller() != null && pCartLn
            .getSeller() != null && pCartLn.getSeller().getItsId().getItsId()
              .equals(ct.getSeller().getItsId().getItsId())) {
          cartTot = ct;
          break;
        }
      }
      if (totSe.compareTo(BigDecimal.ZERO) == 0) {
        if (cartTot != null) {
          cartTot.setDisab(true);
          getSrvOrm().updateEntity(pReqVars, cartTot);
        }
      } else {
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
          getSrvOrm().insertEntity(pReqVars, cartTot);
        } else {
          getSrvOrm().updateEntity(pReqVars, cartTot);
        }
      }
    }
  }

  /**
   * <p>Reveal shared tax rules for cart. It also makes buyer-regCustomer.</p>
   * @param pReqVars request scoped vars
   * @param pCart cart
   * @param pAs Accounting Settings
   * @return tax rules, NULL if not taxable
   * @throws Exception - an exception.
   **/
  @Override
  public final TaxDestination revealTaxRules(final Map<String, Object> pReqVars,
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
   * @param pReqVars request scoped vars
   * @param pCart cart
   * @param pAs Accounting Settings
   * @param pTs TradingSettings
   * @throws Exception - an exception.
   **/
  @Override
  public final void handleCurrencyChanged(final Map<String, Object> pReqVars,
    final Cart pCart, final AccSettings pAs,
      final TradingSettings pTs) throws Exception {
    TaxDestination txRules = revealTaxRules(pReqVars, pCart, pAs);
    for (CartLn cl : pCart.getItems()) {
      if (!cl.getDisab()) {
        makeCartLine(pReqVars, cl, pAs, pTs, txRules, true);
        makeCartTotals(pReqVars, pTs, cl, pAs, txRules);
      }
    }
  }

  /**
   * <p>Makes cart line.</p>
   * @param pReqVars request scoped vars
   * @param pCartLn cart line
   * @param pAs Accounting Settings
   * @param pTs TradingSettings
   * @param pTxRules NULL if not taxable
   * @param pRedoPr redo price
   * @throws Exception - an exception.
   **/
  @Override
  public final void makeCartLine(final Map<String, Object> pReqVars,
    final CartLn pCartLn, final AccSettings pAs, final TradingSettings pTs,
      final TaxDestination pTxRules, final boolean pRedoPr) throws Exception {
    if (pRedoPr) {
      AItemPrice<?, ?> itPrice = revealItemPrice(pReqVars, pTs,
        pCartLn.getItsOwner(), pCartLn.getItTyp(), pCartLn.getItId());
      pCartLn.setPrice(itPrice.getItsPrice());
      List<CurrRate> currRates = (List<CurrRate>) pReqVars.get("currRates");
      for (CurrRate cr: currRates) {
        if (cr.getCurr().getItsId().equals(pCartLn.getItsOwner()
          .getCurr().getItsId())) {
          pCartLn.setPrice(pCartLn.getPrice().multiply(cr.getRate())
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
    //using user passed values:
    BigDecimal totalTaxes = BigDecimal.ZERO;
    BigDecimal bd100 = new BigDecimal("100.00");
    List<CartItTxLn> itls = null;
    if (pTxRules != null && pCartLn.getTxCat() != null) {
      if (!pTxRules.getSalTaxIsInvoiceBase()) {
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
      this.getSrvOrm().insertEntity(pReqVars, pCartLn);
    } else {
      this.getSrvOrm().updateEntity(pReqVars, pCartLn);
      for (int i = 0; i < pCartLn.getItsOwner().getItems().size(); i++) {
        if (pCartLn.getItsOwner().getItems().get(i).getItId()
          .equals(pCartLn.getItId()) && pCartLn.getItsOwner().getItems().get(i)
            .getItTyp().equals(pCartLn.getItTyp())) {
          pCartLn.getItsOwner().getItems().set(i, pCartLn);
          break;
        }
      }
    }
    if (itls != null) {
      pReqVars.put("CartItTxLnitsOwnerdeepLevel", 1);
      List<CartItTxLn> itlsr = getSrvOrm().retrieveListWithConditions(
          pReqVars, CartItTxLn.class, "where CARTID="
            + pCartLn.getItsOwner().getBuyer().getItsId());
      pReqVars.remove("CartItTxLnitsOwnerdeepLevel");
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
      for (CartItTxLn itlrt : itlsr) {
        if (itlrt.getDisab() && itlrt.getItsOwner().getItsId()
          .equals(pCartLn.getItsId())) {
          getSrvOrm().updateEntity(pReqVars, itlrt);
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
  @Override
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
   * <p>Make cart line that stores taxes data in lines set
   * for invoice basis or item basis aggregate rate.</p>
   * @param pTxdLns TD lines
   * @param pTdlId line ID
   * @param pCatId tax category ID
   * @param pTax tax
   * @param pPercent tax rate
   * @param pAs AS
   * @param pSeller Seller
   * @return line
   **/
  public final CartLn makeTxdLine(final List<CartLn> pTxdLns, final Long pTdlId,
    final Long pCatId,  final Tax pTax, final Double pPercent,
      final AccSettings pAs, final SeSeller pSeller) {
    CartLn txdLn = null;
    for (CartLn tdl : pTxdLns) {
      if (tdl.getItsId().equals(pTdlId)
        && (pSeller == null && tdl.getSeller() == null
        || pSeller != null && tdl.getSeller() != null && tdl.getSeller()
          .getItsId().getItsId().equals(pSeller.getItsId().getItsId()))) {
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
      txdLn.setSeller(pSeller);
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
   * @param pReqVars additional param
   * @param pCart cart
   * @param pTax tax
   * @param pSeller seller
   * @param pNeedFind if need to find enabled
   * @return line
   * @throws Exception if no need to find but line is found
   **/
  public final CartTxLn findCreateTaxLine(final Map<String, Object> pReqVars,
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
   * @param pReqVars additional param
   * @param pRequestData Request Data
   * @return shopping cart or null
   * @throws Exception - an exception
   **/
  public final OnlineBuyer createOnlineBuyer(
    final Map<String, Object> pReqVars,
      final IRequestData pRequestData) throws Exception {
    OnlineBuyer buyer = new OnlineBuyer();
    buyer.setIsNew(true);
    buyer.setItsName("newbe" + new Date().getTime());
    getSrvOrm().insertEntity(pReqVars, buyer);
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
   * <p>Geter for logger.</p>
   * @return ILogger
   **/
  public final synchronized ILogger getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final synchronized void setLogger(final ILogger pLogger) {
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
}
