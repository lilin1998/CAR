package carsclient;

import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import entity.GenderEnum;
import static entity.GenderEnum.F;
import static entity.GenderEnum.M;
import entity.PatientEntity;
import entity.StaffEntity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Scanner;
import util.exception.PasswordException;

public class RegistrationOperationModule {
    private DoctorSessionBeanRemote doctorSessionBeanRemote;
    private AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;
    private PatientSessionBeanRemote patientSessionBeanRemote;
    
    private StaffEntity currentStaffEntity;

    public RegistrationOperationModule() {
    }

    
    
    public RegistrationOperationModule(DoctorSessionBeanRemote doctorSessionBeanRemote, AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote, PatientSessionBeanRemote patientSessionBeanRemote, StaffEntity currentStaffEntity) {
        this.doctorSessionBeanRemote = doctorSessionBeanRemote;
        this.appointmentEntitySessionBeanRemote = appointmentEntitySessionBeanRemote;
        this.patientSessionBeanRemote = patientSessionBeanRemote;
        this.currentStaffEntity = currentStaffEntity;
    }
    
    
    
    public void registrationOperation() throws NoSuchAlgorithmException, NoSuchProviderException, PasswordException
    {
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** CARS :: Registration Operation ***\n");
            System.out.println("1: Register New Patient");
            System.out.println("2: Register Walk-In Consultation");
            System.out.println("3: Register Consultation By Appointment");
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    registerNewPatient();
                }
                else if(response == 2)
                {
                    registerWalkinConsultation();
                }
                else if(response == 3)
                {
                    registerConsultationByAppointment();
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
    
    
    
    private void registerNewPatient() throws NoSuchAlgorithmException, NoSuchProviderException, PasswordException 
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
    
    
    private void registerWalkinConsultation()
    {
    
    }

    
    
    private void registerConsultationByAppointment()
    {
    
    }
}
