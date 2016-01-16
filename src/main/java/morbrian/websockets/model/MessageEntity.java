package morbrian.websockets.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.enterprise.context.Dependent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({@NamedQuery(
    name = MessageEntity.FIND_IN_FORUM_ORDERED_BY_CREATED_TIME,
    query = "SELECT e FROM MessageEntity e WHERE e.forumId = :forumId ORDER BY e.createdTime"),
    @NamedQuery(
        name = MessageEntity.FIND_IN_FORUM_WITH_ID_RANGE_EXCLUSIVE_ORDERED_BY_CREATED_TIME,
        query = "SELECT e FROM MessageEntity e WHERE e.forumId = :forumId AND (e.id < :lowId OR e.id > :highId) ORDER BY e.createdTime"),
    @NamedQuery(
        name = MessageEntity.FIND_ONE_BY_ID,
        query = "SELECT e FROM MessageEntity e WHERE e.id = :id")}) @Entity @Table(name = "message")
@Dependent public class MessageEntity extends BaseEntity {

  public static final String FIND_IN_FORUM_ORDERED_BY_CREATED_TIME =
      "MessageEntity.findInForumOrderedByCreatedTime";

  public static final String FIND_IN_FORUM_WITH_ID_RANGE_EXCLUSIVE_ORDERED_BY_CREATED_TIME =
      "MessageEntity.findInForumWithIdRangeExclusiveOrderedByCreatedTime";

  public static final String FIND_ONE_BY_ID = "MessageEntity.findOneById";

  @Column(name = "text", length = 2048, unique = false, nullable = true, updatable = false)
  private String text;

  @Column(name = "image_url", length = 1024, unique = false, nullable = true, updatable = false)
  private String imageUrl;

  // trying to set up the foreign key constraint here results in exception on create:
  // org.hibernate.PropertyAccessException: could not get a field value by reflection getter of morbrian.websockets.model.ForumEntity.id
  //  @ManyToOne(targetEntity = ForumEntity.class)
  //  @JoinColumn(name = "forum_id")
  private long forumId;

  public MessageEntity() {
  }

  public MessageEntity(String text, String imageUrl, Long forumId) {
    this.text = text;
    this.imageUrl = imageUrl;
    this.forumId = forumId;
  }

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public static MessageEntity jsonCreator(@JsonProperty(value = "text") String text,
      @JsonProperty(value = "imageUrl") String imageUrl,
      @JsonProperty(value = "forumId") Long forumId) {
    return new MessageEntity(text, imageUrl, forumId);
  }

  public String getText() {
    return text;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public Long getForumId() {
    return forumId;
  }

  public void setForumId(Long forumId) {

  }

}
