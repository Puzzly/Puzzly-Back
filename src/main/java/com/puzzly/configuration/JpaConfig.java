package com.puzzly.configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import jakarta.persistence.EntityManager;

@Configuration
@EnableJpaAuditing // JPA Auditing 활성화
@EnableJpaRepositories(basePackages = "com.puzzly.api.repository")
public class JpaConfig {

    /**
     * JpaQueryFactory 빈 등록
     *
     * @param entityManager 엔티티 매니저
     * @return JPAQueryFactory 쿼리 및 DML 절 생성을 위한 팩토리 클래스
     */
    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }

}