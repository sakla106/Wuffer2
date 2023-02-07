package de.propra.wuffer2.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;

public class Profile {

  private final @Id Integer id;
  private final Integer profileId;
  private final String name;
  private final String avatarUrl;

  public Profile(Integer profileId, String name, String avatarUrl) {
    this(null, profileId, name, avatarUrl);
  }

  @PersistenceCreator
  public Profile(Integer id, Integer profileId, String name, String avatarUrl) {
    this.id = id;
    this.profileId = profileId;
    this.name = name;
    this.avatarUrl = avatarUrl;
  }

  @Override
  public String toString() {
    return "Profile{" +
        "id=" + id +
        ", profileId=" + profileId +
        ", name='" + name + '\'' +
        ", avatarUrl='" + avatarUrl + '\'' +
        '}';
  }

  public Integer getId() {
    return id;
  }

  public Integer getProfileId() {
    return profileId;
  }

  public String getName() {
    return name;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }
}