CREATE TABLE user_types
(
    id   serial PRIMARY KEY,
    type VARCHAR(10) NOT null UNIQUE
);

CREATE TABLE user_roles
(
    id   serial PRIMARY KEY,
    type VARCHAR(10) NOT null UNIQUE
);

insert into user_roles
values (0, 'user'),
       (1, 'admin');

CREATE TABLE users
(
    id         serial PRIMARY KEY,
    fb_id      VARCHAR     NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    user_role  int         NOT null references user_roles (id) on delete restrict default 0,
    fb_token   VARCHAR,
    created_on TIMESTAMP                                                          default CURRENT_TIMESTAMP
);
