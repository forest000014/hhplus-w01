package io.hhplus.tdd.database;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 해당 Table 클래스는 변경하지 않고 공개된 API 만을 사용해 데이터를 제어합니다.
 */
@Component
public class PointHistoryTable {
    private List<PointHistory> table = new ArrayList<>();

    private Long cursor = 1L;

    public PointHistory insert(
            Long id,
            Long amount,
            TransactionType transactionType,
            Long updateMillis
    ) throws InterruptedException {
        Thread.sleep((long)(Math.random() * 300.0));

        PointHistory history = new PointHistory(cursor++, id, transactionType, amount, updateMillis);
        table.add(history);

        return history;
    }

    public List<PointHistory> selectAllByUserId(Long userId) {
        return table.stream()
                .filter(it -> it.userId().equals(userId))
                .toList();
    }
}
