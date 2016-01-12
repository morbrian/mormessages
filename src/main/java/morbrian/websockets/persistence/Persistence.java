package morbrian.websockets.persistence;

import morbrian.websockets.model.ForumEntity;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Created by morbrian on 6/17/15.
 */
@Stateless public class Persistence {
  @Inject private Logger log;

  @Inject private EntityManager em;

  public ForumEntity createForum(ForumEntity entity) {
    entity.setCreatedByUid("SYSTEM");
    entity.setModifiedByUid("SYSTEM");
    em.persist(entity);
    return entity;
  }

  public ForumEntity updateForum(ForumEntity entity) {
    entity.setCreatedByUid("SYSTEM");
    entity.setModifiedByUid("SYSTEM");
    em.merge(entity);
    return entity;
  }

}
