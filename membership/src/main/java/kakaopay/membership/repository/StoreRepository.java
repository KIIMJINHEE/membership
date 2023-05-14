package kakaopay.membership.repository;

import kakaopay.membership.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Store findById(long id);
}