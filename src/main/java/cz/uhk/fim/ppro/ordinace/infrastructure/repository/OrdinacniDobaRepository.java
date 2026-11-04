package cz.uhk.fim.ppro.ordinace.infrastructure.repository;

import cz.uhk.fim.ppro.ordinace.application.model.OrdinacniDoba;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrdinacniDobaRepository extends JpaRepository<OrdinacniDoba, UUID> {

    List<OrdinacniDoba> findByLekarIdOrderByDenVTydnuAscCasOdAsc(UUID lekarId);

    List<OrdinacniDoba> findByLekarIdAndDenVTydnu(UUID lekarId, Integer denVTydnu);
}
