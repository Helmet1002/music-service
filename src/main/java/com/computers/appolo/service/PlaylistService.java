package com.computers.appolo.service;

import com.computers.appolo.dto.PlaylistResponse;
import com.computers.appolo.entity.Playlist;
import com.computers.appolo.entity.Song;
import com.computers.appolo.entity.User;
import com.computers.appolo.repository.PlaylistRepository;
import com.computers.appolo.repository.SongRepository;
import com.computers.appolo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    @Autowired
    private  PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    @Transactional
    public Playlist createPlaylist(String ownerUsername, String name) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Playlist p = Playlist.builder()
                .name(name)
                .owner(owner)
                .build();
        return playlistRepository.save(p);
    }

    @Transactional
    public Playlist addSong(Long playlistId, Long songId, String actingUsername) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        authorizeModify(playlist, actingUsername);

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        // avoid duplicates
        boolean exists = playlist.getSongs().stream().anyMatch(s -> s.getId().equals(song.getId()));
        if (!exists) {
            playlist.getSongs().add(song);
            playlist = playlistRepository.save(playlist);
        }
        return playlist;
    }

    @Transactional
    public Playlist removeSong(Long playlistId, Long songId, String actingUsername) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        authorizeModify(playlist, actingUsername);

        playlist.getSongs().removeIf(s -> s.getId().equals(songId));
        return playlistRepository.save(playlist);
    }

    public List<Playlist> getByOwnerUsername(String username) {
        return playlistRepository.findByOwner_Username(username);
    }

    public List<Playlist> getByOwnerId(Long ownerId) {
        return playlistRepository.findByOwner_Id(ownerId);
    }

    // Map to DTO response
    public PlaylistResponse toResponse(Playlist p) {
        PlaylistResponse r = new PlaylistResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setOwnerUsername(p.getOwner() != null ? p.getOwner().getUsername() : null);
        r.setSongIds(p.getSongs().stream().map(Song::getId).collect(Collectors.toList()));
        return r;
    }

    // Only owner or ADMIN can modify playlists
    private void authorizeModify(Playlist p, String actingUsername) {
        if (actingUsername == null) throw new AccessDeniedException("Unauthenticated");
        User actingUser = userRepository.findByUsername(actingUsername)
                .orElseThrow(() -> new RuntimeException("Acting user not found"));

        boolean isOwner = p.getOwner() != null && actingUser.getId().equals(p.getOwner().getId());
        boolean isAdmin = "ROLE_ADMIN".equals(actingUser.getRole());

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Not authorized to modify this playlist");
        }
    }
}
