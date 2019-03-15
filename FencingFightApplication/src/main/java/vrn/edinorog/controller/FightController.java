package vrn.edinorog.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import vrn.edinorog.dto.ChangeScoreDto;
import vrn.edinorog.dto.NamesDto;
import vrn.edinorog.dto.TimeDto;

@Controller
public class FightController {

    @MessageMapping("/secretary/change-score/{id}")
    @SendTo("/fight-information/fencing-fight-app/board/change-score/{id}")
    public ChangeScoreDto changeScore(ChangeScoreDto changeScoreDto) {
        return changeScoreDto;
    }

    @MessageMapping("/secretary/change-time/{id}")
    @SendTo("/fight-information/fencing-fight-app/board/change-time/{id}")
    public TimeDto changeTime(TimeDto timeDto) {
        return timeDto;
    }

    @MessageMapping("/secretary/set-names/{id}")
    @SendTo("/fight-information/fencing-fight-app/board/set-names/{id}")
    public NamesDto setNames(NamesDto namesDto) {
        return namesDto;
    }
}