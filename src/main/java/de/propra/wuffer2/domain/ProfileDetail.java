package de.propra.wuffer2.domain;

public record ProfileDetail(
    String username,
    String avatarUrl,
    Integer profileId,
    String githubUrl
) {

}