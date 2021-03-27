--liquibase formatted sql

--changeset Ruslan Valiev:08-02-2021-021
INSERT INTO public.account(
	username, password, date_time_last_input, login_attempt_input)
	VALUES ('admin', 'admin', '2020-02-08 00:00:00 MSK', 0);
