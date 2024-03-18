package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequestMapping("/point")
@RestController
public class PointController {

    @Autowired
    private UserPointTable userPointTable; // TODO - service / repository 계층 분리

    @Autowired
    private PointHistoryTable pointHistoryTable;  // TODO - service / repository 계층 분리

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(@PathVariable Long id) {
        try {
            return userPointTable.selectById(id);
        } catch (InterruptedException e) {
            throw new RuntimeException("UserPointTable selectById() 실행 도중 인터럽트가 발생했습니다.");
        }
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(@PathVariable Long id) {
        return Collections.emptyList();
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(@PathVariable Long id, @RequestBody Long amount) {
        if (amount <= 0L) {
            throw new RuntimeException("'amount'는 자연수이어야 합니다.");
        }

        try {
            UserPoint userPoint = userPointTable.selectById(id);
            // TODO - 트랜잭션 구현 필요
            UserPoint newUserPoint = userPointTable.insertOrUpdate(id, userPoint.point() + amount);
            pointHistoryTable.insert(id, newUserPoint.point(), TransactionType.CHARGE, System.currentTimeMillis());
            return newUserPoint;
        } catch (InterruptedException e) {
            throw new RuntimeException("UserPointTable insertOrUpdate() 실행 도중 인터럽트가 발생했습니다.");
        }
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(@PathVariable Long id, @RequestBody Long amount) {
        return new UserPoint(0L, 0L, 0L);
    }
}
