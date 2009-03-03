
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
      String urlForConnection = "jdbc:mysql://localhost/kl_dhis2";
          
      int talukID = 0;
      String talukName = "";
      int districtID = 0; 
      String districtName = ""; 
      int CHCID = 0;
      String CHCName ="";
      int PHCID = 0;
      String PHCName ="";          
      String stateName = "";
      int totPHCPopulation = -1;
      int totSCPopulation = -1;

	  OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  int selectedOrgUnitID =  Integer.parseInt( selectedId );
	
  
	  String startingDate  =  (String) stack.findValue( "startingPeriod" );
	  String endingDate  =   (String) stack.findValue( "endingPeriod" );
      
	  String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	  int periodTypeID =   Integer.parseInt( monthlyPeriodId );
	       
   	  int lastYear = 0;
      
	  String selectedOrgUnitName = "";
	  String selectedDataPeriodStartDate = "";
   	  String selectedDataPeriodEndDate = "";
	  String lastDataPeriodStartDate = "";
	  
	   String dataElementCodes[] = {
										
										// Element Group 1 + 2
										"' '", "'Form10_DE1'", "'Form10_DE2'", "' '","' '","' '","'Form10_DE3'", "'Form10_DE4'", "'Form10_DE5'", "'Form10_DE6'", "'Form10_DE7'", 
										"'Form10_DE8'","'Form10_DE9'", "'Form10_DE10'", "'Form10_DE11'", "'Form10_DE12'", "'Form10_DE13'", "'Form10_DE14'", "'Form10_DE15'",
										"'Form10_DE16'", "'Form10_DE17'", "'Form10_DE1501'", "'Form10_DE25'", "'Form10_DE1502'","'Form10_DE21'", "'Form10_DE22'", "'Form10_DE23'", 
										"'Form10_DE24'", "'Form10_DE19'", "'Form10_DE26'", "'Form10_DE27'", "'Form10_DE28'", "'Form10_DE29'", "'Form10_DE30'", "'Form10_DE31'", 
										"'Form10_DE32'", "'Form10_DE33'", "'Form10_DE34'", "'Form10_DE35'", "'Form10_DE36'", "'Form10_DE37'", "'Form10_DE38'", 
										
										// 2.7
										"'Form10_DE39','Form10_DE42','Form10_DE45'",
										"'Form10_DE40','Form10_DE43','Form10_DE46'",
										"'Form10_DE41','Form10_DE44','Form10_DE47'",
										
										"'Form10_DE39'", "'Form10_DE40'", "'Form10_DE41'", "'Form10_DE42'", "'Form10_DE43'",	"'Form10_DE44'", "'Form10_DE45'", "'Form10_DE46'", 
										"'Form10_DE47'",	"'Form10_DE48'", "'Form10_DE49'", "'Form10_DE50'", "'Form10_DE51'", "'Form10_DE52'", "'Form10_DE53'", "'Form10_DE54'", 
										"'Form10_DE55'","'Form10_DE56'", 
																												
										"' '","' '","' '", 
						
										// 3.0
										" 'Form10_DE60','Form10_DE63','Form10_DE66','Form10_DE69','Form10_DE75','Form11_DE4','Form11_DE7','Form10_DE78','Form10_DE81'",
										" 'Form10_DE61','Form10_DE64','Form10_DE67','Form10_DE70','Form10_DE76','Form11_DE5','Form11_DE8','Form10_DE79','Form10_DE82'",
										" 'Form10_DE62','Form10_DE65','Form10_DE68','Form10_DE71','Form10_DE77','Form11_DE6','Form11_DE9','Form10_DE80','Form10_DE83'",
									
										// 3.1
										" 'Form10_DE60','Form10_DE63','Form10_DE66','Form10_DE69' ",
										" 'Form10_DE61','Form10_DE64','Form10_DE67','Form10_DE70' ",
										" 'Form10_DE62','Form10_DE65','Form10_DE68','Form10_DE71' ",

										"'Form10_DE60'", "'Form10_DE61'", "'Form10_DE62'",	"'Form10_DE63'", "'Form10_DE64'", "'Form10_DE65'", "'Form10_DE66'", "'Form10_DE67'",
										"'Form10_DE68'", "'Form10_DE69'", "'Form10_DE70'", "'Form10_DE71'",
										
										// 3.2
										" 'Form10_DE75','Form11_DE4','Form11_DE7' ",
										" 'Form10_DE76','Form11_DE5','Form11_DE8' ",
										" 'Form10_DE77','Form11_DE6','Form11_DE9' ",
										
										"'Form10_DE75'", "'Form10_DE76'", "'Form10_DE77'", "'Form11_DE4'","'Form11_DE5'","'Form11_DE6'", "'Form11_DE7'","'Form11_DE8'","'Form11_DE9'",

										// 3.3
										" 'Form10_DE78','Form10_DE81' ",
										" 'Form10_DE79','Form10_DE82' ",
										" 'Form10_DE80','Form10_DE83' ",

										"'Form10_DE78'", "'Form10_DE79'", "'Form10_DE80'", "'Form10_DE81'", "'Form10_DE82'", "'Form10_DE83'", 

										"' '","' '","' '","' '","' '","' '",
										
										// 4.1
										" 'Form10_DE84' ",
										" 'Form10_DE85' ",
										" 'Form10_DE86' ",
										" 'Form10_DE87' ",
										" 'Form10_DE88' ",
										" 'Form10_DE89' ",


										"'Form10_DE84'", "'Form10_DE85'", "'Form10_DE86'", "'Form10_DE87'", "'Form10_DE88'", "'Form10_DE89'", "'Form10_DE90'", "'Form10_DE91'", 
										"'Form10_DE92'", "'Form10_DE93'", "'Form10_DE94'", "'Form10_DE95'", 
										
										"' '","' '","' '","' '","' '","' '",
										 
										"'Form10_DE96'", "'Form10_DE97'", "'Form10_DE98'", "'Form10_DE99'", "'Form10_DE100'", "'Form10_DE101'", "'Form10_DE102'","'Form10_DE103'", 
										"'Form10_DE104'", "'Form10_DE105'", "'Form10_DE106'", "'Form10_DE107'", "'Form10_DE108'", "'Form10_DE109'", "'Form10_DE110'", "'Form10_DE111'",
										"'Form10_DE112'", "'Form10_DE113'",
										
										// 4.3
										" 'Form10_DE117','Form10_DE123' ",
										" 'Form10_DE118','Form10_DE124' ",
										" 'Form10_DE119','Form10_DE125' ",
										" 'Form10_DE120','Form10_DE126' ",	
										" 'Form10_DE121','Form10_DE127' ",
										" 'Form10_DE122','Form10_DE128' ",
																				
										"'Form10_DE117'", "'Form10_DE118'", "'Form10_DE119'", "'Form10_DE120'",	"'Form10_DE121'", "'Form10_DE122'", "'Form10_DE123'", "'Form10_DE124'", 
										"'Form10_DE125'", "'Form10_DE126'", "'Form10_DE127'", "'Form10_DE128'", 
										
										// 4.4
										" 'Form11_DE10','Form11_DE13' ",
										" 'Form11_DE11','Form11_DE14' ",
										" 'Form11_DE12','Form11_DE15' ",
 										
										"'Form11_DE10'", "'Form11_DE11'","'Form11_DE12'","'Form11_DE13'","'Form11_DE14'","'Form11_DE15'",
																				
										"' '","' '","' '", 
																				
										// 5.1
										" 'Form10_DE135','Form10_DE138','Form10_DE141','Form10_DE144','Form10_DE147' ",
										" 'Form10_DE136','Form10_DE139','Form10_DE142','Form10_DE145','Form10_DE148' ",
										" 'Form10_DE137','Form10_DE140','Form10_DE143','Form10_DE146','Form10_DE149' ",

										"'Form10_DE135'", "'Form10_DE136'", "'Form10_DE137'", "'Form10_DE138'", "'Form10_DE139'", "'Form10_DE140'", "'Form10_DE141'", "'Form10_DE142'", 
										"'Form10_DE143'", "'Form10_DE144'", "'Form10_DE145'", "'Form10_DE146'", "'Form10_DE147'", "'Form10_DE148'", "'Form10_DE149'", "'Form10_DE150'", 
										"'Form10_DE151'", "'Form10_DE152'", "'Form10_DE153'", "'Form10_DE154'", "'Form10_DE155'", "'Form10_DE156'", "'Form10_DE157'", "'Form10_DE158'", 
										"'Form10_DE159'", "'Form10_DE160'", "'Form10_DE161'",
										
										"' '","' '","' '",
										
										// 6.1
										" 'Form11_DE16','Form11_DE19' ",
										" 'Form11_DE17','Form11_DE20' ",
										" 'Form11_DE18','Form11_DE21' ",
 																																																		
										"'Form11_DE16'", "'Form11_DE17'", "'Form11_DE18'", "'Form11_DE19'", "'Form11_DE20'", "'Form11_DE21'", "'Form11_DE22'", "'Form11_DE23'", "'Form11_DE24'",
					
										// 6.3
										" 'Form10_DE1500','Form10_DE171' ",
										" 'Form10_DE180','Form10_DE172' ",
										" 'Form10_DE1536','Form10_DE173' ",
										
										"'Form10_DE1500'", "'Form10_DE180'", "'Form10_DE1536'", "'Form10_DE171'", "'Form10_DE172'", "'Form10_DE173'",
										
										"' '","' '","' '",
										
										// 7.1
										" 'Form10_DE1537','Form10_DE1540' ",
										" 'Form10_DE1538','Form10_DE1541' ",
										" 'Form10_DE1539','Form10_DE1542' ",
										
										"'Form10_DE1537'", "'Form10_DE1538'", "'Form10_DE1539'", "'Form10_DE1540'", "'Form10_DE1541'", "'Form10_DE1542'",
										
										// 8
										" 'Form10_DE192','Form10_DE195','Form10_DE198' ",
										" 'Form10_DE193','Form10_DE196','Form10_DE199' ",
										" 'Form10_DE194','Form10_DE197','Form10_DE200' ",
										
										"'Form10_DE192'", "'Form10_DE193'", "'Form10_DE194'", "'Form10_DE195'", "'Form10_DE196'", "'Form10_DE197'", "'Form10_DE198'", "'Form10_DE199'", 
										"'Form10_DE200'", "'Form11_DE25'", "'Form11_DE26'",
																				
										"' '",
										
										"' '","' '","' '",
										
										// 9.1
										" 'Form10_DE207','Form10_DE210','Form10_DE213','Form10_DE216' ",
										" 'Form10_DE208','Form10_DE211','Form10_DE214','Form10_DE217' ",
										" 'Form10_DE209','Form10_DE212','Form10_DE215','Form10_DE218' ",
										
										"'Form10_DE207'", "'Form10_DE208'", "'Form10_DE209'", "'Form10_DE210'", "'Form10_DE211'", "'Form10_DE212'", "'Form10_DE213'", "'Form10_DE214'", 
										"'Form10_DE215'", "'Form10_DE216'", "'Form10_DE217'", "'Form10_DE218'",
										
										// 9.2
										" 'Form10_DE222','Form10_DE225','Form10_DE228' ",
										" 'Form10_DE223','Form10_DE226','Form10_DE229' ",
										" 'Form10_DE224','Form10_DE227','Form10_DE230' ",	
										
										"'Form10_DE222'", "'Form10_DE223'", "'Form10_DE224'", "'Form10_DE225'", "'Form10_DE226'", "'Form10_DE227'", "'Form10_DE228'", "'Form10_DE229'", 
										"'Form10_DE230'",
										
										// 9.3
										" 'Form10_DE234','Form10_DE237' ",
										" 'Form10_DE235','Form10_DE238' ",
										" 'Form10_DE236','Form10_DE239' ",
										
										"'Form10_DE234'", "'Form10_DE235'", "'Form10_DE236'", "'Form10_DE237'", "'Form10_DE238'", "'Form10_DE239'",
										
										// 9.4
										" 'Form11_DE27','Form11_DE30','Form11_DE33' ",
										" 'Form11_DE28','Form11_DE31','Form11_DE34' ",
										" 'Form11_DE29','Form11_DE32','Form11_DE35' ",
																														
										"'Form11_DE27'", "'Form11_DE28'", "'Form11_DE29'", "'Form11_DE30'", "'Form11_DE31'", "'Form11_DE32'", "'Form11_DE33'","'Form11_DE34'", "'Form11_DE35'",

										// 9.5
										" 'Form10_DE246','Form10_DE249','Form10_DE252' ",
										" 'Form10_DE247','Form10_DE250','Form10_DE253' ",
										" 'Form10_DE248','Form10_DE251','Form10_DE254' ",

										"'Form10_DE246'", "'Form10_DE247'", "'Form10_DE248'", "'Form10_DE249'", "'Form10_DE250'", "'Form10_DE251'", "'Form10_DE252'", "'Form10_DE253'", 
										"'Form10_DE254'", 
										
										"' '","' '","' '", 
										
										"'Form10_DE258'","'Form10_DE259'", "'Form10_DE260'", "'Form11_DE36'", "'Form11_DE37'", "'Form11_DE38'",
										
										// 10.3
										" 'Form11_DE42','Form11_DE45' ",
										" 'Form11_DE43','Form11_DE46' ",
										" 'Form11_DE44','Form11_DE47' ",
										
										"'Form11_DE42'", "'Form11_DE43'", "'Form11_DE44'", "'Form11_DE45'",	"'Form11_DE46'", "'Form11_DE47'", "'Form11_DE48'", "'Form11_DE49'", "'Form11_DE50'",
										
										"' '","' '","' '","' '","' '","' '",
																				
										"'Form10_DE261'", "'Form10_DE262'", "'Form10_DE263'", "'Form10_DE264'", "'Form10_DE265'", "'Form10_DE266'", "'Form10_DE267'", "'Form10_DE268'", 
										"'Form10_DE269'", "'Form10_DE270'", "'Form10_DE271'", "'Form10_DE272'", "'Form10_DE273'", "'Form10_DE274'", "'Form10_DE275'", "'Form10_DE276'", 
										"'Form10_DE277'", "'Form10_DE278'", "'Form11_DE51'", "'Form11_DE52'", "'Form11_DE53'", "'Form11_DE54'", "'Form11_DE55'", "'Form11_DE56'", 
										
										"' '","' '","' '",
										
										"'Form10_DE279'", "'Form10_DE280'", "'Form10_DE281'", "'Form10_DE282'", "'Form10_DE283'", "'Form10_DE284'", "'Form10_DE285'", "'Form10_DE286'", 
										"'Form10_DE287'", "'Form10_DE288'", "'Form10_DE289'", "'Form10_DE290'", "'Form10_DE291'", "'Form10_DE292'", "'Form10_DE293'", "'Form10_DE294'", 
										"'Form10_DE295'", 
										
										"' '","' '","' '",
										
										"'Form10_DE296'", "'Form10_DE297'", "'Form10_DE298'",
										
										// 13.2
										" 'Form10_DE302','Form10_DE305','Form10_DE308' ",
										" 'Form10_DE303','Form10_DE306','Form10_DE309' ",
										" 'Form10_DE304','Form10_DE307','Form10_DE310' ",
										
										"'Form10_DE302'", "'Form10_DE303'", "'Form10_DE304'", "'Form10_DE305'", "'Form10_DE306'", "'Form10_DE307'", "'Form10_DE308'", "'Form10_DE309'", 
										"'Form10_DE310'", 
										
										"' '",
										
										"'Form10_DE311'", "'Form10_DE312'",										
										
										"' '",
										
										"'Form10_DE313'", "'Form10_DE314'", "'Form10_DE315'",
										
										// 15
										" 'Form10_DE317','Form10_DE318' ",
 										
										"'Form10_DE317'", "'Form10_DE318'", 

										"' '", "' '", "' '", "' '", "' '", "' '",
										
										"'Form10_DE319'", "'Form10_DE320'", "'Form10_DE321'", "'Form10_DE322'", "'Form10_DE323'", "'Form10_DE324'", "'Form10_DE325'", "'Form10_DE326'", 
										"'Form10_DE327'", "'Form10_DE328'", "'Form10_DE329'", "'Form10_DE330'", 
										
										// 17.12
										" 'Form10_DE337','Form10_DE343','Form10_DE349' ",
										" 'Form10_DE338','Form10_DE344','Form10_DE350' ",
										" 'Form10_DE339','Form10_DE345','Form10_DE351' ",
										" 'Form10_DE340','Form10_DE346','Form10_DE352' ",
										" 'Form10_DE341','Form10_DE347','Form10_DE353' ",
										" 'Form10_DE342','Form10_DE348','Form10_DE354' ",
																			
										"'Form10_DE337'", "'Form10_DE338'", "'Form10_DE339'", "'Form10_DE340'", "'Form10_DE341'", "'Form10_DE342'", "'Form10_DE343'", "'Form10_DE344'", 
										"'Form10_DE345'",
										"'Form10_DE346'", "'Form10_DE347'", "'Form10_DE348'", "'Form10_DE349'", "'Form10_DE350'", "'Form10_DE351'", "'Form10_DE352'", "'Form10_DE353'", 
										"'Form10_DE354'",
										
										// 17.13
										" 'Form10_DE361','Form10_DE367','Form10_DE373','Form10_DE379' ",
										" 'Form10_DE362','Form10_DE368','Form10_DE374','Form10_DE380' ",
										" 'Form10_DE363','Form10_DE369','Form10_DE375','Form10_DE381' ",
										" 'Form10_DE364','Form10_DE370','Form10_DE376','Form10_DE382' ",
										" 'Form10_DE365','Form10_DE371','Form10_DE377','Form10_DE383' ",
										" 'Form10_DE366','Form10_DE372','Form10_DE378','Form10_DE384' ",
										
										"'Form10_DE361'", "'Form10_DE362'", "'Form10_DE363'",
										"'Form10_DE364'", "'Form10_DE365'", "'Form10_DE366'", "'Form10_DE367'", "'Form10_DE368'", "'Form10_DE369'", "'Form10_DE370'", "'Form10_DE371'", 
										"'Form10_DE372'",
										"'Form10_DE373'", "'Form10_DE374'", "'Form10_DE375'", "'Form10_DE376'", "'Form10_DE377'", "'Form10_DE378'", "'Form10_DE379'", "'Form10_DE380'", 
										"'Form10_DE381'",
										"'Form10_DE382'", "'Form10_DE383'", "'Form10_DE384'", 
										
										// 17.14
										" 'Form10_DE391','Form10_DE397','Form10_DE403' ",
										" 'Form10_DE392','Form10_DE398','Form10_DE404' ",
										" 'Form10_DE393','Form10_DE399','Form10_DE405' ",
										" 'Form10_DE394','Form10_DE400','Form10_DE406' ",
										" 'Form10_DE395','Form10_DE401','Form10_DE407' ",
										" 'Form10_DE396','Form10_DE402','Form10_DE408' ",
										
										"'Form10_DE391'", "'Form10_DE392'", "'Form10_DE393'", "'Form10_DE394'", "'Form10_DE395'", "'Form10_DE396'", "'Form10_DE397'", "'Form10_DE398'", 
										"'Form10_DE399'",
										"'Form10_DE400'", "'Form10_DE401'", "'Form10_DE402'", "'Form10_DE403'", "'Form10_DE404'", "'Form10_DE405'", "'Form10_DE406'", "'Form10_DE407'", 
										"'Form10_DE408'",
										"'Form10_DE409'", "'Form10_DE410'", "'Form10_DE411'", "'Form10_DE412'", "'Form10_DE413'", "'Form10_DE414'", "'Form10_DE415'", "'Form10_DE416'", 
										"'Form10_DE417'",
										"'Form10_DE448'", "'Form10_DE449'", "'Form10_DE450'", 
										
										
										// 17.2
										" 'Form10_DE457','Form10_DE463' ",
										" 'Form10_DE458','Form10_DE464' ",
										" 'Form10_DE459','Form10_DE465' ",
										" 'Form10_DE460','Form10_DE466' ",
										" 'Form10_DE461','Form10_DE467' ",
										" 'Form10_DE462','Form10_DE468' ",
										
										"'Form10_DE457'",
										"'Form10_DE458'", "'Form10_DE459'", "'Form10_DE460'", "'Form10_DE461'", "'Form10_DE462'", "'Form10_DE463'", "'Form10_DE464'", "'Form10_DE465'", 
										"'Form10_DE466'",
										"'Form10_DE467'", "'Form10_DE468'", 
										
										"' '","' '","' '","' '","' '","' '",
										
										"'Form10_DE475'",
										"'Form10_DE476'", "'Form10_DE477'", "'Form10_DE478'", "'Form10_DE479'", "'Form10_DE480'", "'Form10_DE481'", "'Form10_DE482'", "'Form10_DE483'", 
										"'Form10_DE484'",
										"'Form10_DE485'", "'Form10_DE486'", "'Form10_DE487'", "'Form10_DE488'", "'Form10_DE489'", "'Form10_DE490'", "'Form10_DE491'", "'Form10_DE492'", 
										"'Form10_DE493'",
										"'Form10_DE494'", "'Form10_DE495'", "'Form10_DE496'", "'Form10_DE497'", "'Form10_DE498'", 
										
										// 17.4
										" 'Form10_DE505','Form10_DE511' ",
										" 'Form10_DE506','Form10_DE512' ",
										" 'Form10_DE507','Form10_DE513' ",
										" 'Form10_DE508','Form10_DE514' ",
										" 'Form10_DE509','Form10_DE515' ",
										" 'Form10_DE510','Form10_DE516' ",
										
										"'Form10_DE505'", "'Form10_DE506'", "'Form10_DE507'", "'Form10_DE508'", "'Form10_DE509'", "'Form10_DE510'", "'Form10_DE511'",
										"'Form10_DE512'", "'Form10_DE513'", "'Form10_DE514'", "'Form10_DE515'", "'Form10_DE516'", "'Form10_DE517'", "'Form10_DE518'", "'Form10_DE519'", 
										"'Form10_DE520'",
										"'Form10_DE521'", "'Form10_DE522'", "'Form10_DE523'", "'Form10_DE524'", "'Form10_DE525'", "'Form10_DE526'", "'Form10_DE527'", "'Form10_DE528'", 
										"'Form10_DE529'",
										"'Form10_DE530'", "'Form10_DE531'", "'Form10_DE532'", "'Form10_DE533'", "'Form10_DE534'", "'Form10_DE535'", "'Form10_DE536'", "'Form10_DE537'", 
										"'Form10_DE538'", 
										"'Form10_DE539'", "'Form10_DE540'", 
										
										
										"'Form10_DE541'", "'Form10_DE542'", "'Form10_DE543'", "'Form10_DE544'", "'Form10_DE545'", "'Form10_DE546'", "'Form10_DE547'", "'Form10_DE548'", 
										"'Form10_DE549'",
										"'Form10_DE550'", "'Form10_DE551'", "'Form10_DE552'", "'Form10_DE553'", "'Form10_DE554'", "'Form10_DE555'", "'Form10_DE556'", "'Form10_DE557'", 
										"'Form10_DE558'",   
										"'Form10_DE559'", "'Form10_DE560'", "'Form10_DE561'", "'Form10_DE562'", "'Form10_DE563'", "'Form10_DE564'", "'Form10_DE565'", "'Form10_DE566'", 
										"'Form10_DE567'",
										"'Form10_DE568'", "'Form10_DE569'", "'Form10_DE570'", "'Form10_DE571'", "'Form10_DE572'", "'Form10_DE573'", "'Form10_DE574'", "'Form10_DE575'", 
										"'Form10_DE576'",
										"'Form10_DE577'", "'Form10_DE578'", "'Form10_DE579'", "'Form10_DE580'", "'Form10_DE581'", "'Form10_DE582'", "'Form10_DE583'", "'Form10_DE584'", 
										"'Form10_DE585'",
										"'Form10_DE586'", "'Form10_DE587'", "'Form10_DE588'", "'Form10_DE589'", "'Form10_DE590'", "'Form10_DE591'", "'Form10_DE592'", "'Form10_DE593'", 
										"'Form10_DE594'",
										"'Form10_DE595'", "'Form10_DE596'", "'Form10_DE597'", "'Form10_DE598'", "'Form10_DE599'", "'Form10_DE600'", "'Form10_DE601'", "'Form10_DE602'", 
										"'Form10_DE603'",
										"'Form10_DE604'", "'Form10_DE605'", "'Form10_DE606'", "'Form10_DE607'", "'Form10_DE608'", "'Form10_DE609'", "'Form10_DE610'", "'Form10_DE611'", 
										"'Form10_DE612'",
										"'Form10_DE613'", "'Form10_DE614'", "'Form10_DE615'", "'Form10_DE616'", "'Form10_DE617'", "'Form10_DE618'", "'Form10_DE619'", "'Form10_DE620'", 
										
										
										"' '","' '","' '","' '",
										
										"'Form11_DE57'", "'Form11_DE58'", 
										
										"' '", 
										
										"'Form11_DE59'", "'Form11_DE60'", 
										
										"' '", 
										
										"'Form11_DE61'", "'Form11_DE62'", 
										
										"' '", 
										
										"'Form11_DE63'", "'Form11_DE64'",
										
										"' '", 
										
										"'Form11_DE65'", "'Form11_DE66'", 
										
										"' '", 
										
										"'Form11_DE67'", "'Form11_DE68'", 
										
										"' '", 
										
										"' '","' '","' '","' '","' '","' '","' '","' '","' '",
										
										"'Form10_DE621'", 
										"'Form10_DE622'", "'Form10_DE623'", "'Form10_DE624'", "'Form10_DE625'", "'Form10_DE626'", "'Form10_DE627'",
										"'Form10_DE628'", "'Form10_DE629'", "'Form10_DE630'", "'Form10_DE631'", "'Form10_DE632'", "'Form10_DE633'", "'Form10_DE634'", "'Form10_DE635'", 
										"'Form10_DE636'",
										"'Form10_DE637'", "'Form10_DE638'", "'Form10_DE639'", "'Form10_DE640'", "'Form10_DE641'", "'Form10_DE642'", "'Form10_DE643'", "'Form10_DE644'", 
										"'Form10_DE645'",
										"'Form10_DE646'", "'Form10_DE647'", "'Form10_DE648'", "'Form10_DE649'", "'Form10_DE650'", 
										
										
										"' '","' '","' '","' '",
										
										"'Form11_DE69'", "'Form11_DE70'", 
										
										"' '", 
										
										"'Form11_DE71'", "'Form11_DE72'", 
										
										"' '", 
										
										"'Form11_DE73'", "'Form11_DE74'", 
										
										"' '", 
										
										"' '", "' '", "' '",
										
										"'Form10_DE651'", "'Form10_DE652'", "'Form10_DE653'", 
										
										// 23.2
										" 'Form11_DE75','Form10_DE726' ",
										" 'Form11_DE76','Form10_DE727' ",
										" 'Form11_DE77','Form10_DE728' ",
 										
										"'Form11_DE75'", "'Form11_DE76'", "'Form11_DE77'",
										"'Form10_DE726'", "'Form10_DE727'", "'Form10_DE728'", 
										
										// 23.3
										" 'Form10_DE729','Form10_DE732','Form10_DE735' ",
										" 'Form10_DE730','Form10_DE733','Form10_DE736' ",
										" 'Form10_DE731','Form10_DE734','Form10_DE737' ",
										
										"'Form10_DE729'", "'Form10_DE730'", "'Form10_DE731'",
										"'Form10_DE732'", "'Form10_DE733'", "'Form10_DE734'", "'Form10_DE735'", "'Form10_DE736'", "'Form10_DE737'",  
										"'Form10_DE666'", "'Form10_DE667'",
										"'Form10_DE1529'", "'Form10_DE669'", "'Form10_DE670'", "'Form10_DE671'", "'Form10_DE672'", "'Form10_DE673'", "'Form10_DE674'", "'Form10_DE675'", 
										"'Form10_DE676'",
										"'Form10_DE677'", "'Form10_DE678'", "'Form10_DE679'", "'Form10_DE680'", "'Form10_DE681'", "'Form10_DE682'", "'Form10_DE683'", "'Form10_DE684'", 
										"'Form10_DE685'",
										"'Form10_DE686'", "'Form10_DE687'", "'Form10_DE688'", "'Form10_DE689'", "'Form10_DE690'", "'Form10_DE691'", "'Form10_DE692'", "'Form10_DE693'", 
										"'Form10_DE694'",
										"'Form10_DE695'", "'Form10_DE696'", "'Form10_DE697'", "'Form10_DE698'", "'Form10_DE699'", "'Form10_DE700'", "'Form10_DE701'", "'Form10_DE702'", 
										"'Form10_DE703'",
										"'Form10_DE704'", "'Form10_DE705'", "'Form10_DE706'", "'Form10_DE707'", "'Form10_DE708'", "'Form10_DE709'", "'Form10_DE710'", "'Form10_DE711'", 
										"'Form10_DE712'",
										"'Form10_DE713'", "'Form10_DE714'", "'Form10_DE715'", "'Form10_DE716'", "'Form10_DE717'", "'Form10_DE718'", "'Form10_DE719'", 
										
										"' '","' '","' '",
										
										// 23.81
										" 'Form10_DE723','Form10_DE726' ",
										" 'Form10_DE724','Form10_DE727' ",
										" 'Form10_DE725','Form10_DE728' ",
										
										"'Form10_DE723'", "'Form10_DE724'", "'Form10_DE725'", "'Form10_DE726'", "'Form10_DE727'", 
										"'Form10_DE728'", 
										
										// 23.82
										" 'Form10_DE729','Form10_DE732','Form10_DE735' ",
										" 'Form10_DE730','Form10_DE733','Form10_DE736' ",
										" 'Form10_DE731','Form10_DE734','Form10_DE737' ",
										
										"'Form10_DE729'", "'Form10_DE730'", "'Form10_DE731'", "'Form10_DE732'", "'Form10_DE733'", "'Form10_DE734'", "'Form10_DE735'",
										"'Form10_DE736'", "'Form10_DE737'", 
										
										"' '", "' '", "' '", "' '","' '","' '","' '","' '","' '",
										
										"'Form10_DE741'", "'Form10_DE742'", 
										"'Form10_DE743'", "'Form10_DE744'", "'Form10_DE745'", "'Form10_DE746'", "'Form10_DE747'", "'Form10_DE748'", "'Form10_DE749'",
										
										
											

	  	  							   };

                 
 	int dataElementIDs[] = new int[dataElementCodes.length+5];  
	int entryNumberValues[]=  new int[dataElementCodes.length+5];	  
   	int entryValuesForLastYear[]= new int[dataElementCodes.length+5];      
   	int cumentryValuesForCurYear[]= new int[dataElementCodes.length+5];      
   	int cumentryValuesForLastYear[]= new int[dataElementCodes.length+5];      
      	
   	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }; 	   
   
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

     
        //rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE id ="+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE organisationunitid ="+selectedOrgUnitID);        
        if(rs1.next())  {   selectedOrgUnitName = rs1.getString(1);     }
        else   {     	selectedOrgUnitName = "";                   }  

        //rs11 = st11.executeQuery("select startDate,endDate from period where id = "+selectedDataPeriodID);
		//if(rs11.next())
		//  {
		//	selectedDataPeriodStartDate =  rs11.getDate(1).toString();
		//	selectedDataPeriodEndDate   =  rs11.getDate(2).toString();
		//  }
		
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
	String partsOfDataPeriodEndDate[] = endingDate.split("-");
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
//	        rs8=st8.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");        
//			if(rs8.next())  { PHCID = rs8.getInt(1);PHCName = rs8.getString(2);  } 
//			else  {  PHCID = 0; PHCName = "";  } 

