package de.propra.wuffer2.db;

import de.propra.wuffer2.domain.Wuff;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface WuffRepo extends ListPagingAndSortingRepository<Wuff, Integer> {

}