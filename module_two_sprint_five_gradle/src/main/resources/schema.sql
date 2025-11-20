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
                                    price       bigint default 0,
                                    Quantity    bigint default 0,
                                    imgPath     varchar NOT NULL);

drop table if exists orders cascade ;
create table if not exists orders(
                                    id           bigserial primary key,
                                    created_at   timestamp with time zone default current_timestamp,
                                    user_id      bigint NOT NULL default 1,
                                    FOREIGN KEY  (user_id) REFERENCES users(id) ON DELETE CASCADE);

drop table if exists in_card cascade;
create table if not exists in_card(
                                    id           bigserial primary key,
                                    user_id      bigint NOT NULL default 1,
                                    item_id      bigint NOT NULL,
                                    count        bigint NOT NULL default 0,
                                    FOREIGN KEY  (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                    FOREIGN KEY  (item_id) REFERENCES items (id) ON DELETE CASCADE);

drop table if exists in_order cascade;
create table if not exists in_order(
                                    order_id      bigint NOT NULL,
                                    item_id       bigint NOT NULL default 1,
                                    PRIMARY KEY   (order_id, item_id),
                                    FOREIGN KEY   (order_id)    REFERENCES orders(id) ON DELETE CASCADE,
                                    FOREIGN KEY   (item_id)     REFERENCES items (id) ON DELETE CASCADE);

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

insert into in_card values  (1,1,5, 3),
                            (2,1,6, 4);

commit;


select i.*, coalesce(ic.count, 0) as count from items i left join (select * from in_card where user_id=1) ic on i.id = ic.item_id ORDER BY i.id;
