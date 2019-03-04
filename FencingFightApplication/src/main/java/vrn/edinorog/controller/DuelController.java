package vrn.edinorog.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vrn.edinorog.domain.Fighter;
import vrn.edinorog.domain.Nomination;
import vrn.edinorog.dto.ResponseDto;
import vrn.edinorog.service.DuelService;
import vrn.edinorog.service.FighterService;
import vrn.edinorog.service.NominationService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("/duel")
public class DuelController {

    private final DuelService duelService;

    @GetMapping(
            path = "/all",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto getAllDuels() {
        log.debug("GET /duel/all");
        return ResponseDto.create().data(duelService.getAllDuels());
    }

}