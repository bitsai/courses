

spool RESULT2.LST
set termout on
set autocommit on
prompt
prompt
prompt
prompt
prompt
prompt
prompt
prompt
prompt IGNORE TABLE CREATION ERROR MESSAGES.
prompt
prompt
prompt
prompt
drop table FACULTY;
create table FACULTY (
        FacID      NUMBER,
        Lname           VARCHAR2(15),
        Fname           VARCHAR2(15),
        Rank            VARCHAR2(15),
        Sex             VARCHAR2(1),
        Phone           NUMBER,
        Room            VARCHAR2(5),
        Building        VARCHAR2(13)
        );

drop table COURSE;
create table COURSE (
        CID             VARCHAR2(7),
        CName           VARCHAR2(40),
        Credits         NUMBER,
        Instructor      NUMBER,
        Days            VARCHAR2(5),
        Hours           VARCHAR2(11),
        DNO             NUMBER
        );

drop table ENROLLED_IN;
create table ENROLLED_IN (
        StuID           NUMBER,
        CID             VARCHAR2(7),
        Grade           VARCHAR2(2)
        );

drop table DEPARTMENT;
create table DEPARTMENT (
        DNO             NUMBER,
        Division        VARCHAR2(2),
        DName           VARCHAR2(25),
        Room            VARCHAR2(5),
        Building        VARCHAR2(13),
        DPhone          NUMBER
        );

drop table MINOR_IN;
create table MINOR_IN (
        StuID      NUMBER,
        DNO        NUMBER
        );

drop table CHAIRED_BY;
create table CHAIRED_BY (
        DNO        NUMBER,
        FacID      NUMBER
        );

drop table MEMBER_OF;
create table MEMBER_OF (
        FacID           NUMBER,
        DNO             NUMBER,
        Appt_Type       VARCHAR2(15)
        );

drop table PARENT_OF;
create table PARENT_OF (
        FacID      NUMBER,
        StuID      NUMBER
        );

drop table STUDENT;
create table STUDENT (
       StuID       NUMBER,
       LName       VARCHAR2(12),
       Fname       VARCHAR2(12),
       Age         NUMBER,
       Sex         VARCHAR2(1),
       Major       NUMBER,
       Advisor     NUMBER,
       city_code   varchar2(3)
       );

drop table DORM ;
create table DORM (
  dormid number,
  dorm_name varchar2(20),
  student_capacity number,
  gender varchar2(1)
) ;

drop table DORM_AMENITY ;
create table DORM_AMENITY (
  amenid number,
  amenity_name varchar2(25)
) ;

drop table HAS_AMENITY ;
create table HAS_AMENITY (
  dormid number,
  amenid number
);

drop table LIVES_IN ;
create table LIVES_IN (
  stuid number,
  dormid number,
  room_number number
) ;

drop table ACTIVITY ;
create table ACTIVITY (
  actid number ,
  activity_name varchar2(25)
);

drop table PARTICIPATES_IN ;
create table PARTICIPATES_IN (
  stuid number,
  actid number
);

drop table FACULTY_PARTICIPATES_IN ;
create table FACULTY_PARTICIPATES_IN (
  FacID number,
  actid number
);


drop table DIRECT_DISTANCE ;
create table DIRECT_DISTANCE (
  city1_code varchar2(3) ,
  city2_code varchar2(3) ,
  distance number
) ;

drop table CITY ;
create table CITY (
  city_code varchar2(3),
  city_name varchar2(25),
  state varchar2(2),
  country varchar2(25),
  latitude number ,
  longitude number
) ;

drop table ALLERGY;
create table ALLERGY (
  AllergyName varchar2(20),
  AllergyType varchar2(15)
);

drop table HAS_ALLERGY;
create table HAS_ALLERGY (
  StuID number,
  AllergyName varchar2(20)
);

drop table PREFERENCES;
create table PREFERENCES (
  StuID number,
  SleepHabits varchar2(20),
  MusicType varchar2(20),
  Smoking varchar2(10)
);

drop table LIKES;
create table LIKES (
  WhoLikes   number,
  WhoIsLiked number
);

drop table LOVES;
create table LOVES (
  WhoLoves   number,
  WhoIsLoved number
);



insert into FACULTY  values ( 1082, 'Giuliano', 'Mark', 'Instructor', 'M', 2424
, '224', 'NEB');
insert into FACULTY  values ( 1121, 'Goodrich', 'Michael', 'Professor', 'M', 
3593, '219', 'NEB');
insert into FACULTY  values ( 1148, 'Masson', 'Gerald', 'Professor', 'M', 3402,
 '224B', 'NEB');
insert into FACULTY  values ( 1193, 'Jones', 'Stacey', 'Instructor', 'F', 3550,
 '224', 'NEB');
insert into FACULTY  values ( 2192, 'Yarowsky', 'David', 'AsstProf', 'M', 6587,
 '324', 'NEB');
