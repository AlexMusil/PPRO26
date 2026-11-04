package cz.uhk.fim.ppro.ordinace.infrastructure.repository;

import cz.uhk.fim.ppro.ordinace.application.model.Navsteva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NavstevaRepository extends JpaRepository<Navsteva, UUID> {

    Optional<Navsteva> findByRezervaceId(UUID rezervaceId);

    @Query("SELECT n FROM Navsteva n " +
           "JOIN FETCH n.rezervace r " +
           "JOIN FETCH r.pacient p " +
           "JOIN FETCH r.lekar l " +
           "LEFT JOIN FETCH n.vykony nv " +
           "LEFT JOIN FETCH nv.vykon " +
           "WHERE (:lekarId IS NULL OR l.id = :lekarId) " +
           "AND n.datumCas >= :od AND n.datumCas <= :do " +
           "ORDER BY n.datumCas ASC")
    List<Navsteva> findNavstevyProVykaz(@Param("lekarId") UUID lekarId,
                                       @Param("od") LocalDateTime od,
                                       @Param("do") LocalDateTime doCas);
}

