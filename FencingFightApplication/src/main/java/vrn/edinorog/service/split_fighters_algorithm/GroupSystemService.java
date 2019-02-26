package vrn.edinorog.service.split_fighters_algorithm;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.stereotype.Service;
import vrn.edinorog.domain.Fighter;
import vrn.edinorog.enums.CompetitionStage;

import java.util.List;

@Service(value = "GroupSystem")
public class GroupSystemService implements SplitFighterInterface {

    @Override
    public List<MutablePair<Fighter, Fighter>> getFighterPairs(List<Fighter> fighters, Fighter skippedRoundFighter, CompetitionStage competitionStage) {
        throw new NotImplementedException("Coming soon");
    }

}