package ams;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import ws.client.AppointmentEntity;
import ws.client.AppointmentNotFoundException_Exception;
import ws.client.CreateAppointmentException_Exception;
import ws.client.CreatePatientException_Exception;
import ws.client.DoctorEntity;
import ws.client.DoctorNotFoundException_Exception;
import ws.client.GenderEnum;
import ws.client.InputDataValidationException_Exception;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.PasswordException_Exception;
import ws.client.PatientEntity;
import ws.client.PatientIdentityNumberExist_Exception;
import ws.client.PatientNotFoundException_Exception;
import ws.client.UnknownPersistenceException_Exception;

public class MainApp 
{
    private PatientEntity currentPatientEntity;
    
    private String[] timeSlot = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};
    private String[] timeSlotThur = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"};
    private String[] timeSlotFri = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"};
    
    public void runApp() throws UnknownPersistenceException_Exception, InvalidLoginCredentialException_Exception, PatientNotFoundException_Exception, AppointmentNotFoundException_Exception, DoctorNotFoundException_Exception, CreateAppointmentException_Exception 
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to AMS Client ***\n");
            System.out.println("1: Register");
            System.out.println("2: Login");
            System.out.println("3: Exit\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();
                if (response == 1)
                {
                    doRegister();
                }
                else if(response == 2)
                {
                    doLogin();
                    System.out.println("Login successful!\n");

                    menuMain();
                }
                else if (response == 3)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 3)
            {
                break;
            }
        }
    }
    
    public void doRegister() throws UnknownPersistenceException_Exception, InvalidLoginCredentialException_Exception
    {
        Scanner scanner = new Scanner(System.in);
        PatientEntity newPatientEntity = new PatientEntity();
        
        System.out.println("*** AMS Client :: Register ***\n");
        System.out.print("Enter Identity Number> ");
        newPatientEntity.setIdentityNumber(scanner.nextLine().trim());
        System.out.print("Enter 6-digit Password> ");
        
        String passwordToHash = scanner.nextLine().trim();
        String securePassword = getSecurePassword(passwordToHash);
        newPatientEntity.setPassword(securePassword);
        
        System.out.print("Enter First Name> ");
        newPatientEntity.setFirstName(scanner.nextLine().trim());
        System.out.print("Enter Last Name> ");
        newPatientEntity.setLastName(scanner.nextLine().trim());
        System.out.print("Enter Gender(M/F)> ");
        if (scanner.nextLine().trim().equals("M")) 
        {
            newPatientEntity.setGender(GenderEnum.M);
        } 
        else 
        {
            newPatientEntity.setGender(GenderEnum.F);
        }
        System.out.print("Enter Age> ");
        newPatientEntity.setAge(scanner.nextInt());
        scanner.nextLine();
        System.out.print("Enter Phone> ");
        newPatientEntity.setPhone(scanner.nextLine().trim());
        System.out.print("Enter Address> ");
        newPatientEntity.setAddress(scanner.nextLine().trim());

         try 
        {
            inputIsIncorrect(newPatientEntity);
            try 
            {
                checkPassword(passwordToHash);
                Long newPatientId = createPatient(newPatientEntity);
                System.out.println("Patient has been registered successfully!\n");
            } 
            catch (PasswordException_Exception | PatientIdentityNumberExist_Exception | InputDataValidationException_Exception e) 
            {
                System.out.println("An error has occured while registering new patient: " + e.getMessage() + "\n");
            }     
        } 
        catch (CreatePatientException_Exception ex) 
        {
            System.out.println(ex.getMessage());
        }
    }
    
    public void doLogin()
    {
        Scanner scanner = new Scanner(System.in);
        String identityNo = "";
        String password = "";
        
        System.out.println("*** AMS Client :: Login ***\n");
        System.out.print("Enter Identity Number> ");
        identityNo = scanner.nextLine().trim();
        System.out.print("Enter 6-digit password> ");
        password = scanner.nextLine().trim();
        
        if(identityNo.length() > 0 && password.length() > 0)
        {
            try {
               currentPatientEntity = patientLogin(identityNo, password);  
            } catch (InvalidLoginCredentialException_Exception ex) {
                System.out.println("An error has occurred while logging in: " + ex.getMessage());
            }   
        }
    }
    
    private void menuMain() throws PatientNotFoundException_Exception, AppointmentNotFoundException_Exception, DoctorNotFoundException_Exception, CreateAppointmentException_Exception
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** AMS Client :: Main ***\n");
            System.out.println("You are login as " + currentPatientEntity.getFirstName() + " " + currentPatientEntity.getLastName() + "\n");
            System.out.println("1: View Appointments");
            System.out.println("2: Add Appointment");
            System.out.println("3: Cancel Appointment");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1)
                {
                    viewPatientAppointments();               
                }
                else if (response == 2)
                {
                    addNewAppointment();
                }
                else if (response == 3)
                {
                    cancelExistingAppointment();
                }
                else if (response == 4)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 4)
            {
                break;
            } 
        }   
    }
    
    private void viewPatientAppointments() throws PatientNotFoundException_Exception
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** AMS Client :: View Appointments ***\n");
      
        try
        { 
            List<AppointmentEntity> patientAppointments = retrieveAppointmentByPatientIdentityNo(currentPatientEntity.getIdentityNumber());
            if (patientAppointments.isEmpty())
            {
                System.out.println("Patient has no upcoming appointments!\n");
            }
            else 
            {
                System.out.println("Appointments:");
                System.out.printf("%-3s|%-12s|%-7s|%-13s\n", "Id", "Date", "Time", "Doctor");
                
                for (AppointmentEntity appointmentEntity : patientAppointments)
                {
                    String doctorName = appointmentEntity.getDoctorEntity().getFirstName() + " " + appointmentEntity.getDoctorEntity().getLastName();
                    String timeString = appointmentEntity.getTime().toString();
                    
                    System.out.printf("%-3s|%-12s|%-7s|%-13s\n", appointmentEntity.getAppointmentId(), appointmentEntity.getDate(), timeString.substring(0, 5), doctorName);
                }
                System.out.print("\n");
            }
        }
        catch(PatientNotFoundException_Exception ex)
        {
              System.out.println("Failed to retrieve list of appointments!\n");
        }
    }
    
    private void addNewAppointment() throws CreateAppointmentException_Exception, PatientNotFoundException_Exception
    {
        Scanner scanner = new Scanner(System.in);
        Long doctorId;
        String date;
        DoctorEntity doctor = null;
        PatientEntity patient = null;
        List<String> tempList = new ArrayList<>();
        
        System.out.println("*** AMS Client:: Add Appointment ***\n");
        
        List<DoctorEntity> doctors = retrieveAllDoctors();
        System.out.println("Doctor:");
        System.out.printf("%-5s|%-20s\n", "Id", "Name");
        
        for (DoctorEntity doctorEntity:doctors)
        {
            String doctorName = doctorEntity.getFirstName() + " " + doctorEntity.getLastName();
            System.out.printf("%-5s|%-20s\n", doctorEntity.getDoctorId(), doctorName);
        }
        System.out.println("");
        
        System.out.print("Enter Doctor Id> ");
        doctorId = scanner.nextLong();
        scanner.nextLine();
        System.out.print("Enter Date(YYYY-MM-DD)> ");
        date = scanner.nextLine();
        System.out.println("");

        Date actualDate = Date.valueOf(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(actualDate);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        
        //set two days validation rule
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date twoDaysLater = new Date((c.getTime()).getTime());
            
        //check if doctor exists
        try
        {
            doctor = retrieveDoctorByDoctorId(doctorId);
//            try 
//            {
                //check if doctor is on leave
//                checkifDocIsOnLeave(doctorId, actualDate);
//             
                try 
                {
                    checkApptNotOnWeekendAnd2DaysLater(date);
                    
                    System.out.println("Availability for " + doctor.getFirstName() + " " + doctor.getLastName() + " on " + date + ":");
                    List<AppointmentEntity> appointments = retrieveAppointmentByDoctorId(doctorId);
                    if (appointments.isEmpty()) {
                        if (day >= Calendar.MONDAY && day <= Calendar.WEDNESDAY) {
                            for (String time : timeSlot) {
                                System.out.print(time + " ");
                            }
                            System.out.print("\n");
                        } else if (day == Calendar.THURSDAY) {
                            for (String time : timeSlotThur) {
                                System.out.print(time + " ");
                            }
                            System.out.print("\n");
                        } else //Friday
                        {
                            for (String time : timeSlotFri) {
                                System.out.print(time + " ");
                            }
                            System.out.print("\n");
                        }
                    } else {
                        for (AppointmentEntity appointmentEntity : appointments) {
                            if (actualDate.equals(appointmentEntity.getDate())) {
                                String unavailableTime = appointmentEntity.getTime().toString();
                                tempList.add(unavailableTime.substring(0, 5));
                            }
                        }
                        if (tempList.isEmpty()) {
                            if (day >= Calendar.MONDAY && day <= Calendar.WEDNESDAY) {
                                for (int i = 0; i < timeSlot.length; i++) {
                                    System.out.print(timeSlot[i] + " ");
                                }
                                System.out.print("\n");
                            } else if (day == Calendar.THURSDAY) {
                                for (int i = 0; i < timeSlotThur.length; i++) {
                                    System.out.print(timeSlotThur[i] + " ");
                                }
                                System.out.print("\n");
                            } else //Friday
                            {
                                for (int i = 0; i < timeSlotFri.length; i++) {
                                    System.out.print(timeSlotFri[i] + " ");
                                }
                                System.out.print("\n");
                            }
                        } else {
                            if (day >= Calendar.MONDAY && day <= Calendar.WEDNESDAY) {
                                for (int i = 0; i < timeSlot.length; i++) {
                                    for (int j = 0; j < tempList.size(); j++) {
                                        if (!timeSlot[i].equals(tempList.get(j)) && j == tempList.size() - 1) {
                                            System.out.print(timeSlot[i] + " ");
                                        }
                                    }
                                }
                                System.out.print("\n");
                                tempList.clear();
                            } else if (day == Calendar.THURSDAY) {
                                for (int i = 0; i < timeSlotThur.length; i++) {
                                    for (int j = 0; j < tempList.size(); j++) {
                                        if (!timeSlotThur[i].equals(tempList.get(j)) && j == tempList.size() - 1) {
                                            System.out.print(timeSlotThur[i] + " ");
                                        }
                                    }
                                }
                                System.out.print("\n");
                                tempList.clear();
                            } else //Friday
                            {
                                for (int i = 0; i < timeSlotFri.length; i++) {
                                    for (int j = 0; j < tempList.size(); j++) {
                                        if (!timeSlotFri[i].equals(tempList.get(j)) && j == tempList.size() - 1) {
                                            System.out.print(timeSlotFri[i] + " ");
                                        }
                                    }
                                }
                                System.out.print("\n");
                                tempList.clear();
                            }
                        }
                    }

                    System.out.println("");
                    System.out.print("Enter Time(HH:MM)> ");
                    String timeInput = scanner.nextLine().trim();
                    String timeformat = timeInput + ":00";
                    Time time = Time.valueOf(timeformat);

                    try {
                        patient = retrievePatientByPatientIdentityNumber(currentPatientEntity.getIdentityNumber());
                    } catch (PatientNotFoundException_Exception ex) {
                        System.out.println("Error in retrieving Patient's identity Number!\n");
                    }

                    AppointmentEntity newAppointmentEntity = new AppointmentEntity();
//                    newAppointmentEntity.setDate(actualDate);
                    newAppointmentEntity.setDoctorEntity(doctor);
//                    newAppointmentEntity.setTime(time);
                    newAppointmentEntity.setPatientEntity(patient);
                    createNewAppointment(newAppointmentEntity);
                    updatePatientList(currentPatientEntity.getIdentityNumber());
                    updateDoctorList(doctorId);

                    String patientName = patient.getFirstName() + " " + patient.getLastName();
                    String doctorName = doctor.getFirstName() + " " + doctor.getLastName();

                    System.out.println(patientName + " appointment with " + doctorName + " at " + timeInput + " on " + date + " has been added.\n");

                }
                catch (CreateAppointmentException_Exception ex)
                {
                    System.out.println(ex.getMessage());
                } 
//            }
//            catch (LeaveApplicationException_Exception ex)
//            {
//                System.out.println(ex.getMessage());
//            }
        }
        catch (DoctorNotFoundException_Exception ex)
        {
            System.out.println("Doctor does not exist!\n");
        }
    }
    
    private void cancelExistingAppointment() throws AppointmentNotFoundException_Exception, DoctorNotFoundException_Exception
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** AMS Client :: Cancel Appointment ***\n");
        
        try
        { 
            List<AppointmentEntity> patientAppointments = retrieveAppointmentByPatientIdentityNo(currentPatientEntity.getIdentityNumber());
            if (patientAppointments.isEmpty())
            {
                System.out.println("Patient has no upcoming appointments!\n");
            }
            else 
            {
                System.out.println("Appointments:");
                System.out.printf("%-3s|%-12s|%-7s|%-13s\n", "Id", "Date", "Time", "Doctor");
                
                for (AppointmentEntity appointmentEntity:patientAppointments)
                {
                    String doctorName = appointmentEntity.getDoctorEntity().getFirstName() + " " + appointmentEntity.getDoctorEntity().getLastName();
                    String timeString = appointmentEntity.getTime().toString();
                    
                    System.out.printf("%-3s|%-12s|%-7s|%-13s\n", appointmentEntity.getAppointmentId(), appointmentEntity.getDate(), timeString.substring(0, 5), "doctorName");
                }
                System.out.print("\n");
            }
            
            System.out.print("Enter Appointment Id> ");
            Long appointmentId = scanner.nextLong();
           
            AppointmentEntity appointmentEntity = retrieveAppointmentByAppointmentId(appointmentId);
            DoctorEntity doctorEntity = appointmentEntity.getDoctorEntity();
            String doctorName = doctorEntity.getFirstName() + " " + doctorEntity.getLastName();
            String timeString = appointmentEntity.getTime().toString();
            String dateString = appointmentEntity.getDate().toString();
            String patientName = currentPatientEntity.getFirstName() + " " + currentPatientEntity.getLastName();
            
            //remove appointment from database
            deleteAppointment(appointmentId);
            updateDoctorList(doctorEntity.getDoctorId());
            updatePatientList(currentPatientEntity.getIdentityNumber());
            
            System.out.println(patientName + " appointment with " + doctorName + " at " + timeString.substring(0, 5) + " on " + dateString + " has been cancelled.\n");
        }
        catch(PatientNotFoundException_Exception ex)
        {
              System.out.println("Failed to retrieve list of appointments!\n");
        }
    }

    private static PatientEntity patientLogin(java.lang.String arg0, java.lang.String arg1) throws InvalidLoginCredentialException_Exception {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        return port.patientLogin(arg0, arg1);
    }

    private static void checkPassword(java.lang.String password) throws PasswordException_Exception {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        port.checkPassword(password);
    }

    private static String getSecurePassword(java.lang.String password) {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        return port.getSecurePassword(password);
    }

    private static Long createPatient(ws.client.PatientEntity patientEntity) throws PatientIdentityNumberExist_Exception, InputDataValidationException_Exception, InvalidLoginCredentialException_Exception, UnknownPersistenceException_Exception {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        return port.createPatient(patientEntity);
    }

    private static void inputIsIncorrect(ws.client.PatientEntity patientIdentity) throws CreatePatientException_Exception {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        port.inputIsIncorrect(patientIdentity);
    }

    private static java.util.List<ws.client.AppointmentEntity> retrieveAppointmentByPatientIdentityNo(java.lang.String identityNumber) throws PatientNotFoundException_Exception {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        return port.retrieveAppointmentByPatientIdentityNo(identityNumber);
    }

    private static AppointmentEntity retrieveAppointmentByAppointmentId(java.lang.Long arg0) throws AppointmentNotFoundException_Exception {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        return port.retrieveAppointmentByAppointmentId(arg0);
    }

    private static void deleteAppointment(java.lang.Long appointmentId) {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        port.deleteAppointment(appointmentId);
    }

    private static void updateDoctorList(java.lang.Long doctorId) throws DoctorNotFoundException_Exception {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        port.updateDoctorList(doctorId);
    }

    private static void updatePatientList(java.lang.String identityNumber) throws PatientNotFoundException_Exception {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        port.updatePatientList(identityNumber);
    }

    private static java.util.List<ws.client.DoctorEntity> retrieveAllDoctors() {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        return port.retrieveAllDoctors();
    }

    private static DoctorEntity retrieveDoctorByDoctorId(java.lang.Long doctorId) throws DoctorNotFoundException_Exception {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        return port.retrieveDoctorByDoctorId(doctorId);
    }

    private static void checkApptNotOnWeekendAnd2DaysLater(java.lang.String date) throws CreateAppointmentException_Exception {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        port.checkApptNotOnWeekendAnd2DaysLater(date);
    }

    private static java.util.List<ws.client.AppointmentEntity> retrieveAppointmentByDoctorId(java.lang.Long doctorId) throws DoctorNotFoundException_Exception {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        return port.retrieveAppointmentByDoctorId(doctorId);
    }

    private static PatientEntity retrievePatientByPatientIdentityNumber(java.lang.String identityNumber) throws PatientNotFoundException_Exception {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        return port.retrievePatientByPatientIdentityNumber(identityNumber);
    }

    private static AppointmentEntity createNewAppointment(ws.client.AppointmentEntity newAppointmentEntity) {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        return port.createNewAppointment(newAppointmentEntity);
    }
    
    
    
    
}
