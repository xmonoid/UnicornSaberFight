package vrn.edinorog.service;

import org.springframework.stereotype.Service;
import vrn.edinorog.dao.DuelRepository;
import vrn.edinorog.dao.FighterRepository;
import vrn.edinorog.dao.NominationRepository;
import vrn.edinorog.domain.Duel;
import vrn.edinorog.domain.Fighter;
import vrn.edinorog.domain.Nomination;
import vrn.edinorog.enums.CompetitionStage;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
public class ManagementService {

    private final NominationRepository nominationRepository;
    private final DuelRepository duelRepository;
    private final FighterRepository fighterRepository;

    public ManagementService(
            NominationRepository nominationRepository,
            DuelRepository duelRepository,
            FighterRepository fighterRepository
    ) {
        this.nominationRepository = nominationRepository;
        this.duelRepository = duelRepository;
        this.fighterRepository = fighterRepository;
    }

    public void addNomination(Nomination nomination) {
        nominationRepository.saveAndFlush(nomination);
    }

    public List<Nomination> getAllNominations() {
        return nominationRepository.findAll();
    }

//    public void test() {
//        Nomination nomination = new Nomination("Test");
//        nomination.setCurrentRoundIndex(1);
//        nomination.setCurrentStage(CompetitionStage.QUALIFYING_STAGE);
//
//        Fighter fighter1 = new Fighter(
//                "qwe",
//                "qew",
//                "qqwe",
//                true,
//                new Date().getTime(),
//                "asd",
//                "asd",
//                new HashSet<>(Arrays.asList(nomination))
//        );
//
//        Fighter fighter2 = new Fighter(
//                "qwe2",
//                "qew2",
//                "qqwe2",
//                true,
//                new Date().getTime(),
//                "asd2",
//                "asd2",
//                new HashSet<>(Arrays.asList(nomination))
//        );
//
//        Duel duel = new Duel(
//                fighter1,
//                fighter2,
//                1,
//                1,
//                nomination,
//                CompetitionStage.QUALIFYING_STAGE,
//                1
//        );
//
//        nominationRepository.saveAndFlush(nomination);
//        fighterRepository.saveAndFlush(fighter1);
//        fighterRepository.saveAndFlush(fighter2);
//        duelRepository.saveAndFlush(duel);
//
//        duelRepository.findByRedFighterOrBlueFighter(fighter1, fighter1);
//    }
}