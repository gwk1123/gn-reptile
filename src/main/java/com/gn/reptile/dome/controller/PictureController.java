package com.gn.reptile.dome.controller;

import com.gn.reptile.dome.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/td")
public class PictureController {

    @Autowired
    private PictureService pictureService;

    /**
     * 截图
     * @throws Exception
     */
    @GetMapping("/p")
    public void getCompared() throws Exception {
        pictureService.compared();
    }
}
