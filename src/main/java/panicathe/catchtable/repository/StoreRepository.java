package panicathe.catchtable.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import panicathe.catchtable.model.Partner;
import panicathe.catchtable.model.Store;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    // 스토어 이름으로 조회
    Store findByName(String name);

    // 파트너별로 상점 조회 (리뷰도 함께 로딩)
    @EntityGraph(attributePaths = {"reviews"})
    List<Store> findAllByPartner(Partner partner);

    // 파트너와 상점 ID로 조회 (리뷰도 함께 로딩)
    @EntityGraph(attributePaths = {"reviews"})
    Store findByIdAndPartner(Long storeId, Partner partner);

    // 키워드로 상점 이름을 검색
    Page<Store> findByNameContainingIgnoreCase(String keyword, Pageable limit);

    // 평균 별점으로 내림차순 정렬된 상점 리스트 조회
    List<Store> findAllByOrderByAverageRatingDesc();

}

