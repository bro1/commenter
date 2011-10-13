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
  remoteCommentId text,
  topicID integer,
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
url text, 
lastChecked date);


INSERT INTO TOPIC (id,name,topictype,url,lastChecked) VALUES (1001,'Sample topic Delfi','delfi','file://misc/examples/delfi/str1p1.html', 0);
INSERT INTO TOPIC (id,name,topictype,url,lastChecked) VALUES (1002,'Sample topic Bernardinai','bernardinai','file://misc/examples/bernardinai/bernardinainew1.html', 0);
INSERT INTO TOPIC (id,name,topictype,url,lastChecked) VALUES (1003,'Bernardinai online','bernardinai','http://www.bernardinai.lt/straipsnis/2011-10-12-valius-sruoga-priklausomybe-tai-labai-sunki-liga/70296/comments', 0);
