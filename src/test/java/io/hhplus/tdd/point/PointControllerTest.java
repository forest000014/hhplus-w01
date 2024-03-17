package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserPointTable userPointTable;

    @Test
    void getPoint_ValidId_Success() throws Exception {
        // given
        Long id = 1L;
        Long point = 1000L;

        // when
        userPointTable.insertOrUpdate(id, point);

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

        // when
        userPointTable.insertOrUpdate(1L, 1000L);

        // then
        mockMvc.perform(get("/point/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(point));
    }
}
