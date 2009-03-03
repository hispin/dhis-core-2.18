
<%@ page import="java.sql.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>

<%
      Connection con=null;
      
      // for Performance in the Reporting Month
      Statement st=null;
      ResultSet rs=null;
      
      // for selected OrgUnit Name and Population
      Statement st1=null;
      ResultSet rs1=null;
      
      // for Performance in Corresponding month Last Year
      Statement st2=null;
      ResultSet rs2=null;
      
      // for Cumulative Performance till Current Month
      Statement st4=null;
      ResultSet rs4=null;
     
      // for Cumulative Performance till corresponding month of Last Year
      Statement st3=null;
      ResultSet rs3=null;
 
      // for Taluk Name and Id
      Statement st5=null;
      ResultSet rs5=null;

      // for District Name and Id
      Statement st6=null;
      ResultSet rs6=null;

     // for PHC Name and Id
      Statement st8=null;
      ResultSet rs8=null;

     // for CHC Name and Id
      Statement st9=null;
      ResultSet rs9=null;

     // for PHC Population Estimates
      Statement st10=null;
      ResultSet rs10=null;

     // for Data Period Start Date and End Date
      Statement st11=null;
      ResultSet rs11=null;
      
     // for DataElement ids based on DataElement code
      Statement st12=null;
      ResultSet rs12=null;
     
      
      String userName = "dhis";           
      String password = "";           
      String urlForConnection = "jdbc:mysql://localhost/gj_dhis2";
          
      int talukID = 0;
      String talukName = "";
      int districtID = 0; 
      String districtName = ""; 
      int CHCID = 0;
      String CHCName ="";
      int PHCID = 0;
      String PHCName ="";          
      int totPHCPopulation = -1;
      int totSCPopulation = -1;

	  OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  int selectedOrgUnitID =   Integer.parseInt( selectedId );
	

 	  String startingDate  =  (String) stack.findValue( "startingPeriod" );
	  String endingDate  =   (String) stack.findValue( "endingPeriod" );

      
	  String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	  int periodTypeID = 	   Integer.parseInt( monthlyPeriodId );
	       
   	  int lastYear = 0;
      
	  String selectedOrgUnitName = "";
	  String selectedDataPeriodStartDate = "";
      	  String selectedDataPeriodEndDate = "";
	  String lastDataPeriodStartDate = "";
	  
	   String dataElementCodes[] = {
										"' '",
										 
										"'Form6_DE0001'", "'Form6_DE0002'", 
										
										"' '","' '","' '",
										
										"'Form6_DE0003'", "'Form6_DE0004'", "'Form6_DE0005'", "'Form6_DE0006'", "'Form6_DE0007'", "'Form6_DE0008'", "'Form6_DE0009'", 
										"'Form6_DE0010'", "'Form6_DE0011'", "'Form6_DE0012'", "'Form6_DE0013'", "'Form6_DE0014'", "'Form6_DE0015'", "'Form6_DE0016'", 
										"'Form6_DE0017'", "'Form6_DE0018'", "'Form6_DE0019'", "'Form6_DE0020'", "'Form6_DE0021'", "'Form6_DE0022'", "'Form6_DE0023'", 
										"'Form6_DE0024'", "'Form6_DE0025'", "'Form6_DE0026'", "'Form6_DE0027'", "'Form6_DE0028'", "'Form6_DE0029'", "'Form6_DE0030'", 
										"'Form6_DE0031'", "'Form6_DE0032'", "'Form6_DE0033'", "'Form6_DE0034'", "'Form6_DE0035'", 	"'Form6_DE0036'", "'Form6_DE0037'", 
										"'Form6_DE0038'", 
										
										// 2.7
										" 'Form6_DE0042','Form6_DE0045' ",
										" 'Form6_DE0043','Form6_DE0046' ",
										" 'Form6_DE0044','Form6_DE0047' ",
										
										"'Form6_DE0039'", "'Form6_DE0040'", "'Form6_DE0041'", "'Form6_DE0042'", "'Form6_DE0043'", "'Form6_DE0044'", "'Form6_DE0045'", 
										"'Form6_DE0046'", "'Form6_DE0047'",	"'Form6_DE0048'", "'Form6_DE0049'", "'Form6_DE0050'", "'Form6_DE0051'", "'Form6_DE0052'",
										"'Form6_DE0053'", "'Form6_DE0054'", "'Form6_DE0055'", "'Form6_DE0056'", 
																				
										"' '","' '","' '",
										
										//3
										" 'Form6_DE0057','Form6_DE0060','Form6_DE0063','Form6_DE0066','Form6_DE0072','Form8_DE0001','Form8_DE0004','Form8_DE0010','Form8_DE0013','Form8_DE0016','Form6_DE0075','Form6_DE0078' ",
										" 'Form6_DE0058','Form6_DE0061','Form6_DE0064','Form6_DE0067','Form6_DE0073','Form8_DE0002','Form8_DE0005','Form8_DE0011','Form8_DE0014','Form8_DE0017','Form6_DE0076','Form6_DE0079' ",
										" 'Form6_DE0059','Form6_DE0062','Form6_DE0065','Form6_DE0068','Form6_DE0074','Form8_DE0003','Form8_DE0006','Form8_DE0012','Form8_DE0015','Form8_DE0018','Form6_DE0077','Form6_DE0080' ",

										
										//3.1
										" 'Form6_DE0057','Form6_DE0060','Form6_DE0063','Form6_DE0066' ",
										" 'Form6_DE0058','Form6_DE0061','Form6_DE0064','Form6_DE0067' ",
										" 'Form6_DE0059','Form6_DE0062','Form6_DE0065','Form6_DE0068' ",

										
										"'Form6_DE0057'", "'Form6_DE0058'", "'Form6_DE0059'", "'Form6_DE0060'", "'Form6_DE0061'", "'Form6_DE0062'", "'Form6_DE0063'", 
										"'Form6_DE0064'", "'Form6_DE0065'",	"'Form6_DE0066'", "'Form6_DE0067'", "'Form6_DE0068'",
										
										//3.15  
										" 'Form6_DE0057','Form6_DE0060' ",
										" 'Form6_DE0058','Form6_DE0061' ",
										" 'Form6_DE0059','Form6_DE0062' ",
																				
										//3.2
										" 'Form6_DE0072','Form8_DE0001','Form8_DE0004','Form8_DE0010','Form8_DE0013','Form8_DE0016' ",
										" 'Form6_DE0073','Form8_DE0002','Form8_DE0005','Form8_DE0011','Form8_DE0014','Form8_DE0017' ",
										" 'Form6_DE0074','Form8_DE0003','Form8_DE0006','Form8_DE0012','Form8_DE0015','Form8_DE0018' ",
										
										"'Form6_DE0072'", "'Form6_DE0073'", "'Form6_DE0074'", 
										"'Form7_DE0001'", "'Form7_DE0002'", "'Form7_DE0003'", "'Form7_DE0001'", "'Form7_DE0002'", "'Form7_DE0003'", 
										"'Form8_DE0001'", "'Form8_DE0002'", "'Form8_DE0003'", "'Form8_DE0004'", "'Form8_DE0005'", "'Form8_DE0006'", "'Form8_DE0010'", 
										"'Form8_DE0011'", "'Form8_DE0012'", "'Form8_DE0013'", "'Form8_DE0014'", "'Form8_DE0015'", "'Form8_DE0016'", "'Form8_DE0017'", 										
										"'Form8_DE0018'",
										
										//3.3
										" 'Form6_DE0075','Form6_DE0078' ",
										" 'Form6_DE0076','Form6_DE0079' ",
										" 'Form6_DE0077','Form6_DE0080' ",
										 
										"'Form6_DE0075'", "'Form6_DE0076'", "'Form6_DE0077'", "'Form6_DE0078'", "'Form6_DE0079'", "'Form6_DE0080'", 
										
										"' '","' '","' '","' '","' '","' '",
										
										//4.1
										" 'Form6_DE0081','Form6_DE0082','Form6_DE0087','Form6_DE0090' ",
										" 'Form6_DE0083','Form6_DE0084','Form6_DE0088','Form6_DE0091' ",
										" 'Form6_DE0085','Form6_DE0086','Form6_DE0089','Form6_DE0092' ",
										
										
										"'Form6_DE0081'", "'Form6_DE0082'", "'Form6_DE0083'", "'Form6_DE0084'", "'Form6_DE0085'", "'Form6_DE0086'", "'Form6_DE0087'", 
										"'Form6_DE0088'", "'Form6_DE0089'", "'Form6_DE0090'", "'Form6_DE0091'", "'Form6_DE0092'",  
										
										"' '", "' '","' '","' '","' '","' '", 
										
										"'Form6_DE0093'", "'Form6_DE0094'", "'Form6_DE0095'", "'Form6_DE0096'", "'Form6_DE0097'", "'Form6_DE0098'", "'Form6_DE0099'", 
										"'Form6_DE0100'", "'Form6_DE0101'", "'Form6_DE0102'", "'Form6_DE0103'", "'Form6_DE0104'", 	"'Form6_DE0105'", "'Form6_DE0106'", 
										"'Form6_DE0107'", "'Form6_DE0108'", "'Form6_DE0109'", "'Form6_DE0110'", "'Form6_DE0111'",	"'Form6_DE0111A'", "'Form6_DE0112'", 
									    "'Form6_DE0112A'", "'Form6_DE0113'", "'Form6_DE0113A'", "'Form6_DE0114'", "'Form6_DE0115'", "'Form6_DE0116'", "'Form6_DE0117'",
									    "'Form6_DE0118'", "'Form6_DE0119'", "'Form6_DE0120'", "'Form6_DE0121'",  "'Form6_DE0122'", "'Form6_DE0123'", "'Form6_DE0124'",
									    "'Form6_DE0125'",
																														
										"' '","' '","' '",
										
										"'Form7_DE0010'", "'Form7_DE0011'", "'Form7_DE0012'", "'Form7_DE0013'", "'Form7_DE0014'", "'Form7_DE0015'",  
										
										"' '","' '","' '",
										
										"'Form6_DE0129'", "'Form6_DE0130'", "'Form6_DE0131'", "'Form6_DE0132'", "'Form6_DE0133'", "'Form6_DE0134'", "'Form6_DE0135'",
										"'Form6_DE0136'", "'Form6_DE0137'", "'Form6_DE0138'", "'Form6_DE0139'", "'Form6_DE0140'", "'Form6_DE0141'", "'Form6_DE0142'",
										"'Form6_DE0143'", "'Form6_DE0144'", "'Form6_DE0145'", "'Form6_DE0146'", "'Form6_DE0147'", "'Form6_DE0148'", "'Form6_DE0149'",
										"'Form6_DE0150'", "'Form6_DE0151'", "'Form6_DE0152'", "'Form6_DE0153'", "'Form6_DE0154'", "'Form6_DE0155'", "'Form6_DE0156'",
										"'Form6_DE0157'", "'Form6_DE0158'",
										
										"' '","' '","' '",
																				
										//6.1
										"' '","' '","' '",

										"'Form7_DE0016'", "'Form7_DE0017'", "'Form7_DE0018'", "'Form7_DE0016'", "'Form7_DE0017'", "'Form7_DE0018'", "'Form8_DE0022'",
										"'Form8_DE0023'", "'Form8_DE0024'", "'Form8_DE0022'", "'Form8_DE0023'", "'Form8_DE0024'", "'Form8_DE0022'", "'Form8_DE0023'",
										"'Form8_DE0024'", "'Form9_DE0001'", "'Form9_DE0002'", "'Form9_DE0003'", "'Form9_DE0004'", "'Form9_DE0005'", "'Form9_DE0006'",
										
										//6.2
										" 'Form6_DE0162','Form6_DE0165' ",
										" 'Form6_DE0163','Form6_DE0166' ",
										" 'Form6_DE0164','Form6_DE0167' ",

										"'Form6_DE0162'", "'Form6_DE0163'", "'Form6_DE0164'", "'Form6_DE0165'", "'Form6_DE0166'", "'Form6_DE0167'", 
										
										"' '","' '","' '",
										
										//7.1
										" 'Form8_DE0040','Form8_DE0043' ",
										" 'Form8_DE0041','Form8_DE0044' ",
										" 'Form8_DE0042','Form8_DE0045' ",
										
										"'Form8_DE0028'", "'Form8_DE0029'", "'Form8_DE0030'", "'Form8_DE0028'", "'Form8_DE0029'", "'Form8_DE0030'", "'Form8_DE0040'",
										"'Form8_DE0041'", "'Form8_DE0042'", "'Form8_DE0043'", "'Form8_DE0044'", "'Form8_DE0045'", "'Form8_DE0028'", "'Form8_DE0029'",
										"'Form8_DE0030'",
										
										"' '","' '","' '",
										
										//7.2										
										" 'Form6_DE0168','Form6_DE0171' ",
										" 'Form6_DE0169','Form6_DE0172' ",
										" 'Form6_DE0170','Form6_DE0173' ",
										
										"'Form6_DE0168'", "'Form6_DE0169'", "'Form6_DE0170'", "'Form6_DE0171'", "'Form6_DE0172'", "'Form6_DE0173'",
										
										"' '","' '","' '",
										
										//8.1
										" 'Form6_DE0174','Form6_DE0177','Form6_DE0180' ",
										" 'Form6_DE0175','Form6_DE0178','Form6_DE0181' ",
										" 'Form6_DE0176','Form6_DE0179','Form6_DE0182' ",
										
										"'Form6_DE0174'", "'Form6_DE0175'", "'Form6_DE0176'", "'Form6_DE0177'", "'Form6_DE0178'", "'Form6_DE0179'",
										"'Form6_DE0180'", "'Form6_DE0181'", "'Form6_DE0182'", 
										
										"' '","' '",
										
										"'Form7_DE0028'", "'Form6_DE0029'",
										
										"' '","' '","' '",

										//9.1
										" 'Form6_DE0183','Form6_DE0186','Form6_DE0189','Form6_DE0192' ",
										" 'Form6_DE0184','Form6_DE0187','Form6_DE0190','Form6_DE0193' ",
										" 'Form6_DE0185','Form6_DE0188','Form6_DE0191','Form6_DE0194' ",
										
										"'Form6_DE0183'", "'Form6_DE0184'", "'Form6_DE0185'", "'Form6_DE0186'", "'Form6_DE0187'", "'Form6_DE0188'",
										"'Form6_DE0189'", "'Form6_DE0190'", "'Form6_DE0191'", "'Form6_DE0192'", "'Form6_DE0193'", "'Form6_DE0194'",
										
										//9.2
										" 'Form6_DE0195','Form6_DE0198','Form6_DE0201' ",
										" 'Form6_DE0196','Form6_DE0199','Form6_DE0202' ",
										" 'Form6_DE0197','Form6_DE0200','Form6_DE0203' ",
										
										"'Form6_DE0195'", "'Form6_DE0196'", "'Form6_DE0197'", "'Form6_DE0198'", "'Form6_DE0199'", "'Form6_DE0200'",
										"'Form6_DE0201'", "'Form6_DE0202'", "'Form6_DE0203'",
										
										//9.3
										" 'Form6_DE0204','Form6_DE0207' ",
										" 'Form6_DE0205','Form6_DE0208' ",
										" 'Form6_DE0206','Form6_DE0209' ",
										
										"'Form6_DE0204'", "'Form6_DE0205'", "'Form6_DE0206'", "'Form6_DE0207'", "'Form6_DE0208'", "'Form6_DE0209'",
										
										"' '","' '","' '",
																				
										"'Form7_DE0030'", "'Form7_DE0031'", "'Form7_DE0032'", "'Form7_DE0036'", "'Form7_DE0037'","'Form7_DE0038'","'Form7_DE0039'",
										"'Form7_DE0040'", "'Form7_DE0041'",
										
										//9.5
										" 'Form6_DE0213','Form6_DE0216','Form6_DE0219' ",
										" 'Form6_DE0214','Form6_DE0217','Form6_DE0220' ",
										" 'Form6_DE0215','Form6_DE0218','Form6_DE0221' ",

										"'Form6_DE0213'","'Form6_DE0214'","'Form6_DE0215'","'Form6_DE0216'","'Form6_DE0217'","'Form6_DE0218'","'Form6_DE0219'",
										"'Form6_DE0220'","'Form6_DE0221'",
										
										"' '","' '","' '",										
										
										//10.1
										" 'Form8_DE0052','Form8_DE0055','Form7_DE0039','Form7_DE0042' ",
										" 'Form8_DE0053','Form8_DE0056','Form7_DE0040','Form7_DE0043' ",
										" 'Form8_DE0054','Form8_DE0057','Form7_DE0041','Form7_DE0044' ",
										
										//10.2
										" 'Form7_DE0045','Form7_DE0048' ",
										" 'Form7_DE0046','Form7_DE0049' ",
										" 'Form7_DE0047','Form7_DE0050' ",
										
										"'Form7_DE0045'","'Form7_DE0046'","'Form7_DE0047'","'Form7_DE0048'","'Form7_DE0049'","'Form7_DE0050'", 
										
										"' '","' '",
										
										"'Form9_DE0007'", "'Form9_DE0008'", "'Form9_DE0009'", "'Form9_DE0010'", 
										
										"' '","' '",
										 
										"'Form9_DE0011'", "'Form9_DE0012'", 
										
										// 11.3
										" 'Form7_DE0054','Form7_DE0055' ",
										
										"' '","' '","' '","' '","' '","' '",
										
										"'Form7_DE0054'", "'Form7_DE0055'", "'Form7_DE0056'", "'Form7_DE0057'", "'Form7_DE0058'", "'Form7_DE0059'", 
										"'Form6_DE0230A'", "'Form6_DE0230B'", "'Form6_DE0230C'", "'Form6_DE0230D'", "'Form6_DE0230E'", "'Form6_DE0230F'",

										"' '","' '","' '",
										
										//12.1
										" 'Form6_DE0237','Form6_DE0240' ",
										" 'Form6_DE0238','Form6_DE0241' ",
										" 'Form6_DE0239','Form6_DE0242' ",
										
										"'Form6_DE0237'", "'Form6_DE0238'", "'Form6_DE0239'", "'Form6_DE0240'", "'Form6_DE0241'", "'Form6_DE0242'", "'Form6_DE0243'", 
										"'Form6_DE0244'", "'Form6_DE0245'", "'Form6_DE0246'", "'Form6_DE0247'", "'Form6_DE0248'", "'Form6_DE0249'", "'Form6_DE0250'",
										
										  
										"' '","' '","' '",
										
										"'Form6_DE0251'", "'Form6_DE0252'", "'Form6_DE0253'",
										
										//13.2
										" 'Form6_DE0254','Form6_DE0257','Form6_DE0260' ",
										" 'Form6_DE0255','Form6_DE0258','Form6_DE0261' ",																	
										" 'Form6_DE0256','Form6_DE0259','Form6_DE0262' ",

										
										"'Form6_DE0254'", "'Form6_DE0255'", "'Form6_DE0256'", "'Form6_DE0257'", "'Form6_DE0258'", "'Form6_DE0259'", "'Form6_DE0260'",  
										"'Form6_DE0261'", "'Form6_DE0262'", "'Form8_DE0017'", "'Form6_DE0263'", "'Form6_DE0264'", 
										
										"' '",
										
										"'Form6_DE0265'", "'Form6_DE0266'", "'Form6_DE0267'", "'Form6_DE0268'", "'Form6_DE0269'", "'Form6_DE0270'", 
										
										"' '","' '","' '","' '","' '","' '",
										
										"'Form6_DE0271'", "'Form6_DE0272'", "'Form6_DE0273'", "'Form6_DE0274'", "'Form6_DE0275'", "'Form6_DE0276'", "'Form6_DE0277'", 
										"'Form6_DE0278'", "'Form6_DE0279'", "'Form6_DE0280'", "'Form6_DE0281'", "'Form6_DE0282'", "'Form6_DE0283'", "'Form6_DE0284'", 
										"'Form6_DE0285'", "'Form6_DE0286'", "'Form6_DE0287'", "'Form6_DE0288'", "'Form6_DE0289'", "'Form6_DE0290'", "'Form6_DE0291'", 
										"'Form6_DE0292'", "'Form6_DE0293'", "'Form6_DE0294'", "'Form6_DE0295'", "'Form6_DE0296'", "'Form6_DE0297'", "'Form6_DE0298'", 
										"'Form6_DE0299'", "'Form6_DE0300'", "'Form6_DE0301'", "'Form6_DE0302'", "'Form6_DE0303'", "'Form6_DE0304'", "'Form6_DE0305'", 
										"'Form6_DE0306'", "'Form6_DE0307'", "'Form6_DE0308'", "'Form6_DE0309'", "'Form6_DE0310'", "'Form6_DE0311'", "'Form6_DE0312'", 
										"'Form6_DE0313'", "'Form6_DE0314'", "'Form6_DE0315'", "'Form6_DE0316'", "'Form6_DE0317'", "'Form6_DE0318'", "'Form6_DE0319'", 
										"'Form6_DE0320'", "'Form6_DE0321'", "'Form6_DE0322'", "'Form6_DE0323'", "'Form6_DE0324'", "'Form6_DE0325'", "'Form6_DE0326'", 
										"'Form6_DE0327'", "'Form6_DE0328'", "'Form6_DE0329'", "'Form6_DE0330'", "'Form6_DE0331'", "'Form6_DE0332'", "'Form6_DE0333'", 
										"'Form6_DE0334'", "'Form6_DE0335'", "'Form6_DE0336'", "'Form6_DE0337'", "'Form6_DE0338'", "'Form6_DE0339'", "'Form6_DE0340'", 
										"'Form6_DE0341'", "'Form6_DE0342'", "'Form6_DE0343'", "'Form6_DE0344'", "'Form6_DE0345'", "'Form6_DE0346'", "'Form6_DE0347'", 
										"'Form6_DE0348'", "'Form6_DE0349'", "'Form6_DE0350'", "'Form6_DE0351'", "'Form6_DE0352'", "'Form6_DE0353'", "'Form6_DE0354'", 
										"'Form6_DE0355'", "'Form6_DE0356'", "'Form6_DE0357'", "'Form6_DE0358'", "'Form6_DE0359'", "'Form6_DE0360'", "'Form6_DE0361'", 
										"'Form6_DE0362'", "'Form6_DE0363'", "'Form6_DE0364'", "'Form6_DE0365'", "'Form6_DE0366'", "'Form6_DE0367'", "'Form6_DE0368'", 
										"'Form6_DE0369'", "'Form6_DE0370'", "'Form6_DE0371'", "'Form6_DE0372'", 
										
										"' '","' '","' '","' '","' '","' '",
										
										"'Form6_DE0373'", "'Form6_DE0374'", "'Form6_DE0375'", "'Form6_DE0376'", "'Form6_DE0377'", "'Form6_DE0378'", "'Form6_DE0379'", 
										"'Form6_DE0380'", "'Form6_DE0381'", "'Form6_DE0382'", "'Form6_DE0383'", "'Form6_DE0384'", "'Form6_DE0385'", "'Form6_DE0386'", 
										"'Form6_DE0387'", "'Form6_DE0388'", "'Form6_DE0389'", "'Form6_DE0390'", "'Form6_DE0391'", "'Form6_DE0392'", "'Form6_DE0393'", 
										"'Form6_DE0394'", "'Form6_DE0395'", "'Form6_DE0396'", "'Form6_DE0397'", "'Form6_DE0398'", "'Form6_DE0399'", "'Form6_DE0400'", 
										"'Form6_DE0401'", "'Form6_DE0402'", "'Form6_DE0403'", "'Form6_DE0404'", "'Form6_DE0405'", "'Form6_DE0406'", "'Form6_DE0407'", 
										"'Form6_DE0408'", "'Form6_DE0409'", "'Form6_DE0410'", "'Form6_DE0411'", "'Form6_DE0412'", "'Form6_DE0413'", "'Form6_DE0414'", 
										"'Form6_DE0415'", "'Form6_DE0416'", "'Form6_DE0417'", "'Form6_DE0418'", "'Form6_DE0419'", "'Form6_DE0420'", 
										

										//16.4
										" 'Form6_DE0421','Form6_DE0427' ",
										" 'Form6_DE0422','Form6_DE0428' ",
										" 'Form6_DE0423','Form6_DE0429' ",
										" 'Form6_DE0424','Form6_DE0430' ",
										" 'Form6_DE0425','Form6_DE0431' ",
										" 'Form6_DE0426','Form6_DE0432' ",
										
										"'Form6_DE0421'", "'Form6_DE0422'", "'Form6_DE0423'", "'Form6_DE0424'", "'Form6_DE0425'", "'Form6_DE0426'", "'Form6_DE0427'",  
										"'Form6_DE0428'", "'Form6_DE0429'", "'Form6_DE0430'", "'Form6_DE0431'", "'Form6_DE0432'", 
										
										//17
										" 'Form6_DE0433','Form6_DE0439','Form6_DE0445' ",
										" 'Form6_DE0434','Form6_DE0440','Form6_DE0446' ",
										" 'Form6_DE0435','Form6_DE0441','Form6_DE0447' ",
										" 'Form6_DE0436','Form6_DE0442','Form6_DE0448' ",
										" 'Form6_DE0437','Form6_DE0443','Form6_DE0449' ",
										" 'Form6_DE0438','Form6_DE0444','Form6_DE0450' ",																														
										
										"'Form6_DE0433'", "'Form6_DE0434'", "'Form6_DE0435'", "'Form6_DE0436'", "'Form6_DE0437'", "'Form6_DE0438'", "'Form6_DE0439'",
										"'Form6_DE0440'", "'Form6_DE0441'", "'Form6_DE0442'", "'Form6_DE0443'", "'Form6_DE0444'", "'Form6_DE0445'", "'Form6_DE0446'",
										"'Form6_DE0447'", "'Form6_DE0448'", "'Form6_DE0449'", "'Form6_DE0450'", 
										

										
										"'Form6_DE0451'", "'Form6_DE0452'", "'Form6_DE0453'", "'Form6_DE0454'", "'Form6_DE0455'", "'Form6_DE0456'", "'Form6_DE0457'",
										"'Form6_DE0458'", "'Form6_DE0459'", "'Form6_DE0460'", "'Form6_DE0461'", "'Form6_DE0462'", "'Form6_DE0463'", "'Form6_DE0464'", 
										"'Form6_DE0465'", "'Form6_DE0466'", "'Form6_DE0467'", "'Form6_DE0468'", "'Form6_DE0469'", "'Form6_DE0470'", 
										"'Form6_DE0471'", "'Form6_DE0472'", "'Form6_DE0473'", "'Form6_DE0474'", "'Form6_DE0475'", "'Form6_DE0476'", "'Form6_DE0477'",
										"'Form6_DE0478'", "'Form6_DE0479'", "'Form6_DE0480'", "'Form6_DE0481'", "'Form6_DE0482'", "'Form6_DE0483'", "'Form6_DE0484'",
										"'Form6_DE0485'", "'Form6_DE0486'", "'Form6_DE0487'", "'Form6_DE0488'", "'Form6_DE0489'", "'Form6_DE0490'", "'Form6_DE0491'",
										"'Form6_DE0492'", "'Form6_DE0493'", "'Form6_DE0494'", "'Form6_DE0495'", "'Form6_DE0496'", "'Form6_DE0497'", "'Form6_DE0498'",
										"'Form6_DE0499'", "'Form6_DE0500'", "'Form6_DE0501'", "'Form6_DE0502'", "'Form6_DE0503'", "'Form6_DE0504'", "'Form6_DE0505'",
										"'Form6_DE0506'", "'Form6_DE0507'", "'Form6_DE0508'", "'Form6_DE0509'", "'Form6_DE0510'", "'Form6_DE0511'", "'Form6_DE0512'", 
										"'Form6_DE0513'", "'Form6_DE0514'", "'Form6_DE0515'", "'Form6_DE0516'", "'Form6_DE0517'", "'Form6_DE0518'", "'Form6_DE0519'", 
										"'Form6_DE0520'", "'Form6_DE0521'", "'Form6_DE0522'", "'Form6_DE0523'", "'Form6_DE0524'", "'Form6_DE0525'", "'Form6_DE0526'",
										"'Form6_DE0527'", "'Form6_DE0528'", "'Form6_DE0529'", "'Form6_DE0530'", 
										
										"' '","' '","' '",   
 
										"'Form7_DE0060'", "'Form7_DE0061'", "'Form7_DE0062'", "'Form7_DE0063'", "'Form7_DE0064'", "'Form7_DE0065'", "'Form7_DE0066'", 
										"'Form7_DE0067'", "'Form7_DE0068'", "'Form7_DE0069'", "'Form7_DE0070'", "'Form7_DE0071'", 
										
										"' '","' '","' '","' '","' '","' '",
										
										"'Form6_DE0531'", "'Form6_DE0532'", "'Form6_DE0533'", "'Form6_DE0534'", "'Form6_DE0535'", "'Form6_DE0536'", "'Form6_DE0537'", 
										"'Form6_DE0538'", "'Form6_DE0539'", "'Form6_DE0540'", "'Form6_DE0541'", "'Form6_DE0542'", "'Form6_DE0543'", "'Form6_DE0544'",
										"'Form6_DE0545'", "'Form6_DE0546'", "'Form6_DE0547'", "'Form6_DE0548'", "'Form6_DE0549'", "'Form6_DE0550'", "'Form6_DE0551'",  
										"'Form6_DE0552'", "'Form6_DE0553'", "'Form6_DE0554'", "'Form6_DE0555'", "'Form6_DE0556'", "'Form6_DE0557'", "'Form6_DE0558'",
										"'Form6_DE0559'", "'Form6_DE0560'",  
										
										"' '","' '","' '",
										
										"'Form7_DE0072'", "'Form7_DE0073'", "'Form7_DE0074'", "'Form7_DE0075'", "'Form7_DE0076'", "'Form7_DE0077'", 
										
										"' '","' '","' '",
										
										"'Form6_DE0561'", "'Form6_DE0562'", "'Form6_DE0563'",
										
										// 22.2
										" 'Form7_DE0078','Form7_DE0081' ",
										" 'Form7_DE0079','Form7_DE0082' ",
										" 'Form7_DE0080','Form7_DE0083' ",
										
										"'Form7_DE0078'", "'Form7_DE0079'", "'Form7_DE0080'", "'Form7_DE0081'", "'Form7_DE0082'", "'Form7_DE0083'", 
										
										// 22.3
										" 'Form7_DE0084','Form7_DE0087','Form7_DE0090' ",
										" 'Form7_DE0085','Form7_DE0088','Form7_DE0091' ",
										" 'Form7_DE0086','Form7_DE0089','Form7_DE0092' ",
										
										"'Form7_DE0084'", "'Form7_DE0085'", "'Form7_DE0086'", "'Form7_DE0087'", "'Form7_DE0088'", "'Form7_DE0089'", "'Form7_DE0090'", 
										"'Form7_DE0091'", "'Form7_DE0092'",
										 
										"'Form6_DE0576'", "'Form6_DE0577'", "'Form6_DE0578'", "'Form6_DE0579'", "'Form6_DE0580'", "'Form6_DE0581'", "'Form6_DE0582'", 
										"'Form6_DE0583'", "'Form6_DE0584'", "'Form6_DE0585'", "'Form6_DE0586'", "'Form6_DE0587'", "'Form6_DE0588'", "'Form6_DE0589'",
										"'Form6_DE0590'", "'Form6_DE0591'", "'Form6_DE0592'", "'Form6_DE0593'", "'Form6_DE0594'", "'Form6_DE0595'", "'Form6_DE0596'", 
										"'Form6_DE0597'", "'Form6_DE0598'", "'Form6_DE0599'", "'Form6_DE0600'", "'Form6_DE0601'", "'Form6_DE0602'", 
										
										//22.54
										" 'Form6_DE0594','Form6_DE0597','Form6_DE0600' ",
										" 'Form6_DE0595','Form6_DE0598','Form6_DE0601' ",
										" 'Form6_DE0596','Form6_DE0599','Form6_DE0602' ",
										
										"'Form6_DE0603'", "'Form6_DE0604'", "'Form6_DE0605'", "'Form6_DE0606'", "'Form6_DE0607'", "'Form6_DE0608'", 
										
										"' '","' '","' '",
										
										"'Form6_DE0609'", "'Form6_DE0610'", "'Form6_DE0611'", "'Form6_DE0612'", "'Form6_DE0613'", "'Form6_DE0614'", "'Form6_DE0615'",
										"'Form6_DE0616'", "'Form6_DE0617'", "'Form6_DE0618'", "'Form6_DE0619'", "'Form6_DE0620'", 
										
										// 22.74
										" 'Form6_DE0612','Form6_DE0615','Form6_DE0618' ",
										" 'Form6_DE0613','Form6_DE0616','Form6_DE0619' ",
										" 'Form6_DE0614','Form6_DE0617','Form6_DE0620' ",
										
										"'Form6_DE0621'", "'Form6_DE0622'",	"'Form6_DE0623'", 
										
										"' '","' '","' '",
										
										// 22.81
										" 'Form6_DE0624','Form6_DE0627' ",
										" 'Form6_DE0625','Form6_DE0628' ",
										" 'Form6_DE0626','Form6_DE0629' ",
										
										"'Form6_DE0624'", "'Form6_DE0625'", "'Form6_DE0626'", "'Form6_DE0627'", "'Form6_DE0628'", "'Form6_DE0629'", 
										 
										// 22.82
										" 'Form6_DE0630','Form6_DE0633','Form6_DE0636' ",
										" 'Form6_DE0631','Form6_DE0634','Form6_DE0637' ",
										" 'Form6_DE0632','Form6_DE0635','Form6_DE0638' ",
										
										"'Form6_DE0630'", "'Form6_DE0631'", "'Form6_DE0632'", "'Form6_DE0633'", "'Form6_DE0634'", "'Form6_DE0635'", "'Form6_DE0636'",
										"'Form6_DE0637'", "'Form6_DE0638'", "'Form6_DE0639'", "'Form6_DE0640'", "'Form6_DE0641'", 
										  
										"' '","' '","' '",
										
										"'Form6_DE0642'", "'Form6_DE0643'", "'Form6_DE0644'", "'Form6_DE0645'", "'Form6_DE0646'", "'Form6_DE0647'", "'Form6_DE0648'",
										"'Form6_DE0649'", "'Form6_DE0650'",
										
										"' '","' '",
										
										"'Form9_DE0013'", "'Form9_DE0014'", "'Form9_DE0015'", 
										
										"' '",
										
										"'Form9_DE0016'", "'Form9_DE0017'", "'Form9_DE0018'", "'Form9_DE0019'",
										
										"' '",
										
										"'Form9_DE0020'", "'Form9_DE0021'", "'Form9_DE0022'", "'Form9_DE0023'",	"'kkk'",

"'kkk'",

"'kkk'",

"'kkk'",

"'kkk'",

	
	  	  							   };

                 
 	int dataElementIDs[] = new int[dataElementCodes.length+5];  
	int entryNumberValues[]=  new int[dataElementCodes.length+5];	  
      	int entryValuesForLastYear[]= new int[dataElementCodes.length+5];      
      	int cumentryValuesForCurYear[]= new int[dataElementCodes.length+5];      
      	int cumentryValuesForLastYear[]= new int[dataElementCodes.length+5];      
      	
   String monthNames[] = { " ", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October'", "November", "December" };    
%>

<%
     try
      {
        Class.forName ("com.mysql.jdbc.Driver").newInstance ();
        con = DriverManager.getConnection (urlForConnection, userName, password);
        
        st=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY); 
        st1=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st2=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st3=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st4=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st5=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY); 
        st6=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st8=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st9=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st10=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st11=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st12=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);

     
        //rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE id = "+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE organisationunitid = "+selectedOrgUnitID);        
        if(rs1.next())  {   selectedOrgUnitName = rs1.getString(1);                  }
        else   {     	selectedOrgUnitName = "";                      }  

  
  		selectedDataPeriodStartDate = startingDate;
 

    } // try block end
     catch(Exception e)  { out.println(e.getMessage());  }
     finally
       {
			try
				{
  					if(rs1!=null)  rs1.close();			if(st1!=null)  st1.close();
					if(rs11!=null)  rs11.close();		if(st11!=null)  st11.close();
				}
			catch(Exception e)   {  out.println(e.getMessage());   }
       } // finally block end		

	String partsOfDataPeriodStartDate[]  =  selectedDataPeriodStartDate.split("-");
	lastYear  = Integer.parseInt(partsOfDataPeriodStartDate[0]) - 1;
	lastDataPeriodStartDate = lastYear+"-"+partsOfDataPeriodStartDate[1]+"-"+partsOfDataPeriodStartDate[2];
     	
	int tempForMonth1 = Integer.parseInt(partsOfDataPeriodStartDate[1]);
	int tempForYear = 0;
	 	
	if(tempForMonth1 < 4)   	{   tempForYear = lastYear;  }
 	else  {   tempForYear = lastYear + 1;   	}
 	 
 	 String curYearStart = tempForYear+"-04-01";
 	 String lastYearStart = (tempForYear-1)+"-04-01";
 	 String lastYearEnd = lastYear+"-"+partsOfDataPeriodStartDate[1]+"-"+partsOfDataPeriodStartDate[2];
	 
     //for district, taluk, CHC names
     try
      {
	        //rs8=st8.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
	        rs8=st8.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");        
			if(rs8.next())  { PHCID = rs8.getInt(1);PHCName = rs8.getString(2);  } 
			else  {  PHCID = 0; PHCName = "";  } 

			//rs9=st9.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+PHCID+")");
			rs9=st9.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+PHCID+")");	
			if(rs9.next())  { CHCID = rs9.getInt(1);CHCName = rs9.getString(2);  } 
			else  {  CHCID = 0; CHCName = "";  } 

			//rs5=st5.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+CHCID+")");
			rs5=st5.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+CHCID+")");	
			if(rs5.next())  { talukID = rs5.getInt(1); talukName = rs5.getString(2);  } 
			else  {  talukID = 0; talukName = "";  } 
        
		    //rs6=st6.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+talukID+")");
		    rs6=st6.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+talukID+")"); 
		    if(rs6.next()) {  districtID = rs6.getInt(1); districtName = rs6.getString(2);}
			else {districtID = 0; districtName = "";}      

			//rs10=st10.executeQuery("SELECT sum(datavalue.value) FROM organisationunit INNER JOIN (dataelement INNER JOIN datavalue ON dataelement.id = datavalue.dataElement) ON organisationunit.id = datavalue.source WHERE organisationunit.parent = "+PHCID+" AND dataelement.name like 'Total Population'");
			rs10=st10.executeQuery("SELECT sum(datavalue.value) FROM organisationunit INNER JOIN (dataelement INNER JOIN datavalue ON dataelement.dataelementid = datavalue.dataelementid) ON organisationunit.organisationunitid = datavalue.sourceid WHERE organisationunit.parentid = "+PHCID+" AND dataelement.name like 'Total Population'");
			if(rs10.next()) { totPHCPopulation = rs10.getInt(1);}
			else {totPHCPopulation = 0;}      
       
       }   // try block end		 
     catch(Exception e)  { out.println("Exception1 : "+e.getMessage());  }
     finally
       {
		 try
			  {
			    if(rs5!=null)  rs5.close();			if(st5!=null)  st5.close();
			    if(rs6!=null)  rs6.close();			if(st6!=null)  st6.close();
			    if(rs8!=null)  rs8.close();			if(st8!=null)  st8.close();   
			    if(rs9!=null)  rs9.close();			if(st9!=null)  st9.close();
			    if(rs10!=null)  rs10.close();			if(st10!=null)  st10.close();                                                                                                                
			  }
		catch(Exception e)   {  out.println("Exception2 : "+e.getMessage());   }
       }  // finally block end
    
     try
      {
		int i=0;    
		int j= dataElementCodes.length;
		String query = "";	
		while(i!=j) 
			{		
				 entryNumberValues[i]	 = -1;
				 entryValuesForLastYear[i] = -1;
				 cumentryValuesForCurYear[i] = -1;
				 cumentryValuesForLastYear[i] = -1;
										
				// for Performance in the reporting month				
				//	query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source ="+selectedOrgUnitID+" AND dataelement.code in ("+dataElementCodes[i]+")"; 
				
				if(i==84 || i==85 || i==86) // 3.15
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP') OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP') OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP')  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP')) AND dataelement.code in ("+dataElementCodes[i]+")";
		   	  	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP') OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP') OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP')  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP')) AND dataelement.code in ("+dataElementCodes[i]+")";	
				else if(i==93 || i==94 || i==95 || i==234 || i==235 || i==236) // 3.22, 6.11
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+") AND shortname LIKE '% - 24 hours') OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% - 24 hours') OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% - 24 hours')  AND shortname LIKE '% - 24 hours')) AND dataelement.code in ("+dataElementCodes[i]+")";	
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+") AND shortname LIKE '% - 24 hours') OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% - 24 hours') OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% - 24 hours')  AND shortname LIKE '% - 24 hours')) AND dataelement.code in ("+dataElementCodes[i]+")";
				else if(i==96 || i==97 || i==98 || i==237 || i==238 || i==249) // 3.23, 6.12
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+") AND shortname LIKE '% PHC') OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% PHC') OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% PHC')  AND shortname LIKE '% PHC')) AND dataelement.code in ("+dataElementCodes[i]+")";
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+") AND shortname LIKE '% PHC') OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% PHC') OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% PHC')  AND shortname LIKE '% PHC')) AND dataelement.code in ("+dataElementCodes[i]+")";	
				else if(i==240 || i==241 || i==242 || i==270 || i==271 || i==272) // 6.13, 7.11
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+") AND shortname LIKE '% CHC') OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% CHC') OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% CHC')  AND shortname LIKE '% CHC')) AND dataelement.code in ("+dataElementCodes[i]+")";
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+") AND shortname LIKE '% CHC') OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% CHC') OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% CHC')  AND shortname LIKE '% CHC')) AND dataelement.code in ("+dataElementCodes[i]+")";	
				else if(i==243 || i==244 || i==245 || i==273 || i==274 || i==275) // 6.14, 7.12
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+") AND shortname LIKE '% FRU') OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% FRU') OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% FRU')  AND shortname LIKE '% FRU')) AND dataelement.code in ("+dataElementCodes[i]+")";
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+") AND shortname LIKE '% FRU') OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% FRU') OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% FRU')  AND shortname LIKE '% FRU')) AND dataelement.code in ("+dataElementCodes[i]+")";	
				else if(i==246 || i==247 || i==248) // 6.15
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+") AND shortname LIKE '% SDH') OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% SDH') OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% SDH')  AND shortname LIKE '% SDH')) AND dataelement.code in ("+dataElementCodes[i]+")";
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+") AND shortname LIKE '% SDH') OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% SDH') OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% SDH')  AND shortname LIKE '% SDH')) AND dataelement.code in ("+dataElementCodes[i]+")";	
				else if(i==282 || i==283 || i==284) // 7.15
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+") AND shortname LIKE '% PPU') OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% PPU') OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% PPU')  AND shortname LIKE '% PPU')) AND dataelement.code in ("+dataElementCodes[i]+")";
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+") AND shortname LIKE '% PPU') OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% PPU') OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% PPU')  AND shortname LIKE '% PPU')) AND dataelement.code in ("+dataElementCodes[i]+")";	
				else if(i==404)
				  //query = "SELECT count(*) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")) OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")) OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")))) AND dataelement.code in ("+dataElementCodes[i]+")  AND datavalue.value >= 10 ";
				  query = "SELECT count(*) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")) OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")) OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")))) AND dataelement.code in ("+dataElementCodes[i]+")  AND datavalue.value >= 10 ";	
			   	else 
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")) OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")) OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")))) AND dataelement.code in ("+dataElementCodes[i]+")";
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")) OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")) OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")))) AND dataelement.code in ("+dataElementCodes[i]+")";	

				rs=st.executeQuery(query);
				if(rs.next())  {  entryNumberValues[i] = rs.getInt(1);  } 
				else  {  entryNumberValues[i] = 0;  } 
						


			    // for Cumulative Performance till Current Month
			    

				if(i==84 || i==85 || i==86) // 3.15
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP') OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP') OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP')  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP')) AND dataelement.code in ("+dataElementCodes[i]+")";
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP') OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP') OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP')  AND shortname LIKE '% UFWC' OR shortname LIKE '% UHP')) AND dataelement.code in ("+dataElementCodes[i]+")";	
				else if(i==93 || i==94 || i==95 || i==234 || i==235 || i==236) // 3.22, 6.11
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+") AND shortname LIKE '% - 24 hours') OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% - 24 hours') OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% - 24 hours')  AND shortname LIKE '% - 24 hours')) AND dataelement.code in ("+dataElementCodes[i]+")";
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+") AND shortname LIKE '% - 24 hours') OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% - 24 hours') OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% - 24 hours')  AND shortname LIKE '% - 24 hours')) AND dataelement.code in ("+dataElementCodes[i]+")";	
				else if(i==96 || i==97 || i==98 || i==237 || i==238 || i==249) // 3.23, 6.12
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+") AND shortname LIKE '% PHC') OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% PHC') OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% PHC')  AND shortname LIKE '% PHC')) AND dataelement.code in ("+dataElementCodes[i]+")";
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+") AND shortname LIKE '% PHC') OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% PHC') OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% PHC')  AND shortname LIKE '% PHC')) AND dataelement.code in ("+dataElementCodes[i]+")";	
				else if(i==240 || i==241 || i==242 || i==270 || i==271 || i==272) // 6.13, 7.11
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+") AND shortname LIKE '% CHC') OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% CHC') OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% CHC')  AND shortname LIKE '% CHC')) AND dataelement.code in ("+dataElementCodes[i]+")";
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+") AND shortname LIKE '% CHC') OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% CHC') OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% CHC')  AND shortname LIKE '% CHC')) AND dataelement.code in ("+dataElementCodes[i]+")";	
				else if(i==243 || i==244 || i==245 || i==273 || i==274 || i==275) // 6.14, 7.12
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+") AND shortname LIKE '% FRU') OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% FRU') OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% FRU')  AND shortname LIKE '% FRU')) AND dataelement.code in ("+dataElementCodes[i]+")";
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+") AND shortname LIKE '% FRU') OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% FRU') OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% FRU')  AND shortname LIKE '% FRU')) AND dataelement.code in ("+dataElementCodes[i]+")";	
				else if(i==246 || i==247 || i==248) // 6.15
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+") AND shortname LIKE '% SDH') OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% SDH') OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% SDH')  AND shortname LIKE '% SDH')) AND dataelement.code in ("+dataElementCodes[i]+")";
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+") AND shortname LIKE '% SDH') OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% SDH') OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% SDH')  AND shortname LIKE '% SDH')) AND dataelement.code in ("+dataElementCodes[i]+")";	
				else if(i==282 || i==283 || i==284) // 7.15
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+") AND shortname LIKE '% PPU') OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% PPU') OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")  AND shortname LIKE '% PPU')  AND shortname LIKE '% PPU')) AND dataelement.code in ("+dataElementCodes[i]+")";
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+") AND shortname LIKE '% PPU') OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% PPU') OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")  AND shortname LIKE '% PPU')  AND shortname LIKE '% PPU')) AND dataelement.code in ("+dataElementCodes[i]+")";	
				else if(i==404)
				  //query = "SELECT count(*) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")) OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")) OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")))) AND dataelement.code in ("+dataElementCodes[i]+")  AND datavalue.value >= 10 ";
				  query = "SELECT count(*) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")) OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")) OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")))) AND dataelement.code in ("+dataElementCodes[i]+")  AND datavalue.value >= 10 ";	
			   	else 
			   	  //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.source in (SELECT id from organisationunit WHERE id = "+selectedOrgUnitID+" OR id in (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")) OR parent IN (SELECT id FROM organisationunit WHERE id IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")) OR id IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent IN (SELECT id FROM organisationunit WHERE parent = "+selectedOrgUnitID+")))) AND dataelement.code in ("+dataElementCodes[i]+")";
			   	  query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"') AND datavalue.sourceid in (SELECT organisationunitid from organisationunit WHERE organisationunitid = "+selectedOrgUnitID+" OR organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")) OR parentid IN (SELECT organisationunitid FROM organisationunit WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")) OR organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid IN (SELECT organisationunitid FROM organisationunit WHERE parentid = "+selectedOrgUnitID+")))) AND dataelement.code in ("+dataElementCodes[i]+")";	



			    rs4=st4.executeQuery(query);
     			if(rs4.next())  {  cumentryValuesForCurYear[i] = rs4.getInt(1);  } 
			    else  {  cumentryValuesForCurYear[i] = 0;  } 
			          
				
				
				if(i==869 || i==870 || i==871 || i==893 || i==894 || i==895) // 22.54, 22.74
				  {
				    entryNumberValues[i] = entryNumberValues[i-9] + entryNumberValues[i-6] - entryNumberValues[i-3];
					cumentryValuesForCurYear[i] = cumentryValuesForCurYear[i-9] + cumentryValuesForCurYear[i-6] + cumentryValuesForCurYear[i-3];
				  }
				else if(i==234 || i==237 || i==240 || i==243 || i==246 || i==249 || i==252) // 6.1
				  {
				   entryNumberValues[231] += entryNumberValues[i];
  				   cumentryValuesForCurYear[231] += cumentryValuesForCurYear[i];
				  }    
				else if(i==235 || i==238 || i==241 || i==244 || i==247 || i==250 || i==253) // 6.1
				  {
				   entryNumberValues[232] += entryNumberValues[i]; 
  				   cumentryValuesForCurYear[232] += cumentryValuesForCurYear[i];
				  }    
				else if(i==236 || i==239 || i==242 || i==245 || i==248 || i==251 || i==254) // 6.1
				  {
				   entryNumberValues[233] += entryNumberValues[i];
  				   cumentryValuesForCurYear[233] += cumentryValuesForCurYear[i];				   
				  }    
				else if(i==270 || i==273 || i==282) // 7.1
				  {
				    entryNumberValues[267] += entryNumberValues[i];
  				    cumentryValuesForCurYear[267] += cumentryValuesForCurYear[i];				    
				  }  
				else if(i==271 || i==274 || i==283) // 7.1
				  {
				    entryNumberValues[268] += entryNumberValues[i];
  				    cumentryValuesForCurYear[268] += cumentryValuesForCurYear[i];				    
				  }  
				else if(i==272 || i==275 || i==284) // 7.1
				  {
				    entryNumberValues[269] += entryNumberValues[i];
  				    cumentryValuesForCurYear[269] += cumentryValuesForCurYear[i];				    
				  }  

				i++;
			}  // while loop end
      }  // try block end   		 
     catch(Exception e)  { out.println(e.getMessage());  }
     finally
       {
			 try
				{
				    if(rs!=null)  rs.close();		if(st!=null)  st.close();      							
					if(rs2!=null)  rs2.close();		if(st2!=null)  st2.close();    
					if(rs3!=null)  rs3.close();		if(st3!=null)  st3.close();					
					if(rs4!=null)  rs4.close();		if(st4!=null)  st4.close();
					if(rs12!=null)  rs12.close();		if(st12!=null)  st12.close();
										
					if(con!=null) con.close();					
		        }	 
			catch(Exception e)   {  out.println("Exception3 : "+e.getMessage());   }
       }  // finally block end	                     	        
