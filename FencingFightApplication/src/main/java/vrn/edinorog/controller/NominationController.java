package vrn.edinorog.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vrn.edinorog.domain.Nomination;
import vrn.edinorog.dto.DeleteObjectDto;
import vrn.edinorog.dto.ResponseDto;
import vrn.edinorog.exception.ApplicationException;
import vrn.edinorog.service.NominationService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("/nomination")
public class NominationController {

    private final NominationService nominationService;

    @GetMapping(
            path = "/all",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto getAllNominations() {
        log.debug("GET /nomination/all");
        return ResponseDto.create().data(nominationService.getAllNominations());
    }

    @PostMapping(
            path = "/add",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto addNewNomination(@RequestBody Nomination nomination) {
        log.debug("POST /nomination/add, RequestBody {}", nomination);
        nominationService.addNewNomination(nomination);
        return ResponseDto.create().message("Номинация успешно добавлена");
    }

    @PostMapping(
            path = "/add-all",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto addNewNominations(@RequestBody List<Nomination> nominations) {
        log.debug("POST /nomination/add-all, RequestBody {}", nominations);
        nominationService.addNewNominations(nominations);
        return ResponseDto.create().message("Номинации успешно добавлены");
    }

    @PostMapping(
            path = "/{id}/update-name",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto updateName(@PathVariable("id") Long id, @RequestBody String name) {
        log.debug("POST /nomination/{}/update-name, RequestBody {}", id, name);
        nominationService.updateNominationName(id, name);
        return ResponseDto.create().message("Имя номинации успешно изменено");
    }

    @PostMapping(
            path = "/update-current-stage",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto updateCurrentStage(@RequestBody Nomination nomination) {
        log.debug("POST /nomination/update-current-stage, RequestBody {}", nomination);
        nominationService.updateNominationCurrentSateAndRoundIndex(nomination.getId(), nomination.getCurrentStage(), nomination.getCurrentRoundIndex());
        return ResponseDto.create().message("Текущий этап соревнований успешно изменен");
    }

    @DeleteMapping(
            path = "/delete",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto deleteNomination(@RequestBody DeleteObjectDto deleteObjectDto) {
        log.debug("DELETE /nomination/delete, RequestBody {}", deleteObjectDto);
        try {
            nominationService.deleteNomination(
                    deleteObjectDto.getObjectId(),
                    deleteObjectDto.getCascadeDelete() != null ? deleteObjectDto.getCascadeDelete() : false
            );
            return ResponseDto.create().message("Номинация успешно удалена");
        } catch (ApplicationException ex) {
            return ResponseDto.create().exception(ex).message(ex.getMessage());
        }
    }
}