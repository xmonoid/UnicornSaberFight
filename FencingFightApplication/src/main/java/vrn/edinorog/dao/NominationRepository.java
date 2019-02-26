package vrn.edinorog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vrn.edinorog.domain.Nomination;
import vrn.edinorog.enums.CompetitionStage;

import java.util.List;
import java.util.Set;

public interface NominationRepository extends JpaRepository<Nomination, Long> {

    @Modifying
    @Query("update Nomination set name = :name where nomination_id = :nominationId")
    void updateNominationName(@Param("nominationId") Long nominationId, @Param("name") String name);

    @Modifying
    @Query("update Nomination " +
            "set currentStage = :currentStage, currentRoundIndex = :currentRoundIndex " +
            "where nomination_id = :nominationId")
    void updateNominationCurrentStageAndRoundIndex(
            @Param("nominationId") Long nominationId,
            @Param("currentStage") CompetitionStage competitionStage,
            @Param("currentRoundIndex") Integer currentRoundIndex
    );


    @Query("select id from Nomination")
    Set<Long> findAllIds();

    @Modifying
    @Query(value = "delete from Fighter_Nominations " +
            "where nominations_nomination_id = :nominationId", nativeQuery = true)
    void deleteAllFighterLinksOfNomination(@Param("nominationId") Long nominationId);

}