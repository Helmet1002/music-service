package com.computers.appolo.dto;

import lombok.Data;
import java.util.List;

@Data
public class PlaylistResponse {
    private Long id;
    private String name;
    private String ownerUsername;
    private List<Long> songIds;
}
