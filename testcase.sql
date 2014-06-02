/* single table */
select * from student;
select name from student;
select id from student where name = 'mr chen' or id = 9962231;
select s.id from student as s where s.id = 9962231;

/* double table */
select * from student, professor;
select name from student, professor;
select name, professorname from student, professor;
select * from student, professor where name = 'mr chen' or professorname = 'hk hon';
select * from student, professor where name = 'mr chen' and professorname = 'hk hon';
select s.name, p.professorname from student as s, professor as p;
select * from student, professor where id = age;

/*double table with the same field name*/
select * from student, enroll;
select id from student, enroll;
select s.id, e.id from student as s, enroll as e;
select * from student as s, enroll as e where s.id = e.id;
select * from student as s, enroll as e where s.id = e.id or e.classname = 'ml';
select * from student as s, enroll as e where s.id = e.id and e.classname = 'ml';
select s1.name, s1.id, s2.id from student as s1, student as s2;
select * from student as s1, student as s2;

/* aggregation function */
select sum(id) from student as s1;
select count(*) from student as s1;
select count(*) from student as s, enroll as e where s.id = e.id; (Incorrect Format.)

/* range query */
select * from student where id > 10;
select name from student where id < 0;
select * from student where name > 'aaa';
select * from student, professor where name > 'aaa';
select * from student, professor where name > 'aaa' and professorname < 'hk hon';
select * from student, professor where name < 'aaa' and professorname = 'hk hon';
select * from student, professor where id < age or id <> 9962231;
select * from student, professor where id > 9962230 and id < 9962232;
select * from student, professor where id < 9962230 or id > 9962232;
select * from student as s, enroll as e where s.id > e.id;
select * from student as s, enroll as e where s.id = e.id and e.classname < 'ml';


/* one table star */
select s.*, e.id from student as s, enroll as e;