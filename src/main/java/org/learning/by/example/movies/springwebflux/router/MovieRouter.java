package org.learning.by.example.movies.springwebflux.router;

import org.learning.by.example.movies.springwebflux.handler.MovieHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
@EnableWebFlux
public class MovieRouter {

    final MovieHandler movieHandler;

    public MovieRouter(final MovieHandler movieHandler) {
        this.movieHandler = movieHandler;
    }

    @Bean
    RouterFunction<ServerResponse> movies() {
        return route(GET("/movies/{genre}"), movieHandler::getMovies);
    }

}
