CREATE TABLE comment (
	id integer primary key,
	time date,
	topicid integer,
	comment text,
	signatureid integer,
	inreplytoremotecommentid integer
, author string, remotecommentid string);
CREATE TABLE remotecomment(
  id integer primary key,
  author text,
  subject text,
  comment text,
  time date
);
CREATE TABLE signature(
id integer primary key,
signature text,
topictype text
);
CREATE TABLE template(
id integer primary key,
template text
);
CREATE TABLE topic 
(
id integer primary key,
name text,
topictype text,
url text
, lastChecked date);
