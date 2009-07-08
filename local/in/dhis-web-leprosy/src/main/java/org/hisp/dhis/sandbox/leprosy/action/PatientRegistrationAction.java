package org.hisp.dhis.sandbox.leprosy.action;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.TreeSet;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.sandbox.leprosy.NLEPDataElements;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Program;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;

import com.opensymphony.xwork.Action;

public class PatientRegistrationAction implements Action {

    // <editor-fold defaultstate="collapsed" desc="TRC Card Fields">
    private String givenName;
    private String middleName;
    private String familyName;
    private String resAddress1;
    private String resAddress2;
    private String cityVillage;
    private String zipCode;
    private String region;
    private String subRegion;
    private String division;
    private String dob;
    private String gender;
    private String district;
    private String phc;
    private String category;
    private String diseaseType;
    private String detectionMode;
    private String caseDetection;
    private String regDate;
    private String firstDose;
    private String relapseSelect;
    private String contacts;
    private String dateRFT;
    private String voidedReason;
    private String deformity;
    private String deformityTime;
    private String deformityType;
    private String rcsEligible;
    private String rcsReferred;
    private String rcsPart;
    private String rcsDate;
    private String rcsCenterName;
    private String rcsRepeat;
    private String rcsPatientBPL;
    private String rcsReimbursementAmt;
    private String disabilityMDT;
    private String reactionTime;
    private String reactionMgmtAt;
    private String treatmentStartDate;
    private String treatmentEndDate;
    private String servicesGiven;
    private String socioEconomicServices;
    private String jobType;
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient Location Accessors">
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPhc() {
        return phc;
    }

