alter table DEBTORCREDITOR add column ISFOREIGNER integer not null default 0;
alter table TRADINGSETTINGS add column DEFAULTPAYMENTMETHOD integer not null default 0;
alter table SPECIFICSOFITEM add column CHOOSEABLESPECIFICSTYPE bigint default null;
alter table SPECIFICSOFITEM add constraint fkspecofitchsptp FOREIGN KEY (CHOOSEABLESPECIFICSTYPE) REFERENCES CHOOSEABLESPECIFICSTYPE(ITSID);
alter table CHOOSEABLESPECIFICSTYPE add column HTMLTEMPLATE bigint default null;
alter table CHOOSEABLESPECIFICSTYPE add constraint fkchspectyphtmltmp FOREIGN KEY (HTMLTEMPLATE) REFERENCES HTMLTEMPLATE(ITSID);
update DATABASEINFO set DATABASEVERSION=6, DESCRIPTION='Beige Accounting OIO DB version 6';
