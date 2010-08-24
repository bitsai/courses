set autocommit on
drop table RAWSCORES;
create table RAWSCORES(
        SSN	VARCHAR2(4) primary key,
        Lname   VARCHAR2(11),
        Fname   VARCHAR2(11),
        Section VARCHAR2(3),
	HW1     NUMBER,
	HW2a    NUMBER,
	HW2b 	NUMBER,
	Midterm NUMBER,
	HW3	NUMBER,
	FExam	NUMBER
        );
INSERT INTO rawscores VALUES ('9176', 'Epp', 'Eric', '415', 99, 79, 31, 99, 119, 199);
INSERT INTO rawscores VALUES ('5992', 'Lin', 'Linda', '415', 98, 71, 29, 83, 105, 171);
INSERT INTO rawscores VALUES ('3774', 'Adams', 'Abigail', '315', 85, 63, 27, 88, 112, 180);
INSERT INTO rawscores VALUES ('1212', 'Osborne', 'Danny', '315', 29, 31, 12, 66, 61, 106);
INSERT INTO rawscores VALUES ('4198', 'Wilson', 'Amanda', '315', 84, 73, 27, 87, 115, 172);
INSERT INTO rawscores VALUES ('1006', 'Nielsen', 'Bridget', '415', 93, 76, 28, 95, 111, 184);
INSERT INTO rawscores VALUES ('8211', 'Clinton', 'Chelsea', '415', 100, 80, 32, 100, 120, 200);
INSERT INTO rawscores VALUES ('1180', 'Quayle', 'Jonathan', '315', 50, 40, 16, 55, 68, 181);
INSERT INTO rawscores VALUES ('0001', 'TOTAL', 'POINTS', '415', 100, 80, 32, 100, 120, 200);
INSERT INTO rawscores VALUES ('0002', 'WEIGHT','OFSCORE','415', 0.1,0.1,0.05,0.25,0.1,0.4);

drop table PASSWORDS;
create table PASSWORDS (
  CurPasswords  varchar2(15)
);

INSERT INTO passwords VALUES ('OpenSesame');
INSERT INTO passwords VALUES ('GuessMe');
INSERT INTO passwords VALUES ('ImTheTA');
