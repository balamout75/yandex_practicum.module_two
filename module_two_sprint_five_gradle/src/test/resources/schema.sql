create sequence items_sequence increment by 1;
create sequence orders_sequence increment by 1;
create sequence users_sequence increment by 1;

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
                         (2,'Кепка1','бейсболка большого размера',1200,12,'cap.jpg'),
                         (3,'Кепка2','бейсболка большого размера',1200,12,'cap.jpg'),
                         (4,'Кепка3','бейсболка большого размера',1200,12,'cap.jpg'),
                         (5,'Кепка4','бейсболка большого размера',1200,12,'cap.jpg'),
                         (6,'Кепка5','бейсболка большого размера',1200,12,'cap.jpg'),
                         (7,'Кепка6','бейсболка большого размера',1200,12,'cap.jpg'),
                         (8,'Кепка7','бейсболка большого размера',1200,12,'cap.jpg'),
                         (9,'Кепка8','бейсболка большого размера',1200,12,'cap.jpg'),
                         (10,'Кепка9','бейсболка большого размера',1200,12,'cap.jpg'),
                         (11,'Кепка10','бейсболка большого размера',1200,12,'cap.jpg'),
                         (12,'Кепка11','бейсболка большого размера',1200,12,'cap.jpg'),
                         (13,'Кепка12','бейсболка большого размера',1200,12,'cap.jpg'),
                         (14,'Кепка13','бейсболка большого размера',1200,12,'cap.jpg');
