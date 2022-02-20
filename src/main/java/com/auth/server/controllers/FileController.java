package com.auth.server.controllers;

import com.auth.server.service.file.FileStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by admin on 19/02/22.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired FileStoreService fileStoreService;

    @PostMapping(value="/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> allAccess(@RequestParam("file") MultipartFile inputfile) {
        if(!inputfile.isEmpty()){
            return fileStoreService.storeAndEncodeFile(inputfile);
        }
        return null;
    }
}
