package carsclient;

import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import entity.AppointmentEntity;
import entity.DoctorEntity;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import util.exception.DoctorNotFoundException;
import util.exception.PatientNotFoundException;

public class AppointmentOperationModule 
{
    private DoctorSessionBeanRemote doctorSessionBeanRemote;
    private PatientSessionBeanRemote patientSessionBeanRemote;
    private AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;
    
    private String[] timeSlot = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};
    private String[] timeSlotThur = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"};
    private String[] timeSlotFri = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"};

    public AppointmentOperationModule() 
    {
    }

    
    
    public AppointmentOperationModule(DoctorSessionBeanRemote doctorSessionBeanRemote, PatientSessionBeanRemote patientSessionBeanRemote, AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote) 
    {
        this();
        
        this.doctorSessionBeanRemote = doctorSessionBeanRemote;
        this.patientSessionBeanRemote = patientSessionBeanRemote;
        this.appointmentEntitySessionBeanRemote = appointmentEntitySessionBeanRemote;
    }
    
    
    
    public void appointmentOperation() throws DoctorNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** CARS :: Appointment Operation ***\n");
            System.out.println("1: View Patient Appointments");
            System.out.println("2: Add Appointment");
            System.out.println("3: Cancel Appointment");
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    viewPatientAppointments();
                }
                else if(response == 2)
                {
                    addNewAppointment();
                }
                else if(response == 3)
                {
                    cancelExistingAppointment();
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
    
    
    
    private void viewPatientAppointments()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** CARS :: Appointment Operation :: View Patient Appointments ***\n");
        System.out.print("Enter Patient Identity Number> ");
        String patientIdentityNo = scanner.nextLine().trim();
        System.out.println();
      
        try
        { 
            List<AppointmentEntity> patientAppointments = appointmentEntitySessionBeanRemote.retrieveAppointmentByPatientIdentityNo(patientIdentityNo);
            if (patientAppointments.isEmpty())
            {
                System.out.println("Patient has no upcoming appointments!\n");
            }
            else 
            {
                System.out.println("Appointments:");
                System.out.printf("%-5s|%-13s|%-13s|%-20s\n", "Id", "Date", "Time", "Doctor");
                
                for (AppointmentEntity appointmentEntity:patientAppointments)
                {
                    String doctorName = appointmentEntity.getDoctorEntity().getFirstName() + " " + appointmentEntity.getDoctorEntity().getLastName();
                    System.out.printf("%-5s|%-13s|%-13s|%-20s\n", appointmentEntity.getPatientEntity().getIdentityNumber(), appointmentEntity.getDate(), appointmentEntity.getTime(), doctorName);
                }
                System.out.println("");
            }
        }
        catch(PatientNotFoundException ex)
        {
              System.out.println("Failed to retrieve list of appointments!\n");
        }
    }
    
    
    
    private void addNewAppointment() throws DoctorNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
        Long doctorId;
        String date;
        DoctorEntity doctor;
        
        System.out.println("*** CARS :: Appointment Operation :: Add Appointment ***\n");
        
        List<DoctorEntity> doctors = doctorSessionBeanRemote.retrieveAllDoctors();
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
        System.out.print("Enter Date> ");
        date = scanner.nextLine();

        Date actualDate = Date.valueOf(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(actualDate);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        
        //check if doctor exists
        try
        {
            doctor = doctorSessionBeanRemote.retrieveDoctorByDoctorId(doctorId);
            System.out.println("Availability for " + doctor.getFirstName() + " " + doctor.getLastName() + " on " + date + ":");
            
            //check that appointment is not on a weekend
            if (day == Calendar.SATURDAY || day == Calendar.SUNDAY)
            {
                System.out.println("Appointment is not available on weekends!\n");
            }
            else 
            {
                List<AppointmentEntity> appointments = appointmentEntitySessionBeanRemote.retrieveAppointmentByDoctorId(doctorId);
                if (appointments.isEmpty()) 
                {
                    if (day >= Calendar.MONDAY && day <= Calendar.WEDNESDAY) 
                    {
                        for (String time : timeSlot) 
                        {
                            System.out.print(time + " ");
                        }
                        System.out.println("\n");
                    } 
                    else if (day == Calendar.THURSDAY) 
                    {
                        for (String time : timeSlotThur) 
                        {
                            System.out.print(time + " ");
                        }
                        System.out.println("\n");
                    } 
                    else //Friday
                    {
                        for (String time : timeSlotFri) 
                        {
                            System.out.print(time + " ");
                        }
                        System.out.println("\n");
                    }
                }
                else 
                {
                    for (AppointmentEntity appointmentEntity : appointments) 
                    {
                        if (actualDate.equals(appointmentEntity.getDate())) 
                        {
                            if (day >= Calendar.MONDAY && day <= Calendar.WEDNESDAY) 
                            {

                            } 
                            else if (day == Calendar.THURSDAY) 
                            {

                            } 
                            else //Friday
                            {

                            }
                        }
                    }
                }
            }
        }
        catch (DoctorNotFoundException ex)
        {
            System.out.println("Doctor does not exist!\n");
        }
        
        System.out.print("Enter Time> ");
        String timeInput = scanner.nextLine().trim();
        System.out.print("Enter Patient Identity Number> ");
        String patientIdentityNo = scanner.nextLine().trim();
        
    }
    
    
    
    private void cancelExistingAppointment()
    {
        
    }
}
