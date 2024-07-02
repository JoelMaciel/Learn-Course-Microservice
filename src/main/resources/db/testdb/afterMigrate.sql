

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

