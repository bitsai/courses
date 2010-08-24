-- Benny Tsai
-- Database Systems
-- 600.315
-- Assignment 2

Prompt 1.;
SELECT fname, lname 
FROM Student 
WHERE stuid IN
	((SELECT stuid FROM Preferences WHERE smoking = 'Yes')
		UNION
	(SELECT stuid FROM Has_allergy WHERE allergyname IN 
		(SELECT allergyname FROM Allergy WHERE allergytype = 'Environmental')))
ORDER BY lname;

Prompt 2.;
SELECT fname, lname 
FROM Student 
WHERE stuid IN
	(SELECT A.stuid FROM Has_allergy A, Has_allergy B WHERE
		A.stuid = B.stuid
		AND
		A.allergyname IN (SELECT allergyname FROM Allergy WHERE allergytype = 'Food')
		AND
		B.allergyname IN (SELECT allergyname FROM Allergy WHERE allergytype = 'Food')
		AND
		A.allergyname <> B.allergyname)
ORDER BY lname;

Prompt 3.;
WITH Allergies AS
	(SELECT * FROM Has_allergy NATURAL INNER JOIN Allergy)
SELECT fname, lname
FROM Student 
WHERE
	NOT EXISTS
		((SELECT DISTINCT allergytype FROM Allergy)
		MINUS
		(SELECT DISTINCT allergytype FROM Allergies WHERE Allergies.stuid = Student.stuid))
ORDER BY lname;

Prompt 4.;
WITH Old_people_allergies AS
	(SELECT allergyname, COUNT(stuid) AS frequency
	FROM (SELECT stuid FROM Student WHERE age > 25) NATURAL INNER JOIN Has_allergy
	GROUP BY allergyname)
SELECT allergyname, allergytype 
FROM Old_people_allergies NATURAL INNER JOIN Allergy
WHERE frequency >= ALL(SELECT frequency FROM Old_people_allergies)
ORDER BY allergyname;

Prompt 5.;
WITH Full_info AS
	(SELECT * FROM Student NATURAL INNER JOIN Lives_in NATURAL INNER JOIN Preferences)
SELECT A.fname, A.lname, B.fname, B.lname FROM Full_info A, Full_info B WHERE
	A.dormid = B.dormid
	AND
	A.room_number = B.room_number
	AND
	(A.sleephabits <> B.sleephabits
	OR
	A.musictype <> B.musictype
	OR
	(A.smoking = 'Yes' AND B.smoking = 'no')
	OR
	(A.smoking = 'no' AND B.smoking = 'Yes'))
	AND
	A.stuid > B.stuid
ORDER BY A.lname;

Prompt 9.;
SELECT fname, lname 
FROM Student 
WHERE stuid IN
	((SELECT stuid FROM Has_allergy WHERE allergyname IN 
		(SELECT allergyname FROM Allergy WHERE allergytype = 'Environmental'))
	INTERSECT
	(SELECT stuid FROM Preferences WHERE smoking = 'Yes'))
ORDER BY lname;

Prompt 10.;
SELECT Student.fname, Student.lname, age, Department.dname AS major, Faculty.fname, Faculty.lname
FROM Student NATURAL INNER JOIN Enrolled_in NATURAL INNER JOIN Course, Faculty, Department
WHERE
	Student.advisor = Course.instructor
	AND
	Course.instructor = Faculty.facid
	AND
	Student.major = Department.dno
ORDER BY Student.lname;

Prompt 11. There happens to be 2 duplicate entries in the Enrolled_in relation, which i decided to eliminate.;
Prompt If they are not eliminated, the total enrollment for Engineering is higher by 2.;
SELECT division, COUNT(*)
FROM (SELECT DISTINCT * FROM Course NATURAL INNER JOIN Enrolled_in) NATURAL INNER JOIN Department 
GROUP BY division
ORDER BY division;

Prompt 12.;
SELECT fname, COUNT(*)
FROM Faculty 
GROUP BY fname 
HAVING COUNT(*) > 1
ORDER BY COUNT(*) DESC;

Prompt 13.;
WITH First_names AS
	(SELECT fname, COUNT(*) AS count
	FROM ((SELECT fname FROM Faculty) UNION ALL (SELECT fname FROM Student))
	GROUP BY fname)
SELECT fname, count 
FROM First_names 
WHERE count >= ALL(SELECT count FROM First_names)
ORDER BY fname;

Prompt 14. I assumed that if no one is enrolled in any course in a given department, that department should get 0 enrollment.;
Prompt If that assumption is incorrect, then the department with the "lowest" enrollment is ECE with 14 students.;
WITH Department_enrollment AS
	(SELECT dname, COUNT(grade) AS enrollment
	FROM (SELECT DISTINCT * FROM course NATURAL INNER JOIN Enrolled_in) NATURAL FULL OUTER JOIN Department 
	GROUP BY dname)
SELECT dname, enrollment 
FROM Department_enrollment
WHERE enrollment <= ALL(SELECT enrollment FROM Department_enrollment)
ORDER BY dname;

Prompt 15.;
WITH Freaks AS
	(SELECT stuid FROM
		(SELECT stuid FROM Student NATURAL INNER JOIN Preferences 
			WHERE smoking like 'no%' AND sleephabits = 'EarlyRiser')
		MINUS
		(SELECT stuid FROM Has_allergy))
SELECT Student.fname, Student.lname, Course.cname, Faculty.fname, Faculty.lname, Enrolled_in.grade
FROM Freaks NATURAL INNER JOIN Student NATURAL INNER JOIN Enrolled_in NATURAL INNER JOIN Course, Faculty
WHERE
	Course.instructor = Faculty.facid
ORDER BY Student.lname;

Prompt 16.;
SELECT fname, lname, age
FROM
	(SELECT fname, lname, age FROM Student WHERE age >= all(SELECT age FROM Student))
	UNION ALL
	(SELECT fname, lname, age FROM Student WHERE age <= all(SELECT age FROM Student))
ORDER BY age DESC;

Prompt 17. With the elimination of duplicate entries in the Enrolled_in relation, the number of A's is reduced by 1,;
Prompt and the total enrollment reduced by 2.;
WITH Dept_info AS
	(SELECT dno, A, total FROM
		(SELECT dno, count(grade) AS A
		FROM (SELECT DISTINCT * FROM course NATURAL INNER JOIN Enrolled_in) NATURAL FULL OUTER JOIN Department
		WHERE grade like 'A%'
		GROUP BY dno)
		NATURAL FULL OUTER JOIN
		(SELECT dno, count(grade) AS total
		FROM (SELECT DISTINCT * FROM course NATURAL INNER JOIN Enrolled_in) NATURAL FULL OUTER JOIN Department
		GROUP BY dno))
SELECT A, total, A / total * 100, dname, fname, lname
FROM (Dept_info NATURAL FULL OUTER JOIN Department NATURAL FULL OUTER JOIN Chaired_by) LEFT OUTER JOIN Faculty ON Chaired_by.facid = Faculty.facid
ORDER BY dname;

Prompt 18. I chose not to display duplicate entries in the result relation here.;
WITH Student_class AS
	(SELECT fname, lname, CID FROM Enrolled_in NATURAL INNER JOIN Student)
SELECT DISTINCT A.fname, A.lname, B.fname, B.lname 
FROM Student_class A, Student_class B
WHERE
	A.fname = B.fname
	AND
	A.CID = B.CID
	AND
	A.lname > B.lname
ORDER BY A.lname;
