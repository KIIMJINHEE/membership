package kakaopay.membership.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Point {

    @Id
    @GeneratedValue
    @Column(name = "point_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String barcode;

    @Column(nullable = false)
    private int pointAmount;

    @Column(nullable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime approvedAt;

    @Column(nullable = false)
    private String pointType;  // "earn" or "use"

}