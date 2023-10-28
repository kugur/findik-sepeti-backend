create sequence category_seq start with 10 increment by 10;
create sequence custom_user_seq start with 10 increment by 50;
create sequence order_item_seq start with 1 increment by 50;
create sequence product_seq start with 1 increment by 50;
create sequence role_seq start with 10 increment by 10;
create sequence sales_order_seq start with 1 increment by 50;
create sequence shipping_seq start with 1 increment by 50;
create table category (id bigint not null, name varchar(255), primary key (id));
create table custom_user (id bigint not null, version bigint, address varchar(255), email varchar(255), first_name varchar(255), gender varchar(255) check (gender in ('MALE','FEMALE')), last_name varchar(255), password varchar(255), primary key (id));
create table order_item (quantity integer not null, id bigint not null, product_id bigint, version bigint, primary key (id));
create table product (price numeric(10,2), category_id bigint not null, id bigint not null, description varchar(2048), image_url varchar(255), name varchar(255), primary key (id));
create table role (id bigint not null, version bigint, name varchar(255), primary key (id));
create table sales_order (created_date timestamp(6), id bigint not null, shipping_id bigint unique, user_id bigint, version bigint, status varchar(255) check (status in ('ORDER_CREATED','PLACE_ORDERED','INVALID_ORDER','FAILED_PAYMENT')), primary key (id));
create table sales_order_order_items (order_id bigint not null, order_items_id bigint not null unique);
create table shipping (id bigint not null, version bigint, address varchar(255), name varchar(255), note varchar(255), primary key (id));
create table user_role (role_id bigint not null, user_id bigint not null, primary key (role_id, user_id));
alter table if exists order_item add constraint FK551losx9j75ss5d6bfsqvijna foreign key (product_id) references product;
alter table if exists product add constraint FK1mtsbur82frn64de7balymq9s foreign key (category_id) references category;
alter table if exists sales_order add constraint FK86a0pf3ybqxdwfq99qcghspdf foreign key (shipping_id) references shipping;
alter table if exists sales_order add constraint FKajn31r9cw127rxfpd51kt005r foreign key (user_id) references custom_user;
alter table if exists sales_order_order_items add constraint FKqr09s9hb0qt4e3am3hmk5yipm foreign key (order_items_id) references order_item;
alter table if exists sales_order_order_items add constraint FKn058sotnokyo5lrxm3ndw5waj foreign key (order_id) references sales_order;
alter table if exists user_role add constraint FKa68196081fvovjhkek5m97n3y foreign key (role_id) references role;
alter table if exists user_role add constraint FKj24i4lfnnewj2k7y9g3csui1n foreign key (user_id) references custom_user;