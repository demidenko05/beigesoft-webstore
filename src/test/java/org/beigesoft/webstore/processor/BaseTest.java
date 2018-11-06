package org.beigesoft.webstore.processor;

/*
 * Copyright (c) 2018 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import org.beigesoft.filter.FilterItems;
import org.beigesoft.webstore.model.TradingCatalog;
import org.beigesoft.webstore.persistable.CatalogGs;

/**
 * <p>Base WS tests.</p>
 *
 * @author Yury Demidenko
 */
public class BaseTest {

  PrcWebstorePage prcWebstorePage;

  public BaseTest() {
    this.prcWebstorePage = new PrcWebstorePage();
  }

  @Test
  public void test1() throws Exception {
    List<TradingCatalog> cats = new ArrayList<TradingCatalog>();
    CatalogGs c1l = new CatalogGs();
    c1l.setItsName("c1l1");
    c1l.setItsId(1L);
    TradingCatalog tc1l =new TradingCatalog();
    tc1l.setCatalog(c1l);
    cats.add(tc1l);
    CatalogGs c2l1 = new CatalogGs();
    c2l1.setItsName("c2l1");
    c2l1.setItsId(21L);
    TradingCatalog tc2l1 =new TradingCatalog();
    tc2l1.setCatalog(c2l1);
    tc1l.getSubcatalogs().add(tc2l1);
    CatalogGs c2l2 = new CatalogGs();
    c2l2.setItsName("c2l2");
    c2l2.setItsId(22L);
    TradingCatalog tc2l2 =new TradingCatalog();
    tc2l2.setCatalog(c2l2);
    tc1l.getSubcatalogs().add(tc2l2);
    CatalogGs c3l1 = new CatalogGs();
    c3l1.setItsName("c3l1");
    c3l1.setItsId(31L);
    TradingCatalog tc3l1 =new TradingCatalog();
    tc3l1.setCatalog(c3l1);
    tc2l1.getSubcatalogs().add(tc3l1);
    List<CatalogGs> subcgs = new ArrayList<CatalogGs>();
    this.prcWebstorePage.copySubcatalogsGs(tc1l, subcgs);
    for (CatalogGs cgs : subcgs) {
      System.out.println(cgs.getItsName() + "#" + cgs.getItsId());
    }
    FilterItems<CatalogGs> fltCat = new FilterItems<CatalogGs>();
    TradingCatalog tc1 = this.prcWebstorePage.findTradingCatalogById(cats, 1L);
    this.prcWebstorePage.copySubcatalogsGs(tc1, fltCat.getItemsAll());
    CatalogGs cgs3l1 = this.prcWebstorePage.findSubcatalogGsByIdInTc(tc1, 31L);
    assertNotNull(cgs3l1);
    assertEquals(31L, cgs3l1.getItsId().longValue());
    assertTrue(tc2l1.getSubcatalogs().size() > 0);
    assertTrue(tc2l2.getSubcatalogs().size() == 0);
    assertTrue(tc1l.getSubcatalogs().size() == 2);
    assertEquals(3, subcgs.size());
    assertEquals(3, fltCat.getItemsAll().size());
    BigDecimal quant = new BigDecimal("2.28");
    BigDecimal uStep = new BigDecimal("0.25");
    BigDecimal qosr = quant.remainder(uStep);
    assertEquals(qosr.compareTo(new BigDecimal("0.03")), 0);
    quant = new BigDecimal("10.00");
    uStep = new BigDecimal("0.01");
    qosr = quant.remainder(uStep);
    assertEquals(qosr.compareTo(new BigDecimal("0.0")), 0);
  }
}