insert into FACULTY  values ( 3457, 'Smith', 'Scott', 'AssocProf', 'M', 1035, '
318', 'NEB');
prompt Going into silent mode.
set termout off
insert into FACULTY  values ( 4230, 'Houlahan', 'Joanne', 'Instructor', 'F', 
1260, '328', 'NEB');
insert into FACULTY  values ( 6112, 'Beach', 'Louis', 'Instructor', 'M', 1838,
'207', 'NEB');
insert into FACULTY  values ( 7712, 'Awerbuch', 'Baruch', 'Professor', 'M', 
2105, '220', 'NEB');
insert into FACULTY  values ( 7792, 'Brill', 'Eric', 'AsstProf', 'M', 2303,   
'324B', 'NEB');
insert into FACULTY  values ( 7723, 'Taylor', 'Russell', 'Professor', 'M', 
2435, '317', 'NEB');
insert into FACULTY  values ( 8114, 'Angelopoulou', 'Ellie', 'Instructor', 'F',
 2152, '316', 'NEB');
insert into FACULTY  values ( 8423, 'Kumar', 'Subodh', 'AsstProf', 'M', 2522, 
'218', 'NEB');
insert into FACULTY  values ( 8721, 'Wolff', 'Lawrence', 'AssocProf', 'M', 
2342, '316', 'NEB');
insert into FACULTY  values ( 8741, 'Salzberg', 'Steven', 'AssocProf', 'M', 
2641,   '324A', 'NEB');
insert into FACULTY  values ( 8918, 'Amir', 'Yair', 'AsstProf', 'M', 2672, 
'308', 'NEB');
insert into FACULTY  values ( 9172, 'Kosaraju', 'Rao', 'Professor', 'M', 2757,
'319', 'NEB');
insert into FACULTY  values ( 9826, 'Delcher', 'Arthur', 'Instructor', 'M', 
2956, '329', 'NEB');
insert into FACULTY  values ( 1172, 'Runolfsson', 'Thordur', 'AssocProf', 'M',
3121, '119', 'Barton');
insert into FACULTY  values ( 1177, 'Naiman', 'Daniel', 'Professor', 'M', 3571,
 '288', 'Krieger');
insert into FACULTY  values ( 1823, 'Davidson', 'Frederic', 'Professor', 'M', 
5629, '119', 'Barton');
insert into FACULTY  values ( 2028, 'Brody', 'William', 'Professor', 'M', 6073,
 '119', 'Barton');
insert into FACULTY  values ( 2119, 'Meyer', 'Gerard', 'Professor', 'M', 6350,
'119', 'Barton');
insert into FACULTY  values ( 2291, 'Scheinerman', 'Edward', 'Professor', 'M',
6654, '288', 'Krieger');
insert into FACULTY  values ( 2311, 'Priebe', 'Carey', 'AsstProf', 'M', 6953, '
288', 'Krieger');
insert into FACULTY  values ( 2738, 'Fill', 'James', 'Professor', 'M', 8209, 
'288', 'Krieger');
insert into FACULTY  values ( 2881, 'Goldman', 'Alan', 'Professor', 'M', 8335,
'288', 'Krieger');
insert into FACULTY  values ( 4432, 'Burzio', 'Luigi', 'Professor', 'M', 1813,
'288', 'Krieger');
insert into FACULTY  values ( 5718, 'Frank', 'Robert', 'AsstProf', 'M', 1751, '
288', 'Krieger');
insert into FACULTY  values ( 6182, 'Cheng', 'Cheng', 'AsstProf', 'M', 1856, 
'288', 'Krieger');
insert into FACULTY  values ( 6191, 'Kaplan', 'Alexander', 'Professor', 'M', 
1825, '119', 'Barton');
insert into FACULTY  values ( 6330, 'Byrne', 'William', 'Instructor', 'M', 
1691, '119', 'Barton');
insert into FACULTY  values ( 6541, 'Han', 'Shih-Ping', 'Professor', 'M', 1914,
 '288', 'Krieger');
insert into FACULTY  values ( 6910, 'Smolensky', 'Paul', 'Professor', 'M', 
2072, '288', 'Krieger');
insert into FACULTY  values ( 6925, 'Iglesias', 'Pablo', 'AsstProf', 'M', 2021,
 '119', 'Barton');
insert into FACULTY  values ( 7134, 'Goutsias', 'John', 'Professor', 'M', 2184,
 '119', 'Barton');
insert into FACULTY  values ( 7231, 'Rugh', 'Wilson', 'Professor', 'M', 2191, 
'119', 'Barton');
insert into FACULTY  values ( 7271, 'Jelinek', 'Frederick', 'Professor', 'M', 
2890, '119', 'Barton');
insert into FACULTY  values ( 7506, 'Westgate', 'Charles', 'Professor', 'M', 
2932, '119', 'Barton');
insert into FACULTY  values ( 8102, 'James', 'Lancelot', 'AsstProf', 'M', 2792,
 '288', 'Krieger');
insert into FACULTY  values ( 8118, 'Weinert', 'Howard', 'Professor', 'M', 
3272, '119', 'Barton');
insert into FACULTY  values ( 8122, 'Wierman', 'John', 'Professor', 'M', 3392,
'288', 'Krieger');
insert into FACULTY  values ( 8722, 'Cauwenberghs', 'Gert', 'AsstProf', 'M', 
1372, '119', 'Barton');
insert into FACULTY  values ( 8723, 'Andreou', 'Andreas', 'Professor', 'M', 
1402, '119', 'Barton');
insert into FACULTY  values ( 8772, 'Cowen', 'Lenore', 'AsstProf', 'F', 2870, 
'288', 'Krieger');
insert into FACULTY  values ( 8791, 'McCloskey', 'Michael', 'Professor', 'M', 
3440, '288', 'Krieger');
insert into FACULTY  values ( 8989, 'Brent', 'Michael', 'AsstProf', 'M', 9373,
'288', 'Krieger');
insert into FACULTY  values ( 9011, 'Rapp', 'Brenda', 'AsstProf', 'F', 2032, 
'288', 'Krieger');
insert into FACULTY  values ( 9191, 'Collins', 'Oliver', 'AssocProf', 'M', 
5427, '119', 'Barton');
insert into FACULTY  values ( 9199, 'Hughes', 'Brian', 'AssocProf', 'M', 5666,
'119', 'Barton');
insert into FACULTY  values ( 9210, 'Joseph', 'Richard', 'Professor', 'M', 
5996, '119', 'Barton');
insert into FACULTY  values ( 9514, 'Prince', 'Jerry', 'AssocProf', 'M', 5106,
'119', 'Barton');
insert into FACULTY  values ( 9823, 'Pang', 'Jong-Shi', 'Professor', 'M', 4366,
 '288', 'Krieger');
insert into FACULTY  values ( 9824, 'Glaser', 'Robert', 'Instructor', 'M', 
4396, '119', 'Barton');
insert into FACULTY  values ( 9811, 'Wu', 'Colin', 'AsstProf', 'M', 2906, 
'288', 'Krieger');
insert into FACULTY  values ( 9643, 'Legendre', 'Geraldine', 'AssocProf', 'F',
8972, '288', 'Krieger');
insert into FACULTY  values ( 9379, 'Khurgin', 'Jacob', 'Professor', 'M', 1060,
 '119', 'Barton');
insert into FACULTY  values ( 9922, 'Hall', 'Leslie', 'AsstProf', 'F', 7332, 
'288', 'Krieger');


set termout on
prompt Loading course table
insert into COURSE values ( '600.101', 'COMPUTER LITERACY',
     3, 6112, 'MTW', '3',600);
insert into COURSE values ( '600.103', 'INTRODUCTION TO COMPUTER SCIENCE',
     1, 4230, 'Th', '4',600);
insert into COURSE values ( '600.107', 'INTRO TO PROGRAMMING IN JAVA',
     3, 1193, 'MTW', '3',600);
insert into COURSE values ( '600.109', 'INTRO TO PROGRAMMING IN C/C++',
     3, 4230, 'MTW', '12',600);
insert into COURSE values ( '600.113', 'EXPLORING THE INTERNET',
     3, 6112, 'MTW', '4',600);
prompt Going into Silent Mode
set termout off
insert into COURSE values ( '600.121', 'JAVA PROGRAMMING',
     3, 6112, 'ThF', '10:30-12',600);
insert into COURSE values ( '600.211', 'UNIX SYSTEMS PROGRAMMING',
     3, 6112, 'ThF', '1-2:15',600);
insert into COURSE values ( '600.227', 'DATA STRUCTURES in JAVA',
     3, 1121, 'MTW', '9',600);
insert into COURSE values ( '600.232', 'MULTIMEDIA COMPUTING',
     3, 9826, 'MW', '1-2:30',600);
insert into COURSE values ( '600.271', 'COMPUTATIONAL MODELS',
     3, 9172, 'MTW', '1',600);
insert into COURSE values ( '600.303', 'SUPERCOMPUTING',
     1, 9826, 'W', '4-6:20',600);
insert into COURSE values ( '600.315', 'DATABASE SYSTEMS',
     3, 2192, 'ThF', '2:30-4',600);
insert into COURSE values ( '600.333', 'COMPUTER SYSTEM FUNDAMENTALS',
     3, 1148, 'MTW', '8',600);
insert into COURSE values ( '600.337', 'DISTRIBUTED SYSTEMS',
     3, 8918, 'M', '3',600);
insert into COURSE values ( '600.363', 'INTRODUCTION TO ALGORITHMS',
     3, 7712, 'MTW', '9',600);
insert into COURSE values ( '600.415', 'DATABASE SYSTEMS',
     3, 2192, 'ThF', '2:30-4',600);
insert into COURSE values ( '600.433', 'COMPUTER SYSTEMS',
     3, 1148, 'MTW', '8',600);
insert into COURSE values ( '600.437', 'DISTRIBUTED SYSTEMS',
     3, 8918, 'M', '3',600);
insert into COURSE values ( '600.445', 'QUANTITATIVE MEDICAL COMPUTING',
     3, 7723, 'ThF', '1-2:15',600);
insert into COURSE values ( '600.461', 'COMPUTER VISION',
     3, 8114, 'MTW', '1',600);
insert into COURSE values ( '600.463', 'ALGORITHMS I',
     3, 7712, 'MTW', '9',600);
insert into COURSE values ( '600.465', 'INTRO TO NATURAL LANGUAGE PROCESSING',
     3, 7792, 'MTW', '2',600);
insert into COURSE values ( '600.509', 'COMPUTER SCIENCE INTERNSHIP',
     3, 1121, 'M', '1',600);
insert into COURSE values ( '600.601', 'COMPUTER SCIENCE SEMINAR',
     1, 6191, 'ThF', '10:30-12',600);
insert into COURSE values ( '600.657', 'HIGH PERFORMANCE GRAPHICS AND MODELING'
,
     3, 8423, 'M', '4-5:30',600);
insert into COURSE values ( '600.787', 'SEMINAR ON COMPUTATIONAL GEOMETRY',
     3, 1121, 'Th', '2',600);
insert into COURSE values ( '550.111', 'STATISTICAL ANALYSIS',
     4, 2311, 'MTW', '12',550);
insert into COURSE values ( '550.171', 'DISCRETE MATHEMATICS',
     4, 8772, 'MTW', '11',550);
insert into COURSE values ( '500.203', 'ACCOUNTING I',
     3, 9823, 'T', '6:15-8:45',550);
insert into COURSE values ( '500.204', 'ACCOUNTING II',
     3, 9823, 'Th', '6:15-8:45',550);
insert into COURSE values ( '500.205', 'BUSINESS LAW I',
     3, 8791, 'W', '6:15-8:45',550);
insert into COURSE values ( '500.206', 'BUSINESS LAW II',
     3, 8791, 'M', '6:15-8:45',550);
insert into COURSE values ( '550.291', 'LINEAR ALGEBRA AND DIFFERENTIAL EQNS',
     4, 6541, 'MTW', '9',550);
insert into COURSE values ( '550.310', 'PROBABILITY AND STATISTICS',
     4, 8102, 'MTW', '10',550);
insert into COURSE values ( '550.361', 'INTRODUCTION TO OPTIMIZATION',
     4, 2881, 'MTW', '2',550);
insert into COURSE values ( '550.413', 'APPLIED STATISTICS AND DATA ANALYSIS',
     4, 1177, 'MTW', '11',550);
insert into COURSE values ( '550.420', 'INTRODUCTION TO PROBABILITY',
     4, 2738, 'MTW', '1',550);
insert into COURSE values ( '550.471', 'COMBINATORIAL ANALYSIS',
     4, 8772, 'MTW', '12',550);
insert into COURSE values ( '550.620', 'PROBABILITY THEORY I',
     3, 2738, 'MTW', '2',550);
insert into COURSE values ( '550.626', 'STOCHASTIC PROCESSES II',
     3, 8102, 'MTW', '1',550);
insert into COURSE values ( '550.631', 'STATISTICAL INFERENCE',
     3, 6182, 'MTW', '3',550);
insert into COURSE values ( '550.661', 'FOUNDATIONS OF OPTIMIZATION',
     3, 9823, 'MTW', '10',550);
insert into COURSE values ( '550.671', 'COMBINATORIAL ANALYSIS',
     3, 8772, 'MTW', '12',550);
insert into COURSE values ( '550.681', 'NUMERICAL ANALYSIS',
     3, 6541, 'MTW', '11',550);
insert into COURSE values ( '550.721', 'PERCOLATION THEORY',
     3, 8122, 'MTW', '9',550);
insert into COURSE values ( '550.750', 'TOPICS IN OPERATIONS RESEARCH',
     3, 9922, 'MW', '3-4:30',550);
insert into COURSE values ( '550.790', 'TOPICS IN APPLIED MATH',
     2, 2881, 'MT', '4:30-6',550);
insert into COURSE values ( '520.137', 'INTRODUCTION TO ECE',
     3, 8723, 'MTW', '11',520);
insert into COURSE values ( '520.213', 'CIRCUITS',
     4, 9210, 'MTW', '2',520);
insert into COURSE values ( '520.219', 'FIELDS, MATTER AND WAVES',
     3, 9210, 'MTW', '3',520);
insert into COURSE values ( '520.325', 'INTEGRATED ELECTRONICS',
     3, 6191, 'MTW', '3',520);
insert into COURSE values ( '520.345', 'ECE LABORATORY',
     3, 1823, 'W', '2',520);
insert into COURSE values ( '520.349', 'MICROPROCESSOR LAB I',
     3, 9824, 'Th', '8',520);
insert into COURSE values ( '520.353', 'CONTROL SYSTEMS',
     3, 6925, 'MTW', '10',520);
insert into COURSE values ( '520.401', 'BASIC COMMUNICATIONS',
     3, 6191, 'MTW', '1',520);
insert into COURSE values ( '520.410', 'FIBER OPTICS AND PHOTONICS',
     3, 6191, 'MTW', '1',520);
insert into COURSE values ( '520.419', 'ITERATIVE ALGORITHMS',
     3, 2119, 'MT', '4-5:15',520);
insert into COURSE values ( '520.421', 'INTRODUCTION TO NON-LINEAR SYSTEMS',
     3, 7231, 'MTW', '9',520);
insert into COURSE values ( '520.432', 'TOPICS IN MEDICAL IMAGING SYSTEMS',
     3, 9514, 'TTh', '8:30-10',520);
insert into COURSE values ( '520.435', 'DIGITAL SIGNAL PROCESSING',
     4, 8118, 'MTW', '11',520);
insert into COURSE values ( '520.475', 'PROCESSING AND RECOGNITION OF SPEECH',
     3, 6330, 'TW', '2-3:30',520);
insert into COURSE values ( '520.490', 'ANALOG AND DIGITAL VLSI SYSTEMS',
     3, 8722, 'ThF', '10:30-12',520);
insert into COURSE values ( '520.603', 'ELECTROMAGNETIC WAVES',
     4, 9210, 'Th', '1-4:30',520);
insert into COURSE values ( '520.605', 'SOLID STATE PHYSICS',
     3, 9379, 'Tu', '1-4',520);
insert into COURSE values ( '520.609', 'NONLINEAR TECHNICAL IMAGE PROCESSING',
     3, 7134, 'Th', '1-4',520);
insert into COURSE values ( '520.651', 'RANDOM SIGNAL ANALYSIS',
     3, 9514, 'ThF', '10:30-12',520);
insert into COURSE values ( '050.102', 'LANGUAGE AND MIND',
     3, 4432, 'MTW', '10',050);
insert into COURSE values ( '050.109', 'MIND, BRAIN, COMPUTERS',
     3, 6910, 'MW', '2-3:15',050);
insert into COURSE values ( '050.203', 'COGNITIVE NEUROSCIENCE',
     4, 9011, 'MT', '3:30-4:45',050);
insert into COURSE values ( '050.325', 'SOUND STRUCTURES IN NATURAL LANGUAGE',
     3, 4432, 'T', '10-12',050);
insert into COURSE values ( '050.370', 'FORMAL METHODS IN COGNITIVE SCIENCE',
     3, 6910, 'MW', '11:30',050);
insert into COURSE values ( '050.381', 'LANGUAGE DEVELOPMENT',
     3, 8989, 'T', '1-3',050);
insert into COURSE values ( '050.427', 'THE HISTORY OF ROMANCE LANGUAGES',
     3, 4432, 'W', '1-3',050);
insert into COURSE values ( '050.670', 'FORMAL METHODS IN COGNITIVE SCIENCE',
     3, 4432, 'MW', '11:30-12:45',050);
insert into COURSE values ( '050.802', 
'RESEARCH SEMINAR IN COGNITIVE PROCESSES',
     1, 9011, 'W', '1-3',050);
insert into COURSE values ( '050.821', 'COMP. MODELS OF SENTENCE PROCESSING',
     3, 5718, 'M', '1-4',050);


insert into CHAIRED_BY values (550, 8122);
insert into CHAIRED_BY values (520, 1823);
insert into CHAIRED_BY values (050, 8791);
insert into CHAIRED_BY values (600, 1148);


insert into MEMBER_OF values (7792,  600, 'Primary');
insert into MEMBER_OF values (9210,  520, 'Primary');
insert into MEMBER_OF values (9811,  550, 'Primary');
insert into MEMBER_OF values (9643,  050, 'Primary');
insert into MEMBER_OF values (9379,  520, 'Primary');
insert into MEMBER_OF values (8918,  600, 'Primary');
insert into MEMBER_OF values (7712,  600, 'Primary');
insert into MEMBER_OF values (1121,  600, 'Primary');
insert into MEMBER_OF values (9172,  600, 'Primary');
insert into MEMBER_OF values (8423,  600, 'Primary');
insert into MEMBER_OF values (1148,  600, 'Primary');
insert into MEMBER_OF values (8741,  600, 'Primary');
insert into MEMBER_OF values (3457,  600, 'Primary');
insert into MEMBER_OF values (7723,  600, 'Primary');
insert into MEMBER_OF values (8721,  600, 'Primary');
insert into MEMBER_OF values (2192,  600, 'Primary');
insert into MEMBER_OF values (8114,  600, 'Primary');
insert into MEMBER_OF values (6112,  600, 'Primary');
insert into MEMBER_OF values (9826,  600, 'Primary');
insert into MEMBER_OF values (1193,  600, 'Primary');
insert into MEMBER_OF values (1082,  600, 'Primary');
insert into MEMBER_OF values (4230,  600, 'Primary');
insert into MEMBER_OF values (8989,  600, 'Secondary');
insert into MEMBER_OF values (7271,  600, 'Secondary');
insert into MEMBER_OF values (8721,  520, 'Secondary');
insert into MEMBER_OF values (8741,  050, 'Secondary');
insert into MEMBER_OF values (7271,  050, 'Secondary');
insert into MEMBER_OF values (6182,  550, 'Primary');
insert into MEMBER_OF values (8772,  550, 'Primary');
insert into MEMBER_OF values (2738,  550, 'Primary');
insert into MEMBER_OF values (2881,  550, 'Primary');
insert into MEMBER_OF values (9922,  550, 'Primary');
insert into MEMBER_OF values (6541,  550, 'Primary');
insert into MEMBER_OF values (8102,  550, 'Primary');
insert into MEMBER_OF values (1177,  550, 'Primary');
insert into MEMBER_OF values (9823,  550, 'Primary');
insert into MEMBER_OF values (2311,  550, 'Primary');
insert into MEMBER_OF values (2291,  550, 'Primary');
insert into MEMBER_OF values (8122,  550, 'Primary');
insert into MEMBER_OF values (8989,  050, 'Primary');
insert into MEMBER_OF values (4432,  050, 'Primary');
insert into MEMBER_OF values (5718,  050, 'Primary');
insert into MEMBER_OF values (8791,  050, 'Primary');
insert into MEMBER_OF values (9011,  050, 'Primary');
insert into MEMBER_OF values (6910,  050, 'Primary');
insert into MEMBER_OF values (8723,  520, 'Primary');
insert into MEMBER_OF values (2028,  520, 'Primary');
insert into MEMBER_OF values (8722,  520, 'Primary');
insert into MEMBER_OF values (9191,  520, 'Primary');
insert into MEMBER_OF values (1823,  520, 'Primary');
insert into MEMBER_OF values (7134,  520, 'Primary');
insert into MEMBER_OF values (9199,  520, 'Primary');
insert into MEMBER_OF values (6925,  520, 'Primary');
insert into MEMBER_OF values (7271,  520, 'Primary');
insert into MEMBER_OF values (6191,  520, 'Primary');
insert into MEMBER_OF values (2119,  520, 'Primary');
insert into MEMBER_OF values (9514,  520, 'Primary');
insert into MEMBER_OF values (7231,  520, 'Primary');
insert into MEMBER_OF values (1172,  520, 'Primary');
insert into MEMBER_OF values (8118,  520, 'Primary');
insert into MEMBER_OF values (7506,  520, 'Primary');
insert into MEMBER_OF values (6330,  520, 'Primary');
insert into MEMBER_OF values (9824,  520, 'Primary');


insert into DEPARTMENT  values ( 010, 'AS', 'History of Art',
     '268', 'Mergenthaler', 7117);
insert into DEPARTMENT  values ( 020, 'AS', 'Biology',
     '144', 'Mudd', 7330);
insert into DEPARTMENT  values ( 030, 'AS', 'Chemistry',
     '113', 'Remsen', 7429);
insert into DEPARTMENT  values ( 040, 'AS', 'Classics',
     '121', 'Gilman', 7556);
insert into DEPARTMENT  values ( 050, 'AS', 'Cognitive Science',
     '381', 'Krieger', 7119);
insert into DEPARTMENT  values ( 060, 'AS', 'English',
     '146', 'Gilman', 7544);
insert into DEPARTMENT  values ( 070, 'AS', 'Anthropology',
     '404B', 'Macaulay', 7272);
insert into DEPARTMENT  values ( 090, 'AS', 'German',
     '245', 'Gilman', 7508);
insert into DEPARTMENT  values ( 100, 'AS', 'History',
     '312', 'Gilman', 7575);
insert into DEPARTMENT  values ( 110, 'AS', 'Mathematics',
     '404', 'Krieger', 7399);
insert into DEPARTMENT  values ( 130, 'AS', 'Near Eastern Studies',
     '128', 'Gilman', 7499);
insert into DEPARTMENT  values ( 140, 'AS', 'History of Science',
     '234', 'Gilman', 7501);
insert into DEPARTMENT  values ( 150, 'AS', 'Philosophy',
     '347', 'Gilman', 7524);
insert into DEPARTMENT  values ( 170, 'AS', 'Physics and Astronomy',
     '366', 'Bloomberg', 7347);
insert into DEPARTMENT  values ( 180, 'AS', 'Economics',
     '440', 'Mergenthaler', 7601);
insert into DEPARTMENT  values ( 190, 'AS', 'Political Science',
     '338', 'Mergenthaler', 7540);
insert into DEPARTMENT  values ( 200, 'AS', 'Psychology',
     '223', 'Ames', 7055);
insert into DEPARTMENT  values ( 340, 'AS', 'French',
     '225', 'Gilman', 7227);
insert into DEPARTMENT  values ( 350, 'AS', 'Hispanic/Italian Studies',
     '221', 'Gilman', 7226);
insert into DEPARTMENT  values ( 520, 'EN', 'ECE',
     '105', 'Barton', 7033);
insert into DEPARTMENT  values ( 530, 'EN', 'Mechanical Engineering',
     '122', 'Latrobe', 7132);
insert into DEPARTMENT  values ( 540, 'EN', 'Chemical Engineering',
     '24', 'NEB', 7170);
insert into DEPARTMENT  values ( 550, 'EN', 'Mathematical Sciences',
     '221', 'Maryland', 7195);
insert into DEPARTMENT  values ( 560, 'EN', 'Civil Engineering',
     '206', 'Latrobe', 8680);
insert into DEPARTMENT  values ( 580, 'EN', 'Biomedical Engineering',
     '144', 'NEB', 8482);
insert into DEPARTMENT  values ( 600, 'EN', 'Computer Science',
     '224', 'NEB', 8577);


insert into ENROLLED_IN values ( 1001, '550.681', 'A-');
insert into ENROLLED_IN values ( 1001, '600.303', 'B');
insert into ENROLLED_IN values ( 1001, '600.315', 'B+');
insert into ENROLLED_IN values ( 1001, '600.337', 'A');
insert into ENROLLED_IN values ( 1001, '600.461', 'B-');
insert into ENROLLED_IN values ( 1001, '600.465', 'B');
insert into ENROLLED_IN values ( 1002, '520.213', 'B+');
insert into ENROLLED_IN values ( 1002, '600.211', 'C');
insert into ENROLLED_IN values ( 1002, '600.303', 'C+');
insert into ENROLLED_IN values ( 1002, '600.337', 'A');
insert into ENROLLED_IN values ( 1002, '600.463', 'B');
insert into ENROLLED_IN values ( 1002, '600.465', 'B+');
insert into ENROLLED_IN values ( 1003, '600.333', 'B');
insert into ENROLLED_IN values ( 1003, '600.337', 'B');
insert into ENROLLED_IN values ( 1003, '600.415', 'B');
insert into ENROLLED_IN values ( 1003, '600.461', 'B+');
insert into ENROLLED_IN values ( 1003, '600.465', 'B');
insert into ENROLLED_IN values ( 1004, '600.303', 'C-');
insert into ENROLLED_IN values ( 1004, '600.415', 'C-');
insert into ENROLLED_IN values ( 1004, '600.437', 'C-');
insert into ENROLLED_IN values ( 1004, '600.445', 'A-');
insert into ENROLLED_IN values ( 1004, '600.461', 'C');
insert into ENROLLED_IN values ( 1004, '600.463', 'A+');
insert into ENROLLED_IN values ( 1004, '600.465', 'A');
insert into ENROLLED_IN values ( 1005, '600.103', 'A');
insert into ENROLLED_IN values ( 1005, '600.107', 'C+');
insert into ENROLLED_IN values ( 1005, '600.113', 'C');
insert into ENROLLED_IN values ( 1005, '600.227', 'A');
insert into ENROLLED_IN values ( 1005, '600.303', 'B');
insert into ENROLLED_IN values ( 1006, '550.420', 'B');
insert into ENROLLED_IN values ( 1006, '600.107', 'B+');
insert into ENROLLED_IN values ( 1006, '600.227', 'B-');
insert into ENROLLED_IN values ( 1006, '600.232', 'C-');
insert into ENROLLED_IN values ( 1006, '600.303', 'A-');
insert into ENROLLED_IN values ( 1006, '600.315', 'A');
insert into ENROLLED_IN values ( 1007, '550.420', 'A');
insert into ENROLLED_IN values ( 1007, '600.113', 'A-');
insert into ENROLLED_IN values ( 1007, '600.227', 'C+');
insert into ENROLLED_IN values ( 1007, '600.315', 'A');
insert into ENROLLED_IN values ( 1007, '600.333', 'A-');
insert into ENROLLED_IN values ( 1007, '600.337', 'C');
insert into ENROLLED_IN values ( 1008, '600.415', 'A+');
insert into ENROLLED_IN values ( 1008, '600.463', 'B');
insert into ENROLLED_IN values ( 1008, '600.465', 'B');
insert into ENROLLED_IN values ( 1008, '600.657', 'B');
insert into ENROLLED_IN values ( 1008, '600.787', 'B');
insert into ENROLLED_IN values ( 1009, '550.413', 'B+');
insert into ENROLLED_IN values ( 1009, '550.471', 'C');
insert into ENROLLED_IN values ( 1009, '550.620', 'A-');
insert into ENROLLED_IN values ( 1009, '550.626', 'B');
insert into ENROLLED_IN values ( 1009, '550.671', 'C');
insert into ENROLLED_IN values ( 1009, '550.681', 'A');
insert into ENROLLED_IN values ( 1009, '550.661', 'B-');
insert into ENROLLED_IN values ( 1009, '550.631', 'A-');
insert into ENROLLED_IN values ( 1010, '550.291', 'A');
insert into ENROLLED_IN values ( 1010, '550.310', 'A');
insert into ENROLLED_IN values ( 1010, '550.413', 'C+');
insert into ENROLLED_IN values ( 1010, '550.420', 'A');
insert into ENROLLED_IN values ( 1010, '550.471', 'A');
insert into ENROLLED_IN values ( 1010, '600.107', 'B+');
insert into ENROLLED_IN values ( 1011, '520.213', 'B');
insert into ENROLLED_IN values ( 1011, '520.345', 'B');
insert into ENROLLED_IN values ( 1011, '520.349', 'A');
insert into ENROLLED_IN values ( 1011, '520.353', 'A-');
insert into ENROLLED_IN values ( 1011, '550.420', 'B');
insert into ENROLLED_IN values ( 1011, '600.415', 'B+');
insert into ENROLLED_IN values ( 1012, '050.109', 'B-');
insert into ENROLLED_IN values ( 1012, '050.203', 'B-');
insert into ENROLLED_IN values ( 1012, '050.325', 'A-');
insert into ENROLLED_IN values ( 1012, '600.107', 'A');
insert into ENROLLED_IN values ( 1012, '600.315', 'B');
insert into ENROLLED_IN values ( 1014, '600.107', 'A');
insert into ENROLLED_IN values ( 1014, '600.227', 'A');
insert into ENROLLED_IN values ( 1014, '600.232', 'A');
insert into ENROLLED_IN values ( 1014, '600.315', 'A+');
insert into ENROLLED_IN values ( 1014, '600.445', 'B');
insert into ENROLLED_IN values ( 1014, '600.461', 'B');
insert into ENROLLED_IN values ( 1014, '600.463', 'B');
insert into ENROLLED_IN values ( 1015, '550.420', 'A');
insert into ENROLLED_IN values ( 1015, '600.227', 'A+');
insert into ENROLLED_IN values ( 1015, '600.303', 'A');
insert into ENROLLED_IN values ( 1015, '600.315', 'C-');
insert into ENROLLED_IN values ( 1015, '600.333', 'A');
insert into ENROLLED_IN values ( 1016, '050.109', 'B-');
insert into ENROLLED_IN values ( 1016, '050.203', 'D-');
insert into ENROLLED_IN values ( 1016, '050.325', 'A');
insert into ENROLLED_IN values ( 1016, '050.821', 'A');
insert into ENROLLED_IN values ( 1016, '550.420', 'A-');
insert into ENROLLED_IN values ( 1016, '600.107', 'B+');
insert into ENROLLED_IN values ( 1016, '600.315', 'B-');
insert into ENROLLED_IN values ( 1017, '050.427', 'B');
insert into ENROLLED_IN values ( 1017, '050.670', 'B');
insert into ENROLLED_IN values ( 1017, '050.802', 'C');
insert into ENROLLED_IN values ( 1017, '550.681', 'B');
insert into ENROLLED_IN values ( 1017, '600.109', 'A-');
insert into ENROLLED_IN values ( 1017, '600.461', 'A');
insert into ENROLLED_IN values ( 1017, '600.465', 'C');
insert into ENROLLED_IN values ( 1018, '520.213', 'A-');
insert into ENROLLED_IN values ( 1018, '600.211', 'A');
insert into ENROLLED_IN values ( 1018, '600.303', 'A');
insert into ENROLLED_IN values ( 1018, '600.337', 'C-');
insert into ENROLLED_IN values ( 1018, '600.463', 'B');
insert into ENROLLED_IN values ( 1018, '600.465', 'B');
insert into ENROLLED_IN values ( 1019, '600.103', 'B');
insert into ENROLLED_IN values ( 1019, '600.107', 'B');
insert into ENROLLED_IN values ( 1019, '600.113', 'D+');
insert into ENROLLED_IN values ( 1019, '600.227', 'A');
insert into ENROLLED_IN values ( 1019, '600.303', 'A');
insert into ENROLLED_IN values ( 1020, '600.333', 'A');
insert into ENROLLED_IN values ( 1020, '600.337', 'A');
insert into ENROLLED_IN values ( 1020, '600.415', 'A');
insert into ENROLLED_IN values ( 1020, '600.461', 'A');
insert into ENROLLED_IN values ( 1020, '600.465', 'A');
insert into ENROLLED_IN values ( 1021, '600.303', 'B-');
insert into ENROLLED_IN values ( 1021, '600.303', 'B');
insert into ENROLLED_IN values ( 1021, '600.415', 'B');
insert into ENROLLED_IN values ( 1021, '600.437', 'B');
insert into ENROLLED_IN values ( 1021, '600.437', 'B');
insert into ENROLLED_IN values ( 1021, '600.445', 'B-');
insert into ENROLLED_IN values ( 1021, '600.445', 'C');
insert into ENROLLED_IN values ( 1021, '600.463', 'A');
insert into ENROLLED_IN values ( 1021, '600.463', 'B');
insert into ENROLLED_IN values ( 1022, '550.420', 'B');
insert into ENROLLED_IN values ( 1022, '550.420', 'B+');
insert into ENROLLED_IN values ( 1022, '600.107', 'A');
insert into ENROLLED_IN values ( 1022, '600.227', 'A');
insert into ENROLLED_IN values ( 1022, '600.227', 'A');
insert into ENROLLED_IN values ( 1022, '600.232', 'B');
insert into ENROLLED_IN values ( 1022, '600.303', 'B');
insert into ENROLLED_IN values ( 1022, '600.315', 'D');
insert into ENROLLED_IN values ( 1022, '600.461', 'A');
insert into ENROLLED_IN values ( 1023, '600.113', 'A-');
insert into ENROLLED_IN values ( 1023, '600.315', 'B');
insert into ENROLLED_IN values ( 1023, '600.333', 'B');
insert into ENROLLED_IN values ( 1023, '600.337', 'B+');
insert into ENROLLED_IN values ( 1023, '600.463', 'A');
insert into ENROLLED_IN values ( 1023, '600.465', 'A');
insert into ENROLLED_IN values ( 1023, '600.657', 'B');
insert into ENROLLED_IN values ( 1023, '600.787', 'B');
insert into ENROLLED_IN values ( 1024, '550.291', 'B');
insert into ENROLLED_IN values ( 1024, '550.413', 'C');
insert into ENROLLED_IN values ( 1024, '550.471', 'A-');
insert into ENROLLED_IN values ( 1024, '550.620', 'A');
insert into ENROLLED_IN values ( 1024, '550.626', 'B');
insert into ENROLLED_IN values ( 1024, '550.671', 'B');
insert into ENROLLED_IN values ( 1024, '550.681', 'B');
insert into ENROLLED_IN values ( 1024, '600.415', 'B');
insert into ENROLLED_IN values ( 1025, '520.213', 'A');
insert into ENROLLED_IN values ( 1025, '520.345', 'A+');
insert into ENROLLED_IN values ( 1025, '550.310', 'A');
insert into ENROLLED_IN values ( 1025, '550.413', 'A');
insert into ENROLLED_IN values ( 1025, '550.420', 'C');
insert into ENROLLED_IN values ( 1025, '550.471', 'B');
insert into ENROLLED_IN values ( 1025, '600.107', 'B');
insert into ENROLLED_IN values ( 1026, '520.349', 'A');
insert into ENROLLED_IN values ( 1026, '520.353', 'A');
insert into ENROLLED_IN values ( 1026, '600.303', 'A');
insert into ENROLLED_IN values ( 1026, '600.437', 'A');
insert into ENROLLED_IN values ( 1026, '600.445', 'A');
insert into ENROLLED_IN values ( 1026, '600.463', 'B-');
insert into ENROLLED_IN values ( 1027, '600.107', 'B');
insert into ENROLLED_IN values ( 1027, '600.227', 'B');
insert into ENROLLED_IN values ( 1027, '600.232', 'B');
insert into ENROLLED_IN values ( 1027, '600.303', 'B');
insert into ENROLLED_IN values ( 1027, '600.315', 'B-');
insert into ENROLLED_IN values ( 1027, '600.461', 'B-');
insert into ENROLLED_IN values ( 1027, '600.463', 'B');
insert into ENROLLED_IN values ( 1028, '550.420', 'B+');
insert into ENROLLED_IN values ( 1028, '600.227', 'A');
insert into ENROLLED_IN values ( 1028, '600.315', 'A+');
insert into ENROLLED_IN values ( 1028, '600.333', 'A');
insert into ENROLLED_IN values ( 1028, '600.337', 'A+');
insert into ENROLLED_IN values ( 1029, '550.413', 'C-');
insert into ENROLLED_IN values ( 1029, '550.471', 'A');
insert into ENROLLED_IN values ( 1029, '550.620', 'B-');
insert into ENROLLED_IN values ( 1029, '550.671', 'A-');
insert into ENROLLED_IN values ( 1029, '600.113', 'B-');
insert into ENROLLED_IN values ( 1029, '600.463', 'A+');
insert into ENROLLED_IN values ( 1030, '520.345', 'B');
insert into ENROLLED_IN values ( 1030, '550.291', 'B');
insert into ENROLLED_IN values ( 1030, '550.310', 'B-');
insert into ENROLLED_IN values ( 1030, '550.413', 'B-');
insert into ENROLLED_IN values ( 1030, '550.420', 'B');
insert into ENROLLED_IN values ( 1030, '550.471', 'B+');
insert into ENROLLED_IN values ( 1030, '600.107', 'B');
insert into ENROLLED_IN values ( 1031, '520.213', 'B+');
insert into ENROLLED_IN values ( 1031, '520.349', 'B');
insert into ENROLLED_IN values ( 1031, '520.353', 'C');
insert into ENROLLED_IN values ( 1031, '600.437', 'A+');
insert into ENROLLED_IN values ( 1032, '550.420', 'A-');
insert into ENROLLED_IN values ( 1032, '550.420', 'D-');
insert into ENROLLED_IN values ( 1032, '600.232', 'A-');
insert into ENROLLED_IN values ( 1032, '600.303', 'A');
insert into ENROLLED_IN values ( 1032, '600.315', 'A');
insert into ENROLLED_IN values ( 1033, '600.113', 'A');
insert into ENROLLED_IN values ( 1033, '600.227', 'A');
insert into ENROLLED_IN values ( 1033, '600.315', 'A');
insert into ENROLLED_IN values ( 1033, '600.333', 'A');
insert into ENROLLED_IN values ( 1033, '600.337', 'B');
insert into ENROLLED_IN values ( 1034, '050.109', 'B+');
insert into ENROLLED_IN values ( 1034, '050.203', 'B');
insert into ENROLLED_IN values ( 1034, '050.325', 'B');
insert into ENROLLED_IN values ( 1034, '600.107', 'B+');
insert into ENROLLED_IN values ( 1034, '600.315', 'B');
insert into ENROLLED_IN values ( 1035, '050.381', 'B-');
insert into ENROLLED_IN values ( 1035, '050.427', 'A-');
insert into ENROLLED_IN values ( 1035, '050.670', 'B');
insert into ENROLLED_IN values ( 1035, '050.802', 'D');
insert into ENROLLED_IN values ( 1035, '050.821', 'A');
insert into ENROLLED_IN values ( 1035, '600.109', 'B-');


insert into STUDENT values ( 1001, 'Smith', 'Linda', 18, 'F', 600, 1121,'BAL');
insert into STUDENT values ( 1002, 'Kim', 'Tracy', 19, 'F', 600, 7712,'HKG');
insert into STUDENT values ( 1003, 'Jones', 'Shiela', 21, 'F', 600, 7792,'WAS');
insert into STUDENT values ( 1004, 'Kumar', 'Dinesh', 20, 'M', 600, 8423,'CHI');
insert into STUDENT values ( 1005, 'Gompers', 'Paul', 26, 'M', 600, 1121,'YYZ');
insert into STUDENT values ( 1006, 'Schultz', 'Andy', 18, 'M', 600, 1148,'BAL');
insert into STUDENT values ( 1007, 'Apap', 'Lisa', 18, 'F', 600, 8918,'PIT');
insert into STUDENT values ( 1008, 'Nelson', 'Jandy', 20, 'F', 600, 9172,'BAL');
insert into STUDENT values ( 1009, 'Tai', 'Eric', 19, 'M', 600, 2192,'YYZ');
insert into STUDENT values ( 1010, 'Lee', 'Derek', 17, 'M', 600, 2192,'HOU');
insert into STUDENT values ( 1011, 'Adams', 'David', 22, 'M', 600, 1148,'PHL');
insert into STUDENT values ( 1012, 'Davis', 'Steven', 20, 'M', 600, 7723,'PIT');
insert into STUDENT values ( 1014, 'Norris', 'Charles', 18, 'M', 600, 8741, 'DAL');
insert into STUDENT values ( 1015, 'Lee', 'Susan', 16, 'F', 600, 8721,'HKG');
insert into STUDENT values ( 1016, 'Schwartz', 'Mark', 17, 'M', 600, 2192,'DET');
insert into STUDENT values ( 1017, 'Wilson', 'Bruce', 27, 'M', 600, 1148,'LON');
insert into STUDENT values ( 1018, 'Leighton', 'Michael', 20, 'M', 600, 1121, 'PIT');
insert into STUDENT values ( 1019, 'Pang', 'Arthur', 18, 'M', 600, 2192,'WAS');
insert into STUDENT values ( 1020, 'Thornton', 'Ian', 22, 'M', 520, 7271,'NYC');
insert into STUDENT values ( 1021, 'Andreou', 'George', 19, 'M', 520, 8722, 'NYC');
insert into STUDENT values ( 1022, 'Woods', 'Michael', 17, 'M', 540, 8722,'PHL');
insert into STUDENT values ( 1023, 'Shieber', 'David', 20, 'M', 520, 8722,'NYC');
insert into STUDENT values ( 1024, 'Prater', 'Stacy', 18, 'F', 540, 7271,'BAL');
insert into STUDENT values ( 1025, 'Goldman', 'Mark', 18, 'M', 520, 7134,'PIT');
insert into STUDENT values ( 1026, 'Pang', 'Eric', 19, 'M', 520, 7134,'HKG');
insert into STUDENT values ( 1027, 'Brody', 'Paul', 18, 'M', 520, 8723,'LOS');
insert into STUDENT values ( 1028, 'Rugh', 'Eric', 20, 'M', 550, 2311,'ROC');
insert into STUDENT values ( 1029, 'Han', 'Jun', 17, 'M', 100, 2311,'PEK');
insert into STUDENT values ( 1030, 'Cheng', 'Lisa', 21, 'F', 550, 2311,'SFO');
insert into STUDENT values ( 1031, 'Smith', 'Sarah', 20, 'F', 550, 8772,'PHL');
insert into STUDENT values ( 1032, 'Brown', 'Eric', 20, 'M', 550, 8772,'ATL');
insert into STUDENT values ( 1033, 'Simms', 'William', 18, 'M', 550, 8772,'NAR');
insert into STUDENT values ( 1034, 'Epp', 'Eric', 18, 'M', 050, 5718,'BOS');
insert into STUDENT values ( 1035, 'Schmidt', 'Sarah', 26, 'F', 050, 5718,'WAS');



insert into PARENT_OF values (2881, 1025);
insert into PARENT_OF values (9823, 1026);
insert into PARENT_OF values (9823, 1019);

insert into MINOR_IN values ( 1004, 520);
insert into MINOR_IN values ( 1005, 550);
insert into MINOR_IN values ( 1006, 050);
insert into MINOR_IN values ( 1007, 520);
insert into MINOR_IN values ( 1008, 550);
insert into MINOR_IN values ( 1014, 090);
insert into MINOR_IN values ( 1015, 140);
insert into MINOR_IN values ( 1016, 190);
insert into MINOR_IN values ( 1027, 530);
insert into MINOR_IN values ( 1031, 540);


drop table GRADECONVERSION;
create table GRADECONVERSION (
        lettergrade         VARCHAR2(2),
        gradepoint          FLOAT
        );


insert into GRADECONVERSION values ('A+', 4.0);
insert into GRADECONVERSION values ('A',  4.0);
insert into GRADECONVERSION values ('A-', 3.7);
insert into GRADECONVERSION values ('B+', 3.3);
insert into GRADECONVERSION values ('B',  3.0);
insert into GRADECONVERSION values ('B-', 2.7);
insert into GRADECONVERSION values ('C+', 2.3);
insert into GRADECONVERSION values ('C',  2.0);
insert into GRADECONVERSION values ('C-', 1.7);
insert into GRADECONVERSION values ('D+', 1.3);
insert into GRADECONVERSION values ('D',  1.0);
insert into GRADECONVERSION values ('D-', 0.7);
insert into GRADECONVERSION values ('F',  0.0);








insert into DORM values (100,'Smith Hall',85,'X');
insert into DORM values (110,'Bud Jones Hall',116,'M');
insert into DORM values (140,'Fawlty Towers',355,'X');
insert into DORM values (160,'Dorm-plex 2000',400,'X');
insert into DORM values (109,'Anonymous Donor Hall',128,'F');
insert into DORM values (117,'University Hovels',40,'X');
insert into DORM values (104,'Grad Student Asylum',256,'X');


insert into DORM_AMENITY values ( 900 , 'TV Lounge' ) ;
insert into DORM_AMENITY values ( 901 , 'Study Room' ) ;
insert into DORM_AMENITY values ( 902 , 'Pub in Basement' ) ;
insert into DORM_AMENITY values ( 903 , 'Carpeted Rooms' ) ;
insert into DORM_AMENITY values ( 904 , '4 Walls' ) ;
insert into DORM_AMENITY values ( 930 , 'Roof' ) ;
insert into DORM_AMENITY values ( 931 , 'Ethernet Ports' ) ;
insert into DORM_AMENITY values ( 932 , 'Air Conditioning' ) ;
insert into DORM_AMENITY values ( 922 , 'Heat' ) ;
insert into DORM_AMENITY values ( 950 , 'Working Fireplaces' ) ;
insert into DORM_AMENITY values ( 955 , 'Kitchen in Every Room' ) ;
insert into DORM_AMENITY values ( 909 , 'Allows Pets' ) ;


insert into HAS_AMENITY values ( 109 ,  900) ;
insert into HAS_AMENITY values ( 109 ,  901) ;
insert into HAS_AMENITY values ( 109 ,  903) ;
insert into HAS_AMENITY values ( 109 ,  904 ) ;
insert into HAS_AMENITY values ( 109 ,  930 ) ;
insert into HAS_AMENITY values ( 109 ,  931) ;
insert into HAS_AMENITY values ( 109 ,  932) ;
insert into HAS_AMENITY values ( 109 ,  922) ;

insert into HAS_AMENITY values ( 104 ,  901) ;
insert into HAS_AMENITY values ( 104 ,  904) ;
insert into HAS_AMENITY values ( 104 ,  930 ) ;

insert into HAS_AMENITY values ( 160 , 900 ) ;
insert into HAS_AMENITY values ( 160 , 901 ) ;
insert into HAS_AMENITY values ( 160 , 902 ) ;
insert into HAS_AMENITY values ( 160 , 903 ) ;
insert into HAS_AMENITY values ( 160 , 931 ) ;
insert into HAS_AMENITY values ( 160 ,  904) ;
insert into HAS_AMENITY values ( 160 ,  930 ) ;
insert into HAS_AMENITY values ( 160 ,  922 ) ;
insert into HAS_AMENITY values ( 160 , 932 ) ;
insert into HAS_AMENITY values ( 160 , 950 ) ;
insert into HAS_AMENITY values ( 160 , 955 ) ;
insert into HAS_AMENITY values ( 160 , 909 ) ;

insert into HAS_AMENITY values ( 100 ,  901) ;
insert into HAS_AMENITY values ( 100 ,  903) ;
insert into HAS_AMENITY values ( 100 ,  904) ;
insert into HAS_AMENITY values ( 100 ,  930 ) ;
insert into HAS_AMENITY values ( 100 ,  922 ) ;

insert into HAS_AMENITY values ( 117 ,  930 ) ;

insert into HAS_AMENITY values ( 110 ,  901) ;
insert into HAS_AMENITY values ( 110 ,  903) ;
insert into HAS_AMENITY values ( 110 ,  904) ;
insert into HAS_AMENITY values ( 110 ,  930 ) ;
insert into HAS_AMENITY values ( 110 ,  922 ) ;

insert into HAS_AMENITY values ( 140 ,  909 ) ;
insert into HAS_AMENITY values ( 140 ,  900) ;
insert into HAS_AMENITY values ( 140 ,  902) ;
insert into HAS_AMENITY values ( 140 ,  904) ;
insert into HAS_AMENITY values ( 140 ,  930 ) ;
insert into HAS_AMENITY values ( 140 ,  932 ) ;



insert into ACTIVITY values ( 770  ,   'Mountain Climbing'  ) ;
insert into ACTIVITY values ( 771  ,   'Canoeing'  ) ;
insert into ACTIVITY values ( 772  ,   'Kayaking'  ) ;
insert into ACTIVITY values ( 773  ,   'Spelunking'  ) ;
insert into ACTIVITY values ( 777  ,   'Soccer'  ) ;
insert into ACTIVITY values ( 778  ,   'Baseball'  ) ;
insert into ACTIVITY values ( 780  ,   'Football'  ) ;
insert into ACTIVITY values ( 782  ,   'Volleyball'  ) ;
insert into ACTIVITY values ( 799  ,   'Bungee Jumping' ) ;
insert into ACTIVITY values ( 779  ,   'Accordion Ensemble' ) ;
insert into ACTIVITY values ( 784  ,   'Canasta'  ) ;
insert into ACTIVITY values ( 785  ,   'Chess'  ) ;
insert into ACTIVITY values ( 776  ,   'Extreme Canasta' ) ;
insert into ACTIVITY values ( 790  ,   'Crossword Puzzles'  ) ;
insert into ACTIVITY values ( 791  ,   'Proselytizing'  ) ;
insert into ACTIVITY values ( 796  ,   'Square Dancing' ) ;








insert into PARTICIPATES_IN values (1001  ,  770) ;
insert into PARTICIPATES_IN values (1001  ,  771) ;
insert into PARTICIPATES_IN values (1001  ,  777) ;

insert into PARTICIPATES_IN values (1002  ,  772) ;
insert into PARTICIPATES_IN values (1002  ,  771) ;

insert into PARTICIPATES_IN values (1003  ,  778) ;


insert into PARTICIPATES_IN values (1004  ,  780) ;
insert into PARTICIPATES_IN values (1004  ,  782) ;
insert into PARTICIPATES_IN values (1004  ,  778) ;
insert into PARTICIPATES_IN values (1004  ,  777) ;

insert into PARTICIPATES_IN values (1005  ,  770) ;


insert into PARTICIPATES_IN values (1006  ,  773) ;


insert into PARTICIPATES_IN values (1007  ,  773) ;
insert into PARTICIPATES_IN values (1007  ,  784) ;

insert into PARTICIPATES_IN values (1008  ,  785) ;
insert into PARTICIPATES_IN values (1008  ,  773) ;
insert into PARTICIPATES_IN values (1008  ,  780) ;
insert into PARTICIPATES_IN values (1008  ,  790) ;

insert into PARTICIPATES_IN values (1009  ,  778) ;
insert into PARTICIPATES_IN values (1009  ,  777) ;
insert into PARTICIPATES_IN values (1009  ,  782) ;

insert into PARTICIPATES_IN values (1010  ,  780) ;


insert into PARTICIPATES_IN values (1011  ,  780) ;


insert into PARTICIPATES_IN values (1012  ,  780) ;


insert into PARTICIPATES_IN values (1014  ,  780) ;
insert into PARTICIPATES_IN values (1014  ,  777) ;
insert into PARTICIPATES_IN values (1014  ,  778) ;
insert into PARTICIPATES_IN values (1014  ,  782) ;
insert into PARTICIPATES_IN values (1014  ,  770) ;
insert into PARTICIPATES_IN values (1014  ,  772) ;

insert into PARTICIPATES_IN values (1015  ,  785) ;


insert into PARTICIPATES_IN values (1016  ,  791) ;
insert into PARTICIPATES_IN values (1016  ,  772) ;

insert into PARTICIPATES_IN values (1017  ,  791) ;
insert into PARTICIPATES_IN values (1017  ,  771) ;
insert into PARTICIPATES_IN values (1017  ,  770) ;

insert into PARTICIPATES_IN values (1018  ,  790) ;
insert into PARTICIPATES_IN values (1018  ,  785) ;
insert into PARTICIPATES_IN values (1018  ,  784) ;
insert into PARTICIPATES_IN values (1018  ,  777) ;
insert into PARTICIPATES_IN values (1018  ,  772) ;
insert into PARTICIPATES_IN values (1018  ,  770) ;

insert into PARTICIPATES_IN values (1019  ,  785) ;
insert into PARTICIPATES_IN values (1019  ,  790) ;

insert into PARTICIPATES_IN values (1020  ,  780) ;


insert into PARTICIPATES_IN values (1021  ,  780) ;
insert into PARTICIPATES_IN values (1021  ,  776) ;

insert into PARTICIPATES_IN values (1022  ,  782) ;
insert into PARTICIPATES_IN values (1022  ,  790) ;

insert into PARTICIPATES_IN values (1023  ,  790) ;
insert into PARTICIPATES_IN values (1023  ,  776) ;

insert into PARTICIPATES_IN values (1024  ,  778) ;
insert into PARTICIPATES_IN values (1024  ,  777) ;
insert into PARTICIPATES_IN values (1024  ,  780) ;

insert into PARTICIPATES_IN values (1025  ,  780) ;
insert into PARTICIPATES_IN values (1025  ,  777) ;
insert into PARTICIPATES_IN values (1025  ,  770) ;

insert into PARTICIPATES_IN values (1028  ,  785) ;


insert into PARTICIPATES_IN values (1029  ,  785) ;
insert into PARTICIPATES_IN values (1029  ,  790) ;

insert into PARTICIPATES_IN values (1030  ,  780) ;
insert into PARTICIPATES_IN values (1030  ,  790) ;

insert into PARTICIPATES_IN values (1033  ,  780) ;

insert into PARTICIPATES_IN values (1034  ,  780) ;
insert into PARTICIPATES_IN values (1034  ,  777) ;
insert into PARTICIPATES_IN values (1034  ,  772) ;
insert into PARTICIPATES_IN values (1034  ,  771) ;

insert into PARTICIPATES_IN values (1035  ,  777) ;
insert into PARTICIPATES_IN values (1035  ,  780) ;
insert into PARTICIPATES_IN values (1035  ,  784) ;





insert into LIVES_IN values ( 1001  , 109  , 105  ) ;
insert into LIVES_IN values ( 1002  , 100  , 112  ) ;
insert into LIVES_IN values ( 1003  , 100  , 124  ) ;
insert into LIVES_IN values ( 1004  , 140  , 215  ) ;
insert into LIVES_IN values ( 1005  , 160  , 333  ) ;
insert into LIVES_IN values ( 1007  , 140  , 113  ) ;
insert into LIVES_IN values ( 1008  , 160  , 334  ) ;
insert into LIVES_IN values ( 1009  , 140  , 332  ) ;
insert into LIVES_IN values ( 1010  , 160  , 443  ) ;
insert into LIVES_IN values ( 1011  , 140  , 102  ) ;
insert into LIVES_IN values ( 1012  , 160  , 333  ) ;
insert into LIVES_IN values ( 1014  , 104  , 211  ) ;
insert into LIVES_IN values ( 1015  , 160  , 443  ) ;
insert into LIVES_IN values ( 1016  , 140  , 301  ) ;
insert into LIVES_IN values ( 1017  , 140  , 319  ) ;
insert into LIVES_IN values ( 1018  , 140  , 225  ) ;
insert into LIVES_IN values ( 1020  , 160  , 405  ) ;
insert into LIVES_IN values ( 1021  , 160  , 405  ) ;
insert into LIVES_IN values ( 1022  , 100  , 153  ) ;
insert into LIVES_IN values ( 1023  , 160  , 317  ) ;
insert into LIVES_IN values ( 1024  , 100  , 151  ) ;
insert into LIVES_IN values ( 1025  , 160  , 317  ) ;
insert into LIVES_IN values ( 1027  , 140  , 208  ) ;
insert into LIVES_IN values ( 1028  , 110  , 48  ) ;
insert into LIVES_IN values ( 1029  , 140  , 418  ) ;
insert into LIVES_IN values ( 1030  , 109  , 211  ) ;
insert into LIVES_IN values ( 1031  , 100  , 112  ) ;
insert into LIVES_IN values ( 1032  , 109  , 105  ) ;
insert into LIVES_IN values ( 1033  , 117  , 3  ) ;
insert into LIVES_IN values ( 1034  , 160  , 105  ) ;
insert into LIVES_IN values ( 1035  , 100  , 124  ) ;


insert into FACULTY_PARTICIPATES_IN values ( 1082, 784) ;
insert into FACULTY_PARTICIPATES_IN values ( 1082, 785) ;
insert into FACULTY_PARTICIPATES_IN values ( 1082, 790) ;

insert into FACULTY_PARTICIPATES_IN values ( 1121, 771) ;
insert into FACULTY_PARTICIPATES_IN values ( 1121, 777) ;
insert into FACULTY_PARTICIPATES_IN values ( 1121, 770) ;

insert into FACULTY_PARTICIPATES_IN values ( 1193, 790) ;
insert into FACULTY_PARTICIPATES_IN values ( 1193, 796) ;
insert into FACULTY_PARTICIPATES_IN values ( 1193, 773) ;

insert into FACULTY_PARTICIPATES_IN values ( 2192, 773) ;
insert into FACULTY_PARTICIPATES_IN values ( 2192, 790) ;
insert into FACULTY_PARTICIPATES_IN values ( 2192, 778) ;

insert into FACULTY_PARTICIPATES_IN values ( 3457, 782) ;
insert into FACULTY_PARTICIPATES_IN values ( 3457, 771) ;
insert into FACULTY_PARTICIPATES_IN values ( 3457, 784) ;

insert into FACULTY_PARTICIPATES_IN values ( 4230, 790) ;
insert into FACULTY_PARTICIPATES_IN values ( 4230, 785) ;

insert into FACULTY_PARTICIPATES_IN values ( 6112, 785) ;
insert into FACULTY_PARTICIPATES_IN values ( 6112, 772) ;

insert into FACULTY_PARTICIPATES_IN values ( 7723, 785) ;
insert into FACULTY_PARTICIPATES_IN values ( 7723, 770) ;

insert into FACULTY_PARTICIPATES_IN values ( 8114, 776) ;

insert into FACULTY_PARTICIPATES_IN values ( 8721, 770) ;
insert into FACULTY_PARTICIPATES_IN values ( 8721, 780) ;

insert into FACULTY_PARTICIPATES_IN values ( 8741, 780) ;
insert into FACULTY_PARTICIPATES_IN values ( 8741, 790) ;

insert into FACULTY_PARTICIPATES_IN values ( 8918, 780) ;
insert into FACULTY_PARTICIPATES_IN values ( 8918, 782) ;
insert into FACULTY_PARTICIPATES_IN values ( 8918, 771) ;

insert into FACULTY_PARTICIPATES_IN values ( 2881, 790) ;
insert into FACULTY_PARTICIPATES_IN values ( 2881, 784) ;

insert into FACULTY_PARTICIPATES_IN values ( 4432, 770) ;
insert into FACULTY_PARTICIPATES_IN values ( 4432, 771) ;

insert into FACULTY_PARTICIPATES_IN values ( 5718, 776) ;


insert into FACULTY_PARTICIPATES_IN values ( 6182, 776) ;
insert into FACULTY_PARTICIPATES_IN values ( 6182, 785) ;

insert into FACULTY_PARTICIPATES_IN values ( 1177, 790) ;
insert into FACULTY_PARTICIPATES_IN values ( 1177, 770) ;

insert into FACULTY_PARTICIPATES_IN values ( 9922, 796) ;



insert into CITY values ( 'BAL' , 'Baltimore' , 'MD' , 'USA' , 39.288 , -76.617
 ) ;
insert into CITY values ( 'PIT' , 'Pittsburgh' , 'PA' , 'USA' , 40.437 , 
-80.000 ) ;
insert into CITY values ( 'PHL' , 'Philadelphia' , 'PA' , 'USA' , 39.950 , 
-75.150 ) ;
insert into CITY values ( 'WAS' , 'Washington' , 'DC' , 'USA' , 38.892 , 
-77.017 ) ;
insert into CITY values ( 'NYC' , 'New York' , 'NY' , 'USA' , 40.849 , -73.867)
 ;
insert into CITY values ( 'ATL' , 'Atlanta' , 'GA' , 'USA' , 33.763 , -84.317 )
 ;
insert into CITY values ( 'EWR' , 'Newark' , 'NJ' , 'USA' , 40.737 , -74.167 )
;
insert into CITY values ( 'FRE' , 'Frederick' , 'MD' , 'USA' , 39.415 , -77.417
 ) ;
insert into CITY values ( 'NAR' , 'Newark' , 'DE' , 'USA' , 39.683 , -75.750 )
;
insert into CITY values ( 'SAN' , 'San Diego' , 'CA' , 'USA' , 32.713 , 
-117.150) ;
insert into CITY values ( 'LOS' , 'Los Angeles' , 'CA' , 'USA' , 34.058 ,
-118.250) ;
insert into CITY values ( 'HON' , 'Honolulu' , 'HI' , 'USA' , 21.313 , -157.850
 ) ;
insert into CITY values ( 'SFO' , 'San Francisco' , 'CA' , 'USA' , 37.775 ,
-122.417) ;
insert into CITY values ( 'PVD' , 'Providence' , 'RI' , 'USA' , 41.817 , 
-71.400 ) ;
insert into CITY values ( 'BOS' , 'Boston' , 'MA' , 'USA' , 42.362 , -71.050 )
;
insert into CITY values ( 'DET' , 'Detroit' , 'MI' , 'USA' , 42.323 , -83.167 )
 ;
insert into CITY values ( 'CHI' , 'Chicago' , 'IL' , 'USA' , 41.883 , -87.617 )
 ;
insert into CITY values ( 'ROC' , 'Rochester' , 'NY' , 'USA' , 43.158 , -77.600
 ) ;
insert into CITY values ( 'DAL' , 'Dallas' , 'TX' , 'USA' , 32.777 , -96.800 )
;
insert into CITY values ( 'HOU' , 'Houston' , 'TX' , 'USA' , 29.834 , -95.000 )
 ;
insert into CITY values ( 'MIA' , 'Miami' , 'FL' , 'USA' , 25.465 , -80.150 ) ;

insert into CITY values ( 'CPK' , 'College Park' , 'MD' , 'USA' , 38.987 ,
-76.933) ;
insert into CITY values ( 'YYZ' , 'Toronto' , 'ON' , 'CANADA' , 43.650 , 
-79.333 ) ;
insert into CITY values ( 'DEL' , 'Delhi' , 'DE' , 'INDIA' , 28.617 , 77.217 )
;
insert into CITY values ( 'PEK' , 'Beijing' , 'BE' , 'CHINA' , 39.917 ,
116.417) ;
insert into CITY values ( 'HKG' , 'Hong Kong' , 'HK' , 'CHINA' , 22.250 ,
114.167) ;
insert into CITY values ( 'TYO' , 'Tokyo' , 'XX' , 'JAPAN' , 35.700 , 139.767 )
 ;
insert into CITY values ( 'LON' , 'London' , 'EN' , 'UK' , 51.500 , -0.167 ) ;
insert into CITY values ( 'PAR' , 'Paris' , 'XX' , 'FRANCE' , 48.867 , 2.333 )
;
insert into CITY values ( 'JNB' , 'Johannesburg' , 'XX' , 'SAFRICA' , -25.550 ,
 28.000 ) ;
insert into CITY values ( 'BKK' , 'Bangkok' , 'XX' , 'THAILAND' , 13.733 , 
100.500 ) ;


insert into DIRECT_DISTANCE values ( 'BAL' , 'ATL' , 576 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'BAL' , 0 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'BKK' , 9631 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'BOS' , 370 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'CHI' , 621 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'CPK' , 21 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'DAL' , 1217 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'DEL' , 7469 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'DET' , 408 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'EWR' , 169 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'FRE' , 35 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'HKG' , 8409 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'HON' , 4832 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'HOU' , 1240 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'JNB' , 7850 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'LON' , 3652 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'LOS' , 2329 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'MIA' , 946 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'NAR' , 71 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'NYC' , 185 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'PAR' , 3800 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'PEK' , 8041 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'PHL' , 105 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'PIT' , 210 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'PVD' , 328 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'ROC' , 277 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'SAN' , 2295 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'SFO' , 2457 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'TYO' , 6740 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'WAS' , 35 ) ;
insert into DIRECT_DISTANCE values ( 'BAL' , 'YYZ' , 347 ) ;
insert into DIRECT_DISTANCE values ( 'ATL' , 'BAL' , 576 ) ;
insert into DIRECT_DISTANCE values ( 'ATL' , 'PHL' , 665 ) ;
insert into DIRECT_DISTANCE values ( 'BKK' , 'BAL' , 9631 ) ;
insert into DIRECT_DISTANCE values ( 'BOS' , 'BAL' , 370 ) ;
insert into DIRECT_DISTANCE values ( 'BOS' , 'EWR' , 200 ) ;
insert into DIRECT_DISTANCE values ( 'BOS' , 'PAR' , 3448 ) ;
insert into DIRECT_DISTANCE values ( 'BOS' , 'PHL' , 280 ) ;
insert into DIRECT_DISTANCE values ( 'CHI' , 'BAL' , 621 ) ;
insert into DIRECT_DISTANCE values ( 'CHI' , 'PHL' , 678 ) ;
insert into DIRECT_DISTANCE values ( 'CPK' , 'BAL' , 21 ) ;
insert into DIRECT_DISTANCE values ( 'DAL' , 'BAL' , 1217 ) ;
insert into DIRECT_DISTANCE values ( 'DEL' , 'BAL' , 7469 ) ;
insert into DIRECT_DISTANCE values ( 'DET' , 'BAL' , 408 ) ;
insert into DIRECT_DISTANCE values ( 'DET' , 'EWR' , 488 ) ;
insert into DIRECT_DISTANCE values ( 'DET' , 'PHL' , 453 ) ;
insert into DIRECT_DISTANCE values ( 'EWR' , 'BAL' , 169 ) ;
insert into DIRECT_DISTANCE values ( 'EWR' , 'BOS' , 200 ) ;
insert into DIRECT_DISTANCE values ( 'EWR' , 'DET' , 488 ) ;
insert into DIRECT_DISTANCE values ( 'EWR' , 'LON' , 3458 ) ;
insert into DIRECT_DISTANCE values ( 'EWR' , 'LOS' , 2454 ) ;
insert into DIRECT_DISTANCE values ( 'EWR' , 'LOS' , 2454 ) ;
insert into DIRECT_DISTANCE values ( 'EWR' , 'PHL' , 89 ) ;
insert into DIRECT_DISTANCE values ( 'EWR' , 'SAN' , 2425 ) ;
insert into DIRECT_DISTANCE values ( 'EWR' , 'WAS' , 199 ) ;
insert into DIRECT_DISTANCE values ( 'FRE' , 'BAL' , 35 ) ;
insert into DIRECT_DISTANCE values ( 'HKG' , 'BAL' , 8409 ) ;
insert into DIRECT_DISTANCE values ( 'HON' , 'BAL' , 4832 ) ;
insert into DIRECT_DISTANCE values ( 'HOU' , 'BAL' , 1240 ) ;
insert into DIRECT_DISTANCE values ( 'HOU' , 'LOS' , 1385 ) ;
insert into DIRECT_DISTANCE values ( 'HOU' , 'PHL' , 1324 ) ;
insert into DIRECT_DISTANCE values ( 'HOU' , 'SAN' , 1270 ) ;
insert into DIRECT_DISTANCE values ( 'HOU' , 'SFO' , 1635 ) ;
insert into DIRECT_DISTANCE values ( 'JNB' , 'BAL' , 7850 ) ;
insert into DIRECT_DISTANCE values ( 'LON' , 'BAL' , 3652 ) ;
insert into DIRECT_DISTANCE values ( 'LON' , 'EWR' , 3458 ) ;
insert into DIRECT_DISTANCE values ( 'LON' , 'NYC' , 3452 ) ;
insert into DIRECT_DISTANCE values ( 'LON' , 'PHL' , 3546 ) ;
insert into DIRECT_DISTANCE values ( 'LON' , 'TYO' , 5975 ) ;
insert into DIRECT_DISTANCE values ( 'LON' , 'WAS' , 3650 ) ;
insert into DIRECT_DISTANCE values ( 'LOS' , 'BAL' , 2329 ) ;
insert into DIRECT_DISTANCE values ( 'LOS' , 'EWR' , 2454 ) ;
insert into DIRECT_DISTANCE values ( 'LOS' , 'EWR' , 2454 ) ;
insert into DIRECT_DISTANCE values ( 'LOS' , 'HOU' , 1385 ) ;
insert into DIRECT_DISTANCE values ( 'LOS' , 'PHL' , 2401 ) ;
insert into DIRECT_DISTANCE values ( 'LOS' , 'SAN' , 109 ) ;
insert into DIRECT_DISTANCE values ( 'MIA' , 'BAL' , 946 ) ;
insert into DIRECT_DISTANCE values ( 'NAR' , 'BAL' , 71 ) ;
insert into DIRECT_DISTANCE values ( 'NYC' , 'BAL' , 185 ) ;
insert into DIRECT_DISTANCE values ( 'NYC' , 'LON' , 3452 ) ;
insert into DIRECT_DISTANCE values ( 'PAR' , 'BAL' , 3800 ) ;
insert into DIRECT_DISTANCE values ( 'PAR' , 'BOS' , 3448 ) ;
insert into DIRECT_DISTANCE values ( 'PEK' , 'BAL' , 8041 ) ;
insert into DIRECT_DISTANCE values ( 'PHL' , 'ATL' , 665 ) ;
insert into DIRECT_DISTANCE values ( 'PHL' , 'BAL' , 105 ) ;
insert into DIRECT_DISTANCE values ( 'PHL' , 'BOS' , 280 ) ;
insert into DIRECT_DISTANCE values ( 'PHL' , 'CHI' , 678 ) ;
insert into DIRECT_DISTANCE values ( 'PHL' , 'DET' , 453 ) ;
insert into DIRECT_DISTANCE values ( 'PHL' , 'EWR' , 89 ) ;
insert into DIRECT_DISTANCE values ( 'PHL' , 'HOU' , 1324 ) ;
insert into DIRECT_DISTANCE values ( 'PHL' , 'LON' , 3546 ) ;
insert into DIRECT_DISTANCE values ( 'PHL' , 'LOS' , 2401 ) ;
insert into DIRECT_DISTANCE values ( 'PHL' , 'PIT' , 267 ) ;
insert into DIRECT_DISTANCE values ( 'PHL' , 'SAN' , 2369 ) ;
insert into DIRECT_DISTANCE values ( 'PHL' , 'SFO' , 2521 ) ;
insert into DIRECT_DISTANCE values ( 'PHL' , 'WAS' , 119 ) ;
insert into DIRECT_DISTANCE values ( 'PIT' , 'BAL' , 210 ) ;
insert into DIRECT_DISTANCE values ( 'PIT' , 'PHL' , 267 ) ;
insert into DIRECT_DISTANCE values ( 'PIT' , 'SAN' , 2106 ) ;
insert into DIRECT_DISTANCE values ( 'PVD' , 'BAL' , 328 ) ;
insert into DIRECT_DISTANCE values ( 'ROC' , 'BAL' , 277 ) ;
insert into DIRECT_DISTANCE values ( 'ROC' , 'WAS' , 296 ) ;
insert into DIRECT_DISTANCE values ( 'SAN' , 'BAL' , 2295 ) ;
insert into DIRECT_DISTANCE values ( 'SAN' , 'EWR' , 2425 ) ;
insert into DIRECT_DISTANCE values ( 'SAN' , 'HOU' , 1270 ) ;
insert into DIRECT_DISTANCE values ( 'SAN' , 'LOS' , 109 ) ;
insert into DIRECT_DISTANCE values ( 'SAN' , 'PHL' , 2369 ) ;
insert into DIRECT_DISTANCE values ( 'SAN' , 'PIT' , 2106 ) ;
insert into DIRECT_DISTANCE values ( 'SFO' , 'BAL' , 2457 ) ;
insert into DIRECT_DISTANCE values ( 'SFO' , 'HOU' , 1635 ) ;
insert into DIRECT_DISTANCE values ( 'SFO' , 'PHL' , 2521 ) ;
insert into DIRECT_DISTANCE values ( 'TYO' , 'BAL' , 6740 ) ;
insert into DIRECT_DISTANCE values ( 'TYO' , 'LON' , 5975 ) ;
insert into DIRECT_DISTANCE values ( 'WAS' , 'BAL' , 35 ) ;
insert into DIRECT_DISTANCE values ( 'WAS' , 'EWR' , 199 ) ;
insert into DIRECT_DISTANCE values ( 'WAS' , 'LON' , 3650 ) ;
insert into DIRECT_DISTANCE values ( 'WAS' , 'PHL' , 119 ) ;
insert into DIRECT_DISTANCE values ( 'WAS' , 'ROC' , 296 ) ;
insert into DIRECT_DISTANCE values ( 'YYZ' , 'BAL' , 347 ) ;


drop table ALLERGY;
create table ALLERGY (
  AllergyName varchar2(20),
  AllergyType varchar2(15)
);

drop table HAS_ALLERGY;
create table HAS_ALLERGY (
  StuID number,
  AllergyName varchar2(20)
);

drop table PREFERENCES;
create table PREFERENCES (
  StuID number,
  SleepHabits varchar2(20),
  MusicType varchar2(20),
  Smoking varchar2(10)
);


insert into ALLERGY values ( 'Pollen', 'Environmental');
insert into ALLERGY values ( 'Mold', 'Environmental');
insert into ALLERGY values ( 'Feathers', 'Environmental');
insert into ALLERGY values ( 'Ragweed', 'Environmental');
insert into ALLERGY values ( 'Dust', 'Environmental');
insert into ALLERGY values ( 'Nuts', 'Food');
insert into ALLERGY values ( 'Strawberries', 'Food');
insert into ALLERGY values ( 'Shellfish', 'Food');
insert into ALLERGY values ( 'Anchovies', 'Food');
insert into ALLERGY values ( 'Wheat', 'Food');
insert into ALLERGY values ( 'Lactose', 'Food');
insert into ALLERGY values ( 'Penicillin', 'Medicine');
insert into ALLERGY values ( 'Cats', 'AnimalHair');
insert into ALLERGY values ( 'Dogs', 'AnimalHair');
insert into ALLERGY values ( 'OtherAnimals', 'AnimalHair');


insert into HAS_ALLERGY values ( 1001  , 'Nuts' );
insert into HAS_ALLERGY values ( 1001  , 'Shellfish' );
insert into HAS_ALLERGY values ( 1001  , 'Cats' );
insert into HAS_ALLERGY values ( 1003  , 'Penicillin' );
insert into HAS_ALLERGY values ( 1003  , 'Cats' );
insert into HAS_ALLERGY values ( 1005  , 'Pollen' );
insert into HAS_ALLERGY values ( 1005  , 'Nuts' );
insert into HAS_ALLERGY values ( 1005  , 'Shellfish' );
insert into HAS_ALLERGY values ( 1006  , 'Shellfish' );
insert into HAS_ALLERGY values ( 1006  , 'Lactose' );
insert into HAS_ALLERGY values ( 1008  , 'Shellfish' );
insert into HAS_ALLERGY values ( 1012  , 'Nuts' );
insert into HAS_ALLERGY values ( 1012  , 'Wheat' );
insert into HAS_ALLERGY values ( 1014  , 'Ragweed' );
insert into HAS_ALLERGY values ( 1014  , 'Strawberries' );
insert into HAS_ALLERGY values ( 1014  , 'Anchovies' );
insert into HAS_ALLERGY values ( 1014  , 'Penicillin' );
insert into HAS_ALLERGY values ( 1014  , 'Dogs' );
insert into HAS_ALLERGY values ( 1016  , 'Ragweed' );
insert into HAS_ALLERGY values ( 1016  , 'Strawberries' );
insert into HAS_ALLERGY values ( 1016  , 'Anchovies' );
insert into HAS_ALLERGY values ( 1016  , 'Pollen' );
insert into HAS_ALLERGY values ( 1016  , 'Shellfish' );
insert into HAS_ALLERGY values ( 1016  , 'Nuts' );
insert into HAS_ALLERGY values ( 1016  , 'Penicillin' );
insert into HAS_ALLERGY values ( 1016  , 'Cats' );
insert into HAS_ALLERGY values ( 1017  , 'Pollen' );
insert into HAS_ALLERGY values ( 1017  , 'Nuts' );
insert into HAS_ALLERGY values ( 1017  , 'Shellfish' );
insert into HAS_ALLERGY values ( 1017  , 'Penicillin' );
insert into HAS_ALLERGY values ( 1018  , 'Nuts' );
insert into HAS_ALLERGY values ( 1018  , 'Cats' );
insert into HAS_ALLERGY values ( 1018  , 'Nuts' );
insert into HAS_ALLERGY values ( 1018  , 'Penicillin' );
insert into HAS_ALLERGY values ( 1018  , 'Lactose' );
insert into HAS_ALLERGY values ( 1019  , 'Penicillin' );
insert into HAS_ALLERGY values ( 1019  , 'Cats' );
insert into HAS_ALLERGY values ( 1021  , 'Dust' );
insert into HAS_ALLERGY values ( 1021  , 'Pollen' );
insert into HAS_ALLERGY values ( 1021  , 'Anchovies' );
insert into HAS_ALLERGY values ( 1022  , 'Pollen' );
insert into HAS_ALLERGY values ( 1022  , 'Nuts' );
insert into HAS_ALLERGY values ( 1023  , 'Dust' );
insert into HAS_ALLERGY values ( 1024  , 'Pollen' );
insert into HAS_ALLERGY values ( 1024  , 'Shellfish' );
insert into HAS_ALLERGY values ( 1024  , 'Nuts' );
insert into HAS_ALLERGY values ( 1024  , 'Penicillin' );
insert into HAS_ALLERGY values ( 1024  , 'Cats' );
insert into HAS_ALLERGY values ( 1025  , 'Nuts' );
insert into HAS_ALLERGY values ( 1025  , 'Pollen' );
insert into HAS_ALLERGY values ( 1025  , 'Anchovies' );
insert into HAS_ALLERGY values ( 1025  , 'Penicillin' );
insert into HAS_ALLERGY values ( 1026  , 'Dust' );
insert into HAS_ALLERGY values ( 1028  , 'Cats' );
insert into HAS_ALLERGY values ( 1031  , 'Anchovies' );
insert into HAS_ALLERGY values ( 1031  , 'Shellfish' );
insert into HAS_ALLERGY values ( 1031  , 'Cats' );
insert into HAS_ALLERGY values ( 1032  , 'Cats' );
insert into HAS_ALLERGY values ( 1033  , 'Cats' );
insert into HAS_ALLERGY values ( 1035  , 'Penicillin' );
insert into HAS_ALLERGY values ( 1035  , 'Anchovies' );



insert into PREFERENCES values ( 1001, 'LateNighter', 'StudiesWith', 'Yes' );
insert into PREFERENCES values ( 1002, 'LateNighter', 'StudiesWith', 'Yes' );
insert into PREFERENCES values ( 1003, 'MidRange', 'StudiesWith', 'no' );
insert into PREFERENCES values ( 1004, 'EarlyRiser', 'StudiesWithout', 'no' );
insert into PREFERENCES values ( 1005, 'MidRange', 'StudiesWithout', 
'no-accept' );
insert into PREFERENCES values ( 1006, 'LateNighter', 'StudiesWith', 
'no-accept' );
insert into PREFERENCES values ( 1007, 'EarlyRiser', 'StudiesWithout', 'Yes' );
insert into PREFERENCES values ( 1008, 'LateNighter', 'StudiesWith', 'Yes' );
insert into PREFERENCES values ( 1009, 'EarlyRiser', 'StudiesWithout', 'Yes' );
insert into PREFERENCES values ( 1010, 'MidRange', 'StudiesWithout', 'Yes' );
insert into PREFERENCES values ( 1011, 'LateNighter', 'StudiesWithout', 'no' );
insert into PREFERENCES values ( 1012, 'LateNighter', 'StudiesWith', 'no' );
insert into PREFERENCES values ( 1014, 'EarlyRiser', 'StudiesWithout', 'no' );
insert into PREFERENCES values ( 1015, 'MidRange', 'StudiesWithout', 'Yes' );
insert into PREFERENCES values ( 1016, 'MidRange', 'StudiesWith', 'Yes' );
insert into PREFERENCES values ( 1017, 'EarlyRiser', 'StudiesWithout', 'no' );
insert into PREFERENCES values ( 1018, 'MidRange', 'StudiesWithout', 'Yes' );
insert into PREFERENCES values ( 1019, 'LateNighter', 'StudiesWithout', 'no' );
insert into PREFERENCES values ( 1020, 'LateNighter', 'StudiesWith', 'Yes' );
insert into PREFERENCES values ( 1021, 'EarlyRiser', 'StudiesWithout', 'Yes' );
insert into PREFERENCES values ( 1022, 'MidRange', 'StudiesWithout', 
'no-accept' );
insert into PREFERENCES values ( 1023, 'MidRange', 'StudiesWith', 'Yes' );
insert into PREFERENCES values ( 1024, 'EarlyRiser', 'StudiesWithout', 'Yes' );
insert into PREFERENCES values ( 1025, 'LateNighter', 'StudiesWith', 'Yes' );
insert into PREFERENCES values ( 1026, 'LateNighter', 'StudiesWith',
'no-accept' );
insert into PREFERENCES values ( 1027, 'LateNighter', 'StudiesWith', 'Yes' );
insert into PREFERENCES values ( 1028, 'EarlyRiser', 'StudiesWithout', 'no' );
insert into PREFERENCES values ( 1029, 'MidRange', 'StudiesWith', 'no' );
insert into PREFERENCES values ( 1030, 'LateNighter', 'StudiesWithout', 'Yes' )
;
insert into PREFERENCES values ( 1031, 'EarlyRiser', 'StudiesWith', 'no' );
insert into PREFERENCES values ( 1032, 'LateNighter', 'StudiesWithout', 'no' );
insert into PREFERENCES values ( 1033, 'LateNighter', 'StudiesWith', 'no' );
insert into PREFERENCES values ( 1034, 'EarlyRiser', 'StudiesWith', 'no' );
insert into PREFERENCES values ( 1035, 'LateNighter', 'StudiesWith', 'no' );


insert into LOVES values (1001, 1014);
insert into LOVES values (1001, 1027);
insert into LOVES values (1002, 1005);
insert into LOVES values (1003, 1009);
insert into LOVES values (1005, 1035);
insert into LOVES values (1006, 1008);
insert into LOVES values (1006, 1024);
insert into LOVES values (1007, 1011);
insert into LOVES values (1008, 1006);
insert into LOVES values (1009, 1030);
insert into LOVES values (1010, 1015);
insert into LOVES values (1012, 1024);
insert into LOVES values (1014, 1001);
insert into LOVES values (1015, 1010);
insert into LOVES values (1016, 1017);
insert into LOVES values (1017, 1016);
insert into LOVES values (1019, 1015);
insert into LOVES values (1020, 1031);
insert into LOVES values (1021, 1031);
insert into LOVES values (1022, 1031);
insert into LOVES values (1023, 1031);
insert into LOVES values (1024, 1012);
insert into LOVES values (1024, 1005);
insert into LOVES values (1025, 1031);
insert into LOVES values (1026, 1015);
insert into LOVES values (1028, 1024);
insert into LOVES values (1029, 1030);
insert into LOVES values (1031, 1002);
insert into LOVES values (1032, 1001);
insert into LOVES values (1032, 1002);
insert into LOVES values (1032, 1003);
insert into LOVES values (1032, 1008);
insert into LOVES values (1032, 1030);
insert into LOVES values (1032, 1031);
insert into LOVES values (1032, 1035);
insert into LOVES values (1033, 1031);
insert into LOVES values (1034, 1031);

insert into LIKES values (1001, 1006);
insert into LIKES values (1001, 1012);
insert into LIKES values (1001, 1014);
insert into LIKES values (1001, 1016);
insert into LIKES values (1001, 1022);
insert into LIKES values (1001, 1027);
insert into LIKES values (1002, 1001);
insert into LIKES values (1002, 1002);
insert into LIKES values (1002, 1005);
insert into LIKES values (1002, 1019);
insert into LIKES values (1002, 1020);
insert into LIKES values (1002, 1022);
insert into LIKES values (1002, 1026);
insert into LIKES values (1002, 1028);
insert into LIKES values (1003, 1002);
insert into LIKES values (1003, 1003);
insert into LIKES values (1003, 1005);
insert into LIKES values (1003, 1009);
insert into LIKES values (1003, 1028);
insert into LIKES values (1003, 1030);
insert into LIKES values (1004, 1004);
insert into LIKES values (1004, 1005);
insert into LIKES values (1004, 1007);
insert into LIKES values (1004, 1009);
insert into LIKES values (1004, 1011);
insert into LIKES values (1005, 1001);
insert into LIKES values (1005, 1005);
insert into LIKES values (1005, 1006);
insert into LIKES values (1005, 1008);
insert into LIKES values (1005, 1010);
insert into LIKES values (1005, 1022);
insert into LIKES values (1005, 1026);
insert into LIKES values (1005, 1027);
insert into LIKES values (1005, 1029);
insert into LIKES values (1006, 1006);
insert into LIKES values (1006, 1008);
insert into LIKES values (1006, 1017);
insert into LIKES values (1006, 1019);
insert into LIKES values (1006, 1021);
insert into LIKES values (1006, 1024);
insert into LIKES values (1006, 1030);
insert into LIKES values (1006, 1032);
insert into LIKES values (1006, 1033);
insert into LIKES values (1006, 1034);
insert into LIKES values (1006, 1035);
insert into LIKES values (1007, 1001);
insert into LIKES values (1007, 1003);
insert into LIKES values (1007, 1005);
insert into LIKES values (1007, 1008);
insert into LIKES values (1007, 1011);
insert into LIKES values (1007, 1021);
insert into LIKES values (1007, 1023);
insert into LIKES values (1007, 1034);
insert into LIKES values (1008, 1001);
insert into LIKES values (1008, 1003);
insert into LIKES values (1008, 1004);
insert into LIKES values (1008, 1005);
insert into LIKES values (1008, 1006);
insert into LIKES values (1008, 1008);
insert into LIKES values (1008, 1009);
insert into LIKES values (1008, 1011);
insert into LIKES values (1008, 1014);
insert into LIKES values (1008, 1016);
insert into LIKES values (1008, 1017);
insert into LIKES values (1008, 1018);
insert into LIKES values (1008, 1020);
insert into LIKES values (1008, 1021);
insert into LIKES values (1008, 1023);
insert into LIKES values (1008, 1024);
insert into LIKES values (1008, 1026);
insert into LIKES values (1008, 1027);
insert into LIKES values (1008, 1032);
insert into LIKES values (1008, 1033);
insert into LIKES values (1009, 1005);
insert into LIKES values (1009, 1006);
insert into LIKES values (1009, 1008);
insert into LIKES values (1009, 1010);
insert into LIKES values (1009, 1014);
insert into LIKES values (1009, 1015);
insert into LIKES values (1009, 1030);
insert into LIKES values (1010, 1001);
insert into LIKES values (1010, 1009);
insert into LIKES values (1010, 1011);
insert into LIKES values (1010, 1012);
insert into LIKES values (1010, 1014);
insert into LIKES values (1010, 1015);
insert into LIKES values (1010, 1016);
insert into LIKES values (1010, 1017);
insert into LIKES values (1010, 1030);
insert into LIKES values (1011, 1011);
insert into LIKES values (1011, 1020);
insert into LIKES values (1011, 1021);
insert into LIKES values (1011, 1023);
insert into LIKES values (1011, 1025);
insert into LIKES values (1011, 1027);
insert into LIKES values (1011, 1028);
insert into LIKES values (1011, 1031);
insert into LIKES values (1012, 1001);
insert into LIKES values (1012, 1002);
insert into LIKES values (1012, 1004);
insert into LIKES values (1012, 1006);
insert into LIKES values (1012, 1008);
insert into LIKES values (1012, 1011);
insert into LIKES values (1012, 1012);
insert into LIKES values (1012, 1014);
insert into LIKES values (1012, 1015);
insert into LIKES values (1012, 1023);
insert into LIKES values (1012, 1024);
insert into LIKES values (1012, 1026);
insert into LIKES values (1012, 1029);
insert into LIKES values (1012, 1030);
insert into LIKES values (1014, 1001);
insert into LIKES values (1015, 1008);
insert into LIKES values (1015, 1009);
insert into LIKES values (1015, 1010);
insert into LIKES values (1015, 1011);
insert into LIKES values (1015, 1012);
insert into LIKES values (1015, 1014);
insert into LIKES values (1015, 1015);
insert into LIKES values (1015, 1016);
insert into LIKES values (1015, 1017);
insert into LIKES values (1015, 1030);
insert into LIKES values (1016, 1001);
insert into LIKES values (1016, 1002);
insert into LIKES values (1016, 1003);
insert into LIKES values (1016, 1004);
insert into LIKES values (1016, 1008);
insert into LIKES values (1016, 1016);
insert into LIKES values (1016, 1017);
insert into LIKES values (1016, 1025);
insert into LIKES values (1016, 1027);
insert into LIKES values (1016, 1030);
insert into LIKES values (1017, 1001);
insert into LIKES values (1017, 1002);
insert into LIKES values (1017, 1003);
insert into LIKES values (1017, 1004);
insert into LIKES values (1017, 1016);
insert into LIKES values (1017, 1017);
insert into LIKES values (1017, 1025);
insert into LIKES values (1017, 1027);
insert into LIKES values (1017, 1030);
insert into LIKES values (1019, 1008);
insert into LIKES values (1019, 1015);
insert into LIKES values (1019, 1019);
insert into LIKES values (1019, 1025);
insert into LIKES values (1019, 1026);
insert into LIKES values (1019, 1028);
insert into LIKES values (1019, 1030);
insert into LIKES values (1019, 1034);
insert into LIKES values (1019, 1035);
insert into LIKES values (1020, 1001);
insert into LIKES values (1020, 1004);
insert into LIKES values (1020, 1006);
insert into LIKES values (1020, 1008);
insert into LIKES values (1020, 1009);
insert into LIKES values (1020, 1012);
insert into LIKES values (1020, 1015);
insert into LIKES values (1020, 1017);
insert into LIKES values (1020, 1019);
insert into LIKES values (1020, 1020);
insert into LIKES values (1020, 1021);
insert into LIKES values (1020, 1022);
insert into LIKES values (1020, 1023);
insert into LIKES values (1020, 1025);
insert into LIKES values (1020, 1026);
insert into LIKES values (1020, 1027);
insert into LIKES values (1020, 1031);
insert into LIKES values (1021, 1008);
insert into LIKES values (1021, 1009);
insert into LIKES values (1021, 1012);
insert into LIKES values (1021, 1021);
insert into LIKES values (1021, 1030);
insert into LIKES values (1021, 1031);
insert into LIKES values (1022, 1001);
insert into LIKES values (1022, 1004);
insert into LIKES values (1022, 1006);
insert into LIKES values (1022, 1008);
insert into LIKES values (1022, 1031);
insert into LIKES values (1023, 1019);
insert into LIKES values (1023, 1020);
insert into LIKES values (1023, 1023);
insert into LIKES values (1023, 1025);
insert into LIKES values (1023, 1031);
insert into LIKES values (1024, 1005);
insert into LIKES values (1024, 1006);
insert into LIKES values (1024, 1007);
insert into LIKES values (1024, 1008);
insert into LIKES values (1024, 1009);
insert into LIKES values (1024, 1012);
insert into LIKES values (1024, 1015);
insert into LIKES values (1024, 1016);
insert into LIKES values (1024, 1024);
insert into LIKES values (1024, 1030);
insert into LIKES values (1024, 1031);
insert into LIKES values (1024, 1032);
insert into LIKES values (1024, 1033);
insert into LIKES values (1025, 1001);
insert into LIKES values (1025, 1003);
insert into LIKES values (1025, 1022);
insert into LIKES values (1025, 1024);
insert into LIKES values (1025, 1031);
insert into LIKES values (1026, 1014);
insert into LIKES values (1026, 1015);
insert into LIKES values (1026, 1018);
insert into LIKES values (1026, 1021);
insert into LIKES values (1027, 1008);
insert into LIKES values (1027, 1009);
insert into LIKES values (1027, 1010);
insert into LIKES values (1027, 1011);
insert into LIKES values (1027, 1012);
insert into LIKES values (1027, 1020);
insert into LIKES values (1027, 1021);
insert into LIKES values (1027, 1023);
insert into LIKES values (1027, 1025);
insert into LIKES values (1027, 1026);
insert into LIKES values (1027, 1027);
insert into LIKES values (1027, 1028);
insert into LIKES values (1028, 1003);
insert into LIKES values (1028, 1004);
insert into LIKES values (1028, 1008);
insert into LIKES values (1028, 1018);
insert into LIKES values (1028, 1020);
insert into LIKES values (1028, 1024);
insert into LIKES values (1028, 1031);
insert into LIKES values (1028, 1033);
insert into LIKES values (1028, 1034);
insert into LIKES values (1029, 1015);
insert into LIKES values (1029, 1018);
insert into LIKES values (1029, 1029);
insert into LIKES values (1029, 1030);
insert into LIKES values (1030, 1025);
insert into LIKES values (1030, 1026);
insert into LIKES values (1030, 1027);
insert into LIKES values (1030, 1030);
insert into LIKES values (1031, 1002);
insert into LIKES values (1031, 1016);
insert into LIKES values (1031, 1019);
insert into LIKES values (1031, 1021);
insert into LIKES values (1031, 1022);
insert into LIKES values (1031, 1031);
insert into LIKES values (1032, 1001);
insert into LIKES values (1032, 1002);
insert into LIKES values (1032, 1003);
insert into LIKES values (1032, 1008);
insert into LIKES values (1032, 1024);
insert into LIKES values (1032, 1025);
insert into LIKES values (1032, 1026);
insert into LIKES values (1032, 1027);
insert into LIKES values (1032, 1028);
insert into LIKES values (1032, 1029);
insert into LIKES values (1032, 1030);
insert into LIKES values (1032, 1031);
insert into LIKES values (1032, 1032);
insert into LIKES values (1032, 1033);
insert into LIKES values (1032, 1034);
insert into LIKES values (1032, 1035);
insert into LIKES values (1033, 1001);
insert into LIKES values (1033, 1002);
insert into LIKES values (1033, 1003);
insert into LIKES values (1033, 1004);
insert into LIKES values (1033, 1005);
insert into LIKES values (1033, 1006);
insert into LIKES values (1033, 1007);
insert into LIKES values (1033, 1008);
insert into LIKES values (1033, 1009);
insert into LIKES values (1033, 1010);
insert into LIKES values (1033, 1011);
insert into LIKES values (1033, 1012);
insert into LIKES values (1033, 1014);
insert into LIKES values (1033, 1015);
insert into LIKES values (1033, 1016);
insert into LIKES values (1033, 1017);
insert into LIKES values (1033, 1018);
insert into LIKES values (1033, 1019);
insert into LIKES values (1033, 1020);
insert into LIKES values (1033, 1021);
insert into LIKES values (1033, 1022);
insert into LIKES values (1033, 1023);
insert into LIKES values (1033, 1024);
insert into LIKES values (1033, 1025);
insert into LIKES values (1033, 1026);
insert into LIKES values (1033, 1027);
insert into LIKES values (1033, 1028);
insert into LIKES values (1033, 1029);
insert into LIKES values (1033, 1030);
insert into LIKES values (1033, 1031);
insert into LIKES values (1033, 1032);
insert into LIKES values (1033, 1033);
insert into LIKES values (1033, 1034);
insert into LIKES values (1033, 1035);
insert into LIKES values (1034, 1008);
insert into LIKES values (1034, 1019);
insert into LIKES values (1034, 1020);
insert into LIKES values (1034, 1021);
insert into LIKES values (1034, 1023);
insert into LIKES values (1034, 1025);
insert into LIKES values (1034, 1027);
insert into LIKES values (1034, 1031);
insert into LIKES values (1034, 1032);
insert into LIKES values (1034, 1034);
insert into LIKES values (1034, 1035);
insert into LIKES values (1035, 1008);
insert into LIKES values (1035, 1023);
insert into LIKES values (1035, 1024);
insert into LIKES values (1035, 1025);
insert into LIKES values (1035, 1026);
insert into LIKES values (1035, 1027);
insert into LIKES values (1035, 1031);
insert into LIKES values (1035, 1035);


spool off
prompt See file RESULT2.LST for report on success of table load.





