package com.auth.server.service.encoding;

import com.auth.server.models.VideoEncodingSyncStatusDTO;
import com.auth.server.repository.VideoEncodingRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by admin on 28/02/22.
 */
 class VideoEncodingServiceImpl implements  VideoEncodingService{

    @Autowired VideoEncodingRepository videoEncodingRepository;
    @Override public VideoEncodingSyncStatusDTO save(VideoEncodingSyncStatusDTO videoEncodingSyncStatusDTO) {
        return videoEncodingRepository.save(videoEncodingSyncStatusDTO);
    }

}
