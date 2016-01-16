package morbrian.websockets.producers;


import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class PersistentContextProducer {
  @Produces @PersistenceContext(unitName = "primary") private EntityManager em;
}
