package selfservicekiosk;

import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateless.LeaveEntitySessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import entity.GenderEnum;
import entity.PatientEntity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Scanner;
import util.exception.AppointmentNotFoundException;
import util.exception.DoctorNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.LeaveApplicationException;
import util.exception.PasswordException;
import util.exception.PatientNotFoundException;

public class MainApp 
{

    private DoctorSessionBeanRemote doctorSessionBeanRemote;
    private PatientSessionBeanRemote patientSessionBeanRemote;
    private AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;
    private LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote;
    
    private SelfServiceAppointmentOperationModule selfServiceAppointmentOperationModule;
    private SelfServiceRegistrationOperationModule selfServiceRegistrationOperationModule;
    
    private PatientEntity currentPatientEntity;
    
    public MainApp() 
    {
    }

    
    
    public MainApp(DoctorSessionBeanRemote doctorSessionBeanRemote, PatientSessionBeanRemote patientSessionBeanRemote, AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote, LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote) 
    {
        this();
        
        this.doctorSessionBeanRemote = doctorSessionBeanRemote;
        this.patientSessionBeanRemote = patientSessionBeanRemote;
        this.appointmentEntitySessionBeanRemote = appointmentEntitySessionBeanRemote;
        this.leaveEntitySessionBeanRemote = leaveEntitySessionBeanRemote;
    }

    
    
    public void runApp() throws NoSuchAlgorithmException, NoSuchProviderException, DoctorNotFoundException, AppointmentNotFoundException, PatientNotFoundException, LeaveApplicationException
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to Self-Service Kiosk ***\n");
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
                    try
                    {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        selfServiceRegistrationOperationModule = new SelfServiceRegistrationOperationModule(doctorSessionBeanRemote, appointmentEntitySessionBeanRemote, patientSessionBeanRemote,leaveEntitySessionBeanRemote);
                        selfServiceAppointmentOperationModule = new SelfServiceAppointmentOperationModule(doctorSessionBeanRemote, patientSessionBeanRemote, appointmentEntitySessionBeanRemote, leaveEntitySessionBeanRemote);                   
                        menuMain();
                    }
                    catch(InvalidLoginCredentialException ex) 
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
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
    
    
    
    public void doRegister() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        Scanner scanner = new Scanner(System.in);
        PatientEntity newPatientEntity = new PatientEntity();
        
        System.out.println("*** Self-Service Kiosk :: Register ***\n");
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
            System.out.println("Registration is successful!\n");
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
    
    
    
    public void doLogin() throws InvalidLoginCredentialException, NoSuchAlgorithmException, NoSuchProviderException
    {
        Scanner scanner = new Scanner(System.in);
        String identityNo = "";
        String password = "";
        
        System.out.println("*** Self-Service Kiosk :: Login ***\n");
        System.out.print("Enter Identity Number> ");
        identityNo = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        //ELIZ: To check encryption of password
         /*
        if(identityNo.length() > 0 && password.length() > 0)
        {
            byte[] salt = getSalt();
            String securePassword = getSecurePassword(password, salt);
            System.out.println(securePassword);
            currentPatientEntity = patientSessionBeanRemote.patientLogin(identityNo, securePassword);      
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
        */
    }
    
    
    
    private void menuMain() throws DoctorNotFoundException, AppointmentNotFoundException, PatientNotFoundException, LeaveApplicationException
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Self-Service Kiosk :: Main ***\n");
            System.out.println("You are login as " + currentPatientEntity.getFirstName() + " " + currentPatientEntity.getLastName() + "\n");
            System.out.println("1: Registration Walk-In Consultation");
            System.out.println("2: Register Consultation By Appointment");
            System.out.println("3: View Appointments\n");
            System.out.println("4: Add Appointment\n");
            System.out.println("5: Cancel Appointment\n");
            System.out.println("6: Logout\n");
            response = 0;
            
            while(response < 1 || response > 6)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1)
                {
                    selfServiceRegistrationOperationModule.registerWalkinConsultation();                    
                }
                else if (response == 2)
                {
                    selfServiceRegistrationOperationModule.registerConsultationByAppointment();
                }
                else if (response == 3)
                {
                    selfServiceAppointmentOperationModule.viewPatientAppointments();
                }
                else if (response == 4)
                {
                    selfServiceAppointmentOperationModule.addNewAppointment();
                }
                else if (response == 5)
                {
                    selfServiceAppointmentOperationModule.cancelExistingAppointment();
                }
                else if (response == 6)
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
}
