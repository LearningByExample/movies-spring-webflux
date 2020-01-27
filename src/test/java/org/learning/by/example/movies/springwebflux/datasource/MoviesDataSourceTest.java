package org.learning.by.example.movies.springwebflux.datasource;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoviesDataSourceTest {
    @Test
    void weCouldCreateADataSourceWithValidProperties() throws Exception {
        final DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setDatabase("movies");
        dataSourceProperties.setCredentials("src/test/resources/creds");
        dataSourceProperties.setProtocol("postgresql");
        final DataSourceProperties.PoolConfig pool = new DataSourceProperties.PoolConfig();
        pool.setMinConnections(1);
        pool.setMaxConnections(3);
        dataSourceProperties.setPool(pool);

        final MoviesDataSource dataSource = new MoviesDataSource(dataSourceProperties, null);
        assertThat(dataSource).isNotNull();
    }

    @Test
    void weCouldNotCreateADataSourceWithInValidProperties() throws Exception {
        final DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setDatabase("movies");
        dataSourceProperties.setCredentials("wrong/path");
        dataSourceProperties.setProtocol("postgresql");

        final Exception thrown = assertThrows(Exception.class, () -> new MoviesDataSource(dataSourceProperties, null), "Expected exception, but got none");
        assertThat(thrown).hasMessageContaining("error getting data source credential value");
    }
}