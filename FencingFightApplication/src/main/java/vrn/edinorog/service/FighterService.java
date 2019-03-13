package vrn.edinorog.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import vrn.edinorog.dao.DuelRepository;
import vrn.edinorog.dao.FighterRepository;
import vrn.edinorog.dao.NominationRepository;
import vrn.edinorog.domain.Duel;
import vrn.edinorog.domain.Fighter;
import vrn.edinorog.domain.Nomination;
import vrn.edinorog.exception.ApplicationException;

import java.util.List;
import java.util.Optional;
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

    public List<Fighter> getFightersByNomination(Nomination nomination) {
        log.debug("#getFightersByNomination(Nomination nomination), {}", nomination);
        Assert.notNull(nomination, "Nomination must be not null!");
        return fighterRepository.findAllByNominationsContainsAndIsActive(nomination, true);
    }

    @Transactional
    public void addNewFighter(Fighter fighter) {
        log.debug("#addNewFighter(Fighter fighter): {}", fighter);
        validateFighterFields(fighter);
        fillFieldNullValue(fighter);
        fighterRepository.save(fighter);
    }

    @Transactional
    public void addNewFighters(List<Fighter> fighters) {
        log.debug("#addNewFighters(List<Fighter> fighters): {}", fighters);
        Assert.isTrue(CollectionUtils.isNotEmpty(fighters), "Fighters must be not empty");

        for (Fighter fighter : fighters) {
            validateFighterFields(fighter);
            fillFieldNullValue(fighter);
        }

        fighterRepository.saveAll(fighters);
    }

    @Transactional
    public void updateFighterPersonalData(Fighter fighter) {
        log.debug("#updateFighterPersonalData(Fighter fighter): {}", fighter);
        validateFighterFields(fighter);
        fillFieldNullValue(fighter);
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
        nominationIds.remove(null);

        fighterRepository.deleteAllNominationLinksOfFighter(fighterId);

        Set<Long> existedNominationIds = nominationRepository.findAllIds();

        for (Long nominationId : nominationIds) {
            if (existedNominationIds.contains(nominationId)) {
                fighterRepository.addNominationForFighter(fighterId, nominationId);
            }
        }
    }

    @Transactional
    public void updateFighterStatus(Long fighterId, boolean isActive) {
        log.debug("#updateFighterStatus(Long fighterId, boolean isActive): {}, {}", fighterId, isActive);
        Assert.notNull(fighterId, "Fighter id must be not null!");

        Optional<Fighter> fighter = fighterRepository.findById(fighterId);

        if (!fighter.isPresent()) {
            throw new ApplicationException(
                    "Участник с id " + fighterId + " не найден!"
            );
        }

        if (CollectionUtils.isEmpty(fighter.get().getNominations()) && isActive) {
            throw new ApplicationException(
                    "Подтвердить участие бойца нельзя, так как не выбрана ни одна номинация!"
            );
        }

        fighterRepository.updateFighterStatus(fighterId, isActive);
    }

    @Transactional
    public void deleteFighter(Long fighterId, boolean cascadeDelete) {
        log.debug("#deleteFighter(Long fighterId, boolean cascadeDelete): {}, {}", fighterId, cascadeDelete);
        Assert.notNull(fighterId, "Fighter id must be not null!");

        Optional<Fighter> fighter = fighterRepository.findById(fighterId);

        if (!fighter.isPresent()) {
            throw new ApplicationException(
                    "Участник с id " + fighterId + " не найден!"
            );
        }

        List<Duel> duels = duelRepository.findByRedFighterOrBlueFighter(fighter.get(), fighter.get());

        if (CollectionUtils.isNotEmpty(duels) && !cascadeDelete) {
            throw new ApplicationException(
                    "Fighter wasn't be deleted, because there are duels related with it",
                    "Удалить участника и все сражения, в которых он участвовал?"
            );
        }

        if (cascadeDelete) {
            duelRepository.deleteInBatch(duels);
        }

        fighterRepository.deleteAllNominationLinksOfFighter(fighterId);
        fighterRepository.deleteById(fighterId);
    }

    private void validateFighterFields(Fighter fighter) {
        Assert.notNull(fighter, "Fighter must be not null!");
        Assert.isTrue(StringUtils.isNoneBlank(fighter.getFirstName()), "First name must be not blank!");
        Assert.isTrue(StringUtils.isNoneBlank(fighter.getLastName()), "Last name must be not blank!");
        Assert.isTrue(StringUtils.isNoneBlank(fighter.getParentName()), "Parent name must be not blank!");
        Assert.notNull(fighter.getSex(), "Sex must be not blank!");
        Assert.notNull(fighter.getBirthDate(), "Birth date must be not blank!");
    }

    private void fillFieldNullValue(Fighter fighter) {
        if (StringUtils.isBlank(fighter.getClub())) {
            fighter.setClub(null);
        }

        if (StringUtils.isBlank(fighter.getCity())) {
            fighter.setCity(null);
        }
    }

}