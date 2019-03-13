package vrn.edinorog.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import vrn.edinorog.domain.Duel;
import vrn.edinorog.domain.Fighter;
import vrn.edinorog.domain.Nomination;
import vrn.edinorog.dto.DuelsManagementDto;
import vrn.edinorog.exception.ApplicationException;
import vrn.edinorog.service.split_fighters_algorithm.SwissSystem;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@AllArgsConstructor
@Service
public class ManagementService {

    private final NominationService nominationService;
    private final FighterService fighterService;
    private final DuelService duelService;
    private final SwissSystem swissSystem;

    private final Map<Integer, List<Duel>> playgroundNumberToDuelsMap = new HashMap<>();
    private final Map<Integer, List<Duel>> playgroundNumberToFinishedDuelsMap = new HashMap<>();

    @PostConstruct
    public void init() {
        playgroundNumberToDuelsMap.put(1, new ArrayList<>());
        playgroundNumberToDuelsMap.put(2, new ArrayList<>());

        playgroundNumberToFinishedDuelsMap.put(1, new ArrayList<>());
        playgroundNumberToFinishedDuelsMap.put(2, new ArrayList<>());
    }

    public List<Fighter> getFightersByNomination(Long nominationId) {
        log.debug("#getFightersByNomination(Long nominationId), {}", nominationId);
        Assert.notNull(nominationId, "Nomination id must be not null!");

        Nomination nomination = nominationService.getNominationById(nominationId);

        List<Fighter> fighters = fighterService.getFightersByNomination(nomination);
        setPoints(fighters, nomination);
        return fighters;
    }

    public void createDuelsForNewQualifyingRound(Long nominationId, Integer numberPlayground) {
        Nomination nomination = nominationService.getNominationById(nominationId);
        List<Duel> duels = duelService.getDuelsByNominationAndDuelStatus(nomination, Duel.DuelStatus.UNFINISHED);

        if (CollectionUtils.isNotEmpty(duels)) {
            throw new ApplicationException("Нельзя составить новые пары, потому что еще не все текущие бои завершены!");
        }

        List<Fighter> fighters = fighterService.getFightersByNomination(nomination);

        duels = duelService.getDuelsByNominationAndCompetitionStageAndRound(nomination, nomination.getCurrentStage(), nomination.getCurrentRoundIndex());

        List<Fighter> skippedRoundFighters = new ArrayList<>();
        for (Duel duel : duels) {
            if (duel.getBlueFighter() == null) {
                skippedRoundFighters.add(duel.getRedFighter());
            } else if (duel.getDuelStatus().equals(Duel.DuelStatus.CANCELED)) {
                skippedRoundFighters.add(duel.getWinner());
            }
        }

        duels = duelService.getDuelsByNominationAndDuelStatus(nomination, Duel.DuelStatus.FINISHED);
        fillRivals(fighters, duels);
        fillFighterIdFromTheSameClubList(fighters);

        List<MutablePair<Fighter, Fighter>> pairs = swissSystem.getFighterPairs(fighters, skippedRoundFighters, true);

        if (CollectionUtils.isEmpty(pairs)) {
            throw new ApplicationException("Список пар пустой!");
        }

        if (!checkPairs(pairs)) {
            throw new ApplicationException("Составить пары для отборочного круга нельзя без нарушения правила, что бойцы два раза не могут встречаться друг с другом!");
        }

        List<Duel> newDuels = new ArrayList<>();
        for (MutablePair<Fighter, Fighter> pair : pairs) {
            Duel duel = new Duel(pair.left, pair.right, 1, nomination, nomination.getCurrentStage(), nomination.getCurrentRoundIndex() + 1);
            newDuels.add(duel);
        }

        Map<Integer, List<Duel>> playGroundNumberToDuelsMap = new HashMap<>();
        List<DuelsManagementDto> duelsManagementDtos = new ArrayList<>();
        if (numberPlayground == 1 || numberPlayground == 2) {
            playGroundNumberToDuelsMap.put(numberPlayground, newDuels);

            if (CollectionUtils.isNotEmpty(this.playgroundNumberToDuelsMap.get(numberPlayground))) {
                throw new ApplicationException(String.format("Нельзя развести новые пары, потому что на %s площадке не закончились бои", numberPlayground));
            }

            DuelsManagementDto duelsManagementDto = new DuelsManagementDto();
            duelsManagementDto.setMethodType(DuelsManagementDto.MethodType.CLEAR_FINIS_DUELS_LIST);
            duelsManagementDto.setPlayGroundNumber(numberPlayground);
            duelsManagementDtos.add(duelsManagementDto);

        } else {
            playGroundNumberToDuelsMap.put(1, newDuels.subList(0, newDuels.size() / 2));
            playGroundNumberToDuelsMap.put(2, newDuels.subList(newDuels.size() / 2, newDuels.size()));

            if (CollectionUtils.isNotEmpty(this.playgroundNumberToDuelsMap.get(1))) {
                throw new ApplicationException(String.format("Нельзя развести новые пары, потому что на %s площадке не закончились бои", 1));
            }

            if (CollectionUtils.isNotEmpty(this.playgroundNumberToDuelsMap.get(2))) {
                throw new ApplicationException(String.format("Нельзя развести новые пары, потому что на %s площадке не закончились бои", 2));
            }

            DuelsManagementDto duelsManagementDto = new DuelsManagementDto();
            duelsManagementDto.setMethodType(DuelsManagementDto.MethodType.CLEAR_FINIS_DUELS_LIST);
            duelsManagementDto.setPlayGroundNumber(1);
            duelsManagementDtos.add(duelsManagementDto);

            duelsManagementDto = new DuelsManagementDto();
            duelsManagementDto.setMethodType(DuelsManagementDto.MethodType.CLEAR_FINIS_DUELS_LIST);
            duelsManagementDto.setPlayGroundNumber(2);
            duelsManagementDtos.add(duelsManagementDto);
        }

        DuelsManagementDto duelsManagementDto = new DuelsManagementDto();
        duelsManagementDto.setMethodType(DuelsManagementDto.MethodType.ADD_DUELS);
        duelsManagementDto.setPlayGroundNumberToDuelsMap(playGroundNumberToDuelsMap);
        duelsManagementDtos.add(duelsManagementDto);

        applyMethodToPlaygroundNumberToDuelsMap(duelsManagementDtos);

        duelService.addNewDuels(newDuels);
        nominationService.updateNominationCurrentSateAndRoundIndex(nominationId, nomination.getCurrentStage(), nomination.getCurrentRoundIndex() + 1);
    }

