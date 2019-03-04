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
}