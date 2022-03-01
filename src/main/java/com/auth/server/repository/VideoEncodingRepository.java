package com.auth.server.repository;

import com.auth.server.models.VideoEncodingSyncStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    public VideoEncodingSyncStatusDTO getVideoEncodingSyncStatusDTO(String requestId){
        VideoEncodingSyncStatusDTO videoEncodingSyncStatusDTO = fetch(requestId);
        if(videoEncodingSyncStatusDTO == null){
            VideoEncodingSyncStatusDTO videoEncodingSyncStatus = new VideoEncodingSyncStatusDTO(requestId);
            videoEncodingSyncStatusDTO = updateVideoEncodingSyncStatusDTO(videoEncodingSyncStatus);
        }
        return  videoEncodingSyncStatusDTO;
    }

    public VideoEncodingSyncStatusDTO updateVideoEncodingSyncStatusDTO(VideoEncodingSyncStatusDTO videoEncodingSyncStatusDTO){
        return save(videoEncodingSyncStatusDTO);
    }

    public VideoEncodingSyncStatusDTO fetch(String requestId){
       return mongoOperations.findOne(new Query(
                        Criteria.where("requestId").is(requestId)),
                VideoEncodingSyncStatusDTO.class);
    }

}