    public Map<Integer, List<Duel>> getCurrentDuelsByNomination(Long nominationId) {

        Map<Integer, List<Duel>> result = new HashMap<>();

        for (Map.Entry<Integer, List<Duel>> playgroundNumberToDuelList : playgroundNumberToFinishedDuelsMap.entrySet()) {
            result.put(playgroundNumberToDuelList.getKey(), new ArrayList<>());

            if (CollectionUtils.isNotEmpty(playgroundNumberToDuelList.getValue()) &&
                    playgroundNumberToDuelList.getValue().get(0).getNomination().getId().equals(nominationId)) {
                result.get(playgroundNumberToDuelList.getKey()).addAll(playgroundNumberToDuelList.getValue());
            }
        }

        for (Map.Entry<Integer, List<Duel>> playgroundNumberToDuelList : playgroundNumberToDuelsMap.entrySet()) {
            if (CollectionUtils.isNotEmpty(playgroundNumberToDuelList.getValue()) &&
                    playgroundNumberToDuelList.getValue().get(0).getNomination().getId().equals(nominationId)) {
                result.get(playgroundNumberToDuelList.getKey()).addAll(playgroundNumberToDuelList.getValue());
            }
        }
        return result;
    }

    public Map<Integer, List<Duel>> getCurrentDuels() {

        Map<Integer, List<Duel>> result = new HashMap<>();

        for (Map.Entry<Integer, List<Duel>> playgroundNumberToDuelList : playgroundNumberToFinishedDuelsMap.entrySet()) {
            result.put(playgroundNumberToDuelList.getKey(), new ArrayList<>());
            result.get(playgroundNumberToDuelList.getKey()).addAll(playgroundNumberToDuelList.getValue());
        }

        for (Map.Entry<Integer, List<Duel>> playgroundNumberToDuelList : playgroundNumberToDuelsMap.entrySet()) {
            result.get(playgroundNumberToDuelList.getKey()).addAll(playgroundNumberToDuelList.getValue());
        }
        return result;
    }

