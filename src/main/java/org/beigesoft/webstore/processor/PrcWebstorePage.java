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

import org.beigesoft.log.ILogger;
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
import org.beigesoft.webstore.model.EFilterOperator;
import org.beigesoft.webstore.model.FilterInteger;
import org.beigesoft.webstore.model.FilterCatalog;
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

/**
 * <p>Service that retrieve webstore page.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcWebstorePage<RS> implements IProcessor, ILstnCatalogChanged {

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
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>Handle catalog changed event.</p>
   * @throws Exception an Exception
   **/
  @Override
  public final synchronized void onCatalogChanged() throws Exception {
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
    String catalogIdStr = pRequestData.getParameter("catalogId");
    Long catId = null;
    if (catalogIdStr != null) {
      catId = Long.valueOf(catalogIdStr);
    }
    if (catId == null && tradingSettings.getCatalogOnStart() != null) {
      catId = tradingSettings.getCatalogOnStart().getItsId();
    }
    if (catId != null) {
      // either selected by user catalog or "on start" must be
      // if user additionally selected filters (include set of subcatalogs)
      // then the main (root) catalog ID still present in request
      TradingCatalog tcat = findTradingCatalogById(this.catalogs, catId);
      if (tcat == null) {
        this.logger.warn(pAddParam, PrcWebstorePage.class,
          "Can't find catalog #" + catId);
      } else {
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
        FilterInteger filterPrice = revialFilterPrice(tcat,
          pAddParam, pRequestData);
        FilterCatalog filterCatalog = revialFilterCatalog(tcat,
          pAddParam, pRequestData);
        String whereAdd = revealWhereAdd(filterPrice);
        String whereCatalog = revealWhereCatalog(tcat, filterCatalog);
        String query = lazyGetQueryGilForCatNoAucSmPr().replace(
          ":CATALOGFILTER", whereCatalog).replace(":WHEREADD", whereAdd);
        Integer rowCount = this.srvOrm
          .evalRowCountByQuery(pAddParam, ItemInList.class,
            "select count(*) as TOTALROWS from (" + query + ") as ALLRC;");
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
        String pageStr = pRequestData.getParameter("page");
        Integer page;
        if (pageStr != null) {
          page = Integer.valueOf(pageStr);
        } else {
          page = 1;
        }
        Integer itemsPerPage = tradingSettings.getItemsPerPage();
        int totalPages = this.srvPage.evalPageCount(rowCount, itemsPerPage);
        if (page > totalPages) {
          page = totalPages;
        }
        int firstResult = (page - 1) * itemsPerPage; //0-20,20-40
        List<ItemInList> itemsList = getSrvOrm()
          .retrievePageByQuery(pAddParam, ItemInList.class,
            query, firstResult, itemsPerPage);
        pAddParam.remove("ItemInListneededFields");
        Integer paginationTail = Integer.valueOf(mngUvdSettings.getAppSettings()
          .get("paginationTail"));
        List<Page> pages = this.srvPage.evalPages(1, totalPages,
          paginationTail);
        if (filterPrice != null) {
          pRequestData.setAttribute("filterPrice", filterPrice);
        }
        if (filterCatalog != null) {
          pRequestData.setAttribute("filterCatalog", filterCatalog);
        }
        pRequestData.setAttribute("catalog", tcat.getCatalog());
        pRequestData.setAttribute("totalItems", rowCount);
        pRequestData.setAttribute("pages", pages);
        pRequestData.setAttribute("itemsList", itemsList);
      }
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
        if (this.catalogs == null) {
          List<CatalogGs> catalogsGs = getSrvOrm().retrieveListWithConditions(
            pAddParam, CatalogGs.class, " order by ITSINDEX");
          //only ID:
          pAddParam.put("SubcatalogsCatalogsGsitsCatalogdeepLevel", 1);
          pAddParam.put("SubcatalogsCatalogsGssubcatalogdeepLevel", 1);
          List<SubcatalogsCatalogsGs> scList = getSrvOrm().retrieveList(
            pAddParam, SubcatalogsCatalogsGs.class);
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
            tc.setCatalog(findCatalogGsById(catalogsGs, id));
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
              tc.setCatalog(findCatalogGsById(catalogsGs, cat.getItsId()));
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
   * <p>Reveal filter price for catalog and fill it from request data.</p>
   * @param pTcat Catalog
   * @param pAddParam params
   * @param pRequestData Request Data
   * @return filter price or null if catalog hasn't any filter
   * @throws Exception an Exception
   **/
  public final FilterInteger revialFilterPrice(final TradingCatalog pTcat,
    final Map<String, Object> pAddParam,
      final IRequestData pRequestData) throws Exception {
    if (pTcat.getCatalog().getUseFilterSpecifics()
      || pTcat.getCatalog().getUseFilterSubcatalog()
        || pTcat.getCatalog().getUseAvailableFilter()
          || pTcat.getCatalog().getUsePickupPlaceFilter()) {
      FilterInteger res = new FilterInteger();
      String operStr = pRequestData.getParameter("fltPriOp");
      String val1Str = pRequestData.getParameter("fltPriVal1");
      if (operStr != null && !"".equals(operStr)
        && val1Str != null && !"".equals(val1Str)) {
        res.setOperator(Enum.valueOf(EFilterOperator.class, operStr));
        res.setValue1(Integer.valueOf(val1Str));
        String val2Str = pRequestData.getParameter("fltPriVal2");
        if (val2Str != null && !"".equals(val2Str)) {
          res.setValue2(Integer.valueOf(val2Str));
        }
      }
      return res;
    } else {
      return null;
    }
  }

  /**
   * <p>Reveal filter catalog for catalog and fill it from request data.</p>
   * @param pTcat Catalog
   * @param pAddParam params
   * @param pRequestData Request Data
   * @return filter catalog or null if catalog hasn't any filter
   * @throws Exception an Exception
   **/
  public final FilterCatalog revialFilterCatalog(final TradingCatalog pTcat,
    final Map<String, Object> pAddParam,
      final IRequestData pRequestData) throws Exception {
    if (pTcat.getCatalog().getUseFilterSubcatalog()) {
      FilterCatalog res = new FilterCatalog();
      copySubcatalogsGs(pTcat, res.getCatalogsAll());
      String operStr = pRequestData.getParameter("fltCtOp");
      String[] valStrs = pRequestData.getParameterValues("fltCtVal");
      if (operStr != null && !"".equals(operStr)
        && valStrs != null && valStrs.length > 0) {
        res.setOperator(Enum.valueOf(EFilterOperator.class, operStr));
        for (String idStr : valStrs) {
          Long id = Long.valueOf(idStr);
          CatalogGs cgs = findSubcatalogGsByIdInTc(pTcat, id);
          if (cgs == null) {
            throw new Exception("Algorithm error! Can't find subcatalog #/in: "
              + id + "/" + pTcat.getCatalog().getItsName());
          }
          res.getCatalogs().add(cgs);
        }
      }
      return res;
    } else {
      return null;
    }
  }

  /**
   * <p>Reveal part of where clause e.g. " and ITSPRICE<21" or empty string.</p>
   * @param pFilterPrice Filter Price
   * @return part of where clause e.g. " and ITSPRICE<21" or empty string
   * @throws Exception an Exception
   **/
  public final String revealWhereAdd(
    final FilterInteger pFilterPrice) throws Exception {
    if (pFilterPrice == null || pFilterPrice.getOperator() == null
      || pFilterPrice.getValue1() == null) {
      return "";
    }
    if (EFilterOperator.LESS_THAN.equals(pFilterPrice.getOperator())
      || EFilterOperator.LESS_THAN_EQUAL.equals(pFilterPrice.getOperator())
      || EFilterOperator.GREATER_THAN.equals(pFilterPrice.getOperator())
    || EFilterOperator.GREATER_THAN_EQUAL.equals(pFilterPrice.getOperator())) {
      return " and ITSPRICE" + toSqlOperator(pFilterPrice.getOperator())
        + pFilterPrice.getValue1();
    }
    if (pFilterPrice.getValue2() != null) {
      if (EFilterOperator.BETWEEN.equals(pFilterPrice.getOperator())) {
        return " and ITSPRICE<" + pFilterPrice.getValue1()
          + " and ITSPRICE>" + pFilterPrice.getValue2();
      } else if (EFilterOperator.BETWEEN.equals(pFilterPrice.getOperator())) {
        return " and ITSPRICE<=" + pFilterPrice.getValue1()
          + " and ITSPRICE>=" + pFilterPrice.getValue2();
      }
    }
    return "";
  }

  /**
   * <p>Reveal part of where catalog clause e.g. "=12",
   * " in (12,14)" or empty string.</p>
   * @param pTcat Catalog
   * @param pFilterCatalog Filter Catalog
   * @return part of where clause e.g. "=12", " in (12,14)" or empty string
   * @throws Exception an Exception
   **/
  public final String revealWhereCatalog(final TradingCatalog pTcat,
    final FilterCatalog pFilterCatalog) throws Exception {
    if (pFilterCatalog != null && pFilterCatalog.getOperator() != null
      && pFilterCatalog.getCatalogs().size() > 0) {
      if (EFilterOperator.IN.equals(pFilterCatalog.getOperator())
        && pFilterCatalog.getCatalogs().size() == 1) {
        return "=" + pFilterCatalog.getCatalogs().get(0).getItsId();
      } else if (EFilterOperator.NOT_IN.equals(pFilterCatalog.getOperator())
        && pFilterCatalog.getCatalogs().size() == 1) {
        return "!=" + pFilterCatalog.getCatalogs().get(0).getItsId();
      } else {
        StringBuffer sb = new StringBuffer();
        if (EFilterOperator.IN.equals(pFilterCatalog.getOperator())) {
          sb.append(" in (");
        } else {
          sb.append(" not in (");
        }
        boolean isFirst = true;
        for (CatalogGs cgs : pFilterCatalog.getCatalogs()) {
          if (isFirst) {
            isFirst = false;
          } else {
            sb.append(",");
          }
          sb.append(cgs.getItsId());
        }
        sb.append(")");
        return sb.toString();
      }
    } else {
      List<CatalogGs> subcgs;
      if (pFilterCatalog != null
        && pFilterCatalog.getCatalogsAll().size() > 0) {
        subcgs = pFilterCatalog.getCatalogsAll();
      } else {
        subcgs = new ArrayList<CatalogGs>();
        copySubcatalogsGs(pTcat, subcgs);
      }
      if (subcgs.size() > 0) {
        StringBuffer sb = new StringBuffer(" in ("
          + pTcat.getCatalog().getItsId());
        for (CatalogGs cgs : subcgs) {
          sb.append("," + cgs.getItsId());
        }
        sb.append(")");
        return sb.toString();
      } else {
        return "=" + pTcat.getCatalog().getItsId();
      }
    }
  }

  /**
   * <p>Convert from EFilterOperator to SQL one.</p>
   * @param pFilterOperator EFilterOperator
   * @return SQL operator
   * @throws Exception if not found
   **/
  public final String toSqlOperator(
    final EFilterOperator pFilterOperator) throws Exception {
    if (EFilterOperator.LESS_THAN.equals(pFilterOperator)) {
      return "<";
    }
    if (EFilterOperator.LESS_THAN_EQUAL.equals(pFilterOperator)) {
      return "<=";
    }
    if (EFilterOperator.GREATER_THAN.equals(pFilterOperator)) {
      return ">";
    }
    if (EFilterOperator.GREATER_THAN_EQUAL.equals(pFilterOperator)) {
      return ">=";
    }
    throw new Exception(
      "Algorithm error! Cant match SQL operator to EFilterOperator: "
        + pFilterOperator);
  }

  /**
   * <p>Set filters/orders for all sub-catalogs same as main-catalog.</p>
   * @param pMainCatalog main catalog
   * @throws Exception an Exception
   **/
  public final void setSubcatalogsFilters(
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
  public final void retrieveSubcatalogs(
    final List<TradingCatalog> pCurrentList, final List<CatalogGs> pCatalogs,
      final List<SubcatalogsCatalogsGs> pCatalogsSubcatalogs) throws Exception {
    for (TradingCatalog tc : pCurrentList) {
      for (SubcatalogsCatalogsGs catSubc : pCatalogsSubcatalogs) {
        if (tc.getCatalog().getItsId().equals(catSubc.getItsCatalog()
          .getItsId())) {
          TradingCatalog tci = new TradingCatalog();
          tci.setCatalog(findCatalogGsById(pCatalogs, catSubc.getSubcatalog()
            .getItsId()));
          tc.getSubcatalogs().add(tci);
        }
      }
      if (tc.getSubcatalogs().size() > 0) {
        //recursion:
        retrieveSubcatalogs(tc.getSubcatalogs(), pCatalogs,
          pCatalogsSubcatalogs);
      }
    }
  }

  /**
   * <p>Find catalog GS by ID.</p>
   * @param pCatalogs Catalog List
   * @param pId Catalog ID
   * @return CatalogGs Catalog
   * @throws Exception if not found
   **/
  public final CatalogGs findCatalogGsById(final List<CatalogGs> pCatalogs,
    final Long pId) throws Exception {
    for (CatalogGs cat : pCatalogs) {
      if (cat.getItsId().equals(pId)) {
        return cat;
      }
    }
    throw new Exception("Algorithm error! Can't find catalog #" + pId);
  }

  /**
   * <p>Find trading catalog by ID.</p>
   * @param pCatalogs trading catalogs
   * @param pId Catalog ID
   * @return Trading Catalog
   **/
  public final TradingCatalog findTradingCatalogById(
    final List<TradingCatalog> pCatalogs,
      final Long pId) {
    for (TradingCatalog cat : pCatalogs) {
      if (cat.getCatalog().getItsId().equals(pId)) {
        return cat;
      }
      if (cat.getSubcatalogs().size() > 0) {
        //recursion:
        TradingCatalog tc = findTradingCatalogById(cat.getSubcatalogs(), pId);
        if (tc != null) {
          return tc;
        }
      }
    }
    return null;
  }

  /**
   * <p>Copy sub-catalog-GS from given catalog-T to given set-CGS.</p>
   * @param pCatalog trading catalog
   * @param pCatalogsGs given set-CGS
   **/
  public final void copySubcatalogsGs(
    final TradingCatalog pCatalog,
      final List<CatalogGs> pCatalogsGs) {
    for (TradingCatalog cat : pCatalog.getSubcatalogs()) {
      pCatalogsGs.add(cat.getCatalog());
      for (TradingCatalog cati : cat.getSubcatalogs()) {
        pCatalogsGs.add(cati.getCatalog());
        //recursion:
        copySubcatalogsGs(cati, pCatalogsGs);
      }
    }
  }

  /**
   * <p>Find sub-catalog-GS by ID in root catalog-T.</p>
   * @param pCatalog trading catalog
   * @param pId Catalog ID
   * @return Catalog GS
   **/
  public final CatalogGs findSubcatalogGsByIdInTc(
    final TradingCatalog pCatalog, final Long pId) {
    for (TradingCatalog cat : pCatalog.getSubcatalogs()) {
      if (cat.getCatalog().getItsId().equals(pId)) {
        return cat.getCatalog();
      }
      for (TradingCatalog cati : cat.getSubcatalogs()) {
        if (cati.getCatalog().getItsId().equals(pId)) {
          return cati.getCatalog();
        }
        //recursion:
        CatalogGs cgs = findSubcatalogGsByIdInTc(cati, pId);
        if (cgs != null) {
          return cgs;
        }
      }
    }
    return null;
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
  public final synchronized List<TradingCatalog> getCatalogs() {
    return this.catalogs;
  }

  /**
   * <p>Setter for catalogs.</p>
   * @param pCatalogs reference
   **/
  public final synchronized void setCatalogs(
    final List<TradingCatalog> pCatalogs) {
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
}
