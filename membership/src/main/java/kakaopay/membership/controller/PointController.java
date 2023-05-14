package kakaopay.membership.controller;

import kakaopay.membership.domain.Category;
import kakaopay.membership.domain.Point;
import kakaopay.membership.domain.Store;
import kakaopay.membership.service.PointService;
import kakaopay.membership.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class PointController {
    private final StoreService storeService;
    private final PointService pointService;

    @PostMapping("/points/earn")
    public ResponseEntity<String> earnPoints(@RequestParam Long storeId, @RequestParam String barcode, @RequestParam int amount) {
        try {
            // 상점 ID로 상점 정보 조회
            Optional<Store> store = storeService.getStoreById(storeId);
            // 등록되지 않은 상점 ID인 경우 오류 반환
            if (store.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록되지 않은 상점입니다.");

            // 상점의 Category 조회
            Category category = store.get().getCategory();
            // 바코드와 상점의 Category로 포인트 조회
            Point point = pointService.getPointByCategoryAndBarcode(category, barcode);
            // 등록되지 않은 멤버십 바코드인 경우 오류 반환
            if (point == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록되지 않은 멤버십 바코드입니다.");

            // 포인트 적립 처리
            pointService.earnPoint(point, amount);
            return ResponseEntity.ok("포인트가 적립되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }

    @PostMapping("/points/use")
    public ResponseEntity<String> usePoints(@RequestParam Long storeId, @RequestParam String barcode, @RequestParam int amount) {
        try {
            // 상점 ID로 상점 정보 조회
            Optional<Store> store = storeService.getStoreById(storeId);
            // 등록되지 않은 상점 ID인 경우 오류 반환
            if (store.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록되지 않은 상점입니다.");

            // 상점의 Category 조회
            Category category = store.get().getCategory();
            // 바코드와 상점의 Category로 포인트 조회
            Point point = pointService.getPointByCategoryAndBarcode(category, barcode);
            // 등록되지 않은 멤버십 바코드인 경우 오류 반환
            if (point == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록되지 않은 멤버십 바코드입니다.");

            // 포인트 사용 금액이 적립 금액을 초과하는 경우 오류 반환
            if (amount > point.getPointAmount()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("포인트 부족으로 사용할 수 없습니다.");

            // 포인트 사용 처리
            pointService.usePoint(point, amount);
            return ResponseEntity.ok("포인트가 사용되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }

    @GetMapping("/points/history")
    public ResponseEntity<Object> getPointHistory(
            @RequestParam String barcode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate
    ) {
        try {
            // 멤버십 바코드로 포인트 내역 조회
            List<Point> history = pointService.getPointHistory(barcode, startDate, endDate);

            // 응답값 생성
            List<Map<String, Object>> response = new ArrayList<>();
            for (Point point : history) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("approved_at", point.getApprovedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                entry.put("type", point.getPointType());
                entry.put("category", point.getCategory().getCategoryName());
                entry.put("partner_name", point.getStore().getStoreName());
                response.add(entry);
            }

            Map<String, List<Map<String, Object>>> result = new HashMap<>();
            result.put("history", response);

            //등록된 내역이 없는 경우 오류 반환
            if (response.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록된 내역이 없습니다.");
            else return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }
}
