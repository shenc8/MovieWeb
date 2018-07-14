USE   `moviedb`;
create table employees(
email varchar(50) not null,
password varchar(20) not null,
fullname varchar(100),
primary key (email)
);
insert into employees values('classta@email.edu','classta','TA CS122B');