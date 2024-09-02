package com.springboot.stores.service.Impl;

import com.springboot.stores.common.model.ServiceResult;
import com.springboot.stores.common.util.JWTUtils;
import com.springboot.stores.dto.ReviewDeleteDto;
import com.springboot.stores.dto.ReviewDto;
import com.springboot.stores.dto.ReviewModifyDto;
import com.springboot.stores.entity.ReservationStatus;
import com.springboot.stores.entity.Review;
import com.springboot.stores.entity.Store;
import com.springboot.stores.entity.User;
import com.springboot.stores.repository.ReservationRepository;
import com.springboot.stores.repository.ReviewRepository;
import com.springboot.stores.repository.StoreRepository;
import com.springboot.stores.repository.UserRepository;
import com.springboot.stores.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;


    // 해당 매장 이용한 적 있는 사용자만 리뷰 등록하는 함수 (단, 리뷰는 한사람당 하나만 가능!)
    @Transactional
    public ServiceResult addReview(ReviewDto reviewDto, String token) {

        // 이메일을 토큰에서 추출
        String email = JWTUtils.getEmailFromToken(token);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ServiceResult.fail("사용자가 존재하지 않습니다.");
        }

        Optional<Store> optionalStore = storeRepository.findByName(reviewDto.getStoreName());
        if (optionalStore.isEmpty()) {
            return ServiceResult.fail("매장이 존재하지 않습니다.");
        }

        User user = optionalUser.get();
        Store store = optionalStore.get();

        // 사용자가 이 매장에서 체크인한 기록이 있는지 확인
        boolean hasCheckedIn = reservationRepository.existsByUserAndStoreAndStatus(user, store, ReservationStatus.CHECKED_IN);

        if (!hasCheckedIn) {
            return ServiceResult.fail("해당 매장에서 체크인한 기록이 없습니다. 리뷰를 작성할 수 없습니다.");
        }

        // 기존에 사용자가 작성한 리뷰가 있는지 확인
        if (reviewRepository.existsByUserAndStore(user, store)) {
            return ServiceResult.fail("이미 해당 매장에 대한 리뷰를 작성하셨습니다.");
        }

        // 리뷰 작성
        Review review = Review.builder()
                .store(store)
                .user(user)
                .rating(reviewDto.getRating())
                .comment(reviewDto.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);

        // 매장 rating 업데이트
        updateStoreRating(store);

        return ServiceResult.success("리뷰 작성이 완료되었습니다.");
    }

    protected void updateStoreRating(Store store) {
        List<Review> reviews = reviewRepository.findByStore(store);
        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        store.setRating(averageRating);
        storeRepository.save(store);
    }

    // 해당 매장 이용한 적 있는 사용자만 리뷰 수정하는 함수
    @Override
    public ServiceResult modifyReview(ReviewModifyDto reviewModifyDto, String token) {

        // 이메일을 토큰에서 추출
        String email = JWTUtils.getEmailFromToken(token);

        // 사용자 조회
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ServiceResult.fail("사용자가 존재하지 않습니다.");
        }

        User user = optionalUser.get();

        // 리뷰 조회 (리뷰 ID로 조회하는 방식으로 가정)
        Optional<Review> optionalReview = reviewRepository.findById(reviewModifyDto.getReviewId());

        if (optionalReview.isEmpty()) {
            return ServiceResult.fail("수정할 리뷰가 존재하지 않습니다.");
        }

        Review review = optionalReview.get();

        // 리뷰 작성자와 현재 사용자가 일치하는지 확인
        if (!review.getUser().equals(user)) {
            return ServiceResult.fail("해당 리뷰 작성자만 수정할 수 있습니다.");
        }

        // 리뷰 수정 내용 반영
        review.setRating(reviewModifyDto.getRating());
        review.setComment(reviewModifyDto.getComment());
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(review); // 기존 리뷰 업데이트

        return ServiceResult.success("리뷰 수정에 성공했습니다.");
    }

    // 해당 매장의 리뷰 작성자와 매장의 점장만 리뷰 삭제하는 함수
    @Override
    public ServiceResult deleteReview(ReviewDeleteDto reviewDeleteDto, String token) {

        // 토큰에서 이메일을 추출
        String email = JWTUtils.getEmailFromToken(token);

        // 사용자 조회
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ServiceResult.fail("사용자가 존재하지 않습니다.");
        }

        User user = optionalUser.get();

        // 삭제할 리뷰 조회
        Optional<Review> optionalReview = reviewRepository.findById(reviewDeleteDto.getReviewId());
        if (optionalReview.isEmpty()) {
            return ServiceResult.fail("리뷰 내역이 존재하지 않습니다.");
        }

        Review review = optionalReview.get();

        // 로그인한 사람이 리뷰 작성자 또는 매장 점장인지 확인
        if (review.getUser().equals(user) || review.getStore().getOwner().equals(user)) {
            reviewRepository.delete(review); // 리뷰 삭제
            return ServiceResult.success("리뷰 삭제에 성공했습니다.");
        } else {
            return ServiceResult.fail("해당 매장의 리뷰 작성자와 점장만 리뷰를 삭제할 수 있습니다.");
        }
    }


}
