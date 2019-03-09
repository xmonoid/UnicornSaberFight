package vrn.edinorog.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import vrn.edinorog.dao.DuelRepository;
import vrn.edinorog.dao.NominationRepository;
import vrn.edinorog.domain.Duel;
import vrn.edinorog.domain.Nomination;
import vrn.edinorog.enums.CompetitionStage;
import vrn.edinorog.exception.ApplicationException;

import java.util.Collections;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class NominationService {

    private final NominationRepository nominationRepository;
    private final DuelRepository duelRepository;

    public List<Nomination> getAllNominations() {
        log.debug("#getAllNominations()");
        return nominationRepository.findAll();
    }

    @Transactional
    public void addNewNomination(Nomination nomination) {
        log.debug("#addNewNomination(Nomination nomination): {}", nomination);
        Assert.notNull(nomination, "Nomination must be not null!");
        Assert.isTrue(StringUtils.isNoneBlank(nomination.getName()), "Name must be not blank!");
        nominationRepository.save(nomination);
    }

    @Transactional
    public void addNewNominations(List<Nomination> nominations) {
        log.debug("#addNewNominations(List<Nomination> nominations): {}", nominations);
        Assert.notNull(nominations, "Nominations list must be not null!");
        for (Nomination nomination : nominations) {
            Assert.isTrue(StringUtils.isNoneBlank(nomination.getName()), "Name must be not blank!");
        }

        nominationRepository.saveAll(nominations);
    }

    @Transactional
    public void updateNominationName(Long nominationId, String name) {
        log.debug("#updateNominationName(Long nominationId, String name): {}, {}", nominationId, name);
        Assert.notNull(nominationId, "Nomination id must be not null!");
        Assert.isTrue(StringUtils.isNoneBlank(name), "Name must be not blank!");
        nominationRepository.updateNominationName(nominationId, name);
    }

    @Transactional
    public void updateNominationCurrentSateAndRoundIndex(Long nominationId, CompetitionStage competitionStage, Integer roundIndex) {
        log.debug("#updateNominationCurrentSateAndRoundIndex(Long nominationId, CompetitionStage competitionStage, Integer roundIndex): {}, {}, {}", nominationId, competitionStage, roundIndex);
        Assert.notNull(nominationId, "Nomination id must be not null!");
        Assert.notNull(competitionStage, "Competition stage must be not null!");
        Assert.notNull(nominationId, "Round index must be not null!");
        Assert.isTrue(roundIndex > 0, "Round index must be positive!");
        nominationRepository.updateNominationCurrentStageAndRoundIndex(nominationId, competitionStage, roundIndex);
    }

    @Transactional
    public void deleteNomination(Long nominationId, boolean cascadeDelete) {
        log.debug("#deleteNomination(Long nominationId): {}, {}", nominationId, cascadeDelete);
        Assert.notNull(nominationId, "Nomination id must be not null!");

        Nomination nomination = nominationRepository.getOne(nominationId);
        List<Duel> duels = duelRepository.findByNomination(nomination);

        if (CollectionUtils.isNotEmpty(duels) && !cascadeDelete) {
            throw new ApplicationException(
                    "Nomination wasn't be deleted, because there are duels related with it",
                    "Удалить номинацию и все сражения в ней?"
            );
        }

        if (cascadeDelete) {
            duelRepository.deleteInBatch(duels);
        }

        nominationRepository.deleteAllFighterLinksOfNomination(nominationId);
        nominationRepository.deleteById(nominationId);
    }
}