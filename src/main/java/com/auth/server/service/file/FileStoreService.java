package com.auth.server.service.file;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by admin on 20/02/22.
 */
public interface FileStoreService {
    ResponseEntity<?> storeAndEncodeFile(MultipartFile inputfile);
}
