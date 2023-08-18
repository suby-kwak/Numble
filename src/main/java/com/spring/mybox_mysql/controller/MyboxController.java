package com.spring.mybox_mysql.controller;

import com.spring.mybox_mysql.entity.User;
import com.spring.mybox_mysql.service.MyboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/mybox")
public class MyboxController {
    @Autowired
    private MyboxService service;

    @GetMapping("/index")
    public String index(Model model) {
        User user = service.findById("rhkdbtj@naver.com").orElseThrow();

        model.addAttribute("user", user);

        return "index";
    }

    @PostMapping("/filesave")
    public String fileSave(@RequestParam("file") MultipartFile file) {
        try {
            service.saveFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/mybox/index";
    }

//    @GetMapping("/filedownload/{fileNo}")
//    public String fileDownload(@PathVariable("fileNo") long fileNo,
//                               Http) {
//
//
//        return "redirect:/mybox/index";
//    }
}
