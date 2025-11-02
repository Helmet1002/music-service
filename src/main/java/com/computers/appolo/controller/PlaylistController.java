package com.computers.appolo.controller;

import com.computers.appolo.dto.PlaylistDTO;
import com.computers.appolo.dto.PlaylistResponse;
import com.computers.appolo.entity.Playlist;
import com.computers.appolo.service.PlaylistService;
import com.computers.appolo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final UserService userService;

    // Create a playlist for the authenticated user
    @PostMapping
    public ResponseEntity<PlaylistResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PlaylistDTO dto
    ) {
        String username = userDetails.getUsername();
        Playlist p = playlistService.createPlaylist(username, dto.getName());
        return ResponseEntity.ok(playlistService.toResponse(p));
    }

    // Add a song to playlist - acting user must be owner or admin
    @PostMapping("/{playlistId}/add")
    public ResponseEntity<PlaylistResponse> addSong(
            @PathVariable Long playlistId,
            @RequestParam Long songId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String actingUsername = userDetails.getUsername();
        Playlist p = playlistService.addSong(playlistId, songId, actingUsername);
        return ResponseEntity.ok(playlistService.toResponse(p));
    }

    // Remove a song from playlist
    @PostMapping("/{playlistId}/remove")
    public ResponseEntity<PlaylistResponse> removeSong(
            @PathVariable Long playlistId,
            @RequestParam Long songId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String actingUsername = userDetails.getUsername();
        Playlist p = playlistService.removeSong(playlistId, songId, actingUsername);
        return ResponseEntity.ok(playlistService.toResponse(p));
    }

    // Get my playlists
    @GetMapping("/me")
    public ResponseEntity<List<PlaylistResponse>> myPlaylists(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        List<PlaylistResponse> out = playlistService.getByOwnerUsername(username)
                .stream().map(playlistService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    // Admin or the user themselves can view playlists of a user
    @GetMapping("/user/{username}")
    public ResponseEntity<List<PlaylistResponse>> byUser(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // allow if requesting user is admin or requesting own data
        String requester = userDetails.getUsername();
        var requesterEntity = userService.findByUsername(requester);
        if (!requester.equals(username) && (requesterEntity == null || !"ROLE_ADMIN".equals(requesterEntity.getRole()))) {
            return ResponseEntity.status(403).build();
        }
        List<PlaylistResponse> out = playlistService.getByOwnerUsername(username)
                .stream().map(playlistService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }
}
