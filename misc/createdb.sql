create table topic 
(
id integer primary key,
name text,
topictype text,
url text
);

create table signature(
id integer primary key,
signature text,
topictype text
);

create table remotecomment(
  id integer primary key,
  author text,
  subject text,
  comment text,
  time date
);

create table comment (
	id integer primary key,
	time date,
	topicid integer,
	comment text,
	signatureid integer,
	inreplytoremotecommentid integer
);

create table template(
id integer primary key,
template text
);
