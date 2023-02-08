create table profile
(
    id         serial primary key,
    name       varchar(255),
    profile_id integer,
    avatar_url text
);

create table wuff
(
    id         serial primary key,
    profile    integer references profile (id),
    text       varchar(300),
    created_at timestamp
);