package morbrian.websockets.persistence;

import morbrian.websockets.model.ForumEntity;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@ApplicationScoped public class Repository {
  @Inject private Logger log;

  @Inject private EntityManager em;

  public List<ForumEntity> findAllForumsOrderedByCreatedTime() {
    return findAllForumsOrderedBy("createdTime");
  }

  public List<ForumEntity> findAllForumsOrderedBy(String attrName) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<ForumEntity> criteria = cb.createQuery(ForumEntity.class);
    Root<ForumEntity> entity = criteria.from(ForumEntity.class);
    // the order by attribute string refers to the java class attribute not the column name
    criteria.select(entity).orderBy(cb.asc(entity.get(attrName)));
    return em.createQuery(criteria).getResultList();
  }

  public ForumEntity findForumById(Long id) throws NoResultException {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<ForumEntity> criteria = cb.createQuery(ForumEntity.class);
    Root<ForumEntity> entity = criteria.from(ForumEntity.class);
    Predicate condition = cb.equal(entity.get("id"), id);
    // the order by attribute string refers to the java class attribute not the column name
    criteria.select(entity).where(condition);
    return em.createQuery(criteria).getSingleResult();
  }

}
