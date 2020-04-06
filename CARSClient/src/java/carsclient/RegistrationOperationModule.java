package carsclient;

import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateless.LeaveEntitySessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
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
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import util.exception.CreateAppointmentException;
import util.exception.DoctorNotFoundException;
import util.exception.PasswordException;
import util.exception.PatientNotFoundException;

public class RegistrationOperationModule {
    private DoctorSessionBeanRemote doctorSessionBeanRemote;
    private AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;
    private PatientSessionBeanRemote patientSessionBeanRemote;
    private LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote;
    
    private StaffEntity currentStaffEntity;
    
    private static int queueNo = 1;

    private String[] timeSlot = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};
    private String[] timeSlotThur = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"};
    private String[] timeSlotFri = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"};
    
    public RegistrationOperationModule() {
    }

    
    
    public RegistrationOperationModule(DoctorSessionBeanRemote doctorSessionBeanRemote, AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote, PatientSessionBeanRemote patientSessionBeanRemote, StaffEntity currentStaffEntity, LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote) {
        this.doctorSessionBeanRemote = doctorSessionBeanRemote;
        this.appointmentEntitySessionBeanRemote = appointmentEntitySessionBeanRemote;
        this.patientSessionBeanRemote = patientSessionBeanRemote;
        this.currentStaffEntity = currentStaffEntity;
        this.leaveEntitySessionBeanRemote = leaveEntitySessionBeanRemote;
    }
    
    
    
    public void registrationOperation() throws NoSuchAlgorithmException, NoSuchProviderException, PasswordException, DoctorNotFoundException, PatientNotFoundException, CreateAppointmentException
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
        
        System.out.println("*** CARS :: Registration Operation :: Add New Patient ***\n");
        System.out.print("Enter Identity Number> ");
        newPatientEntity.setIdentityNumber(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        
        String passwordToHash = scanner.nextLine().trim();
        byte[] salt = getSalt();
        String securePassword = getSecurePassword(passwordToHash, salt);
        newPatientEntity.setPassword(securePassword);
        String userSalt = Base64.getEncoder().encodeToString(salt);
        newPatientEntity.setUsersalt(userSalt);
        
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
    
    
    
    private void registerWalkinConsultation() throws DoctorNotFoundException, PatientNotFoundException, CreateAppointmentException
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
                        Time input = java.sql.Time.valueOf(timeToCheck);
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
                System.out.println("Queue number is: " + queueNo + ".\n");
                queueNo++;
            }
        }
        catch (CreateAppointmentException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    
    
    private void registerConsultationByAppointment()
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
                    System.out.println("Queue Number is " + queueNo + ".\n");
                    queueNo++;
                }
            }
        }  
    }
}
