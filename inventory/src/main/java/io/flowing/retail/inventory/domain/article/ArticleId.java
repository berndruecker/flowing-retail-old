package io.flowing.retail.inventory.domain.article;

import io.flowing.retail.domain.Identity;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ArticleId extends Identity<String> {

  public ArticleId(String id) {
    super(id);
  }

}
