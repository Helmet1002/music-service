package com.computers.appolo.controller;

import com.computers.appolo.entity.Playback;
import com.computers.appolo.service.PlaybackService;
import com.computers.appolo.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/playback")
@RequiredArgsConstructor
public class PlaybackController {

    private final PlaybackService playbackService;
    private final UserService userService;

    @PostMapping("/play")
    public Playback play(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long songId
    ) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        return playbackService.play(userId, songId);
    }

    @PostMapping("/pause")
    public Playback pause(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        return playbackService.pause(userId);
    }

    @PostMapping("/resume")
    public Playback resume(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        return playbackService.resume(userId);
    }

    @PostMapping("/stop")
    public Playback stop(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        return playbackService.stop(userId);
    }

    @GetMapping("/current")
    public Playback current(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getId();
        return playbackService.getCurrent(userId);
    }
}
