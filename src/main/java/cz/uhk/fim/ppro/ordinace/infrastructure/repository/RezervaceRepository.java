package cz.uhk.fim.ppro.ordinace.infrastructure.repository;

import cz.uhk.fim.ppro.ordinace.application.model.Rezervace;
import cz.uhk.fim.ppro.ordinace.application.model.StavRezervace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface RezervaceRepository extends JpaRepository<Rezervace, UUID> {

    @Query("SELECT r FROM Rezervace r JOIN FETCH r.pacient JOIN FETCH r.lekar WHERE r.lekar.id = :lekarId AND r.casOd >= :od AND r.casOd <= :do ORDER BY r.casOd ASC")
    List<Rezervace> findByLekarAndObdobi(@Param("lekarId") UUID lekarId,
                                        @Param("od") LocalDateTime od,
                                        @Param("do") LocalDateTime doCas);

    @Query("SELECT r FROM Rezervace r JOIN FETCH r.pacient JOIN FETCH r.lekar WHERE r.pacient.id = :pacientId ORDER BY r.casOd DESC")
    List<Rezervace> findByPacientIdOrderByCasOdDesc(@Param("pacientId") UUID pacientId);

    @Query("SELECT r FROM Rezervace r WHERE r.lekar.id = :lekarId AND r.stav = cz.uhk.fim.ppro.ordinace.application.model.StavRezervace.PLANOVANA AND r.casOd < :casDo AND r.casDo > :casOd")
    List<Rezervace> findKolizeRezervace(@Param("lekarId") UUID lekarId,
                                       @Param("casOd") LocalDateTime casOd,
                                       @Param("casDo") LocalDateTime casDo);

    @Query("SELECT r FROM Rezervace r WHERE r.lekar.id = :lekarId AND r.stav = cz.uhk.fim.ppro.ordinace.application.model.StavRezervace.PLANOVANA AND r.casOd < :casDo AND r.casDo > :casOd AND r.id <> :excludeId")
    List<Rezervace> findKolizeRezervaceMimoId(@Param("lekarId") UUID lekarId,
                                             @Param("casOd") LocalDateTime casOd,
                                             @Param("casDo") LocalDateTime casDo,
                                             @Param("excludeId") UUID excludeId);

    @Query("SELECT r FROM Rezervace r JOIN FETCH r.pacient JOIN FETCH r.lekar WHERE r.lekar.id = :lekarId AND r.stav = cz.uhk.fim.ppro.ordinace.application.model.StavRezervace.PLANOVANA AND r.casOd < :casDo AND r.casDo > :casOd ORDER BY r.casOd ASC")
    List<Rezervace> findPlatneRezervaceVIntervalu(@Param("lekarId") UUID lekarId,
                                                 @Param("casOd") LocalDateTime casOd,
                                                 @Param("casDo") LocalDateTime casDo);

    long countByPacientIdAndStavIn(UUID pacientId, Collection<StavRezervace> stavy);

    @Query("SELECT r FROM Rezervace r JOIN FETCH r.pacient JOIN FETCH r.lekar WHERE r.stav = cz.uhk.fim.ppro.ordinace.application.model.StavRezervace.K_PREOBJEDNANI ORDER BY r.casOd ASC")
    List<Rezervace> findVsechnyKPreobjednani();
}

