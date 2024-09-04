package panicathe.catchtable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import panicathe.catchtable.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    User findByEmail(String email);
}
