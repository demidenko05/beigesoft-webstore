alter table DEBTORCREDITOR add column ISFOREIGNER tinyint not null default 0;
alter table DEBTORCREDITOR add column NATIVENAME varchar(255) default null;
alter table TRADINGSETTINGS add column DEFAULTPAYMENTMETHOD tinyint not null default 0;
alter table TRADINGSETTINGS add column USEADVANCEDI18N tinyint not null default 0;
alter table ACCSETTINGS add column USECURRENCYSIGN tinyint not null default 0;
alter table SPECIFICSOFITEM add column CHOOSEABLESPECIFICSTYPE bigint unsigned default null;
alter table SPECIFICSOFITEM add FOREIGN KEY (CHOOSEABLESPECIFICSTYPE) REFERENCES CHOOSEABLESPECIFICSTYPE(ITSID);
alter table CHOOSEABLESPECIFICSTYPE add column HTMLTEMPLATE bigint unsigned default null;
alter table CHOOSEABLESPECIFICSTYPE add FOREIGN KEY (HTMLTEMPLATE) REFERENCES HTMLTEMPLATE(ITSID);
alter table SPECIFICSOFITEMGROUP add column TEMPLATESTART bigint unsigned default null;
alter table SPECIFICSOFITEMGROUP add FOREIGN KEY (TEMPLATESTART) REFERENCES HTMLTEMPLATE(ITSID);
alter table SPECIFICSOFITEMGROUP add column TEMPLATEEND bigint unsigned default null;
alter table SPECIFICSOFITEMGROUP add FOREIGN KEY (TEMPLATEEND) REFERENCES HTMLTEMPLATE(ITSID);
alter table SPECIFICSOFITEMGROUP add column TEMPLATEDETAIL bigint unsigned default null;
alter table SPECIFICSOFITEMGROUP add FOREIGN KEY (TEMPLATEDETAIL) REFERENCES HTMLTEMPLATE(ITSID);
alter table PICKUPPLACE add column LATITUDE decimal(2,6) default null;
alter table PICKUPPLACE add column LONGITUDE decimal(3,6) default null;
alter table ITEMINLIST add column DETAILSMETHOD int default null;
alter table GOODSSPECIFIC add column STRINGVALUE3 varchar(255) default null;
alter table GOODSSPECIFIC add column STRINGVALUE4 varchar(255) default null;
alter table SPECIFICSOFITEM add column USEFORORDERING tinyint not null default 0;
alter table PICKUPPLACE add column TIMEZONE int default null;
alter table SETTINGSADD add column MINIMUMLISTSIZEFORORDERING int not null default 20;
alter table CATALOGGS add column USEFILTERSPECIFICS tinyint not null default 0;
alter table CATALOGGS add column USEFILTERSUBCATALOG tinyint not null default 0;
alter table CATALOGGS add column USEPICKUPPLACEFILTER tinyint not null default 0;
alter table CATALOGGS add column USEAVAILABLEFILTER tinyint not null default 0;
alter table CATALOGGS add column FILTERPRICEID int default null;
alter table CURRENCY add column ITSSIGN varchar(6) default null;
update CURRENCY set ITSSIGN='€', ITSVERSION=(ITSVERSION+1) where ITSID=978;
update CURRENCY set ITSSIGN='$', ITSVERSION=(ITSVERSION+1) where ITSID=840;
update CURRENCY set ITSSIGN='₽', ITSVERSION=(ITSVERSION+1) where ITSID=643;
insert into LANGUAGES (ITSID, ITSNAME, ITSVERSION) values ('ru', 'Русский', 1462867931627);
insert into LANGUAGES (ITSID, ITSNAME, ITSVERSION) values ('en', 'English', 1462867931627);
insert into COUNTRIES (ITSID, ITSNAME, ITSVERSION) values ('US', 'USA', 1462867931627);
insert into COUNTRIES (ITSID, ITSNAME, ITSVERSION) values ('RU', 'РФ', 1462867931627);
insert into DECIMALSEPARATOR (ITSID, ITSNAME, ITSVERSION) values (',', 'comma', 1462867931627);
insert into DECIMALSEPARATOR (ITSID, ITSNAME, ITSVERSION) values ('.', 'dot', 1462867931627);
insert into DECIMALGROUPSEPARATOR (ITSID, ITSNAME, ITSVERSION) values (',', 'comma', 1462867931627);
insert into DECIMALGROUPSEPARATOR (ITSID, ITSNAME, ITSVERSION) values ('space', 'space', 1462867931627);
insert into LANGPREFERENCES (DECIMALGROUPSEP, LANG, COUNTRY, DECIMALSEP, ISDEFAULT, ITSVERSION, DIGITSINGROUP) values (',', 'en', 'US', '.', 1, 1462867931627, 3);
insert into LANGPREFERENCES (DECIMALGROUPSEP, LANG, COUNTRY, DECIMALSEP, ISDEFAULT, ITSVERSION, DIGITSINGROUP) values ('space', 'ru', 'RU', ',', 0, 1462867931627, 3);
update DATABASEINFO set DATABASEVERSION=6, DESCRIPTION='Beige Accounting OIO DB version 6';
