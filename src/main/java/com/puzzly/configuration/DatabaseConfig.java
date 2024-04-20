package com.puzzly.configuration;

import com.puzzly.api.enums.AccountAuthority;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.puzzly.api.repository.jpa", entityManagerFactoryRef = "entityManagerFactory"
)
@MapperScan(basePackages = {"com.puzzly.api.repository.mybatis"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class DatabaseConfig {
    Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private final String TYPE_ALIASE = "com.puzzly.api.enums";

    // Type Handler
    // https://velog.io/@ghk4889/mybatis%EC%9D%98-custom-typehandler-%EB%A7%8C%EB%93%A4%EA%B8%B0JAVA-Enum-%ED%83%80%EC%9E%85
    // https://www.holaxprogramming.com/2015/11/12/spring-boot-mybatis-typehandler/
    public static TypeHandler<?>[] SQL_SESSION_FACTORY_TYPE_HANDELER = new TypeHandler[] {
            new AccountAuthority.AuthTypeHandler()
    };

    //application.yml에서 datasource.mariadb 선언값 조회, 자동으로 HikariCP Configure에 삽입
    @Bean(name="hikariConfig")
    //@ConfigurationProperties(prefix="datasource.mariadb")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    // HikariConfig을 사용하는 DataSource 생성 (DB Conn Entity, JdbcTemplate에서 갖다씀)
    /*
    @Bean(name="datasource")
    public DataSource dataSource() {
//		HikariConfig hikariConfig = hikariConfig();

        return new HikariDataSource(hikariConfig());
    }

     */

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource, EntityManagerFactory entityManagerFactory){
        // Mybatis Transactional
        DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource);


        // JPA transactional
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);

        // Chained transaction manager (MyBatis X JPA)
        ChainedTransactionManager transactionManager = new ChainedTransactionManager(jpaTransactionManager, manager);
        return manager;
        // REF : https://jforj.tistory.com/92
    }

    @Bean(name = "sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(DataSource DataSource, ApplicationContext applicationContext) throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(DataSource);
        org.apache.ibatis.session.Configuration prop = new org.apache.ibatis.session.Configuration();
        prop.setUseGeneratedKeys(true);
        prop.setMapUnderscoreToCamelCase(true);
        sqlSessionFactoryBean.setConfiguration(prop);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mybatisMapper/*.xml"));
        sqlSessionFactoryBean.setTypeAliasesPackage(TYPE_ALIASE);
        sqlSessionFactoryBean.setTypeHandlers(SQL_SESSION_FACTORY_TYPE_HANDELER);
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    // jpa 설정
    // 별도 설정을 명세하지 않음으로써 기존 설정 가져다 쓰는방향으로...
    // 괜한 삽질...

/*
    @Primary
    @Bean(name="jpaProps")
    @ConfigurationProperties(prefix = "spring.jpa")
    public Properties jpaProps(){
        return new Properties();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setPersistenceUnitName("jpa"); // persistence.xml의 설정 정의된 이름
        entityManagerFactory.setJpaProperties(jpaProps());
        entityManagerFactory.setPackagesToScan("com.puzzly.api.entity");
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        logger.error(entityManagerFactory.getJpaPropertyMap().toString());
        return entityManagerFactory;
    }



 */
}
