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

import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.math.BigDecimal;

import org.beigesoft.model.ICookie;
import org.beigesoft.model.CookieTmp;
import org.beigesoft.model.IRequestData;
import org.beigesoft.model.Page;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvPage;
import org.beigesoft.settings.IMngSettings;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.webstore.model.TradingCatalog;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.persistable.SubcatalogsCatalogsGs;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.ShoppingCart;
import org.beigesoft.webstore.persistable.CartItem;
import org.beigesoft.webstore.persistable.ItemInList;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.service.ISrvTradingSettings;

/**
 * <p>Service that retrieve webstore page.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcWebstorePage<RS> implements IProcessor {

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Query catalog 1 and 2 level.</p>
   **/
  private String queryCatalogs1And2Level;

  /**
   * <p>Query goods in list for catalog not auctioning
   * same price for all customers.</p>
   **/
  private String queryGilForCatNoAucSmPr;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Business service for trading settings.</p>
   **/
  private ISrvTradingSettings srvTradingSettings;

  /**
   * <p>Page service.</p>
   */
  private ISrvPage srvPage;

  /**
   * <p>Manager UVD settings.</p>
   **/
  private IMngSettings mngUvdSettings;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pAddParam,
    final IRequestData pRequestData) throws Exception {
    List<TradingCatalog> cat1and2l = new ArrayList<TradingCatalog>();
    Map<TradingCatalog, List<TradingCatalog>> cat3aml =
      new HashMap<TradingCatalog, List<TradingCatalog>>();
    if (this.queryCatalogs1And2Level == null) {
      lazyGetQueryCatalogs1And2Level();
    }
    IRecordSet<RS> recordSet = null;
    try {
      recordSet = getSrvDatabase()
        .retrieveRecords(this.queryCatalogs1And2Level);
      if (recordSet.moveToFirst()) {
        do {
          Long cat1lId = recordSet.getLong("CAT1LID");
          Integer cat1lHs = recordSet.getInteger("CAT1HS");
          String cat1lName = recordSet.getString("CAT1LNAME");
          Long cat2lId = recordSet.getLong("CAT2LID");
          String cat2lName = recordSet.getString("CAT2LNAME");
          Integer cat2lHs = recordSet.getInteger("CAT2HS");
          TradingCatalog catalog1l = findById(cat1and2l, cat1lId);
          if (catalog1l == null) {
            catalog1l = new TradingCatalog();
            catalog1l.setItsId(cat1lId);
            catalog1l.setItsName(cat1lName);
            if (cat1lHs == 1) {
              catalog1l.setSubcatalogs(new ArrayList<TradingCatalog>());
            }
            cat1and2l.add(catalog1l);
          }
          if (cat2lId != null) {
            TradingCatalog catalog2l = new TradingCatalog();
            catalog2l.setItsId(cat2lId);
            catalog2l.setItsName(cat2lName);
            catalog1l.getSubcatalogs().add(catalog2l);
            if (cat2lHs == 1) {
              catalog2l.setSubcatalogs(new ArrayList<TradingCatalog>());
            }
          }
        } while (recordSet.moveToNext());
      }
    } finally {
      if (recordSet != null) {
        recordSet.close();
      }
    }
    for (TradingCatalog cat1l : cat1and2l) {
      if (cat1l.getSubcatalogs() != null) {
        for (TradingCatalog cat2l : cat1l.getSubcatalogs()) {
          if (cat2l.getSubcatalogs() != null) {
            addCat3aml(pAddParam, cat3aml, cat2l);
          }
        }
      }
    }
    pRequestData.setAttribute("cat1and2l", cat1and2l);
    pRequestData.setAttribute("cat3aml", cat3aml);
    TradingSettings tradingSettings = srvTradingSettings
      .lazyGetTradingSettings(pAddParam);
    pRequestData.setAttribute("tradingSettings", tradingSettings);
    String catalogId = pRequestData.getParameter("catalogId");
    String catalogName = pRequestData.getParameter("catalogName");
    if (catalogId == null && tradingSettings.getCatalogOnStart() != null) {
      catalogId = tradingSettings.getCatalogOnStart().getItsId().toString();
      catalogName = tradingSettings.getCatalogOnStart().getItsName();
    }
    if (catalogId != null) {
      pRequestData.setAttribute("catalogName", catalogName);
      pRequestData.setAttribute("catalogId", catalogId);
      if (this.queryGilForCatNoAucSmPr == null) {
        lazyGetQueryGilForCatNoAucSmPr();
      }
      String query = this.queryGilForCatNoAucSmPr
        .replace(":ITSCATALOG", catalogId);
      Set<String> neededFieldNames = new HashSet<String>();
      neededFieldNames.add("itsType");
      neededFieldNames.add("itemId");
      neededFieldNames.add("itsName");
      neededFieldNames.add("imageUrl");
      neededFieldNames.add("specificInList");
      neededFieldNames.add("itsPrice");
      neededFieldNames.add("previousPrice");
      neededFieldNames.add("availableQuantity");
      neededFieldNames.add("itsRating");
      pAddParam.put("ItemInListneededFieldNames", neededFieldNames);
      List<ItemInList> itemsList = getSrvOrm()
        .retrievePageByQuery(pAddParam, ItemInList.class,
          query, 0, tradingSettings.getItemsPerPage());
      pAddParam.remove("ItemInListneededFieldNames");
      Integer rowCount = this.srvOrm
        .evalRowCountByQuery(pAddParam, ItemInList.class,
          "select count(*) as TOTALROWS from (" + query + ") as ALLRC;");
      int totalPages = srvPage
        .evalPageCount(rowCount, tradingSettings.getItemsPerPage());
      Integer paginationTail = Integer.valueOf(mngUvdSettings.getAppSettings()
        .get("paginationTail"));
      List<Page> pages = srvPage.evalPages(1, totalPages, paginationTail);
      pRequestData.setAttribute("totalItems", rowCount);
      pRequestData.setAttribute("pages", pages);
      pRequestData.setAttribute("itemsList", itemsList);
    }
    pRequestData.setAttribute("accSettings",
      this.srvAccSettings.lazyGetAccSettings(pAddParam));
    if (pRequestData.getAttribute("shoppingCart") == null) {
      ShoppingCart shoppingCart = getShoppingCart(pAddParam,
        pRequestData, false);
      if (shoppingCart != null) {
        pRequestData.setAttribute("shoppingCart", shoppingCart);
      }
    }
    if (pRequestData.getAttribute("shoppingCart") != null) {
      ShoppingCart shoppingCart = (ShoppingCart) pRequestData
        .getAttribute("shoppingCart");
      if (shoppingCart.getItsItems() != null) {
        Map<EShopItemType, Map<Long, CartItem>> cartMap =
          new HashMap<EShopItemType, Map<Long, CartItem>>();
        for (CartItem ci : shoppingCart.getItsItems()) {
          if (!ci.getIsDisabled()) {
            Map<Long, CartItem> typedMap = cartMap.get(ci.getItemType());
            if (typedMap == null) {
              typedMap = new HashMap<Long, CartItem>();
              cartMap.put(ci.getItemType(), typedMap);
            }
            typedMap.put(ci.getItemId(), ci);
          }
        }
        pRequestData.setAttribute("cartMap", cartMap);
      }
    }
  }

  /**
   * <p>Get/Create ShoppingCart.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @param pIsNeedToCreate Is Need To Create cart
   * @return shopping cart or null
   * @throws Exception - an exception
   **/
  public final ShoppingCart getShoppingCart(final Map<String, Object> pAddParam,
    final IRequestData pRequestData,
      final boolean pIsNeedToCreate) throws Exception {
    ICookie[] cookies = pRequestData.getCookies();
    Long buyerId = null;
    ICookie cookieWas = null;
    if (cookies != null) {
      for (ICookie cookie : cookies) {
        if (cookie.getName().equals("cBuyerId")) {
          buyerId = Long.valueOf(cookie.getValue());
          cookieWas = cookie;
        }
      }
    }
    OnlineBuyer onlineBuyer;
    if (buyerId == null) {
      TradingSettings tradingSettings = srvTradingSettings
        .lazyGetTradingSettings(pAddParam);
      if (pIsNeedToCreate
        || tradingSettings.getIsCreateOnlineUserOnFirstVisit()) {
        onlineBuyer = createOnlineBuyer(pAddParam, pRequestData);
        CookieTmp cookie = new CookieTmp();
        cookie.setName("cBuyerId");
        cookie.setValue(onlineBuyer.getItsId().toString());
        pRequestData.addCookie(cookie);
      } else {
        return null;
      }
    } else {
      onlineBuyer = getSrvOrm()
        .retrieveEntityById(pAddParam, OnlineBuyer.class, buyerId);
      if (onlineBuyer == null) { // deleted for any reason, so create new:
        onlineBuyer = createOnlineBuyer(pAddParam, pRequestData);
        cookieWas.setValue(onlineBuyer.getItsId().toString());
      }
    }
    ShoppingCart shoppingCart = getSrvOrm()
      .retrieveEntityById(pAddParam, ShoppingCart.class, onlineBuyer);
    if (shoppingCart != null) {
      CartItem ci = new CartItem();
      ci.setItsOwner(shoppingCart);
      List<CartItem> cartItems = getSrvOrm()
        .retrieveListForField(pAddParam, ci, "itsOwner");
      shoppingCart.setItsItems(cartItems);
    } else if (pIsNeedToCreate) {
      shoppingCart = new ShoppingCart();
      shoppingCart.setItsId(onlineBuyer);
      shoppingCart.setBuyer(onlineBuyer);
      shoppingCart.setItsTotal(BigDecimal.ZERO);
      shoppingCart.setTotalItems(0);
      getSrvOrm().insertEntity(pAddParam, shoppingCart);
    }
    return shoppingCart;
  }

  /**
   * <p>Create OnlineBuyer.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @return shopping cart or null
   * @throws Exception - an exception
   **/
  public final OnlineBuyer createOnlineBuyer(
    final Map<String, Object> pAddParam,
      final IRequestData pRequestData) throws Exception {
    OnlineBuyer onlineBuyer = new OnlineBuyer();
    onlineBuyer.setIsNew(true);
    onlineBuyer.setItsName("newbe" + new Date());
    getSrvOrm().insertEntity(pAddParam, onlineBuyer);
    return onlineBuyer;
  }

  /**
   * <p>Add catalogs 3 and more levels into 2l catalog.</p>
   * @param pAddParam additional param
   * @param pCat3aml Catalogs 3 and more levels map
   * @param pCat2l Catalog 2 level
   * @throws Exception - an exception
   **/
  protected final void addCat3aml(final Map<String, Object> pAddParam,
    final Map<TradingCatalog, List<TradingCatalog>> pCat3aml,
    final TradingCatalog pCat2l) throws Exception {
    pCat3aml.put(pCat2l, pCat2l.getSubcatalogs());
    addSubcatalogsRecurcively(pAddParam, pCat2l);
  }

  /**
   * <p>Add catalogs 3 and more levels recursively.</p>
   * @param pAddParam additional param
   * @param pCat Catalog N level
   * @throws Exception - an exception
   **/
  protected final void addSubcatalogsRecurcively(
    final Map<String, Object> pAddParam,
      final TradingCatalog pCat) throws Exception {
    String whereStr = "where ITSCATALOG=" + pCat.getItsId();
    List<SubcatalogsCatalogsGs> gscList = getSrvOrm()
      .retrieveListWithConditions(pAddParam, SubcatalogsCatalogsGs.class,
        whereStr);
    for (SubcatalogsCatalogsGs gsc : gscList) {
      TradingCatalog subCat = new TradingCatalog();
      subCat.setItsId(gsc.getSubcatalog().getItsId());
      subCat.setItsName(gsc.getSubcatalog().getItsName());
      pCat.getSubcatalogs().add(subCat);
      if (gsc.getSubcatalog().getHasSubcatalogs()) {
        subCat.setSubcatalogs(new ArrayList<TradingCatalog>());
        addSubcatalogsRecurcively(pAddParam, subCat);
      }
    }
  }

  /**
   * <p>Find catalog by ID.</p>
   * @param pCatalogList Catalog List
   * @param pId Catalog ID
   * @return TradingCatalog Trading Catalog
   **/
  protected final TradingCatalog findById(
    final List<TradingCatalog> pCatalogList,
      final Long pId) {
    for (TradingCatalog tc : pCatalogList) {
      if (tc.getItsId().equals(pId)) {
        return tc;
      }
    }
    return null;
  }

  /**
   * <p>Lazy Get queryCatalogs1And2Level.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String
    lazyGetQueryCatalogs1And2Level() throws Exception {
    if (this.queryCatalogs1And2Level == null) {
      String flName = "/webstore/catalogs1And2Level.sql";
      this.queryCatalogs1And2Level = loadString(flName);
    }
    return this.queryCatalogs1And2Level;
  }

  /**
   * <p>Lazy Get queryGilForCatNoAucSmPr.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String
    lazyGetQueryGilForCatNoAucSmPr() throws Exception {
    if (this.queryGilForCatNoAucSmPr == null) {
      String flName = "/webstore/goodsInListForCatalogNotAucSamePrice.sql";
      this.queryGilForCatNoAucSmPr = loadString(flName);
    }
    return this.queryGilForCatNoAucSmPr;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = PrcTradeEntitiesPage.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = PrcTradeEntitiesPage.class
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
   * <p>Setter for queryCatalogs1And2Level.</p>
   * @param pQueryCatalogs1And2Level reference
   **/
  public final void setQueryCatalogs1And2Level(
    final String pQueryCatalogs1And2Level) {
    this.queryCatalogs1And2Level = pQueryCatalogs1And2Level;
  }

  /**
   * <p>Setter for queryGilForCatNoAucSmPr.</p>
   * @param pQueryGilForCatNoAucSmPr reference
   **/
  public final void setQueryGilForCatNoAucSmPr(
    final String pQueryGilForCatNoAucSmPr) {
    this.queryGilForCatNoAucSmPr = pQueryGilForCatNoAucSmPr;
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
   * <p>Getter for mngUvdSettings.</p>
   * @return IMngSettings
   **/
  public final IMngSettings getMngUvdSettings() {
    return this.mngUvdSettings;
  }

  /**
   * <p>Setter for mngUvdSettings.</p>
   * @param pMngUvdSettings reference
   **/
  public final void setMngUvdSettings(final IMngSettings pMngUvdSettings) {
    this.mngUvdSettings = pMngUvdSettings;
  }

  /**
   * <p>Getter for srvPage.</p>
   * @return ISrvPage
   **/
  public final ISrvPage getSrvPage() {
    return this.srvPage;
  }

  /**
   * <p>Setter for srvPage.</p>
   * @param pSrvPage reference
   **/
  public final void setSrvPage(final ISrvPage pSrvPage) {
    this.srvPage = pSrvPage;
  }
}
