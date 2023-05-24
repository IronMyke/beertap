package com.rviewer.beertap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rviewer.beertap.model.BeerDispenserUsage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneySpentResponse {

    @NotNull
    @JsonProperty("amount")
    private Float totalMoneySpent;

    @NotNull
    @JsonProperty("usages")
    private List<BeerDispenserUsage> usages = new ArrayList<>();
}
