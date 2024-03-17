package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserPointTable userPointTable;

    @Test
    void getPoint_ValidId_Success() throws Exception {
        // given
        Long id = 1L;
        Long point = 1000L;
        given(userPointTable.selectById(id))
                .willReturn(new UserPoint(id, point, System.currentTimeMillis()));

        // when

        // then
        mockMvc.perform(get("/point/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(point));
    }

    @Test
    void getPoint_MissingIdPathVariable_Status404() throws Exception {
        // given

        // when

        // then
        mockMvc.perform(get("/point"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPoint_NotExistingId_Point0() throws Exception {
        // given
        Long id = 999_999L;
        Long point = 0L;
        given(userPointTable.selectById(id))
                .willReturn(new UserPoint(id, point, System.currentTimeMillis()));

        // when

        // then
        mockMvc.perform(get("/point/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(point));
    }

    @Test
    void getPoint_InvalidId_Status400() throws Exception {
        // given
        String id = "abc";

        // when

        // then
        mockMvc.perform(get("/point/{id}", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPoint_DbInterrupted_Status500() throws Exception {
        // given
        Long id = 1L;
        given(userPointTable.selectById(id))
                .willThrow(new InterruptedException());

        // when

        // then
        mockMvc.perform(get("/point/{id}", id))
                .andExpect(status().isInternalServerError());
    }
}
