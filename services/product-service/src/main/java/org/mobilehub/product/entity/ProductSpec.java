package org.mobilehub.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_spec")
public class ProductSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String os;

    @Column(nullable = false)
    private String cpu;

    @Column(nullable = false)
    private String cpu_speed;

    @Column(nullable = false)
    private String gpu;

    @Column(nullable = false)
    private String ram;

    @Column(nullable = false)
    private String storage_cap;

    @Column(nullable = false)
    private String battery_cap;

    @Column(nullable = false)
    private String rear_cam;

    @Column(nullable = false)
    private String front_cam;

    @Column(nullable = false)
    private String screen_res;

    @Column(nullable = false)
    private String features;

    @Column(nullable = false)
    private String material;

    @Column(nullable = false)
    private String size_weight;

    @Column(nullable = true)
    private String brand;

    @Column(nullable = false)
    private String release_date;

    @OneToOne(mappedBy = "spec")
    private Product product;
}
