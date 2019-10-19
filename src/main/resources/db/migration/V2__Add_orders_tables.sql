create table `order`
(
    id          bigint auto_increment primary key,
    buyer_email varchar(255) not null,
    created     datetime(6)  not null
);

create table order_item
(
    id    bigint auto_increment primary key,
    name  varchar(255) not null,
    price bigint       not null,
    sku   varchar(255) not null
);

create table order_order_items
(
    order_id       bigint not null,
    order_items_id bigint not null,
    constraint UK_1rracteuxt3a7vtc8brtu0u6q
        unique (order_items_id),
    constraint FK2713hfg6inwk6dwhys79p9j7r
        foreign key (order_id) references `order` (id),
    constraint FKf50spc9rq8otpxsfivlte3mum
        foreign key (order_items_id) references order_item (id)
);

