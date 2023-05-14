package kakaopay.membership.repository;
import kakaopay.membership.domain.Category;
import kakaopay.membership.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {

    Point findByCategoryAndBarcode(Category category, String barcode);

    List<Point> findByBarcodeAndApprovedAtBetween(String barcode, LocalDateTime startDate, LocalDateTime endDate);
}
