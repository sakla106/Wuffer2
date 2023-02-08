package de.propra.wuffer2.domain;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

public class Wuff {

  private final @Id Integer id;
  private final String text;
  private final AggregateReference<Profile, Integer> profile;
  private final LocalDateTime createdAt;

  @PersistenceCreator
  public Wuff(Integer id, String text, AggregateReference<Profile, Integer> profile,
              LocalDateTime createdAt) {
    this.id = id;
    this.text = text;
    this.profile = profile;
    this.createdAt = createdAt;
  }

  public Wuff(String text, Profile profile, LocalDateTime createdAt) {
    this(null, text, AggregateReference.to(profile.getId()), createdAt);
  }

  public Integer getId() {
    return id;
  }

  public String getText() {
    return text;
  }

  public AggregateReference<Profile, Integer> getProfile() {
    return profile;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  @Override
  public String toString() {
    return "Wuff{" +
        "id=" + id +
        ", text='" + text + '\'' +
        ", profile=" + profile +
        ", createdAt=" + createdAt +
        '}';
  }
}