package carsclient;

import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateless.LeaveEntitySessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import ejb.session.stateless.StaffEntitySessionBeanRemote;
import entity.AppointmentEntity;
import entity.DoctorEntity;
import entity.GenderEnum;
import entity.LeaveEntity;
import entity.PatientEntity;
import entity.StaffEntity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import util.exception.AppointmentNotFoundException;
import util.exception.CreateAppointmentException;
import util.exception.DeleteDoctorException;
import util.exception.DeletePatientException;
import util.exception.DoctorNotFoundException;
import util.exception.LeaveApplicationException;
import util.exception.PasswordException;
import util.exception.PatientNotFoundException;
import util.exception.StaffNotFoundException;
import util.exception.UpdateDoctorException;
import util.exception.UpdatePatientException;

public class AdministrationOperationModule 
{
    private DoctorSessionBeanRemote doctorSessionBeanRemote;
    private PatientSessionBeanRemote patientSessionBeanRemote;
    private StaffEntitySessionBeanRemote staffEntitySessionBeanRemote;
    private LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote;

    public AdministrationOperationModule() 
    {
    }

    public AdministrationOperationModule(DoctorSessionBeanRemote doctorSessionBeanRemote, PatientSessionBeanRemote patientSessionBeanRemote, StaffEntitySessionBeanRemote staffEntitySessionBeanRemote, LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote) 
    {
        this();
        this.doctorSessionBeanRemote = doctorSessionBeanRemote;
        this.patientSessionBeanRemote = patientSessionBeanRemote;
        this.staffEntitySessionBeanRemote = staffEntitySessionBeanRemote;
        this.leaveEntitySessionBeanRemote = leaveEntitySessionBeanRemote;
    }
    
