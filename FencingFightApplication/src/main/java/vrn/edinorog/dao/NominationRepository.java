package vrn.edinorog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vrn.edinorog.domain.Nomination;
import vrn.edinorog.enums.CompetitionStage;

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

}