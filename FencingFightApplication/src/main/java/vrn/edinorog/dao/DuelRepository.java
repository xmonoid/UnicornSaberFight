package vrn.edinorog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vrn.edinorog.domain.Duel;
import vrn.edinorog.domain.Fighter;
import vrn.edinorog.domain.Nomination;

import java.util.List;

public interface DuelRepository extends JpaRepository<Duel, Long> {

    @Modifying
    @Query("update Duel " +
            "set red_fighter_id = :redFighterId, " +
                "blue_fighter_id = :blueFighterId " +
            "where duel_id = :duelId")
    void updateFighterCompetitionData(
            @Param("duelId") Long duelId,
            @Param("redFighterId") Long redFighterId,
            @Param("blueFighterId") Long blueFighterId
    );

    @Modifying
    @Query("update Duel " +
            "set redScore = :#{#duel.redScore}, " +
                "blueScore = :#{#duel.blueScore}, " +
                "mutualHitCount = :#{#duel.mutualHitCount}, " +
                "winner = :#{#duel.winner} " +
            "where duel_id = :#{#duel.id}")
    void updateDuelResult(@Param("duel") Duel duel);

    @Modifying
    @Query("update Duel " +
            "set duelStatus = :duelStatus " +
            "where duel_id = :duelId")
    void updateDuelStatus(@Param("duelId") Long duelId, @Param("duelStatus") Duel.DuelStatus duelStatus);

    List<Duel> findByRedFighterOrBlueFighter(Fighter redFighter, Fighter blueFighter);

    List<Duel> findByNomination(Nomination nomination);

}