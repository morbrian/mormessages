package morbrian.websockets.model;

import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.security.Principal;
import java.util.Calendar;

public class BaseEntityListener {

  @Inject private Logger logger;
  @Inject private Principal principal;

  @PrePersist public void prePersist(BaseEntity entity) {
    if (logger != null) {
      logger.info("INJECTION WORKS IN BASE_ENTITY_LISTENER");
    } else {
      System.out.println("AWW, STILL NOT WORK");
    }
    if (principal != null) {
      System.out.println("PRINCIPAL IS NOT NULL IN THE BASE_ENTTITY_LISTENER");
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
