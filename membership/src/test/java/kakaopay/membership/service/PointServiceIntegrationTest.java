package kakaopay.membership.service;

import kakaopay.membership.domain.Category;
import kakaopay.membership.domain.Member;
import kakaopay.membership.domain.Point;
import kakaopay.membership.domain.Store;
import kakaopay.membership.repository.CategoryRepository;
import kakaopay.membership.repository.MemberRepository;
import kakaopay.membership.repository.PointRepository;
import kakaopay.membership.repository.StoreRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PointServiceIntegrationTest {

    @Autowired private MemberService memberService;
    @Autowired private PointService pointService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private PointRepository pointRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private StoreRepository storeRepository;

    @Test
    public void 포인트적립() {
        // given
        Category category = new Category();
        category.setCategoryName("Category A");
        categoryRepository.save(category);

        Store store = new Store();
        store.setStoreName("Store A");
        store.setCategory(category);
        storeRepository.save(store);

        Member member = new Member();
        member.setMemberName(123456789L);
        member.setBarcode("1234567890");
        memberRepository.save(member);

        Point point = new Point();
        point.setStore(store);
        point.setCategory(category);
        point.setBarcode(member.getBarcode());
        point.setPointAmount(0);
        point.setApprovedAt(LocalDateTime.now());
        pointRepository.save(point);

        int amount = 100;

        // when
        pointService.earnPoint(point, amount);

        // then
        Point updatedPoint = pointRepository.findById(point.getId()).orElse(null);
        assertNotNull(updatedPoint);
        assertEquals(amount, updatedPoint.getPointAmount());
    }

    @Test
    public void 포인트사용() {
        // given
        Category category = new Category();
        category.setCategoryName("Category A");
        categoryRepository.save(category);

        Store store = new Store();
        store.setStoreName("Store A");
        store.setCategory(category);
        storeRepository.save(store);

        Member member = new Member();
        member.setMemberName(123456789L);
        member.setBarcode("1234567890");
        memberRepository.save(member);

        Point point = new Point();
        point.setStore(store);
        point.setCategory(category);
        point.setBarcode(member.getBarcode());
        point.setPointAmount(100);
        point.setApprovedAt(LocalDateTime.now());
        pointRepository.save(point);

        int amount = 50;

        // when
        pointService.usePoint(point, amount);

        // then
        Point updatedPoint = pointRepository.findById(point.getId()).orElse(null);
        assertNotNull(updatedPoint);
        assertEquals(50, updatedPoint.getPointAmount());
    }

    @Test
    public void 포인트내역조회() {
        // given
        Category category = new Category();
        category.setCategoryName("Category A");
        categoryRepository.save(category);

        Store store = new Store();
        store.setStoreName("Store A");
        store.setCategory(category);
        storeRepository.save(store);

        Member member = new Member();
        member.setMemberName(345678912L);
        member.setBarcode("3456789123");
        memberRepository.save(member);

        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.now();

        Point point1 = new Point();
        point1.setStore(store);
        point1.setCategory(category);
        point1.setBarcode(member.getBarcode());
        point1.setPointAmount(100);
        point1.setApprovedAt(LocalDateTime.now().minusDays(7));
        point1.setPointType("earn");
        pointRepository.save(point1);

        Point point2 = new Point();
        point2.setStore(store);
        point2.setCategory(category);
        point2.setBarcode(member.getBarcode());
        point2.setPointAmount(50);
        point2.setApprovedAt(LocalDateTime.now().minusDays(3));
        point2.setPointType("earn");
        pointRepository.save(point2);

        // when
        List<Point> points = pointService.getPointHistory(member.getBarcode(), startDate, endDate);

        // then
        assertEquals(2, points.size());
        assertEquals(100, points.get(0).getPointAmount());
        assertEquals(50, points.get(1).getPointAmount());
    }

    @Test
    public void 포인트적립시포인트금액이음수일경우() {
        // given
        Category category = new Category();
        category.setCategoryName("Category A");
        categoryRepository.save(category);

        Store store = new Store();
        store.setStoreName("Store A");
        store.setCategory(category);
        storeRepository.save(store);

        Member member = new Member();
        member.setMemberName(123456789L);
        member.setBarcode("1234567890");
        memberRepository.save(member);

        Point point = new Point();
        point.setStore(store);
        point.setCategory(category);
        point.setBarcode(member.getBarcode());
        point.setPointAmount(0);
        point.setApprovedAt(LocalDateTime.now());
        pointRepository.save(point);

        int amount = -100;

        // when
        try {
            pointService.earnPoint(point, amount);
        } catch (IllegalArgumentException e) {
            // then
            assertEquals("포인트 금액은 음수일 수 없습니다.", e.getMessage());
        }
    }

    @Test
    public void 포인트사용시사용금액이포인트잔액보다클경우() {
        // given
        Category category = new Category();
        category.setCategoryName("Category A");
        categoryRepository.save(category);

        Store store = new Store();
        store.setStoreName("Store A");
        store.setCategory(category);
        storeRepository.save(store);

        Member member = new Member();
        member.setMemberName(123456789L);
        member.setBarcode("1234567890");
        memberRepository.save(member);

        Point point = new Point();
        point.setStore(store);
        point.setCategory(category);
        point.setBarcode(member.getBarcode());
        point.setPointAmount(100);
        point.setApprovedAt(LocalDateTime.now());
        pointRepository.save(point);

        int amount = 200;

        // when
        try {
            pointService.usePoint(point, amount);
        } catch (IllegalArgumentException e) {
            // then
            assertEquals("사용하려는 포인트 금액은 포인트 잔액보다 클 수 없습니다.", e.getMessage());
        }
    }

}
