package vrn.edinorog.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vrn.edinorog.domain.Fighter;
import vrn.edinorog.domain.Nomination;
import vrn.edinorog.dto.ResponseDto;
import vrn.edinorog.service.FighterService;
import vrn.edinorog.service.NominationService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("/fighter")
public class FighterController {

    private final FighterService fighterService;

    @GetMapping(
            path = "/all",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto getAllFighters() {
        log.debug("GET /fighter/all");
        return ResponseDto.create().data(fighterService.getAllFighters());
    }

    @PostMapping(
            path = "/add",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto addNewFighter(@RequestBody Fighter fighter) {
        log.debug("POST /management/add, RequestBody {}", fighter);
        fighterService.addNewFighter(fighter);
        return ResponseDto.create().message("Fighter was successfully added");
    }

    @PostMapping(
            path = "/add-all",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto addNewFighters(@RequestBody List<Fighter> fighters) {
        log.debug("POST /management/add-all, RequestBody {}", fighters);
        fighterService.addNewFighters(fighters);
        return ResponseDto.create().message("Fighters were successfully added");
    }

    @PostMapping(
            path = "/update-personal-data",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto updatePersonalData(@RequestBody Fighter fighter) {
        log.debug("POST /management/update-personal-data, RequestBody {}", fighter);
        fighterService.updateFighterPersonalData(fighter);
        return ResponseDto.create().message("Fighter personal data were successfully update");
    }

    @PostMapping(
            path = "/update-competition-data",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto updateFighterCompetitionData(@RequestBody Fighter fighter) {
        log.debug("POST /management/update-competition-data, RequestBody {}", fighter);
        fighterService.updateFighterCompetitionData(fighter);
        return ResponseDto.create().message("Fighter competition data were successfully update");
    }

    @PostMapping(
            path = "/{id}/update-nominations",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto updateNominations(@PathVariable("id") Long fighterId, @RequestBody List<Long> nominationIds) {
        log.debug("POST /management/{}/update-nominations, RequestBody {}", fighterId, nominationIds);
        fighterService.updateFighterNominations(fighterId, nominationIds);
        return ResponseDto.create().message("Fighter nominations were successfully update");
    }

}