package vrn.edinorog.service.split_fighters_algorithm;

import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.stereotype.Service;
import vrn.edinorog.domain.Fighter;
import vrn.edinorog.enums.CompetitionStage;
import vrn.edinorog.utils.SimpleFighterGraph;

import java.util.*;

@Service
public class SwissSystem {

    public List<MutablePair<Fighter, Fighter>> getFighterPairs(List<Fighter> fighters, Fighter skippedRoundFighter, boolean isStrongSkipRound) {
        List<Fighter> tempFighters = new ArrayList<>();
        Collections.copy(tempFighters, fighters);

        int score = tempFighters.get(0).getPoints();
        boolean isNonRating = true;

        for (Fighter fighter : tempFighters) {
            if (score != fighter.getPoints()) {
                isNonRating = false;
                break;
            }
        }

        if (isNonRating) {
            return getRandomFighterPairs(tempFighters);
        } else {
            return getByRatingFighterPairs(tempFighters, skippedRoundFighter, isStrongSkipRound);
        }
    }

    private List<MutablePair<Fighter, Fighter>> getRandomFighterPairs(List<Fighter> fighters) {
        Collections.shuffle(fighters, new Random(new Date().getTime()));
        SimpleFighterGraph simpleFighterGraph = new SimpleFighterGraph(fighters, null, false);
        List<MutablePair<Fighter, Fighter>> fightersPairs = simpleFighterGraph.getFighterPairs();
        return fightersPairs;
    }

    private List<MutablePair<Fighter, Fighter>> getByRatingFighterPairs(List<Fighter> fighters, Fighter skippedRoundFighter, boolean isStrongSkipRound) {
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

        Fighter fighterForSkip = null;

        if (fighters.size() % 2 != 0 && isStrongSkipRound) {
            fighterForSkip = scoreToListFighterMap.get(scores.get(0)).get(0);
            scoreToListFighterMap.get(scores.get(0)).remove(fighterForSkip);
        }

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

        if (fighterForSkip != null) {
            fightersPairs.add(new MutablePair<>(fighterForSkip, null));
        }

        for (Fighter fighter : fightersFromAnotherGroup) {
            fightersPairs.add(new MutablePair<>(fighter, null));
        }

        return  fightersPairs;
    }

}