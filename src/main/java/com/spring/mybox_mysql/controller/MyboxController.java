package com.spring.mybox_mysql.controller;

import com.spring.mybox_mysql.entity.User;
import com.spring.mybox_mysql.service.MyboxService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
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
    public String index(Model model,HttpSession session) {
        Object userId = session.getAttribute("userId");
        if(userId != null) {
            User user = service.findById(userId.toString()).orElseThrow();
            model.addAttribute("user", user);
        }

        return "index";
    }

    //회원가입
    @PostMapping("/signup")
    public String signup(@RequestParam("userID") String userId,
                         @RequestParam("password") String password,
                         @RequestParam("userName") String userName) {

        service.saveUser(userId, userName, password);

        return "redirect:/mybox/index";
    }

    @PostMapping("/login")
    public String login(@RequestParam("userID") String userId,
                        @RequestParam("password") String password,
                        HttpSession session) {

        User user = service.findById(userId).orElseThrow();

        if (user.getPassword().equals(password)) {
            session.setAttribute("userId",userId);
            session.setAttribute("userName",user.getUserName());
        }

        return "redirect:/mybox/index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "redirect:/mybox/index";
    }

    @PostMapping("/filesave")
    public String fileSave(@RequestParam("file") MultipartFile file, HttpSession session) {

        try {
            service.uploadfile(file, session.getAttribute("userId").toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/mybox/index";
    }

//    @GetMapping("/filedownload/{fileNo}")
//    public ResponseEntity<byte[]> fileDownload(@PathVariable("fileNo") Long fileNo) {
//        return service.downloadFile(fileNo);
//    }

    @GetMapping("/filedownload/{fileNo}")
    public ResponseEntity<Resource> fileDownload2(@PathVariable("fileNo") Long fileNo) throws IOException, InterruptedException {
        return service.download2(fileNo);
    }

}
