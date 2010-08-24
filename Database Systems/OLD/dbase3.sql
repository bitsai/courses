CREATE OR REPLACE VIEW TotalPoints AS
	SELECT *
	FROM RawScores
	WHERE ssn = 0001;
	
CREATE OR REPLACE VIEW Weights AS
	SELECT *
	FROM RawScores
	WHERE ssn = 0002;
	
CREATE OR REPLACE VIEW Percentages AS
	SELECT RawScores.ssn ssn, RawScores.lname lname, RawScores.fname fname, RawScores.section section, 
	ROUND(RawScores.hw1 / TotalPoints.hw1 * 100, 2) hw1, ROUND(RawScores.hw2a / TotalPoints.hw2a * 100, 2) hw2a, 
	ROUND(RawScores.hw2b / TotalPoints.hw2b * 100, 2) hw2b, ROUND(RawScores.midterm / TotalPoints.midterm * 100, 2) midterm, 
	ROUND(RawScores.hw3 / TotalPoints.hw3 * 100, 2) hw3, ROUND(RawScores.fexam / TotalPoints.fexam * 100, 2) fexam
	FROM RawScores, TotalPoints;
	
CREATE OR REPLACE VIEW CumAvgs AS
	SELECT Percentages.ssn ssn, Percentages.lname lname, Percentages.fname fname, Percentages.section section,
	ROUND(Percentages.hw1 * Weights.hw1 + Percentages.hw2a * Weights.hw2a +
	Percentages.hw2b * Weights.hw2b + Percentages.midterm * Weights.midterm + 
	Percentages.hw3 * Weights.hw3 + Percentages.fexam * Weights.fexam, 2) cumavg
	FROM Percentages, Weights;

CREATE OR REPLACE PROCEDURE ShowRawScores(searchssn IN varchar2)
AS
	output boolean;
BEGIN
	output := owa_util.tablePrint('RawScores', 'BORDER', owa_util.HTML_TABLE, '*', 'WHERE ssn = ' || searchssn);
	
	htp.anchor('http://dbase.cs.jhu.edu:8080/~btsai/Index.html', 'Back to Menu');
END;
/

CREATE OR REPLACE PROCEDURE ShowPercentages(searchssn IN varchar2)
AS
	output boolean;
	
	match CumAvgs%ROWTYPE;
	CURSOR cumavg_match IS
		SELECT *
		FROM CumAvgs
		WHERE ssn = searchssn;
BEGIN
	output := owa_util.tablePrint('Percentages', 'BORDER', owa_util.HTML_TABLE, '*', 'WHERE ssn = ' || searchssn);

	OPEN cumavg_match;
	FETCH cumavg_match INTO match;
	htp.print('The cumulative course average for ' || match.fname || ' ' || match.lname || ' is ' || match.cumavg || '%');
	CLOSE cumavg_match;
	
	htp.anchor('http://dbase.cs.jhu.edu:8080/~btsai/Index.html', 'Back to Menu');
END;
/

CREATE OR REPLACE PROCEDURE AllRawScores(userpassword IN varchar2)
AS
	output boolean;
	pass boolean := FALSE;

	password Passwords%ROWTYPE;
	CURSOR password_valid IS
		SELECT *
		FROM Passwords;
BEGIN
	OPEN password_valid;
	FETCH password_valid INTO password;
	WHILE password_valid%FOUND LOOP
		IF password.CurPasswords = userpassword
			THEN pass := TRUE;
		END IF;
		FETCH password_valid INTO password;
	END LOOP;
	CLOSE password_valid;
	
	IF pass = TRUE
		THEN output := owa_util.tablePrint('RawScores', 'BORDER', owa_util.HTML_TABLE, '*', 'WHERE ssn > 0002 ORDER BY section, lname, fname');
		ELSE htp.print('Invalid Password!');
	END IF;
	
	htp.anchor('http://dbase.cs.jhu.edu:8080/~btsai/Index.html', 'Back to Menu');
END;
/

CREATE OR REPLACE PROCEDURE AllPercentages(userpassword IN varchar2)
AS
	output boolean;
	pass boolean := FALSE;
	
	password Passwords%ROWTYPE;
	CURSOR password_valid IS
		SELECT *
		FROM Passwords;
BEGIN
	OPEN password_valid;
	FETCH password_valid INTO password;
	WHILE password_valid%FOUND LOOP
		IF password.CurPasswords = userpassword
			THEN pass := TRUE;
		END IF;
		FETCH password_valid INTO password;
	END LOOP;
	CLOSE password_valid;
	
	IF pass = TRUE
		THEN output := owa_util.tablePrint('Percentages NATURAL INNER JOIN CumAvgs', 'BORDER', owa_util.HTML_TABLE, 'ssn, lname, fname, section, hw1, hw2a, hw2b, midterm, hw3, fexam, cumavg', 'WHERE ssn > 0002 ORDER BY section, cumavg');
		ELSE htp.print('Invalid Password!');
	END IF;
	
	htp.anchor('http://dbase.cs.jhu.edu:8080/~btsai/Index.html', 'Back to Menu');
END;
/

GRANT EXECUTE ON ShowRawScores TO PUBLIC;

GRANT EXECUTE ON ShowPercentages TO PUBLIC;

GRANT EXECUTE ON AllRawScores TO PUBLIC;

GRANT EXECUTE ON AllPercentages TO PUBLIC;
