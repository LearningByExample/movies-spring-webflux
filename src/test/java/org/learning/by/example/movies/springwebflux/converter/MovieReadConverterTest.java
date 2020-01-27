package org.learning.by.example.movies.springwebflux.converter;

import io.r2dbc.spi.Row;
import org.junit.jupiter.api.Test;
import org.learning.by.example.movies.springwebflux.model.Movie;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MovieReadConverterTest {
    @Test
    void weShouldMapANormalRow() throws Exception {
        final Row row = mock(Row.class);
        when(row.get("title", String.class)).thenReturn("this is a test (2000)");
        when(row.get("id", Integer.class)).thenReturn(1);
        when(row.get("genres", String.class)).thenReturn("one|two");

        final MovieReadConverter converter = new MovieReadConverter();
        final Movie movie = converter.convert(row);

        assertThat(movie).isNotNull();
        assertThat(movie.getId()).isEqualTo(1);
        assertThat(movie.getTitle()).isEqualTo("this is a test");
        assertThat(movie.getYear()).isEqualTo(2000);
        final List<String> genres = movie.getGenres();
        assertThat(genres).hasSize(2);
        assertThat(genres).contains("one");
        assertThat(genres).contains("two");
    }

    @Test
    void weShouldMapARowWithNoYear() throws Exception {
        final Row row = mock(Row.class);
        when(row.get("title", String.class)).thenReturn("this is a test");
        when(row.get("id", Integer.class)).thenReturn(1);
        when(row.get("genres", String.class)).thenReturn("one|two");

        final MovieReadConverter converter = new MovieReadConverter();
        final Movie movie = converter.convert(row);

        assertThat(movie).isNotNull();
        assertThat(movie.getId()).isEqualTo(1);
        assertThat(movie.getTitle()).isEqualTo("this is a test");
        assertThat(movie.getYear()).isEqualTo(1900);
        final List<String> genres = movie.getGenres();
        assertThat(genres).hasSize(2);
        assertThat(genres).contains("one");
        assertThat(genres).contains("two");
    }

    @Test
    void weShouldMapARowWithOneGenre() throws Exception {
        final Row row = mock(Row.class);
        when(row.get("title", String.class)).thenReturn("this is a test (2000)");
        when(row.get("id", Integer.class)).thenReturn(1);
        when(row.get("genres", String.class)).thenReturn("one");

        final MovieReadConverter converter = new MovieReadConverter();
        final Movie movie = converter.convert(row);

        assertThat(movie).isNotNull();
        assertThat(movie.getId()).isEqualTo(1);
        assertThat(movie.getTitle()).isEqualTo("this is a test");
        assertThat(movie.getYear()).isEqualTo(2000);
        final List<String> genres = movie.getGenres();
        assertThat(genres).hasSize(1);
        assertThat(genres).contains("one");
    }

    @Test
    void weShouldMapARowWithNoGenres() throws Exception {
        final Row row = mock(Row.class);
        when(row.get("title", String.class)).thenReturn("this is a test (2000)");
        when(row.get("id", Integer.class)).thenReturn(1);
        when(row.get("genres", String.class)).thenReturn("");

        final MovieReadConverter converter = new MovieReadConverter();
        final Movie movie = converter.convert(row);

        assertThat(movie).isNotNull();
        assertThat(movie.getId()).isEqualTo(1);
        assertThat(movie.getTitle()).isEqualTo("this is a test");
        assertThat(movie.getYear()).isEqualTo(2000);
        final List<String> genres = movie.getGenres();
        assertThat(genres).hasSize(0);
        assertThat(genres).isEmpty();
    }

    @Test
    void weShouldMapATitleWithAlternativeTitle() throws Exception {
        final Row row = mock(Row.class);
        when(row.get("title", String.class)).thenReturn("this is a test (alternative title) (2000)");
        when(row.get("id", Integer.class)).thenReturn(1);
        when(row.get("genres", String.class)).thenReturn("one|two");

        final MovieReadConverter converter = new MovieReadConverter();
        final Movie movie = converter.convert(row);

        assertThat(movie).isNotNull();
        assertThat(movie.getId()).isEqualTo(1);
        assertThat(movie.getTitle()).isEqualTo("this is a test (alternative title)");
        assertThat(movie.getYear()).isEqualTo(2000);
        final List<String> genres = movie.getGenres();
        assertThat(genres).hasSize(2);
        assertThat(genres).contains("one");
        assertThat(genres).contains("two");
    }

    @Test
    void weShouldMapATitleWithAlternativeTitleAndNoYear() throws Exception {
        final Row row = mock(Row.class);
        when(row.get("title", String.class)).thenReturn("this is a test (alternative title)");
        when(row.get("id", Integer.class)).thenReturn(1);
        when(row.get("genres", String.class)).thenReturn("one|two");

        final MovieReadConverter converter = new MovieReadConverter();
        final Movie movie = converter.convert(row);

        assertThat(movie).isNotNull();
        assertThat(movie.getId()).isEqualTo(1);
        assertThat(movie.getTitle()).isEqualTo("this is a test (alternative title)");
        assertThat(movie.getYear()).isEqualTo(1900);
        final List<String> genres = movie.getGenres();
        assertThat(genres).hasSize(2);
        assertThat(genres).contains("one");
        assertThat(genres).contains("two");
    }
}