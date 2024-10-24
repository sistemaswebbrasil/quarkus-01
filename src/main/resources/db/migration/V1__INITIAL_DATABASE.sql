CREATE TABLE calendar_event
(
    all_day  BIT(1) NULL,
    end_date datetime NULL,
    id       BIGINT AUTO_INCREMENT NOT NULL,
    start    datetime NULL,
    title    VARCHAR(255) NULL,
    url      VARCHAR(255) NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);

CREATE TABLE extended_props
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    calendar      VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    location      VARCHAR(255) NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);

CREATE TABLE extended_props_guests
(
    extended_props_id BIGINT NOT NULL,
    guest_id          BIGINT NOT NULL
);

CREATE TABLE guest
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);

ALTER TABLE extended_props_guests
    ADD CONSTRAINT UKtg9ennhod8a99drci7nsp906u UNIQUE (guest_id);

ALTER TABLE extended_props_guests
    ADD CONSTRAINT FKq1qtudwn04o3tlav6g73qhg2n FOREIGN KEY (extended_props_id) REFERENCES extended_props (id) ON DELETE NO ACTION;

CREATE INDEX FKq1qtudwn04o3tlav6g73qhg2n ON extended_props_guests (extended_props_id);

ALTER TABLE extended_props_guests
    ADD CONSTRAINT FKs8esvl4j8c66sv0bqdkj28akd FOREIGN KEY (guest_id) REFERENCES guest (id) ON DELETE NO ACTION;
