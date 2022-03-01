package com.auth.server.service.encoding;

import com.auth.server.models.VideoEncodingSyncStatusDTO;

/**
 * Created by admin on 28/02/22.
 */
public interface VideoEncodingService {
    public VideoEncodingSyncStatusDTO save(VideoEncodingSyncStatusDTO videoEncodingSyncStatusDTO);
    public VideoEncodingSyncStatusDTO getVideoEncodingStatus(String requestId);
    public VideoEncodingSyncStatusDTO updateVideoEncodingStatus(VideoEncodingSyncStatusDTO videoEncodingSyncStatusDTO);
}
