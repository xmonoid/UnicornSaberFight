package vrn.edinorog.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import vrn.edinorog.dto.ChangeMutualHitCountDto;
import vrn.edinorog.dto.ChangeScoreDto;
import vrn.edinorog.dto.NamesDto;
import vrn.edinorog.dto.TimeDto;

@Controller
public class FightController {

    @MessageMapping("/secretary/change-score")
    @SendTo("/fight-information/fencing-fight-app/board/change-score")
    public ChangeScoreDto changeScore(ChangeScoreDto changeScoreDto) {
        return changeScoreDto;
    }

    @MessageMapping("/secretary/change-time")
    @SendTo("/fight-information/fencing-fight-app/board/change-time")
    public TimeDto changeTime(TimeDto timeDto) {
        return timeDto;
    }

    @MessageMapping("/secretary/set-names")
    @SendTo("/fight-information/fencing-fight-app/board/set-names")
    public NamesDto setNames(NamesDto namesDto) {
        return namesDto;
    }

    @MessageMapping("/secretary/change-mutual-hit-count")
    @SendTo("/fight-information/fencing-fight-app/board/change-mutual-hit-count")
    public ChangeMutualHitCountDto changeMitualHitCount(ChangeMutualHitCountDto changeMutualHitCountDto) {
        return changeMutualHitCountDto;
    }
}