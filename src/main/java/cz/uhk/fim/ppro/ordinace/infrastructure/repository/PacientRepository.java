package cz.uhk.fim.ppro.ordinace.infrastructure.repository;

import cz.uhk.fim.ppro.ordinace.application.model.Pacient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PacientRepository extends JpaRepository<Pacient, UUID> {

    Optional<Pacient> findByCisloPojistence(String cisloPojistence);

    boolean existsByCisloPojistence(String cisloPojistence);

    boolean existsByJmenoIgnoreCaseAndPrijmeniIgnoreCaseAndTelefon(String jmeno, String prijmeni, String telefon);

    @Query("SELECT p FROM Pacient p WHERE " +
           "LOWER(p.prijmeni) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.jmeno) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "p.telefon LIKE CONCAT('%', :query, '%') OR " +
           "p.cisloPojistence LIKE CONCAT('%', :query, '%') " +
           "ORDER BY p.prijmeni ASC, p.jmeno ASC")
    List<Pacient> searchPacienti(@Param("query") String query);

    List<Pacient> findAllByOrderByPrijmeniAscJmenoAsc();
}
