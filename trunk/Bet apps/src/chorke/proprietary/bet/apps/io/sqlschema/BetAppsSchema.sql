CREATE DATABASE BetApp;

--matches
CREATE TABLE matches
(
  id serial NOT NULL,
  sport character varying(20),
  country character varying(50),
  league character varying(50),
  matchdate timestamp without time zone,
  CONSTRAINT id PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE matches
  OWNER TO "Juraj Durani";

--scores
CREATE TABLE scores
(
  matchid integer NOT NULL,
  team1 integer,
  team2 integer,
  part integer NOT NULL,
  CONSTRAINT "scoreID" PRIMARY KEY (matchid, part)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE scores
  OWNER TO "Juraj Durani";

--bet 1x2
CREATE TABLE bet1x2
(
  matchid integer NOT NULL,
  betcompany character varying(50) NOT NULL,
  bet1 double precision,
  betx double precision,
  bet2 double precision,
  CONSTRAINT "bet1x2ID" PRIMARY KEY (matchid, betcompany)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE bet1x2
  OWNER TO "Juraj Durani";

--bet asian handicap
CREATE TABLE betah
(
  matchid integer NOT NULL,
  betcompany character varying(50) NOT NULL,
  bet1 double precision,
  bet2 double precision,
  handicap double precision NOT NULL,
  description character varying(20) NOT NULL,
  CONSTRAINT "betAHID" PRIMARY KEY (matchid, betcompany, handicap, description)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE betah
  OWNER TO "Juraj Durani";

--bet both teams to score
CREATE TABLE betbtts
(
  matchid integer NOT NULL,
  betcompany character varying(50) NOT NULL,
  yesbet double precision,
  nobet double precision,
  CONSTRAINT "betBTTSID" PRIMARY KEY (matchid, betcompany)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE betbtts
  OWNER TO "Juraj Durani";

-- bet double chance
CREATE TABLE betdc
(
  matchid integer NOT NULL,
  betcompany character varying(50) NOT NULL,
  bet1x double precision,
  bet2x double precision,
  bet12 double precision,
  CONSTRAINT "betDCID" PRIMARY KEY (matchid, betcompany)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE betdc
  OWNER TO "Juraj Durani";

--bet draw no bet
CREATE TABLE betdnb
(
  matchid integer NOT NULL,
  betcompany character varying(50) NOT NULL,
  bet1 double precision,
  bet2 double precision,
  CONSTRAINT "betDNBID" PRIMARY KEY (matchid, betcompany)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE betdnb
  OWNER TO "Juraj Durani";

-- bet over under
CREATE TABLE betou
(
  matchid integer NOT NULL,
  betcompany character varying(50) NOT NULL,
  total double precision NOT NULL,
  overbet double precision,
  underbet double precision,
  description character varying(50) NOT NULL,
  CONSTRAINT "betOUID" PRIMARY KEY (matchid, betcompany, total, description)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE betou
  OWNER TO "Juraj Durani";