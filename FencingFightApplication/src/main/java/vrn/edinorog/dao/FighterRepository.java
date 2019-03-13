package vrn.edinorog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vrn.edinorog.domain.Fighter;
import vrn.edinorog.domain.Nomination;

import java.util.List;

public interface FighterRepository extends JpaRepository<Fighter, Long> {

    @Modifying
    @Query("update Fighter " +
            "set firstName = :#{#fighter.firstName}, " +
                "lastName = :#{#fighter.lastName}, " +
                "parentName = :#{#fighter.parentName}, " +
                "sex = :#{#fighter.sex}, " +
                "birthDate = :#{#fighter.birthDate}, " +
                "club = :#{#fighter.club}, " +
                "city = :#{#fighter.city} " +
            "where fighter_id = :#{#fighter.id}")
    void updateFighterPersonalData(@Param("fighter") Fighter fighter);

    @Modifying
    @Query("update Fighter " +
            "set medalAchievement = :#{#fighter.medalAchievement}, " +
                "cupAchievement = :#{#fighter.cupAchievement} " +
            "where fighter_id = :#{#fighter.id}")
    void updateFighterCompetitionData(@Param("fighter") Fighter fighter);

    @Modifying
    @Query(value = "delete from Fighter_Nominations " +
            "where fighter_fighter_id = :fighterId", nativeQuery = true)
    void deleteAllNominationLinksOfFighter(@Param("fighterId") Long fighterId);

    @Modifying
    @Query(value = "insert into Fighter_Nominations (FIGHTER_FIGHTER_ID, NOMINATIONS_NOMINATION_ID ) " +
                   "values (:fighterId, :nominationId)",
            nativeQuery = true
    )
    void addNominationForFighter(@Param("fighterId") Long fighterId, @Param("nominationId") Long nominationId);

    @Modifying
    @Query("update Fighter set isActive = :isActive where fighter_id = :fighterId")
    void updateFighterStatus(@Param("fighterId") Long fighterId, @Param("isActive") boolean isActive);

    List<Fighter> findAllByNominationsContainsAndIsActive(Nomination nomination, boolean isActive);
}