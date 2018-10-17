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
import java.math.BigDecimal;

import org.beigesoft.log.ILogger;
import org.beigesoft.model.IRequestData;
import org.beigesoft.model.Page;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvPage;
import org.beigesoft.settings.IMngSettings;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.filter.EFilterOperator;
import org.beigesoft.filter.FilterInteger;
import org.beigesoft.filter.FilterBigDecimal;
import org.beigesoft.filter.FilterItems;
import org.beigesoft.webstore.model.TradingCatalog;
import org.beigesoft.webstore.model.CmprTradingCatalog;
import org.beigesoft.webstore.model.EShopItemType;
import org.beigesoft.webstore.model.ESpecificsItemType;
import org.beigesoft.webstore.model.SpecificsFilter;
import org.beigesoft.webstore.model.SpecificsFiltersWhere;
import org.beigesoft.webstore.persistable.ChooseableSpecifics;
import org.beigesoft.webstore.persistable.ChooseableSpecificsType;
import org.beigesoft.webstore.persistable.CatalogSpecifics;
import org.beigesoft.webstore.persistable.CatalogGs;
import org.beigesoft.webstore.persistable.SubcatalogsCatalogsGs;
import org.beigesoft.webstore.persistable.ShoppingCart;
import org.beigesoft.webstore.persistable.CartItem;
import org.beigesoft.webstore.persistable.ItemInList;
import org.beigesoft.webstore.persistable.TradingSettings;
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
   * <p>Total query goods in list for catalog not auctioning
   * same price for all customers.</p>
   **/
  private String queryGilForCatNoAucSmPrTotal;

  /**
   * <p>I18N query goods in list for catalog not auctioning
   * same price for all customers.</p>
   **/
  private String queryGilForCatNoAucSmPrI18n;

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
   * <p>Query for specifics of goods filter.</p>
   **/
  private String querySpecificsGoodsFilter;

  /**
   * <p>Query for specifics of services filter.</p>
   **/
  private String querySpecificsServicesFilter;

  /**
   * <p>Query services in list for catalog not auctioning
   * same price for all customers.</p>
   **/
  private String querySilForCatNoAucSmPr;

  /**
   * <p>Total query services in list for catalog not auctioning
   * same price for all customers.</p>
   **/
  private String querySilForCatNoAucSmPrTotal;

  /**
   * <p>I18N query services in list for catalog not auctioning
   * same price for all customers.</p>
   **/
  private String querySilForCatNoAucSmPrI18n;

  /**
   * <p>Query for specifics of S.E.Goods filter.</p>
   **/
  private String quSpecSeGdFlt;

  /**
   * <p>Query S.E.Goods in list for catalog not auctioning
   * same price for all customers.</p>
   **/
  private String quSeGdForCatNoAucSmPr;

  /**
   * <p>Total qu S.E.Goods in list for catalog not auctioning
   * same price for all customers.</p>
   **/
  private String quSeGdForCatNoAucSmPrTotal;

  /**
   * <p>I18N qu S.E.Goods in list for catalog not auctioning
   * same price for all customers.</p>
   **/
  private String quSeGdForCatNoAucSmPrI18n;

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
   * @param pReqVars additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    pRequestData.setAttribute("catalogs", lazyRetrieveCatalogs(pReqVars));
    TradingSettings tradingSettings = (TradingSettings)
      pReqVars.get("tradingSettings");
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
        this.logger.warn(pReqVars, PrcWebstorePage.class,
          "Can't find catalog #" + catId);
      } else {
        if (tradingSettings.getIsUsePriceForCustomer()) {
          throw new Exception(
            "Method price depends of customer's category not yet implemented!");
        }
        if (tradingSettings.getIsUseAuction()) {
          throw new Exception(
            "Auctioning not yet implemented!");
        }
        FilterInteger filterPrice = revialFilterPrice(tcat,
          pReqVars, pRequestData);
        FilterItems<CatalogGs> filterCatalog = revialFilterCatalog(tcat,
          pReqVars, pRequestData);
        List<SpecificsFilter> filtersSpecifics = revialFiltersSpecifics(tcat,
          pReqVars, pRequestData);
        String whereAdd = revealWhereAdd(filterPrice);
        String whereCatalog = revealWhereCatalog(tcat, filterCatalog);
        String queryg = null;
        String querys = null;
        String queryseg = null;
        if (tradingSettings.getUseAdvancedI18n()) {
          String lang = (String) pReqVars.get("lang");
          String langDef = (String) pReqVars.get("langDef");
          if (!lang.equals(langDef)) {
            if (tcat.getCatalog().getHasGoods()) {
              queryg = lazyGetQueryGilForCatNoAucSmPrI18n().replace(
                ":CATALOGFILTER", whereCatalog).replace(":WHEREADD", whereAdd)
                  .replace(":LANG", lang);
            }
            if (tcat.getCatalog().getHasServices()) {
              querys = lazyGetQuerySilForCatNoAucSmPrI18n().replace(
                ":CATALOGFILTER", whereCatalog).replace(":WHEREADD", whereAdd)
                  .replace(":LANG", lang);
            }
            if (tcat.getCatalog().getHasSeGoods()) {
              queryseg = lazyGetQuSeGdForCatNoAucSmPrI18n().replace(
                ":CATALOGFILTER", whereCatalog).replace(":WHEREADD", whereAdd)
                  .replace(":LANG", lang);
            }
          }
        }
        if (tcat.getCatalog().getHasGoods() && queryg == null) {
          queryg = lazyGetQueryGilForCatNoAucSmPr().replace(
            ":CATALOGFILTER", whereCatalog).replace(":WHEREADD", whereAdd);
        }
        if (tcat.getCatalog().getHasServices() && querys == null) {
          querys = lazyGetQuerySilForCatNoAucSmPr().replace(
            ":CATALOGFILTER", whereCatalog).replace(":WHEREADD", whereAdd);
        }
        if (tcat.getCatalog().getHasSeGoods() && queryseg == null) {
          queryseg = lazyGetQuSeGdForCatNoAucSmPr().replace(
            ":CATALOGFILTER", whereCatalog).replace(":WHEREADD", whereAdd);
        }
        String querygRc = null;
        if (tcat.getCatalog().getHasGoods()) {
          querygRc = lazyGetQueryGilForCatNoAucSmPrTotal().replace(
            ":CATALOGFILTER", whereCatalog).replace(":WHEREADD", whereAdd);
        }
        String querysRc = null;
        if (tcat.getCatalog().getHasServices()) {
          querysRc = lazyGetQuerySilForCatNoAucSmPrTotal().replace(
            ":CATALOGFILTER", whereCatalog).replace(":WHEREADD", whereAdd);
        }
        String querysegRc = null;
        if (tcat.getCatalog().getHasSeGoods()) {
          querysegRc = lazyGetQuSeGdForCatNoAucSmPrTotal().replace(
            ":CATALOGFILTER", whereCatalog).replace(":WHEREADD", whereAdd);
        }
        if (filtersSpecifics != null) {
          if (getLogger().getIsShowDebugMessagesFor(getClass())
            && getLogger().getDetailLevel() > 2000) {
            getLogger().debug(pReqVars, PrcWebstorePage.class,
              "filters apecifics: size: " + filtersSpecifics.size());
          }
          SpecificsFiltersWhere whereSpec =
            revealWhereSpecifics(filtersSpecifics);
          if (whereSpec != null) {
            if (queryg != null) {
              String querySpec = lazyGetQuerySpecificsGoodsFilter().replace(
                ":WHERESPGDFILTER", whereSpec.getWhere()).replace(
                  ":SPGDFILTERCOUNT", whereSpec.getWhereCount().toString());
              queryg += querySpec;
              querygRc += querySpec;
            }
            if (querys != null) {
              String querySpec = lazyGetQuerySpecificsServicesFilter().replace(
                ":WHERESPGDFILTER", whereSpec.getWhere()).replace(
                  ":SPGDFILTERCOUNT", whereSpec.getWhereCount().toString());
              querys += querySpec;
              querysRc += querySpec;
            }
            if (queryseg != null) {
              String querySpec = lazyGetQuSpecSeGdFlt().replace(
                ":WHERESPGDFILTER", whereSpec.getWhere()).replace(
                  ":SPGDFILTERCOUNT", whereSpec.getWhereCount().toString());
              queryseg += querySpec;
              querysegRc += querySpec;
            }
          }
        }
        if (queryg != null || querys != null || queryseg != null) {
          String query = null;
          String queryRc = null;
          if (queryg != null) {
            query = queryg;
            queryRc = querygRc;
          }
          if (querys != null) {
            if (query == null) {
              query = querys;
              queryRc = querysRc;
            } else {
              query += "\n union all \n" + querys;
              queryRc += "\n union all \n" + querysRc;
            }
          }
          if (queryseg != null) {
            if (query == null) {
              query = queryseg;
              queryRc = querysegRc;
            } else {
              query += "\n union all \n" + queryseg;
              queryRc += "\n union all \n" + querysegRc;
            }
          }
          Integer rowCount = this.srvOrm
            .evalRowCountByQuery(pReqVars, ItemInList.class, queryRc);
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
          pReqVars.put("ItemInListneededFields", neededFields);
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
            .retrievePageByQuery(pReqVars, ItemInList.class,
              query, firstResult, itemsPerPage);
          pReqVars.remove("ItemInListneededFields");
          Integer paginationTail = Integer.valueOf(mngUvdSettings
            .getAppSettings().get("paginationTail"));
          List<Page> pages = this.srvPage.evalPages(1, totalPages,
            paginationTail);
          pRequestData.setAttribute("pages", pages);
          pRequestData.setAttribute("itemsList", itemsList);
          pRequestData.setAttribute("totalItems", rowCount);
        }
        if (filterPrice != null) {
          pRequestData.setAttribute("filterPrice", filterPrice);
        }
        if (filterCatalog != null) {
          pRequestData.setAttribute("filterCatalog", filterCatalog);
        }
        if (filtersSpecifics != null) {
          pRequestData.setAttribute("filtersSpecifics", filtersSpecifics);
        }
        pRequestData.setAttribute("catalog", tcat.getCatalog());
      }
    }
    if (pRequestData.getAttribute("shoppingCart") == null) {
      ShoppingCart shoppingCart = this.srvShoppingCart
        .getShoppingCart(pReqVars, pRequestData, false);
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
   * @param pReqVars params
   * @return trading catalogs
   * @throws Exception - an exception
   **/
  public final List<TradingCatalog> lazyRetrieveCatalogs(
    final Map<String, Object> pReqVars) throws Exception {
    if (this.catalogs == null) {
      synchronized (this) {
        if (this.catalogs == null) {
          List<CatalogGs> catalogsGs = getSrvOrm().retrieveListWithConditions(
            pReqVars, CatalogGs.class, " order by ITSINDEX");
          pReqVars.put("CatalogSpecificsitsOwnerdeepLevel", 1); //only ID
          for (CatalogGs cat : catalogsGs) {
            CatalogSpecifics cs = new CatalogSpecifics();
            cs.setItsOwner(cat);
            cat.setUsedSpecifics(getSrvOrm().retrieveListForField(pReqVars,
              cs, "itsOwner"));
          }
          pReqVars.remove("CatalogSpecificsitsOwnerdeepLevel");
          //only ID:
          pReqVars.put("SubcatalogsCatalogsGsitsCatalogdeepLevel", 1);
          pReqVars.put("SubcatalogsCatalogsGssubcatalogdeepLevel", 1);
          List<SubcatalogsCatalogsGs> scList = getSrvOrm().retrieveList(
            pReqVars, SubcatalogsCatalogsGs.class);
          pReqVars.remove("SubcatalogsCatalogsGsitsCatalogdeepLevel");
          pReqVars.remove("SubcatalogsCatalogsGssubcatalogdeepLevel");
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
          refreshCatalogsFilters(pReqVars,  result);
          this.catalogs = result;
        }
      }
    }
    return this.catalogs;
  }

  /**
   * <p>Refresh catalog filters, the first catalog in which filter is enabled
   * is propagated into sub-catalogs.</p>
   * @param pReqVars params
   * @param pCurrentList Catalog List current
   * @throws Exception an Exception
   **/
  public final void refreshCatalogsFilters(final Map<String, Object> pReqVars,
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
          pReqVars.put("CatalogSpecificsitsOwnerdeepLevel", 1); //only ID
          tc.getCatalog().setUsedSpecifics(getSrvOrm().retrieveListForField(
            pReqVars, cs, "itsOwner"));
          pReqVars.remove("CatalogSpecificsitsOwnerdeepLevel");
        }
        propagateCatalogSettings(tc);
      } else if (tc.getSubcatalogs().size() > 0) {
        tc.getCatalog().setUsedSpecifics(null); //reset if not null
        //recursion:
        refreshCatalogsFilters(pReqVars, tc.getSubcatalogs());
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
   * @param pReqVars params
   * @param pRequestData Request Data
   * @return filter price or null if catalog hasn't any filter
   * @throws Exception an Exception
   **/
  public final FilterInteger revialFilterPrice(final TradingCatalog pTcat,
    final Map<String, Object> pReqVars,
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
   * <p>Reveal filters specifics for given catalog
   * and fill it from request data.</p>
   * @param pTcat Catalog
   * @param pReqVars params
   * @param pRequestData Request Data
   * @return filter specifics or null
   * @throws Exception an Exception
   **/
  public final List<SpecificsFilter> revialFiltersSpecifics(
    final TradingCatalog pTcat, final Map<String, Object> pReqVars,
      final IRequestData pRequestData) throws Exception {
    if (pTcat.getCatalog().getUsedSpecifics() != null
      && pTcat.getCatalog().getUsedSpecifics().size() > 0) {
      List<SpecificsFilter> res = new ArrayList<SpecificsFilter>();
      for (CatalogSpecifics cs : pTcat.getCatalog().getUsedSpecifics()) {
        String operStr = pRequestData.getParameter("fltSp"
          + cs.getSpecifics().getItsId() + "Op");
        if (cs.getSpecifics().getChooseableSpecificsType() != null
        && cs.getSpecifics().getChooseableSpecificsType().getItsId() != null) {
          FilterItems<ChooseableSpecifics> fltItms =
            new FilterItems<ChooseableSpecifics>();
          SpecificsFilter spf = new SpecificsFilter();
          spf.setFilter(fltItms);
          spf.setCatSpec(cs);
          res.add(spf);
          fltItms.setItemsAll(retrieveAllChSpecifics(pReqVars,
            cs.getSpecifics().getChooseableSpecificsType()));
          String[] valStrs = pRequestData.getParameterValues("fltSp"
            + cs.getSpecifics().getItsId() + "Val");
          if (operStr != null && !"".equals(operStr)
            && valStrs != null && valStrs.length > 0) {
            fltItms.setOperator(Enum.valueOf(EFilterOperator.class, operStr));
            for (String idStr : valStrs) {
              Long id = Long.valueOf(idStr);
              ChooseableSpecifics chs = findChSpecificsById(fltItms
                .getItemsAll(), id);
              if (chs == null) {
                this.logger.warn(pReqVars, PrcWebstorePage.class,
                  "Can't find chspecifics #: " + id);
              } else {
                fltItms.getItems().add(chs);
              }
            }
          }
        } else if (ESpecificsItemType.INTEGER
          .equals(cs.getSpecifics().getItsType())) {
          FilterInteger flt = new FilterInteger();
          SpecificsFilter spf = new SpecificsFilter();
          spf.setFilter(flt);
          spf.setCatSpec(cs);
          res.add(spf);
          String val1Str = pRequestData.getParameter("fltSp"
            + cs.getSpecifics().getItsId() + "Val1");
          if (operStr != null && !"".equals(operStr)
            && val1Str != null && val1Str.length() > 0) {
            flt.setOperator(Enum.valueOf(EFilterOperator.class, operStr));
            flt.setValue1(Integer.valueOf(val1Str));
            String val2Str = pRequestData.getParameter("fltSp"
              + cs.getSpecifics().getItsId() + "Val2");
            if (val2Str != null && val2Str.length() > 0) {
              flt.setValue2(Integer.valueOf(val2Str));
            }
          }
        } else if (ESpecificsItemType.BIGDECIMAL
          .equals(cs.getSpecifics().getItsType())) {
          FilterBigDecimal flt = new FilterBigDecimal();
          SpecificsFilter spf = new SpecificsFilter();
          spf.setFilter(flt);
          spf.setCatSpec(cs);
          res.add(spf);
          String val1Str = pRequestData.getParameter("fltSp"
            + cs.getSpecifics().getItsId() + "Val1");
          if (operStr != null && !"".equals(operStr)
            && val1Str != null && val1Str.length() > 0) {
            flt.setOperator(Enum.valueOf(EFilterOperator.class, operStr));
            flt.setValue1(new BigDecimal(val1Str));
            String val2Str = pRequestData.getParameter("fltSp"
              + cs.getSpecifics().getItsId() + "Val2");
            if (val2Str != null && val2Str.length() > 0) {
              flt.setValue2(new BigDecimal(val2Str));
            }
          }
        } else {
          this.logger.error(pReqVars, PrcWebstorePage.class,
            "Filter specifics not implemented yet, for - "
              + cs.getSpecifics().getItsName());
        }
      }
      return res;
    } else {
      return null;
    }
  }
  /**
   * <p>Retrieve all choseeable specifics.</p>
   * @param pReqVars params
   * @param pChSpecType Ch-Specifics Type
   * @return List Ch-Specifics
   * @throws Exception an Exception
   **/
  public final List<ChooseableSpecifics> retrieveAllChSpecifics(
    final Map<String, Object> pReqVars,
      final ChooseableSpecificsType pChSpecType) throws Exception {
    ChooseableSpecifics chs = new ChooseableSpecifics();
    chs.setItsType(pChSpecType);
    pReqVars.put("ChooseableSpecificsitsTypedeepLevel", 1); //only ID
    List<ChooseableSpecifics> result = getSrvOrm()
      .retrieveListForField(pReqVars, chs, "itsType");
    pReqVars.remove("ChooseableSpecificsitsTypedeepLevel");
    return result;
  }

  /**
   * <p>Find chooseable specifics in given list by ID.</p>
   * @param pListChSpecifics List Ch-Specifics
   * @param pId Id
   * @return chooseable specifics or null
   **/
  public final ChooseableSpecifics findChSpecificsById(
    final List<ChooseableSpecifics> pListChSpecifics,
      final Long pId) {
    for (ChooseableSpecifics chs : pListChSpecifics) {
      if (chs.getItsId().equals(pId)) {
        return chs;
      }
    }
    return null;
  }


  /**
   * <p>Reveal filter catalog for catalog and fill it from request data.</p>
   * @param pTcat Catalog
   * @param pReqVars params
   * @param pRequestData Request Data
   * @return filter catalog or null if catalog hasn't any filter
   * @throws Exception an Exception
   **/
  public final FilterItems<CatalogGs> revialFilterCatalog(
    final TradingCatalog pTcat, final Map<String, Object> pReqVars,
      final IRequestData pRequestData) throws Exception {
    if (pTcat.getCatalog().getUseFilterSubcatalog()) {
      FilterItems<CatalogGs> res = new FilterItems<CatalogGs>();
      copySubcatalogsGs(pTcat, res.getItemsAll());
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
          res.getItems().add(cgs);
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
      } else if (EFilterOperator.BETWEEN_INCLUDE
        .equals(pFilterPrice.getOperator())) {
        return " and ITSPRICE<=" + pFilterPrice.getValue1()
          + " and ITSPRICE>=" + pFilterPrice.getValue2();
      }
    }
    return "";
  }

  /**
   * <p>Reveal part of where specifics clause e.g.:
   * "(SPECIFICS=3 and LONGVALUE1 in (3, 14))
   * or (SPECIFICS=4 and NUMERICVALUE1&lt;2.33)",
   * and count of conditions, or null.</p>
   * @param pFiltersSpecifics Filters Specifics
   * @return SpecificsFiltersWhere bundle or null
   * @throws Exception an Exception
   **/
  public final SpecificsFiltersWhere revealWhereSpecifics(
    final List<SpecificsFilter> pFiltersSpecifics) throws Exception {
    SpecificsFiltersWhere result = null;
    StringBuffer sb = new StringBuffer();
    boolean isFirst = true;
    for (SpecificsFilter sf : pFiltersSpecifics) {
      if (sf.getFilter().getOperator() != null) {
        if (isFirst) {
          sb.append("(SPECIFICS=" + sf.getCatSpec().getSpecifics().getItsId()
            + " and ");
          isFirst = false;
        } else {
          sb.append(" or (SPECIFICS=" + sf.getCatSpec().getSpecifics()
            .getItsId() + " and ");
        }
        if (sf.getFilter().getClass() == FilterItems.class) {
          @SuppressWarnings("unchecked")
          FilterItems<ChooseableSpecifics> fltItms =
            (FilterItems<ChooseableSpecifics>) sf.getFilter();
          sb.append("LONGVALUE1");
          if (EFilterOperator.IN.equals(fltItms.getOperator())
            && fltItms.getItems().size() == 1) {
            sb.append("=" + fltItms.getItems().get(0).getItsId());
          } else if (EFilterOperator.NOT_IN.equals(fltItms.getOperator())
            && fltItms.getItems().size() == 1) {
            sb.append("!=" + fltItms.getItems().get(0).getItsId());
          } else {
            if (EFilterOperator.IN.equals(fltItms.getOperator())) {
              sb.append(" in (");
            } else {
              sb.append(" not in (");
            }
            boolean isFstItm = true;
            for (ChooseableSpecifics chs : fltItms.getItems()) {
              if (isFstItm) {
                isFstItm = false;
              } else {
                sb.append(",");
              }
              sb.append(chs.getItsId());
            }
            sb.append(")");
          }
        } else if (sf.getFilter().getClass() == FilterInteger.class) {
          FilterInteger flt = (FilterInteger) sf.getFilter();
          sb.append("LONGVALUE1");
          if (EFilterOperator.LESS_THAN.equals(flt.getOperator())
            || EFilterOperator.LESS_THAN_EQUAL.equals(flt.getOperator())
              || EFilterOperator.GREATER_THAN.equals(flt.getOperator())
            || EFilterOperator.GREATER_THAN_EQUAL.equals(flt.getOperator())) {
            sb.append(toSqlOperator(flt.getOperator()) + flt.getValue1());
          } else if (EFilterOperator.BETWEEN.equals(flt.getOperator())) {
            sb.append(">" + flt.getValue1() + " and LONGVALUE2<"
              + flt.getValue2());
          } else if (EFilterOperator.BETWEEN_INCLUDE
            .equals(flt.getOperator())) {
            sb.append(">=" + flt.getValue1() + " and LONGVALUE2<="
              + flt.getValue2());
          } else {
            throw new Exception(
              "Algorithm error for where integer specifics/operator: "
                + sf.getCatSpec().getSpecifics().getItsName()
                  + "/" + flt.getOperator());
          }
        } else if (sf.getFilter().getClass() == FilterBigDecimal.class) {
          FilterBigDecimal flt = (FilterBigDecimal) sf.getFilter();
          sb.append("NUMERICVALUE1");
          if (EFilterOperator.LESS_THAN.equals(flt.getOperator())
            || EFilterOperator.LESS_THAN_EQUAL.equals(flt.getOperator())
              || EFilterOperator.GREATER_THAN.equals(flt.getOperator())
            || EFilterOperator.GREATER_THAN_EQUAL.equals(flt.getOperator())) {
            sb.append(toSqlOperator(flt.getOperator()) + flt.getValue1());
          } else if (EFilterOperator.BETWEEN.equals(flt.getOperator())) {
            sb.append(">" + flt.getValue1() + " and NUMERICVALUE2<"
              + flt.getValue2());
          } else if (EFilterOperator.BETWEEN_INCLUDE
            .equals(flt.getOperator())) {
            sb.append(">=" + flt.getValue1() + " and NUMERICVALUE2<="
              + flt.getValue2());
          } else {
            throw new Exception(
              "Algorithm error for where integer specifics/operator: "
                + sf.getCatSpec().getSpecifics().getItsName()
                  + "/" + flt.getOperator());
          }
        } else {
          throw new Exception(
            "Making WHERE not implemented for specifics/filter: "
              + sf.getCatSpec().getSpecifics().getItsName()
                + "/" + sf.getFilter().getClass());
        }
        sb.append(")");
        if (result == null) {
          result = new SpecificsFiltersWhere();
        }
        result.setWhereCount(result.getWhereCount() + 1);
      }
    }
    if (result != null) {
      result.setWhere(sb.toString());
    }
    return result;
  }

  /**
   * <p>Reveal part of where catalog clause e.g. "=12",
   * " in (12,14)".</p>
   * @param pTcat Catalog
   * @param pFilterCatalog Filter Catalog
   * @return part of where clause e.g. "=12", " in (12,14)"
   * @throws Exception an Exception
   **/
  public final String revealWhereCatalog(final TradingCatalog pTcat,
    final FilterItems<CatalogGs> pFilterCatalog) throws Exception {
    List<CatalogGs> subcgsAll = new ArrayList<CatalogGs>();
    subcgsAll.add(pTcat.getCatalog());
    if (pFilterCatalog != null && pFilterCatalog.getOperator() != null
      && pFilterCatalog.getItems().size() > 0) {
      for (CatalogGs cgs : pFilterCatalog.getItems()) {
        TradingCatalog tcat = findTradingCatalogById(this.catalogs,
          cgs.getItsId());
        subcgsAll.add(cgs);
        copySubcatalogsGs(tcat, subcgsAll);
      }
    } else {
      copySubcatalogsGs(pTcat, subcgsAll);
    }
    Set<CatalogGs> subcgs = new HashSet<CatalogGs>();
    for (CatalogGs cgs : subcgsAll) {
      if (!cgs.getHasSubcatalogs()) {
        subcgs.add(cgs);
      }
    }
    if (subcgs.size() > 1) {
      StringBuffer sb = new StringBuffer(" in ("
        + pTcat.getCatalog().getItsId());
      for (CatalogGs cgs : subcgs) {
        sb.append("," + cgs.getItsId());
      }
      sb.append(")");
      return sb.toString();
    } else {
      return "=" + subcgs.iterator().next().getItsId();
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
   * <p>Set filters/orders/has goods/services...
   * for all sub-catalogs same as main-catalog.</p>
   * @param pMainCatalog main catalog
   * @throws Exception an Exception
   **/
  public final void propagateCatalogSettings(
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
      tc.getCatalog().setHasGoods(pMainCatalog.getCatalog()
        .getHasGoods());
      tc.getCatalog().setHasServices(pMainCatalog.getCatalog()
        .getHasServices());
      tc.getCatalog().setHasSeGoods(pMainCatalog.getCatalog()
        .getHasSeGoods());
      tc.getCatalog().setHasSeServices(pMainCatalog.getCatalog()
        .getHasSeServices());
      if (tc.getSubcatalogs().size() > 0) {
        //recursion:
        propagateCatalogSettings(tc);
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
   * <p>Lazy Get quSeGdForCatNoAucSmPrTotal.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQuSeGdForCatNoAucSmPrTotal() throws Exception {
    if (this.quSeGdForCatNoAucSmPrTotal == null) {
      String flName =
        "/webstore/seGdInLstForCatNotAucSmPrTot.sql";
      this.quSeGdForCatNoAucSmPrTotal = loadString(flName);
    }
    return this.quSeGdForCatNoAucSmPrTotal;
  }

  /**
   * <p>Lazy Get quSeGdForCatNoAucSmPr.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQuSeGdForCatNoAucSmPr() throws Exception {
    if (this.quSeGdForCatNoAucSmPr == null) {
      String flName = "/webstore/seGdInLstForCatNotAucSmPr.sql";
      this.quSeGdForCatNoAucSmPr = loadString(flName);
    }
    return this.quSeGdForCatNoAucSmPr;
  }

  /**
   * <p>Lazy Get quSeGdForCatNoAucSmPrI18n.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQuSeGdForCatNoAucSmPrI18n() throws Exception {
    if (this.quSeGdForCatNoAucSmPrI18n == null) {
      String flName =
        "/webstore/seGdInLstForCatNotAucSmPrI18n.sql";
      this.quSeGdForCatNoAucSmPrI18n = loadString(flName);
    }
    return this.quSeGdForCatNoAucSmPrI18n;
  }

  /**
   * <p>Lazy Get quSpecSeGdFlt.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQuSpecSeGdFlt() throws Exception {
    if (this.quSpecSeGdFlt == null) {
      String flName = "/webstore/specificsSeGoodsFilter.sql";
      this.quSpecSeGdFlt = loadString(flName);
    }
    return this.quSpecSeGdFlt;
  }

  /**
   * <p>Lazy Get querySilForCatNoAucSmPrTotal.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String
    lazyGetQuerySilForCatNoAucSmPrTotal() throws Exception {
    if (this.querySilForCatNoAucSmPrTotal == null) {
      String flName =
        "/webstore/servicesInListForCatalogNotAucSamePriceTotal.sql";
      this.querySilForCatNoAucSmPrTotal = loadString(flName);
    }
    return this.querySilForCatNoAucSmPrTotal;
  }

  /**
   * <p>Lazy Get querySilForCatNoAucSmPr.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String
    lazyGetQuerySilForCatNoAucSmPr() throws Exception {
    if (this.querySilForCatNoAucSmPr == null) {
      String flName = "/webstore/servicesInListForCatalogNotAucSamePrice.sql";
      this.querySilForCatNoAucSmPr = loadString(flName);
    }
    return this.querySilForCatNoAucSmPr;
  }

  /**
   * <p>Lazy Get querySilForCatNoAucSmPrI18n.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String
    lazyGetQuerySilForCatNoAucSmPrI18n() throws Exception {
    if (this.querySilForCatNoAucSmPrI18n == null) {
      String flName =
        "/webstore/servicesInListForCatalogNotAucSamePriceI18n.sql";
      this.querySilForCatNoAucSmPrI18n = loadString(flName);
    }
    return this.querySilForCatNoAucSmPrI18n;
  }

  /**
   * <p>Lazy Get querySpecificsServicesFilter.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQuerySpecificsServicesFilter() throws Exception {
    if (this.querySpecificsServicesFilter == null) {
      String flName = "/webstore/specificsServiceFilter.sql";
      this.querySpecificsServicesFilter = loadString(flName);
    }
    return this.querySpecificsServicesFilter;
  }

  /**
   * <p>Lazy Get queryGilForCatNoAucSmPrTotal.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String
    lazyGetQueryGilForCatNoAucSmPrTotal() throws Exception {
    if (this.queryGilForCatNoAucSmPrTotal == null) {
      String flName = "/webstore/goodsInListForCatalogNotAucSamePriceTotal.sql";
      this.queryGilForCatNoAucSmPrTotal = loadString(flName);
    }
    return this.queryGilForCatNoAucSmPrTotal;
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
   * <p>Lazy Get queryGilForCatNoAucSmPrI18n.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String
    lazyGetQueryGilForCatNoAucSmPrI18n() throws Exception {
    if (this.queryGilForCatNoAucSmPrI18n == null) {
      String flName = "/webstore/goodsInListForCatalogNotAucSamePriceI18n.sql";
      this.queryGilForCatNoAucSmPrI18n = loadString(flName);
    }
    return this.queryGilForCatNoAucSmPrI18n;
  }

  /**
   * <p>Lazy Get querySpecificsGoodsFilter.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQuerySpecificsGoodsFilter() throws Exception {
    if (this.querySpecificsGoodsFilter == null) {
      String flName = "/webstore/specificsGoodsFilter.sql";
      this.querySpecificsGoodsFilter = loadString(flName);
    }
    return this.querySpecificsGoodsFilter;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = PrcWebstorePage.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = PrcWebstorePage.class
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
   * <p>Setter for queryGilForCatNoAucSmPrI18n.</p>
   * @param pQueryGilForCatNoAucSmPrI18n reference
   **/
  public final void setQueryGilForCatNoAucSmPrI18n(
    final String pQueryGilForCatNoAucSmPrI18n) {
    this.queryGilForCatNoAucSmPrI18n = pQueryGilForCatNoAucSmPrI18n;
  }

  /**
   * <p>Setter for queryGilForCatNoAucSmPrTotal.</p>
   * @param pQueryGilForCatNoAucSmPrTotal reference
   **/
  public final void setQueryGilForCatNoAucSmPrTotal(
    final String pQueryGilForCatNoAucSmPrTotal) {
    this.queryGilForCatNoAucSmPrTotal = pQueryGilForCatNoAucSmPrTotal;
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

  /**
   * <p>Getter for querySpecificsGoodsFilter.</p>
   * @return String
   **/
  public final String getQuerySpecificsGoodsFilter() {
    return this.querySpecificsGoodsFilter;
  }

  /**
   * <p>Setter for querySpecificsGoodsFilter.</p>
   * @param pQuerySpecificsGoodsFilter reference
   **/
  public final void setQuerySpecificsGoodsFilter(
    final String pQuerySpecificsGoodsFilter) {
    this.querySpecificsGoodsFilter = pQuerySpecificsGoodsFilter;
  }
}
