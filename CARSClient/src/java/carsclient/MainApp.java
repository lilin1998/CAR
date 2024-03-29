package carsclient;

import ejb.session.stateful.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateful.LeaveEntitySessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import ejb.session.stateless.QueueSessionBeanRemote;
import ejb.session.stateless.StaffEntitySessionBeanRemote;
import entity.StaffEntity;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.util.Scanner;
import util.exception.AppointmentNotFoundException;
import util.exception.CreateAppointmentException;
import util.exception.DeletePatientException;
import util.exception.DoctorNotFoundException;
import util.exception.DoctorRegistrationExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.LeaveApplicationException;
import util.exception.PasswordException;
import util.exception.PatientIdentityNumberExist;
import util.exception.PatientNotFoundException;
import util.exception.QueueNotFoundException;
import util.exception.StaffNotFoundException;
import util.exception.StaffUsernameExistException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdatePatientException;

public class MainApp 
{
    private StaffEntitySessionBeanRemote staffEntitySessionBeanRemote;
    private DoctorSessionBeanRemote doctorSessionBeanRemote;
    private PatientSessionBeanRemote patientSessionBeanRemote;
    private AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;
    private LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote;
    private QueueSessionBeanRemote queueSessionBeanRemote;
    
    private RegistrationOperationModule registrationOperationModule;
    private AppointmentOperationModule appointmentOperationModule;
    private AdministrationOperationModule administrationOperationModule;
    
    private StaffEntity currentStaffEntity;
    
    public MainApp() 
    {
    }

    
    
    public MainApp(StaffEntitySessionBeanRemote staffEntitySessionBeanRemote, DoctorSessionBeanRemote doctorSessionBeanRemote, PatientSessionBeanRemote patientSessionBeanRemote, AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote, LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote, QueueSessionBeanRemote queueSessionBeanRemote) 
    {
        this();
        
        this.staffEntitySessionBeanRemote = staffEntitySessionBeanRemote;
        this.doctorSessionBeanRemote = doctorSessionBeanRemote;
        this.patientSessionBeanRemote = patientSessionBeanRemote;
        this.appointmentEntitySessionBeanRemote = appointmentEntitySessionBeanRemote;
        this.leaveEntitySessionBeanRemote = leaveEntitySessionBeanRemote;
        this.queueSessionBeanRemote = queueSessionBeanRemote;
    }

    

    public void runApp() throws DoctorNotFoundException, PatientNotFoundException, UpdatePatientException, NoSuchAlgorithmException, NoSuchProviderException, PasswordException, DeletePatientException, AppointmentNotFoundException, StaffNotFoundException, LeaveApplicationException, CreateAppointmentException, SQLException, PatientIdentityNumberExist, UnknownPersistenceException, InputDataValidationException, StaffUsernameExistException, DoctorRegistrationExistException, QueueNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to Clinic Appointment Registration System (CARS) ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    try
                    {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        registrationOperationModule = new RegistrationOperationModule(doctorSessionBeanRemote, appointmentEntitySessionBeanRemote, patientSessionBeanRemote, currentStaffEntity, leaveEntitySessionBeanRemote, queueSessionBeanRemote);
                        appointmentOperationModule = new AppointmentOperationModule(doctorSessionBeanRemote, patientSessionBeanRemote, appointmentEntitySessionBeanRemote, leaveEntitySessionBeanRemote);
                        administrationOperationModule = new AdministrationOperationModule(doctorSessionBeanRemote, patientSessionBeanRemote, staffEntitySessionBeanRemote, leaveEntitySessionBeanRemote);
                        menuMain();
                    }
                    catch(InvalidLoginCredentialException ex) 
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 2)
            {
                break;
            }
        }
    }
    
    
    
    private void doLogin() throws InvalidLoginCredentialException
    {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** CARS :: Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0)
        {
            currentStaffEntity = staffEntitySessionBeanRemote.staffLogin(username, password);      
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
    

    private void menuMain() throws DoctorNotFoundException, PatientNotFoundException, UpdatePatientException, NoSuchAlgorithmException, NoSuchProviderException, PasswordException, DeletePatientException, AppointmentNotFoundException, StaffNotFoundException, LeaveApplicationException, CreateAppointmentException, SQLException, PatientIdentityNumberExist, UnknownPersistenceException, InputDataValidationException, StaffUsernameExistException, DoctorRegistrationExistException, QueueNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** CARS :: Main ***\n");
            System.out.println("You are login as " + currentStaffEntity.getFirstName() + " " + currentStaffEntity.getLastName() + "\n");
            System.out.println("1: Registration Operation");
            System.out.println("2: Appointment Operation");
            System.out.println("3: Administration Operation");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1)
                {
                    registrationOperationModule.registrationOperation();                    
                }
                else if (response == 2)
                {
                    appointmentOperationModule.appointmentOperation();
                }
                else if (response == 3)
                {
                    administrationOperationModule.administrationOperation();
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
}
