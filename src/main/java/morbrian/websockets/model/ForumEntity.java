package morbrian.websockets.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass @EntityListeners({ForumEntityListener.class}) public class ForumEntity
    extends BaseEntity {

  @NotNull @Column(name = "title", length = 255, unique = true, nullable = false) String title;

  @Column(name = "description", length = 255, unique = false, nullable = true) String description;

  @Column(name = "image_url", length = 255, unique = false, nullable = true) String imageUrl;

  public ForumEntity(String title, String description, String imageUrl) {
    this.title = title;
    this.description = description;
    this.imageUrl = imageUrl;
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

  @JsonCreator(mode=JsonCreator.Mode.PROPERTIES)
  public static ForumEntity jsonCreator(
      @JsonProperty(value = "title") String title,
      @JsonProperty(value = "description") String description,
      @JsonProperty(value = "imageUrl") String imageUrl) {
    return new ForumEntity(title, description, imageUrl);
  }
}
