package vrn.edinorog.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vrn.edinorog.domain.Fighter;
import vrn.edinorog.domain.Nomination;
import vrn.edinorog.dto.DeleteObjectDto;
import vrn.edinorog.dto.ResponseDto;
import vrn.edinorog.exception.ApplicationException;
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
        log.debug("POST /fighter/add, RequestBody {}", fighter);
        fighterService.addNewFighter(fighter);
        return ResponseDto.create().message("Участник успешно добавлен");
    }

    @PostMapping(
            path = "/add-all",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto addNewFighters(@RequestBody List<Fighter> fighters) {
        log.debug("POST /fighter/add-all, RequestBody {}", fighters);
        fighterService.addNewFighters(fighters);
        return ResponseDto.create().message("Участники успешно добавлены");
    }

    @PostMapping(
            path = "/update-personal-data",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto updatePersonalData(@RequestBody Fighter fighter) {
        log.debug("POST /fighter/update-personal-data, RequestBody {}", fighter);
        fighterService.updateFighterPersonalData(fighter);
        return ResponseDto.create().message("Личные данные участника успешно изменены");
    }

    @PostMapping(
            path = "/update-competition-data",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto updateFighterCompetitionData(@RequestBody Fighter fighter) {
        log.debug("POST /fighter/update-competition-data, RequestBody {}", fighter);
        fighterService.updateFighterCompetitionData(fighter);
        return ResponseDto.create().message("Fighter competition data were successfully update");
    }

    @PostMapping(
            path = "/{id}/update-fighter-status",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto updateFighterStatus(@PathVariable("id") Long fighterId, @RequestBody boolean isActive) {
        log.debug("POST /fighter/{}/update-fighter-status, RequestBody {}", fighterId, isActive);
        fighterService.updateFighterStatus(fighterId, isActive);
        if (isActive) {
            return ResponseDto.create().message("Участие бойца подтверждено");
        } else {
            return ResponseDto.create().message("Участие бойца не подтверждено");
        }
    }

    @PostMapping(
            path = "/{id}/update-nominations",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto updateNominations(@PathVariable("id") Long fighterId, @RequestBody List<Long> nominationIds) {
        log.debug("POST /fighter/{}/update-nominations, RequestBody {}", fighterId, nominationIds);
        fighterService.updateFighterNominations(fighterId, nominationIds);
        return ResponseDto.create().message("Список номинаций успешно изменен");
    }

    @DeleteMapping(
            path = "/delete",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto deleteFighter(@RequestBody DeleteObjectDto deleteObjectDto) {
        log.debug("DELETE /fighter/delete, RequestBody {}", deleteObjectDto);
        try {
            fighterService.deleteFighter(
                    deleteObjectDto.getObjectId(),
                    deleteObjectDto.getCascadeDelete() != null ? deleteObjectDto.getCascadeDelete() : false
            );
            return ResponseDto.create().message("Участник успешно удалена");
        } catch (ApplicationException ex) {
            return ResponseDto.create().exception(ex).message(ex.getMessage());
        }
    }

}