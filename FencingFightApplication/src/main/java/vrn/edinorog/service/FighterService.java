package vrn.edinorog.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import vrn.edinorog.dao.DuelRepository;
import vrn.edinorog.dao.FighterRepository;
import vrn.edinorog.dao.NominationRepository;
import vrn.edinorog.domain.Duel;
import vrn.edinorog.domain.Fighter;

import java.util.List;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
public class FighterService {

    private final FighterRepository fighterRepository;
    private final NominationRepository nominationRepository;
    private final DuelRepository duelRepository;

    public List<Fighter> getAllFighters() {
        log.debug("#getAllFighters()");
        return fighterRepository.findAll();
    }

    @Transactional
    public void addNewFighter(Fighter fighter) {
        log.debug("#addNewFighter(Fighter fighter): {}", fighter);
        Assert.notNull(fighter, "Fighter must be not null!");
        fighterRepository.save(fighter);
    }

    @Transactional
    public void addNewFighters(List<Fighter> fighters) {
        log.debug("#addNewFighters(List<Fighter> fighters): {}", fighters);
        Assert.isTrue(CollectionUtils.isNotEmpty(fighters), "Fighters must be not empty");
        fighterRepository.saveAll(fighters);
    }

    @Transactional
    public void updateFighterPersonalData(Fighter fighter) {
        log.debug("#updateFighterPersonalData(Fighter fighter): {}", fighter);
        Assert.notNull(fighter, "Fighter must be not null!");
        fighterRepository.updateFighterPersonalData(fighter);
    }

    @Transactional
    public void updateFighterCompetitionData(Fighter fighter) {
        log.debug("#updateFighterCompetitionData(Fighter fighter): {}", fighter);
        Assert.notNull(fighter, "Fighter must be not null!");
        fighterRepository.updateFighterCompetitionData(fighter);
    }

    @Transactional
    public void updateFighterNominations(Long fighterId, List<Long> nominationIds) {
        log.debug("#updateFighterNominations(Long nominationId, List<String> nominationIds): {}, {}", fighterId, nominationIds);
        Assert.notNull(fighterId, "Fighter id must be not null!");
        Assert.notNull(nominationIds, "Nomination ids collection must be not null!");
        fighterRepository.deleteAllNominationLinksOfFighter(fighterId);

        Set<Long> existedNominationIds = nominationRepository.findAllIds();

        for (Long nominationId : nominationIds) {
            if (existedNominationIds.contains(nominationId)) {
                fighterRepository.addNominationForFighter(fighterId, nominationId);
            }
        }
    }

    @Transactional
    public void deleteFighter(Long fighterId, boolean isDeactivate, boolean cascadeDelete) {
        log.debug("#deleteFighter(Long fighterId, boolean isDeactivate): {}, {}", fighterId, isDeactivate);
        Assert.notNull(fighterId, "Fighter id must be not null!");
        if (isDeactivate) {
            fighterRepository.updateFighterStatus(fighterId, false);
        } else if (cascadeDelete) {
            Fighter fighter = fighterRepository.getOne(fighterId);

            List<Duel> duels = duelRepository.findByRedFighterOrBlueFighter(fighter, fighter);
            duelRepository.deleteInBatch(duels);

            fighterRepository.deleteAllNominationLinksOfFighter(fighterId);
            fighterRepository.deleteById(fighterId);
        } else {
            fighterRepository.deleteAllNominationLinksOfFighter(fighterId);
            fighterRepository.deleteById(fighterId);
        }
    }

}