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
alter table SALESINVOICETAXLINE add column TAXABLEINVBAS decimal(19,4) default 0;
alter table SALESINVOICETAXLINE add column TAXABLEINVBASFC decimal(19,4) default 0;
alter table PURCHASERETURNTAXLINE add column TAXABLEINVBAS decimal(19,4) default 0;
alter table SALESRETURNTAXLINE add column TAXABLEINVBAS decimal(19,4) default 0;
alter table PAYMENTFROM add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PREPAYMENTFROM add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PURCHASEINVOICE add column FOREIGNCURRENCY bigint unsigned default null;
alter table PURCHASEINVOICE add FOREIGN KEY (FOREIGNCURRENCY) REFERENCES CURRENCY(ITSID);
alter table PURCHASEINVOICE add column EXCHANGERATE decimal(19,4) default 0;
alter table PURCHASEINVOICE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table PURCHASEINVOICE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASEINVOICE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PURCHASEINVOICELINE add column FOREIGNPRICE decimal(19,4) default 0;
alter table PURCHASEINVOICELINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASEINVOICELINE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table PURCHASEINVOICELINE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNPRICE decimal(19,4) default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNSUBTOTAL decimal(19,4) default 0;
alter table PURCHASEINVOICESERVICELINE add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PURCHASEINVOICEGOODSTAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASEINVOICESERVICETAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASEINVOICETAXLINE add column FOREIGNTOTALTAXES decimal(19,4) default 0;
alter table PURCHASEINVOICETAXLINE add column TAXABLEINVBAS decimal(19,4) default 0;
alter table PURCHASEINVOICETAXLINE add column TAXABLEINVBASFC decimal(19,4) default 0;
alter table PAYMENTTO add column FOREIGNTOTAL decimal(19,4) default 0;
alter table PREPAYMENTTO add column FOREIGNTOTAL decimal(19,4) default 0;
alter table ACCSETTINGS add column SALTAXISINVOICEBASE tinyint not null default 0;
alter table ACCSETTINGS add column SALTAXUSEAGGREGITBAS tinyint not null default 0;
alter table ACCSETTINGS add column SALTAXROUNDMODE tinyint not null default 4;
alter table ACCSETTINGS add column TTFFILENAME varchar(100) default 'DejaVuSerif';
alter table ACCSETTINGS add column TTFBOLDFILENAME varchar(100) default 'DejaVuSerif-Bold';
alter table ACCSETTINGS add column PAGESIZE tinyint not null default 2;
alter table ACCSETTINGS add column PAGEORIENTATION tinyint not null default 0;
alter table ACCSETTINGS add column MARGINLEFT decimal(19,4) default 30;
alter table ACCSETTINGS add column MARGINRIGHT decimal(19,4) default 15;
alter table ACCSETTINGS add column MARGINTOP decimal(19,4) default 20;
alter table ACCSETTINGS add column MARGINBOTTOM decimal(19,4) default 20;
alter table ACCSETTINGS add column FONTSIZE decimal(19,4) default 3.5;
alter table ACCSETTINGS add column TAXPRECISION integer not null default 3;
alter table PURCHASEINVOICE add column PRICEINCTAX tinyint not null default 0;
alter table SALESINVOICE add column PRICEINCTAX tinyint not null default 0;
alter table DEBTORCREDITOR add column TAXDESTINATION bigint unsigned default null;
alter table DEBTORCREDITOR add FOREIGN KEY (TAXDESTINATION) references TAXDESTINATION(ITSID);
alter table INVITEMTAXCATEGORY add column AGGRONLYPERCENT decimal(19,4) default 0;
alter table PRICECATEGORY add column ISRETAILONLY tinyint not null default 0;
alter table PURCHASEINVOICELINE add column TAXCATEGORY bigint unsigned default null;
alter table PURCHASEINVOICELINE add FOREIGN KEY (TAXCATEGORY) references INVITEMTAXCATEGORY(ITSID);
alter table PURCHASEINVOICESERVICELINE add column TAXCATEGORY bigint unsigned default null;
alter table PURCHASEINVOICESERVICELINE add FOREIGN KEY (TAXCATEGORY) references INVITEMTAXCATEGORY(ITSID);
alter table SALESINVOICELINE add column TAXCATEGORY bigint unsigned default null;
alter table SALESINVOICELINE add FOREIGN KEY (TAXCATEGORY) references INVITEMTAXCATEGORY(ITSID);
alter table SALESINVOICESERVICELINE add column TAXCATEGORY bigint unsigned default null;
alter table SALESINVOICESERVICELINE add FOREIGN KEY (TAXCATEGORY) references INVITEMTAXCATEGORY(ITSID);
alter table SALESRETURNLINE add column TAXCATEGORY bigint unsigned default null;
alter table SALESRETURNLINE add FOREIGN KEY (TAXCATEGORY) references INVITEMTAXCATEGORY(ITSID);
alter table SERVICETOSALE add column DEFUNITOFMEASURE bigint unsigned default null;
alter table SERVICETOSALE add FOREIGN KEY (DEFUNITOFMEASURE) REFERENCES UNITOFMEASURE(ITSID);
alter table SERVICEPURCHASED add column DEFUNITOFMEASURE bigint unsigned default null;
alter table SERVICEPURCHASED add FOREIGN KEY (DEFUNITOFMEASURE) REFERENCES UNITOFMEASURE(ITSID);
alter table PURCHASEINVOICE add column OMITTAXES tinyint not null default 0;
alter table SALESINVOICE add column OMITTAXES tinyint not null default 0;
alter table SALESRETURN add column OMITTAXES tinyint not null default 0;
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
drop table GOODSSPECIFIC;
drop table GOODSCATALOGS;
drop table GOODSPRICE;
drop table GOODSAVAILABLE;
set FOREIGN_KEY_CHECKS=0;
drop table SESELLER;
set FOREIGN_KEY_CHECKS=1;
update GOODSINLISTLUV set GOODSAVAILABLELUV=null;
update DATABASEINFO set DATABASEVERSION=7, DESCRIPTION='Beige Accounting OIO DB version 7';
