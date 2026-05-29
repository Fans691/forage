package com.forage.forage.service;

import java.util.List;
import java.util.Optional;

import com.forage.forage.model.Client;
import org.springframework.stereotype.Service;
import com.forage.forage.repository.ClientRepository;

@Service
public class ClientService {
    protected final ClientRepository clientRepository;

    public ClientService(ClientRepository cr) {
        this.clientRepository = cr;
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        return client.orElse(null);
    }

    public boolean authentification(Client cl) {
        Optional<Client> cli = clientRepository.findById(cl.getId());

        if(cli == null) return false;
        return true;
    }
}
