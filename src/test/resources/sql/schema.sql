DROP TABLE IF EXISTS movies;
CREATE TABLE movies (
  id serial primary key,
  title text not null,
  genres varchar(1024)
);