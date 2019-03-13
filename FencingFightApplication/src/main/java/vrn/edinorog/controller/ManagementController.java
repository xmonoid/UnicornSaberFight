package vrn.edinorog.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vrn.edinorog.domain.Nomination;
import vrn.edinorog.dto.ResponseDto;
import vrn.edinorog.service.ManagementService;
import vrn.edinorog.service.NominationService;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("/management")
public class ManagementController {

    private final ManagementService managementService;

    @GetMapping("/{id}/nomination")
    public String getNominationPage(@PathVariable("id") Long nominationId) {
        log.debug("GET /management/{id}/nomination", nominationId);
        return "../static/html/management-nomination.html";
    }

    @GetMapping(
            path = "/{id}/fighters",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto getAllFightersByNomination(@PathVariable("id")Long nominationId) {
        log.debug("GET /management/{id}/fighters", nominationId);
        return ResponseDto.create().data(managementService.getFightersByNomination(nominationId));
    }

    @PostMapping(
            path = "/{id}/create-duels",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto createDuelsByNomination(@PathVariable("id")Long nominationId, @RequestBody Integer numberPlayground) {
        log.debug("POST /management/{id}/create-duels: RequestBody {}", nominationId, numberPlayground);
        managementService.createDuelsForNewQualifyingRound(nominationId, numberPlayground);
        return ResponseDto.create().message("Пары успешно разведены");
    }

    @PostMapping(
            path = "/{id}/load-duels",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto loadDuelsByNomination(@PathVariable("id")Long nominationId, @RequestBody Integer numberPlayground) {
        log.debug("POST /management/{id}/load-duels: RequestBody {}", nominationId, numberPlayground);
        return ResponseDto.create().data(managementService.loadCurrentDuelsByNomination(nominationId, numberPlayground));
    }

    @GetMapping(
            path = "/{id}/duels",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseDto getCurrentDuelsByNomination(@PathVariable("id")Long nominationId) {
        log.debug("GET /management/{id}/duels", nominationId);
        return ResponseDto.create().data(managementService.getCurrentDuelsByNomination(nominationId));
    }

}