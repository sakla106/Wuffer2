package de.propra.wuffer2.db;

import de.propra.wuffer2.domain.Wuff;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WuffRepo extends PagingAndSortingRepository<Wuff, Integer> {

  List<Wuff> findAll();

  List<Wuff> findAllByOrderByCreatedAtDesc();
}