package org.beigesoft.webstore.factory;

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

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import org.beigesoft.log.ILogger;
import org.beigesoft.model.IHasIdLongVersion;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.settings.IMngSettings;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.persistable.MatchForeign;
import org.beigesoft.persistable.MatchForeignLine;
import org.beigesoft.persistable.CsvMethod;
import org.beigesoft.persistable.CsvColumn;
import org.beigesoft.persistable.Languages;
import org.beigesoft.persistable.Countries;
import org.beigesoft.persistable.DecimalSeparator;
import org.beigesoft.persistable.DecimalGroupSeparator;
import org.beigesoft.persistable.EmailConnect;
import org.beigesoft.persistable.EmailStringProperty;
import org.beigesoft.persistable.EmailIntegerProperty;
import org.beigesoft.persistable.EmailMsg;
import org.beigesoft.persistable.Eattachment;
import org.beigesoft.persistable.Erecipient;
import org.beigesoft.persistable.UserTomcat;
import org.beigesoft.persistable.UserRoleTomcat;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.I18nInvItem;
import org.beigesoft.accounting.persistable.DebtorCreditor;
import org.beigesoft.accounting.persistable.ServiceToSale;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.UnitOfMeasure;
import org.beigesoft.orm.factory.FctBnEntitiesProcessors;
import org.beigesoft.orm.processor.PrcEntityRetrieve;
import org.beigesoft.webstore.service.ISrvSettingsAdd;
import org.beigesoft.webstore.service.ISrvTradingSettings;
import org.beigesoft.webstore.service.IFindSeSeller;
import org.beigesoft.webstore.persistable.base.AItemSpecifics;
import org.beigesoft.webstore.persistable.base.AItemSpecificsId;
import org.beigesoft.webstore.persistable.base.AItemCatalogId;
import org.beigesoft.webstore.persistable.AdviseCategoryOfGs;
import org.beigesoft.webstore.persistable.AdvisedGoodsForGoods;
import org.beigesoft.webstore.persistable.BuyerPriceCategory;
import org.beigesoft.webstore.persistable.CartItem;
import org.beigesoft.webstore.persistable.CartTaxLine;
import org.beigesoft.webstore.persistable.CatalogGs;
import org.beigesoft.webstore.persistable.CatalogSpecifics;
import org.beigesoft.webstore.persistable.ChooseableSpecifics;
import org.beigesoft.webstore.persistable.ChooseableSpecificsType;
import org.beigesoft.webstore.persistable.CustomerGoodsSeen;
import org.beigesoft.webstore.persistable.CustomerOrder;
import org.beigesoft.webstore.persistable.CustomerOrderGoods;
import org.beigesoft.webstore.persistable.CustomerOrderSeGoods;
import org.beigesoft.webstore.persistable.CustomerOrderService;
import org.beigesoft.webstore.persistable.CustomerOrderSeService;
import org.beigesoft.webstore.persistable.CustomerOrderTaxLine;
import org.beigesoft.webstore.persistable.GoodsAdviseCategories;
import org.beigesoft.webstore.persistable.GoodsCatalog;
import org.beigesoft.webstore.persistable.GoodsPlace;
import org.beigesoft.webstore.persistable.GoodsRating;
import org.beigesoft.webstore.persistable.GoodsSpecifics;
import org.beigesoft.webstore.persistable.HtmlTemplate;
import org.beigesoft.webstore.persistable.I18nCatalogGs;
import org.beigesoft.webstore.persistable.I18nChooseableSpecifics;
import org.beigesoft.webstore.persistable.I18nSpecificsOfItem;
import org.beigesoft.webstore.persistable.I18nSpecificsOfItemGroup;
import org.beigesoft.webstore.persistable.I18nWebStore;
import org.beigesoft.webstore.persistable.OnlineBuyer;
import org.beigesoft.webstore.persistable.PickUpPlace;
import org.beigesoft.webstore.persistable.PriceCategory;
import org.beigesoft.webstore.persistable.PriceCategoryOfBuyers;
import org.beigesoft.webstore.persistable.PriceCategoryOfItems;
import org.beigesoft.webstore.persistable.PriceGoods;
import org.beigesoft.webstore.persistable.ServiceCatalog;
import org.beigesoft.webstore.persistable.ServicePlace;
import org.beigesoft.webstore.persistable.ServicePrice;
import org.beigesoft.webstore.persistable.ServiceSpecifics;
import org.beigesoft.webstore.persistable.SeSeller;
import org.beigesoft.webstore.persistable.SettingsAdd;
import org.beigesoft.webstore.persistable.ShoppingCart;
import org.beigesoft.webstore.persistable.SpecificsOfItem;
import org.beigesoft.webstore.persistable.SpecificsOfItemGroup;
import org.beigesoft.webstore.persistable.SubcatalogsCatalogsGs;
import org.beigesoft.webstore.persistable.TradingSettings;
import org.beigesoft.webstore.persistable.SeGoodCatalog;
import org.beigesoft.webstore.processor.PrcSeSellerDel;
import org.beigesoft.webstore.processor.PrcSeSellerSave;
import org.beigesoft.webstore.processor.PrcAdvisedGoodsForGoodsSave;
import org.beigesoft.webstore.processor.PrcItemCatalogSave;
import org.beigesoft.webstore.processor.PrcSettingsAddSave;
import org.beigesoft.webstore.processor.PrcTradingSettingsSave;
import org.beigesoft.webstore.processor.PrcSubcatalogsCatalogsGsSave;
import org.beigesoft.webstore.processor.PrcGoodsAdviseCategoriesSave;
import org.beigesoft.webstore.processor.PrcItemSpecificsSave;
import org.beigesoft.webstore.processor.PrcItemSpecificsRetrieve;
import org.beigesoft.webstore.processor.PrcItSpecEmbFlSave;
import org.beigesoft.webstore.processor.PrcItSpecEmbFlDel;