//			rs9=st9.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+PHCID+")");	
//			if(rs9.next())  { CHCID = rs9.getInt(1);CHCName = rs9.getString(2);  } 
//			else  {  CHCID = 0; CHCName = "";  } 

			//rs5=st5.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
			rs5=st5.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");	
			if(rs5.next())  {  districtID = rs5.getInt(1); districtName = rs5.getString(2);}
			else  {districtID = 0; districtName = "";}
        
		    //rs6=st6.executeQuery("select organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+districtID+")");
		    rs6=st6.executeQuery("select organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+districtID+")"); 
		    if(rs6.next()) {  stateName = rs6.getString(1);}
			else { stateName = "";}      

			//rs10=st10.executeQuery("SELECT sum(datavalue.value) FROM organisationunit INNER JOIN (dataelement INNER JOIN datavalue ON dataelement.id = datavalue.dataElement) ON organisationunit.id = datavalue.source WHERE organisationunit.parent = "+PHCID+" AND dataelement.name like 'Total Population'");
			rs10=st10.executeQuery("SELECT sum(datavalue.value) FROM organisationunit INNER JOIN (dataelement INNER JOIN datavalue ON dataelement.dataelementid = datavalue.dataelementid) ON organisationunit.organisationunitid = datavalue.sourceid WHERE organisationunit.parentid = "+PHCID+" AND dataelement.name like 'Total Population'");
			if(rs10.next()) { totPHCPopulation = rs10.getInt(1);}
			else {totPHCPopulation = 0;}      
       
       }   // try block end		 
     catch(Exception e)  { out.println(e.getMessage());  }
     finally
       {
		 try
			  {
			    if(rs5!=null)  rs5.close();			if(st5!=null)  st5.close();
			    if(rs6!=null)  rs6.close();			if(st6!=null)  st6.close();
			    if(rs8!=null)  rs8.close();			if(st8!=null)  st8.close();   
			    if(rs9!=null)  rs9.close();			if(st9!=null)  st9.close();
			    if(rs10!=null)  rs10.close();		if(st10!=null)  st10.close();                                                                                                                
			  }
		catch(Exception e)   {  out.println(e.getMessage());   }
       }  // finally block end
    
     try
      {
		int i=0;    
		int j= dataElementCodes.length;
		String query = "";
		int in = 0;
			
		while(i!=j)
			{		
				entryNumberValues[i]	 = -1;
				entryValuesForLastYear[i] = -1;
				cumentryValuesForCurYear[i] = -1;
				cumentryValuesForLastYear[i] = -1;
																
				// for Performance in the reporting month
				//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in ( select id from organisationunit where parent ="+selectedOrgUnitID+" OR id ="+selectedOrgUnitID+") AND dataelement.code in ("+dataElementCodes[i]+")";
				query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in ( select organisationunitid from organisationunit where parentid ="+selectedOrgUnitID+" OR organisationunitid ="+selectedOrgUnitID+") AND dataelement.code in ("+dataElementCodes[i]+")"; 
				rs=st.executeQuery(query);
				if(rs.next())  {  entryNumberValues[i] = rs.getInt(1);  } 
				else  {  entryNumberValues[i] = 0;  } 
												
		        // for Cumulative Performance till Current Month
				//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodType = "+periodTypeID+")  AND datavalue.source in ( select id from organisationunit where parent ="+selectedOrgUnitID+" OR id ="+selectedOrgUnitID+")  AND dataelement.code in ("+dataElementCodes[i]+")";
				query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodtypeid = "+periodTypeID+")  AND datavalue.sourceid in ( select organisationunitid from organisationunit where parentid ="+selectedOrgUnitID+" OR organisationunitid ="+selectedOrgUnitID+")  AND dataelement.code in ("+dataElementCodes[i]+")";			    
			    rs4=st4.executeQuery(query);
     			if(rs4.next())  {  cumentryValuesForCurYear[i]= rs4.getInt(1);  } 
			    else  {  cumentryValuesForCurYear[i] = 0;  } 
			            
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
					if(rs12!=null)  rs12.close();	if(st12!=null)  st12.close();
										
					if(con!=null) con.close();					
		        }	 
			catch(Exception e)   {  out.println(e.getMessage());   }
       }  // finally block end	                     	        
