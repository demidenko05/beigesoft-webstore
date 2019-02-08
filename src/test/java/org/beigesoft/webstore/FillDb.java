package org.beigesoft.webstore;

/*
 * Copyright (c) 2019 Beigesoft™
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
import java.util.HashMap;
import java.util.Date;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;

import com.zaxxer.hikari.HikariDataSource;

import org.beigesoft.jdbc.service.SrvDatabase;
import org.beigesoft.orm.service.SrvOrmSqlite;
import org.beigesoft.settings.MngSettings;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.SrvSqlEscape;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.factory.FctConvertersToFromString;
import org.beigesoft.factory.FctFillersObjectFields;
import org.beigesoft.holder.IHolderForClassByName;
import org.beigesoft.holder.HolderRapiSetters;
import org.beigesoft.holder.HolderRapiGetters;
import org.beigesoft.holder.HolderRapiFields;
import org.beigesoft.persistable.UserTomcat;
import org.beigesoft.persistable.UserRoleTomcat;
import org.beigesoft.persistable.IdUserRoleTomcat;
import org.beigesoft.service.IUtlReflection;
import org.beigesoft.properties.UtlProperties;
import org.beigesoft.service.UtlReflection;
import org.beigesoft.settings.MngSettings;
import org.beigesoft.orm.factory.FctBnCnvIbnToColumnValues;
import org.beigesoft.orm.factory.FctBcCnvEntityToColumnsValues;
import org.beigesoft.factory.FctBcFctSimpleEntities;
import org.beigesoft.orm.factory.FctBnCnvBnFromRs;
import org.beigesoft.model.ColumnsValues;
import org.beigesoft.orm.holder.HldCnvToColumnsValuesNames;
import org.beigesoft.orm.holder.HldCnvFromRsNames;
import org.beigesoft.service.SrvSqlEscape;
import org.beigesoft.service.HlpInsertUpdate;
import org.beigesoft.orm.service.FillerEntitiesFromRs;
import org.beigesoft.log.LoggerSimple;
import org.beigesoft.converter.CnvHasVersionToColumnsValues;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.InvItemCategory;
import org.beigesoft.accounting.persistable.InvItemType;
import org.beigesoft.accounting.persistable.UnitOfMeasure;
import org.beigesoft.webstore.persistable.CatalogGs;
import org.beigesoft.webstore.persistable.GoodsCatalog;
import org.beigesoft.webstore.persistable.PickUpPlace;
import org.beigesoft.webstore.persistable.GoodsPlace;
import org.beigesoft.webstore.persistable.PriceGoods;
import org.beigesoft.webstore.persistable.PriceCategory;
import org.beigesoft.webstore.persistable.SpecificsOfItem;
import org.beigesoft.webstore.persistable.GoodsSpecifics;

/**
 * <p>Populating SQLite database with sample data - pizza with bacon hot#, pizza with cheese hot#, Ford #, Honda#.
 * Database must has tax category, item category, catalogs as in sample database bobs-pizza-ws2!
 * Usage with Maven example, fill database from /beige-orm/sqlite/app-settings.xml (bobs-pizza-ws2.sqlite) with 1000 sample records for each good:
 * <pre>
 * mvn exec:java -Dexec.mainClass="org.beigesoft.webstore.FillDb" -Dexec.args="100" -Dexec.classpathScope=test
 * </pre>
 * </p>
 *
 * @author Yury Demidenko
 */
