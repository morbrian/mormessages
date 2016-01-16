package morbrian.websockets.persistence;

import morbrian.websockets.model.ForumEntity;
import morbrian.websockets.model.MessageEntity;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@ApplicationScoped public class Repository {
  @Inject private transient Logger logger;

  @Inject private EntityManager em;

  public List<ForumEntity> findAllForumsOrderedByCreatedTime() {
    //noinspection unchecked
    return em.createNamedQuery(ForumEntity.FIND_ALL_ORDERED_BY_CREATED_TIME).getResultList();
  }

  public ForumEntity findForumById(Long id) throws NoResultException {
    return (ForumEntity) em.createNamedQuery(ForumEntity.FIND_ONE_BY_ID).setParameter("id", id)
        .getSingleResult();
  }

  public List<MessageEntity> findMessagesForForumOrderedByCreatedTime(Long forumId) {
    //noinspection unchecked
    return em.createNamedQuery(MessageEntity.FIND_IN_FORUM_ORDERED_BY_CREATED_TIME)
        .setParameter("forumId", forumId).getResultList();
  }

  public List<MessageEntity> findMessagesForForumWithIdRangeOrderedByCreatedTime(Long forumId,
      Long lowId, Long highId) {
    //noinspection unchecked
    return em.createNamedQuery(
        MessageEntity.FIND_IN_FORUM_WITH_ID_RANGE_EXCLUSIVE_ORDERED_BY_CREATED_TIME)
        .setParameter("forumId", forumId).setParameter("lowId", lowId)
        .setParameter("highId", highId).getResultList();
  }

  public MessageEntity findMessageById(Long id) throws NoResultException {
    return (MessageEntity) em.createNamedQuery(MessageEntity.FIND_ONE_BY_ID).setParameter("id", id)
        .getSingleResult();
  }

}
