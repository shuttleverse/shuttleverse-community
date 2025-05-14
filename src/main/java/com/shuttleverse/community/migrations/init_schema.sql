CREATE TABLE club
(
    club_id     UUID                        NOT NULL,
    name        VARCHAR(100)                NOT NULL,
    location    VARCHAR(255),
    description VARCHAR(1000),
    website     VARCHAR(255),
    phone       VARCHAR(20),
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    creator_id  UUID                        NOT NULL,
    owner_id    UUID                        NOT NULL,
    is_verified BOOLEAN                     NOT NULL,
    CONSTRAINT pk_club PRIMARY KEY (club_id)
);

CREATE TABLE coach
(
    coach_id         UUID                        NOT NULL,
    club_id          UUID                        NOT NULL,
    name             VARCHAR(100)                NOT NULL,
    location         VARCHAR(255),
    description      VARCHAR(1000),
    experience_years INTEGER,
    other_contacts   VARCHAR(100),
    phone_number     VARCHAR(20),
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    owner_id         UUID,
    is_verified      BOOLEAN                     NOT NULL,
    creator_id       UUID,
    CONSTRAINT pk_coach PRIMARY KEY (coach_id)
);

CREATE TABLE coach_schedule
(
    schedule_id  UUID                        NOT NULL,
    coach_id     UUID                        NOT NULL,
    day_of_week  INTEGER                     NOT NULL,
    start_time   VARCHAR(10)                 NOT NULL,
    end_time     VARCHAR(10)                 NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    upvotes      INTEGER                     NOT NULL,
    is_verified  BOOLEAN                     NOT NULL,
    submitted_by UUID,
    CONSTRAINT pk_coach_schedule PRIMARY KEY (schedule_id)
);

CREATE TABLE court
(
    court_id     UUID                        NOT NULL,
    name         VARCHAR(255),
    location     VARCHAR(255),
    description  VARCHAR(255),
    website      VARCHAR(255),
    phone_number VARCHAR(255),
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    creator_id   UUID,
    owner_id     UUID,
    CONSTRAINT pk_court PRIMARY KEY (court_id)
);

CREATE TABLE court_price
(
    price_id    UUID                        NOT NULL,
    court_id    UUID,
    price       DOUBLE PRECISION            NOT NULL,
    duration    INTEGER                     NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    upvotes     INTEGER                     NOT NULL,
    is_verified BOOLEAN                     NOT NULL,
    CONSTRAINT pk_court_price PRIMARY KEY (price_id)
);

CREATE TABLE court_schedule
(
    schedule_id  UUID                        NOT NULL,
    court_id     UUID,
    day_of_week  INTEGER                     NOT NULL,
    open_time    VARCHAR(255)                NOT NULL,
    close_time   VARCHAR(255)                NOT NULL,
    is_verified  BOOLEAN                     NOT NULL,
    upvotes      INTEGER                     NOT NULL,
    submitted_by UUID,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_court_schedule PRIMARY KEY (schedule_id)
);

CREATE TABLE ownership_claim
(
    claim_id    UUID                        NOT NULL,
    entity_type VARCHAR(50)                 NOT NULL,
    entity_id   UUID                        NOT NULL,
    proof       VARCHAR(2000),
    admin_id    UUID,
    admin_notes VARCHAR(2000),
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_ownership_claim PRIMARY KEY (claim_id)
);

CREATE TABLE stringer
(
    stringer_id        UUID                        NOT NULL,
    club_id            UUID,
    name               VARCHAR(255),
    description        VARCHAR(255),
    other_contacts     VARCHAR(255),
    phone_number       VARCHAR(255),
    additional_details VARCHAR(255),
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    creator_id         UUID,
    owner_id           UUID,
    CONSTRAINT pk_stringer PRIMARY KEY (stringer_id)
);

CREATE TABLE stringer_price
(
    price_id     UUID                        NOT NULL,
    stringer_id  UUID                        NOT NULL,
    string_name  VARCHAR(100)                NOT NULL,
    price        DOUBLE PRECISION            NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    upvotes      INTEGER                     NOT NULL,
    is_verified  BOOLEAN                     NOT NULL,
    submitted_by UUID,
    CONSTRAINT pk_stringer_price PRIMARY KEY (price_id)
);

CREATE TABLE users
(
    user_id    UUID                        NOT NULL,
    username   VARCHAR(50)                 NOT NULL,
    email      VARCHAR(100)                NOT NULL,
    bio        VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_admin   BOOLEAN                     NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE coach
    ADD CONSTRAINT FK_COACH_ON_CLUB FOREIGN KEY (club_id) REFERENCES club (club_id);

ALTER TABLE coach
    ADD CONSTRAINT FK_COACH_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (user_id);

ALTER TABLE coach
    ADD CONSTRAINT FK_COACH_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (user_id);

ALTER TABLE coach_schedule
    ADD CONSTRAINT FK_COACH_SCHEDULE_ON_COACH FOREIGN KEY (coach_id) REFERENCES coach (coach_id);

ALTER TABLE coach_schedule
    ADD CONSTRAINT FK_COACH_SCHEDULE_ON_SUBMITTED_BY FOREIGN KEY (submitted_by) REFERENCES users (user_id);

ALTER TABLE court
    ADD CONSTRAINT FK_COURT_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (user_id);

ALTER TABLE court
    ADD CONSTRAINT FK_COURT_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (user_id);

ALTER TABLE court_price
    ADD CONSTRAINT FK_COURT_PRICE_ON_COURT FOREIGN KEY (court_id) REFERENCES court (court_id);

ALTER TABLE court_schedule
    ADD CONSTRAINT FK_COURT_SCHEDULE_ON_COURT FOREIGN KEY (court_id) REFERENCES court (court_id);

ALTER TABLE court_schedule
    ADD CONSTRAINT FK_COURT_SCHEDULE_ON_SUBMITTED_BY FOREIGN KEY (submitted_by) REFERENCES users (user_id);

ALTER TABLE ownership_claim
    ADD CONSTRAINT FK_OWNERSHIP_CLAIM_ON_ADMIN FOREIGN KEY (admin_id) REFERENCES users (user_id);

ALTER TABLE stringer
    ADD CONSTRAINT FK_STRINGER_ON_CLUB FOREIGN KEY (club_id) REFERENCES club (club_id);

ALTER TABLE stringer
    ADD CONSTRAINT FK_STRINGER_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (user_id);

ALTER TABLE stringer
    ADD CONSTRAINT FK_STRINGER_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (user_id);

ALTER TABLE stringer_price
    ADD CONSTRAINT FK_STRINGER_PRICE_ON_STRINGER FOREIGN KEY (stringer_id) REFERENCES stringer (stringer_id);

ALTER TABLE stringer_price
    ADD CONSTRAINT FK_STRINGER_PRICE_ON_SUBMITTED_BY FOREIGN KEY (submitted_by) REFERENCES users (user_id);