    public Map<Integer, List<Duel>> loadCurrentDuelsByNomination(Long nominationId, Integer numberPlayground) {

        Map<Integer, List<Duel>> playgroundNumberToDuelsMap = new HashMap<>();
        Map<Integer, List<Duel>> playgroundNumberToFinishedDuelsMap = new HashMap<>();

        Nomination nomination = nominationService.getNominationById(nominationId);
        List<Duel> duels = duelService.getDuelsByNominationAndCompetitionStageAndRound(nomination, nomination.getCurrentStage(), nomination.getCurrentRoundIndex());

        List<DuelsManagementDto> duelsManagementDtos = new ArrayList<>();
        if (numberPlayground == 1 || numberPlayground == 2) {
            if (CollectionUtils.isNotEmpty(this.playgroundNumberToDuelsMap.get(numberPlayground))) {
                throw new ApplicationException(String.format("Нельзя загрузить новые пары, потому что на %s площадке не закончились бои", numberPlayground));
            }

            DuelsManagementDto duelsManagementDto = new DuelsManagementDto();
            duelsManagementDto.setMethodType(DuelsManagementDto.MethodType.CLEAR_FINIS_DUELS_LIST);
            duelsManagementDto.setPlayGroundNumber(numberPlayground);
            duelsManagementDtos.add(duelsManagementDto);

            playgroundNumberToDuelsMap.put(numberPlayground, new ArrayList<>());
            playgroundNumberToFinishedDuelsMap.put(numberPlayground, new ArrayList<>());

            for (Duel duel : duels) {
                if (duel.getDuelStatus().equals(Duel.DuelStatus.UNFINISHED)) {
                    playgroundNumberToDuelsMap.get(numberPlayground).add(duel);
                } else {
                    playgroundNumberToFinishedDuelsMap.get(numberPlayground).add(duel);
                }
            }

        } else {

            if (CollectionUtils.isNotEmpty(this.playgroundNumberToDuelsMap.get(1))) {
                throw new ApplicationException(String.format("Нельзя загрузить новые пары, потому что на %s площадке не закончились бои", 1));
            }

            if (CollectionUtils.isNotEmpty(this.playgroundNumberToDuelsMap.get(2))) {
                throw new ApplicationException(String.format("Нельзя загрузить новые пары, потому что на %s площадке не закончились бои", 2));
            }

            DuelsManagementDto duelsManagementDto = new DuelsManagementDto();
            duelsManagementDto.setMethodType(DuelsManagementDto.MethodType.CLEAR_FINIS_DUELS_LIST);
            duelsManagementDto.setPlayGroundNumber(1);
            duelsManagementDtos.add(duelsManagementDto);

            duelsManagementDto = new DuelsManagementDto();
            duelsManagementDto.setMethodType(DuelsManagementDto.MethodType.CLEAR_FINIS_DUELS_LIST);
            duelsManagementDto.setPlayGroundNumber(2);
            duelsManagementDtos.add(duelsManagementDto);

            List<Duel> finishedDuels = new ArrayList<>();
            Iterator<Duel> duelIterator = duels.iterator();

            while (duelIterator.hasNext()) {
                Duel duel = duelIterator.next();
                if (!duel.getDuelStatus().equals(Duel.DuelStatus.UNFINISHED)) {
                    finishedDuels.add(duel);
                    duelIterator.remove();
                }
            }

            playgroundNumberToDuelsMap.put(1, new ArrayList<>());
            playgroundNumberToFinishedDuelsMap.put(1, new ArrayList<>());

            playgroundNumberToDuelsMap.put(2, new ArrayList<>());
            playgroundNumberToFinishedDuelsMap.put(2, new ArrayList<>());

            playgroundNumberToDuelsMap.put(1, duels.subList(0, duels.size() / 2));
            playgroundNumberToDuelsMap.put(2, duels.subList(duels.size() / 2, duels.size()));

            playgroundNumberToFinishedDuelsMap.put(1, finishedDuels.subList(0, finishedDuels.size() / 2));
            playgroundNumberToFinishedDuelsMap.put(2, finishedDuels.subList(finishedDuels.size() / 2, finishedDuels.size()));
        }

        DuelsManagementDto duelsManagementDto = new DuelsManagementDto();
        duelsManagementDto.setMethodType(DuelsManagementDto.MethodType.ADD_DUELS);
        duelsManagementDto.setPlayGroundNumberToDuelsMap(playgroundNumberToDuelsMap);
        duelsManagementDto.setPlayGroundNumberToFinishedDuelsMap(playgroundNumberToFinishedDuelsMap);
        duelsManagementDtos.add(duelsManagementDto);

        applyMethodToPlaygroundNumberToDuelsMap(duelsManagementDtos);

        Map<Integer, List<Duel>> result = new HashMap<>();

        result.put(1, new ArrayList<>());
        result.put(2, new ArrayList<>());

        result.get(1).addAll(playgroundNumberToFinishedDuelsMap.get(1));
        result.get(2).addAll(playgroundNumberToFinishedDuelsMap.get(2));

        result.get(1).addAll(playgroundNumberToDuelsMap.get(1));
        result.get(2).addAll(playgroundNumberToDuelsMap.get(2));

        return result;
    }

