package io.hhplus.tdd.point;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
public class PointControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void get_point_by_id() throws Exception {
        // given
        Long id = 0L;
        Gson gson = new Gson();
        String responseJson = gson.toJson(new UserPoint(0L, 0L, 0L));

        // when

        // then
        mockMvc.perform(get("/point/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string(responseJson));
    }
}
