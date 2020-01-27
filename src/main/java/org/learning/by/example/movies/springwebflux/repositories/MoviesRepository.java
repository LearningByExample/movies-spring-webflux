package org.learning.by.example.movies.springwebflux.repositories;

import org.learning.by.example.movies.springwebflux.model.Movie;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MoviesRepository extends ReactiveCrudRepository<Movie, Integer> {
    @Query(value = "SELECT \n" +
            "  m.id, m.title, m.genres \n" +
            " FROM \n" +
            "  movies as m \n" +
            " WHERE :genre = ANY(string_to_array(LOWER(m.genres),'|')) \n")
    Flux<Movie> findByGenre(String genre);
}
