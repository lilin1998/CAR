package carsclient;

import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import ejb.session.stateless.StaffEntitySessionBeanRemote;
import entity.GenderEnum;
import entity.PatientEntity;
import java.util.List;
import java.util.Scanner;
import util.exception.AppointmentNotFoundException;
import util.exception.DeletePatientException;
import util.exception.DoctorNotFoundException;
import util.exception.PatientNotFoundException;
import util.exception.StaffNotFoundException;
import util.exception.UpdatePatientException;

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
                    viewPatientDetails();
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
        System.out.println("Patient ID " + newPatientId + " has been added successfully\n");
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
            System.out.printf("%-15s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s\n", "Patient ID", "Identity Number", "Password", "First Name", "Last Name", "Gender", "Age", "Phone", "Address");
            System.out.printf("%-15s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s\n", patientEntity.getPatientId().toString(), patientEntity.getIdentityNumber(), patientEntity.getPassword(), patientEntity.getFirstName(), patientEntity.getLastName(), patientEntity.getGender().toString(), patientEntity.getAge().toString(), patientEntity.getPhone(), patientEntity.getAddress());         
            System.out.println("------------------------");

        }
        catch(PatientNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving patient: " + ex.getMessage() + "\n");
        }
    }
    
    private void updatePatient() throws PatientNotFoundException, UpdatePatientException
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Patient ID> ");
        Long patientId = scanner.nextLong();
        PatientEntity patientEntity = patientSessionBeanRemote.retrievePatientByPatientId(patientId);
        String input;
        Integer integerInput;
        
        System.out.println("*** CARS :: Administration Operation :: Update Patient ***\n");
        System.out.print("Enter Identity Number (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            patientEntity.setIdentityNumber(input);
        }
        
        System.out.print("Enter Password (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            patientEntity.setPassword(input);
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
        System.out.printf("%-15s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s\n", "Patient ID", "Identity Number", "Password", "First Name", "Last Name", "Gender", "Age", "Phone", "Address");

        for(PatientEntity patientEntity:patientEntities)
        {
            System.out.printf("%-15s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s\n", patientEntity.getPatientId().toString(), patientEntity.getIdentityNumber(), patientEntity.getPassword(), patientEntity.getFirstName(), patientEntity.getLastName(), patientEntity.getGender().toString(), patientEntity.getAge().toString(), patientEntity.getPhone(), patientEntity.getAddress());         
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
}
