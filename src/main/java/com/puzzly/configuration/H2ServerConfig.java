package com.puzzly.configuration;

//import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

//@Configuration
@Profile("local")
public class H2ServerConfig {

    //@Profile("local")
    /*
    @Bean
    public Server h2TcpServer() throws SQLException {
        return Server.createTcpServer().start();
    }

     */
}
