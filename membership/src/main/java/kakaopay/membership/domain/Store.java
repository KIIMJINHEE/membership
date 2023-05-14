package kakaopay.membership.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Store {

    @Id
    @GeneratedValue
    @Column(name = "store_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String storeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull
    private Category category;
}
