drop table if exists users cascade ;
create table if not exists users(
                                    id          bigserial primary key,
                                    firstname   varchar NOT NULL,
                                    lastname    varchar NOT NULL,
                                    available   boolean  default true,
                                    created_at timestamp with time zone default current_timestamp);


drop table if exists items cascade ;
create table if not exists items(
                                    id          bigserial primary key,
                                    title       varchar NOT NULL,
                                    description varchar NOT NULL,
                                    price       numeric(10,2) NOT NULL,
                                    Quantity    bigint default 0,
                                    imgPath     varchar NOT NULL);

drop table if exists cards cascade ;
create table if not exists cards(
                                    id      bigserial primary key,
                                    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE);

drop table if exists orders cascade ;
create table if not exists orders(
                                    id           bigserial primary key,
                                    created_at   timestamp with time zone default current_timestamp,
                                    user_id      bigint NOT NULL default 1,
                                    FOREIGN KEY  (user_id) REFERENCES users(id) ON DELETE CASCADE);

drop table if exists in_card cascade;
create table if not exists in_card(
                                    card_id      bigint NOT NULL default 1,
                                    item_id      bigint NOT NULL,
                                    PRIMARY KEY  (card_id, item_id),
                                    FOREIGN KEY  (card_id) REFERENCES cards(id) ON DELETE CASCADE,
                                    FOREIGN KEY  (item_id) REFERENCES items (id) ON DELETE CASCADE);

drop table if exists in_order cascade;
create table if not exists in_order(
                                    order_id      bigint NOT NULL,
                                    item_id       bigint NOT NULL default 1,
                                    PRIMARY KEY   (order_id, item_id),
                                    FOREIGN KEY   (order_id)    REFERENCES orders(id) ON DELETE CASCADE,
                                    FOREIGN KEY   (item_id)     REFERENCES items (id) ON DELETE CASCADE);

insert into items values (1,'Кепка','бейсболка большого размера',1200,12,'cap.jpg'),
                                                                                                              (2,'Кепка','бейсболка большого размера',1200,12,'cap.jpg'),
                                                                                                              (3,'Кепка','бейсболка большого размера',1200,12,'cap.jpg'),
                                                                                                              (4,'Кепка','бейсболка большого размера',1200,12,'cap.jpg'),
                                                                                                              (5,'Кепка','бейсболка большого размера',1200,12,'cap.jpg'),
                                                                                                              (6,'Кепка','бейсболка большого размера',1200,12,'cap.jpg'),
                                                                                                              (7,'Кепка','бейсболка большого размера',1200,12,'cap.jpg'),
                                                                                                              (8,'Кепка','бейсболка большого размера',1200,12,'cap.jpg'),
                                                                                                              (9,'Кепка','бейсболка большого размера',1200,12,'cap.jpg'),
                                                                                                              (10,'Кепка','бейсболка большого размера',1200,12,'cap.jpg'),
                                                                                                              (11,'Кепка','бейсболка большого размера',1200,12,'cap.jpg'),
                                                                                                              (12,'Кепка','бейсболка большого размера',1200,12,'cap.jpg'),
                                                                                                              (13,'Кепка','бейсболка большого размера',1200,12,'cap.jpg'),
                                                                                                              (14,'Кепка','бейсболка большого размера',1200,12,'cap.jpg');
commit;
select *  from items;
