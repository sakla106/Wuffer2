package de.propra.wuffer2.domain;

import java.time.LocalDateTime;

public record WuffDetail(
    String text,
    String profileName,
    String profilePicture,
    LocalDateTime createdAt
) {

}