package com.beehivemonitor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MicroserviceRealtimeRequest {
    private List<Long> hiveIds;
}


