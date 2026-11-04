package cz.uhk.fim.ppro.ordinace.infrastructure.repository;

import cz.uhk.fim.ppro.ordinace.application.model.NavstevaVykon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NavstevaVykonRepository extends JpaRepository<NavstevaVykon, UUID> {

    List<NavstevaVykon> findByNavstevaId(UUID navstevaId);
}
