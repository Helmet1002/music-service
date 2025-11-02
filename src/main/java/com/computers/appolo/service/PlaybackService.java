package com.computers.appolo.service;

import com.computers.appolo.entity.Playback;
import com.computers.appolo.entity.Song;
import com.computers.appolo.entity.User;
import com.computers.appolo.repository.PlaybackRepository;
import com.computers.appolo.repository.SongRepository;
import com.computers.appolo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaybackService {

    private final PlaybackRepository playbackRepo;
    private final UserRepository userRepo;
    private final SongRepository songRepo;

    public Playback play(Long userId, Long songId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Song song = songRepo.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        Playback pb = playbackRepo.findByUser_Id(userId)
                .orElse(Playback.builder().user(user).build());

        pb.setSong(song);
        pb.setStatus("PLAYING");
        pb.setPositionSec(0);

        return playbackRepo.save(pb);
    }

    public Playback pause(Long userId) {
        Playback pb = playbackRepo.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Nothing is playing"));

        pb.setStatus("PAUSED");
        return playbackRepo.save(pb);
    }

    public Playback resume(Long userId) {
        Playback pb = playbackRepo.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Nothing to resume"));

        pb.setStatus("PLAYING");
        return playbackRepo.save(pb);
    }

    public Playback stop(Long userId) {
        Playback pb = playbackRepo.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Nothing to stop"));

        pb.setStatus("STOPPED");
        pb.setPositionSec(0);
        pb.setSong(null);

        return playbackRepo.save(pb);
    }

    public Playback getCurrent(Long userId) {
        return playbackRepo.findByUser_Id(userId)
                .orElse(null);
    }
}
