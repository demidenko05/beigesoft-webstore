alter table PURCHASERETURN add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table PURCHASERETURN add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASERETURN add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PURCHASERETURNLINE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table PURCHASERETURNLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASERETURNLINE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PURCHASERETURNGOODSTAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASERETURNTAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASERETURNTAXLINE add column TAXABLEINVBASFC decimal(19,4) default 0;
alter table SALESRETURN add column PRICEINCTAX tinyint not null default 0;
alter table SALESRETURN add column FOREIGNCURRENCY bigint default null;
alter table SALESRETURN add constraint fksalretforcurrn FOREIGN KEY (FOREIGNCURRENCY) REFERENCES CURRENCY(ITSID);
alter table SALESRETURN add column EXCHANGERATE decimal(19,4) default 0;
alter table SALESRETURN add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table SALESRETURN add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESRETURN add column FOREIGNTOTAL decimal(19,4) default 0;
alter table SALESRETURNLINE add column FOREIGNPRICE decimal(19,4) default 0;
alter table SALESRETURNLINE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table SALESRETURNLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESRETURNLINE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table SALESRETURNGOODSTAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESRETURNTAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table SALESRETURNTAXLINE add column TAXABLEINVBASFC decimal(19,4) default 0;
alter table WAREHOUSEREST add column ITSVERSION bigint not null default 1;
alter table SERVICETOSALE add column TMME tinyint not null default 0;
alter table SERVICETOSALE add column TMAD int default null;
alter table SESERVICE add column TMME tinyint not null default 0;
alter table SESERVICE add column TMAD integer default null;
alter table SESERVICE add column TAXCATEGORY bigint default null;
alter table SESERVICE add constraint fkseservtcat FOREIGN KEY (TAXCATEGORY) references INVITEMTAXCATEGORY(ITSID);
alter table SESERVICE add column DEFUNITOFMEASURE bigint default null;
alter table SESERVICE add constraint fkseservuom FOREIGN KEY (DEFUNITOFMEASURE) REFERENCES UNITOFMEASURE(ITSID);
alter table SEGOODS add column TAXCATEGORY bigint default null;
alter table SEGOODS add constraint fksegoodtcat FOREIGN KEY (TAXCATEGORY) references INVITEMTAXCATEGORY(ITSID);
alter table SEGOODS add column DEFUNITOFMEASURE bigint default null;
alter table SEGOODS add constraint fksegooduom FOREIGN KEY (DEFUNITOFMEASURE) REFERENCES UNITOFMEASURE(ITSID);
alter table SETTINGSADD add column DAOF int not null default 365;
alter table SETTINGSADD add column BKTR int not null default 1;
alter table SETTINGSADD add column OPMD int not null default 0;
alter table SETTINGSADD add column ONLMD bigint default 0;
alter table GOODSINLISTLUV add column SESERVICESPECIFICLUV bigint default null;
alter table GOODSINLISTLUV add column SESERVICEPRICELUV bigint default null;
alter table GOODSINLISTLUV add column SESERVICEPLACELUV bigint default null;
alter table GOODSINLISTLUV add column SESERVICERATINGLUV bigint default null;
alter table GOODSINLISTLUV add column SEGOODSPECIFICLUV bigint default null;
alter table GOODSINLISTLUV add column SEGOODPRICELUV bigint default null;
alter table GOODSINLISTLUV add column SEGOODPLACELUV bigint default null;
alter table GOODSINLISTLUV add column SEGOODRATINGLUV bigint default null;
alter table SESERVICEPRICE add column UNSTEP decimal(9,4) default 1;
alter table SEGOODSPRICE add column UNSTEP decimal(9,4) default 1;
alter table SERVICEPRICE add column UNSTEP decimal(9,4) default 1;
alter table PRICEGOODS add column UNSTEP decimal(9,4) default 1;
alter table ITEMINLIST add column UNSTEP decimal(9,4) default 1;
alter table TAXDESTINATION add column REGZIP varchar(10);
alter table ONLINEBUYER add column BUSEID varchar(36);
alter table ONLINEBUYER add column TIN varchar(10);
alter table CURRENCY add column STCO varchar(5);
alter table ONLINEBUYER add column FOREIG tinyint not null default 0;
alter table ONLINEBUYER add column FRE tinyint not null default 0;
alter table ONLINEBUYER add column LSTM bigint default 0;
alter table ONLINEBUYER add column TAXDEST bigint default null;
alter table ONLINEBUYER add constraint fkonlbtxdest FOREIGN KEY (TAXDEST) references TAXDESTINATION(ITSID);
alter table TRADINGSETTINGS add column TXEXCL tinyint not null default 0;
alter table TRADINGSETTINGS add column TXDESTS tinyint not null default 0;
alter table SERVICEPLACE add column ISALWAYS tinyint not null default 0;
alter table SESERVICEPLACE add column ISALWAYS tinyint not null default 0;
alter table SEGOODSPLACE add column ISALWAYS tinyint not null default 0;
drop table CUSTOMERORDERTAXLINE;
drop table CUSTOMERORDERSESERVICE;
drop table CUSTOMERORDERSERVICE;
drop table CUSTOMERORDERSEGOODS;
drop table CUSTOMERORDERGOODS;
drop table CUSTOMERORDER;
drop table CARTTAXLINE;
drop table CARTITEM;
drop table SHOPPINGCART;
update DATABASEINFO set DATABASEVERSION=8, DESCRIPTION='Beige Accounting OIO DB version 8';