    public void administrationOperation() throws PatientNotFoundException, UpdatePatientException, NoSuchAlgorithmException, NoSuchProviderException, PasswordException, DeletePatientException, StaffNotFoundException, AppointmentNotFoundException, DoctorNotFoundException, LeaveApplicationException
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** CARS :: Administration Operation ***\n");
            System.out.println("1: Patient Management");
            System.out.println("2: Doctor Management");
            System.out.println("3: Staff Management");
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                   patientManagement();
                }
                else if(response == 2)
                {
                    doctorManagement();
                }
                else if(response == 3)
                {
                    staffManagement();
                }
                else if(response == 4)
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
    
    private void patientManagement() throws PatientNotFoundException, UpdatePatientException, NoSuchAlgorithmException, NoSuchProviderException, PasswordException, DeletePatientException
    {
    
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** CARS :: Administration Operation :: Patient Management ***\n");
            System.out.println("1: Add Patient");
            System.out.println("2: View Patient Details");
            System.out.println("3: Update Patient");
            System.out.println("4: Delete Patient");
            System.out.println("5: View All Patients");
            System.out.println("6: Back\n");

            response = 0;
            
            while(response < 1 || response > 6)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                   addPatient();
                }
                else if(response == 2)
                {
                    viewPatientDetails();
                }
                else if(response == 3)
                {
                    updatePatient();
                }
                else if(response == 4)
                {
                    deletePatient();
                }
                else if(response == 5)
                {
                    viewAllPatients();
                }
                else if(response == 6)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 6)
            {
                break;
            }
        }
    }
    
    private void addPatient() throws NoSuchAlgorithmException, NoSuchProviderException, PasswordException 
    {
        Scanner scanner = new Scanner(System.in);
        PatientEntity newPatientEntity = new PatientEntity();
        
        System.out.println("*** CARS :: Administration Operation :: Add New Patient ***\n");
        System.out.print("Enter Identity Number> ");
        newPatientEntity.setIdentityNumber(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        
        String passwordToHash = scanner.nextLine().trim();
        byte[] salt = getSalt();
        String securePassword = getSecurePassword(passwordToHash, salt);
        newPatientEntity.setPassword(securePassword);
        
        System.out.print("Enter First Name> ");
        newPatientEntity.setFirstName(scanner.nextLine().trim());
        System.out.print("Enter Last Name> ");
        newPatientEntity.setLastName(scanner.nextLine().trim());
        System.out.print("Enter Gender> ");
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
            patientSessionBeanRemote.checkPassword(passwordToHash);
            Long newPatientId = patientSessionBeanRemote.createPatient(newPatientEntity);
            System.out.println("Patient ID " + newPatientId + " has been added successfully\n");
        } 
        catch (PasswordException e) 
        {
            System.out.println("An error has occured while adding new patient: " + e.getMessage() + "\n");
        }   
    }
    
    private static String getSecurePassword(String passwordToHash, byte[] salt)
    {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(salt);
            //Get the hash's bytes 
            byte[] bytes = md.digest(passwordToHash.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
     
    //Add salt
    private static byte[] getSalt() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        //Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        //Create array for salt
        byte[] salt = new byte[16];
        //Get a random salt
        sr.nextBytes(salt);
        //return salt
        return salt;
    }

    private void viewPatientDetails()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** CARS :: Administration Operation :: View Patient Details***\n");
        System.out.print("Enter Patient ID> ");
        Long patientId = scanner.nextLong();
        
        try
        {
            PatientEntity patientEntity = patientSessionBeanRemote.retrievePatientByPatientId(patientId);
            System.out.printf("%-15s|%-20s|%-30s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s\n", "Patient ID", "Identity Number", "Password", "First Name", "Last Name", "Gender", "Age", "Phone", "Address");
            System.out.printf("%-15s|%-20s|%-30s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s\n", patientEntity.getPatientId().toString(), patientEntity.getIdentityNumber(), patientEntity.getPassword(), patientEntity.getFirstName(), patientEntity.getLastName(), patientEntity.getGender().toString(), patientEntity.getAge().toString(), patientEntity.getPhone(), patientEntity.getAddress());         
            System.out.println("------------------------");

        }
        catch(PatientNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving patient: " + ex.getMessage() + "\n");
        }
    }
    
    private void updatePatient() throws PatientNotFoundException, UpdatePatientException, NoSuchAlgorithmException, NoSuchProviderException
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Patient Identity Number to update details of the patient> ");
        String patientIdentityNo = scanner.nextLine().trim();
        System.out.println("");
        PatientEntity patientEntity = patientSessionBeanRemote.retrievePatientByPatientIdentityNumber(patientIdentityNo);
        String input;
        Integer integerInput;
        
        System.out.println("*** CARS :: Administration Operation :: Update Patient ***\n");
                
        System.out.print("Enter Password (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            String passwordToHash = input;
            byte[] salt = getSalt();
            String securePassword = getSecurePassword(passwordToHash, salt);
            patientEntity.setPassword(securePassword);
        }
        
        System.out.print("Enter First Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            patientEntity.setFirstName(input);
        }
        
        System.out.print("Enter Last Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            patientEntity.setLastName(input);
        }
        
        System.out.print("Enter Gender (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            if(input.equals("M"))
            {
                patientEntity.setGender(GenderEnum.M);
            }           
            else
            {
                patientEntity.setGender(GenderEnum.F);
            }
        }
        
        
        System.out.print("Enter Age (blank if no change)> ");
        integerInput = scanner.nextInt();
        if(integerInput > 0)
        {
            patientEntity.setAge(integerInput);
        }
        
        scanner.nextLine();
        
        System.out.print("Enter Phone (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            patientEntity.setPhone(input);
        }
        
        System.out.print("Enter Address (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            patientEntity.setAddress(input);
        }
        
        try
        {
            patientSessionBeanRemote.updatePatient(patientEntity);
            System.out.println("Patient updated successfully!\n");
        }
        catch (UpdatePatientException ex) 
        {
            System.out.println("An error has occurred while updating patient: " + ex.getMessage() + "\n");
        }
    }
    
    private void deletePatient() throws DeletePatientException 
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Patient ID> ");
        Long patientId = scanner.nextLong();

        String input;
        
        scanner.nextLine();

        try 
        {
            PatientEntity patientEntity = patientSessionBeanRemote.retrievePatientByPatientId(patientId);
            System.out.println("*** CARS :: Administration Operation :: Delete Patient ***\n");
            System.out.printf("Confirm Delete Patient %s (Identity Number: %s) (Enter 'Y' to Delete)> ", patientEntity.getFirstName(), patientEntity.getIdentityNumber());
            input = scanner.nextLine().trim();

            if (input.equals("Y")) 
            {
                try 
                {
                    patientSessionBeanRemote.deletePatient(patientEntity.getPatientId());
                    System.out.println("Patient deleted successfully!\n");
                } 
                catch (PatientNotFoundException | DeletePatientException ex) 
                {
                    System.out.println("An error has occurred while deleting patient: " + ex.getMessage() + "\n");
                }
            } 
            else 
            {
                System.out.println("Patient NOT deleted!\n");
            }
        } 
        catch (PatientNotFoundException ex) 
        {
            System.out.println("An error has occurred while retrieving patient: " + ex.getMessage() + "\n");
        }
    }
    
    
    private void viewAllPatients() 
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** CARS :: Administration Operation :: View All Patients ***\n");
        
        List<PatientEntity> patientEntities = patientSessionBeanRemote.retrieveAllPatients();
        System.out.printf("%-15s|%-20s|%-30s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s\n", "Patient ID", "Identity Number", "Password", "First Name", "Last Name", "Gender", "Age", "Phone", "Address");

        for(PatientEntity patientEntity:patientEntities)
        {
            System.out.printf("%-15s|%-20s|%-30s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s\n", patientEntity.getPatientId().toString(), patientEntity.getIdentityNumber(), patientEntity.getPassword(), patientEntity.getFirstName(), patientEntity.getLastName(), patientEntity.getGender().toString(), patientEntity.getAge().toString(), patientEntity.getPhone(), patientEntity.getAddress());         
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
        private void doctorManagement() throws AppointmentNotFoundException, DoctorNotFoundException, LeaveApplicationException
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** CARS :: Administration Operation :: Doctor Management ***\n");
            System.out.println("1: Add Doctor");
            System.out.println("2: View Doctor Details");
            System.out.println("3: Update Doctor");
            System.out.println("4: Delete Doctor");
            System.out.println("5: View All Doctor");
            System.out.println("6: Leave Management");
            System.out.println("7: Back\n");

            response = 0;
            
            while(response < 1 || response > 7)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                   addDoctor();
                }
                else if(response == 2)
                {
                    viewDoctorDetails();
                }
                else if(response == 3)
                {
                    updateDoctor();
                }
                else if(response == 4)
                {
                    deleteDoctor();
                }
                else if(response == 5)
                {
                    viewAllDoctors();
                }
                else if(response == 6)
                {
                    leaveManagement();
                }
                else if(response == 7)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 7)
            {
                break;
            }
        }
    }
    
    private void addDoctor() 
    {
        Scanner scanner = new Scanner(System.in);
        DoctorEntity newDoctorEntity = new DoctorEntity();
        
        System.out.println("*** CARS :: Administration Operation :: Add New Doctor ***\n");
        System.out.print("Enter First Name> ");
        newDoctorEntity.setFirstName(scanner.nextLine().trim());
        System.out.print("Enter Last Name> ");
        newDoctorEntity.setLastName(scanner.nextLine().trim());
        System.out.print("Enter Registration> ");
        newDoctorEntity.setRegistration(scanner.nextLine().trim());
        System.out.print("Enter Qualification> ");
        newDoctorEntity.setQualifications(scanner.nextLine().trim());

        Long newDoctorId = doctorSessionBeanRemote.createNewDoctor(newDoctorEntity);
        System.out.println(newDoctorId + " Doctor has been added successfully\n");
    }
    
    private void viewDoctorDetails()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** CARS :: Administration Operation :: View Doctor Details***\n");
        System.out.print("Enter Doctor ID> ");
        Long doctorId = scanner.nextLong();
        
        try
        {
            DoctorEntity doctorEntity = doctorSessionBeanRemote.retrieveDoctorByDoctorId(doctorId);
            System.out.printf("%-15s|%-20s|%-20s|%-20s|%-20s\n", "Doctor ID", "First Name", "Last Name", "Registration", "Qualification");
            System.out.printf("%-15s|%-20s|%-20s|%-20s|%-20s\n", doctorEntity.getDoctorId().toString(), doctorEntity.getFirstName(), doctorEntity.getLastName(), doctorEntity.getRegistration(), doctorEntity.getQualifications());         
            System.out.println("------------------------");

        }
        catch(DoctorNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving patient: " + ex.getMessage() + "\n");
        }
    }
    
    private void updateDoctor() throws DoctorNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Doctor ID> ");
        Long doctorId = scanner.nextLong();
        DoctorEntity doctorEntity = doctorSessionBeanRemote.retrieveDoctorByDoctorId(doctorId);
        String input;
        
        scanner.nextLine();
        
        System.out.println("*** CARS :: Administration Operation :: Update Doctor ***\n");
        System.out.print("Enter First Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            doctorEntity.setFirstName(input);
        }
        
        System.out.print("Enter Last Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            doctorEntity.setLastName(input);
        }
        
        System.out.print("Enter Registration (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            doctorEntity.setRegistration(input);
        }
        
        System.out.print("Enter Qualification (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            doctorEntity.setQualifications(input);
        }
        
        try
        {
            doctorSessionBeanRemote.updateDoctor(doctorEntity);
            System.out.println("Doctor updated successfully!\n");
        }
        catch (UpdateDoctorException ex) 
        {
            System.out.println("An error has occurred while updating doctor: " + ex.getMessage() + "\n");
        }
    }
    
    private void deleteDoctor() throws AppointmentNotFoundException 
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Doctor ID> ");
        Long doctorId = scanner.nextLong();

        String input;
        
        scanner.nextLine();

        try 
        {
            DoctorEntity doctorEntity = doctorSessionBeanRemote.retrieveDoctorByDoctorId(doctorId);
            System.out.println("*** CARS :: Administration Operation :: Delete Doctor ***\n");
            System.out.printf("Confirm Delete Doctor %s (Registration: %s) (Enter 'Y' to Delete)> ", doctorEntity.getFirstName(), doctorEntity.getRegistration());
            input = scanner.nextLine().trim();

            if (input.equals("Y")) 
            {
                try 
                {
                    doctorSessionBeanRemote.deleteDoctor(doctorEntity);
                    System.out.println("Doctoe deleted successfully!\n");
                } 
                catch (DoctorNotFoundException | DeleteDoctorException ex) 
                {
                    System.out.println("An error has occurred while deleting doctor: " + ex.getMessage() + "\n");
                }
            } 
            else 
            {
                System.out.println("Doctor NOT deleted!\n");
            }
        } 
        catch (DoctorNotFoundException ex) 
        {
            System.out.println("An error has occurred while retrieving doctor: " + ex.getMessage() + "\n");
        }
    }
    
    private void viewAllDoctors() 
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** CARS :: Administration Operation :: View All Doctors ***\n");
        
        List<DoctorEntity> doctorEntities = doctorSessionBeanRemote.retrieveAllDoctors();
        System.out.printf("%-15s|%-20s|%-20s|%-20s|%-20s\n", "Doctor ID", "First Name", "Last Name", "Registration", "Qualification");

        for(DoctorEntity doctorEntity : doctorEntities)
        {
            System.out.printf("%-15s|%-20s|%-20s|%-20s|%-20s\n", doctorEntity.getDoctorId().toString(), doctorEntity.getFirstName(), doctorEntity.getLastName(), doctorEntity.getRegistration(), doctorEntity.getQualifications());         
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void leaveManagement() throws LeaveApplicationException
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Doctor ID> ");
        Long doctorId = scanner.nextLong();
        scanner.nextLine();
        System.out.print("Enter Date> ");
        String date = scanner.nextLine();

        Date appliedLeaveDate = Date.valueOf(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(appliedLeaveDate);
        int day = cal.get(Calendar.DAY_OF_WEEK);

        //set a week in advance rule
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 6);
        Date aWeekAdvance = new Date((c.getTime()).getTime());      
        
        try //check if doctor exist
        {
            DoctorEntity doctorEntity = doctorSessionBeanRemote.retrieveDoctorByDoctorId(doctorId);
            try // check if applied date is valid
            {
                if(appliedLeaveDate.compareTo(aWeekAdvance) < 0) // check if date is applied a week in advance
                {
                    throw new LeaveApplicationException("Leave date applied must be at least a week in advance!\n");
            
                }
                leaveEntitySessionBeanRemote.checkIfDoctorAppliedInSameWeek(doctorId, appliedLeaveDate);
                doctorSessionBeanRemote.checkAppointmentSchedule(doctorId, appliedLeaveDate); // check if doc has appt on applied day
                leaveEntitySessionBeanRemote.createNewLeave(new LeaveEntity(doctorEntity, appliedLeaveDate));
                System.out.println("Leave date applied was successful!\n");

            }
            catch (LeaveApplicationException ex) 
            {
                System.out.println("There was an error applying for leave: " + ex.getMessage());
            }   
        }    
        catch (DoctorNotFoundException ex)
        {
            System.out.println("Doctor does not exist!\n");
        } 
    }
    
    private void staffManagement() throws StaffNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** CARS :: Administration Operation :: Staff Management ***\n");
            System.out.println("1: Add Staff");
            System.out.println("2: View Staff Details");
            System.out.println("3: Update Staff");
            System.out.println("4: Delete Staff");
            System.out.println("5: View All Staff");
            System.out.println("6: Back\n");

            response = 0;
            
            while(response < 1 || response > 6)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                   addStaff();
                }
                else if(response == 2)
                {
                    viewStaffDetails();
                }
                else if(response == 3)
                {
                    updateStaff();
                }
                else if(response == 4)
                {
                    deleteStaff();
                }
                else if(response == 5)
                {
                    viewAllStaffs();
                }
                else if(response == 6)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 6)
            {
                break;
            }
        }
    }
    
    private void addStaff() 
    {
        Scanner scanner = new Scanner(System.in);
        StaffEntity staffEntity = new StaffEntity();
        
        System.out.println("*** CARS :: Administration Operation :: Add New Staff ***\n");
        System.out.print("Enter First Name> ");
        staffEntity.setFirstName(scanner.nextLine().trim());
        System.out.print("Enter Last Name> ");
        staffEntity.setLastName(scanner.nextLine().trim());
        System.out.print("Enter Username> ");
        staffEntity.setUsername(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        staffEntity.setPassword(scanner.nextLine().trim());

        Long newStaffId = staffEntitySessionBeanRemote.createStaffEntity(staffEntity);
        System.out.println(newStaffId + " Staff has been added successfully\n");
    }
    
    private void viewStaffDetails()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** CARS :: Administration Operation :: View Staff Details***\n");
        System.out.print("Enter Staff ID> ");
        Long staffId = scanner.nextLong();
        
        try
        {
            StaffEntity staffEntity = staffEntitySessionBeanRemote.retrieveStaffEntityByStaffId(staffId);
            System.out.printf("%-15s|%-20s|%-30s|%-20s|%-20s\n", "Doctor ID", "First Name", "Last Name", "Username", "Password");
            System.out.printf("%-15s|%-20s|%-30s|%-20s|%-20s\n", staffEntity.getStaffId().toString(), staffEntity.getFirstName(), staffEntity.getLastName(), staffEntity.getUsername(), staffEntity.getPassword());         
            System.out.println("------------------------");

        }
        catch(StaffNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving staff: " + ex.getMessage() + "\n");
        }
    }
    
    private void updateStaff() throws StaffNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Staff ID> ");
        Long staffId = scanner.nextLong();
        StaffEntity staffEntity  = staffEntitySessionBeanRemote.retrieveStaffEntityByStaffId(staffId);
        String input;
        
        scanner.nextLine();
        
        System.out.println("*** CARS :: Administration Operation :: Update Staff ***\n");
        System.out.print("Enter First Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            staffEntity.setFirstName(input);
        }
        
        System.out.print("Enter Last Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            staffEntity.setLastName(input);
        }
        
        System.out.print("Enter Username (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            staffEntity.setUsername(input);
        }
        
        System.out.print("Enter Password (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            staffEntity.setPassword(input);
        }
        
        staffEntitySessionBeanRemote.updateStaffEntity(staffEntity);
        System.out.println("Staff updated successfully!\n");
    }
    
    private void deleteStaff() throws StaffNotFoundException  
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Staff ID> ");
        Long staffId = scanner.nextLong();

        String input;
        
        scanner.nextLine();

        try 
        {
            StaffEntity staffEntity = staffEntitySessionBeanRemote.retrieveStaffEntityByStaffId(staffId);
            System.out.println("*** CARS :: Administration Operation :: Delete Staff ***\n");
            System.out.printf("Confirm Delete Staff %s (Username: %s) (Enter 'Y' to Delete)> ", staffEntity.getFirstName(), staffEntity.getUsername());
            input = scanner.nextLine().trim();

            if (input.equals("Y")) 
            {
                staffEntitySessionBeanRemote.deleteStaffEntity(staffId);
                System.out.println("Staff deleted successfully!\n");
            } 
            else 
            {
                System.out.println("Staff NOT deleted!\n");
            }
        } 
        catch (StaffNotFoundException ex) 
        {
            System.out.println("An error has occurred while retrieving staff: " + ex.getMessage() + "\n");
        }
    }
    
    private void viewAllStaffs() 
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** CARS :: Administration Operation :: View All Staffs ***\n");
        
        List<StaffEntity> staffEntities = staffEntitySessionBeanRemote.retrieveAllStaffs();
        System.out.printf("%-15s|%-20s|%-20s|%-20s|%-20s\n", "Staff ID", "First Name", "Last Name", "Username", "Password");

        for(StaffEntity staffEntity : staffEntities)
        {
            System.out.printf("%-15s|%-20s|%-20s|%-20s|%-20s\n", staffEntity.getStaffId().toString(), staffEntity.getFirstName(), staffEntity.getLastName(), staffEntity.getUsername(), staffEntity.getPassword());         
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
}
