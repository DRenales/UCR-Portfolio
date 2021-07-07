

--------------
---DROPPING---
--------------
DROP TABLE IF EXISTS State CASCADE;--OK
DROP TABLE IF EXISTS Study CASCADE;--OK

------------
---TABLES---
------------

CREATE TABLE State
(
	state_abbr CHAR(2) NOT NULL,
	name CHAR(25) NOT NULL,
	state_fips CHAR(2) NOT NULL,
	fema_region CHAR(25)
);


CREATE TABLE Study
(
	overall_outcome CHAR(15) NOT NULL,
	date_of_study DATE NOT NULL,
	new_results INTEGER NOT NULL,
	total_results INTEGER NOT NULL,
	sName CHAR(25) NOT NULL
);


----------------------------
-- INSERT DATA STATEMENTS --
----------------------------

COPY State (
	state_abbr,
	name,
	state_fips,
	fema_region
)
FROM 'state.csv'
WITH DELIMITER ',';

COPY Study (
	overall_outcome,
	date_of_study,
	new_results,
	total_results,
	sName
)
FROM 'study.csv'
WITH DELIMITER ',';
