package de.propra.wuffer2.db;

import de.propra.wuffer2.domain.Wuff;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WuffRepo extends ListPagingAndSortingRepository<Wuff, Integer> {

}