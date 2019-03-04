package vrn.edinorog.service.split_fighters_algorithm;

import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.stereotype.Service;
import vrn.edinorog.domain.Duel;
import vrn.edinorog.domain.Fighter;
import vrn.edinorog.utils.SimpleFighterGraph;

import java.util.*;

@Service
public class OlympicSystem {

    public List<MutablePair<Fighter, Fighter>> getFighterPairs(List<Fighter> fighters, List<Duel> semiFinalDuels, boolean isNeedValidation) {
        List<Fighter> tempFighters = new ArrayList<>();
        if (fighters != null) {
            Collections.copy(tempFighters, fighters);
        }

        if(isNeedValidation && tempFighters.size() > 0) {
            if (tempFighters.size() < 3) {
                throw new IllegalArgumentException("Test"); //TODO fix message
            }

            if (tempFighters.size() != 3 && tempFighters.size() % 2 != 0) {
                throw new IllegalArgumentException("Test"); //TODO fix message
            }

            if (Integer.bitCount(tempFighters.size()) != 1 && (tempFighters.size() % 3 != 0 || Integer.bitCount(tempFighters.size() / 3) != 1)) {
                throw new IllegalArgumentException("Test"); //TODO fix message
            }
        } else  if (isNeedValidation) {
            if (semiFinalDuels.size() != 2) {
                throw new IllegalArgumentException("Test"); //TODO fix message
            }
        }

        if (tempFighters.size() == 3) {
            return getPairsFor3Fighters(tempFighters);
        }

        if (tempFighters.size() == 0) {
            return getPairsForFinal(semiFinalDuels);
        }

        return getPairs(tempFighters);
    }

    private List<MutablePair<Fighter, Fighter>> getPairsFor3Fighters(List<Fighter> fighters) {
        List<MutablePair<Fighter, Fighter>> result = new ArrayList<>();

        result.add(new MutablePair<>(fighters.get(0), fighters.get(1)));
        result.add(new MutablePair<>(fighters.get(0), fighters.get(2)));
        result.add(new MutablePair<>(fighters.get(1), fighters.get(2)));

        return result;
    }

    private List<MutablePair<Fighter, Fighter>> getPairsForFinal(List<Duel> semiFinalDuels) {
        List<MutablePair<Fighter, Fighter>> result = new ArrayList<>();

        result.add(new MutablePair<>(semiFinalDuels.get(0).getLoser(), semiFinalDuels.get(1).getLoser()));
        result.add(new MutablePair<>(semiFinalDuels.get(0).getWinner(), semiFinalDuels.get(1).getWinner()));

        return result;
    }

    private List<MutablePair<Fighter, Fighter>> getPairs(List<Fighter> fighters) {
        Collections.sort(fighters, Comparator.comparingInt(Fighter::getPoints));
        Collections.reverse(fighters);

        List<MutablePair<Fighter, Fighter>> fightersPairs = new ArrayList<>();
        if (fighters.size() % 2 != 0) {
            fightersPairs.add(new MutablePair<>(fighters.get(0), null));
            fighters.remove(0);
        }

        if (fighters.get(0).getPoints().equals(fighters.get(fighters.size() - 1).getPoints())) {
            Collections.shuffle(fighters, new Random(new Date().getTime()));
        }

        SimpleFighterGraph simpleFighterGraph = new SimpleFighterGraph(fighters, null, true);
        fightersPairs.addAll(simpleFighterGraph.getFighterPairs());
        return fightersPairs;
    }

}