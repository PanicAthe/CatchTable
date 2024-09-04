package panicathe.catchtable.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import panicathe.catchtable.dto.ResponseDTO;
import panicathe.catchtable.dto.StoreDTO;
import panicathe.catchtable.exception.CustomException;
import panicathe.catchtable.exception.ErrorCode;
import panicathe.catchtable.model.Partner;
import panicathe.catchtable.model.Store;
import panicathe.catchtable.repository.PartnerRepository;
import panicathe.catchtable.repository.StoreRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final StoreRepository storeRepository;

    // 1 상점 정보 등록
    public ResponseEntity<ResponseDTO> addStore(StoreDTO storeDTO, String email) {

        Partner partner= partnerRepository.findByEmail(email);

        if(storeRepository.findByName(storeDTO.getName()) != null)
            throw new CustomException(ErrorCode.STORE_NAME_ALREADY_REGISTERED);
        
        storeRepository.save(Store.builder()
                        .partner(partner)
                        .lat(storeDTO.getLat())
                        .lon(storeDTO.getLon())
                        .description(storeDTO.getDescription())
                        .name(storeDTO.getName())
                .build());

        ResponseDTO responseDTO = new ResponseDTO("상점 등록이 완료되었습니다.", HttpStatus.OK.value());
        return ResponseEntity.ok(responseDTO);
    }

    // 1 내 상점 정보 조회
    public ResponseEntity<ResponseDTO> getStores(String email) {
        Partner partner= partnerRepository.findByEmail(email);
        List<Store> stores = storeRepository.findAllByPartner(partner);
        ResponseDTO responseDTO = new ResponseDTO(stores.toString(), HttpStatus.OK.value());
        return ResponseEntity.ok(responseDTO);
    }


    // 1 상점 특정 리뷰 삭제
    
    // 내 상점 예약 정보 조회

    // 상정 정보 수정

    // 상점 정보 삭제
    
    // 예약 승인
    
    // 예약 취소
}
