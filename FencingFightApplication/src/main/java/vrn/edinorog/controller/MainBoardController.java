package vrn.edinorog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vrn.edinorog.dao.FighterRepository;

@RequiredArgsConstructor
@Controller
public class MainBoardController {

    private final FighterRepository fighterRepository;

    @GetMapping("/main-board")
    public String getFighters(Model model) {
        model.addAttribute("fighters", fighterRepository.findAll());
        return "main-board-page";
    }
}
