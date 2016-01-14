package morbrian.websockets.persistence;

import morbrian.websockets.model.ForumEntity;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.security.Principal;

/**
 * Created by morbrian on 6/17/15.
 */
@Stateless public class Persistence {
  @Inject private Logger log;
  @Inject private Principal principal;
  @Inject private EntityManager em;

  public ForumEntity createForum(ForumEntity entity) {
    entity.setCreatedByUid(principal.getName());
    entity.setModifiedByUid(principal.getName());
    em.persist(entity);
    return entity;
  }

  public ForumEntity updateForum(ForumEntity entity) {
    entity.setCreatedByUid(principal.getName());
    entity.setModifiedByUid(principal.getName());
    em.merge(entity);
    return entity;
  }

  public void removeForum(ForumEntity entity) {
    em.remove(em.find(ForumEntity.class, entity.getId()));
  }


}
