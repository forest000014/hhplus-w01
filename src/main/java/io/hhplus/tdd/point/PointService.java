package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    public UserPoint getPoint(long id) {
        return userPointTable.selectById(id);
    }

    public UserPoint updatePoint(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        UserPoint newUserPoint = userPointTable.insertOrUpdate(id, userPoint.point() + amount);
        pointHistoryTable.insert(id, newUserPoint.point(), TransactionType.CHARGE, System.currentTimeMillis());
        // TODO - 트랜잭션 구현 필요

        return newUserPoint ;
    }
}
