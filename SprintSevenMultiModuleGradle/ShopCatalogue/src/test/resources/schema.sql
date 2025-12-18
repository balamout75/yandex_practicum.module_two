drop table if exists items cascade;
drop table if exists orders cascade;
drop table if exists users cascade;
drop table if exists cart_items cascade;
drop table if exists order_items cascade;

drop sequence if exists items_sequence;
drop sequence if exists orders_sequence;
drop sequence if exists users_sequence;

create sequence items_sequence start 6 increment by 1;
create sequence orders_sequence start 2 increment by 1;
create sequence users_sequence start 2 increment by 1;

create table if not exists users(
                                    id          bigint primary key DEFAULT nextval('users_sequence'),
                                    firstname   varchar NOT NULL,
                                    lastname    varchar NOT NULL);

create table if not exists items(
                                    id          bigint primary key DEFAULT nextval('items_sequence'),
                                    title       varchar NOT NULL,
                                    description varchar NOT NULL,
                                    price       bigint default 0,
                                    quantity    bigint default 0,
                                    imgPath     varchar NOT NULL);

create table if not exists orders(
                                    id           bigint primary key DEFAULT nextval('orders_sequence'),
                                    created_at   timestamp with time zone default current_timestamp,
                                    user_id      bigint NOT NULL default 1,
                                    FOREIGN KEY  (user_id) REFERENCES users(id) ON DELETE CASCADE);

create table if not exists cart_items(
                                    user_id      bigint NOT NULL default 1,
                                    item_id      bigint NOT NULL,
                                    count        bigint NOT NULL default 0,
                                    FOREIGN KEY  (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                    FOREIGN KEY  (item_id) REFERENCES items (id) ON DELETE CASCADE,
                                    primary key  (user_id, item_id));

create table if not exists order_items(
                                    order_id      bigint NOT NULL,
                                    item_id       bigint NOT NULL default 1,
                                    count         bigint NOT NULL default 0,
                                    FOREIGN KEY   (order_id)    REFERENCES orders(id) ON DELETE CASCADE,
                                    FOREIGN KEY   (item_id)     REFERENCES items (id) ON DELETE CASCADE,
                                    primary key  (order_id, item_id));

insert into users (id, firstname, lastname) values (1, 'John','Smith');

insert into items values (1,'Кепка','бейсболка большого размера',1200,12,'cap.jpg'),
                         (2,'Самокат','Моноколесо',120000,12,'cap.jpg'),
                         (3,'ZX-Spectrum','Ретрокомпьютер',12000,12,'cap.jpg'),
                         (4,'Зонт','просто зонт',2000,12,'cap.jpg'),
                         (5,'Ежедневник','Записная книжка на каждый день, и кепка',200,12,'cap.jpg');

insert into orders (id, user_id) values (1, 1);

insert into order_items values (1, 1, 10),
                               (1, 2, 9),
                               (1, 3, 8);


insert into cart_items values  (1, 4, 1),
                               (1, 5, 3);

select i.*, ci.count from items i left join cart_items  ci on i.id = ci.item_id;