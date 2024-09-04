package panicathe.catchtable.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import panicathe.catchtable.dto.ResponseDTO;
import panicathe.catchtable.dto.StoreDTO;
import panicathe.catchtable.repository.PartnerRepository;


@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;

    // 1 상점 정보 등록
    public ResponseEntity<ResponseDTO> addStore(StoreDTO storeDTO, String email) {

        Long id = partnerRepository.findByEmail(email).getId();

        return null;
    }

    // 1 상점 특정 리뷰 삭제

    // 1 내 상점 정보 조회
    
    // 내 상점 예약 정보 조회

    // 상정 정보 수정

    // 상점 정보 삭제
    
    // 예약 승인
    
    // 예약 취소
}