    private void setPoints(List<Fighter> fighters, Nomination nomination) {
        List<Duel> duels = duelService.getQualifyingDuelsByNomination(nomination);

        Map<Long, Fighter> fighterIdToFighterMap = new HashMap<>();
        for (Fighter fighter : fighters) {
            fighterIdToFighterMap.put(fighter.getId(), fighter);
        }

        for (Duel duel : duels) {
            if (!duel.getDuelStatus().equals(Duel.DuelStatus.FINISHED)) {
                continue;
            }
            if (duel.getResult().equals(Duel.Winner.DRAW)) {
                fighterIdToFighterMap.get(duel.getRedFighter().getId()).incPoints(1);
                fighterIdToFighterMap.get(duel.getBlueFighter().getId()).incPoints(1);
            } else if (
                    duel.getResult().equals(Duel.Winner.RED_TECHNICAL_WIN) ||
                    duel.getResult().equals(Duel.Winner.BLUE_TECHNICAL_WIN) ||
                    duel.getResult().equals(Duel.Winner.RED) ||
                    duel.getResult().equals(Duel.Winner.BLUE
            )) {
                fighterIdToFighterMap.get(duel.getWinner().getId()).incPoints(duel.getWinnerPoints());
            }
        }
    }

    private void fillRivals(List<Fighter> fighters, List<Duel> duels) {
        Map<Long, Fighter> fighterIdToFighterMap = new HashMap<>();
        for (Fighter fighter : fighters) {
            fighter.setRivalIds(new HashSet<>());
            fighterIdToFighterMap.put(fighter.getId(), fighter);
        }

        for (Duel duel : duels) {
            if (duel.getRedFighter() != null && duel.getBlueFighter() != null) {
                fighterIdToFighterMap.get(duel.getRedFighter().getId()).getRivalIds().add(duel.getBlueFighter().getId());
                fighterIdToFighterMap.get(duel.getBlueFighter().getId()).getRivalIds().add(duel.getRedFighter().getId());
            }
        }
    }

    private void fillFighterIdFromTheSameClubList(List<Fighter> fighters) {
        Map<String, List<Fighter>> clubNameToFightersMap = new HashMap<>();
        for (Fighter fighter : fighters) {
            fighter.setFighterIdFromTheSameClub(new HashSet<>());

            if (StringUtils.isNoneBlank(fighter.getClub())) {
                continue;
            }

            if (!clubNameToFightersMap.containsKey(fighter.getClub())) {
                clubNameToFightersMap.put(fighter.getClub(), new ArrayList<>());
            }

            clubNameToFightersMap.get(fighter.getClub()).add(fighter);
        }

        for (List<Fighter> fightersFromTheSameClub : clubNameToFightersMap.values()) {
            Set<Long> ids = new HashSet<>();
            for (Fighter fighter : fightersFromTheSameClub) {
                ids.add(fighter.getId());
            }

            for (Fighter fighter : fightersFromTheSameClub) {
                fighter.getFighterIdFromTheSameClub().addAll(ids);
                fighter.getFighterIdFromTheSameClub().remove(fighter.getId());
            }
        }
    }

    private synchronized void applyMethodToPlaygroundNumberToDuelsMap(List<DuelsManagementDto> duelsManagementDtos) {
        for (DuelsManagementDto duelsManagementDto : duelsManagementDtos) {
            switch (duelsManagementDto.getMethodType()) {
                case ADD_DUELS:
                    for (Map.Entry<Integer, List<Duel>> entry : duelsManagementDto.getPlayGroundNumberToDuelsMap().entrySet()) {
                        playgroundNumberToDuelsMap.get(entry.getKey()).addAll(entry.getValue());
                    }

                    if (duelsManagementDto.getPlayGroundNumberToFinishedDuelsMap() != null) {
                        for (Map.Entry<Integer, List<Duel>> entry : duelsManagementDto.getPlayGroundNumberToFinishedDuelsMap().entrySet()) {
                            playgroundNumberToFinishedDuelsMap.get(entry.getKey()).addAll(entry.getValue());
                        }
                    }
                    break;
                case FINISH_DUEL:
                    //TODO implement
                    break;
                case CHANGE_PLAYGROUND_NUMBER:
                    //TODO implement
                    break;
                case CLEAR_FINIS_DUELS_LIST:
                    playgroundNumberToDuelsMap.get(duelsManagementDto.getPlayGroundNumber()).clear();
                    break;
                default:
                    throw new ApplicationException(String.format("Unexpected Method type: %s", duelsManagementDto.getMethodType()));
            }
        }
    }

    private boolean checkPairs(List<MutablePair<Fighter, Fighter>> pairs) {
        int countFailedPairs = 0;
        for (MutablePair<Fighter, Fighter> pair : pairs) {
            if (pair.right == null) {
                countFailedPairs++;
            }
            if (countFailedPairs > 1) {
                return false;
            }
        }
        return true;
    }
}