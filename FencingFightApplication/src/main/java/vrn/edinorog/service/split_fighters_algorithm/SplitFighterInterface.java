package vrn.edinorog.service.split_fighters_algorithm;

import org.apache.commons.lang3.tuple.MutablePair;
import vrn.edinorog.domain.Fighter;
import vrn.edinorog.enums.CompetitionStage;

import java.util.List;

public interface SplitFighterInterface {

    List<MutablePair<Fighter, Fighter>> getFighterPairs(List<Fighter> fighters, Fighter skippedRoundFighter, CompetitionStage competitionStage);

}