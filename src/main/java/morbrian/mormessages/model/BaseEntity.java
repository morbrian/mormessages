package morbrian.mormessages.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import morbrian.mormessages.dataformat.CalendarDeserializer;
import morbrian.mormessages.dataformat.CalendarSerializer;

import javax.enterprise.context.Dependent;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Calendar;

@MappedSuperclass @EntityListeners({BaseEntityListener.class}) @Dependent public class BaseEntity {

  @Temporal(TemporalType.TIMESTAMP) @Column(name = "created_time", updatable = false)
  private Calendar createdTime;
  @Temporal(TemporalType.TIMESTAMP) @Column(name = "modified_time") private Calendar modifiedTime;
  @Column(name = "created_by_uid", updatable = false, length = 255, nullable = false) private String
      createdByUid;
  @Column(name = "modified_by_uid", length = 255, nullable = false) private String modifiedByUid;
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  @Column(name = "uuid", nullable = false, unique = true) private String uuid;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @JsonSerialize(using = CalendarSerializer.class) public Calendar getCreatedTime() {
    return createdTime;
  }

  @JsonDeserialize(using = CalendarDeserializer.class)
  public void setCreatedTime(Calendar createdTime) {
    this.createdTime = createdTime;
  }

  @JsonSerialize(using = CalendarSerializer.class) public Calendar getModifiedTime() {
    return modifiedTime;
  }

  @JsonDeserialize(using = CalendarDeserializer.class)
  public void setModifiedTime(Calendar modifiedTime) {
    this.modifiedTime = modifiedTime;
  }

  public String getCreatedByUid() {
    return createdByUid;
  }

  public void setCreatedByUid(String createdByUid) {
    this.createdByUid = createdByUid;
  }

  public String getModifiedByUid() {
    return modifiedByUid;
  }

  public void setModifiedByUid(String modifiedByUid) {
    this.modifiedByUid = modifiedByUid;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }
}
