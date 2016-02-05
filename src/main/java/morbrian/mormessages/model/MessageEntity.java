package morbrian.mormessages.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.enterprise.context.Dependent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({@NamedQuery(
    name = MessageEntity.FIND_ALL_IN_FORUM,
    query = "SELECT e FROM MessageEntity e WHERE e.forumUuid = :forumUuid ORDER BY e.modifiedTime DESC"),
    @NamedQuery(
        name = MessageEntity.FIND_ALL_IN_FORUM_WHERE_MODIFIED_GREATER_THAN,
        query = "SELECT e FROM MessageEntity e WHERE e.forumUuid = :forumUuid AND e.modifiedTime > :modifiedTime ORDER BY e.modifiedTime DESC"),
    @NamedQuery(
        name = MessageEntity.FIND_ONE_BY_UUID,
        query = "SELECT e FROM MessageEntity e WHERE e.uuid = :uuid")}) @Entity
@Table(name = "message") @Dependent public class MessageEntity extends BaseEntity {

  public static final String FIND_ALL_IN_FORUM = "MessageEntity.findAllInForum";

  public static final String FIND_ALL_IN_FORUM_WHERE_MODIFIED_GREATER_THAN =
      "MessageEntity.findAllInForumWhereModifiedGreaterThan";

  public static final String FIND_ONE_BY_UUID = "MessageEntity.findOneByUuid";

  @Column(name = "text", length = 2048, unique = false, nullable = true, updatable = false)
  private String text;

  @Column(name = "image_url", length = 1024, unique = false, nullable = true, updatable = false)
  private String imageUrl;

  // trying to set up the foreign key constraint here results in exception on create:
  // org.hibernate.PropertyAccessException: could not get a field value by reflection getter of morbrian.mormessages.model.ForumEntity.id
  //  @ManyToOne(targetEntity = ForumEntity.class)
  //  @JoinColumn(name = "forum_id")
  private String forumUuid;

  public MessageEntity() {
  }

  public MessageEntity(String text, String imageUrl, String forumUuid) {
    this.text = text;
    this.imageUrl = imageUrl;
    this.forumUuid = forumUuid;
  }

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public static MessageEntity jsonCreator(@JsonProperty(value = "text") String text,
      @JsonProperty(value = "imageUrl") String imageUrl,
      @JsonProperty(value = "forumUuid") String forumUuid) {
    return new MessageEntity(text, imageUrl, forumUuid);
  }

  public String getText() {
    return text;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getForumUuid() {
    return forumUuid;
  }

  public void setForumUuid(String forumUuid) {

  }

}
