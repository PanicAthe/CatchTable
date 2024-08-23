package panicathe.catchtable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import panicathe.catchtable.model.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
}
