package selfservicekiosk;

import ejb.session.stateful.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateful.LeaveEntitySessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import ejb.session.stateless.QueueSessionBeanRemote;
import entity.GenderEnum;
import entity.PatientEntity;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Scanner;
import util.exception.AppointmentNotFoundException;
import util.exception.CreatePatientException;
import util.exception.DoctorNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.LeaveApplicationException;
import util.exception.PasswordException;
import util.exception.PatientNotFoundException;
import util.exception.QueueNotFoundException;

public class MainApp 
{

    private DoctorSessionBeanRemote doctorSessionBeanRemote;
    private PatientSessionBeanRemote patientSessionBeanRemote;
    private AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;
    private LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote;
    private QueueSessionBeanRemote queueSessionBeanRemote;
    
    private SelfServiceAppointmentOperationModule selfServiceAppointmentOperationModule;
    private SelfServiceRegistrationOperationModule selfServiceRegistrationOperationModule;
    
    private PatientEntity currentPatientEntity;
    
    public MainApp() 
    {
    }

    
    
    public MainApp(DoctorSessionBeanRemote doctorSessionBeanRemote, PatientSessionBeanRemote patientSessionBeanRemote, AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote, LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote, QueueSessionBeanRemote queueSessionBeanRemote) 
    {
        this();
        
        this.doctorSessionBeanRemote = doctorSessionBeanRemote;
        this.patientSessionBeanRemote = patientSessionBeanRemote;
        this.appointmentEntitySessionBeanRemote = appointmentEntitySessionBeanRemote;
        this.leaveEntitySessionBeanRemote = leaveEntitySessionBeanRemote;
        this.queueSessionBeanRemote = queueSessionBeanRemote;
    }

    
    
    public void runApp() throws NoSuchAlgorithmException, NoSuchProviderException, DoctorNotFoundException, AppointmentNotFoundException, PatientNotFoundException, LeaveApplicationException, QueueNotFoundException
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
                        
                        selfServiceRegistrationOperationModule = new SelfServiceRegistrationOperationModule(currentPatientEntity, doctorSessionBeanRemote, appointmentEntitySessionBeanRemote, patientSessionBeanRemote, leaveEntitySessionBeanRemote, queueSessionBeanRemote);
                        selfServiceAppointmentOperationModule = new SelfServiceAppointmentOperationModule(currentPatientEntity, doctorSessionBeanRemote, patientSessionBeanRemote, appointmentEntitySessionBeanRemote, leaveEntitySessionBeanRemote);                   
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
        System.out.print("Enter 6-digit Password> ");
        
        String passwordToHash = scanner.nextLine().trim();
        String securePassword = patientSessionBeanRemote.getSecurePassword(passwordToHash);
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
            patientSessionBeanRemote.inputIsIncorrect(newPatientEntity);
            try 
            {
                patientSessionBeanRemote.checkPassword(passwordToHash);
                Long newPatientId = patientSessionBeanRemote.createPatient(newPatientEntity);
                System.out.println("Patient has been registered successfully!\n");
            } 
            catch (PasswordException e) 
            {
                System.out.println("An error has occured while registering new patient: " + e.getMessage() + "\n");
            }     
        } 
        catch (CreatePatientException ex) 
        {
            System.out.println(ex.getMessage());
        }
    }
    
    
    
    public void doLogin() throws InvalidLoginCredentialException, NoSuchAlgorithmException, NoSuchProviderException, PatientNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
        String identityNo = "";
        String password = "";
        
        System.out.println("*** Self-Service Kiosk :: Login ***\n");
        System.out.print("Enter Identity Number> ");
        identityNo = scanner.nextLine().trim();
        System.out.print("Enter 6-digit password> ");
        password = scanner.nextLine().trim();
        
        if(identityNo.length() > 0 && password.length() > 0)
        {
            currentPatientEntity = patientSessionBeanRemote.patientLogin(identityNo, password); 
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!\n");
        }
    }
    
    
    
    private void menuMain() throws DoctorNotFoundException, AppointmentNotFoundException, PatientNotFoundException, LeaveApplicationException, QueueNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Self-Service Kiosk :: Main ***\n");
            System.out.println("You are login as " + currentPatientEntity.getFirstName() + " " + currentPatientEntity.getLastName() + "\n");
            System.out.println("1: Registration Walk-In Consultation");
            System.out.println("2: Register Consultation By Appointment");
            System.out.println("3: View Appointments");
            System.out.println("4: Add Appointment");
            System.out.println("5: Cancel Appointment");
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
