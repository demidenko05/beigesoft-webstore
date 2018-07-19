alter table SETTINGSADD add column SPECSEPARATOR text not null default ',';
alter table SETTINGSADD add column SPECGRSEPARATOR text default null;
alter table SETTINGSADD add column SPECHTMLSTART text default null;
alter table SETTINGSADD add column SPECHTMLEND text default null;
alter table SETTINGSADD add column SPECGRHTMLSTART text default null;
alter table SETTINGSADD add column SPECGRHTMLEND text default null;
alter table GOODSINLISTLUV add column SERVICESPECIFICLUV integer default null;
alter table GOODSINLISTLUV add column SERVICEPRICELUV integer default null;
alter table GOODSINLISTLUV add column SERVICEPLACELUV integer default null;
alter table GOODSINLISTLUV add column SERVICERATINGLUV integer default null;
alter table SPECIFICSOFITEM add column TEMPHTML integer default null references HTMLTEMPLATE(ITSID);
alter table ITEMINLIST add column UNITOFMEASURE integer default null references UNITOFMEASURE(ITSID);
alter table I18NSPECIFICINLIST add column ITSNAME text;
alter table CATALOGGS add column HASGOODS integer not null default 0;
alter table CATALOGGS add column HASSERVICES integer not null default 0;
alter table CATALOGGS add column HASSEGOODS integer not null default 0;
alter table CATALOGGS add column HASSESERVICES integer not null default 0;
alter table SALESINVOICE add column FOREIGNCURRENCY integer default null references CURRENCY(ITSID);
alter table SALESINVOICE add column EXCHANGERATE real default 0;
alter table SALESINVOICE add column FOREIGNSUBTOTAL real default 0;
alter table SALESINVOICE add column FOREIGNTOTALTAXES real default 0;
alter table SALESINVOICE add column FOREIGNTOTAL real default 0;
alter table SALESINVOICELINE add column FOREIGNPRICE real default 0;
alter table SALESINVOICELINE add column FOREIGNTOTALTAXES real default 0;
alter table SALESINVOICELINE add column FOREIGNSUBTOTAL real default 0;
alter table SALESINVOICELINE add column FOREIGNTOTAL real default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNPRICE real default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNTOTALTAXES real default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNSUBTOTAL real default 0;
alter table SALESINVOICESERVICELINE add column FOREIGNTOTAL real default 0;
alter table SALESINVOICEGOODSTAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table SALESINVOICESERVICETAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table SALESINVOICETAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table SALESINVOICETAXLINE add column TAXABLEINVBAS real default 0;
alter table SALESINVOICETAXLINE add column TAXABLEINVBASFC real default 0;
alter table PAYMENTFROM add column FOREIGNTOTAL real default 0;
alter table PREPAYMENTFROM add column FOREIGNTOTAL real default 0;
alter table PURCHASEINVOICE add column FOREIGNCURRENCY integer default null references CURRENCY(ITSID);
alter table PURCHASEINVOICE add column EXCHANGERATE real default 0;
alter table PURCHASEINVOICE add column FOREIGNSUBTOTAL real default 0;
alter table PURCHASEINVOICE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASEINVOICE add column FOREIGNTOTAL real default 0;
alter table PURCHASEINVOICELINE add column FOREIGNPRICE real default 0;
alter table PURCHASEINVOICELINE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASEINVOICELINE add column FOREIGNSUBTOTAL real default 0;
alter table PURCHASEINVOICELINE add column FOREIGNTOTAL real default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNPRICE real default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNSUBTOTAL real default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNTOTAL real default 0;
alter table PURCHASEINVOICEGOODSTAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASEINVOICESERVICETAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASEINVOICETAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASEINVOICETAXLINE add column TAXABLEINVBAS real default 0;
alter table PURCHASEINVOICETAXLINE add column TAXABLEINVBASFC real default 0;
alter table PAYMENTTO add column FOREIGNTOTAL real default 0;
alter table PREPAYMENTTO add column FOREIGNTOTAL real default 0;drop table GOODSSPECIFIC;
alter table ACCSETTINGS add column SALTAXISINVOICEBASE integer not null default 0;
alter table ACCSETTINGS add column SALTAXRIPDECPL integer not null default 0;
alter table ACCSETTINGS add column SALTAXROUNDMODE integer not null default 4;
drop table GOODSCATALOGS;
drop table GOODSPRICE;
drop table GOODSAVAILABLE;
drop table SESELLER;
update GOODSINLISTLUV set GOODSAVAILABLELUV=null;
update DATABASEINFO set DATABASEVERSION=7, DESCRIPTION='Beige Accounting OIO DB version 7';
