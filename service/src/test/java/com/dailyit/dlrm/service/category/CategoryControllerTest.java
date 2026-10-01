package com.dailyit.dlrm.service.category;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dailyit.dlrm.core.domain.Category;
import com.dailyit.dlrm.core.repository.CategoryRepository;
import com.dailyit.dlrm.core.testsupport.PostgresTestContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Import(PostgresTestContainer.class)
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 목록을 id 순으로 응답한다")
    void getCategoriesOrderedById() throws Exception {
        Category exhibition =
                categoryRepository.save(
                        Category.builder()
                                .name("전시")
                                .iconUrl("https://example.com/icons/exhibition.png")
                                .build());
        categoryRepository.save(
                Category.builder()
                        .name("팝업")
                        .iconUrl("https://example.com/icons/popup.png")
                        .build());
        categoryRepository.save(
                Category.builder()
                        .name("소품샵")
                        .iconUrl("https://example.com/icons/prop-shop.png")
                        .build());

        // PostgreSQL은 수정된 행을 테이블 뒤쪽에 새로 쓰므로, 정렬 없이 조회하면 "전시"가 마지막에 나온다
        exhibition.update("전시", "https://example.com/icons/exhibition-v2.png");
        categoryRepository.saveAndFlush(exhibition);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("전시"))
                .andExpect(jsonPath("$[1].name").value("팝업"))
                .andExpect(jsonPath("$[2].name").value("소품샵"))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(
                        jsonPath("$[0].icon_url")
                                .value("https://example.com/icons/exhibition-v2.png"));
    }

    @Test
    @DisplayName("카테고리가 없으면 빈 배열을 응답한다")
    void getCategoriesWhenEmpty() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