/**
 * <p>Webstore entities processors factory.
 * These are non-public processors.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class FctBnTradeEntitiesProcessors<RS> implements IFactoryAppBeansByName<IEntityProcessor> {

  /**
   * <p>Factory non-acc entity processors.
   * Concrete factory for concrete bean name that is bean class
   * simple name. Any way any such factory must be no abstract.</p>
   **/
  private FctBnEntitiesProcessors<RS> fctBnEntitiesProcessors;

  /**
   * <p>Manager UVD settings.</p>
   **/
  private IMngSettings mngUvdSettings;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for additional settings.</p>
   **/
  private ISrvSettingsAdd srvSettingsAdd;

  /**
   * <p>Business service for trading settings.</p>
   **/
  private ISrvTradingSettings srvTradingSettings;

  /**
   * <p>Upload directory relative to WEB-APP path
   * without start and end separator, e.g. "static/uploads".</p>
   **/
  private String uploadDirectory;

  /**
   * <p>Full WEB-APP path without end separator,
   * revealed from servlet context and used for upload files.</p>
   **/
  private String webAppPath;

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>Find S.E.Seller service.</p>
   **/
  private IFindSeSeller findSeSeller;

  /**
   * <p>Converters map "converter name"-"object' s converter".</p>
   **/
  private final Map<String, IEntityProcessor> processorsMap = new HashMap<String, IEntityProcessor>();

  /**
   * <p>Web-store entities.</p>
   **/
  private final Set<Class<?>> wsEntities;

  /**
   * <p>Shared entities.</p>
   **/
  private final Set<Class<?>> sharedEntities;

  /**
   * <p>Only constructor. S.E. entities (shared) will be added
   * by Mather's factory.</p>
   **/
  public FctBnTradeEntitiesProcessors() {
    this.sharedEntities = new HashSet<Class<?>>();
    this.sharedEntities.add(MatchForeign.class);
    this.sharedEntities.add(MatchForeignLine.class);
    this.sharedEntities.add(CsvMethod.class);
    this.sharedEntities.add(CsvColumn.class);
    this.sharedEntities.add(Languages.class);
    this.sharedEntities.add(Countries.class);
    this.sharedEntities.add(DecimalSeparator.class);
    this.sharedEntities.add(DecimalGroupSeparator.class);
    this.sharedEntities.add(InvItem.class);
    this.sharedEntities.add(I18nInvItem.class);
    this.sharedEntities.add(DebtorCreditor.class);
    this.sharedEntities.add(ServiceToSale.class);
    this.sharedEntities.add(InvItemTaxCategory.class);
    this.sharedEntities.add(UnitOfMeasure.class);
    this.wsEntities = new HashSet<Class<?>>();
    this.wsEntities.add(UserTomcat.class);
    this.wsEntities.add(UserRoleTomcat.class);
    this.wsEntities.add(AdviseCategoryOfGs.class);
    this.wsEntities.add(AdvisedGoodsForGoods.class);
    this.wsEntities.add(BuyerPriceCategory.class);
    this.wsEntities.add(CartItem.class);
    this.wsEntities.add(CartTaxLine.class);
    this.wsEntities.add(CatalogGs.class);
    this.wsEntities.add(CatalogSpecifics.class);
    this.wsEntities.add(ChooseableSpecifics.class);
    this.wsEntities.add(ChooseableSpecificsType.class);
    this.wsEntities.add(CustomerGoodsSeen.class);
    this.wsEntities.add(CustomerOrder.class);
    this.wsEntities.add(CustomerOrderGoods.class);
    this.wsEntities.add(CustomerOrderSeGoods.class);
    this.wsEntities.add(CustomerOrderService.class);
    this.wsEntities.add(CustomerOrderSeService.class);
    this.wsEntities.add(CustomerOrderTaxLine.class);
    this.wsEntities.add(GoodsAdviseCategories.class);
    this.wsEntities.add(GoodsCatalog.class);
    this.wsEntities.add(GoodsPlace.class);
    this.wsEntities.add(GoodsRating.class);
    this.wsEntities.add(GoodsSpecifics.class);
    this.wsEntities.add(HtmlTemplate.class);
    this.wsEntities.add(I18nCatalogGs.class);
    this.wsEntities.add(I18nChooseableSpecifics.class);
    this.wsEntities.add(I18nSpecificsOfItem.class);
    this.wsEntities.add(I18nSpecificsOfItemGroup.class);
    this.wsEntities.add(I18nWebStore.class);
    this.wsEntities.add(OnlineBuyer.class);
    this.wsEntities.add(PickUpPlace.class);
    this.wsEntities.add(PriceCategory.class);
    this.wsEntities.add(PriceCategoryOfBuyers.class);
    this.wsEntities.add(PriceCategoryOfItems.class);
    this.wsEntities.add(PriceGoods.class);
    this.wsEntities.add(ServiceCatalog.class);
    this.wsEntities.add(ServicePlace.class);
    this.wsEntities.add(ServicePrice.class);
    this.wsEntities.add(ServiceSpecifics.class);
    this.wsEntities.add(SeSeller.class);
    this.wsEntities.add(SettingsAdd.class);
    this.wsEntities.add(ShoppingCart.class);
    this.wsEntities.add(SpecificsOfItem.class);
    this.wsEntities.add(SpecificsOfItemGroup.class);
    this.wsEntities.add(SubcatalogsCatalogsGs.class);
    this.wsEntities.add(TradingSettings.class);
    this.wsEntities.add(EmailConnect.class);
    this.wsEntities.add(EmailStringProperty.class);
    this.wsEntities.add(EmailIntegerProperty.class);
    this.wsEntities.add(EmailMsg.class);
    this.wsEntities.add(Eattachment.class);
    this.wsEntities.add(Erecipient.class);
    this.wsEntities.add(SeGoodCatalog.class);
  }

  /**
   * <p>Get bean in lazy mode (if bean is null then initialize it).</p>
   * @param pAddParam additional param
   * @param pBeanName - bean name
   * @return requested bean
   * @throws Exception - an exception
   */
  @Override
  public final IEntityProcessor lazyGet(final Map<String, Object> pAddParam, final String pBeanName) throws Exception {
    IEntityProcessor proc = this.processorsMap.get(pBeanName);
    if (proc == null) {
      // locking:
      synchronized (this.processorsMap) {
        // make sure again whether it's null after locking:
        proc = this.processorsMap.get(pBeanName);
        if (proc == null) {
          if (pBeanName.equals(PrcAdvisedGoodsForGoodsSave.class.getSimpleName())) {
            proc = lazyGetPrcAdvisedGoodsForGoodsSave(pAddParam);
          } else if (pBeanName.equals(PrcSettingsAddSave.class.getSimpleName())) {
            proc = lazyGetPrcSettingsAddSave(pAddParam);
          } else if (pBeanName.equals(PrcTradingSettingsSave.class.getSimpleName())) {
            proc = lazyGetPrcTradingSettingsSave(pAddParam);
          } else if (pBeanName.equals(PrcItemCatalogSave.class.getSimpleName())) {
            proc = lazyGetPrcItemCatalogSave(pAddParam);
          } else if (pBeanName.equals(PrcSubcatalogsCatalogsGsSave.class.getSimpleName())) {
            proc = lazyGetPrcSubcatalogsCatalogsGsSave(pAddParam);
          } else if (pBeanName.equals(PrcSeSellerDel.class.getSimpleName())) {
            proc = lazyGetPrcSeSellerDel(pAddParam);
          } else if (pBeanName.equals(PrcSeSellerSave.class.getSimpleName())) {
            proc = lazyGetPrcSeSellerSave(pAddParam);
          } else if (pBeanName.equals(PrcGoodsAdviseCategoriesSave.class.getSimpleName())) {
            proc = lazyGetPrcGoodsAdviseCategoriesSave(pAddParam);
          } else if (pBeanName.equals(PrcItemSpecificsRetrieve.class.getSimpleName())) {
            proc = lazyGetPrcItemSpecificsRetrieve(pAddParam);
          } else if (pBeanName.equals(PrcItemSpecificsSave.class.getSimpleName())) {
            proc = lazyGetPrcItemSpecificsSave(pAddParam);
          } else if (pBeanName.equals(PrcItSpecEmbFlSave.class.getSimpleName())) {
            proc = lazyGetPrcItSpecEmbFlSave(pAddParam);
          } else if (pBeanName.equals(PrcItSpecEmbFlDel.class.getSimpleName())) {
            proc = lazyGetPrcItSpecEmbFlDel(pAddParam);
          } else {
            proc = this.fctBnEntitiesProcessors.lazyGet(pAddParam, pBeanName);
          }
        }
      }
    }
    if (proc == null) {
      this.logger.info(null, FctBnTradeEntitiesProcessors.class, pBeanName + " not found!");
    }
    return proc;
  }

  /**
   * <p>Set bean.</p>
   * @param pBeanName - bean name
   * @param pBean bean
   * @throws Exception - an exception
   */
  @Override
  public final void set(final String pBeanName,
    final IEntityProcessor pBean) throws Exception {
    throw new Exception("Setting is not allowed!");
  }

  /**
   * <p>Get PrcAdvisedGoodsForGoodsSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAdvisedGoodsForGoodsSave
   * @throws Exception - an exception
   */
  protected final PrcAdvisedGoodsForGoodsSave<RS> lazyGetPrcAdvisedGoodsForGoodsSave(final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcAdvisedGoodsForGoodsSave.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcAdvisedGoodsForGoodsSave<RS> proc = (PrcAdvisedGoodsForGoodsSave<RS>) this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcAdvisedGoodsForGoodsSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeEntitiesProcessors.class, beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcSettingsAddSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSettingsAddSave
   * @throws Exception - an exception
   */
  protected final PrcSettingsAddSave<RS> lazyGetPrcSettingsAddSave(final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcSettingsAddSave.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcSettingsAddSave<RS> proc = (PrcSettingsAddSave<RS>) this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcSettingsAddSave<RS>();
      proc.setSrvSettingsAdd(getSrvSettingsAdd());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeEntitiesProcessors.class, beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcTradingSettingsSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcTradingSettingsSave
   * @throws Exception - an exception
   */
  protected final PrcTradingSettingsSave<RS> lazyGetPrcTradingSettingsSave(final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcTradingSettingsSave.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcTradingSettingsSave<RS> proc = (PrcTradingSettingsSave<RS>) this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcTradingSettingsSave<RS>();
      proc.setSrvTradingSettings(getSrvTradingSettings());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeEntitiesProcessors.class, beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcItemCatalogSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcItemCatalogSave
   * @throws Exception - an exception
   */
  protected final PrcItemCatalogSave<RS, IHasIdLongVersion, AItemCatalogId<IHasIdLongVersion>>
    lazyGetPrcItemCatalogSave(final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcItemCatalogSave.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcItemCatalogSave<RS, IHasIdLongVersion, AItemCatalogId<IHasIdLongVersion>> proc =
      (PrcItemCatalogSave<RS, IHasIdLongVersion, AItemCatalogId<IHasIdLongVersion>>)
        this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcItemCatalogSave<RS, IHasIdLongVersion, AItemCatalogId<IHasIdLongVersion>>();
      proc.setSrvOrm(getSrvOrm());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeEntitiesProcessors.class, beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcSubcatalogsCatalogsGsSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSubcatalogsCatalogsGsSave
   * @throws Exception - an exception
   */
  protected final PrcSubcatalogsCatalogsGsSave<RS> lazyGetPrcSubcatalogsCatalogsGsSave(final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcSubcatalogsCatalogsGsSave.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcSubcatalogsCatalogsGsSave<RS> proc = (PrcSubcatalogsCatalogsGsSave<RS>) this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcSubcatalogsCatalogsGsSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeEntitiesProcessors.class, beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcSeSellerDel (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSeSellerDel
   * @throws Exception - an exception
   */
  protected final PrcSeSellerDel<RS> lazyGetPrcSeSellerDel(final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcSeSellerDel.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcSeSellerDel<RS> proc = (PrcSeSellerDel<RS>) this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcSeSellerDel<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setFindSeSeller(getFindSeSeller());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeEntitiesProcessors.class, beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcSeSellerSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSeSellerSave
   * @throws Exception - an exception
   */
  protected final PrcSeSellerSave<RS> lazyGetPrcSeSellerSave(final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcSeSellerSave.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcSeSellerSave<RS> proc = (PrcSeSellerSave<RS>) this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcSeSellerSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setFindSeSeller(getFindSeSeller());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeEntitiesProcessors.class, beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcGoodsAdviseCategoriesSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcGoodsAdviseCategoriesSave
   * @throws Exception - an exception
   */
  protected final PrcGoodsAdviseCategoriesSave<RS> lazyGetPrcGoodsAdviseCategoriesSave(final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcGoodsAdviseCategoriesSave.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcGoodsAdviseCategoriesSave<RS> proc = (PrcGoodsAdviseCategoriesSave<RS>) this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcGoodsAdviseCategoriesSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeEntitiesProcessors.class, beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcItemSpecificsRetrieve (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcItemSpecificsRetrieve
   * @throws Exception - an exception
   */
  protected final PrcItemSpecificsRetrieve<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>>
      lazyGetPrcItemSpecificsRetrieve(final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcItemSpecificsRetrieve.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcItemSpecificsRetrieve<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>> proc =
      (PrcItemSpecificsRetrieve<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>>)
        this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcItemSpecificsRetrieve<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>>();
      @SuppressWarnings("unchecked")
      PrcEntityRetrieve<RS, AItemSpecifics<IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>>, AItemSpecificsId<IHasIdLongVersion>>
        procDlg = (PrcEntityRetrieve<RS, AItemSpecifics<IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>>, AItemSpecificsId<IHasIdLongVersion>>)
          this.fctBnEntitiesProcessors.lazyGet(pAddParam, PrcEntityRetrieve.class.getSimpleName());
      proc.setPrcEntityRetrieve(procDlg);
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeEntitiesProcessors.class, beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcItSpecEmbFlDel (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcItSpecEmbFlDel
   * @throws Exception - an exception
   */
  protected final PrcItSpecEmbFlDel<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>>
    lazyGetPrcItSpecEmbFlDel(final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcItSpecEmbFlDel.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcItSpecEmbFlDel<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>> proc =
      (PrcItSpecEmbFlDel<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>>)
        this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcItSpecEmbFlDel<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>>();
      proc.setSrvOrm(getSrvOrm());
      proc.setUploadDirectory(getUploadDirectory());
      proc.setWebAppPath(getWebAppPath());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeEntitiesProcessors.class, beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcItSpecEmbFlSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcItSpecEmbFlSave
   * @throws Exception - an exception
   */
  protected final PrcItSpecEmbFlSave<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>>
    lazyGetPrcItSpecEmbFlSave(final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcItSpecEmbFlSave.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcItSpecEmbFlSave<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>> proc =
      (PrcItSpecEmbFlSave<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>>)
        this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcItSpecEmbFlSave<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>>();
      proc.setSrvOrm(getSrvOrm());
      proc.setUploadDirectory(getUploadDirectory());
      proc.setWebAppPath(getWebAppPath());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeEntitiesProcessors.class, beanName + " has been created.");
    }
    return proc;
  }

  /**
   * <p>Get PrcItemSpecificsSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcItemSpecificsSave
   * @throws Exception - an exception
   */
  protected final PrcItemSpecificsSave<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>>
    lazyGetPrcItemSpecificsSave(final Map<String, Object> pAddParam) throws Exception {
    String beanName = PrcItemSpecificsSave.class.getSimpleName();
    @SuppressWarnings("unchecked")
    PrcItemSpecificsSave<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>> proc =
      (PrcItemSpecificsSave<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>>)
        this.processorsMap.get(beanName);
    if (proc == null) {
      proc = new PrcItemSpecificsSave<RS, IHasIdLongVersion, AItemSpecificsId<IHasIdLongVersion>>();
      proc.setSrvOrm(getSrvOrm());
      //assigning fully initialized object:
      this.processorsMap.put(beanName, proc);
      this.logger.info(null, FctBnTradeEntitiesProcessors.class, beanName + " has been created.");
    }
    return proc;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for fctBnEntitiesProcessors.</p>
   * @return FctBnEntitiesProcessors<RS>
   **/
  public final FctBnEntitiesProcessors<RS> getFctBnEntitiesProcessors() {
    return this.fctBnEntitiesProcessors;
  }

  /**
   * <p>Setter for fctBnEntitiesProcessors.</p>
   * @param pFctBnEntitiesProcessors reference
   **/
  public final void setFctBnEntitiesProcessors(
    final FctBnEntitiesProcessors<RS> pFctBnEntitiesProcessors) {
    this.fctBnEntitiesProcessors = pFctBnEntitiesProcessors;
  }

  /**
   * <p>Getter for srvOrm.</p>
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
   * <p>Getter for srvSettingsAdd.</p>
   * @return ISrvSettingsAdd
   **/
  public final ISrvSettingsAdd getSrvSettingsAdd() {
    return this.srvSettingsAdd;
  }

  /**
   * <p>Setter for srvSettingsAdd.</p>
   * @param pSrvSettingsAdd reference
   **/
  public final void setSrvSettingsAdd(final ISrvSettingsAdd pSrvSettingsAdd) {
    this.srvSettingsAdd = pSrvSettingsAdd;
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
   * <p>Getter for uploadDirectory.</p>
   * @return String
   **/
  public final String getUploadDirectory() {
    return this.uploadDirectory;
  }

  /**
   * <p>Setter for uploadDirectory.</p>
   * @param pUploadDirectory reference
   **/
  public final void setUploadDirectory(final String pUploadDirectory) {
    this.uploadDirectory = pUploadDirectory;
  }

  /**
   * <p>Getter for webAppPath.</p>
   * @return String
   **/
  public final String getWebAppPath() {
    return this.webAppPath;
  }

  /**
   * <p>Setter for webAppPath.</p>
   * @param pWebAppPath reference
   **/
  public final void setWebAppPath(final String pWebAppPath) {
    this.webAppPath = pWebAppPath;
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
   * <p>Getter for wsEntities.</p>
   * @return final Set<Class<?>>
   **/
  public final Set<Class<?>> getWsEntities() {
    return this.wsEntities;
  }


  /**
   * <p>Getter for sharedEntities.</p>
   * @return final Set<Class<?>>
   **/
  public final Set<Class<?>> getSharedEntities() {
    return this.sharedEntities;
  }

  /**
   * <p>Getter for findSeSeller.</p>
   * @return IFindSeSeller<RS>
   **/
  public final IFindSeSeller getFindSeSeller() {
    return this.findSeSeller;
  }

  /**
   * <p>Setter for findSeSeller.</p>
   * @param pFindSeSeller reference
   **/
  public final void setFindSeSeller(final IFindSeSeller pFindSeSeller) {
    this.findSeSeller = pFindSeSeller;
  }
}
