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
 * Payment methods.
 * </pre>
 *
 * @author Yury Demidenko
 */
public enum EPaymentMethod {

  /**
   * <p>0, buyer must pay right now with any online method
   * (e.g. credit card, PayPal).</p>
   **/
  ONLINE,

  /**
   * <p>1, any, it's means that buyer can pay 100% with any method
   * (e.g. with cash when goods has been delivered),
   * and if order can be payed partially with several methods,
   * e.g. 50% online, 50% bank transfer, cash or cheque.</p>
   **/
  ANY,

  /**
   * <p>2, cash.</p>
   **/
  CASH,

  /**
   * <p>3, bank transfer.</p>
   **/
  BANK_TRANSFER,

  /**
   * <p>4, bank cheque.</p>
   **/
  BANK_CHEQUE,

  /**
   * <p>5, cash or bank transfer.</p>
   **/
  CASH_BANK_TRANSFER,

  /**
   * <p>6, bank transfer or cheque.</p>
   **/
  BANK_TRANSFER_CHEQUE,

  /**
   * <p>7, cash, bank transfer or cheque.</p>
   **/
  CASH_BANK_TRANSFER_CHEQUE,

  /**
   * <p>8, in case when order must be payed partially online
   * e.g 50% and the rest with any methods - online, bank transfer,
   * cash or check.</p>
   **/
  PARTIAL_ONLINE,

  /**
   * <p>9, exactly PayPal.</p>
   **/
  PAYPAL,

  /**
   * <p>10, PayPal or credit card (or any other method) through PayPal.</p>
   **/
  PAYPAL_ANY;
}
