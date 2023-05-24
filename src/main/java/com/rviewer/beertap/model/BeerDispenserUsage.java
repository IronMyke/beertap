package com.rviewer.beertap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "beer_dispenser_usages")
public class BeerDispenserUsage {

    @JsonIgnore
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @JsonProperty("opened_at")
    @Column(name = "begin_date")
    private Date beginDate;

    @JsonProperty("closed_at")
    @Column(name = "end_date")
    private Date endDate;

    @NotNull
    @JsonProperty("flow_volume")
    @Column(name = "flow_volume")
    private Float flowVolume;

    @NotNull
    @JsonProperty("total_spent")
    @Column(name = "money_spent")
    private Float moneySpent = 0f;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "dispenser_id")
    private BeerDispenser dispenser;

    public final Float getActiveSeconds(){
        if(this.beginDate == null || this.endDate == null) return 0f;
        if(this.endDate.before(this.beginDate)) return 0f;
        return (this.endDate.getTime() - this.beginDate.getTime())/1000f;
    }
}
