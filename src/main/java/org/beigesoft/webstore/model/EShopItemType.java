package org.beigesoft.webstore.model;

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

/**
 * <pre>
 * Shop item type.
 * </pre>
 *
 * @author Yury Demidenko
 */
public enum EShopItemType {

  /**
   * <p>0, Goods that stored in shop owner's warehouse, they are usually
   * belongs to the owner. Owner can rent his warehouse
   * to store S.E. seller's goods.
   * It's org.beigesoft.accounting.persistable.InvItem.</p>
   **/
  GOODS,

  /**
   * <p>1, Service that performed by shop owner.
   * It's org.beigesoft.accounting.persistable.ServiceToSale.</p>
   **/
  SERVICE,

  /**
   * <p>2, S.E. Goods that located in S.E. seller warehouse.
   * It's org.beigesoft.webstore.persistable.SeGoods.</p>
   **/
  SEGOODS,

  /**
   * <p>3, S.E.Service that performed by S.E. Seller.
   * It's org.beigesoft.webstore.persistable.SeService</p>
   **/
  SESERVICE;
}
