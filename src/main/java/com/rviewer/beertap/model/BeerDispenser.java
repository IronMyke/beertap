package com.rviewer.beertap.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "beer_dispensers")
public class BeerDispenser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @NotNull
    @Column(name = "flow_volume")
    private Float flowVolume;

    @OneToMany(mappedBy = "dispenser", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("beginDate DESC")
    private List<BeerDispenserUsage> usages = new ArrayList<>();
}
