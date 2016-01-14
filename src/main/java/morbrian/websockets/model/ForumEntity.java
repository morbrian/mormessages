package morbrian.websockets.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity @Table(name = "forum") public class ForumEntity extends BaseEntity {

  @NotNull @Column(name = "title", length = 255, unique = true, nullable = false) private String
      title;

  @Column(name = "description", length = 255, unique = false, nullable = true) private String
      description;

  @Column(name = "image_url", length = 255, unique = false, nullable = true) private String
      imageUrl;

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
}
