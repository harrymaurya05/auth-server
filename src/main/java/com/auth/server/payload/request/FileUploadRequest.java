package com.auth.server.payload.request;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by admin on 19/02/22.
 */
public class FileUploadRequest {
    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
    public boolean isEmpty(){
        return file.isEmpty();
    }
}
