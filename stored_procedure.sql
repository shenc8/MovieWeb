DELIMITER $$
USE `moviedb`$$
drop procedure if exists add_movie$$
CREATE PROCEDURE add_movie(
IN movie_title varchar(100),
IN movie_year integer,
IN movie_director varchar(100),
IN star_name varchar(100),
IN star_birthYear integer,
IN genre_name varchar(32),
out message varchar(100)
)
BEGIN
	declare star_id varchar(10) default '';
    declare movie_id varchar(10) default '';
    declare genre_id int(11) default 0;
	if (select count(*) from movies where movies.title = movie_title and movies.year =movie_year and movies.director = movie_director)>0
    then
		set message = "Movie exists, no change will be made";
	else
		set movie_id = (select concat((select left((select max(id) from movies),2)),(select substring((select max(id) from movies),3,9)+1)));
        insert into movies (id, title, year, director) values (movie_id,movie_title, movie_year,movie_director);
        if (star_birthYear is null)
        then
        if (select count(*) from stars where stars.name=star_name and stars.birthYear is null) = 0
        then
			set star_id = (select concat((select left((select max(id) from stars),2)),(select substring((select max(id) from stars),3,9)+1)));
            insert into stars (id, name, birthYear) values (star_id,star_name,star_birthYear);
		else
			set star_id = (select id from stars where stars.name=star_name and stars.birthYear is null);
		end if;
        else
        if (select count(*) from stars where stars.name=star_name and stars.birthYear=star_birthYear) = 0
        then
			set star_id = (select concat((select left((select max(id) from stars),2)),(select substring((select max(id) from stars),3,9)+1)));
            insert into stars (id, name, birthYear) values (star_id,star_name,star_birthYear);
		else
			set star_id = (select id from stars where stars.name=star_name and stars.birthYear=star_birthYear);
		end if;
        end if;
		insert into stars_in_movies (starId, movieId) values(star_id,movie_id);
        if (select count(*) from genres where genres.name=genre_name) = 0
        then
			set genre_id = (select max(id)+1 from genres);
            insert into genres (id,name) values(genre_id,genre_name);
		else
			set genre_id = (select id from genres where genres.name=genre_name);
		end if;
        insert into ratings (movieId, rating, numVotes) values (movie_id,0,0);
        insert into genres_in_movies (genreId, movieId)values(genre_id,movie_id);
        set message = concat("Successfully Insert Movies: Movie ID(",movie_id,")");
    end if;
END
$$