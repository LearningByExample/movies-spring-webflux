package org.learning.by.example.movies.springwebflux.router;

import org.junit.jupiter.api.Test;
import org.learning.by.example.movies.springwebflux.model.Movie;
import org.learning.by.example.movies.springwebflux.repositories.MoviesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@AutoConfigureWebTestClient
@SpringBootTest
class MovieRouterTest {
    @Autowired
    WebTestClient client;

    @MockBean
    MoviesRepository moviesRepository;

    private static final String ACTION_GENRE = "Action";
    private static final String SCI_FI_GENRE = "Sci-Fi";
    private static final String COMEDY_GENRE = "Comedy";

    private static final int FIRST_MOVIE_ID = 1;
    private static final String FIRST_MOVIE_TITLE = "Big Movie";
    private static final int FIRST_MOVIE_YEAR = 2019;

    private static final int SECOND_MOVIE_ID = 2;
    private static final String SECOND_MOVIE_TITLE = "Little Movie";
    private static final int SECOND_MOVIE_YEAR = 2018;

    @Test
    void weShouldGetMovies() throws Exception {
        final List<Movie> movieList = Arrays.asList(
                new Movie(FIRST_MOVIE_ID, FIRST_MOVIE_TITLE, FIRST_MOVIE_YEAR, Arrays.asList(ACTION_GENRE, SCI_FI_GENRE)),
                new Movie(SECOND_MOVIE_ID, SECOND_MOVIE_TITLE, SECOND_MOVIE_YEAR, Arrays.asList(SCI_FI_GENRE, COMEDY_GENRE))
        );

        when(moviesRepository.findByGenre(any())).thenReturn(Flux.fromIterable(movieList));

        client.get()
                .uri("/movies/sci-fi")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(FIRST_MOVIE_ID)
                .jsonPath("$[0].title").isEqualTo(FIRST_MOVIE_TITLE)
                .jsonPath("$[0].year").isEqualTo(FIRST_MOVIE_YEAR)
                .jsonPath("$[1].id").isEqualTo(SECOND_MOVIE_ID)
                .jsonPath("$[1].title").isEqualTo(SECOND_MOVIE_TITLE)
                .jsonPath("$[1].year").isEqualTo(SECOND_MOVIE_YEAR);

        reset(moviesRepository);
    }

    @Test
    void weShouldGetAndEmptyArray() throws Exception {
        final List<Movie> movieList = Collections.emptyList();

        when(moviesRepository.findByGenre(any())).thenReturn(Flux.fromIterable(movieList));

        client.get()
                .uri("/movies/sci-fi")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBody().json("[]");

        reset(moviesRepository);
    }

    @Test
    void weShouldGetNotFound() throws Exception {
        client.get()
                .uri("/not/valid/url")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
