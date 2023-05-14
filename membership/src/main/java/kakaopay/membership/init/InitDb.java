package kakaopay.membership.init;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import kakaopay.membership.domain.Category;
import kakaopay.membership.domain.Member;
import kakaopay.membership.domain.Point;
import kakaopay.membership.domain.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void dbInit1() {

            Category categoryA = createCategory("A");
            em.persist(categoryA);
            Category categoryB = createCategory("B");
            em.persist(categoryB);
            Category categoryC = createCategory("C");
            em.persist(categoryC);

            Store store1 = createStore("스타벅스", categoryA);
            em.persist(store1);
            Store store2 = createStore("올리브영", categoryB);
            em.persist(store2);
            Store store3 = createStore("버거킹", categoryC);
            em.persist(store3);

            Point pointA = createPoint(store1, store1.getCategory(), "1234567891", 10000, LocalDateTime.now(), "earn");
            em.persist(pointA);
            Point pointB = createPoint(store2, store2.getCategory(), "1234567891", 20000, LocalDateTime.now(), "earn");
            em.persist(pointB);
            Point pointC = createPoint(store3, store3.getCategory(), "1234567891", 30000, LocalDateTime.now(), "earn");
            em.persist(pointC);

            Member member = createMember(123456789L, "1234567891");
            em.persist(member);
        }

        private Member createMember(Long memberName, String Barcode) {
            Member member = new Member();
            member.setMemberName(memberName);
            member.setBarcode(Barcode);
            return member;
        }

        private Point createPoint(Store store, Category category, String barcode, int availablePoints, LocalDateTime approvedAt, String pointType) {
            Point point = new Point();
            point.setStore(store);
            point.setCategory(category);
            point.setBarcode(barcode);
            point.setPointAmount(availablePoints);
            point.setApprovedAt(approvedAt);
            point.setPointType(pointType);
            return point;
        }

        private Category createCategory(String name) {
            Category category = new Category();
            category.setCategoryName(name);
            return category;
        }

        private Store createStore(String name, Category category) {
            Store store = new Store();
            store.setStoreName(name);
            store.setCategory(category);
            return store;
        }
    }

}
