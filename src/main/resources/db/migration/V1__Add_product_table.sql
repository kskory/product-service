create table product
(
    id      bigint auto_increment not null primary key,
    sku     varchar(255)          not null unique,
    name    varchar(255)          not null,
    price   bigint                not null,
    deleted bit                   not null,
    created datetime(6)           null
);
create index product_deleted_index
    on product (deleted);

