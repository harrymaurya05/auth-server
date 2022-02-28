package com.auth.server.repository;

import com.auth.server.models.VideoEncodingSyncStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

/**
 * Created by admin on 28/02/22.
 */
@Repository
public class VideoEncodingRepository {
    @Autowired
    private MongoOperations mongoOperations;


    public VideoEncodingSyncStatusDTO save(VideoEncodingSyncStatusDTO videoEncodingSyncStatusDTO) {
        mongoOperations.save(videoEncodingSyncStatusDTO);
        return videoEncodingSyncStatusDTO;
    }
}
