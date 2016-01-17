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

  public static final int DEFAULT_QUERY_OFFSET = 0;

  public static final int DEFAULT_RESULT_SIZE = 100;

  @Inject private transient Logger logger;

  @Inject private EntityManager em;

  public List<ForumEntity> listForums() {
    //noinspection unchecked
    return listForums(DEFAULT_QUERY_OFFSET, DEFAULT_RESULT_SIZE);
  }

  public List<ForumEntity> listForums(Integer offset, Integer resultSize) {
    //noinspection unchecked
    return em.createNamedQuery(ForumEntity.FIND_ALL)
        .setFirstResult((offset != null) ? offset : DEFAULT_QUERY_OFFSET)
        .setMaxResults((resultSize != null) ? resultSize : DEFAULT_RESULT_SIZE).getResultList();
  }

  public ForumEntity findForumById(Long id) throws NoResultException {
    return (ForumEntity) em.createNamedQuery(ForumEntity.FIND_ONE_BY_ID).setParameter("id", id)
        .getSingleResult();
  }

  public ForumEntity findForumByTitle(String title) throws NoResultException {
    return (ForumEntity) em.createNamedQuery(ForumEntity.FIND_ONE_BY_TITLE)
        .setParameter("title", title).getSingleResult();
  }

  public List<MessageEntity> listMessagesInForum(Long forumId) {
    return listMessagesInForum(forumId, DEFAULT_QUERY_OFFSET, DEFAULT_RESULT_SIZE);
  }

  public List<MessageEntity> listMessagesInForum(Long forumId, Integer offset, Integer resultSize) {
    //noinspection unchecked
    return em.createNamedQuery(MessageEntity.FIND_ALL_IN_FORUM).setParameter("forumId", forumId)
        .setFirstResult((offset != null) ? offset : DEFAULT_QUERY_OFFSET)
        .setMaxResults((resultSize != null) ? resultSize : DEFAULT_RESULT_SIZE).getResultList();
  }

  public MessageEntity findMessageById(Long id) throws NoResultException {
    return (MessageEntity) em.createNamedQuery(MessageEntity.FIND_ONE_BY_ID).setParameter("id", id)
        .getSingleResult();
  }

}
