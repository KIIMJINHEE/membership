package kakaopay.membership.service;

import kakaopay.membership.domain.Category;
import kakaopay.membership.domain.Point;
import kakaopay.membership.repository.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class PointService {

    /*
     * 다수의 서버에 다수의 인스턴스로 동작하더라도 기능에 문제가 없도록
     * 분산 환경에서의 데이터 일관성과 동시성 문제 해결을 위해
     * Isolation.REPEATABLE_READ 로 데이터베이스 트랜잭션의 동시성을 관리하고,
     * lock.lock() 로 자바 코드의 동시성을 관리합니다.
     * */

    private final PointRepository pointRepository;
    private final ReentrantLock lock = new ReentrantLock();

    @Autowired
    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    public Point getPointByCategoryAndBarcode(Category category, String barcode) {
        return pointRepository.findByCategoryAndBarcode(category, barcode);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void earnPoint(Point point, int amount) {
        lock.lock();
        try {
            int currentPoints = point.getPointAmount();
            point.setPointAmount(currentPoints + amount);
            pointRepository.save(point);
        } finally {
            lock.unlock();
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void usePoint(Point point, int amount) {
        lock.lock();
        try {
            int currentPoints = point.getPointAmount();
            if (currentPoints >= amount) {
                point.setPointAmount(currentPoints - amount);
                pointRepository.save(point);
            } else {
                throw new IllegalArgumentException("사용하려는 포인트 금액은 포인트 잔액보다 클 수 없습니다.");
            }
        } finally {
            lock.unlock();
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Point> getPointHistory(String barcode, LocalDateTime startDate, LocalDateTime endDate) {
        return pointRepository.findByBarcodeAndApprovedAtBetween(barcode, startDate, endDate);
    }
}




