package vrn.edinorog.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vrn.edinorog.domain.Nomination;
import vrn.edinorog.dto.ResponseDto;
import vrn.edinorog.service.NominationService;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("/management")
public class ManagementController {

    private final NominationService nominationService;

    @PostMapping(
            path = "/add-nomination",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto addNewNomination(@RequestBody Nomination nomination) {
        log.debug("POST /management/add-nomination, RequestBody {}", nomination);
        nominationService.addNewNomination(nomination);
        return ResponseDto.create().message("Nomination was successfully added");
    }

    @GetMapping(
            path = "/nominations",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto getAllNominations() {
        log.debug("GET /management/nominations");
        return ResponseDto.create().data(nominationService.getAllNominations());
    }

    @PostMapping(
            path = "/{id}/update-name",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto updateName(@PathVariable("id") Long id, @RequestBody String name) {
        log.debug("POST /management/{}/update-name, RequestBody {}", id, name);
        nominationService.updateNominationName(id, name);
        return ResponseDto.create().message("Nomination was successfully update");
    }
}