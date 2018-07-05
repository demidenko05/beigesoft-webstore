select SERVICESPECIFICS.ITSVERSION as ITSVERSION, SERVICESPECIFICS.LONGVALUE2 as LONGVALUE2, SERVICE.ITSID as GOODSITSID, SERVICESPECIFICS.LONGVALUE1 as LONGVALUE1,
case when I18NSERVICETOSALE.ITSNAME is null then SERVICE.ITSNAME else I18NSERVICETOSALE.ITSNAME end as GOODSITSNAME,
case when I18NCHOOSEABLESPECIFICS.ITSNAME is not null then I18NCHOOSEABLESPECIFICS.ITSNAME when I18NUNITOFMEASURE.ITSNAME is not null then I18NUNITOFMEASURE.ITSNAME else SERVICESPECIFICS.STRINGVALUE1 end as STRINGVALUE1,
case when I18NSPECIFICSOFITEMGROUP.ITSNAME is null then ITSGROOP.ITSNAME else I18NSPECIFICSOFITEMGROUP.ITSNAME end as ITSGROOPITSNAME,
case when I18NSPECIFICSOFITEM.ITSNAME is null then SPECIFICS.ITSNAME else I18NSPECIFICSOFITEM.ITSNAME end as SPECIFICSITSNAME,
SERVICESPECIFICS.NUMERICVALUE1 as NUMERICVALUE1, SERVICESPECIFICS.STRINGVALUE3 as STRINGVALUE3, SERVICESPECIFICS.STRINGVALUE2 as STRINGVALUE2,
SERVICESPECIFICS.NUMERICVALUE2 as NUMERICVALUE2, SERVICESPECIFICS.STRINGVALUE4 as STRINGVALUE4, TEMPLATEDETAIL.HTMLTEMPLATE as TEMPLATEDETAILHTMLTEMPLATE,
TEMPLATEDETAIL.ITSID as TEMPLATEDETAILITSID, TEMPLATESTART.HTMLTEMPLATE as TEMPLATESTARTHTMLTEMPLATE, TEMPLATESTART.ITSID as TEMPLATESTARTITSID,
TEMPLATEEND.HTMLTEMPLATE as TEMPLATEENDHTMLTEMPLATE, TEMPLATEEND.ITSID as TEMPLATEENDITSID, ITSGROOP.ITSID as ITSGROOPITSID,
SPECIFICS.ISSHOWINLIST as SPECIFICSISSHOWINLIST, SPECIFICS.ITSID as SPECIFICSITSID, SPECIFICS.ITSTYPE as SPECIFICSITSTYPE, TEMPHTML.HTMLTEMPLATE as TEMPHTMLHTMLTEMPLATE, TEMPHTML.ITSID as TEMPHTMLITSID
from SERVICESPECIFICS
left join SERVICETOSALE as SERVICE on SERVICESPECIFICS.ITEM=SERVICE.ITSID
left join SPECIFICSOFITEM as SPECIFICS on SERVICESPECIFICS.SPECIFICS=SPECIFICS.ITSID
left join SPECIFICSOFITEMGROUP as ITSGROOP on SPECIFICS.ITSGROOP=ITSGROOP.ITSID
left join HTMLTEMPLATE as TEMPLATEDETAIL on ITSGROOP.TEMPLATEDETAIL=TEMPLATEDETAIL.ITSID
left join HTMLTEMPLATE as TEMPLATESTART on ITSGROOP.TEMPLATESTART=TEMPLATESTART.ITSID
left join HTMLTEMPLATE as TEMPLATEEND on ITSGROOP.TEMPLATEEND=TEMPLATEEND.ITSID
left join HTMLTEMPLATE as TEMPHTML on SPECIFICS.TEMPHTML=TEMPHTML.ITSID
left join (select ITSNAME, HASNAME from I18NSERVICETOSALE where HASNAME=:ITEMID and LANG=':LANG') as I18NSERVICETOSALE on I18NSERVICETOSALE.HASNAME=SERVICE.ITSID
left join (select ITSNAME, HASNAME from I18NSPECIFICSOFITEM where LANG=':LANG') as I18NSPECIFICSOFITEM on I18NSPECIFICSOFITEM.HASNAME=SPECIFICS.ITSID
left join (select ITSNAME, HASNAME from I18NSPECIFICSOFITEMGROUP where LANG=':LANG') as I18NSPECIFICSOFITEMGROUP on I18NSPECIFICSOFITEMGROUP.HASNAME=ITSGROOP.ITSID
left join (select ITSNAME, HASNAME from I18NCHOOSEABLESPECIFICS where LANG=':LANG') as I18NCHOOSEABLESPECIFICS on I18NCHOOSEABLESPECIFICS.HASNAME=SERVICESPECIFICS.LONGVALUE1 and SPECIFICS.ITSTYPE=9
left join (select ITSNAME, HASNAME from I18NUNITOFMEASURE where LANG=':LANG') as I18NUNITOFMEASURE on I18NUNITOFMEASURE.HASNAME=SERVICESPECIFICS.LONGVALUE2 and (SPECIFICS.ITSTYPE=1 or SPECIFICS.ITSTYPE=2)
where SERVICESPECIFICS.ITEM=:ITEMID order by SPECIFICS.ITSINDEX;
