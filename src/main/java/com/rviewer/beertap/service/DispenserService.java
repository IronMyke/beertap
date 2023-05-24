package com.rviewer.beertap.service;

import com.rviewer.beertap.dto.BeerDispenserStatus;
import com.rviewer.beertap.model.BeerDispenser;

import java.util.Date;

public interface DispenserService {

    BeerDispenser createDispenser(Float flowVolume);

    BeerDispenser updateStatusByIdAtTime(String dispenserId, BeerDispenserStatus requestedStatus, Date requestedChangeDate);

    BeerDispenser getDispenserById(String dispenserId);
}
