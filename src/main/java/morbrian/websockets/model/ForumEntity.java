package morbrian.websockets.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.enterprise.context.Dependent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@NamedQueries({@NamedQuery(
    name = ForumEntity.FIND_ALL,
    query = "SELECT e FROM ForumEntity e ORDER BY e.id"), @NamedQuery(
    name = ForumEntity.FIND_ONE_BY_ID,
    query = "SELECT e FROM ForumEntity e WHERE e.id = :id"), @NamedQuery(
    name = ForumEntity.FIND_ONE_BY_TITLE,
    query = "SELECT e FROM ForumEntity e WHERE e.title = :title")}) @Entity @Table(name = "forum")
@Dependent public class ForumEntity extends BaseEntity {

  public static final String FIND_ALL = "ForumEntity.findAll";

  public static final String FIND_ONE_BY_ID = "ForumEntity.findOneById";

  public static final String FIND_ONE_BY_TITLE = "ForumEntity.findOneByTitle";

  @NotNull @Column(name = "title", length = 255, unique = true, nullable = false) private String
      title;

  @Column(name = "description", length = 255, unique = false, nullable = true) private String
      description;

  @Column(name = "image_url", length = 1024, unique = false, nullable = true) private String
      imageUrl;

  //@JsonIgnore
  //@OneToMany(mappedBy = "forumId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  //private List<MessageEntity> messages;


  public ForumEntity() {
  }

  public ForumEntity(String title, String description, String imageUrl) {
    this.title = title;
    this.description = description;
    this.imageUrl = imageUrl;
  }

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public static ForumEntity jsonCreator(@JsonProperty(value = "title") String title,
      @JsonProperty(value = "description") String description,
      @JsonProperty(value = "imageUrl") String imageUrl) {
    return new ForumEntity(title, description, imageUrl);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  //  public List<MessageEntity> getMessages() {
  //    return messages;
  //  }

}
