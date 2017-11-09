package org.beigesoft.webstore.persistable;

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

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;

/**
 * <pre>
 * Model of Advised Goods For Goods.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class AdvisedGoodsForGoods extends AEditableHasVersion
  implements IHasId<AdvisedGoodsForGoodsId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private AdvisedGoodsForGoodsId itsId = new AdvisedGoodsForGoodsId();

  /**
   * <p>Not null, for forAdviseCategory advise category,
   * e.g. "smartphones 5inch 2*3".</p>
   **/
  private AdviseCategoryOfGs forAdviseCategory;

  /**
   * <p>Not null, forAdviseCategory advised category,
   * e.g. "covers for smartphones 5inch 2*3".</p>
   **/
  private AdviseCategoryOfGs advisedCategory;

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final AdvisedGoodsForGoodsId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pId model ID
   **/
  @Override
  public final void setItsId(final AdvisedGoodsForGoodsId pItsId) {
    this.itsId = pItsId;
    if (this.itsId != null) {
      this.advisedCategory = this.itsId.getAdvisedCategory();
      this.forAdviseCategory = this.itsId.getForAdviseCategory();
    } else {
      this.advisedCategory = null;
      this.forAdviseCategory = null;
    }
  }

  /**
   * <p>Setter for forAdviseCategory.</p>
   * @param pForAdviseCategory reference
   **/
  public final void setForAdviseCategory(
    final AdviseCategoryOfGs pForAdviseCategory) {
    this.forAdviseCategory = pForAdviseCategory;
    if (this.itsId == null) {
      this.itsId = new AdvisedGoodsForGoodsId();
    }
    this.itsId.setForAdviseCategory(this.forAdviseCategory);
  }

  /**
   * <p>Setter for advisedCategory.</p>
   * @param pAdvisedCategory reference
   **/
  public final void setAdvisedCategory(
    final AdviseCategoryOfGs pAdvisedCategory) {
    this.advisedCategory = pAdvisedCategory;
    if (this.itsId == null) {
      this.itsId = new AdvisedGoodsForGoodsId();
    }
    this.itsId.setAdvisedCategory(this.advisedCategory);
  }

  //Simple getters and setters:
  /**
   * <p>Getter for forAdviseCategory.</p>
   * @return AdviseCategoryOfGs
   **/
  public final AdviseCategoryOfGs getForAdviseCategory() {
    return this.forAdviseCategory;
  }

  /**
   * <p>Getter for advisedCategory.</p>
   * @return AdviseCategoryOfGs
   **/
  public final AdviseCategoryOfGs getAdvisedCategory() {
    return this.advisedCategory;
  }
}
