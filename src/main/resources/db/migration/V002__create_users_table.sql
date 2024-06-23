CREATE TABLE USERS (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(80) NOT NULL,
    user_type VARCHAR(50) NOT NULL ,
    cpf VARCHAR(50) NOT NULL
);

CREATE TABLE COURSES_USERS (
    course_id UUID NOT NULL,
    user_id UUID NOT NULL,
    PRIMARY KEY (course_id, user_id),
    CONSTRAINT fk_course
        FOREIGN KEY (course_id)
        REFERENCES COURSE (course_id) ON DELETE CASCADE ,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES USERS (user_id) ON DELETE CASCADE
);