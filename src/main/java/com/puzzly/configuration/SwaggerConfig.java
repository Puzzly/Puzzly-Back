package com.puzzly.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class SwaggerConfig {

    @Value("${puzzly.swagger-ui.server-url}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI(){
        Info info = new Info()
                .version("Dev 0.0.0")
                .title("Puzzly API")
                .description("** NOTIFICATION : API마다 생략가능한 값이나 무시되는 값이 별도로 존재합니다. 각 API 설명 참고 부탁드립니다..");
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("JWT");
        Components components = new Components().addSecuritySchemes("JWT", new SecurityScheme()
                .name("JWT")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description(" Swagger로 테스트하실때에는 login-end-point의 response 값중 Bearar 값은 빼고 여기에 입력해주세요.\n\n Swagger가 자동으로 한번 더 붙여서 보내서 에러납니다..")
        );
        // 나중에 Server Object 쓰게되면 nginx 앞단에 세워서 upstream은 http 통신 하도록 적용하는것이 간단하게 처리할 수 있어보임.
        return new OpenAPI().info(info).addSecurityItem(securityRequirement).components(components).servers(List.of(
                new Server().url(serverUrl).description("referencing application.yml puzzly.swagger-ui.server-url")
                //new Server().url("http://localhost:8080").description("Avail on local")
        ));
    }
    /*
    @Bean
    @Lazy(false)
    @ConditionalOnProperty(Constants.SPRINGDOC_SHOW_LOGIN_ENDPOINT)
    public OpenApiCustomizer springSecurityLoginEndpointCustomizer(CustomUsernamePasswordAuthenticationFilter authenticationFilter) throws Exception{
        return new OpenApiLoginEndpointCustomizer()
                .loginEndpointCustomizer(authenticationFilter, "00.SignIn");
    }

    public class OpenApiLoginEndpointCustomizer<JSON_FILTER extends UsernamePasswordAuthenticationFilter>{
        public OpenApiCustomizer loginEndpointCustomizer(JSON_FILTER filter, String tagName) throws Exception{
            return openAPI -> {
                Operation operation = new Operation();

                operation.requestBody(getLoginRequestBody(filter));
                operation.responses(getLoginApiResponses());
                operation.addTagsItem(tagName);
                operation.summary("로그인");
                operation.description("사용자 계정의 인증을 처리한다.");

                PathItem pathItem = new PathItem().post(operation);
                log.error("[dev]" + operation.toString());
                try {
                    openAPI.getPaths().addPathItem(getLoginPath(filter), pathItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }
        private RequestBody getLoginRequestBody(JSON_FILTER filter) {
            Schema<?> schema = new ObjectSchema();
            schema.addProperty(filter.getUsernameParameter(), new StringSchema()._default("email"));
            schema.addProperty(filter.getPasswordParameter(), new StringSchema()._default(filter.getPasswordParameter()));

            return new RequestBody().content(new Content().addMediaType(
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                    new MediaType().schema(schema)
            ));
        }
        private String getLoginPath(JSON_FILTER filter) throws Exception {
            Field requestMatcherField = AbstractAuthenticationProcessingFilter.class.getDeclaredField("requiresAuthenticationRequestMatcher");
            requestMatcherField.setAccessible(true);
            AntPathRequestMatcher requestMatcher = (AntPathRequestMatcher) requestMatcherField.get(filter);
            String loginPath = requestMatcher.getPattern();
            requestMatcherField.setAccessible(false);
            return loginPath;
        }

        private ApiResponses getLoginApiResponses() {
            ApiResponses apiResponses = new ApiResponses();
            apiResponses.addApiResponse(
                    String.valueOf(HttpStatus.OK.value()),
                    new ApiResponse().description(HttpStatus.OK.getReasonPhrase())
            );
            apiResponses.addApiResponse(
                    String.valueOf(HttpStatus.FORBIDDEN.value()),
                    new ApiResponse().description(HttpStatus.FORBIDDEN.getReasonPhrase())
            );

            return apiResponses;
        }

    }

*/
}
