package org.learning.by.example.movies.springwebflux.converter;

import io.r2dbc.spi.Row;
import org.learning.by.example.movies.springwebflux.model.Movie;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class MovieReadConverter implements Converter<Row, Movie> {

    // Without escape characters this pattern ins : (.*) \((\d{4})\)
    //  (       = START GROUP 1
    //  .*      = any set of characters
    //  )       = END GROUP 1
    //          = space
    //  \(      = the character (
    //  (       = START GROUP 2
    //  d{4}    = four digits, ex 1945
    //  )       = END GROUP 2
    //  \)      = the character )
    final static Pattern TITLE_YEAR_PATTERN = Pattern.compile("(.*) \\((\\d{4})\\)");

    @Override
    public Movie convert(Row row) {
        final String rowTile = row.get("title", String.class);

        final String realTitle;
        final int year;

        final Matcher matcher = TITLE_YEAR_PATTERN.matcher(Objects.requireNonNull(rowTile));
        if (matcher.find()) {
            realTitle = matcher.group(1);
            year = Integer.parseInt(matcher.group(2));
        } else {
            realTitle = rowTile;
            year = 1900;
        }

        final String[] genres = Objects.requireNonNull(row.get("genres", String.class)).split("\\|");
        final List<String> genresList = Arrays.stream(genres).filter(
                genre -> !genre.isEmpty()
        ).collect(Collectors.toList());

        final int id = Objects.requireNonNull(row.get("id", Integer.class));

        return new Movie(id, realTitle, year, genresList);

    }
}
