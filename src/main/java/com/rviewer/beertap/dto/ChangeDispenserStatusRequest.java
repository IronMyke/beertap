package com.rviewer.beertap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDispenserStatusRequest {

    @NotNull
    @JsonProperty("status")
    private BeerDispenserStatus requestedStatus;

    @NotNull
    @PastOrPresent
    @JsonProperty("updated_at")
    private Date requestedChangeTime;
}
