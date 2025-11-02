package com.computers.appolo.repository;

import com.computers.appolo.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByOwner_Id(Long ownerId);
    List<Playlist> findByOwner_Username(String username);
}