%>

<HTML>
<HEAD>
   <TITLE>Form - 9</TITLE>
   <script src="../dhis-web-reports/Gujarat/GDENamesForForm9.js" type="text/javascript" language="Javascript"></script> 	
   <script>
   		function fun1()
        	{ 
        	    
        		var start=0;
        		var end = 28;    
        		var j=1;
        		var k=0;             	
        		var id="";        		
        	
        		while(j<=11)
        		 {        		    
        		   	
        		   	if(j==1) end = 22;
					else if(j==2) end = 34;
					else if(j==3) end = 29;
					else if(j==4) end = 31;
					else if(j==5) end = 33;
					else if(j==6) end = 24;
					else if(j==7) end = 15;
					else if(j==8) end = 28;
        		   	else if(j==9) end = 20;
        		   	else if(j==10) end = 19;
        		   	else if(j==11) end = 13;
        		   	
	   	 			for(start=0;start<=end;start++)
	   	 			 {	
					  	 id="cell1"+k;   	 			  
	   	 			  	 document.getElementById(id).innerHTML = slnoForForm9[k];
					  	id="cell2"+k;   	 			  
	   	 			  	document.getElementById(id).innerHTML = denamesForForm9[k];
	   	 			  	k++;
	   	 			 }
	   	 			j++; 
	   	 		 }	   	 			   	 		
	  		}
  	</script>   		    
