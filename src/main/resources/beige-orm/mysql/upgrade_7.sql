alter table SETTINGSADD add column SPECSEPARATOR varchar(50) not null default ',';
alter table SETTINGSADD add column SPECGRSEPARATOR varchar(50) default null;
alter table SETTINGSADD add column SPECHTMLSTART varchar(255) default null;
alter table SETTINGSADD add column SPECHTMLEND varchar(255) default null;
alter table SETTINGSADD add column SPECGRHTMLSTART varchar(255) default null;
alter table SETTINGSADD add column SPECGRHTMLEND varchar(255) default null;
alter table SPECIFICSOFITEM add column TEMPHTML bigint unsigned default null;
alter table SPECIFICSOFITEM add FOREIGN KEY (TEMPHTML) REFERENCES HTMLTEMPLATE(ITSID);
update DATABASEINFO set DATABASEVERSION=7, DESCRIPTION='Beige Accounting OIO DB version 7';
