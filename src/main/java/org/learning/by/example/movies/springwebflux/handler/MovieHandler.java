package org.learning.by.example.movies.springwebflux.handler;

import org.learning.by.example.movies.springwebflux.model.Movie;
import org.learning.by.example.movies.springwebflux.repositories.MoviesRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MovieHandler {
    final MoviesRepository moviesRepository;

    public MovieHandler(final MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
    }

    public Mono<ServerResponse> getMovies(final ServerRequest request) {
        final String genre = request.pathVariable("genre");
        final Flux<Movie> movies = moviesRepository.findByGenre(genre.toLowerCase());
        return ServerResponse.ok().body(movies, Movie.class);
    }
}