</HEAD>
<BODY BGCOLOR="#FFFFFF" onload="fun1()">
	<br><br>
	<center>
		<font face="Arial" size="3">
			<b>&#2731;&#2763;&#2736;&#2765;&#2734; &#2728;&#2690;. &#2799;<BR>&#2730;&#2765;&#2736;&#2716;&#2750;&#2728;&#2728; &#2693;&#2728;&#2759; &#2732;&#2750;&#2739; &#2694;&#2736;&#2763;&#2711;&#2765;&#2735; - ||<BR>&#2744;&#2690;&#2744;&#2765;&#2725;&#2750;&#2709;&#2752;&#2735; &#2734;&#2750;&#2745;&#2751;&#2724;&#2752; &#2730;&#2727;&#2765;&#2727;&#2724;&#2751;<BR>&#2716;&#2751;&#2738;&#2765;&#2738;&#2750; &#2726;&#2765;&#2741;&#2736;&#2750; &#2736;&#2750;&#2716;&#2765;&#2735; &#2709;&#2709;&#2765;&#2743;&#2750;&#2703; &#2734;&#2763;&#2709;&#2738;&#2741;&#2750;&#2728;&#2763; &#2734;&#2750;&#2744;&#2752;&#2709; &#2734;&#2763;&#2728;&#2752;&#2719;&#2736;&#2752;&#2690;&#2711; &#2736;&#2752;&#2730;&#2763;&#2736;&#2765;&#2719;</b>
		</font>
	</center>

	<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  		<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    		<td width="70%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       			<font face="Arial" size="2">&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2753;&#2690; &#2728;&#2750;&#2734; :&nbsp;&nbsp;<%=selectedOrgUnitName%></font>
    	</td>
    		<td width="30%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       			<font face="Arial" size="2">&#2693;&#2745;&#2759;&#2741;&#2750;&#2738;&#2728;&#2763; &#2734;&#2750;&#2744; :&nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%></font>
    	</td>   
  	</tr>
 	<tr>
    		<td width="70%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" height="19">
       			<font face="Arial" size="2">&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2763; &#2709;&#2763;&#2721; (&#2741;&#2744;&#2724;&#2751; &#2711;&#2723;&#2724;&#2736;&#2752; &#2792;&#2790;&#2790;&#2791; &#2734;&#2753;&#2716;&#2732;)</font>
    	</td>
    		<td width="30%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
       			<font face="Arial" size="2">&#2693;&#2745;&#2759;&#2741;&#2750;&#2738;&#2728;&#2753; &#2741;&#2736;&#2765;&#2743; :&nbsp;&nbsp;<%=partsOfDataPeriodStartDate[0]%></font>
    	</td>
  	</tr>		
	</table>  
