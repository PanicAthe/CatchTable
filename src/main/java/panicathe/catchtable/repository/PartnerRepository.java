package panicathe.catchtable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import panicathe.catchtable.model.Partner;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {
    
    Partner findByEmail(String userEmail);

    boolean existsByEmail(String email);
}
