package com.rviewer.beertap.controller;

import com.rviewer.beertap.dto.*;
import com.rviewer.beertap.model.BeerDispenser;
import com.rviewer.beertap.model.BeerDispenserUsage;
import com.rviewer.beertap.service.DispenserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public final class DispenserController {

    private final DispenserService dispenserService;

    @Autowired
    public DispenserController( DispenserService dispenserService) {
        this.dispenserService = dispenserService;
    }

    @PostMapping("/dispenser")
    @ResponseStatus( HttpStatus.CREATED )
    private CreateDispenserResponse createDispenser(@RequestBody CreateDispenserRequest createDispenserRequest){
        final BeerDispenser createdDispenser = this.dispenserService.createDispenser(createDispenserRequest.getFlowVolume());
        final CreateDispenserResponse response = new CreateDispenserResponse();
        response.setDispenserId(createdDispenser.getId());
        response.setFlowVolume(createdDispenser.getFlowVolume());
        return response;
    }

    @PutMapping("/dispenser/{dispenserId}")
    @ResponseStatus( HttpStatus.ACCEPTED )
    private void changeDispenserStatus(@PathVariable String dispenserId, @RequestBody ChangeDispenserStatusRequest changeDispenserStatusRequest) {
        final BeerDispenserStatus requestedStatus = changeDispenserStatusRequest.getRequestedStatus();
        final Date requestedChangeDate = changeDispenserStatusRequest.getRequestedChangeTime();
        this.dispenserService.updateStatusByIdAtTime(dispenserId, requestedStatus, requestedChangeDate);
    }

    @GetMapping("/dispenser/{dispenserId}")
    @ResponseStatus( HttpStatus.OK )
    private MoneySpentResponse getMoneySpent(@PathVariable String dispenserId) {
        final BeerDispenser dispenser = this.dispenserService.getDispenserById(dispenserId);
        final MoneySpentResponse response = new MoneySpentResponse();
        final Float amount = dispenser.getUsages().stream().map(BeerDispenserUsage::getMoneySpent).reduce(Float::sum).orElse(0f);
        response.setTotalMoneySpent(amount);
        response.setUsages(dispenser.getUsages());
        return response;
    }
}