<br>

<%
 	int i;
 	int j = 1;
 	int k = 0;
 	int l = 0;
 	int endcount = 28;
 	int flag = 0;
	String tempForentryNumberValues[] = new String[12];
	String tempForcumentryValuesForCurYear[] = new String[12];
	
 	 	 	
 	String temp1 = "";
 	
 	String temp2 = "";
 	
 	int temp = 0;
int bt = 0;
 	while(j<=11)
 	 { 
 	   
 	    if(j==1) 
 	      { %>
 	       	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">
  				<tr>
    				<td width="100%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%" align="center" colspan="14"><font face="Arial" size="2"><b>&#2733;&#2750;&#2711;-&#2693; &#2734;&#2750;&#2724;&#2750; &#2693;&#2728;&#2759; &#2732;&#2750;&#2710; &#2694;&#2736;&#2763;&#2711;&#2765;&#2735;</b></font></td>
				</tr> 
		   <%	 	      
 	       }
 	    else if(j==8)
 	       { %>
 	        <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
			<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%"> 	       
 	         <tr>
    			<td width="2%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; " bordercolor="#111111" width="100%" align="left" rowspan="3"><b><font face="Arial" size="2">&#2791;&#2798;</b></font></td>
    			<td width="34%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:15; padding-right:1; " bordercolor="#111111" width="100%" rowspan="3"><b><font face="Arial" size="2">&#2732;&#2750;&#2739;&#2709;&#2763;&#2728;&#2750; &#2736;&#2763;&#2711;</b></font></td>
    			<td width="32%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="8"><b><font face="Arial" size="2">&#2714;&#2750;&#2738;&#2753; &#2734;&#2750;&#2744; &#2727;&#2736;&#2734;&#2765;&#2735;&#2750;&#2728;</b></font></td>
    			<td width="32%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="8"><b><font face="Arial" size="2">&#2703;&#2730;&#2765;&#2736;&#2752;&#2738;&#2735;&#2752; &#2741;&#2727;&#2724;&#2752; &#2716;&#2724;&#2752; &#2709;&#2750;&#2734;&#2711;&#2752;&#2736;&#2752;</font></b></td>
		  	</tr>
			<tr>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2742;&#2763;&#2727;&#2759;&#2738;</font></b></td>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2744;&#2750;&#2736;&#2741;&#2750;&#2736; &#2694;&#2730;&#2759;&#2738;</font></b></td>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2736;&#2752;&#2731;&#2736; &#2709;&#2736;&#2759;&#2738;</font></b></td>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2734;&#2755;&#2724;&#2765;&#2735;&#2753;</font></b></td>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2742;&#2763;&#2727;&#2759;&#2738;</font></b></td>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2744;&#2750;&#2736;&#2741;&#2750;&#2736; &#2694;&#2730;&#2759;&#2738;</font></b></td>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2736;&#2752;&#2731;&#2736; &#2709;&#2736;&#2759;&#2738;</font></b></td>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2734;&#2755;&#2724;&#2765;&#2735;&#2753;</font></b></td>
		  	</tr>
			<tr>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
		  	</tr> 	
 	       <%
 	       }
 	    else if(j==2)
 	      { %>
			<br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
        else if(j==3)
 	      { %>
			<br><br><br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
 		else if(j==4)
 	      { %>
			<br><br><br><br><br><br><br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
		else if(j==5)
 	      { %>
			<br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
		else if(j==6)
 	      { %>
			<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%"> 													  	         
 	      <% 
			}
		else if(j==7)
 	      { %>
			<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
		else if(j==8)
 	      { %>
			<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
		else if(j==9)
 	      { %>
			<br><br><br><br><br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
		else if(j==10)
 	      { %>
			<br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
		else if(j==11)
 	      { %>
			<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}

 	    else
 	      { %>
			<br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
		if(j==1) endcount = 22; 
		else if(j==2) endcount = 34; 
		else if(j==3) endcount = 29;
		else if(j==4) endcount = 31;
		else if(j==5) endcount = 33;
		else if(j==6) endcount = 24;
		else if(j==7) endcount = 15;
		else if(j==8) endcount = 28;
		else if(j==9) endcount = 20;
		else if(j==10) endcount = 19;
		else if(j==11) endcount = 13;
												
 		for(i=0;i<=endcount;i++)
 	 	  {  	
 	     	String id1="cell1"+k;
 	     	String id2="cell2"+k;
 	    
 	     	if((k>=3 && k<=42) || (k==44) || (k>=46 && k<=47) || (k>=55 && k<=96) || (k>=99 && k<=124) || (k>=137 && k<=142) || (k>=145 && k<=150) || (k>=224 && k<=264) )
 	      	{ 
               if(k==3 || k==23 || k==55 || k==58 ||  k==88 || k==99 || k==120 || k==137 || k==135 || k==145 || k==224 || k==245 || k==252 || k==261) 
 	            {
 	              tempForentryNumberValues[0]= "<b>&#2709;&#2753;&#2738;</b>";
 	              tempForentryNumberValues[1]= "<b>&#2693;.&#2716;&#2750;.</b>";
				  tempForentryNumberValues[2]= "<b>&#2693;.&#2716;.&#2716;&#2750;</b>";
 
 	              tempForcumentryValuesForCurYear[0] = "<b>&#2709;&#2753;&#2738;</b>";
 	              tempForcumentryValuesForCurYear[1] = "<b>&#2693;.&#2716;&#2750;.</b>";
 	              tempForcumentryValuesForCurYear[2] = "<b>&#2693;.&#2716;.&#2716;&#2750;</b>"; 	              
 	              
 	              l= l+3;
 	            }
			   else if(k==69 || k==81 || k==92 || k==112)
				{
 	              tempForentryNumberValues[0]= " ";
 	              tempForentryNumberValues[1]= " ";
				  tempForentryNumberValues[2]= " ";
 
 	              tempForcumentryValuesForCurYear[0] = " ";
 	              tempForcumentryValuesForCurYear[1] = " ";
 	              tempForcumentryValuesForCurYear[2] = " "; 	              
 	              
 	              l= l+3;
 	            }			    	
 	           else 
 	            {

					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[0] = "";
 	         		else tempForentryNumberValues[0] = ""+temp;
 					
 					temp = cumentryValuesForCurYear[l];
			 		if(temp==-1) tempForcumentryValuesForCurYear[0] = "";
			 		else tempForcumentryValuesForCurYear[0] = ""+temp;
//tempForcumentryValuesForCurYear[0] = dataElementCodes[l];
//tempForentryNumberValues[0] = ""+l;

					l++;
						         
 	         		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[1] = "";
 	         		else tempForentryNumberValues[1] = ""+temp;

					temp = cumentryValuesForCurYear[l];
			 		if(temp==-1) tempForcumentryValuesForCurYear[1] = "";
			 		else tempForcumentryValuesForCurYear[1] = ""+temp;
//tempForcumentryValuesForCurYear[1] = dataElementCodes[l];
 	         		
 	         		l++;
 	         		
			 		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[2] = "";
 	         		else tempForentryNumberValues[2] = ""+temp;
 	         		
 	         		temp = cumentryValuesForCurYear[l];
			 		if(temp==-1) tempForcumentryValuesForCurYear[2] = "";
			 		else tempForcumentryValuesForCurYear[2] = ""+temp;
//tempForcumentryValuesForCurYear[2] = dataElementCodes[l];

					l++;
 			 		
   	            } 
 	        %>
 	        
 	   
 
 	        
 	        
 	        
 	      	<tr>
    			<td id="<%=id1%>" name="<%=id1%>" width="2%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; " bordercolor="#111111" align="left">&nbsp;</td>
    			<td id="<%=id2%>" name="<%=id2%>" width="34%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ></td>
    			<td width="12%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="2"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    			<td width="10%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="2"><font face="Arial" size="2"><%=tempForentryNumberValues[1]%></font></td>
    			<td width="10%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="2"><font face="Arial" size="2"><%=tempForentryNumberValues[2]%></font></td>
    			<td width="12%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="2"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[0]%></font></td>
    			<td width="10%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="2"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[1]%></font></td>
    			<td width="10%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="2"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[2]%></font></td>
			</tr>
 	   <%   }


 	    else if( (k==43) || (k==45) || (k>=48 && k<=54) ||  (k>=134 && k<=136) || (k>=161 && k<=194) || (k>=213 && k<=218))
 	      { 
                if(k==43 || k==48 || k==94 || k==120 || k==134 || k==161 || k==179 || k==213) 
 	            {
 	              tempForentryNumberValues[0]= "<b>&#2730;&#2753;&#2736;&#2753;&#2743;</b>";
 	              tempForentryNumberValues[1]= "<b>&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</b>";
				  tempForentryNumberValues[2]= "<b>&#2730;&#2753;&#2736;&#2753;&#2743;</b>";
   	              tempForentryNumberValues[3]= "<b>&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</b>";
   	              tempForentryNumberValues[4]= "<b>&#2730;&#2753;&#2736;&#2753;&#2743;</b>";
 	              tempForentryNumberValues[5]= "<b>&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</b>";

 	              tempForcumentryValuesForCurYear[0] = "<b>&#2730;&#2753;&#2736;&#2753;&#2743;</b>";
 	              tempForcumentryValuesForCurYear[1] = "<b>&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</b>";
 	              tempForcumentryValuesForCurYear[2] = "<b>&#2730;&#2753;&#2736;&#2753;&#2743;</b>";
   	              tempForcumentryValuesForCurYear[3] = "<b>&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</b>";
   	              tempForcumentryValuesForCurYear[4] = "<b>&#2730;&#2753;&#2736;&#2753;&#2743;</b>";
 	              tempForcumentryValuesForCurYear[5] = "<b>&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</b>"; 	
 	              
				  l = l+6;	               	              
 	            }
 	           else 
 	            {
				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[0] = "";
         		else tempForentryNumberValues[0] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[0] = "";
		 		else tempForcumentryValuesForCurYear[0] = ""+temp;
//tempForcumentryValuesForCurYear[0] = dataElementCodes[l];
//tempForentryNumberValues[0] = ""+l;

				l++;
				
				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[1] = "";
         		else tempForentryNumberValues[1] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[1] = "";
		 		else tempForcumentryValuesForCurYear[1] = ""+temp;
//tempForcumentryValuesForCurYear[1] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[2] = "";
         		else tempForentryNumberValues[2] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[2] = "";
		 		else tempForcumentryValuesForCurYear[2] = ""+temp;
//tempForcumentryValuesForCurYear[2] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[3] = "";
         		else tempForentryNumberValues[3] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[3] = "";
		 		else tempForcumentryValuesForCurYear[3] = ""+temp;
//tempForcumentryValuesForCurYear[3] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[4] = "";
         		else tempForentryNumberValues[4] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[4] = "";
		 		else tempForcumentryValuesForCurYear[4] = ""+temp;
//tempForcumentryValuesForCurYear[4] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[5] = "";
         		else tempForentryNumberValues[5] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[5] = "";
		 		else tempForcumentryValuesForCurYear[5] = ""+temp;
//tempForcumentryValuesForCurYear[5] = dataElementCodes[l];

				l++;
 	            } 

 	        %>
 	      	<tr>
    			<td id="<%=id1%>" name="<%=id1%>" width="2%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; " bordercolor="#111111" align="left">&nbsp;</td>
    			<td id="<%=id2%>" name="<%=id2%>" width="34%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ></td>
    			<td width="6%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    			<td width="6%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[1]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[2]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[3]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[4]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[5]%></font></td>
    			<td width="6%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[0]%></font></td>
    			<td width="6%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[1]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[2]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[3]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[4]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[5]%></font></td>
			</tr>
 	   <%   }



 else if( (k>=195 && k<=204))
 	      { 
                
				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[0] = "";
         		else tempForentryNumberValues[0] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[0] = "";
		 		else tempForcumentryValuesForCurYear[0] = ""+temp;
//tempForcumentryValuesForCurYear[0] = dataElementCodes[l];		 		
//tempForentryNumberValues[0] = ""+l;

				l++;
				
				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[1] = "";
         		else tempForentryNumberValues[1] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[1] = "";
		 		else tempForcumentryValuesForCurYear[1] = ""+temp;
//tempForcumentryValuesForCurYear[1] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[2] = "";
         		else tempForentryNumberValues[2] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[2] = "";
		 		else tempForcumentryValuesForCurYear[2] = ""+temp;
//tempForcumentryValuesForCurYear[2] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[3] = "";
         		else tempForentryNumberValues[3] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[3] = "";
		 		else tempForcumentryValuesForCurYear[3] = ""+temp;
//tempForcumentryValuesForCurYear[3] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
 	         	if(temp==-1) tempForentryNumberValues[4] = "";
 	         	else tempForentryNumberValues[4] = ""+temp;
 					
 				temp = cumentryValuesForCurYear[l];
			 	if(temp==-1) tempForcumentryValuesForCurYear[4] = "";
			 	else tempForcumentryValuesForCurYear[4] = ""+temp;
//tempForcumentryValuesForCurYear[4] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
 	         	if(temp==-1) tempForentryNumberValues[5] = "";
 	         	else tempForentryNumberValues[5] = ""+temp;
 					
 				temp = cumentryValuesForCurYear[l];
			 	if(temp==-1) tempForcumentryValuesForCurYear[5] = "";
			 	else tempForcumentryValuesForCurYear[5] = ""+temp;
//tempForcumentryValuesForCurYear[5] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
 	         	if(temp==-1) tempForentryNumberValues[6] = "";
 	         	else tempForentryNumberValues[6] = ""+temp;
 					
 				temp = cumentryValuesForCurYear[l];
			 	if(temp==-1) tempForcumentryValuesForCurYear[6] = "";
			 	else tempForcumentryValuesForCurYear[6] = ""+temp;
//tempForcumentryValuesForCurYear[6] = dataElementCodes[l];

				l++;
					
 				temp =  entryNumberValues[l];
 	         	if(temp==-1) tempForentryNumberValues[7] = "";
 	         	else tempForentryNumberValues[7] = ""+temp;
 					
 				temp = cumentryValuesForCurYear[l];
			 	if(temp==-1) tempForcumentryValuesForCurYear[7] = "";
			 	else tempForcumentryValuesForCurYear[7] = ""+temp;
//tempForcumentryValuesForCurYear[7] = dataElementCodes[l];

				l++;



 	        %>
 	      	<tr>
    			<td id="<%=id1%>" name="<%=id1%>" width="2%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; " bordercolor="#111111" width="100%" align="left">&nbsp;</td>
    			<td id="<%=id2%>" name="<%=id2%>" width="34%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:15; padding-right:1; " bordercolor="#111111" width="100%">&nbsp;</td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[1]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[2]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[3]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[4]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[5]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[6]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[7]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[0]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[1]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[2]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[3]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[4]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[5]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[6]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[7]%></font></td>
		  	</tr>
 	   <%   }

		else if((k>=125 && k<=132) || (k>=265 && k<=278))
		 { 
		 

		  	if(k==125 || k==130 || k==265) 
 	            {
 	              	tempForentryNumberValues[0]= "<B>&#2709;&#2753;&#2738;</B>";
					tempForentryNumberValues[1]= "<B>&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2750; &#2693;&#2727;&#2751;&#2709;&#2750;&#2736;&#2752; &#2727;&#2765;&#2741;&#2750;&#2736;&#2750;</B>";
					tempForentryNumberValues[2]= "<B>&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2750; &#2693;&#2727;&#2751;&#2709;&#2750;&#2736;&#2752; &#2727;&#2765;&#2741;&#2750;&#2736;&#2750;</B>";

 	              	tempForcumentryValuesForCurYear[0] = "<B>&#2709;&#2753;&#2738;</B>";
					tempForcumentryValuesForCurYear[1] = "<B>&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2750; &#2693;&#2727;&#2751;&#2709;&#2750;&#2736;&#2752; &#2727;&#2765;&#2741;&#2750;&#2736;&#2750;</B>";
					tempForcumentryValuesForCurYear[2] = "<B>&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2750; &#2693;&#2727;&#2751;&#2709;&#2750;&#2736;&#2752; &#2727;&#2765;&#2741;&#2750;&#2736;&#2750;</B>";

 	                l= l+2;
 	             }
			else if(k==269 || k==274)
			    {
			    	tempForentryNumberValues[0] = " ";
			    	tempForcumentryValuesForCurYear[0] = " ";
			    	l++;			    	
			    }
 	       	else  
 	        	{

					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[0] = "";
 	         		else tempForentryNumberValues[0] = ""+temp;
 					
 					temp = cumentryValuesForCurYear[l];
			 		if(temp==-1) tempForcumentryValuesForCurYear[0] = "";
			 		else tempForcumentryValuesForCurYear[0] = ""+temp;

//tempForcumentryValuesForCurYear[0] = dataElementCodes[l];
//tempForentryNumberValues[0] = ""+l;
					
					l++;
					
 	         		
 	        	}  

		 
		 %>
 	       	<tr>
    			<td id="<%=id1%>" name="<%=id1%>" width="2%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1;  padding-bottom:0; " bordercolor="#111111" align="left">&nbsp;</td>
    			<td id="<%=id2%>" name="<%=id2%>" width="34%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1;  padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ></td>
    			<td width="22%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="4"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    			<td width="21%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="4"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[0]%></font></td>
    			<td width="21%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="4"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[0]%></font></td>
			</tr>   	       	

 	   <%   }

		else if( (k>=97 && k<=98) || (k>=206 && k<=212) || (k>=220 && k<=223))
		 { 
		 

		  	if(k==97 || k==192 || k==206 || k==220) 
 	            {
 	              	tempForentryNumberValues[0]= "<B>&#2709;&#2753;&#2738;</B>";
					tempForentryNumberValues[1]= "<B>&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2750; &#2693;&#2727;&#2751;&#2709;&#2750;&#2736;&#2752; &#2727;&#2765;&#2741;&#2750;&#2736;&#2750;</B>";

 	              	tempForcumentryValuesForCurYear[0] = "<B>&#2709;&#2753;&#2738;</B>";
					tempForcumentryValuesForCurYear[1] = "<B>&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2750; &#2693;&#2727;&#2751;&#2709;&#2750;&#2736;&#2752; &#2727;&#2765;&#2741;&#2750;&#2736;&#2750;</B>";

 	                l= l+2;
 	             }
 	       	else  
 	        	{

					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[0] = "";
 	         		else tempForentryNumberValues[0] = ""+temp;
 					
 					temp = cumentryValuesForCurYear[l];
			 		if(temp==-1) tempForcumentryValuesForCurYear[0] = "";
			 		else tempForcumentryValuesForCurYear[0] = ""+temp;
//tempForcumentryValuesForCurYear[0] = dataElementCodes[l];
//tempForentryNumberValues[0] = ""+l;

					l++;

					temp =  entryNumberValues[l];
         			if(temp==-1) tempForentryNumberValues[1] = "";
         			else tempForentryNumberValues[1] = ""+temp;
 					
					temp = cumentryValuesForCurYear[l];
		 			if(temp==-1) tempForcumentryValuesForCurYear[1] = "";
		 			else tempForcumentryValuesForCurYear[1] = ""+temp;
//tempForcumentryValuesForCurYear[1] = dataElementCodes[l];

					l++;
 	         		
 	        	}  

		 
		 %>
   	       	
   	       	<tr>
    			<td id="<%=id1%>" name="<%=id1%>" width="2%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1;  padding-bottom:0; " bordercolor="#111111" align="left">&nbsp;</td>
    			<td id="<%=id2%>" name="<%=id2%>" width="34%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1;  padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ></td>
    			<td width="16%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="3"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    			<td width="16%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="3"><font face="Arial" size="2"><%=tempForentryNumberValues[1]%></font></td>
    			<td width="16%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="3"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[0]%></font></td>
    			<td width="16%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="3"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[1]%></font></td>
			</tr>

	<%	 }
 	     else 
 	      {   	       

 			if(k==205) { %>
							</table> 	    
 	    					<br>
 	    					<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">
 	   			 		<% }

 	         if(k==0 || k==83 || k==112 || k==115 || k==141 || k==154 || k==205 || k==219) 
 	            {
 	              tempForentryNumberValues[0]= "<B>&#2714;&#2750;&#2738;&#2753; &#2734;&#2750;&#2744; &#2727;&#2736;&#2734;&#2765;&#2735;&#2750;&#2728;</B>";
 	              tempForcumentryValuesForCurYear[0] = "<B>&#2703;&#2730;&#2765;&#2736;&#2752;&#2738;&#2735;&#2752; &#2741;&#2727;&#2724;&#2752; &#2716;&#2724;&#2752; &#2709;&#2750;&#2734;&#2711;&#2752;&#2736;&#2752;</B>";
 	              
 	              l= l+1;
 	             }
 	       	  else  
 	        	{

					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[0] = "";
 	         		else tempForentryNumberValues[0] = ""+temp;
//tempForentryNumberValues[0] = ""+l;
 					
 					temp = cumentryValuesForCurYear[l];
			 		if(temp==-1) tempForcumentryValuesForCurYear[0] = "";
			 		else tempForcumentryValuesForCurYear[0] = ""+temp;
//tempForcumentryValuesForCurYear[0] = dataElementCodes[l];

					l++;
 	         		
 	        	}  
 	       %>
 	       	<tr>
    			<td id="<%=id1%>" name="<%=id1%>" width="2%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1;  padding-bottom:0; " bordercolor="#111111" align="left">&nbsp;</td>
    			<td id="<%=id2%>" name="<%=id2%>" width="34%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1;  padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ></td>
    			<td width="32%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="6"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    			<td width="32%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="6"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[0]%></font></td>
			</tr>
 	     	<% 
 	     }    	  	   
		 k++;
		}		
		 j++;
		%>
		</table>
		<%
	 }	
 %>
</BODY>
</HTML>