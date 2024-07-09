

DELETE FROM COURSE;
DELETE FROM USERS;
DELETE FROM COURSES_USERS;

INSERT INTO course (course_id, title, subtitle, price, creation_date)
VALUES (
    '0a657c35-e0ed-44e0-b25c-3afbeadd643e',
    'Java Developer',
    'Java advanced  for developers',
     99.99,
    '2024-07-01 10:30:00'
);

INSERT INTO USERS (user_id, email, cpf, user_type, full_name)
VALUES (
    'e53b4d24-6b49-4b7e-9f0b-69f77d4d64b8',
    'admin@example.com',
    '008.655.700-94',
    'ADMIN',
    'Maciel Viana Admin'
);

