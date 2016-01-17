package morbrian.websockets.model;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Calendar;
import java.util.UUID;

public class BaseEntityListener {

  // CDI is supposed to work now in Java EE 7.
  // However, having Inject of any kind inside an entity-listener just causes NPE.
  // See: http://stackoverflow.com/questions/33891368/org-jboss-weld-exceptions-illegalargumentexception-weld-001456-argument-resolv
  // I suppose upgrading Hibernate to 5.0.6 might fix?
  // will probably just wait until Wildfly 10 is released
  //  @Inject private Logger logger;
  //  @Inject private Principal principal;

  @PrePersist public void prePersist(BaseEntity entity) {
    //    if (logger != null) {
    //      logger.info("INJECTION WORKS IN BASE_ENTITY_LISTENER");
    //    } else {
    //      System.out.println("INJECTION BROKEN IN BASE_ENTITY_LISTENER");
    //    }
    //    if (principal != null) {
    //      System.out.println("PRINCIPAL IS NOT NULL IN THE BASE_ENTTITY_LISTENER");
    //    }
    if (entity.getUuid() == null || entity.getUuid().isEmpty()) {
      entity.setUuid(UUID.randomUUID().toString());
    }
    Calendar now = Calendar.getInstance();
    entity.setCreatedTime(now);
    entity.setModifiedTime(now);
  }

  @PreUpdate public void preUpdate(BaseEntity entity) {
    Calendar now = Calendar.getInstance();
    entity.setModifiedTime(now);
  }

}