%>

<HTML>
<HEAD>
   <TITLE>Form - 2</TITLE>
   <script src="../dhis-web-reports/Kerala/KDENamesForForm2.js" type="text/javascript" language="Javascript"></script> 	
   <script>
   		function fun1()
        	{ 
        	    
        		var start=0;
        		var end = 28;    
        		var j=1;
        		var k=0;             	
        		var id="";        		
        	
        		while(j<=5)
        		 {        		    
        		   	
        		   	if(j==1) end = 27;
					else if(j==2) end = 40;
					else if(j==3) end = 40;
					else if(j==4) end = 24;
					else if(j==5) end = 17;
        		   	
	   	 			for(start=0;start<=end;start++)
	   	 			 {	
					  	 id="cell1"+k;   	 			  
	   	 			  	 document.getElementById(id).innerHTML = slnoForForm2[k];
					  	 id="cell2"+k;   	 			  
	   	 			  	 document.getElementById(id).innerHTML = denamesForForm2[k];
	   	 			  	 
	   	 			  	 k++;
	   	 			 }
	   	 			j++; 
	   	 		 }	   	 			   	 		
	  		}
  	</script>   		    
</HEAD>
<BODY BGCOLOR="#FFFFFF" onload="fun1()">  
	<font face="Arial" size="2">(To be submitted by 15th March to the District 
    Family Welfare Officer for data-entry in NIC - District Computer</font>)<center>
		<font face="Arial" size="3">
			<b>FORM&nbsp; 2<br>PHC ACTION PLAN</b></font>
	</center>

		<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    			<td width="60%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="2">A. General information : &nbsp;&nbsp;&nbsp;</font>
    		</td>
    			<td width="40%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="2"></font>
    		</td>   
  		</tr>
 		<tr>
    		<td width="60%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" height="19">
       				<font face="Arial" size="2">State : &nbsp;&nbsp;<%=stateName%></font>
    		</td>
    		<td width="40%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
					<font face="Arial" size="2">Year : &nbsp;&nbsp;<%=partsOfDataPeriodStartDate[0]%> - <%=partsOfDataPeriodEndDate[0]%></font>
    		</td>
  		</tr>		
 		<tr>
    			<td width="60%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" height="19">
       				<font face="Arial" size="2">District : &nbsp;&nbsp;<%=districtName%></font>
    		</td>
    			<td width="40%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
       				<font face="Arial" size="2">Birth Rate : </font>
    		</td>
  		</tr>		
 		<tr>
    			<td width="60%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" height="19">
       				<font face="Arial" size="2">P.H.C. : &nbsp;&nbsp;<%=selectedOrgUnitName%></font>
    		</td>
    			<td width="40%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
       				<font face="Arial" size="2">(District / State) : </font>
    		</td>
  		</tr>		
 		<tr>
    			<td width="60%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" height="19">
       				<font face="Arial" size="2">No.of sub-centres under PHC. : </font>
    		</td>
    			<td width="40%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
       				<font face="Arial" size="2">Eligible couples : </font>
    		</td>
  		</tr>		
 		<tr>
    			<td width="60%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" height="19">
       				<font face="Arial" size="2">Population of PHC (Total of 
                    population under sub-centres) : </font>
    		</td>
    			<td width="40%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
       				&nbsp;
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
int bt =0; 	
 	while(j<=5)
 	 { 
 	   
 	    if(j==1) 
 	      { %>
 	       	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">
		   <%	 	      
 	       }
 	    else if(j==2)
 	      { %>
			<br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
        else if(j==3)
 	      { %>
			<br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
 		else if(j==4)
 	      { %>
			<br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
 		else if(j==5)
 	      { %>
			<br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
 	    else
 	      { %>
			<br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}

		if(j==1) endcount = 27; 
		else if(j==2) endcount = 40; 
		else if(j==3) endcount = 40;
		else if(j==4) endcount = 24;
		else if(j==5) endcount = 17;
								
				
 		for(i=0;i<=endcount;i++)
 	 	  {  	
 	     	String id1="cell1"+k;
 	     	String id2="cell2"+k;
 	     	 	    
 	     	if((k>=29 && k<=68) || (k>=70 && k<=88))
 	      	{ 
               if(k==29 || k==70) 
 	            {
 	              tempForentryNumberValues[0]= "<b>Male</b>";
 	              tempForentryNumberValues[1]= "<b>Female</b>";
				  tempForentryNumberValues[2]= "<b>Male</b>";
				  tempForentryNumberValues[3]= "<b>Female</b>";
 	              
 	              l=l+4;
 	            }
 	           else if(k==30 || k==31 || k==36 || k==37 || k==47 || k==50 || k==52 || k==54 || k==56 || k==57 || k==59 || k==63 || k==64 || k==71 || k==72 || k==77 || k==78 || k==81 || k==84)
 				{
 	              tempForentryNumberValues[0]= " ";
 	              tempForentryNumberValues[1]= " ";
				  tempForentryNumberValues[2]= " ";
				  tempForentryNumberValues[3]= " ";
  	              
 	              l=l+4;
 	            }
 	            
 	           else 
 	            {
   					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[0] = "";
 	         		else tempForentryNumberValues[0] = ""+temp;
 					
					l++;
						         
 	         		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[1] = "";
 	         		else tempForentryNumberValues[1] = ""+temp;
 	         							
 	         		l++;
 	         		
			 		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[2] = "";
 	         		else tempForentryNumberValues[2] = ""+temp;
 	         		
					l++;

			 		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[3] = "";
 	         		else tempForentryNumberValues[3] = ""+temp;
 	         		
					l++;

 	            } 
 	        %> 	        
 	   
	 	    	<tr>
		    			<td id="<%=id1%>" name="<%=id1%>" width="3%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; " bordercolor="#111111" align="left"><font face="Arial" size="2"></font></td>
    					<td id="<%=id2%>" name="<%=id2%>" width="67%" align="left" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ><font face="Arial" size="2"></font></td>
    					<td width="8%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" dir="ltr" colspan="2"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    					<td width="7%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" dir="ltr" colspan="3"><font face="Arial" size="2"><%=tempForentryNumberValues[1]%></font></td>
    					<td width="8%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="3"><font face="Arial" size="2"><%=tempForentryNumberValues[2]%></font></td>
    					<td width="7%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="2"><font face="Arial" size="2"><%=tempForentryNumberValues[3]%></font></td>
				</tr>
 	        
 	     <%   }
 	     	
 	     else if((k>=-1 && k<=-1))
 	      	{ 
               if(k==89) 
 	            { 
 	              %>
 	              </table>
 	              <center>
 	              <br><br>
 	              	<font face="arial" size=3><b>Materials &amp; Supplies</b></font>
 	              </center>	
		 	      	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">
 	              <%
 	              tempForentryNumberValues[0]= "<b>&#2351;&#2370;&#2344;&#2367;&#2335;</b>";
	 	             tempForentryNumberValues[1]= "<b>&#2327;&#2340; &#2357;&#2352;&#2381;&#2359; &#2325;&#2375; &#2342;&#2380;&#2352;&#2366;&#2344; &#2344;&#2367;&#2352;&#2381;&#2343;&#2366;&#2352;&#2367;&#2340; &#2325;&#2368; &#2327;&#2312; &#2310;&#2357;&#2358;&#2381;&#2351;&#2325;&#2340;&#2366;</b>";
					  tempForentryNumberValues[2]= "<b>&#2327;&#2340; &#2357;&#2352;&#2381;&#2359; &#2346;&#2381;&#2352;&#2366;&#2346;&#2381;&#2340; &#2357;&#2366;&#2360;&#2381;&#2340;&#2357;&#2367;&#2325; &#2350;&#2366;&#2340;&#2381;&#2352;&#2366;</b>";
				  tempForentryNumberValues[3]= "<b>&#2327;&#2340; &#2357;&#2352;&#2381;&#2359; &#2325;&#2366; &#2358;&#2375;&#2359; &#2351;&#2366; &#2325;&#2350;&#2368;</b>";
					  tempForentryNumberValues[4]= "<b>&#2330;&#2366;&#2354;&#2370; &#2357;&#2352;&#2381;&#2359; &#2325;&#2375; &#2354;&#2367;&#2319; &#2310;&#2357;&#2358;&#2381;&#2351;&#2325;&#2340;&#2366;</b>";
 	              
 	              l=l+5;
 	            }
 	           else if(k==1 || k==3 || k==6 || k==12 || k==14 || k==18 || k==22 || k==23 || k==26 || k==30 || k==31 || k==36 || k==37 || k==47 || k==50 || k==52 || k==54 || k==56 || k==58 || k==59 || k==63 || k==64 || k==69 || k==70 || k==75 || k==76 || k==79 || k==82 || k==88 || k==94 || k==101 || k==106)
 				{
 	              tempForentryNumberValues[0]= " ";
 	              tempForentryNumberValues[1]= " ";
				  tempForentryNumberValues[2]= " ";
				  tempForentryNumberValues[3]= " ";
				  tempForentryNumberValues[4]= " ";
  	              
 	              l=l+5;
 	            }
 	            
 	           else 
 	            {
   					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[0] = "";
 	         		else tempForentryNumberValues[0] = ""+temp;
 					
					l++;
						         
 	         		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[1] = "";
 	         		else tempForentryNumberValues[1] = ""+temp;
 	         							
 	         		l++;
 	         		
			 		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[2] = "";
 	         		else tempForentryNumberValues[2] = ""+temp;
 	         		
					l++;

			 		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[3] = "";
 	         		else tempForentryNumberValues[3] = ""+temp;
 	         		
					l++;

			 		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[4] = "";
 	         		else tempForentryNumberValues[4] = ""+temp;
 	         		
					l++;
 	            } 
 	        %> 	        
 	   
	 	    	<tr>
		    			<td id="<%=id1%>" name="<%=id1%>" width="3%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; " bordercolor="#111111" align="left"><font face="Arial" size="2"></font></td>
    					<td id="<%=id2%>" name="<%=id2%>" width="67%" align="left" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ><font face="Arial" size="2"></font></td>
    					<td width="6%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" dir="ltr"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    					<td width="6%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" dir="ltr" colspan="3"><font face="Arial" size="2"><%=tempForentryNumberValues[1]%></font></td>
    					<td width="6%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" dir="ltr" colspan="2"><font face="Arial" size="2"><%=tempForentryNumberValues[2]%></font></td>
    					<td width="6%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" dir="ltr" colspan="3"><font face="Arial" size="2"><%=tempForentryNumberValues[3]%></font></td>
    					<td width="6%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" dir="ltr"><font face="Arial" size="2"><%=tempForentryNumberValues[4]%></font></td>
				</tr>
 	        
 	   <%   }

 	     else if((k>=88 && k<=109) || (k>=135 && k<=152))
 	      	{ 
               if(k==135) 
 	            { %>
 	             	              
 	              	<br>
 	              	<font face="arial" size=3><b>Staff Position</b></font> 
 	              	<%
 	              	tempForentryNumberValues[0]= "<b>Number<br>Sanctioned</b>";
 	              	tempForentryNumberValues[1]= "<b>Number in<br>position</b>";
				  	tempForentryNumberValues[2]= "<b>Number vacant<br>since that date</b>";
 	              
 	              l=l+3;
 	            }
               else if(k==89) 
 	            {
 	                %>
			        </table> 	              
 	              	<br>
 	              	<font face="arial" size=3><b>Materials & Supplies</b></font> 	              	
	     	       	 <table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">
 	              	
 	              	<%
 	              	tempForentryNumberValues[0]= "<b>Quantity used<br>in previous<br>year</b>";
 	              	tempForentryNumberValues[1]= "<b>Stock<br>position on<br>1st April</b>";
  				  	  tempForentryNumberValues[2]= "<b>Additional <br>Additional<br>quantity required<br>in current year</b>";
 	              
 	              l=l+3;
 	            } 	            
 	           else if(k==90 || k==96 || k==103 || k==108)
 				{
 	              tempForentryNumberValues[0]= " ";
 	              tempForentryNumberValues[1]= " ";
				  tempForentryNumberValues[2]= " ";
  	              
 	              l=l+3;
 	            }
 	            
 	           else 
 	            {
   					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[0] = "";
 	         		else tempForentryNumberValues[0] = ""+temp;
 					
					l++;
						         
 	         		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[1] = "";
 	         		else tempForentryNumberValues[1] = ""+temp;
 	         							
 	         		l++;
 	         		
			 		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[2] = "";
 	         		else tempForentryNumberValues[2] = ""+temp;
 	         		
					l++;

 	            } 
 	        %> 	        
 	   
	 	    	<tr>
		    			<td id="<%=id1%>" name="<%=id1%>" width="3%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; " bordercolor="#111111" align="left"><font face="Arial" size="2"></font></td>
    					<td id="<%=id2%>" name="<%=id2%>" width="67%" align="left" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ><font face="Arial" size="2"></font></td>
    					<td width="10%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" dir="ltr" colspan="4"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    					<td width="10%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" dir="ltr" colspan="5"><font face="Arial" size="2"><%=tempForentryNumberValues[1]%></font></td>
    					<td width="10%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" dir="ltr" colspan="4"><font face="Arial" size="2"><%=tempForentryNumberValues[2]%></font></td>
				</tr>
 	   <%   }

 	     else 
 	      {   	       			  
			  if(k==0)
			    {
			      	tempForentryNumberValues[0]= "<font face='arial' size=3><b>Performance <br>last year</b></font>";
              	  	tempForentryNumberValues[1]= "<font face='arial' size=3><b>Planned<br>performance in<br>current year<br>complied from<br>sub-centre plans</b></font>";
 	              
 	              l=l+2;
			    }			    
			  else if(k==28 || k==69)
			    {
			      	tempForentryNumberValues[0]= "<font face='arial' size=3><b>Performance in<br>Last year</b></font>";
              	  	tempForentryNumberValues[1]= "<font face='arial' size=3><b>Expected Requirement<br>for current year</b></font>";
 	              
 	              l=l+2;
			    }			    
              else if(k==110) 
 	            { %>
			       </table> 	              
 	              	<br>
 	              	<font face="arial" size=3><b>Equipments &amp; Facilities<br>1.PHC Building Owned or Rented :</b></font> 	              	
	     	       	 <table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">
				  
				  <%	
				 	tempForentryNumberValues[0]= "<b>No.of Available</b>";
 	              	tempForentryNumberValues[1]= "<b>No.of Functioning</b>";
 	              
 	              l=l+2;
 	            }
 	           else if(k==1 || k==3 || k==6 || k==12 || k==14 || k==18 || k==22 || k==23 || k==26 || k==30 || k==31 || k==36 || k==37 || k==47 || k==50 || k==52 || k==54 || k==56 || k==58 || k==59 || k==63 || k==64 || k==69 || k==70 || k==75 || k==76 || k==79 || k==82 || k==88 || k==94 || k==101 || k==106)
				{
			      	tempForentryNumberValues[0]= " ";
              	  	tempForentryNumberValues[1]= " ";
 	              
 	              	l=l+2;				
				}
		 	 else 
 	          {
 					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[0] = "";
 	         		else tempForentryNumberValues[0] = ""+temp;
					
					l++;
					
 					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[1] = "";
 	         		else tempForentryNumberValues[1] = ""+temp;
 	         		 					
					l++;
 	          }	
 	       %>
	 	    	<tr>
		    			<td id="<%=id1%>" name="<%=id1%>" width="3%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; " bordercolor="#111111" align="left"><font face="Arial" size="2"></font></td>
    					<td id="<%=id2%>" name="<%=id2%>" width="67%" align="left" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ><font face="Arial" size="2"></font></td>
    					<td width="15%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" dir="ltr" colspan="5"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    					<td width="15%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="5"><font face="Arial" size="2"><%=tempForentryNumberValues[1]%></font></td>
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