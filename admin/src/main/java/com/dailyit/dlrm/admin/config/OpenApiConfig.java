package com.dailyit.dlrm.admin.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.BindParam;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("들름 어드민 API").version("v1").description("들름 운영자용 API입니다."));
    }

    // swagger-core는 Jackson 2를 써서 spring.jackson 설정이 적용되지 않으므로, application.yaml의 이름 규칙과 맞춘다
    @Bean
    public ModelResolver modelResolver() {
        return new ModelResolver(
                Json.mapper()
                        .copy()
                        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE));
    }

    // springdoc은 @BindParam을 읽지 않으므로, 문서의 파라미터 이름을 @BindParam 값으로 맞춘다
    @Bean
    public ParameterCustomizer bindParamCustomizer() {
        return (parameter, methodParameter) -> {
            BindParam bindParam = methodParameter.getParameterAnnotation(BindParam.class);
            if (parameter != null && bindParam != null) {
                parameter.setName(bindParam.value());
            }
            return parameter;
        };
    }
}
