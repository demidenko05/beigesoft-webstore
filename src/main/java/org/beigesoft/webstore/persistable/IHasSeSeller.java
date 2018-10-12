package org.beigesoft.webstore.persistable;

/*
 * Copyright (c) 2018 Beigesoft™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import org.beigesoft.model.IHasId;

/**
 * <p>Abstraction of S.E.Seller's data model.</p>
 *
 * @param <ID> type of ID
 * @author Yury Demidenko
 */
public interface IHasSeSeller<ID> extends IHasId<ID> {

  /**
   * <p>Getter for seller.</p>
   * @return SeSeller
   **/
  SeSeller getSeller();

  /**
   * <p>Setter for seller.</p>
   * @param pSeller reference
   **/
  void setSeller(SeSeller pSeller);
}
