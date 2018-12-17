drop table CUSTOMERORDERTAXLINE;
drop table CUSTOMERORDERSESERVICE;
drop table CUSTOMERORDERSERVICE;
drop table CUSTOMERORDERSEGOODS;
drop table CUSTOMERORDERGOODS;
drop table CUSTOMERORDER;
drop table CARTTAXLINE;
drop table CARTITEM;
drop table SHOPPINGCART;
drop table SEGOODS;
drop table SESERVICE;
alter table PURCHASERETURN add column FOREIGNSUBTOTAL real default 0;
alter table PURCHASERETURN add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASERETURN add column FOREIGNTOTAL real default 0;
alter table PURCHASERETURNLINE add column FOREIGNSUBTOTAL real default 0;
alter table PURCHASERETURNLINE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASERETURNLINE add column FOREIGNTOTAL real default 0;
alter table PURCHASERETURNGOODSTAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASERETURNTAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table PURCHASERETURNTAXLINE add column TAXABLEINVBAS real default 0;
alter table PURCHASERETURNTAXLINE add column TAXABLEINVBASFC real default 0;
alter table SALESRETURN add column PRICEINCTAX integer not null default 0;
alter table SALESRETURN add column FOREIGNCURRENCY integer default null references CURRENCY(ITSID);
alter table SALESRETURN add column EXCHANGERATE real default 0;
alter table SALESRETURN add column FOREIGNSUBTOTAL real default 0;
alter table SALESRETURN add column FOREIGNTOTALTAXES real default 0;
alter table SALESRETURN add column FOREIGNTOTAL real default 0;
alter table SALESRETURNLINE add column FOREIGNPRICE real default 0;
alter table SALESRETURNLINE add column FOREIGNSUBTOTAL real default 0;
alter table SALESRETURNLINE add column FOREIGNTOTALTAXES real default 0;
alter table SALESRETURNLINE add column FOREIGNTOTAL real default 0;
alter table SALESRETURNGOODSTAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table SALESRETURNTAXLINE add column FOREIGNTOTALTAXES real default 0;
alter table SALESRETURNTAXLINE add column TAXABLEINVBASFC real default 0;
alter table GOODSINLISTLUV add column SESERVICESPECIFICLUV integer default null;
alter table GOODSINLISTLUV add column SESERVICEPRICELUV integer default null;
alter table GOODSINLISTLUV add column SESERVICEPLACELUV integer default null;
alter table GOODSINLISTLUV add column SESERVICERATINGLUV integer default null;
alter table GOODSINLISTLUV add column SEGOODSPECIFICLUV integer default null;
alter table GOODSINLISTLUV add column SEGOODPRICELUV integer default null;
alter table GOODSINLISTLUV add column SEGOODPLACELUV integer default null;
alter table GOODSINLISTLUV add column SEGOODRATINGLUV integer default null;
alter table SESERVICEPRICE add column UNSTEP real default 1;
alter table SEGOODSPRICE add column UNSTEP real default 1;
alter table SERVICEPRICE add column UNSTEP real default 1;
alter table PRICEGOODS add column UNSTEP real default 1;
alter table ITEMINLIST add column UNSTEP real default 1;
alter table TAXDESTINATION add column REGZIP text;
alter table ONLINEBUYER add column TIN text;
alter table ONLINEBUYER add column FOREIG integer not null default 0;
alter table ONLINEBUYER add column TAXDEST integer default null references TAXDESTINATION(ITSID);
alter table TRADINGSETTINGS add column TXEXCL integer not null default 0;
alter table TRADINGSETTINGS add column OVERSEAS integer not null default 0;
alter table TRADINGSETTINGS add column TXDESTS integer not null default 0;
alter table TRADINGSETTINGS add column ONLYDELIV integer default null;
update DATABASEINFO set DATABASEVERSION=8, DESCRIPTION='Beige Accounting OIO DB version 8';
