package org.learning.by.example.movies.springwebflux.datasource;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import org.learning.by.example.movies.springwebflux.converter.MovieReadConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static io.r2dbc.pool.PoolingConnectionFactoryProvider.*;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
public class MoviesDataSource extends AbstractR2dbcConfiguration {
    final DataSourceProperties dataSourceProperties;
    final MovieReadConverter movieReadConverter;
    final String userName;
    final String password;

    MoviesDataSource(final DataSourceProperties dataSourceProperties, final MovieReadConverter movieReadConverter) {
        super();
        this.dataSourceProperties = dataSourceProperties;
        this.movieReadConverter = movieReadConverter;
        final String credentials = dataSourceProperties.getCredentials();
        this.userName = getCredentialValue(credentials, "username");
        this.password = getCredentialValue(credentials, "password");
    }

    private String getCredentialValue(final String credentials, final String value) {
        final Path path = Paths.get(credentials, value);
        try {
            return new String(Files.readAllBytes(path));
        } catch (Exception ex) {
            throw new RuntimeException("error getting data source credential value : " + path.toString(), ex);
        }
    }


    @Override
    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<>();
        converterList.add(movieReadConverter);
        return new R2dbcCustomConversions(getStoreConversions(), converterList);
    }

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        //https://github.com/r2dbc/r2dbc-postgresql/blob/master/src/main/java/io/r2dbc/postgresql/client/SSLMode.java
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(PROTOCOL, dataSourceProperties.getProtocol())
                .option(DRIVER, POOLING_DRIVER)
                .option(HOST, dataSourceProperties.getHost())
                .option(PORT, dataSourceProperties.getPort())
                .option(USER, userName)
                .option(PASSWORD, password)
                .option(SSL, true)
                .option(Option.valueOf("sslMode"), "allow")
                .option(DATABASE, dataSourceProperties.getDatabase())
                .option(INITIAL_SIZE, dataSourceProperties.getPool().getMinConnections())
                .option(MAX_SIZE, dataSourceProperties.getPool().getMaxConnections())
                .option(VALIDATION_QUERY, "SELECT 1")
                .build());
    }
}
