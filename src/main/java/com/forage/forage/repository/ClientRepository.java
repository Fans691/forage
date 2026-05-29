package com.forage.forage.repository;

import com.forage.forage.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    
}
