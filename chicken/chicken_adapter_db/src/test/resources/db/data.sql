insert into STUDENT (id, git_hub_handle)
values (1, 'tiemotm'),
       (2, 'jatsc104'),
       (3, 't1mbo1991'),
       (4, 'nreck1');

insert into EXAM (id, is_presence, event_id, name, date, start, end)
values (1, false, '123', 'Propra', '2022-03-22', '13:00:00', '14:00:00'),
       (2, false, '1234', 'Propra 2', '2022-03-23', '08:00:00', '10:30:00'),
       (3, true, '223', 'Aldat', '2022-03-23', '08:00:00', '10:30:00');

insert into STUDENT_EXAM (student, exam)
values (1, 1),
       (1, 2),
       (2, 2),
       (4, 2);

insert into HOLIDAY (id, student, date, start, end)
values (1, 1, '2022-03-23', '09:30:00', '10:00:00'),
       (2, 2, '2022-03-23', '10:30:00', '11:00:00'),
       (3, 3, '2022-03-24', '09:30:00', '10:00:00'),
       (4, 4, '2022-03-23', '11:30:00', '10:00:00');