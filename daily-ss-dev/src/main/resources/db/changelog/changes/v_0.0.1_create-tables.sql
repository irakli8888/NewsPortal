--liquibase formatted sql

--changeset Aleksey Romodin:01-02-2021-001
CREATE TABLE account (
                         id BIGSERIAL PRIMARY KEY,
                         username VARCHAR(50) NOT NULL UNIQUE,
                         password VARCHAR(500) NOT NULL,
                         date_time_last_input TIMESTAMPTZ NOT NULL,
                         login_attempt_input SMALLINT DEFAULT 0 NOT NULL
);
--rollback drop table account;

--changeset Aleksey Romodin:01-02-2021-002
CREATE TABLE person (
                        id BIGSERIAL PRIMARY KEY,
                        account_id BIGINT NOT NULL UNIQUE,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        first_name VARCHAR(50) NOT NULL,
                        middle_name VARCHAR(50),
                        last_name VARCHAR(50) NOT NULL,
                        date_of_birth DATE NOT NULL,
                        country VARCHAR(100),
                        city VARCHAR(100)
);
--rollback drop table person;

--changeset Aleksey Romodin:01-02-2021-003
CREATE TABLE feedback (
                          id BIGSERIAL PRIMARY KEY,
                          news_id BIGINT NOT NULL,
                          comment VARCHAR(400) NOT NULL,
                          count_likes BIGINT DEFAULT 0 NOT NULL CHECK ( count_likes >= 0 ),
                          count_dislikes BIGINT DEFAULT 0 NOT NULL CHECK ( count_dislikes >= 0 ),
                          account_id BIGINT NOT NULL,
                          author_name VARCHAR(100),
                          parent_id BIGINT DEFAULT 0 NOT NULL,
                          date_time_created  TIMESTAMPTZ DEFAULT now() NOT NULL,
                          date_time_modified TIMESTAMPTZ DEFAULT now() NOT NULL
);
--rollback drop table feedback;

--changeset Aleksey Romodin:01-02-2021-004
CREATE TABLE likes_news (
                            id BIGSERIAL PRIMARY KEY,
                            account_id BIGINT NOT NULL,
                            content_id BIGINT NOT NULL,
                            voice BOOLEAN DEFAULT false NOT NULL,
                            date_time_modified TIMESTAMPTZ DEFAULT now() NOT NULL
);
--rollback drop table likes_news;

--changeset Aleksey Romodin:01-02-2021-005
CREATE TABLE likes_feedback (
                                id BIGSERIAL PRIMARY KEY,
                                account_id BIGINT NOT NULL,
                                content_id BIGINT NOT NULL,
                                voice BOOLEAN DEFAULT false NOT NULL,
                                date_time_modified TIMESTAMPTZ DEFAULT now() NOT NULL
);
--rollback drop table likes_feedback;


--changeset Aleksey Romodin:01-02-2021-006
CREATE TABLE news (
                      id BIGSERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      text_news TEXT NOT NULL,
                      account_id BIGINT NOT NULL,
                      author_name VARCHAR(100),
                      count_likes BIGINT DEFAULT 0 NOT NULL CHECK ( count_likes >= 0 ),
                      count_dislikes BIGINT DEFAULT 0 NOT NULL CHECK ( count_dislikes >= 0 ),
                      count_comments BIGINT DEFAULT 0 NOT NULL CHECK ( count_comments >= 0 ),
                      count_media BIGINT DEFAULT 0 NOT NULL CHECK ( count_media >= 0 ),
                      date_time_created TIMESTAMPTZ DEFAULT now() NOT NULL,
                      date_time_modified TIMESTAMPTZ DEFAULT now() NOT NULL
);
--rollback drop table news;

--changeset Aleksey Romodin:01-02-2021-007
CREATE TABLE news_tag (
                          news_id BIGINT NOT NULL,
                          tag_id BIGINT NOT NULL
);
--rollback drop table news_tag;

