INSERT INTO account(date_time_last_input)
VALUES (now()),
       (now()),
       (now()),
       (now()),
       (now());

INSERT INTO account(username, password, date_time_last_input)
VALUES ('anonumous', 'anonumous', now()),
       ('admin', 'admin', now()),
       ('ivanov', 'ivanov', now()),
       ('sidorov', 'sidorov', now()),
       ('petrov', 'petrov', now()) ON CONFLICT DO NOTHING;

INSERT INTO news(title, text_news, account_id, author_name)
VALUES ('Новости',
        'Оперативная информация, которая представляет политический, ' ||
        'социальный или экономический интерес для аудитории в своей ' ||
        'свежести, то есть сообщения о событиях, произошедших ' ||
        'недавно или происходящих в данный момент. Также новостями ' ||
        'называют программы (собрание нескольких новостей) на телевидении ' ||
        'и радио, а в печатной прессе или на веб-сайтах — сводки новостей, ' ||
        'в специальной рубрике в газете.',
        (SELECT id FROM account WHERE account.username = 'anonumous'),
        'Василий') ON CONFLICT DO NOTHING;

INSERT INTO person(account_id, email, first_name, middle_name, last_name, date_of_birth, country, city)
VALUES ((SELECT id FROM account WHERE username = 'admin'), 'admin@newportal.com', 'Андрей', 'Федорович', 'Главный', '20/01/1980', 'Россия', 'Москва'),
       ((SELECT id FROM account WHERE username = 'ivanov'), 'ivanov@newportal.com', 'Иван', 'Иванович', 'Иванов', '01/03/1985', 'Россия', 'Самара'),
       ((SELECT id FROM account WHERE username = 'sidorov'), 'sidorov@newportal.com', 'Константин', 'Иванович', 'Сидоров', '08/08/1987', 'Россия', 'Тверь'),
       ((SELECT id FROM account WHERE username = 'petrov'), 'petrov@newportal.com', 'Александр', 'Петрович', 'Петров', '18/08/1991', 'Россия', 'Казань')
ON CONFLICT DO NOTHING;
