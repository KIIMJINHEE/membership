package kakaopay.membership.service;

import kakaopay.membership.domain.Store;
import kakaopay.membership.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StoreService {

    private final StoreRepository storeRepository;

    @Autowired
    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public Optional<Store> getStoreById(Long id) {
        return storeRepository.findById(id);
    }
}