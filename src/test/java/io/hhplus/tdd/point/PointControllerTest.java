package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 컨트롤러만을 테스트하기 위해, @SpringBootTest보다 가벼운 @WebMvcTest를 사용했습니다.
 */
@WebMvcTest(PointController.class)
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 컨트롤러 테스트이기 때문에, (DB의 기능은 정상적으로 작동한다고 가정하고) @MockBean을 사용했습니다.
     */
    @MockBean
    private UserPointTable userPointTable;

    /**
     * 1-1. GET /point/{id} - 정상적으로 조회 성공하는 케이스
     */
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

    /**
     * 1-2. GET /point/{id} - path에서 /{id}를 누락한 케이스
     */
    @Test
    void getPoint_MissingId_Status404() throws Exception {
        // given

        // when

        // then
        mockMvc.perform(get("/point"))
                .andExpect(status().isNotFound());
    }

    /**
     * 1-3. GET /point/{id} - 존재하지 않는 id를 조회하는 케이스
     */
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

    /**
     * 1-4. GET /point/{id} - 유효하지 않은 id(e.g., 문자열)를 사용한 케이스
     */
    @Test
    void getPoint_InvalidId_Status400() throws Exception {
        // given
        String id = "abc";

        // when

        // then
        mockMvc.perform(get("/point/{id}", id))
                .andExpect(status().isBadRequest());
    }

    /**
     * 1-5. GET /point/{id} - DB 조회 중 인터럽트가 발생한 케이스
     */
    @Test
    void getPoint_DbInterrupted_Status500() throws Exception {
        // given
        Long id = 1L;
        given(userPointTable.selectById(id))
                .willThrow(new InterruptedException());

        // when

        // then
        mockMvc.perform(get("/point/{id}", id))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.message").value("에러가 발생했습니다."));
    }

    /**
     * PATCH /point/{id}/charge - path에서 /{id}를 누락한 케이스
     */
    @Test
    void patchPointCharge_MissingIdPathVariable_Status405() throws Exception {
        // given

        // when

        // then
        mockMvc.perform(patch("/point/charge"))
                .andExpect(status().isMethodNotAllowed()); // TODO - 컨트롤러 예외 처리 통일 필요
    }

    /**
     * PATCH /point/{id} - 유효하지 않은 id(e.g., 문자열)를 사용한 케이스
     */
    @Test
    void patchPointCharge_InvalidId_Status400() throws Exception {
        // given
        String id = "abc";
        Map<String, String> body = new HashMap<>();
        body.put("amount", "1000");

        // when

        // then
        mockMvc.perform(patch("/point/{id}/charge", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    /**
     * PATCH /point/{id} - 유효하지 않은 amount(e.g., 문자열)를 사용한 케이스
     */
    @Test
    void patchPointCharge_InvalidAmount_Status400() throws Exception {
        // given
        String id = "1";
        Map<String, String> body = new HashMap<>();
        body.put("amount", "abc");

        // when

        // then
        mockMvc.perform(patch("/point/{id}/charge", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    /**
     * PATCH /point/{id} - DB 업데이트 중 인터럽트가 발생한 케이스
     */
    @Test
    void patchPointCharge_DbInterrupted_Status500() throws Exception {
        // given
        Long id = 1L;
        Long amount = 1000L;
        given(userPointTable.insertOrUpdate(id, amount))
                .willThrow(new InterruptedException());

        // when

        // then
        mockMvc.perform(patch("/point/{id}/charge", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(amount.toString()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.message").value("에러가 발생했습니다."));
    }
}
