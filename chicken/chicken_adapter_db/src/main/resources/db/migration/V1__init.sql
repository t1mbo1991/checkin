drop table if exists HOLIDAY;
drop table if exists STUDENT_EXAM;
drop table if exists STUDENT;
drop table if exists EXAM;

create table STUDENT
(
    id             int auto_increment primary key,
    git_hub_handle varchar(40) unique not null
);

create table EXAM
(
    id          int auto_increment primary key,
    is_presence bool         not null,
    event_id    varchar(10)  not null,
    name        varchar(255) not null,
    date        date         not null,
    start       time         not null,
    end         time         not null
);

create table STUDENT_EXAM
(
    student int references STUDENT (id),
    exam    int references EXAM (id),
    primary key (student, exam)
);

create table HOLIDAY
(
    id      int auto_increment primary key,
    student int references STUDENT (id),
    date    date not null,
    start   time not null,
    end     time not null
);