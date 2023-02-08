package de.propra.wuffer2.db;

import de.propra.wuffer2.domain.Profile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepo extends CrudRepository<Profile, Integer> {

  List<Profile> findAll();

  Optional<Profile> findByProfileId(Integer profileId);
}