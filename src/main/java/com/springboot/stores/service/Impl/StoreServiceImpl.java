package com.springboot.stores.service.Impl;

import com.springboot.stores.common.exception.BizException;
import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.common.util.DistanceCalculator;
import com.springboot.stores.common.util.JWTUtils;
import com.springboot.stores.dto.*;
import com.springboot.stores.entity.Reservation;
import com.springboot.stores.entity.Role;
import com.springboot.stores.entity.Store;
import com.springboot.stores.entity.User;
import com.springboot.stores.repository.ReservationRepository;
import com.springboot.stores.repository.ReviewRepository;
import com.springboot.stores.repository.StoreRepository;
import com.springboot.stores.repository.UserRepository;
import com.springboot.stores.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    // 매장 등록하는 함수
    public ServiceResult registerStore(StoreRegistrationDto storeDto, String token) {

        //STORE-TOKEN에 로그인한 사용자의 JWT토큰 존재, email 값

        // 이메일과 권한을 토큰에서 추출
        String email = JWTUtils.getEmailFromToken(token);
        String role = JWTUtils.getRoleFromToken(token);

        // 권한 체크
        if (!Role.ROLE_PARTNER.equals(Role.valueOf(role))) {
            throw new BizException("파트너 권한이 필요합니다.");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new BizException("사용자를 찾을 수 없습니다.");
        }

        User user = optionalUser.get();

        if (storeRepository.countByName(storeDto.getName()) > 0) {
            throw new BizException("이미 존재하는 매장 이름입니다.");
        }

        storeRepository.save(Store.builder()
                .name(storeDto.getName())
                .location(storeDto.getLocation())
                .description(storeDto.getDescription())
                .rating(0.0)
                .distance(0.0)
                .latitude(storeDto.getLatitude())
                .longitude(storeDto.getLongitude())
                .owner(user)
                .build());

        return ServiceResult.success("매장 등록에 성공했습니다.");
    }

    // 매장 정보 수정하는 함수
    @Override
    public ServiceResult modifyStore(StoreModifyDto storeDto, String token) {

        // 이메일과 권한을 토큰에서 추출
        String email = JWTUtils.getEmailFromToken(token);
        String role = JWTUtils.getRoleFromToken(token);

        Optional<Store> optionalStore = storeRepository.findByName(storeDto.getName());
        if (optionalStore.isEmpty()) {
            throw new BizException("해당 매장이 존재하지 않습니다.");
        }

        Optional<User> optionalEmail = userRepository.findByEmail(email);
        if (optionalEmail.isEmpty()) {
            throw new BizException("매장 점주가 존재하지 않습니다.");
        }

        User user = optionalEmail.get();
        Store store = optionalStore.get();

        if (!store.getOwner().getId().equals(user.getId())) {
            throw new BizException("해당 매장의 점주가 아닙니다.");
        }

        // 권한 체크
        if (!Role.ROLE_PARTNER.equals(Role.valueOf(role))) {
            throw new BizException("파트너 권한이 필요합니다.");
        }

        store.setName(storeDto.getNewName());
        store.setLocation(storeDto.getLocation());
        store.setDescription(storeDto.getDescription());
        store.setLatitude(storeDto.getLatitude());
        store.setLatitude(storeDto.getLongitude());

        storeRepository.save(store);

        return ServiceResult.success("매장 정보를 수정했습니다.");
    }

    // 매장 삭제하는 함수
    @Transactional
    @Override
    public ServiceResult deleteStore(StoreDeleteDto storeDeleteDto, String token) {

        // 이메일과 권한을 토큰에서 추출
        String email = JWTUtils.getEmailFromToken(token);
        String role = JWTUtils.getRoleFromToken(token);

        Optional<Store> optionalStore = storeRepository.findByName(storeDeleteDto.getName());
        if (optionalStore.isEmpty()) {
            throw new BizException("해당 매장이 존재하지 않습니다.");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new BizException("매장 점주가 존재하지 않습니다.");
        }

        Store store = optionalStore.get();
        User user = optionalUser.get();

        if (!store.getOwner().getId().equals(user.getId())) {
            throw new BizException("해당 매장의 점주가 아닙니다.");
        }

        // 권한 체크
        if (!Role.ROLE_PARTNER.equals(Role.valueOf(role))) {
            throw new BizException("파트너 권한이 필요합니다.");
        }

        reviewRepository.deleteByStoreId(store.getId());
        reservationRepository.deleteByStoreId(store.getId());
        storeRepository.deleteById(store.getId());

        return ServiceResult.success("해당 매장 삭제를 완료했습니다.");
    }


    // 매장 이름이나 매장이름을 포함하는 단어가 있으면 찾아서
    // 가나다(name)순, 별점순(rating), 거리(distance)순으로 매장 상세 정보를 가진 매장 리스트를 반환하는 함수
    @Override
    public List<StoreDto> searchStores(String search, double userLatitude, double userLongitude, String sortBy) {
        List<Store> stores;
        if (search == null || search.isEmpty()) {
            stores = storeRepository.findAll();
        } else {
            stores = storeRepository.findByNameContaining(search);
        }

        // 거리 계산
        stores.forEach(store -> {
            double distance = DistanceCalculator.calculateDistance(userLatitude, userLongitude, store.getLatitude(), store.getLongitude());
            store.setDistance(distance);
        });

        // 정렬 조건에 따라 정렬
        switch (sortBy.toLowerCase()) {
            case "name":
                // 가나다순 정렬
                stores.sort(Comparator.comparing(Store::getName));
                break;
            case "rating":
                // 별점순 정렬 (내림차순)
                stores.sort(Comparator.comparing(Store::getRating).reversed());
                break;
            case "distance":
                // 거리순 정렬 (기본값으로 이미 설정됨)
                stores.sort(Comparator.comparing(Store::getDistance));
                break;
            default:
                throw new IllegalArgumentException("Unknown sort option: " + sortBy);
        }

        // StoreDto로 변환하여 반환
        return stores.stream()
                .map(store -> {
                    StoreDto dto = new StoreDto();
                    dto.setId(store.getId());
                    dto.setName(store.getName());
                    dto.setLocation(store.getLocation());
                    dto.setDescription(store.getDescription());
                    dto.setRating(store.getRating());
                    dto.setDistance(store.getDistance());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 매장 점주 예약 정보 날짜별 리스트 확인하는 함수
    public List<ReservationStateCheckDto> checkReservations(String token, LocalDate date) {
        List<ReservationStateCheckDto> reservationList = new ArrayList<>();

        // 이메일과 권한을 토큰에서 추출
        String email = JWTUtils.getEmailFromToken(token);
        String role = JWTUtils.getRoleFromToken(token);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new BizException("사용자가 존재하지 않습니다.");
        }

        // 권한 체크
        if (!Role.ROLE_PARTNER.equals(Role.valueOf(role))) {
            throw new BizException("파트너 권한이 필요합니다.");
        }

        User user = optionalUser.get();
        List<String> storeList = storeRepository.findStoreNamesByOwnerId(user.getId());

        // 매장 점주 가게가 여러개 있을수도 있어서 이렇게 코드 설계.
        for (String storeName : storeList) {
            List<Reservation> reservations = reservationRepository.findReservationsByStoreNameAndDate(storeName, date.atStartOfDay());
            for (Reservation reservation : reservations) {
                ReservationStateCheckDto dto = new ReservationStateCheckDto();
                dto.setReservationId(reservation.getId());
                dto.setReservationTime(reservation.getReservationTime());
                dto.setStoreName(reservation.getStore().getName());
                dto.setReservationState(reservation.getStatus().toString());
                dto.setUsername(reservation.getUser().getUsername());
                reservationList.add(dto);
            }
        }
        return reservationList;
    }
}