public class FillDb {
  

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.err.println("usage: " + FillDb.class.getName() +
      "<records count>");
      System.exit(1);
    }
    String rcs = args[0];
    int rct = 0;
    try {
      rct = Integer.parseInt(rcs);
    } catch (Exception e) {
      System.err.println("usage: " + FillDb.class.getName() +
      "<records count>");
      System.exit(1);
    }
    final int rc = rct;
    Thread thread1 = new Thread() {
      public void run() {
        FillDb fdb = new FillDb();
        try {
          fdb.populate(rc);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    thread1.start();
  }

  public void populate(int pRc) throws Exception {
    SrvOrmSqlite<ResultSet> srvOrm;
    SrvDatabase srvDatabase;
    LoggerSimple logger = new LoggerSimple();
    IUtlReflection utlReflection = new UtlReflection();
    logger.setIsShowDebugMessages(false);
    //logger.setDetailLevel(100000);
    srvOrm = new SrvOrmSqlite<ResultSet>();
    srvDatabase = new SrvDatabase();
    srvDatabase.setLogger(logger);
    srvOrm.setSrvDatabase(srvDatabase);
    srvOrm.setLogger(logger);
    srvOrm.setHlpInsertUpdate(new HlpInsertUpdate());
    srvOrm.setUtlReflection(utlReflection);
    srvDatabase.setHlpInsertUpdate(srvOrm.getHlpInsertUpdate());
    MngSettings mngSettings = new MngSettings();
    mngSettings.setLogger(logger);
    mngSettings.setUtlProperties(new UtlProperties());
    mngSettings.setUtlReflection(new UtlReflection());
    srvOrm.setMngSettings(mngSettings);
    srvOrm.loadConfiguration("beige-orm", "persistence-sqlite.xml");
    FctBnCnvIbnToColumnValues facConvFields = new FctBnCnvIbnToColumnValues();
    facConvFields.setUtlReflection(utlReflection);
    facConvFields.setTablesMap(srvOrm.getTablesMap());
    HolderRapiGetters hrg = new HolderRapiGetters();
    hrg.setUtlReflection(utlReflection);
    facConvFields.setGettersRapiHolder(hrg);
    HolderRapiFields hrf = new HolderRapiFields();
    hrf.setUtlReflection(utlReflection);
    facConvFields.setFieldsRapiHolder(hrf);
    facConvFields.setSrvSqlEscape(new SrvSqlEscape());
    FctBcCnvEntityToColumnsValues fcetcv = new FctBcCnvEntityToColumnsValues();
    HldCnvToColumnsValuesNames hldConvFld = new HldCnvToColumnsValuesNames();
    hldConvFld.setFieldsRapiHolder(hrf);
    fcetcv.setLogger(logger);
    fcetcv.setTablesMap(srvOrm.getTablesMap());
    fcetcv.setFieldsConvertersNamesHolder(hldConvFld);
    fcetcv.setGettersRapiHolder(hrg);
    fcetcv.setFieldsRapiHolder(hrf);
    fcetcv.setFieldsConvertersFatory(facConvFields);
    srvOrm.setFactoryCnvEntityToColumnsValues(fcetcv);
    FillerEntitiesFromRs<ResultSet> fillerEntitiesFromRs = new FillerEntitiesFromRs<ResultSet>();
    fillerEntitiesFromRs.setTablesMap(srvOrm.getTablesMap());
    fillerEntitiesFromRs.setLogger(logger);
    fillerEntitiesFromRs.setFieldsRapiHolder(hrf);
    FctFillersObjectFields fctFillersObjectFields = new FctFillersObjectFields();
    fctFillersObjectFields.setUtlReflection(utlReflection);
    HolderRapiSetters hrs = new HolderRapiSetters();
    hrs.setUtlReflection(utlReflection);
    fctFillersObjectFields.setSettersRapiHolder(hrs);
    fillerEntitiesFromRs.setFillersFieldsFactory(fctFillersObjectFields);
    srvOrm.setFctFillersObjectFields(fctFillersObjectFields);
    FctBnCnvBnFromRs<ResultSet> fctBnCnvBnFromRs = new FctBnCnvBnFromRs<ResultSet>();
    FctBcFctSimpleEntities fctBcFctSimpleEntities = new FctBcFctSimpleEntities();
    fctBcFctSimpleEntities.setSrvDatabase(srvDatabase);
    srvOrm.setEntitiesFactoriesFatory(fctBcFctSimpleEntities);
    fctBnCnvBnFromRs.setEntitiesFactoriesFatory(fctBcFctSimpleEntities);
    fctBnCnvBnFromRs.setFillersFieldsFactory(fctFillersObjectFields);
    fctBnCnvBnFromRs.setTablesMap(srvOrm.getTablesMap());
    fctBnCnvBnFromRs.setFieldsRapiHolder(hrf);
    fctBnCnvBnFromRs.setFillerObjectsFromRs(fillerEntitiesFromRs);
    fillerEntitiesFromRs.setConvertersFieldsFatory(fctBnCnvBnFromRs);
    HldCnvFromRsNames hldCnvFromRsNames = new HldCnvFromRsNames();
    hldCnvFromRsNames.setFieldsRapiHolder(hrf);
    fillerEntitiesFromRs.setFieldConverterNamesHolder(hldCnvFromRsNames);
    srvOrm.setFillerEntitiesFromRs(fillerEntitiesFromRs);
    String currDir = System.getProperty("user.dir");
    System.out.println("Start test JDBC Sqlite");
    System.out.println("Current dir using System:" + currDir);
    HikariDataSource ds = new HikariDataSource();
    ds.setJdbcUrl(srvOrm.getPropertiesBase().getDatabaseUrl());
    ds.setDriverClassName(srvOrm.getPropertiesBase().getJdbcDriverClass());
    srvDatabase.setDataSource(ds);

    System.out.println("Populating db/records: " + srvOrm.getPropertiesBase().getDatabaseUrl() + "/" + pRc);
    Map<String, Object> rqVs = new HashMap<String, Object>();
    InvItemCategory pbhc = new InvItemCategory();
    pbhc.setItsId(3L);
    InvItemCategory pchc = new InvItemCategory();
    pchc.setItsId(2L);
    InvItemType gd = new InvItemType();
    gd.setItsId(4L);
    UnitOfMeasure each = new UnitOfMeasure();
    each.setItsId(1L);
    CatalogGs ctpbh = new CatalogGs();
    ctpbh.setItsId(122L);
    CatalogGs ctpch = new CatalogGs();
    ctpch.setItsId(121L);
    CatalogGs catCars = new CatalogGs();
    catCars.setItsId(4L);
    PickUpPlace pzr = new PickUpPlace();
    pzr.setItsId(1L);
    PriceCategory pct = new PriceCategory();
    pct.setItsId(2L);
    SpecificsOfItem img = new SpecificsOfItem();
    img.setItsId(1L);
    SpecificsOfItem weight = new SpecificsOfItem();
    weight.setItsId(10L);
    SpecificsOfItem spYear = new SpecificsOfItem();
    spYear.setItsId(105L);
    SpecificsOfItem spManuf = new SpecificsOfItem();
    spManuf.setItsId(100L);
    SpecificsOfItem spClr = new SpecificsOfItem();
    spClr.setItsId(101L);
    SpecificsOfItem spFuel = new SpecificsOfItem();
    spFuel.setItsId(103L);
    SpecificsOfItem spBody = new SpecificsOfItem();
    spBody.setItsId(102L);
    SpecificsOfItem spTrn = new SpecificsOfItem();
    spTrn.setItsId(104L);
    SpecificsOfItem spHtml = new SpecificsOfItem();
    spHtml.setItsId(1000L);
    SpecificsOfItem spIm1 = new SpecificsOfItem();
    spIm1.setItsId(2000L);
    SpecificsOfItem spIm2 = new SpecificsOfItem();
    spIm2.setItsId(2001L);
    SpecificsOfItem spIm3 = new SpecificsOfItem();
    spIm3.setItsId(2002L);
    Date now = new Date();
    try {
      Long caId = 76149L;
      InvItemCategory iicCars = srvOrm.retrieveEntityById(rqVs, InvItemCategory.class, caId);
      if (iicCars == null) {
        iicCars = new InvItemCategory();
        iicCars.setItsId(caId);
        iicCars.setItsName("Cars");
        srvOrm.insertEntity(rqVs, iicCars);
      }
      srvDatabase.setIsAutocommit(false);
      for (int i =0; i < pRc; i++) {
        InvItem pbh = new InvItem();
        pbh.setItsName("pizza with bacon hot#" + i);
        pbh.setItsCategory(pbhc);
        pbh.setKnownCost(BigDecimal.ZERO);
        pbh.setIdDatabaseBirth(srvDatabase.getIdDatabase());
        pbh.setItsType(gd);
        pbh.setDefUnitOfMeasure(each);
        srvOrm.insertEntity(rqVs, pbh);
        GoodsCatalog ctPbh = new GoodsCatalog();
        ctPbh.setItem(pbh);
        ctPbh.setItsCatalog(ctpbh);
        srvOrm.insertEntity(rqVs, ctPbh);
        GoodsPlace plcPbh = new GoodsPlace();
        plcPbh.setSinceDate(now);
        plcPbh.setItem(pbh);
        plcPbh.setPickUpPlace(pzr);
        plcPbh.setItsQuantity(BigDecimal.TEN);
        srvOrm.insertEntity(rqVs, plcPbh);
        PriceGoods priPbh = new PriceGoods();
        priPbh.setItem(pbh);
        priPbh.setPriceCategory(pct);
        priPbh.setUnitOfMeasure(each);
        double pr = 7;
        if ((i + Math.round(Math.random() * 2.0)) % 2 == 0) {
          pr = pr + Math.round(Math.random()) + Math.random();
        } else {
          pr = pr - Math.round(Math.random()) + Math.random();
        }
        priPbh.setItsPrice(new BigDecimal(pr).setScale(2, RoundingMode.HALF_UP));
        priPbh.setUnStep(BigDecimal.ONE);
        srvOrm.insertEntity(rqVs, priPbh);
        GoodsSpecifics specWePbh = new GoodsSpecifics();
        specWePbh.setItem(pbh);
        specWePbh.setSpecifics(weight);
        specWePbh.setLongValue1(2L);
        specWePbh.setLongValue2(5L);
        double wei = 0.1 + Math.random();
        specWePbh.setNumericValue1(new BigDecimal(wei).setScale(2, RoundingMode.HALF_UP));
        srvOrm.insertEntity(rqVs, specWePbh);
        GoodsSpecifics specImPbh = new GoodsSpecifics();
        specImPbh.setItem(pbh);
        specImPbh.setSpecifics(img);
        if ((i + Math.round(Math.random() * 2.0)) % 2 == 0) {
          specImPbh.setStringValue1("static/uploads/merchandise.png");
        } else {
          specImPbh.setStringValue1("static/uploads/merchandise2.png");
        }
        srvOrm.insertEntity(rqVs, specImPbh);
        InvItem pch = new InvItem();
        pch.setItsName("pizza with cheese hot#" + i);
        pch.setKnownCost(BigDecimal.ZERO);
        pch.setItsCategory(pchc);
        pch.setIdDatabaseBirth(srvDatabase.getIdDatabase());
        pch.setItsType(gd);
        pch.setDefUnitOfMeasure(each);
        srvOrm.insertEntity(rqVs, pch);
        GoodsCatalog ctPch = new GoodsCatalog();
        ctPch.setItem(pch);
        ctPch.setItsCatalog(ctpch);
        srvOrm.insertEntity(rqVs, ctPch);
        GoodsPlace plcPch = new GoodsPlace();
        plcPch.setSinceDate(now);
        plcPch.setItem(pch);
        plcPch.setPickUpPlace(pzr);
        plcPch.setItsQuantity(BigDecimal.TEN);
        srvOrm.insertEntity(rqVs, plcPch);
        PriceGoods priPch = new PriceGoods();
        priPch.setItem(pch);
        priPch.setPriceCategory(pct);
        priPch.setUnitOfMeasure(each);
        pr = 8.0;
        if ((i + Math.round(Math.random() * 2.0)) % 2 == 0) {
          pr = pr + Math.round(Math.random()) + Math.random();
        } else {
          pr = pr - Math.round(Math.random()) + Math.random();
        }
        priPch.setItsPrice(new BigDecimal(pr).setScale(2, RoundingMode.HALF_UP));
        priPch.setUnStep(BigDecimal.ONE);
        srvOrm.insertEntity(rqVs, priPch);
        GoodsSpecifics specWePch = new GoodsSpecifics();
        specWePch.setItem(pch);
        specWePch.setSpecifics(weight);
        wei = 0.1 + Math.random();
        specWePch.setNumericValue1(new BigDecimal(wei).setScale(2, RoundingMode.HALF_UP));
        specWePch.setLongValue1(2L);
        specWePch.setLongValue2(5L);
        srvOrm.insertEntity(rqVs, specWePch);
        GoodsSpecifics specImPch = new GoodsSpecifics();
        specImPch.setItem(pch);
        specImPch.setSpecifics(img);
        if ((i + Math.round(Math.random())) % 2 == 0) {
          specImPch.setStringValue1("static/uploads/merchandise.png");
        } else {
          specImPch.setStringValue1("static/uploads/merchandise2.png");
        }
        srvOrm.insertEntity(rqVs, specImPch);

        InvItem hond = new InvItem();
        hond.setKnownCost(BigDecimal.ZERO);
        hond.setItsName("Honda#" + i);
        hond.setItsCategory(iicCars);
        hond.setIdDatabaseBirth(srvDatabase.getIdDatabase());
        hond.setItsType(gd);
        hond.setDefUnitOfMeasure(each);
        srvOrm.insertEntity(rqVs, hond);
        GoodsCatalog ctHond = new GoodsCatalog();
        ctHond.setItem(hond);
        ctHond.setItsCatalog(catCars);
        srvOrm.insertEntity(rqVs, ctHond);
        GoodsPlace plcHond = new GoodsPlace();
        plcHond.setSinceDate(now);
        plcHond.setItem(hond);
        plcHond.setPickUpPlace(pzr);
        plcHond.setItsQuantity(BigDecimal.ONE);
        srvOrm.insertEntity(rqVs, plcHond);
        PriceGoods priHond = new PriceGoods();
        priHond.setItem(hond);
        priHond.setPriceCategory(pct);
        priHond.setUnitOfMeasure(each);
        pr = 899.0 + (Math.random() * 1000);
        priHond.setItsPrice(new BigDecimal(pr).setScale(2, RoundingMode.HALF_UP));
        priHond.setUnStep(BigDecimal.ONE);
        srvOrm.insertEntity(rqVs, priHond);
        GoodsSpecifics specYearHond = new GoodsSpecifics();
        specYearHond.setItem(hond);
        specYearHond.setSpecifics(spYear);
        Long year = 2005L + Double.valueOf(Math.random() * 10.0).longValue();
        specYearHond.setLongValue1(year);
        srvOrm.insertEntity(rqVs, specYearHond);
        GoodsSpecifics spManufHond = new GoodsSpecifics();
        spManufHond.setItem(hond);
        spManufHond.setSpecifics(spManuf);
        spManufHond.setLongValue2(1L);
        spManufHond.setStringValue2("Manufacturer");
        spManufHond.setLongValue1(2L);
        spManufHond.setStringValue1("Honda");
        srvOrm.insertEntity(rqVs, spManufHond);
        GoodsSpecifics spClrHond = new GoodsSpecifics();
        spClrHond.setItem(hond);
        spClrHond.setSpecifics(spClr);
        spClrHond.setLongValue2(2L);
        spClrHond.setStringValue2("Color");
        if ((i + Math.round(Math.random() * 2.0)) % 2 == 0) {
          spClrHond.setLongValue1(3L);
          spClrHond.setStringValue1("Red");
        } else {
          spClrHond.setLongValue1(7L);
          spClrHond.setStringValue1("White");
        }
        srvOrm.insertEntity(rqVs, spClrHond);
        GoodsSpecifics spFuelHond = new GoodsSpecifics();
        spFuelHond.setItem(hond);
        spFuelHond.setSpecifics(spFuel);
        spFuelHond.setLongValue2(4L);
        spFuelHond.setStringValue2("Fuel");
        if ((i + Math.round(Math.random() * 2.0)) % 2 == 0) {
          spFuelHond.setLongValue1(6L);
          spFuelHond.setStringValue1("Gasoline");
        } else {
          spFuelHond.setLongValue1(10L);
          spFuelHond.setStringValue1("Diesel");
        }
        srvOrm.insertEntity(rqVs, spFuelHond);
        GoodsSpecifics spBodyHond = new GoodsSpecifics();
        spBodyHond.setItem(hond);
        spBodyHond.setSpecifics(spBody);
        spBodyHond.setLongValue2(3L);
        spBodyHond.setStringValue2("Body type");
        if ((i + Math.round(Math.random() * 2.0)) % 2 == 0) {
          spBodyHond.setLongValue1(5L);
          spBodyHond.setStringValue1("Sedan");
        } else {
          spBodyHond.setLongValue1(8L);
          spBodyHond.setStringValue1("Wagon");
        }
        srvOrm.insertEntity(rqVs, spBodyHond);
        GoodsSpecifics spTrnHond = new GoodsSpecifics();
        spTrnHond.setItem(hond);
        spTrnHond.setSpecifics(spTrn);
        spTrnHond.setLongValue2(5L);
        spTrnHond.setStringValue2("Transmission");
        if ((i + Math.round(Math.random() * 2.0)) % 2 == 0) {
          spTrnHond.setLongValue1(4L);
          spTrnHond.setStringValue1("AT");
        } else {
          spTrnHond.setLongValue1(9L);
          spTrnHond.setStringValue1("MT");
        }
        srvOrm.insertEntity(rqVs, spTrnHond);
        GoodsSpecifics specImHond = new GoodsSpecifics();
        specImHond.setItem(hond);
        specImHond.setSpecifics(img);
        if ((i + Math.round(Math.random() * 2.0)) % 2 == 0) {
          specImHond.setStringValue1("static/uploads/merchandise.png");
        } else {
          specImHond.setStringValue1("static/uploads/merchandise2.png");
        }
        srvOrm.insertEntity(rqVs, specImHond);
        GoodsSpecifics spIm1Hond = new GoodsSpecifics();
        spIm1Hond.setItem(hond);
        spIm1Hond.setSpecifics(spIm1);
        spIm1Hond.setStringValue3("scratch on rear");
        spIm1Hond.setStringValue1("static/uploads/merchandise.png");
        srvOrm.insertEntity(rqVs, spIm1Hond);
        GoodsSpecifics spIm2Hond = new GoodsSpecifics();
        spIm2Hond.setItem(hond);
        spIm2Hond.setSpecifics(spIm2);
        spIm2Hond.setStringValue3("front stain");
        spIm2Hond.setStringValue1("static/uploads/merchandise2.png");
        srvOrm.insertEntity(rqVs, spIm2Hond);
        GoodsSpecifics spIm3Hond = new GoodsSpecifics();
        spIm3Hond.setItem(hond);
        spIm3Hond.setSpecifics(spIm3);
        spIm3Hond.setStringValue3("scratch on window");
        spIm3Hond.setStringValue1("static/uploads/merchandise.png");
        srvOrm.insertEntity(rqVs, spIm3Hond);
        GoodsSpecifics spHtmlHond = new GoodsSpecifics();
        spHtmlHond.setItem(hond);
        spHtmlHond.setSpecifics(spHtml);
        spHtmlHond.setStringValue3("ru,fr");
        spHtmlHond.setStringValue1("static/uploads/1540295496778fordred.html");
        srvOrm.insertEntity(rqVs, spHtmlHond);

        InvItem ford = new InvItem();
        ford.setItsName("Ford#" + i);
        ford.setKnownCost(BigDecimal.ZERO);
        ford.setItsCategory(iicCars);
        ford.setIdDatabaseBirth(srvDatabase.getIdDatabase());
        ford.setItsType(gd);
        ford.setDefUnitOfMeasure(each);
        srvOrm.insertEntity(rqVs, ford);
        GoodsCatalog ctFord = new GoodsCatalog();
        ctFord.setItem(ford);
        ctFord.setItsCatalog(catCars);
        srvOrm.insertEntity(rqVs, ctFord);
        GoodsPlace plcFord = new GoodsPlace();
        plcFord.setSinceDate(now);
        plcFord.setItem(ford);
        plcFord.setPickUpPlace(pzr);
        plcFord.setItsQuantity(BigDecimal.ONE);
        srvOrm.insertEntity(rqVs, plcFord);
        PriceGoods priFord = new PriceGoods();
        priFord.setItem(ford);
        priFord.setPriceCategory(pct);
        priFord.setUnitOfMeasure(each);
        pr = 899.0 + (Math.random() * 1000);
        priFord.setItsPrice(new BigDecimal(pr).setScale(2, RoundingMode.HALF_UP));
        priFord.setUnStep(BigDecimal.ONE);
        srvOrm.insertEntity(rqVs, priFord);
        GoodsSpecifics specYearFord = new GoodsSpecifics();
        specYearFord.setItem(ford);
        specYearFord.setSpecifics(spYear);
        year = 2005L + Double.valueOf(Math.random() * 10.0).longValue();
        specYearFord.setLongValue1(year);
        srvOrm.insertEntity(rqVs, specYearFord);
        GoodsSpecifics spManufFord = new GoodsSpecifics();
        spManufFord.setItem(ford);
        spManufFord.setSpecifics(spManuf);
        spManufFord.setLongValue1(1L);
        spManufFord.setLongValue2(1L);
        spManufFord.setStringValue1("Ford");
        spManufFord.setStringValue2("Manufacturer");
        srvOrm.insertEntity(rqVs, spManufFord);
        GoodsSpecifics spClrFord = new GoodsSpecifics();
        spClrFord.setItem(ford);
        spClrFord.setSpecifics(spClr);
        spClrFord.setLongValue2(2L);
        spClrFord.setStringValue2("Color");
        if ((i + Math.round(Math.random() * 2.0)) % 2 == 0) {
          spClrFord.setLongValue1(3L);
          spClrFord.setStringValue1("Red");
        } else {
          spClrFord.setLongValue1(7L);
          spClrFord.setStringValue1("White");
        }
        srvOrm.insertEntity(rqVs, spClrFord);
        GoodsSpecifics spFuelFord = new GoodsSpecifics();
        spFuelFord.setItem(ford);
        spFuelFord.setSpecifics(spFuel);
        spFuelFord.setLongValue2(4L);
        spFuelFord.setStringValue2("Fuel");
        if ((i + Math.round(Math.random() * 2.0)) % 2 == 0) {
          spFuelFord.setLongValue1(6L);
          spFuelFord.setStringValue1("Gasoline");
        } else {
          spFuelFord.setLongValue1(10L);
          spFuelFord.setStringValue1("Diesel");
        }
        srvOrm.insertEntity(rqVs, spFuelFord);
        GoodsSpecifics spBodyFord = new GoodsSpecifics();
        spBodyFord.setItem(ford);
        spBodyFord.setSpecifics(spBody);
        spBodyFord.setLongValue2(3L);
        spBodyFord.setStringValue2("Body type");
        if ((i + Math.round(Math.random() * 2.0)) % 2 == 0) {
          spBodyFord.setLongValue1(5L);
          spBodyFord.setStringValue1("Sedan");
        } else {
          spBodyFord.setLongValue1(8L);
          spBodyFord.setStringValue1("Wagon");
        }
        srvOrm.insertEntity(rqVs, spBodyFord);
        GoodsSpecifics spTrnFord = new GoodsSpecifics();
        spTrnFord.setItem(ford);
        spTrnFord.setSpecifics(spTrn);
        spTrnFord.setLongValue2(5L);
        spTrnFord.setStringValue2("Transmission");
        if ((i + Math.round(Math.random() * 2.0)) % 2 == 0) {
          spTrnFord.setLongValue1(4L);
          spTrnFord.setStringValue1("AT");
        } else {
          spTrnFord.setLongValue1(9L);
          spTrnFord.setStringValue1("MT");
        }
        srvOrm.insertEntity(rqVs, spTrnFord);
        GoodsSpecifics specImFord = new GoodsSpecifics();
        specImFord.setItem(ford);
        specImFord.setSpecifics(img);
        if ((i + Math.round(Math.random() * 2.0)) % 2 == 0) {
          specImFord.setStringValue1("static/uploads/merchandise.png");
        } else {
          specImFord.setStringValue1("static/uploads/merchandise2.png");
        }
        srvOrm.insertEntity(rqVs, specImFord);
        GoodsSpecifics spIm1Ford = new GoodsSpecifics();
        spIm1Ford.setItem(ford);
        spIm1Ford.setSpecifics(spIm1);
        spIm1Ford.setStringValue3("scratch on rear");
        spIm1Ford.setStringValue1("static/uploads/merchandise.png");
        srvOrm.insertEntity(rqVs, spIm1Ford);
        GoodsSpecifics spIm2Ford = new GoodsSpecifics();
        spIm2Ford.setItem(ford);
        spIm2Ford.setSpecifics(spIm2);
        spIm2Ford.setStringValue3("front stain");
        spIm2Ford.setStringValue1("static/uploads/merchandise2.png");
        srvOrm.insertEntity(rqVs, spIm2Ford);
        GoodsSpecifics spIm3Ford = new GoodsSpecifics();
        spIm3Ford.setItem(ford);
        spIm3Ford.setSpecifics(spIm3);
        spIm3Ford.setStringValue3("scratch on window");
        spIm3Ford.setStringValue1("static/uploads/merchandise.png");
        srvOrm.insertEntity(rqVs, spIm3Ford);
        GoodsSpecifics spHtmlFord = new GoodsSpecifics();
        spHtmlFord.setItem(ford);
        spHtmlFord.setSpecifics(spHtml);
        spHtmlFord.setStringValue3("ru,fr");
        spHtmlFord.setStringValue1("static/uploads/1540295496778fordred.html");
        srvOrm.insertEntity(rqVs, spHtmlFord);

      }
      srvDatabase.commitTransaction();
    } catch (Exception ex) {
      ex.printStackTrace();
      if (!srvDatabase.getIsAutocommit()) {
        srvDatabase.rollBackTransaction();
      }
      throw new Exception(ex);
    } finally {
      srvDatabase.releaseResources();
      ds.close();
    }
  }
}
