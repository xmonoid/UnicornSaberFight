package vrn.edinorog.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import vrn.edinorog.dto.ChangeScoreDto;

@Controller
public class FightController {

    @MessageMapping("/secretary/change-score")
    @SendTo("/fight-information/fencing-fight-app/board/change-score")
    public ChangeScoreDto changeScore(ChangeScoreDto changeScoreDto) throws Exception {
        return changeScoreDto;
    }

}