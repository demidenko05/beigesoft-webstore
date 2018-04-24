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

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.model.IRequestData;
import org.beigesoft.model.Page;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvPage;
import org.beigesoft.settings.IMngSettings;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.webstore.model.TradingCatalog;
import org.beigesoft.webstore.model.CmprTradingCatalog;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.persistable.CatalogSpecifics;
import org.beigesoft.webstore.persistable.CatalogGs;
import org.beigesoft.webstore.persistable.SubcatalogsCatalogsGs;
import org.beigesoft.webstore.persistable.ShoppingCart;
import org.beigesoft.webstore.persistable.CartItem;
import org.beigesoft.webstore.persistable.ItemInList;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.service.ISrvTradingSettings;
import org.beigesoft.webstore.service.ISrvShoppingCart;
import org.beigesoft.webstore.service.ILstnCatalogChanged;
import org.beigesoft.webstore.service.ILstnFoSpecificsChanged;

/**
 * <p>Service that retrieve webstore page.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcWebstorePage<RS> implements IProcessor,
  ILstnCatalogChanged, ILstnFoSpecificsChanged {

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

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
   * <p>Shopping Cart service.</p>
   **/
  private ISrvShoppingCart srvShoppingCart;

  /**
   * <p>Cached catalogs.</p>
   **/
  private List<TradingCatalog> catalogs;

  /**
   * <p>Comparator of catalogs by index.</p>
   **/
  private CmprTradingCatalog cmprCatalogs = new CmprTradingCatalog();

  /**
   * <p>Handle FO specifics changed event.</p>
   * @throws Exception an Exception
   **/
  @Override
  public final void onFoSpecificsChanged() throws Exception {
    if (this.catalogs != null) {
      refreshCatalogsFilters(new HashMap<String, Object>(), this.catalogs);
    }
  }

  /**
   * <p>Handle catalog changed event.</p>
   * @throws Exception an Exception
   **/
  @Override
  public final void onCatalogChanged() throws Exception {
    this.catalogs = null;
  }

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pAddParam,
    final IRequestData pRequestData) throws Exception {
    pRequestData.setAttribute("catalogs", lazyRetrieveCatalogs(pAddParam));
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
      if (tradingSettings.getIsUsePriceForCustomer()) {
        throw new Exception(
          "Method price depends of customer's category not yet implemented!");
      }
      if (tradingSettings.getIsServiceStore()) {
        throw new Exception(
          "Service store not yet implemented!");
      }
      if (tradingSettings.getIsSeServiceStore()) {
        throw new Exception(
          "SE-service store not yet implemented!");
      }
      if (tradingSettings.getIsSeGoodsStore()) {
        throw new Exception(
          "SE-goods store not yet implemented!");
      }
      if (tradingSettings.getIsUseAuction()) {
        throw new Exception(
          "Auctioning not yet implemented!");
      }
      pRequestData.setAttribute("catalogName", catalogName);
      pRequestData.setAttribute("catalogId", catalogId);
      String query = lazyGetQueryGilForCatNoAucSmPr()
        .replace(":ITSCATALOG", catalogId);
      Set<String> neededFields = new HashSet<String>();
      neededFields.add("itsType");
      neededFields.add("itemId");
      neededFields.add("itsName");
      neededFields.add("imageUrl");
      neededFields.add("specificInList");
      neededFields.add("itsPrice");
      neededFields.add("previousPrice");
      neededFields.add("availableQuantity");
      neededFields.add("itsRating");
      neededFields.add("detailsMethod");
      pAddParam.put("ItemInListneededFields", neededFields);
      List<ItemInList> itemsList = getSrvOrm()
        .retrievePageByQuery(pAddParam, ItemInList.class,
          query, 0, tradingSettings.getItemsPerPage());
      pAddParam.remove("ItemInListneededFields");
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
   * <p>Build catalogs in lazy mode.</p>
   * @param pAddParam params
   * @return trading catalogs
   * @throws Exception - an exception
   **/
  public final List<TradingCatalog> lazyRetrieveCatalogs(
    final Map<String, Object> pAddParam) throws Exception {
    if (this.catalogs == null) {
      synchronized (this) {
        List<CatalogGs> catalogsGs = getSrvOrm().retrieveListWithConditions(
          pAddParam, CatalogGs.class, " where ISINMENU=1 order by ITSINDEX");
        pAddParam.put("SubcatalogsCatalogsGsitsCatalogdeepLevel", 1); //only ID
        pAddParam.put("SubcatalogsCatalogsGssubcatalogdeepLevel", 1); //only ID
        List<SubcatalogsCatalogsGs> scList = getSrvOrm().retrieveList(pAddParam,
          SubcatalogsCatalogsGs.class);
        pAddParam.remove("SubcatalogsCatalogsGsitsCatalogdeepLevel");
        pAddParam.remove("SubcatalogsCatalogsGssubcatalogdeepLevel");
        List<TradingCatalog> result = new ArrayList<TradingCatalog>();
        Set<Long> firstLevel = new HashSet<Long>();
        Set<Long> allLevels = new HashSet<Long>();
        for (SubcatalogsCatalogsGs catSubc : scList) {
          firstLevel.add(catSubc.getItsCatalog().getItsId());
          allLevels.add(catSubc.getItsCatalog().getItsId());
          allLevels.add(catSubc.getSubcatalog().getItsId());
        }
        for (SubcatalogsCatalogsGs catSubc : scList) {
          firstLevel.remove(catSubc.getSubcatalog().getItsId());
        }
        //first level is from tree and not (that has no sub-catalogsGs)
        for (Long id : firstLevel) {
          TradingCatalog tc = new TradingCatalog();
          tc.setCatalog(findCatalogById(catalogsGs, id));
          result.add(tc);
        }
        for (CatalogGs cat : catalogsGs) {
          boolean inTree = false;
          for (Long id : allLevels) {
            if (cat.getItsId().equals(id)) {
              inTree = true;
              break;
            }
          }
          if (!inTree) {
            TradingCatalog tc = new TradingCatalog();
            tc.setCatalog(findCatalogById(catalogsGs, cat.getItsId()));
            result.add(tc);
          }
        }
        //2-nd .. levels:
        retrieveSubcatalogs(result, catalogsGs, scList);
        //Sorting all levels recursively:
        sortCatalogs(result);
        refreshCatalogsFilters(pAddParam,  result);
        this.catalogs = result;
      }
    }
    return this.catalogs;
  }

  /**
   * <p>Refresh catalog filters, the first catalog in which filter is enabled
   * is propagated into sub-catalogs.</p>
   * @param pAddParam params
   * @param pCurrentList Catalog List current
   * @throws Exception an Exception
   **/
  public final void refreshCatalogsFilters(final Map<String, Object> pAddParam,
    final List<TradingCatalog> pCurrentList) throws Exception {
    for (TradingCatalog tc : pCurrentList) {
      if (tc.getSubcatalogs().size() > 0
        && (tc.getCatalog().getUseAvailableFilter()
          || tc.getCatalog().getUseFilterSpecifics()
            || tc.getCatalog().getUseFilterSubcatalog()
              || tc.getCatalog().getUsePickupPlaceFilter())) {
        if (tc.getCatalog().getUseFilterSpecifics()) {
          CatalogSpecifics cs = new CatalogSpecifics();
          cs.setItsOwner(tc.getCatalog());
          pAddParam.put("CatalogSpecificsitsOwnerdeepLevel", 1); //only ID
          tc.getCatalog().setUsedSpecifics(getSrvOrm().retrieveListForField(
            pAddParam, cs, "itsOwner"));
          pAddParam.remove("CatalogSpecificsitsOwnerdeepLevel");
        }
        setSubcatalogsFilters(tc);
      } else if (tc.getSubcatalogs().size() > 0) {
        tc.getCatalog().setUsedSpecifics(null); //reset if not null
        //recursion:
        refreshCatalogsFilters(pAddParam, tc.getSubcatalogs());
      } else {
        tc.getCatalog().setUsedSpecifics(null); //reset if not null
      }
    }
  }

  /**
   * <p>Sort recursively catalogs in tree.</p>
   * @param pCurrentList Catalog List current
   **/
  public final void sortCatalogs(final List<TradingCatalog> pCurrentList) {
    Collections.sort(pCurrentList, this.cmprCatalogs);
    for (TradingCatalog tc : pCurrentList) {
      if (tc.getSubcatalogs().size() > 0) {
        sortCatalogs(tc.getSubcatalogs());
      }
    }
  }

  /**
   * <p>Set filters/orders for all sub-catalogs same as main-catalog.</p>
   * @param pMainCatalog main catalog
   * @throws Exception an Exception
   **/
  protected final void setSubcatalogsFilters(
    final TradingCatalog pMainCatalog) throws Exception {
    for (TradingCatalog tc : pMainCatalog.getSubcatalogs()) {
      //copy filters/specifics:
      tc.getCatalog().setUseAvailableFilter(pMainCatalog.getCatalog()
        .getUseAvailableFilter());
      tc.getCatalog().setUseFilterSpecifics(pMainCatalog.getCatalog()
        .getUseFilterSpecifics());
      tc.getCatalog().setUseFilterSubcatalog(pMainCatalog.getCatalog()
        .getUseFilterSubcatalog());
      tc.getCatalog().setUsePickupPlaceFilter(pMainCatalog.getCatalog()
        .getUsePickupPlaceFilter());
      tc.getCatalog().setUsedSpecifics(pMainCatalog.getCatalog()
        .getUsedSpecifics());
      if (tc.getSubcatalogs().size() > 0) {
        //recursion:
        setSubcatalogsFilters(tc);
      }
    }
  }

  /**
   * <p>Retrieve recursively sub-catalogs for current level catalogs.</p>
   * @param pCurrentList Catalog List current
   * @param pCatalogs CatalogGs List
   * @param pCatalogsSubcatalogs Catalogs-Subcatalogs
   * @throws Exception an Exception
   **/
  protected final void retrieveSubcatalogs(
    final List<TradingCatalog> pCurrentList, final List<CatalogGs> pCatalogs,
      final List<SubcatalogsCatalogsGs> pCatalogsSubcatalogs) throws Exception {
    for (TradingCatalog tc : pCurrentList) {
      for (SubcatalogsCatalogsGs catSubc : pCatalogsSubcatalogs) {
        if (tc.getCatalog().getItsId().equals(catSubc.getItsCatalog()
          .getItsId())) {
          TradingCatalog tci = new TradingCatalog();
          tci.setCatalog(findCatalogById(pCatalogs, catSubc.getSubcatalog()
            .getItsId()));
          tc.getSubcatalogs().add(tci);
        }
      }
      retrieveSubcatalogs(tc.getSubcatalogs(), pCatalogs, pCatalogsSubcatalogs);
    }
  }

  /**
   * <p>Find catalog by ID.</p>
   * @param pCatalogs Catalog List
   * @param pId Catalog ID
   * @return CatalogGs Catalog
   * @throws Exception if not found
   **/
  protected final CatalogGs findCatalogById(
    final List<CatalogGs> pCatalogs,
      final Long pId) throws Exception {
    for (CatalogGs cat : pCatalogs) {
      if (cat.getItsId().equals(pId)) {
        return cat;
      }
    }
    throw new Exception("Algorithm error! Can't find catalog #" + pId);
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
   * <p>Getter for catalogs.</p>
   * @return List<TradingCatalog>
   **/
  public final List<TradingCatalog> getCatalogs() {
    return this.catalogs;
  }

  /**
   * <p>Setter for catalogs.</p>
   * @param pCatalogs reference
   **/
  public final void setCatalogs(final List<TradingCatalog> pCatalogs) {
    this.catalogs = pCatalogs;
  }

  /**
   * <p>Getter for cmprCatalogs.</p>
   * @return CmprTradingCatalog
   **/
  public final CmprTradingCatalog getCmprCatalogs() {
    return this.cmprCatalogs;
  }

  /**
   * <p>Setter for cmprCatalogs.</p>
   * @param pCmprCatalogs reference
   **/
  public final void setCmprCatalogs(final CmprTradingCatalog pCmprCatalogs) {
    this.cmprCatalogs = pCmprCatalogs;
  }
}
