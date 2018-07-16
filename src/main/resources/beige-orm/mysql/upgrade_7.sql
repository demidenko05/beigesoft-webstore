alter table SETTINGSADD add column SPECSEPARATOR varchar(50) not null default ',';
alter table SETTINGSADD add column SPECGRSEPARATOR varchar(50) default null;
alter table SETTINGSADD add column SPECHTMLSTART varchar(255) default null;
alter table SETTINGSADD add column SPECHTMLEND varchar(255) default null;
alter table SETTINGSADD add column SPECGRHTMLSTART varchar(255) default null;
alter table SETTINGSADD add column SPECGRHTMLEND varchar(255) default null;
alter table GOODSINLISTLUV add column SERVICESPECIFICLUV bigint default null;
alter table GOODSINLISTLUV add column SERVICEPRICELUV bigint default null;
alter table GOODSINLISTLUV add column SERVICEPLACELUV bigint default null;
alter table GOODSINLISTLUV add column SERVICERATINGLUV bigint default null;
alter table SPECIFICSOFITEM add column TEMPHTML bigint unsigned default null;
alter table SPECIFICSOFITEM add FOREIGN KEY (TEMPHTML) REFERENCES HTMLTEMPLATE(ITSID);
alter table ITEMINLIST add column UNITOFMEASURE bigint unsigned default null;
alter table ITEMINLIST add FOREIGN KEY (UNITOFMEASURE) REFERENCES UNITOFMEASURE(ITSID);
alter table I18NSPECIFICINLIST add column ITSNAME varchar(255);
alter table CATALOGGS add column HASGOODS tinyint not null default 0;
alter table CATALOGGS add column HASSERVICES tinyint not null default 0;
alter table CATALOGGS add column HASSEGOODS tinyint not null default 0;
alter table CATALOGGS add column HASSESERVICES tinyint not null default 0;
alter table SALESINVOICE add column FOREIGNCURRENCY bigint unsigned default null;
alter table SALESINVOICE add FOREIGN KEY (FOREIGNCURRENCY) REFERENCES CURRENCY(ITSID);
alter table SALESINVOICE add column EXCHANGERATE decimal(19,4) default 0;
alter table SALESINVOICE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table SALESINVOICE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESINVOICE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table SALESINVOICELINE add column FOREIGNPRICE decimal(19,4) default 0;
alter table SALESINVOICELINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESINVOICELINE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table SALESINVOICELINE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNPRICE decimal(19,4) default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table SALESINVOICEGOODSTAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESINVOICESERVICETAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESINVOICETAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
drop table GOODSSPECIFIC;
drop table GOODSCATALOGS;
drop table GOODSPRICE;
drop table GOODSAVAILABLE;
drop table SESELLER;
update GOODSINLISTLUV set GOODSAVAILABLELUV=null;
update DATABASEINFO set DATABASEVERSION=7, DESCRIPTION='Beige Accounting OIO DB version 7';
