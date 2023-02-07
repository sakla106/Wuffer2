package de.propra.wuffer2.db;

import de.propra.wuffer2.domain.Profile;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepo extends ListCrudRepository<Profile, Integer> {

}