package com.puzzly.configuration;

import com.puzzly.security.filter.CustomUsernamePasswordAuthenticationFilter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.utils.Constants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.lang.reflect.Field;

@Configuration
@Slf4j
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        Info info = new Info()
                .version("Dev 0.0.0")
                .title("Puzzly API")
                .description("");
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("JWT");
        Components components = new Components().addSecuritySchemes("JWT", new SecurityScheme()
                .name("JWT")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("When you using swagger-ui, \"Bearer \" string value will be automatically added in authorization value\n" +
                        "so you should remove string \"Bearer \" value from access token.\n" +
                        "On the other hand, if you using your own application, You should maintain \"Bearer \" value in access token.")
        );
        return new OpenAPI().info(info).addSecurityItem(securityRequirement).components(components);
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
