package com.springboot.stores.service.Impl;

import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.common.util.JWTUtils;
import com.springboot.stores.dto.ReservationDto;
import com.springboot.stores.entity.Reservation;
import com.springboot.stores.entity.ReservationStatus;
import com.springboot.stores.entity.Store;
import com.springboot.stores.entity.User;
import com.springboot.stores.repository.ReservationRepository;
import com.springboot.stores.repository.StoreRepository;
import com.springboot.stores.repository.UserRepository;
import com.springboot.stores.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;


    // 매장 예약하는 함수
    public ServiceResult makeReservation(ReservationDto reservationDto, String token) {

        // 이메일을 토큰에서 추출
        String email = JWTUtils.getEmailFromToken(token);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Optional<Store> optionalStore = storeRepository.findByName(reservationDto.getStoreName());
            if (optionalStore.isEmpty()) {
                return ServiceResult.fail("매장을 찾을 수 없습니다.");
            }

            Store store = optionalStore.get();

            // 현재 시간을 가져옴
            LocalDateTime now = LocalDateTime.now();

            // 1. 과거 시간 검증
            if (reservationDto.getReservationTime().isBefore(now)) {
                return ServiceResult.fail("과거 시간으로는 예약할 수 없습니다. 다른 시간을 선택해주세요.");
            }

            // 2. 예약 가능 여부 확인 (예: 동일한 시간에 이미 예약이 있는지 확인)
            boolean exists = reservationRepository.existsByStoreAndReservationTime(store, reservationDto.getReservationTime());
            if (exists) {
                return ServiceResult.fail("해당 시간에 이미 예약이 있습니다. 다른 시간을 선택해주세요.");
            }

            // 예약 진행
            reservationRepository.save(Reservation.builder()
                    .store(store)
                    .user(user)
                    .reservationTime(reservationDto.getReservationTime())
                    .status(ReservationStatus.PENDING)
                    .build());

            return ServiceResult.success("매장 예약 대기 처리를 완료했습니다.");
        } else {
            return ServiceResult.fail("예약을 위해서는 회원가입을 먼저 해주세요.");
        }
    }

    // 매장 도착 확인 함수
    public ServiceResult confirmArrival(String token, LocalDateTime requestedReservationTime) {
        // 토큰에서 이메일 추출
        String email = JWTUtils.getEmailFromToken(token);

        // 이메일을 통해 사용자 조회
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ServiceResult.fail("사용자를 찾을 수 없습니다.");
        }
        User user = optionalUser.get();

        // 예약 목록 중에서 해당 사용자의 특정 예약 시간에 해당하는 예약 찾기
        Optional<Reservation> optionalReservation = reservationRepository.findByUserAndReservationTime(user, requestedReservationTime);
        if (optionalReservation.isEmpty()) {
            return ServiceResult.fail("해당 시간에 예약이 존재하지 않습니다.");
        }

        Reservation reservation = optionalReservation.get();

        // 현재 시각과 예약 시간을 기준으로 체크인 가능 여부 확인
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reservationTime = reservation.getReservationTime();

        if (now.isBefore(reservationTime.minusMinutes(10))) {
            return ServiceResult.fail("체크인 가능 시간보다 일찍 도착했습니다. 체크인 시간 10분 전에 다시 시도해주세요.");
        } else if (now.isAfter(reservationTime)) {
            return ServiceResult.fail("체크인 가능한 예약이 없습니다. 예약 시간이 지났습니다.");
        }

        // 체크인 가능: 예약 상태 업데이트
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservationRepository.save(reservation);
        return ServiceResult.success("체크인 성공!");
    }
}
