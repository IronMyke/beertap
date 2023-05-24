package com.rviewer.beertap.exception;

import com.rviewer.beertap.dto.BeerDispenserStatus;
import lombok.AllArgsConstructor;

import java.util.Date;

@AllArgsConstructor
public class IllegalStatusChangeDateException extends RuntimeException {

    private final BeerDispenserStatus requestedStatus;
    private final Date latestChangeDate;
    private final Date requestedChangeDate;

    @Override
    public String getMessage() {
        final String requestedStatus = this.requestedStatus == BeerDispenserStatus.open ? "opened" : "closed";
        final String latestStatus = this.requestedStatus == BeerDispenserStatus.closed ? "opened" : "closed";
        return "Dispenser could not be " + requestedStatus + " at time " + this.requestedChangeDate
                + ", as it was last " + latestStatus + " at " + this.latestChangeDate;
    }
}