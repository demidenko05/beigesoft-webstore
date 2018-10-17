select SEGOODSSPECIFICS.ITSVERSION as ITSVERSION, SEGOODSSPECIFICS.LONGVALUE2 as LONGVALUE2, ITEM.ITSID as ITEMITSID, SEGOODSSPECIFICS.LONGVALUE1 as LONGVALUE1,
case when I18NSEGOODS.ITSNAME is null then ITEM.ITSNAME else I18NSEGOODS.ITSNAME end as ITEMITSNAME,
case when I18NCHOOSEABLESPECIFICS.ITSNAME is not null then I18NCHOOSEABLESPECIFICS.ITSNAME when I18NUNITOFMEASURE.ITSNAME is not null then I18NUNITOFMEASURE.ITSNAME else SEGOODSSPECIFICS.STRINGVALUE1 end as STRINGVALUE1,
case when I18NSPECIFICSOFITEMGROUP.ITSNAME is null then ITSGROOP.ITSNAME else I18NSPECIFICSOFITEMGROUP.ITSNAME end as ITSGROOPITSNAME,
case when I18NSPECIFICSOFITEM.ITSNAME is null then SPECIFICS.ITSNAME else I18NSPECIFICSOFITEM.ITSNAME end as SPECIFICSITSNAME,
SEGOODSSPECIFICS.NUMERICVALUE1 as NUMERICVALUE1, SEGOODSSPECIFICS.STRINGVALUE3 as STRINGVALUE3, SEGOODSSPECIFICS.STRINGVALUE2 as STRINGVALUE2,
SEGOODSSPECIFICS.NUMERICVALUE2 as NUMERICVALUE2, SEGOODSSPECIFICS.STRINGVALUE4 as STRINGVALUE4, TEMPLATEDETAIL.HTMLTEMPLATE as TEMPLATEDETAILHTMLTEMPLATE,
TEMPLATEDETAIL.ITSID as TEMPLATEDETAILITSID, TEMPLATESTART.HTMLTEMPLATE as TEMPLATESTARTHTMLTEMPLATE, TEMPLATESTART.ITSID as TEMPLATESTARTITSID,
TEMPLATEEND.HTMLTEMPLATE as TEMPLATEENDHTMLTEMPLATE, TEMPLATEEND.ITSID as TEMPLATEENDITSID, ITSGROOP.ITSID as ITSGROOPITSID,
SPECIFICS.ISSHOWINLIST as SPECIFICSISSHOWINLIST, SPECIFICS.ITSID as SPECIFICSITSID, SPECIFICS.ITSTYPE as SPECIFICSITSTYPE, TEMPHTML.HTMLTEMPLATE as TEMPHTMLHTMLTEMPLATE, TEMPHTML.ITSID as TEMPHTMLITSID
from SEGOODSSPECIFICS
left join SEGOODS as ITEM on SEGOODSSPECIFICS.ITEM=ITEM.ITSID
left join SPECIFICSOFITEM as SPECIFICS on SEGOODSSPECIFICS.SPECIFICS=SPECIFICS.ITSID
left join SPECIFICSOFITEMGROUP as ITSGROOP on SPECIFICS.ITSGROOP=ITSGROOP.ITSID
left join HTMLTEMPLATE as TEMPLATEDETAIL on ITSGROOP.TEMPLATEDETAIL=TEMPLATEDETAIL.ITSID
left join HTMLTEMPLATE as TEMPLATESTART on ITSGROOP.TEMPLATESTART=TEMPLATESTART.ITSID
left join HTMLTEMPLATE as TEMPLATEEND on ITSGROOP.TEMPLATEEND=TEMPLATEEND.ITSID
left join HTMLTEMPLATE as TEMPHTML on SPECIFICS.TEMPHTML=TEMPHTML.ITSID
left join (select ITSNAME, HASNAME from I18NSEGOODS where HASNAME=:ITEMID and LANG=':LANG') as I18NSEGOODS on I18NSEGOODS.HASNAME=ITEM.ITSID
left join (select ITSNAME, HASNAME from I18NSPECIFICSOFITEM where LANG=':LANG') as I18NSPECIFICSOFITEM on I18NSPECIFICSOFITEM.HASNAME=SPECIFICS.ITSID
left join (select ITSNAME, HASNAME from I18NSPECIFICSOFITEMGROUP where LANG=':LANG') as I18NSPECIFICSOFITEMGROUP on I18NSPECIFICSOFITEMGROUP.HASNAME=ITSGROOP.ITSID
left join (select ITSNAME, HASNAME from I18NCHOOSEABLESPECIFICS where LANG=':LANG') as I18NCHOOSEABLESPECIFICS on I18NCHOOSEABLESPECIFICS.HASNAME=SEGOODSSPECIFICS.LONGVALUE1 and SPECIFICS.ITSTYPE=9
left join (select ITSNAME, HASNAME from I18NUNITOFMEASURE where LANG=':LANG') as I18NUNITOFMEASURE on I18NUNITOFMEASURE.HASNAME=SEGOODSSPECIFICS.LONGVALUE2 and (SPECIFICS.ITSTYPE=1 or SPECIFICS.ITSTYPE=2)
where SEGOODSSPECIFICS.ITEM=:ITEMID order by SPECIFICS.ITSINDEX;