    public void setPhc(String phc) {
        this.phc = phc;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient Name Accessors">
    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient Address Accessors">
    public String getResAddress1() {
        return resAddress1;
    }

    public void setResAddress1(String resAddress1) {
        this.resAddress1 = resAddress1;
    }

    public String getResAddress2() {
        return resAddress2;
    }

    public void setResAddress2(String resAddress2) {
        this.resAddress2 = resAddress2;
    }

    public String getCityVillage() {
        return cityVillage;
    }

    public void setCityVillage(String cityVillage) {
        this.cityVillage = cityVillage;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient Residential Status Accessors">
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSubRegion() {
        return subRegion;
    }

    public void setSubRegion(String subRegion) {
        this.subRegion = subRegion;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient DOB Accessors">
    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient Gender Accessors">
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient Caste Category Accessors">
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient DiseaseType Accessors">
    public String getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType(String diseaseType) {
        this.diseaseType = diseaseType;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Detected By (detectionMode) Accessors">
    public String getDetectionMode() {
        return detectionMode;
    }

    public void setDetectionMode(String detectionMode) {
        this.detectionMode = detectionMode;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Program Registration Date (regDate) Accessors">
    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's First Encounter Date (firstDose) Accessors">
    public String getFirstDose() {
        return firstDose;
    }

    public void setFirstDose(String firstDose) {
        this.firstDose = firstDose;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's 1st CaseDetection (caseDetection, relapseSelect) Accessors">
    public String getCaseDetection() {
        return caseDetection;
    }

    public void setCaseDetection(String caseDetection) {
        this.caseDetection = caseDetection;
    }

    public String getRelapseSelect() {
        return relapseSelect;
    }

    public void setRelapseSelect(String relapseSelect) {
        this.relapseSelect = relapseSelect;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Contacts Accessors">
    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Date of Completion of Treatment (dateRFT)">
    public String getDateRFT() {
        return dateRFT;
    }

    public void setDateRFT(String dateRFT) {
        this.dateRFT = dateRFT;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Leprosy Program Voided Reason">
    public String getVoidedReason() {
        return voidedReason;
    }

    public void setVoidedReason(String voidedReason) {
        this.voidedReason = voidedReason;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Deformity Grade(deformity, deformityTime) Accessors">
    public String getDeformity() {
        return deformity;
    }

    public void setDeformity(String deformity) {
        this.deformity = deformity;
    }

    public String getDeformityTime() {
        return deformityTime;
    }

    public void setDeformityTime(String deformityTime) {
        this.deformityTime = deformityTime;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Deformity in Organ(deformityType) Accessors">
    public String getDeformityType() {
        return deformityType;
    }

    public void setDeformityType(String deformityType) {
        this.deformityType = deformityType;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's RCS (rcsEligible, rcsReferred, rcsPart, rcsDate, rcsCenterName, rcsRepeat, rcsPatientBPL, rcsReimbursementAmt) Accessors">
    public String getRcsDate() {
        return rcsDate;
    }

    public void setRcsDate(String rcsDate) {
        this.rcsDate = rcsDate;
    }

    public String getRcsEligible() {
        return rcsEligible;
    }

    public void setRcsEligible(String rcsEligible) {
        this.rcsEligible = rcsEligible;
    }

    public String getRcsPart() {
        return rcsPart;
    }

    public void setRcsPart(String rcsPart) {
        this.rcsPart = rcsPart;
    }

    public String getRcsReferred() {
        return rcsReferred;
    }

    public void setRcsReferred(String rcsReferred) {
        this.rcsReferred = rcsReferred;
    }

    public String getRcsCenterName() {
        return rcsCenterName;
    }

    public void setRcsCenterName(String rcsCenterName) {
        this.rcsCenterName = rcsCenterName;
    }

    public String getRcsRepeat() {
        return rcsRepeat;
    }

    public void setRcsRepeat(String rcsRepeat) {
        this.rcsRepeat = rcsRepeat;
    }

    public String getRcsPatientBPL() {
        return rcsPatientBPL;
    }

    public void setRcsPatientBPL(String rcsPatientBPL) {
        this.rcsPatientBPL = rcsPatientBPL;
    }

    public String getRcsReimbursementAmt() {
        return rcsReimbursementAmt;
    }

    public void setRcsReimbursementAmt(String rcsReimbursementAmt) {
        this.rcsReimbursementAmt = rcsReimbursementAmt;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Disability After MDT (disabilityMDT) Accessors">
    public String getDisabilityMDT() {
        return disabilityMDT;
    }

    public void setDisabilityMDT(String disabilityMDT) {
        this.disabilityMDT = disabilityMDT;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Reaction & Management (reactionTime, reactionMgmtAt) Accessors">
    public String getReactionMgmtAt() {
        return reactionMgmtAt;
    }

    public void setReactionMgmtAt(String reactionMgmtAt) {
        this.reactionMgmtAt = reactionMgmtAt;
    }

    public String getReactionTime() {
        return reactionTime;
    }

    public void setReactionTime(String reactionTime) {
        this.reactionTime = reactionTime;
    }

    public String getTreatmentEndDate() {
        return treatmentEndDate;
    }

    public void setTreatmentEndDate(String treatmentEndDate) {
        this.treatmentEndDate = treatmentEndDate;
    }

    public String getTreatmentStartDate() {
        return treatmentStartDate;
    }

    public void setTreatmentStartDate(String treatmentStartDate) {
        this.treatmentStartDate = treatmentStartDate;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Socio-economic Services (servicesGiven, socioEconomicServices, jobType) Accessors">
    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getServicesGiven() {
        return servicesGiven;
    }

    public void setServicesGiven(String servicesGiven) {
        this.servicesGiven = servicesGiven;
    }

    public String getSocioEconomicServices() {
        return socioEconomicServices;
    }

    public void setSocioEconomicServices(String socioEconomicServices) {
        this.socioEconomicServices = socioEconomicServices;
    }
    //</editor-fold>

    public void getOpenmrsContext() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(System.getenv("DHIS2_HOME") + "/hibernate.properties"));
            String user = props.getProperty("hibernate.connection.username");
            String pass = props.getProperty("hibernate.connection.password");
            Context.startup("jdbc:mysql://localhost:3306/openmrs_leprosy?autoReconnect=true", user, pass, new Properties());
            Context.openSession();
            Context.authenticate("admin", "test");
            System.out.println("NLEP>>>Context Session Opened");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String execute() throws Exception {
        
        orgUnit = organisationUnitService.getOrganisationUnit( parentouIDTB );
        
        period = getCurrentMonthlyPeriod();
        
        try {
            if (givenName != null) {
                getOpenmrsContext();

                //Get PatientService and Patient.
                PatientService patientService = Context.getPatientService();
                
                Patient leprosyPatient = new Patient();

                // <editor-fold defaultstate="collapsed" desc="Add Identifier to LeprosyPatient">
                Location location = new Location(1);
                PatientIdentifierType identType = patientService.getPatientIdentifierType(1);
                Date date = new Date();
                int year = date.getYear() + 1900;
                String idWithoutCheckdigit = "MH" + district.toUpperCase()+ phc.toUpperCase() + year;
                System.out.println("NLEP>>>ID Without CheckDigit = " + idWithoutCheckdigit);
                int checkedDigit = checkDigit(idWithoutCheckdigit);
                System.out.println("NLEP>>>CheckDigit = " + checkedDigit);
                String patientIdentifierString = idWithoutCheckdigit + '-' + (Integer.toString(checkedDigit));
                System.out.println("NLEP>>>Patient Identifier = " + patientIdentifierString);
                PatientIdentifier patientIdentifier = new PatientIdentifier(patientIdentifierString, identType, location);
                leprosyPatient.addIdentifier(patientIdentifier);
                System.out.println("NLEP>>>Identifier = " + patientIdentifier + " added to patient");
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Add Name to LeprosyPatient">
                PersonName name = new PersonName();
                name.setGivenName(givenName);
                name.setMiddleName(middleName);
                name.setFamilyName(familyName);
                leprosyPatient.addName(name);
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Add Address to LeprosyPatient">
                PersonAddress address = new PersonAddress();
                address.setAddress1(resAddress1);
                address.setAddress2(resAddress2);
                address.setCityVillage(cityVillage);
                address.setPostalCode(zipCode);
                address.setRegion(region);
                address.setSubregion(subRegion);
                address.setTownshipDivision(division);
                leprosyPatient.addAddress(address);
                System.out.println("NLEP>>>Address = " + address + " added to patient");
                System.out.println("NLEP>>>Addresses set on patient");
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Add Birthdate & Gender to LeprosyPatient">
                SimpleDateFormat myDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                leprosyPatient.setBirthdate(myDateFormat.parse(dob));
                leprosyPatient.setGender(gender);
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Add Attributes (CasteCategory) to LeprosyPatient">
                PersonAttribute casteCategory = new PersonAttribute(new PersonAttributeType(8), category);
                TreeSet attribSet = new TreeSet();
                attribSet.add(casteCategory);
                leprosyPatient.setAttributes(attribSet);
                System.out.println("NLEP>>>Category = " + category + " is set on patient");
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Save LeprosyPatient">
                System.out.println("NLEP>>>Now Saving...");
                patientService.savePatient(leprosyPatient);
                System.out.println("NLEP>>>Saved Successfully");
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Add DiseaseType, regDate, detectionMode and voidedReason to Enroll LeprosyPatient">
                ProgramWorkflowService progService = Context.getProgramWorkflowService();
                Program program = new Program();
                if (diseaseType.equals("PB")) {
                    program = progService.getProgram(5);
                } else if (diseaseType.equals("MB")) {
                    program = progService.getProgram(6);
                }
                PatientProgram leprosyPatientProg = new PatientProgram();
                leprosyPatientProg.setProgram(program);
                leprosyPatientProg.setPatient(leprosyPatient);
                leprosyPatientProg.setDateEnrolled(myDateFormat.parse(regDate));
                leprosyPatientProg.setDateCompleted(myDateFormat.parse(dateRFT));
                UserService userService = Context.getUserService();
                leprosyPatientProg.setCreator(userService.getUserByUsername("DEFAULT" + detectionMode));
                if (!(voidedReason.equals(""))) {
                    leprosyPatientProg.setVoided(true);
                    leprosyPatientProg.setVoidReason(voidedReason);
                }
                progService.savePatientProgram(leprosyPatientProg);
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Add Initial Encounter (firstDose, caseDetection, relapseSelect, contacts) to LeprosyPatient">
                EncounterService encService = Context.getEncounterService();
                EncounterType encType = new EncounterType();
                if (leprosyPatient.getAge() <= 14) {
                    encType = encService.getEncounterType(3);
                } else {
                    encType = encService.getEncounterType(1);
                }
                Encounter initEnc = new Encounter();
                initEnc.setEncounterDatetime(myDateFormat.parse(firstDose));
                initEnc.setEncounterType(encType);
                // <editor-fold defaultstate="collapsed" desc="caseDetection Observation for Initial Encounter">
                Obs caseDetectionObs = new Obs();
                caseDetectionObs.setPerson(leprosyPatient);
                caseDetectionObs.setConcept(new Concept(1301));
                caseDetectionObs.setLocation(new Location(1));
                if (caseDetection.equals("RELAPSE")) {
                    caseDetectionObs.setValueText(caseDetection + "#" + relapseSelect);
                } else {
                    caseDetectionObs.setValueText(caseDetection);
                }
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="contacts Observation for Initial Encounter">
                Obs contactsObs = new Obs();
                contactsObs.setPerson(leprosyPatient);
                contactsObs.setConcept(new Concept(6102));
                contactsObs.setLocation(new Location(1));
                contactsObs.setValueText(contacts);
                //</editor-fold>
                initEnc.addObs(caseDetectionObs);
                initEnc.addObs(contactsObs);
                initEnc.setPatient(leprosyPatient);
                initEnc.setLocation(new Location(1));
                initEnc.setProvider(userService.getUserByUsername("DEFAULT" + detectionMode));
                encService.saveEncounter(initEnc);
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Deformity Encounters (deformity, deformityTime, deformityType)">
                if (leprosyPatient.getAge() <= 14) {
                    encType = encService.getEncounterType(4);
                } else {
                    encType = encService.getEncounterType(2);
                }
                Encounter deformityEnc = new Encounter();
                deformityEnc.setEncounterDatetime(new Date());
                deformityEnc.setEncounterType(encType);

                // <editor-fold defaultstate="collapsed" desc="Deformity Grade (deformity, deformityTime) Observation for Deformity Encounters">
                Obs deformityObs = new Obs();
                deformityObs.setPerson(leprosyPatient);
                deformityObs.setConcept(new Concept(6103));
                deformityObs.setLocation(new Location(1));
                deformityObs.setValueText(deformity + "#" + deformityTime);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Deformity In Part (deformityType) Observation for Deformity Encounters">
                Obs deformityTypeObs = new Obs();
                deformityTypeObs.setPerson(leprosyPatient);
                deformityTypeObs.setConcept(new Concept(6103));
                deformityTypeObs.setLocation(new Location(1));
                deformityTypeObs.setValueText(deformityType);
                //</editor-fold>

                deformityEnc.addObs(deformityObs);
                deformityEnc.addObs(deformityTypeObs);
                deformityEnc.setPatient(leprosyPatient);
                deformityEnc.setLocation(new Location(1));
                deformityEnc.setProvider(Context.getAuthenticatedUser());
                encService.saveEncounter(deformityEnc);
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="RCS Encounters (rcsEligible, rcsReferred, rcsPart, rcsDate, rcsCenterName, rcsRepeat)">
                if (leprosyPatient.getAge() <= 14) {
                    encType = encService.getEncounterType(4);
                } else {
                    encType = encService.getEncounterType(2);
                }
                Encounter rcsEnc = new Encounter();
                rcsEnc.setEncounterDatetime(new Date());
                rcsEnc.setEncounterType(encType);

                // <editor-fold defaultstate="collapsed" desc="RCS Eligible (rcsEligible) Observation for RCS Encounters">
                Obs rcsEligibleObs = new Obs();
                rcsEligibleObs.setPerson(leprosyPatient);
                rcsEligibleObs.setConcept(new Concept(6104));
                rcsEligibleObs.setLocation(new Location(1));
                rcsEligibleObs.setValueText("ELIGIBLE" + "#" + rcsEligible);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="RCS Referred (rcsReferred) Observation for RCS Encounters">
                Obs rcsReferredObs = new Obs();
                rcsReferredObs.setPerson(leprosyPatient);
                rcsReferredObs.setConcept(new Concept(6104));
                rcsReferredObs.setLocation(new Location(1));
                rcsReferredObs.setValueText("REFERRED" + "#" + rcsReferred);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="RCS Done on Part (rcsPart) Observation for RCS Encounters">
                Obs rcsPartObs = new Obs();
                rcsPartObs.setPerson(leprosyPatient);
                rcsPartObs.setConcept(new Concept(6104));
                rcsPartObs.setLocation(new Location(1));
                rcsPartObs.setValueText("PARTS" + "#" + rcsPart);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="RCS Done on Date (rcsDate) Observation for RCS Encounters">
                Obs rcsDateObs = new Obs();
                rcsDateObs.setPerson(leprosyPatient);
                rcsDateObs.setConcept(new Concept(6104));
                rcsDateObs.setLocation(new Location(1));
                rcsDateObs.setValueText("DATE" + "#" + rcsDate);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="RCS Center Name (rcsCenterName) Observation for RCS Encounters">
                Obs rcsCenterNameObs = new Obs();
                rcsCenterNameObs.setPerson(leprosyPatient);
                rcsCenterNameObs.setConcept(new Concept(6104));
                rcsCenterNameObs.setLocation(new Location(1));
                rcsCenterNameObs.setValueText("CENTER_NAME" + "#" + rcsCenterName);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="RCS Repeated on Parts (rcsRepeat) Observation for RCS Encounters">
                Obs rcsRepeatObs = new Obs();
                rcsRepeatObs.setPerson(leprosyPatient);
                rcsRepeatObs.setConcept(new Concept(6104));
                rcsRepeatObs.setLocation(new Location(1));
                rcsRepeatObs.setValueText("REPEAT_PARTS" + "#" + rcsRepeat);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="RCS Patient Below Poverty Line (rcsPatientBPL) Observation for RCS Encounters">
                Obs rcsPatientBPLObs = new Obs();
                rcsPatientBPLObs.setPerson(leprosyPatient);
                rcsPatientBPLObs.setConcept(new Concept(6104));
                rcsPatientBPLObs.setLocation(new Location(1));
                rcsPatientBPLObs.setValueText("BPL" + "#" + rcsPatientBPL);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="RCS Reimbursement Amount (rcsReimbursementAmt) Observation for RCS Encounters">
                Obs rcsReimbursementAmtObs = new Obs();
                rcsReimbursementAmtObs.setPerson(leprosyPatient);
                rcsReimbursementAmtObs.setConcept(new Concept(6104));
                rcsReimbursementAmtObs.setLocation(new Location(1));
                rcsReimbursementAmtObs.setValueText("REIMBURSEMENT_AMT" + "#" + rcsReimbursementAmt);
                //</editor-fold>

                rcsEnc.addObs(rcsEligibleObs);
                rcsEnc.addObs(rcsReferredObs);
                rcsEnc.addObs(rcsPartObs);
                rcsEnc.addObs(rcsDateObs);
                rcsEnc.addObs(rcsCenterNameObs);
                rcsEnc.addObs(rcsRepeatObs);
                rcsEnc.addObs(rcsPatientBPLObs);
                rcsEnc.addObs(rcsReimbursementAmtObs);
                rcsEnc.setPatient(leprosyPatient);
                rcsEnc.setLocation(new Location(1));
                rcsEnc.setProvider(Context.getAuthenticatedUser());
                encService.saveEncounter(rcsEnc);
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Disability for MDT Encounters (disabilityMDT)">
                if (leprosyPatient.getAge() <= 14) {
                    encType = encService.getEncounterType(4);
                } else {
                    encType = encService.getEncounterType(2);
                }
                Encounter disabilityEnc = new Encounter();
                disabilityEnc.setEncounterDatetime(new Date());
                disabilityEnc.setEncounterType(encType);

                // <editor-fold defaultstate="collapsed" desc="Disability after MDT (disabilityMDT) Observation for Disability Encounters">
                Obs disabilityMDTObs = new Obs();
                disabilityMDTObs.setPerson(leprosyPatient);
                disabilityMDTObs.setConcept(new Concept(6103));
                disabilityMDTObs.setLocation(new Location(1));
                disabilityMDTObs.setValueText("NEW_DISABILITIES" + "#" + disabilityMDT);
                //</editor-fold>

                disabilityEnc.addObs(disabilityMDTObs);
                disabilityEnc.setPatient(leprosyPatient);
                disabilityEnc.setLocation(new Location(1));
                disabilityEnc.setProvider(Context.getAuthenticatedUser());
                encService.saveEncounter(disabilityEnc);
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Reaction Encounters (reactionTime, reactionMgmtAt, treatmentStartDate, treatmentEndDate)">
                if (leprosyPatient.getAge() <= 14) {
                    encType = encService.getEncounterType(4);
                } else {
                    encType = encService.getEncounterType(2);
                }
                Encounter reactionEnc = new Encounter();
                reactionEnc.setEncounterDatetime(new Date());
                reactionEnc.setEncounterType(encType);

                // <editor-fold defaultstate="collapsed" desc="When the Reaction Happened (reactionTime) Observation for Reaction Encounters">
                Obs reactionTimeObs = new Obs();
                reactionTimeObs.setPerson(leprosyPatient);
                reactionTimeObs.setConcept(new Concept(6105));
                reactionTimeObs.setLocation(new Location(1));
                reactionTimeObs.setValueText("OCCURANCE" + "#" + reactionTime);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Where Reaction Managed (reactionMgmtAt) Observation for Reaction Encounters">
                Obs reactionMgmtAtObs = new Obs();
                reactionMgmtAtObs.setPerson(leprosyPatient);
                reactionMgmtAtObs.setConcept(new Concept(6105));
                reactionMgmtAtObs.setLocation(new Location(1));
                reactionMgmtAtObs.setValueText("MANAGED_AT" + "#" + reactionMgmtAt);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Date of Starting Treatment (treatmentStartDate) Observation for Reaction Encounters">
                Obs treatmentStartObs = new Obs();
                treatmentStartObs.setPerson(leprosyPatient);
                treatmentStartObs.setConcept(new Concept(6105));
                treatmentStartObs.setLocation(new Location(1));
                treatmentStartObs.setValueText("TREATMENT_START_DATE" + "#" + treatmentStartDate);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Date of Ending Treatment (treatmentEndDate) Observation for Reaction Encounters">
                Obs treatmentEndObs = new Obs();
                treatmentEndObs.setPerson(leprosyPatient);
                treatmentEndObs.setConcept(new Concept(6105));
                treatmentEndObs.setLocation(new Location(1));
                treatmentEndObs.setValueText("TREATMENT_END_DATE" + "#" + treatmentEndDate);
                //</editor-fold>

                reactionEnc.addObs(reactionTimeObs);
                reactionEnc.addObs(reactionMgmtAtObs);
                reactionEnc.addObs(treatmentStartObs);
                reactionEnc.addObs(treatmentEndObs);
                reactionEnc.setPatient(leprosyPatient);
                reactionEnc.setLocation(new Location(1));
                reactionEnc.setProvider(Context.getAuthenticatedUser());
                encService.saveEncounter(reactionEnc);
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Services Encounters (serviceGiven, socioEconomicServices, jobType)">
                if (leprosyPatient.getAge() <= 14) {
                    encType = encService.getEncounterType(4);
                } else {
                    encType = encService.getEncounterType(2);
                }
                Encounter servicesEnc = new Encounter();
                servicesEnc.setEncounterDatetime(new Date());
                servicesEnc.setEncounterType(encType);

                // <editor-fold defaultstate="collapsed" desc="Services Given (servicesGiven) Observation for Services Encounters">
                Obs servicesGivenObs = new Obs();
                servicesGivenObs.setPerson(leprosyPatient);
                servicesGivenObs.setConcept(new Concept(6105));
                servicesGivenObs.setLocation(new Location(1));
                servicesGivenObs.setValueText("SERVICES_GIVEN" + "#" + servicesGiven);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Socio-Economic Services Given (socioEconomicServices) Observation for Services Encounters">
                Obs socioEconomicServicesObs = new Obs();
                socioEconomicServicesObs.setPerson(leprosyPatient);
                socioEconomicServicesObs.setConcept(new Concept(6105));
                socioEconomicServicesObs.setLocation(new Location(1));
                socioEconomicServicesObs.setValueText("SOCIOECONOMIC_SERVICES" + "#" + reactionMgmtAt);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="JobType (jobType) Observation for Services Encounters">
                Obs jobTypeObs = new Obs();
                jobTypeObs.setPerson(leprosyPatient);
                jobTypeObs.setConcept(new Concept(6105));
                jobTypeObs.setLocation(new Location(1));
                jobTypeObs.setValueText("JOBTYPE" + "#" + treatmentStartDate);
                //</editor-fold>

                servicesEnc.addObs(servicesGivenObs);
                servicesEnc.addObs(socioEconomicServicesObs);
                servicesEnc.addObs(jobTypeObs);
                servicesEnc.setPatient(leprosyPatient);
                servicesEnc.setLocation(new Location(1));
                servicesEnc.setProvider(Context.getAuthenticatedUser());
                encService.saveEncounter(servicesEnc);
                //</editor-fold>

                Context.closeSession();
                System.out.println("NLEP>>>Close Context Session");
            }
        } finally {
        }
       
        return SUCCESS;
    }

    public int checkDigit(String idWithoutCheckdigit) throws InvalidIdentifierException {
        System.out.println("NLEP>>>inside checkDigit");
        // allowable characters within identifier
        String validChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_"; // MHMUMBAITHANE1096

        // remove leading or trailing whitespace, convert to uppercase
        idWithoutCheckdigit = idWithoutCheckdigit.trim().toUpperCase();

        // this will be a running total
        int sum = 0;

        // loop through digits from right to left
        for (int i = 0; i < idWithoutCheckdigit.length(); i++) {
            //set ch to "current" character to be processed
            char ch = idWithoutCheckdigit.charAt(idWithoutCheckdigit.length() - i - 1);

            // throw exception for invalid characters
            if (validChars.indexOf(ch) == -1) {
                throw new InvalidIdentifierException("\"" + ch + "\" is an invalid character");
            }
            // our "digit" is calculated using ASCII value - 48
            int digit = (int) ch - 48;

            // weight will be the current digit's contribution to the running total
            int weight;
            if (i % 2 == 0) {
                // for alternating digits starting with the rightmost, we use our formula this is the same as multiplying x 2 and
                // adding digits together for values 0 to 9.  Using the following formula allows us to gracefully calculate a
                // weight for non-numeric "digits" as well (from their ASCII value - 48).
                weight = (2 * digit) - (int) (digit / 5) * 9;

            } else {
                // even-positioned digits just contribute their ascii value minus 48
                weight = digit;
            }
            // keep a running total of weights
            sum += weight;
        }
        // avoid sum less than 10 (if characters below "0" allowed, this could happen)
        sum = Math.abs(sum) + 10;

        // check digit is amount needed to reach next number divisible by ten
        return (10 - (sum % 10)) % 10;
    }

    
    private Period getCurrentMonthlyPeriod()
    {
        Calendar calendar1 = Calendar.getInstance();
        Date sysDate = new Date();
        calendar1.setTime(sysDate);
        
        MonthlyPeriodType periodType = new MonthlyPeriodType();
        
        Collection<Period> periodList = periodService.getIntersectingPeriodsByPeriodType( periodType, sysDate, sysDate );
        if( periodList == null )
        {
            System.out.println("PeriodList is null");
            return null;
        }
        
        return periodList.iterator().next();
    }
    
    
    private void saveData(String deString) {
        String partsOfdeString[] = deString.split(":");

        int dataElementId = Integer.parseInt(partsOfdeString[0]);
        int optionComboId = Integer.parseInt(partsOfdeString[1]);

        DataElement dataElement = dataElementService.getDataElement(dataElementId);
        DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo(optionComboId);

        if (dataElement == null || optionCombo == null) {
        } else {
            DataValue dataValue = dataValueService.getDataValue(orgUnit, dataElement, period, optionCombo);
            //DataValue dataValue = null;

            if (dataValue == null) {
                String value = "1";

                if (value != null) {
                    dataValue = new DataValue(dataElement, period, orgUnit, value, "admin", new Date(), null, optionCombo);
                    dataValueService.addDataValue(dataValue);
                }
            } else {
                int val = Integer.parseInt(dataValue.getValue());
                val++;

                dataValue.setValue("" + val);
                dataValue.setTimestamp(new Date());
                dataValue.setStoredBy("admin");

                dataValueService.updateDataValue(dataValue);
            }
        }
    }

    public double getAge() {
        Calendar calendar1 = Calendar.getInstance();

        int dayOfBirth = Integer.parseInt(dob.substring(0, 2));
        int monthOfBirth = Integer.parseInt(dob.substring(3, 5));
        int yearOfBirth = Integer.parseInt(dob.substring(6, 10));

        calendar1.set(yearOfBirth, monthOfBirth, dayOfBirth);

        Calendar calendar2 = Calendar.getInstance();
        Date sysDate = new Date();
        calendar2.setTime(sysDate);

        long milliseconds1 = calendar1.getTimeInMillis();
        long milliseconds2 = calendar2.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        long diffYear = diff / (24 * 60 * 60 * 1000 * 365);
        System.out.println("Years : " + diffYear);

        return diffYear;
    }

    public void populateDHISLeprosyData() {

        // <editor-fold defaultstate="collapsed" desc="SECTION1 - Required for SIS Report">

        //</editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Populate Based on Disease Type">
        if (diseaseType.equalsIgnoreCase("PB")) {
            if (gender.equalsIgnoreCase("M")) {
                //Number of balance cases at the  begining of month , New PB, Male total
                //saveData(NLEPDataElements.NLEP_DE1);

                if (category.equalsIgnoreCase("SC")) {
                    //Number of balance cases at the begining of month, New PB, Male S.C

                    if (getAge() > 14) {
                        //Number of new leprosy cases detected in the reporting months, ADULT PB, Male S.C
                        //Number of new leprosy cases detected in the reporting months, ADULTPB, Male total
                    } else {
                        //Number of new leprosy cases detected in the reporting months, Child PB, Male S.C
                        //Number of new leprosy cases detected in the reporting months, Child  PB, Male total
                    }
                    if (deformity.equalsIgnoreCase("Grade1")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months Grade 1,  PB, Male S.C
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade 1 PB, Male total
                    } else if (deformity.equalsIgnoreCase("Grade2")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade IIPB, Male S.C
                        //Number of deformity case amoung new leprosy cases detected in the reporting months  grade II PB, Male total
                    }
                } else if (category.equalsIgnoreCase("ST")) {
                    //Number of balance cases at the beginingof month ,New  PB, Male S.T
                    if (getAge() > 14) {
                        //Number of new leprosy cases detected in the reporting months, ADULT  PB, Male S.T
                        //Number of new leprosy cases detected in the reporting months, ADULTPB, Male total
                    } else {
                        //Number of new leprosy cases detected in the reporting months, Child  PB, Male S.T
                        //Number of new leprosy cases detected in the reporting months, Child  PB, Male total                        
                    }
                    if (deformity.equalsIgnoreCase("Grade1")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade 1PB, Male S.T
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade 1 PB, Male total
                    } else if (deformity.equalsIgnoreCase("Grade2")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II PB, Male S.T
                        //Number of deformity case amoung new leprosy cases detected in the reporting months  grade II PB, Male total
                    }
                } else if (category.equalsIgnoreCase("GENERAL")) {
                    if (getAge() > 14) {
                        //Number of new leprosy cases detected in the reporting months, ADULTPB, Male total
                    } else {
                        //Number of new leprosy cases detected in the reporting months, Child  PB, Male total                        
                    }

                    if (deformity.equalsIgnoreCase("Grade1")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade 1 PB, Male total
                    } else if (deformity.equalsIgnoreCase("Grade2")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months  grade II PB, Male total
                    }
                }
            }// Male
            else if (gender.equalsIgnoreCase("F")) {
                //Number of balance cases at the  begining of month , New PB, Female total

                if (category.equalsIgnoreCase("SC")) {
                    //Number of balance cases at the begining of month , New PB, Female S.C

                    if (getAge() > 14) {
                        //Number of new leprosy cases detected in the reporting months, ADULT PB, Female S.C
                        //Number of new leprosy cases detected in the reporting months, ADULT PB, Female total 
                    } else {
                        //Number of new leprosy cases detected in the reporting months, Child  PB, Female S.C
                        //Number of new leprosy cases detected in the reporting months, Child  PB, Female total                        
                    }

                    if (deformity.equalsIgnoreCase("Grade1")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months Grade 1 PB, Female S.C
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade1  PB, Female total
                    } else if (deformity.equalsIgnoreCase("Grade2")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II PB, Female S.C
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II PB, Female total
                    }
                } else if (category.equalsIgnoreCase("ST")) {
                    //Number of balance cases at the begining of month , New PB, Female S.T
                    if (getAge() > 14) {
                        //Number of new leprosy cases detected in the reporting months, ADULTPB, Female S.T
                        //Number of new leprosy cases detected in the reporting months, ADULT PB, Female total 
                    } else {
                        //Number of new leprosy cases detected in the reporting months, Child  PB, Female S.T
                        //Number of new leprosy cases detected in the reporting months, Child  PB, Female total
                    }

                    if (deformity.equalsIgnoreCase("Grade1")) {
                        //NNumber of deformity case amoung new leprosy cases detected in the reporting months grade 1PB, Female S.T
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade1  PB, Female total
                    } else if (deformity.equalsIgnoreCase("Grade2")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months  grade II PB, Female S.T
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II PB, Female total
                    }
                } else if (category.equalsIgnoreCase("GENERAL")) {
                    if (getAge() > 14) {
                        //Number of new leprosy cases detected in the reporting months, ADULT PB, Female total
                    } else {
                        //Number of new leprosy cases detected in the reporting months, Child  PB, Female total                        
                    }

                    if (deformity.equalsIgnoreCase("Grade1")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade1  PB, Female total
                    } else if (deformity.equalsIgnoreCase("Grade2")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II PB, Female total
                    }
                }

            }// Female
        }// PB
        else if (diseaseType.equalsIgnoreCase("MB")) {
            if (gender.equalsIgnoreCase("M")) {
                //Number of balance cases at the  begining of month , New MB, Male total

                if (category.equalsIgnoreCase("SC")) {
                    //Number of balance cases at the  begining of month , New MB, Male S.C

                    if (getAge() > 14) {
                        //Number of new leprosy cases detected in the reporting months, ADULT MB, Male S.C
                        //Number of new leprosy cases detected in the reporting months, ADULT MB, Male total
                    } else {
                        //Number of new leprosy cases detected in the reporting months, Child  MB, Male S.C
                        //Number of new leprosy cases detected in the reporting months, Child  MB, Male total
                    }

                    if (deformity.equalsIgnoreCase("Grade1")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade 1MB, Male S.C
                        //Number of deformity case amoung new leprosy cases detected in the reporting monthsgrade1 MB, Male total
                    } else if (deformity.equalsIgnoreCase("Grade2")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II MB, Male S.C
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II MB, Male total
                    }
                } else if (category.equalsIgnoreCase("ST")) {
                    //Number of balance cases at the  begining of month ,New  MB, Male S.T

                    if (getAge() > 14) {
                        //Number of new leprosy cases detected in the reporting months, ADULTMB, Male S.T
                        //Number of new leprosy cases detected in the reporting months, ADULT MB, Male total
                    } else {
                        //Number of new leprosy cases detected in the reporting months, Child   MB, Male S.T
                        //Number of new leprosy cases detected in the reporting months, Child  MB, Male total
                    }

                    if (deformity.equalsIgnoreCase("Grade1")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade1MB, Male S.T
                        //Number of deformity case amoung new leprosy cases detected in the reporting monthsgrade1 MB, Male total
                    } else if (deformity.equalsIgnoreCase("Grade2")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II MB, Male S.T
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II MB, Male total
                    }
                } else if (category.equalsIgnoreCase("GENERAL")) {
                    if (getAge() > 14) {
                        //Number of new leprosy cases detected in the reporting months, ADULT MB, Male total
                    } else {
                        //Number of new leprosy cases detected in the reporting months, Child  MB, Male total
                    }

                    if (deformity.equalsIgnoreCase("Grade1")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting monthsgrade1 MB, Male total                        
                    } else if (deformity.equalsIgnoreCase("Grade2")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II MB, Male total
                    }
                }
            }// Male
            else if (gender.equalsIgnoreCase("F")) {
                //Number of balance cases at the begining of month , New MB, Female total 

                if (category.equalsIgnoreCase("SC")) {
                    //Number of balance cases at the  begining of month , New MB, Female S.C
                    if (getAge() > 14) {
                        //Number of new leprosy cases detected in the reporting months, ADULT MB, Female S.C
                        //Number of new leprosy cases detected in the reporting months, ADULT MB, Female total
                    } else {
                        //Number of new leprosy cases detected in the reporting months, Child  MB, Female S.C
                        //Number of new leprosy cases detected in the reporting months, Child  MB, Female total
                    }

                    if (deformity.equalsIgnoreCase("Grade1")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade1  MB, Female S.C
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade1 MB, Female total
                    } else if (deformity.equalsIgnoreCase("Grade2")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II MB, Female S.C
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II  MB, Female total
                    }
                } else if (category.equalsIgnoreCase("ST")) {
                    //Number of balance cases at the  begining of month , New MB, Female S.T
                    if (getAge() > 14) {
                        //Number of new leprosy cases detected in the reporting months, ADULT MB, Female S.T
                        //Number of new leprosy cases detected in the reporting months, ADULT MB, Female total
                    } else {
                        //Number of new leprosy cases detected in the reporting months, Child  MB, Female S.T
                        //Number of new leprosy cases detected in the reporting months, Child  MB, Female total
                    }

                    if (deformity.equalsIgnoreCase("Grade1")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting monthsgrade1 MB, Female S.T
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade1 MB, Female total
                    } else if (deformity.equalsIgnoreCase("Grade2")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II MB, Female S.T
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II  MB, Female total
                    }
                } else if (category.equalsIgnoreCase("GENERAL")) {
                    /* Adult/Child */
                    if (getAge() > 14) {
                        //Number of new leprosy cases detected in the reporting months, ADULT MB, Female total
                    } else {
                        //Number of new leprosy cases detected in the reporting months, Child  MB, Female total
                    }

                    /* Grade */
                    if (deformity.equalsIgnoreCase("Grade1")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade1 MB, Female total
                    } else if (deformity.equalsIgnoreCase("Grade2")) {
                        //Number of deformity case amoung new leprosy cases detected in the reporting months grade II  MB, Female total
                    }
                }

            } // Female           
        }// MB
        //</editor-fold>

        // <editor-fold defaultstate="collapsed" desc="SECTION3 - Populate Based on Reaction - Required for DPMR PHC/Block Report">
        if (reactionTime != null) {
            if (diseaseType.equalsIgnoreCase("PB")) {
                //No. of reaction cases recorded (PB) - 2680
                saveData(NLEPDataElements.NLEP_DE181 + ":1");
                if (reactionMgmtAt.equalsIgnoreCase("CENTER")) {
                    //No.of reaction cases managed at PHC  (PB) - 2681
                    saveData(NLEPDataElements.NLEP_DE182);
                } else if (reactionMgmtAt.equalsIgnoreCase("REFERRED_TO_DIST_HOSPITAL")) {
                    //No.of reaction cases referred to Dist. Hosp./other instt(PB) - 2682
                    saveData(NLEPDataElements.NLEP_DE183);
                }
            } else if (diseaseType.equalsIgnoreCase("MB")) {
                //No. of reaction cases recorded (MB) - 2696
                saveData(NLEPDataElements.NLEP_DE197);
                if (reactionMgmtAt.equalsIgnoreCase("CENTER")) {
                    //No.of reaction cases managed at PHC (MB) - 2697
                    saveData(NLEPDataElements.NLEP_DE198);
                } else if (reactionMgmtAt.equalsIgnoreCase("REFERRED_TO_DIST_HOSPITAL")) {
                    //No.of reaction cases referred to Dist. Hosp./other instt. (MB) - 2698
                    saveData(NLEPDataElements.NLEP_DE199);
                }
            }
        }

        if (diseaseType.equalsIgnoreCase("PB")) {
            if (caseDetection.equalsIgnoreCase("RELAPSE") && relapseSelect.equalsIgnoreCase("YES")) {
                //No. of relapse cases suspected and referred (PB) - 2683
                saveData(NLEPDataElements.NLEP_DE184);
            }

            if (disabilityMDT != null) {
                //No. of cases developed new disability after MDT (PB) - 2684
                saveData(NLEPDataElements.NLEP_DE185);
            }

            if (servicesGiven.equalsIgnoreCase("MCR_CHAPPAL")) {
                //No. of patients provided with footwear (PB) - 2685
                saveData(NLEPDataElements.NLEP_DE186);
            } else if (servicesGiven.equalsIgnoreCase("SELF_CARE_KIT")) {
                //No. of patients provided with self care kit (PB) - 2686
                saveData(NLEPDataElements.NLEP_DE187);
            }

            if (rcsReferred.equalsIgnoreCase("YES")) {
                //No. of patients referred for RCS (PB) - 2687
                saveData(NLEPDataElements.NLEP_DE188);
            }
        } else if (diseaseType.equalsIgnoreCase("MB")) {
            if (caseDetection.equalsIgnoreCase("RELAPSE") && relapseSelect.equalsIgnoreCase("YES")) {
                //No. of relapse cases suspected and referred (MB) - 2699
                saveData(NLEPDataElements.NLEP_DE200);
            }

            if (disabilityMDT != null) {
                //No. of cases developed new disability after MDT (MB) - 2700
                saveData(NLEPDataElements.NLEP_DE201);
            }

            if (servicesGiven.equalsIgnoreCase("MCR_CHAPPAL")) {
                //No. of patients provided with footwear (MB) - 2701
                saveData(NLEPDataElements.NLEP_DE202);
            } else if (servicesGiven.equalsIgnoreCase("SELF_CARE_KIT")) {
                //No. of patients provided with self care kit (MB) - 2702
                saveData(NLEPDataElements.NLEP_DE203);
            }

            if (rcsReferred.equalsIgnoreCase("YES")) {
                //No. of patients referred for RCS (MB) - 2703
                saveData(NLEPDataElements.NLEP_DE204);
            }
        }
        //</editor-fold>
    }
    
    //-------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService(DataElementService dataElementService) {
        this.dataElementService = dataElementService;
    }
    private DataValueService dataValueService;

    public void setDataValueService(DataValueService dataValueService) {
        this.dataValueService = dataValueService;
    }
    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
            DataElementCategoryOptionComboService dataElementCategoryOptionComboService) {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    //-------------------------------------------------------------------------
    // Input & Output
    //-------------------------------------------------------------------------
    
    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }
    
    private int parentouIDTB;

    public void setParentouIDTB( int parentouIDTB )
    {
        this.parentouIDTB = parentouIDTB;
    }

    private OrganisationUnit orgUnit;
    
    private Period period;
    
}
