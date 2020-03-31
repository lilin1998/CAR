package carsclient;

import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import ejb.session.stateless.StaffEntitySessionBeanRemote;
import entity.GenderEnum;
import entity.PatientEntity;
import java.util.Scanner;
import util.exception.AppointmentNotFoundException;
import util.exception.DoctorNotFoundException;
import util.exception.PatientNotFoundException;
import util.exception.StaffNotFoundException;

public class AdministrationOperationModule 
{
    private DoctorSessionBeanRemote doctorSessionBeanRemote;
    private PatientSessionBeanRemote patientSessionBeanRemote;
    private StaffEntitySessionBeanRemote staffEntitySessionBeanRemote;

    public AdministrationOperationModule() 
    {
    }

    public AdministrationOperationModule(DoctorSessionBeanRemote doctorSessionBeanRemote, PatientSessionBeanRemote patientSessionBeanRemote, StaffEntitySessionBeanRemote staffEntitySessionBeanRemote) 
    {
        this();
        this.doctorSessionBeanRemote = doctorSessionBeanRemote;
        this.patientSessionBeanRemote = patientSessionBeanRemote;
        this.staffEntitySessionBeanRemote = staffEntitySessionBeanRemote;
    }
    
    public void administrationOperation() throws PatientNotFoundException
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
//                    doctorManagement();
                }
                else if(response == 3)
                {
//                    staffManagement();
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
    
    private void patientManagement() throws PatientNotFoundException
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
//                    viewPatientDetails();
                }
                else if(response == 3)
                {
//                    updatePatient();
                }
                else if(response == 4)
                {
//                    deletePatient();
                }
                else if(response == 5)
                {
//                    viewAllPatients();
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
    
    private void addPatient() 
    {
        Scanner scanner = new Scanner(System.in);
        PatientEntity newPatientEntity = new PatientEntity();
        
        System.out.println("*** CARS :: Administration Operation :: Add New Patient ***\n");
        System.out.print("Enter Identity Number> ");
        newPatientEntity.setIdentityNumber(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newPatientEntity.setPassword(scanner.nextLine().trim());
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

        Long newPatientId = patientSessionBeanRemote.createPatient(newPatientEntity);
        System.out.println(newPatientId + " Patient has been added successfully\n");
    }

}
