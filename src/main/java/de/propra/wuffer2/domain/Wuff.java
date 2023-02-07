package de.propra.wuffer2.domain;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

public class Wuff {

  private final @Id Integer id;
  private final String text;
  private final AggregateReference<Profile, Integer> profile;
  private final LocalDateTime timestamp;

  @PersistenceCreator
  public Wuff(Integer id, String text, Profile profile,
              LocalDateTime timestamp) {
    this.id = id;
    this.text = text;
    this.profile = AggregateReference.to(profile.getId());
    this.timestamp = timestamp;
  }

  public Wuff(String text, Profile profile, LocalDateTime timestamp) {
    this(null, text, profile, timestamp);
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

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  @Override
  public String toString() {
    return "Wuff{" +
        "id=" + id +
        ", text='" + text + '\'' +
        ", profile=" + profile +
        ", timestamp=" + timestamp +
        '}';
  }
}