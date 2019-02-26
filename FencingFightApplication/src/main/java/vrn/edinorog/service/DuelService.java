package vrn.edinorog.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import vrn.edinorog.dao.DuelRepository;
import vrn.edinorog.domain.Duel;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class DuelService {

    private final DuelRepository duelRepository;

    public List<Duel> getAllDuels() {
        log.debug("#getAllDuels()");
        return duelRepository.findAll();
    }

    @Transactional
    public void addNewDuel(Duel duel) {
        log.debug("#addNewDuel(Duel duel): {}", duel);
        Assert.notNull(duel, "Duel must be not null!");
        duelRepository.save(duel);
    }

    @Transactional
    public void addNewDuels(List<Duel> duels) {
        log.debug("#addNewNominations(List<Duel> duels): {}", duels);
        Assert.isTrue(CollectionUtils.isNotEmpty(duels), "Duels must be not empty");
        duelRepository.saveAll(duels);
    }

    @Transactional
    public void updateDuelFighters(Long duelId, Long redFighterId, Long blueFighterId) {
        log.debug("#updateDuelFighters(Long duelId, Long redFighterId, Long blueFighterId): {}, {}, {}", duelId, redFighterId, blueFighterId);
        Assert.notNull(duelId, "Duel id must be not null!");
        Assert.notNull(redFighterId, "Red fighter id must be not null!");
        Assert.notNull(blueFighterId, "Blue fighter id must be not null!");
        duelRepository.updateFighterCompetitionData(duelId, redFighterId, blueFighterId);
    }

    @Transactional
    public void updateDuelResult(Duel duel) {
        log.debug("#updateDuelResult(Duel duel): {}", duel);
        Assert.notNull(duel, "Duel must be not null!");
        duelRepository.updateDuelResult(duel);
    }

    @Transactional
    public void updateDuelResult(Long duelId, Duel.DuelStatus duelStatus) {
        log.debug("#updateDuelResult(Long duelId, Duel.DuelStatus duelStatus): {}, {}", duelId, duelStatus);
        Assert.notNull(duelId, "Duel id must be not null!");
        Assert.notNull(duelStatus, "Duel status must be not null!");
        duelRepository.updateDuelStatus(duelId, duelStatus);
    }

    @Transactional
    public void deleteDuel(Long duelId) {
        log.debug("#deleteDuel(Long duelId): {}", duelId);
        Assert.notNull(duelId, "Duel id must be not null!");
        duelRepository.deleteById(duelId);
    }

    @Transactional
    public void deleteDuels(List<Duel> duels) {
        log.debug("#deleteDuels(List<Duel> duels): {}", duels);
        Assert.notNull(duels, "Duels collection must be not null!");
        duelRepository.deleteInBatch(duels);
    }

}