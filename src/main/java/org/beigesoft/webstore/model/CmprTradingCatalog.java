package org.beigesoft.webstore.model;

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

import java.util.Comparator;
import java.io.Serializable;

/**
 * <p>Comparator of TradingCatalog by Index.</p>
 *
 * @author Yury Demidenko
 */
public class CmprTradingCatalog
  implements Comparator<TradingCatalog>, Serializable {

  /**
   * <p>serialVersionUID.</p>
   **/
  static final long serialVersionUID = 49731247829112L;

  @Override
  public final int compare(final TradingCatalog o1,
          final TradingCatalog o2) {
    return o1.getCatalog().getItsIndex()
      .compareTo(o2.getCatalog().getItsIndex());
  }
}
