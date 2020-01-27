package org.learning.by.example.movies.springwebflux.repositories;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.by.example.movies.springwebflux.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = {MoviesRepositoryTest.Initializer.class})
class MoviesRepositoryTest {
    @Container
    public static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.4-alpine")
            .withDatabaseName("movies")
            .withUsername("sa")
            .withPassword("");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "movies-datasource.host=" + postgreSQLContainer.getContainerIpAddress(),
                    "movies-datasource.port=" + postgreSQLContainer.getFirstMappedPort()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Autowired
    MoviesRepository moviesRepository;

    @Autowired
    ConnectionFactory connectionFactory;

    void loadSQL(final String fileName) throws Exception {
        final File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).getFile());
        final String content = new String(Files.readAllBytes(file.toPath()));
        final DatabaseClient client = DatabaseClient.create(connectionFactory);
        client.execute(content)
                .fetch()
                .rowsUpdated()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @BeforeEach
    void initData() throws Exception {
        loadSQL("sql/schema.sql");
        loadSQL("sql/data.sql");
    }

    @Test
    public void weCouldGetSciFiMovies() throws Exception {
        moviesRepository.findByGenre("sci-fi")
                .as(StepVerifier::create)
                .expectSubscription()
                .thenRequest(Long.MAX_VALUE)
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }

    @Test
    public void weCouldGetActionMovies() throws Exception {
        moviesRepository.findByGenre("action")
                .as(StepVerifier::create)
                .expectSubscription()
                .thenRequest(Long.MAX_VALUE)
                .expectNextCount(4)
                .expectComplete()
                .verify();

    }

    @Test
    public void weCouldGetFantasyMovies() throws Exception {
        moviesRepository.findByGenre("fantasy")
                .as(StepVerifier::create)
                .expectSubscription()
                .thenRequest(Long.MAX_VALUE)
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    public void weCouldNotGetAnimationMovies() throws Exception {
        moviesRepository.findByGenre("animation")
                .as(StepVerifier::create)
                .expectSubscription()
                .thenRequest(Long.MAX_VALUE)
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

    @Test
    public void weGetCorrectMovies() throws Exception {
        final List<Movie> adventureMovies = Arrays.asList(
                new Movie(2, "Second movie", 2019, Arrays.asList("Action", "Adventure")),
                new Movie(3, "Third movie (Alternate Title Third)", 2018, Arrays.asList("Action", "Adventure", "Sci-Fi"))
        );
        moviesRepository.findByGenre("adventure")
                .as(StepVerifier::create)
                .recordWith(ArrayList::new)
                .thenRequest(Long.MAX_VALUE)
                .expectNextCount(2)
                .expectRecordedMatches(adventureMovies::containsAll)
                .expectComplete()
                .verify();
    }
}