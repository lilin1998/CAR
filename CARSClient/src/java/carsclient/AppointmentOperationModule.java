package carsclient;

import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import entity.AppointmentEntity;
import entity.DoctorEntity;
import entity.PatientEntity;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import util.exception.AppointmentNotFoundException;
import util.exception.CreateAppointmentException;
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
    
    
    
    public void appointmentOperation() throws DoctorNotFoundException, PatientNotFoundException, AppointmentNotFoundException
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
                System.out.printf("%-3s|%-12s|%-7s|%-13s\n", "Id", "Date", "Time", "Doctor");
                
                for (AppointmentEntity appointmentEntity:patientAppointments)
                {
                    String doctorName = appointmentEntity.getDoctorEntity().getFirstName() + " " + appointmentEntity.getDoctorEntity().getLastName();
                    String timeString = appointmentEntity.getTime().toString();
                    
                    System.out.printf("%-3s|%-12s|%-7s|%-13s\n", appointmentEntity.getAppointmentId(), appointmentEntity.getDate(), timeString.substring(0, 5), doctorName);
                }
                System.out.print("\n");
            }
        }
        catch(PatientNotFoundException ex)
        {
              System.out.println("Failed to retrieve list of appointments!\n");
        }
    }
    
    
    
    private void addNewAppointment() throws DoctorNotFoundException, PatientNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
        Long doctorId;
        String date;
        DoctorEntity doctor = null;
        PatientEntity patient = null;
        List<String> tempList = new ArrayList<>();
        
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
        
        //set two days validation rule
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 2);
        Date twoDaysLater = new Date((c.getTime()).getTime());
            
        //check if doctor exists
        try
        {
            doctor = doctorSessionBeanRemote.retrieveDoctorByDoctorId(doctorId);
            //check if doctor is on leave
            
            try 
            {
                //check that appointment is not on a weekend
                if (day == Calendar.SATURDAY || day == Calendar.SUNDAY)
                {
                    throw new CreateAppointmentException("Appointment is not available on weekends!\n");
                }
                //check that apppointment is at least two days later
                else if (actualDate.compareTo(twoDaysLater) < 0) 
                {
                    throw new CreateAppointmentException("Appointment must be booked at least two days in advance!\n");
                }
                else 
                {
                    System.out.println("Availability for " + doctor.getFirstName() + " " + doctor.getLastName() + " on " + date + ":");
                    List<AppointmentEntity> appointments = appointmentEntitySessionBeanRemote.retrieveAppointmentByDoctorId(doctorId);
                    if (appointments.isEmpty()) 
                    {
                        if (day >= Calendar.MONDAY && day <= Calendar.WEDNESDAY) 
                        {
                            for (String time : timeSlot) 
                            {
                                System.out.print(time + " ");
                            }
                            System.out.print("\n");
                        }    
                        else if (day == Calendar.THURSDAY) 
                        {
                            for (String time : timeSlotThur) 
                            {
                                System.out.print(time + " ");
                            }
                            System.out.print("\n");
                        } 
                        else //Friday
                        {
                            for (String time : timeSlotFri) 
                            {
                                System.out.print(time + " ");
                            }
                            System.out.print("\n");
                        }
                    }
                    else 
                    {
                        for (AppointmentEntity appointmentEntity : appointments) 
                        {
                            if (actualDate.equals(appointmentEntity.getDate())) 
                            {
                                String unavailableTime = appointmentEntity.getTime().toString();
                                tempList.add(unavailableTime.substring(0, 5));
                            }
                        }
                        if (tempList.isEmpty())
                        {
                            if (day >= Calendar.MONDAY && day <= Calendar.WEDNESDAY) 
                            {
                                for (int i = 0; i < timeSlot.length; i++)
                                {
                                    System.out.print(timeSlot[i] + " ");
                                }
                                System.out.print("\n");
                            }
                             else if (day == Calendar.THURSDAY) 
                            {
                                for (int i = 0; i < timeSlotThur.length; i++)
                                {
                                    System.out.print(timeSlotThur[i] + " ");
                                }
                                System.out.print("\n");
                            }
                            else //Friday
                            {
                                for (int i = 0; i < timeSlotFri.length; i++)
                                {
                                    System.out.print(timeSlotFri[i] + " ");
                                }
                                System.out.print("\n");
                            }
                        }
                        else
                        {
                            if (day >= Calendar.MONDAY && day <= Calendar.WEDNESDAY) 
                            {
                                for (int i = 0; i < timeSlot.length; i++)
                                {
                                    for (int j = 0; j < tempList.size(); j++)
                                    {
                                        if (!timeSlot[i].equals(tempList.get(j)) && j == tempList.size() - 1)
                                    {
                                        System.out.print(timeSlot[i] + " ");
                                    }
                                }
                            }
                            System.out.print("\n");
                            tempList.clear();
                        } 
                        else if (day == Calendar.THURSDAY) 
                        {
                            for (int i = 0; i < timeSlotThur.length; i++)
                            {
                                for (int j = 0; j < tempList.size(); j++)
                                {
                                    if (!timeSlotThur[i].equals(tempList.get(j)) && j == tempList.size() - 1)
                                    {
                                        System.out.print(timeSlotThur[i] + " ");
                                    }
                                }
                            }
                            System.out.print("\n");
                            tempList.clear();
                        } 
                        else //Friday
                        {
                            for (int i = 0; i < timeSlotFri.length; i++)
                            {
                                for (int j = 0; j < tempList.size(); j++)
                                {
                                    if (!timeSlotFri[i].equals(tempList.get(j)) && j == tempList.size() - 1)
                                    {
                                        System.out.print(timeSlotFri[i] + " ");
                                    }
                                }
                            }
                            System.out.print("\n");
                            tempList.clear();
                        }
                        }
                    }
                    
                    System.out.print("Enter Time> ");
                    String timeInput = scanner.nextLine().trim();
                    String timeformat = timeInput + ":00";
                    Time time = Time.valueOf(timeformat);

                    System.out.print("Enter Patient Identity Number> ");
                    String patientIdentityNo = scanner.nextLine().trim();
                    
                    try 
                    {
                        patient =  patientSessionBeanRemote.retrievePatientByPatientIdentityNumber(patientIdentityNo);
                    } 
                    catch (PatientNotFoundException ex) 
                    {
                        System.out.println("Error in retrieving Patient's identity Number!\n");
                    }
        
                    AppointmentEntity newAppointmentEntity = new AppointmentEntity(doctor, actualDate, time, patient);
                    appointmentEntitySessionBeanRemote.createNewAppointment(newAppointmentEntity);
                    patientSessionBeanRemote.updatePatientList(patientIdentityNo);
                    doctorSessionBeanRemote.updateDoctorList(doctorId);

                    String patientName = patient.getFirstName() + " " + patient.getLastName();
                    String doctorName = doctor.getFirstName() + " " + doctor.getLastName();

                    System.out.println(patientName + " appointment with " + doctorName + " at " + timeInput + " on " + date + " has been added.\n");
                }
            }
            catch (CreateAppointmentException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        catch (DoctorNotFoundException ex)
        {
            System.out.println("Doctor does not exist!\n");
        }
    }
    
    
    
    private void cancelExistingAppointment() throws DoctorNotFoundException, AppointmentNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** CARS :: Appointment Operation :: Cancel Appointment ***\n");
        System.out.print("Enter Patient Identity Number> ");
        String patientIdentityNo = scanner.nextLine().trim();
        System.out.print("\n");
        
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
                System.out.printf("%-3s|%-12s|%-7s|%-13s\n", "Id", "Date", "Time", "Doctor");
                
                for (AppointmentEntity appointmentEntity:patientAppointments)
                {
                    String doctorName = appointmentEntity.getDoctorEntity().getFirstName() + " " + appointmentEntity.getDoctorEntity().getLastName();
                    String timeString = appointmentEntity.getTime().toString();
                    
                    System.out.printf("%-3s|%-12s|%-7s|%-13s\n", appointmentEntity.getAppointmentId(), appointmentEntity.getDate(), timeString.substring(0, 5), doctorName);
                }
                System.out.print("\n");
            }
            
            System.out.print("Enter Appointment Id> ");
            Long appointmentId = scanner.nextLong();
           
            AppointmentEntity appointmentEntity = appointmentEntitySessionBeanRemote.retrieveAppointmentByAppointmentId(appointmentId);
            DoctorEntity doctorEntity = appointmentEntity.getDoctorEntity();
            String doctorName = doctorEntity.getFirstName() + " " + doctorEntity.getLastName();
            String timeString = appointmentEntity.getTime().toString();
            String dateString = appointmentEntity.getDate().toString();
            String patientName = appointmentEntity.getPatientEntity().getFirstName() + " " + appointmentEntity.getPatientEntity().getLastName();
            
            //remove appointment from database
            appointmentEntitySessionBeanRemote.deleteAppointment(appointmentId);
            doctorSessionBeanRemote.updateDoctorList(doctorEntity.getDoctorId());
            patientSessionBeanRemote.updatePatientList(patientIdentityNo);
            
            System.out.println(patientName + " appointment with " + doctorName + " at " + timeString.substring(0, 5) + " on " + dateString + " has been cancelled.\n");
        }
        catch(PatientNotFoundException ex)
        {
              System.out.println("Failed to retrieve list of appointments!\n");
        }
    }
}
