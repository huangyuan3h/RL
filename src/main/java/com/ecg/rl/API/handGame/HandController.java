package com.ecg.rl.API.handGame;

import com.ecg.rl.model.HandGameModel;
import com.ecg.rl.util.HandGameUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("hand")
public class HandController {



    @GetMapping("choose/{action}")
    public ResponseEntity<HandGameModel> doHandGame(@PathVariable String action) {

        HandGameModel model =new HandGameModel();
        model.setYourChoice(action);
        model.setComputerChoice(XiaoMin.ChooseAction());
        HandGameUtil.compare(model);

        return new ResponseEntity<HandGameModel>(model, HttpStatus.OK);
    }
}
