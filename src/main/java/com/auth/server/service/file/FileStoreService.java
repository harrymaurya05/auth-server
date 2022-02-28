package com.auth.server.service.file;

import com.auth.server.models.Video;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by admin on 20/02/22.
 */
public interface FileStoreService {
    ResponseEntity<?> storeAndEncodeFile(MultipartFile inputfile);
    List<Video> fetchVideosByUser();
    List<Video> fetchVideos();
}
