package carsclient;

import ejb.session.stateful.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateful.LeaveEntitySessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import ejb.session.stateless.QueueSessionBeanRemote;
import entity.AppointmentEntity;
import entity.DoctorEntity;
import entity.GenderEnum;
import entity.LeaveEntity;
import entity.PatientEntity;
import entity.QueueEntity;
import entity.StaffEntity;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import util.exception.CreateAppointmentException;
import util.exception.CreatePatientException;
import util.exception.DoctorNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.PasswordException;
import util.exception.PatientIdentityNumberExist;
import util.exception.PatientNotFoundException;
import util.exception.QueueNotFoundException;
import util.exception.UnknownPersistenceException;

public class RegistrationOperationModule {
    private DoctorSessionBeanRemote doctorSessionBeanRemote;
    private AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;
    private PatientSessionBeanRemote patientSessionBeanRemote;
    private LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote;
    private QueueSessionBeanRemote queueSessionBeanRemote;
    
    private StaffEntity currentStaffEntity;

    private String[] timeSlot = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};
    private String[] timeSlotThur = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"};
    private String[] timeSlotFri = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"};
    
    public RegistrationOperationModule() {
    }

    
    
    public RegistrationOperationModule(DoctorSessionBeanRemote doctorSessionBeanRemote, AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote, PatientSessionBeanRemote patientSessionBeanRemote, StaffEntity currentStaffEntity, LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote, QueueSessionBeanRemote queueSessionBeanRemote) 
    {
        this();
        
        this.doctorSessionBeanRemote = doctorSessionBeanRemote;
        this.appointmentEntitySessionBeanRemote = appointmentEntitySessionBeanRemote;
        this.patientSessionBeanRemote = patientSessionBeanRemote;
        this.currentStaffEntity = currentStaffEntity;
        this.leaveEntitySessionBeanRemote = leaveEntitySessionBeanRemote;
        this.queueSessionBeanRemote = queueSessionBeanRemote;
    }
    
    

    public void registrationOperation() throws NoSuchAlgorithmException, NoSuchProviderException, PasswordException, DoctorNotFoundException, PatientNotFoundException, CreateAppointmentException, PatientIdentityNumberExist, UnknownPersistenceException, InputDataValidationException, QueueNotFoundException
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
    
    
    
    private void registerNewPatient() throws NoSuchAlgorithmException, NoSuchProviderException, PasswordException, PatientIdentityNumberExist, UnknownPersistenceException, InputDataValidationException 
    {
        Scanner scanner = new Scanner(System.in);
        PatientEntity newPatientEntity = new PatientEntity();
        
        System.out.println("*** CARS :: Registration Operation :: Add New Patient ***\n");
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
            catch (PatientIdentityNumberExist e)
            {
                System.out.println("It seems like: " + e.getMessage() + "\n");
            }
        } 
        catch (CreatePatientException ex) 
        {
            System.out.println(ex.getMessage());
        }
    }
    
    
    
    private void registerWalkinConsultation() throws DoctorNotFoundException, PatientNotFoundException, CreateAppointmentException, QueueNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
        List<AppointmentEntity> upcomingappointment = new ArrayList<>();
       
        System.out.println("*** CARS :: Registration Operation :: Register Walk-In Consultation ***\n");
        
        //retrieve today's date and current time
        Date dateToday = Date.valueOf(LocalDate.now());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateToday);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        Time timeNow = Time.valueOf(LocalTime.now());
        
        List<DoctorEntity> doctors = doctorSessionBeanRemote.retrieveAllDoctors();
        List<DoctorEntity> doctorsOnLeave = new ArrayList<>();
        
        for (DoctorEntity doctorEntity : doctors) //retrieve doctors on leave
        {
            LeaveEntity onLeave = leaveEntitySessionBeanRemote.retrieveLeaveByDateNDoctorId(doctorEntity.getDoctorId(), dateToday);
            if(onLeave != null) {
                doctorsOnLeave.add(doctorEntity);
            }
        }
        
        for(DoctorEntity onLeave : doctorsOnLeave) //remove the docs on leave 
        {
            doctors.remove(onLeave);
        }
        
        System.out.println("Doctor:");
        System.out.printf("%-3s|%-20s\n", "Id", "Name");
        
        for (DoctorEntity doctorEntity : doctors)
        {
            String doctorName = doctorEntity.getFirstName() + " " + doctorEntity.getLastName();
            System.out.printf("%-3s|%-20s\n", doctorEntity.getDoctorId(), doctorName);
        }
        System.out.println("");
        
        //retrieve latest display time
        Time threeHoursLater = Time.valueOf(LocalTime.now().plusHours(3));
        
        List<String> availableTimeList = new ArrayList<>();
        if (day >= Calendar.MONDAY && day <= Calendar.WEDNESDAY) 
        {
            for (String time : timeSlot) 
            {
                time += ":00";
                Time compareTime = Time.valueOf(time);
                if (compareTime.compareTo(timeNow) >= 0 && compareTime.compareTo(threeHoursLater) <= 0)
                {
                    availableTimeList.add(time);
                }
            }   
        }    
        else if (day == Calendar.THURSDAY) 
        {
            for (String time : timeSlotThur) 
            {
                time += ":00";
                Time compareTime = Time.valueOf(time);
                if (compareTime.compareTo(timeNow) >= 0 && compareTime.compareTo(threeHoursLater) <= 0)
                {
                    availableTimeList.add(time);
                }
            }
        } 
        else if (day == Calendar.FRIDAY)//Friday
        {
            for (String time : timeSlotFri) 
            {
                time += ":00";
                Time compareTime = Time.valueOf(time);
                if (compareTime.compareTo(timeNow) >= 0 && compareTime.compareTo(threeHoursLater) <= 0)
                {
                    availableTimeList.add(time);
                }    
            }       
        }

        System.out.println("Availability:");
        try
        {
            if (availableTimeList.isEmpty())
            {
                throw new CreateAppointmentException("No available slots at the moment!\n");
            }
            else 
            {
                System.out.printf("%-6s|", "Time");
                for(DoctorEntity doctorEntity : doctors)
                {
                    System.out.printf("%-2s|", doctorEntity.getDoctorId());
                }
                System.out.print("\n");
        
                for (int i = 0; i < availableTimeList.size(); i++)
                {
                    String slot = availableTimeList.get(i);
                    System.out.printf("%-6s|", slot.substring(0, 5));
                    Time time = java.sql.Time.valueOf(slot);
                    for (DoctorEntity doctorEntity : doctors) {
                        AppointmentEntity ap = appointmentEntitySessionBeanRemote.retrieveAppointmentByDoctorIdAndDateAndTime(doctorEntity.getDoctorId(), dateToday, time);
                        if (ap == null) { // don have appt
                            System.out.printf("%-2s|", "O");
                        }
                        else
                        {
                            System.out.printf("%-2s|", "X");                       
                        }
                    }
                    System.out.print("\n");
                }
                
            
                System.out.print("Enter Doctor Id> ");
                Long doctorId = scanner.nextLong();
                scanner.nextLine();
                System.out.print("Enter Patient Identity Number> ");
                String identityNumber = scanner.nextLine().trim();

                DoctorEntity doctor = doctorSessionBeanRemote.retrieveDoctorByDoctorId(doctorId);
                PatientEntity patient = patientSessionBeanRemote.retrievePatientByPatientIdentityNumber(identityNumber);

                //To retrieve earliest free appointment slot
                String bookingTime = "";
                List<AppointmentEntity> doctorAppointment = appointmentEntitySessionBeanRemote.retrieveAppointmentByDoctorIdAndDate(doctorId, dateToday);
                
                
                if (doctorAppointment.isEmpty())
                {
                    bookingTime = availableTimeList.get(0);
                }
                else 
                {
                    for (int i = 0; i < availableTimeList.size(); i++) 
                    {
                        String timeToCheck = availableTimeList.get(i);
                        Time input = Time.valueOf(timeToCheck);
                        AppointmentEntity appointmentEntity = appointmentEntitySessionBeanRemote.retrieveAppointmentByDoctorIdAndDateAndTime(doctorId, dateToday, input);
                        if (appointmentEntity == null) {
                            bookingTime = timeToCheck;
                            break;
                        }
                    }
                }
                
                Time time = Time.valueOf(bookingTime);
                AppointmentEntity newAppointmentEntity = new AppointmentEntity(doctor, dateToday, time, patient);
                appointmentEntitySessionBeanRemote.createNewAppointment(newAppointmentEntity);
                patientSessionBeanRemote.updatePatientList(identityNumber);
                doctorSessionBeanRemote.updateDoctorList(doctorId);

                String patientName = patient.getFirstName() + " " + patient.getLastName();
                String doctorName = doctor.getFirstName() + " " + doctor.getLastName();

                System.out.println(patientName + " appointment with Dr. " + doctorName + " has been booked at " + bookingTime.substring(0, 5) + ".");

                List<QueueEntity> currentQueue = queueSessionBeanRemote.retrieveQueueByDate(dateToday);
                if (currentQueue.isEmpty())
                {
                    int resetQueueNumber = queueSessionBeanRemote.resetQueueNumber();
                    QueueEntity newQueueEntity = new QueueEntity(resetQueueNumber, dateToday);
                    queueSessionBeanRemote.createQueue(newQueueEntity);
                    System.out.println("Queue number is: " + newQueueEntity.getQueueNumber() + ".\n"); 
                }
                else 
                {
                    int newQueueNumber = queueSessionBeanRemote.updateQueueNumber(dateToday);
                    QueueEntity newQueueEntity = new QueueEntity(newQueueNumber, dateToday);
                    queueSessionBeanRemote.createQueue(newQueueEntity);
   
                    System.out.println("Queue number is: " + newQueueEntity.getQueueNumber() + ".\n");
                } 
            }
        }
        catch (CreateAppointmentException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    
    
    private void registerConsultationByAppointment() throws QueueNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** CARS :: Registration Operation :: Register Consultation By Appointment ***\n");
        System.out.print("Enter Patient Identity Number> ");
        String patientIdentityNo = scanner.nextLine().trim();
        System.out.println();
        
        Date todayDate = Date.valueOf(LocalDate.now());
      
        List<AppointmentEntity> patientAppointments = appointmentEntitySessionBeanRemote.retrieveAppointmentByPatientIdentityNoAndDate(patientIdentityNo, todayDate);
        if (patientAppointments.isEmpty())
        {
            System.out.println("Patient has no appointments today!\n");
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
            
            System.out.println("");
            System.out.print("Enter Appointment Id>");
            Long appoinmentId = scanner.nextLong();
            scanner.nextLine();

            for (AppointmentEntity appointmentEntity : patientAppointments) 
            {
                if (appoinmentId.compareTo(appointmentEntity.getAppointmentId()) == 0) 
                {
                    String doctorName = appointmentEntity.getDoctorEntity().getFirstName() + " " + appointmentEntity.getDoctorEntity().getLastName();
                    String timeString = appointmentEntity.getTime().toString();
                    String patientName = appointmentEntity.getPatientEntity().getFirstName() + " " + appointmentEntity.getPatientEntity().getLastName();
                    System.out.println(patientName + " appointment is confirmed with " + doctorName + " at " + timeString.substring(0, 5) + ".");
                   
                    List<QueueEntity> currentQueue = queueSessionBeanRemote.retrieveQueueByDate(todayDate);
                    if (currentQueue.isEmpty())
                    {
                        int resetQueueNumber = queueSessionBeanRemote.resetQueueNumber();
                        QueueEntity newQueueEntity = new QueueEntity(resetQueueNumber, todayDate);
                        queueSessionBeanRemote.createQueue(newQueueEntity);
                        System.out.println("Queue number is: " + newQueueEntity.getQueueNumber() + ".\n"); 
                    }
                    else 
                    {
                        int newQueueNumber = queueSessionBeanRemote.updateQueueNumber(todayDate);
                        QueueEntity newQueueEntity = new QueueEntity(newQueueNumber, todayDate);
                        queueSessionBeanRemote.createQueue(newQueueEntity);
                        System.out.println("Queue number is: " + newQueueEntity.getQueueNumber() + ".\n"); 
                    }
                }
            }
        }  
    }
}
