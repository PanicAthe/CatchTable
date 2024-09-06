package panicathe.catchtable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import panicathe.catchtable.model.Partner;
import panicathe.catchtable.model.Store;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
    Store findByName(String name);

    List<Store> findAllByPartner(Partner partner);

    Store findByIdAndPartner(Long storeId, Partner partner);
}
