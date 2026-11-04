package cz.uhk.fim.ppro.ordinace.infrastructure.repository;

import cz.uhk.fim.ppro.ordinace.application.model.Vykon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VykonRepository extends JpaRepository<Vykon, String> {

    List<Vykon> findAllByAktivniTrueOrderByKodAsc();

    Optional<Vykon> findByKod(String kod);
}
