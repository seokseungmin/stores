package com.springboot.stores.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    private String description;

    private Double rating;  // 별점 필드 추가

    private Double distance;  // 거리 필드 추가

    @Column(nullable = false)
    private Double latitude;  // 위도 필드 추가

    @Column(nullable = false)
    private Double longitude;  // 경도 필드 추가

    @OneToMany(mappedBy = "store")
    private List<Review> reviews;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}