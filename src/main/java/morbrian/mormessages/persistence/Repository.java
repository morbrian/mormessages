package morbrian.mormessages.persistence;

import morbrian.mormessages.model.ForumEntity;
import morbrian.mormessages.model.MessageEntity;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@ApplicationScoped public class Repository {

  public static final int DEFAULT_QUERY_OFFSET = 0;

  public static final int DEFAULT_RESULT_SIZE = 100;

  public static final Calendar DEFAULT_QUERY_GREATER_THAN = new GregorianCalendar(2010, 0, 1);

  @Inject private transient Logger logger;

  @Inject private EntityManager em;

  public List<ForumEntity> listForums() {
    //noinspection unchecked
    return listForums(DEFAULT_QUERY_OFFSET, DEFAULT_RESULT_SIZE, DEFAULT_QUERY_GREATER_THAN);
  }

  public List<ForumEntity> listForums(Integer offset, Integer resultSize, Calendar greaterThan) {
    //noinspection unchecked
    return em.createNamedQuery(ForumEntity.FIND_ALL_WHERE_MODIFIED_GREATER_THAN)
        .setParameter("modifiedTime",
            (greaterThan != null) ? greaterThan : DEFAULT_QUERY_GREATER_THAN)
        .setFirstResult((offset != null) ? offset : DEFAULT_QUERY_OFFSET)
        .setMaxResults((resultSize != null) ? resultSize : DEFAULT_RESULT_SIZE).getResultList();
  }

  public ForumEntity findForumByUuid(String uuid) throws NoResultException {
    return (ForumEntity) em.createNamedQuery(ForumEntity.FIND_ONE_BY_UUID)
        .setParameter("uuid", uuid).getSingleResult();
  }

  public ForumEntity findForumByTitle(String title) throws NoResultException {
    return (ForumEntity) em.createNamedQuery(ForumEntity.FIND_ONE_BY_TITLE)
        .setParameter("title", title).getSingleResult();
  }

  public List<MessageEntity> listMessagesInForum(String forumUuid) {
    return listMessagesInForum(forumUuid, DEFAULT_QUERY_OFFSET, DEFAULT_RESULT_SIZE,
        DEFAULT_QUERY_GREATER_THAN);
  }

  public List<MessageEntity> listMessagesInForum(String forumUuid, Integer offset,
      Integer resultSize, Calendar greaterThan) {
    //noinspection unchecked
    return em.createNamedQuery(MessageEntity.FIND_ALL_IN_FORUM_WHERE_MODIFIED_GREATER_THAN)
        .setParameter("forumUuid", forumUuid).setParameter("modifiedTime",
            (greaterThan != null) ? greaterThan : DEFAULT_QUERY_GREATER_THAN)
        .setFirstResult((offset != null) ? offset : DEFAULT_QUERY_OFFSET)
        .setMaxResults((resultSize != null) ? resultSize : DEFAULT_RESULT_SIZE).getResultList();
  }

  public MessageEntity findMessageByUuid(String uuid) throws NoResultException {
    return (MessageEntity) em.createNamedQuery(MessageEntity.FIND_ONE_BY_UUID)
        .setParameter("uuid", uuid).getSingleResult();
  }

}
