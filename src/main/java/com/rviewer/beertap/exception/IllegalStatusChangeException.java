package com.rviewer.beertap.exception;

import com.rviewer.beertap.dto.BeerDispenserStatus;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IllegalStatusChangeException extends RuntimeException{

    private BeerDispenserStatus requestedStatus;

    @Override
    public String getMessage() {
        return "Dispenser is already " + requestedStatus;
    }
}
