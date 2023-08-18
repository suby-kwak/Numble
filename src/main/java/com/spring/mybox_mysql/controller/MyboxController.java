package com.spring.mybox_mysql.controller;

import com.spring.mybox_mysql.entity.User;
import com.spring.mybox_mysql.entity.UserFile;
import com.spring.mybox_mysql.service.MyboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @GetMapping("/filedownload/{fileNo}")
    public ResponseEntity<Resource> fileDownload(@PathVariable("fileNo") Long fileNo) {
        Resource resource = null;
        HttpHeaders headers = null;
        try {
            UserFile file = service.findByNo(fileNo);
            Path path = Paths.get(file.getPath());
            resource = new InputStreamResource(Files.newInputStream(path));

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                                            .filename(URLEncoder.encode(file.getFileOriginName(),"UTF-8")).build());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }
}
