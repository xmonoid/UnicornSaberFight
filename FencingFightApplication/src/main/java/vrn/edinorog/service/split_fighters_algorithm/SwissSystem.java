package vrn.edinorog.service.split_fighters_algorithm;

import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.stereotype.Service;
import vrn.edinorog.domain.Fighter;
import vrn.edinorog.enums.CompetitionStage;
import vrn.edinorog.utils.SimpleFighterGraph;

import java.util.*;

@Service(value = "SwissSystem")
public class SwissSystem implements SplitFighterInterface {

    @Override
    public List<MutablePair<Fighter, Fighter>> getFighterPairs(List<Fighter> fighters, Fighter skippedRoundFighter, CompetitionStage competitionStage) {
        List<Fighter> tempFighters = new ArrayList<>();
        if (true) {
            return new ArrayList<>();
        }
        Collections.copy(tempFighters, fighters);
        switch (competitionStage) {
            case INIT_STAGE:
                return getInitStageFighterPairs(tempFighters);
            case QUALIFYING_STAGE:
                return getQualifyingStageFighterPairs(tempFighters, skippedRoundFighter);
            case PLAY_OFF_STAGE:
                return getPlayOffStageFighterPairs(fighters);
        }
        return null;
    }

    private List<MutablePair<Fighter, Fighter>> getInitStageFighterPairs(List<Fighter> fighters) {
        List<MutablePair<Fighter, Fighter>> fightersPairs = new ArrayList<>();
        Collections.shuffle(fighters, new Random(new Date().getTime()));
        for (int pairCount = 0; pairCount < fighters.size() / 2; pairCount++) {
            Fighter redFighter = fighters.get(0);
            Fighter blueFighter = null;
            for (int ind = 1; ind < fighters.size(); ind++) {
                if (!fighters.get(ind).getFighterIdFromTheSameClub().contains(redFighter.getId())) {
                    blueFighter = fighters.get(ind);
                }
            }
            if (blueFighter == null) {
                blueFighter = fighters.get(1);
            }

            fightersPairs.add(new MutablePair<>(redFighter, blueFighter));

            fighters.remove(redFighter);
            fighters.remove(blueFighter);
        }

        if (fighters.size() > 0) {
            fightersPairs.add(new MutablePair<>(fighters.get(0), null));
        }

        return fightersPairs;
    }

    private List<MutablePair<Fighter, Fighter>> getQualifyingStageFighterPairs(List<Fighter> fighters, Fighter skippedRoundFighter) {
        List<MutablePair<Fighter, Fighter>> fightersPairs = new ArrayList<>();
        if (skippedRoundFighter != null) {
            fighters.remove(skippedRoundFighter);
        }

        Map<Integer, List<Fighter>> scoreToListFighterMap = new HashMap<>();

        for (Fighter fighter : fighters) {
            if (!scoreToListFighterMap.containsKey(fighter.getPoints())) {
                scoreToListFighterMap.put(fighter.getPoints(), new ArrayList<>());
            }
            scoreToListFighterMap.get(fighter.getPoints()).add(fighter);
        }

        List<Integer> scores = new ArrayList<>(scoreToListFighterMap.keySet());
        Collections.sort(scores);
        Collections.reverse(scores);

        if (skippedRoundFighter != null) {
            scoreToListFighterMap.get(scores.get(0)).add(skippedRoundFighter);
            scoreToListFighterMap.get(scores.get(scores.size() - 1)).add(skippedRoundFighter);
        }

        List<Fighter> fightersFromAnotherGroup = new ArrayList<>();
        for (int scoreInd = 0; scoreInd < scores.size(); scoreInd++) {
            List<Fighter> fightersGroup = scoreToListFighterMap.get(scores.get(scoreInd));
            SimpleFighterGraph simpleFighterGraph = new SimpleFighterGraph(fightersGroup, fightersFromAnotherGroup, false);
            List<MutablePair<Fighter, Fighter>> fightersPairsForOneGroup = simpleFighterGraph.getFighterPairs();
            fightersFromAnotherGroup = new ArrayList<>();
            for (MutablePair<Fighter, Fighter> fighterPair : fightersPairsForOneGroup) {
                if (fighterPair.getRight() == null) {
                    fightersFromAnotherGroup.add(fighterPair.getLeft());
                } else {
                    fightersPairs.add(fighterPair);
                }
            }
        }

        for (Fighter fighter : fightersFromAnotherGroup) {
            fightersPairs.add(new MutablePair<>(fighter, null));
        }

        return  fightersPairs;
    }

    private List<MutablePair<Fighter, Fighter>> getPlayOffStageFighterPairs(List<Fighter> fighters) {
        Collections.sort(fighters, Comparator.comparingInt(Fighter::getPoints));
        Collections.reverse(fighters);

        List<MutablePair<Fighter, Fighter>> fightersPairs = new ArrayList<>();
        if (fighters.size() % 2 != 0) {
            fightersPairs.add(new MutablePair<>(fighters.get(0), null));
        }

        SimpleFighterGraph simpleFighterGraph = new SimpleFighterGraph(fighters.subList(1, fighters.size()), null, true);
        fightersPairs.addAll(simpleFighterGraph.getFighterPairs());
        return fightersPairs;
    }

}