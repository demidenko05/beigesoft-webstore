alter table DEBTORCREDITOR add column ISFOREIGNER integer not null default 0;
alter table TRADINGSETTINGS add column DEFAULTPAYMENTMETHOD integer not null default 0;
alter table SPECIFICSOFITEM add column CHOOSEABLESPECIFICSTYPE integer default null references CHOOSEABLESPECIFICSTYPE(ITSID);
alter table CHOOSEABLESPECIFICSTYPE add column HTMLTEMPLATE integer default null references HTMLTEMPLATE(ITSID);
alter table SPECIFICSOFITEMGROUP add column TEMPLATESTART integer default null references HTMLTEMPLATE(ITSID);
alter table SPECIFICSOFITEMGROUP add column TEMPLATEEND integer default null references HTMLTEMPLATE(ITSID);
alter table SPECIFICSOFITEMGROUP add column TEMPLATEDETAIL integer default null references HTMLTEMPLATE(ITSID);
update DATABASEINFO set DATABASEVERSION=6, DESCRIPTION='Beige Accounting OIO DB version 6';
