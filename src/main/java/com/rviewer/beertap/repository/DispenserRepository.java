package com.rviewer.beertap.repository;

import com.rviewer.beertap.model.BeerDispenser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispenserRepository extends JpaRepository<BeerDispenser, String> {
}
