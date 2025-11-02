package com.computers.appolo.repository;

import com.computers.appolo.entity.Playback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaybackRepository extends JpaRepository<Playback, Long> {

    Optional<Playback> findByUser_Id(Long userId);
}
