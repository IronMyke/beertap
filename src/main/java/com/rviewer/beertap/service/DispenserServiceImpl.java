package com.rviewer.beertap.service;

import com.rviewer.beertap.dto.BeerDispenserStatus;
import com.rviewer.beertap.exception.EntityNotFoundException;
import com.rviewer.beertap.exception.IllegalStatusChangeDateException;
import com.rviewer.beertap.exception.IllegalStatusChangeException;
import com.rviewer.beertap.model.BeerDispenser;
import com.rviewer.beertap.model.BeerDispenserUsage;
import com.rviewer.beertap.repository.DispenserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DispenserServiceImpl implements DispenserService {

    @Value( "${com.rviewer.beertap.beerPrice}" )
    private float beerPrice;

    private final DispenserRepository dispenserRepository;

    @Autowired
    public DispenserServiceImpl(DispenserRepository dispenserRepository){
        this.dispenserRepository = dispenserRepository;
    }

    @Override
    public BeerDispenser createDispenser(Float flowVolume) {
        BeerDispenser dispenser = new BeerDispenser();
        dispenser.setFlowVolume(flowVolume);
        return this.dispenserRepository.save(dispenser);
    }

    @Override
    public BeerDispenser updateStatusByIdAtTime(String dispenserId, BeerDispenserStatus requestedStatus, Date requestedChangeDate) {
        final BeerDispenser dispenser = this.dispenserRepository.findById(dispenserId).orElseThrow(() -> new EntityNotFoundException(BeerDispenser.class, dispenserId));
        if (requestedStatus == BeerDispenserStatus.closed) {
            if (dispenser.getUsages().isEmpty()) throw new IllegalStatusChangeException(requestedStatus);
            final BeerDispenserUsage latestUsage = dispenser.getUsages().get(0);
            if (latestUsage.getEndDate() != null) throw new IllegalStatusChangeException(requestedStatus);
            if (latestUsage.getBeginDate().after(requestedChangeDate)) throw new IllegalStatusChangeDateException(requestedStatus, latestUsage.getBeginDate(), requestedChangeDate);
            latestUsage.setEndDate(requestedChangeDate);
            latestUsage.setMoneySpent(latestUsage.getFlowVolume() * latestUsage.getActiveSeconds() * this.beerPrice);
        } else {
            if (!dispenser.getUsages().isEmpty()) {
                final BeerDispenserUsage latestUsage = dispenser.getUsages().get(0);
                if (latestUsage.getEndDate() == null) throw new IllegalStatusChangeException(requestedStatus);
                if (latestUsage.getEndDate().after(requestedChangeDate)) throw new IllegalStatusChangeDateException(requestedStatus, latestUsage.getBeginDate(), requestedChangeDate);
            }
            final BeerDispenserUsage newUsage = new BeerDispenserUsage();
            newUsage.setDispenser(dispenser);
            newUsage.setBeginDate(requestedChangeDate);
            newUsage.setFlowVolume(dispenser.getFlowVolume());
            dispenser.getUsages().add(0, newUsage);
        }
        return dispenserRepository.save(dispenser);
    }

    @Override
    public BeerDispenser getDispenserById(String dispenserId) {
        return this.dispenserRepository.findById(dispenserId).orElseThrow(() -> new EntityNotFoundException(BeerDispenser.class, dispenserId));
    }
}
