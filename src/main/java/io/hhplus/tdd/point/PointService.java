package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PointService {

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    public UserPoint getPoint(long id) {
        return userPointTable.selectById(id);
    }

    public UserPoint chargePoint(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        UserPoint newUserPoint = userPointTable.insertOrUpdate(id, userPoint.point() + amount);
        pointHistoryTable.insert(id, newUserPoint.point(), TransactionType.CHARGE, System.currentTimeMillis());
        // TODO - 트랜잭션 구현 필요

        return newUserPoint ;
    }

    public UserPoint usePoint(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);

        if (userPoint.point() < amount) {
            throw new RuntimeException("보유 중인 포인트가 사용하고자 하는 포인트보다 적습니다.");
        }

        UserPoint newUserPoint = userPointTable.insertOrUpdate(id, userPoint.point() - amount);
        pointHistoryTable.insert(id, newUserPoint.point(), TransactionType.USE, System.currentTimeMillis());
        // TODO - 트랜잭션 구현 필요

        return newUserPoint ;
    }

    public List<PointHistory> getPointHistories(long id) {
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
        pointHistories.sort(Comparator.comparingLong(PointHistory::updateMillis));
        return pointHistories;
    }
}
