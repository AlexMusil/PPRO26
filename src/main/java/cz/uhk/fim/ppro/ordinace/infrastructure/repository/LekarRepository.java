package cz.uhk.fim.ppro.ordinace.infrastructure.repository;

import cz.uhk.fim.ppro.ordinace.application.model.Lekar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LekarRepository extends JpaRepository<Lekar, UUID> {

    List<Lekar> findAllByOrderByPrijmeniAscJmenoAsc();

    List<Lekar> findAllByAktivniTrueOrderByPrijmeniAscJmenoAsc();
}

