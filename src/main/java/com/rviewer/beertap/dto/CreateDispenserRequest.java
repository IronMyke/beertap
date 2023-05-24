package com.rviewer.beertap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDispenserRequest {

    @JsonProperty("flow_volume")
    @Positive(message = "Flow Volume must be greater than zero")
    private Float flowVolume;
}
