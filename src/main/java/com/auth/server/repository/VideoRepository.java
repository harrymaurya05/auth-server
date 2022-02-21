package com.auth.server.repository;

import com.auth.server.models.User;
import com.auth.server.models.Video;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by admin on 21/02/22.
 */
public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<User> findById(int  user_id);


}
