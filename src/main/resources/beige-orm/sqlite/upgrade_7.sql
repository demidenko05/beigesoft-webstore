alter table SETTINGSADD add column SPECSEPARATOR text not null default ',';
alter table SETTINGSADD add column SPECGRSEPARATOR text default null;
alter table SETTINGSADD add column SPECHTMLSTART text default null;
alter table SETTINGSADD add column SPECHTMLEND text default null;
alter table SETTINGSADD add column SPECGRHTMLSTART text default null;
alter table SETTINGSADD add column SPECGRHTMLEND text default null;
alter table SPECIFICSOFITEM add column TEMPHTML integer default null references HTMLTEMPLATE(ITSID);
update DATABASEINFO set DATABASEVERSION=7, DESCRIPTION='Beige Accounting OIO DB version 7';