--changeset Aleksey Romodin:01-02-2021-008
CREATE TABLE media (
                       id BIGSERIAL PRIMARY KEY,
                       news_id BIGINT NOT NULL,
                       type_media VARCHAR(50) NOT NULL
);
--rollback drop table media;

--changeset Aleksey Romodin:01-02-2021-009
CREATE TABLE tag (
                     id BIGSERIAL PRIMARY KEY,
                     tag_name VARCHAR(50) NOT NULL UNIQUE
);
--rollback drop table tag;

--changeset Aleksey Romodin:01-02-2021-010
ALTER TABLE person ADD CONSTRAINT fk_person_account
    FOREIGN KEY (account_id) REFERENCES account (id)
        ON UPDATE CASCADE ON DELETE CASCADE;
--rollback alter table person drop constraint fk_person_account;

--changeset Aleksey Romodin:01-02-2021-011
ALTER TABLE feedback ADD CONSTRAINT fk_feedback_news
    FOREIGN KEY (news_id) REFERENCES news (id)
        ON UPDATE CASCADE ON DELETE CASCADE;
--rollback alter table feedback drop constraint fk_feedback_news;

--changeset Aleksey Romodin:01-02-2021-012
ALTER TABLE feedback ADD CONSTRAINT fk_feedback_account
    FOREIGN KEY (account_id) REFERENCES account (id)
        ON UPDATE CASCADE ON DELETE RESTRICT;
--rollback alter table feedback drop constraint fk_feedback_account;

--changeset Aleksey Romodin:01-02-2021-013
ALTER TABLE likes_news ADD CONSTRAINT fk_likes_news_account
    FOREIGN KEY (account_id) REFERENCES account (id)
        ON UPDATE CASCADE ON DELETE CASCADE;
--rollback alter table likes drop constraint fk_likes_news_account;

--changeset Aleksey Romodin:01-02-2021-014
ALTER TABLE likes_news ADD CONSTRAINT fk_likes_news_news
    FOREIGN KEY (content_id) REFERENCES news (id)
        ON UPDATE CASCADE ON DELETE CASCADE;
--rollback alter table likes drop constraint fk_likes_news_news;

--changeset Aleksey Romodin:01-02-2021-015
ALTER TABLE likes_feedback ADD CONSTRAINT fk_likes_feedback_account
    FOREIGN KEY (account_id) REFERENCES account (id)
        ON UPDATE CASCADE ON DELETE CASCADE;
--rollback alter table likes drop constraint fk_likes_feedback_account;

--changeset Aleksey Romodin:01-02-2021-016
ALTER TABLE likes_feedback ADD CONSTRAINT fk_likes_feedback_news
    FOREIGN KEY (content_id) REFERENCES feedback (id)
        ON UPDATE CASCADE ON DELETE CASCADE;
--rollback alter table likes drop constraint fk_likes_feedback_news;

--changeset Aleksey Romodin:01-02-2021-017
ALTER TABLE news ADD CONSTRAINT fk_news_account
    FOREIGN KEY (account_id) REFERENCES account (id)
        ON UPDATE CASCADE ON DELETE RESTRICT;
--rollback alter table likes drop constraint fk_news_account;

--changeset Aleksey Romodin:01-02-2021-018
ALTER TABLE news_tag ADD CONSTRAINT fk_news_tag_news
    FOREIGN KEY (news_id) REFERENCES news (id)
        ON UPDATE CASCADE ON DELETE CASCADE;
--rollback alter table tag drop constraint fk_news_tag_news;

--changeset Aleksey Romodin:01-02-2021-019
ALTER TABLE news_tag ADD CONSTRAINT fk_news_tag_tag
    FOREIGN KEY (tag_id) REFERENCES tag (id)
        ON UPDATE CASCADE ON DELETE CASCADE;
--rollback alter table tag drop constraint fk_news_tag_tag;

--changeset Aleksey Romodin:01-02-2021-020
ALTER TABLE media ADD CONSTRAINT fk_media_news
    FOREIGN KEY (news_id) REFERENCES news (id)
        ON UPDATE CASCADE ON DELETE CASCADE;
--rollback alter table media drop constraint fk_media_news;




