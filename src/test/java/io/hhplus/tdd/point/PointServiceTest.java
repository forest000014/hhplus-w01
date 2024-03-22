package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

public class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @MockBean
    private UserPointTable userPointTable;

    @MockBean
    private PointHistoryTable pointHistoryTable;

    @Test
    public void getPoint_Success() {
        // given
        long id = 1;
        long point = 1000;

        // when
        given(userPointTable.selectById(id))
                .willReturn(new UserPoint(id, point, System.currentTimeMillis()));
        UserPoint result = pointService.getPoint(id);

        // then

        assertEquals(id, result.id());
        assertEquals(point, result.point());
    }

    @Test
    public void chargePoint_Success() {
        // given
        long id = 1;
        long point = 2000;

        // when
        given(userPointTable.selectById(id))
                .willReturn(new UserPoint(id, 0, System.currentTimeMillis()));
        given(userPointTable.insertOrUpdate(id, point))
                .willReturn(new UserPoint(id, point, System.currentTimeMillis()));
        UserPoint orgUserPoint = userPointTable.selectById(id);
        UserPoint newUserPoint = userPointTable.insertOrUpdate(id, point);

        // then
        assertEquals(id, orgUserPoint.id());
        assertEquals(0, orgUserPoint.point());
        assertEquals(id, newUserPoint.id());
        assertEquals(point, newUserPoint.point());
    }
}
