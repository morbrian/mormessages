package morbrian.mormessages.persistence;

import morbrian.mormessages.model.BaseEntity;
import morbrian.mormessages.model.ForumEntity;
import morbrian.mormessages.model.MessageEntity;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import java.security.Principal;

@Stateless public class Persistence {
  @Inject private Logger logger;
  @Inject private Repository repository;
  @Inject private Principal principal;
  @Inject private EntityManager em;

  public BaseEntity createEntity(BaseEntity entity) {
    assert entity != null : "entity to create must not be null";
    assert principal != null : "injected principal must not be null";

    entity.setCreatedByUid(principal.getName());
    entity.setModifiedByUid(principal.getName());
    em.persist(entity);
    return entity;
  }

  public BaseEntity updateEntity(BaseEntity entity) {
    assert entity != null : "entity to update must not be null";
    assert principal != null : "injected principal must not be null";

    entity.setModifiedByUid(principal.getName());
    return em.merge(entity);
  }

  public void removeEntity(BaseEntity entity) {
    assert entity != null : "entity to remove must not be null";
    em.remove(em.find(entity.getClass(), entity.getId()));
  }

  public ForumEntity createForum(ForumEntity entity) {
    return (ForumEntity) createEntity(entity);
  }

  public ForumEntity updateForum(ForumEntity entity) {
    return (ForumEntity) updateEntity(entity);
  }

  public MessageEntity createMessage(MessageEntity entity) {
    // TODO: this is basically a constraint check until i fix the orm schema
    try {
      repository.findForumByUuid(entity.getForumUuid());
      return (MessageEntity) createEntity(entity);
    } catch (NoResultException exc) {
      throw new PersistenceException("created message cannot be created for nonexistant forum",
          exc);
    }
  }

}
