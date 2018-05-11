alter table DEBTORCREDITOR add column ISFOREIGNER integer not null default 0;
alter table TRADINGSETTINGS add column DEFAULTPAYMENTMETHOD integer not null default 0;
alter table SPECIFICSOFITEM add column CHOOSEABLESPECIFICSTYPE bigint default null;
alter table SPECIFICSOFITEM add constraint fkspecofitchsptp FOREIGN KEY (CHOOSEABLESPECIFICSTYPE) REFERENCES CHOOSEABLESPECIFICSTYPE(ITSID);
alter table CHOOSEABLESPECIFICSTYPE add column HTMLTEMPLATE bigint default null;
alter table CHOOSEABLESPECIFICSTYPE add constraint fkchspectyphtmltmp FOREIGN KEY (HTMLTEMPLATE) REFERENCES HTMLTEMPLATE(ITSID);
alter table SPECIFICSOFITEMGROUP add column TEMPLATESTART bigint default null;
alter table SPECIFICSOFITEMGROUP add constraint fkspoigrtmpst FOREIGN KEY (TEMPLATESTART) REFERENCES HTMLTEMPLATE(ITSID);
alter table SPECIFICSOFITEMGROUP add column TEMPLATEEND bigint default null;
alter table SPECIFICSOFITEMGROUP add constraint fkspoigrtmpend FOREIGN KEY (TEMPLATEEND) REFERENCES HTMLTEMPLATE(ITSID);
alter table SPECIFICSOFITEMGROUP add column TEMPLATEDETAIL bigint default null;
alter table SPECIFICSOFITEMGROUP add constraint fkspoigrtmdet FOREIGN KEY (TEMPLATEDETAIL) REFERENCES HTMLTEMPLATE(ITSID);
alter table PICKUPPLACE add column LATITUDE decimal(2,6) default null;
alter table PICKUPPLACE add column LONGITUDE decimal(3,6) default null;
alter table ITEMINLIST add column DETAILSMETHOD integer default null;
alter table GOODSSPECIFIC add column STRINGVALUE3 varchar(255) default null;
alter table GOODSSPECIFIC add column STRINGVALUE4 varchar(255) default null;
alter table SPECIFICSOFITEM add column USEFORORDERING integer not null default 0;
alter table PICKUPPLACE add column TIMEZONE integer default null;
alter table SETTINGSADD add column MINIMUMLISTSIZEFORORDERING integer not null default 20;
alter table CATALOGGS add column USEFILTERSPECIFICS integer not null default 0;
alter table CATALOGGS add column USEFILTERSUBCATALOG integer not null default 0;
alter table CATALOGGS add column USEPICKUPPLACEFILTER integer not null default 0;
alter table CATALOGGS add column USEAVAILABLEFILTER integer not null default 0;
update DATABASEINFO set DATABASEVERSION=6, DESCRIPTION='Beige Accounting OIO DB version 6';