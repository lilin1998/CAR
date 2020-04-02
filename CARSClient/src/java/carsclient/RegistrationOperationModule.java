package carsclient;

import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import entity.AppointmentEntity;
import entity.DoctorEntity;
import entity.GenderEnum;
import entity.PatientEntity;
import entity.StaffEntity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import util.exception.DoctorNotFoundException;
import util.exception.PasswordException;

public class RegistrationOperationModule {
    private DoctorSessionBeanRemote doctorSessionBeanRemote;
    private AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;
    private PatientSessionBeanRemote patientSessionBeanRemote;
    
    private StaffEntity currentStaffEntity;

    private String[] timeSlot = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};
    private String[] timeSlotThur = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"};
    private String[] timeSlotFri = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"};
    
    public RegistrationOperationModule() {
    }

    
    
    public RegistrationOperationModule(DoctorSessionBeanRemote doctorSessionBeanRemote, AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote, PatientSessionBeanRemote patientSessionBeanRemote, StaffEntity currentStaffEntity) {
        this.doctorSessionBeanRemote = doctorSessionBeanRemote;
        this.appointmentEntitySessionBeanRemote = appointmentEntitySessionBeanRemote;
        this.patientSessionBeanRemote = patientSessionBeanRemote;
        this.currentStaffEntity = currentStaffEntity;
    }
    
    
    
    public void registrationOperation() throws NoSuchAlgorithmException, NoSuchProviderException, PasswordException, DoctorNotFoundException
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
    
    
    
    private void registerWalkinConsultation() throws DoctorNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
       
        System.out.println("*** CARS :: Registration Operation :: Register Walk-In Consultation ***\n");
        
        //retrieve today's date and current time
        Long today = System.currentTimeMillis();
        java.sql.Date dateToday = new java.sql.Date(today);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateToday);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        java.sql.Time timeNow = new java.sql.Time(today);
        
        //check if doctor on leave
        
        List<DoctorEntity> doctors = doctorSessionBeanRemote.retrieveAllDoctors();
        System.out.println("Doctor:");
        System.out.printf("%-3s|%-20s\n", "Id", "Name");
        
        for (DoctorEntity doctorEntity : doctors)
        {
            String doctorName = doctorEntity.getFirstName() + " " + doctorEntity.getLastName();
            System.out.printf("%-3s|%-20s\n", doctorEntity.getDoctorId(), doctorName);
        }
        System.out.println("");
        
        //retrieve latest display time
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY, 3);
        Time threeHoursLater = new Time((c.getTime()).getTime());
        
        List<String> availableTimeList = new ArrayList<>();
        if (day >= Calendar.MONDAY && day <= Calendar.WEDNESDAY) 
        {
            for (String time : timeSlot) 
            {
                time += ":00";
                if (Time.valueOf(time).compareTo(threeHoursLater) <= 0)
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
                if (Time.valueOf(time).compareTo(threeHoursLater) <= 0)
                {
                    availableTimeList.add(time);
                }
            }
        } 
        else //Friday
        {
            for (String time : timeSlotFri) 
            {time += ":00";
                if (Time.valueOf(time).compareTo(threeHoursLater) <= 0)
                {
                    availableTimeList.add(time);
                }    
            }       
        }
        
        System.out.println("Availability:");
        System.out.printf("%-7s|", "Time");
        for(DoctorEntity doctorEntity : doctors)
        {
            System.out.printf("%-3s|", doctorEntity.getDoctorId());
        }
        System.out.print("\n");
        
        for (int i = 0; i < availableTimeList.size(); i++)
        {
            System.out.printf("%-7s|", availableTimeList.get(i).substring(0, 5));
            for (DoctorEntity doctorEntity : doctors)
            {
                List<AppointmentEntity> doctorappointment = appointmentEntitySessionBeanRemote.retrieveAppointmentByDoctorId(doctorEntity.getDoctorId());
                for (int j = 0; j < doctorappointment.size(); j++)
                {
                    String appointmentTime = doctorappointment.get(j).getTime().toString();
                    if (appointmentTime.substring(0, 5).equals(availableTimeList.get(i)))
                    {
                        System.out.printf("%-3s|", "X");
                    }
                    if (!appointmentTime.substring(0, 5).equals(availableTimeList.get(i)) && j == doctorappointment.size() - 1)
                    {
                        System.out.printf("%-3s|", "O");
                    }
                }
            }
            System.out.print("\n");
        }
    }

    
    
    private void registerConsultationByAppointment()
    {
    
    }
}
