package panicathe.catchtable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import panicathe.catchtable.model.Review;

@Repository
public interface ReviewRepostiroy extends JpaRepository<Review, Integer> {
}
