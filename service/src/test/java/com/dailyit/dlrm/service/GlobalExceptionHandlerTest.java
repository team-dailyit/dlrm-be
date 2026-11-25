package com.dailyit.dlrm.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dailyit.dlrm.core.exception.BaseException;
import com.dailyit.dlrm.core.exception.CommonErrorCode;
import com.dailyit.dlrm.core.testsupport.PostgresTestContainer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Import({PostgresTestContainer.class, GlobalExceptionHandlerTest.TestController.class})
@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired MockMvc mockMvc;

    @Test
    @DisplayName("없는 주소로 요청하면 404를 응답한다")
    void notFound() throws Exception {
        mockMvc.perform(get("/no-such-path"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(CommonErrorCode.NOT_FOUND.message()));
    }

    @Test
    @DisplayName("BaseException이 발생하면 그 ErrorCode로 응답한다")
    void baseException() throws Exception {
        mockMvc.perform(get("/test/base-exception"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value(CommonErrorCode.CONFLICT.message()));
    }

    @Test
    @DisplayName("예상치 못한 에러는 500을 응답한다")
    void unexpectedException() throws Exception {
        mockMvc.perform(get("/test/unexpected-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(
                        jsonPath("$.message")
                                .value(CommonErrorCode.INTERNAL_SERVER_ERROR.message()));
    }

    @Test
    @DisplayName("JSON 형식이 잘못되면 400을 응답한다")
    void invalidJson() throws Exception {
        mockMvc.perform(
                        post("/test/body")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(CommonErrorCode.BAD_REQUEST.message()));
    }

    @Test
    @DisplayName("지원하지 않는 메서드는 405를 응답한다")
    void methodNotAllowed() throws Exception {
        mockMvc.perform(get("/test/body"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"))
                .andExpect(
                        jsonPath("$.message").value(CommonErrorCode.METHOD_NOT_ALLOWED.message()));
    }

    @Test
    @DisplayName("요청 값 검증에 실패하면 400을 응답한다")
    void validationFailed() throws Exception {
        mockMvc.perform(
                        post("/test/body")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(CommonErrorCode.BAD_REQUEST.message()));
    }

    static class TestConflictException extends BaseException {
        TestConflictException() {
            super(CommonErrorCode.CONFLICT);
        }
    }

    @RestController
    static class TestController {
        record TestRequest(@NotBlank String name) {}

        @GetMapping("/test/base-exception")
        public void throwBaseException() {
            throw new TestConflictException();
        }

        @GetMapping("/test/unexpected-exception")
        public void throwUnexpectedException() {
            throw new IllegalStateException("DB 비밀번호 틀림 같은 내부 정보");
        }

        @PostMapping("/test/body")
        public void receiveBody(@Valid @RequestBody TestRequest request) {}
    }
}
