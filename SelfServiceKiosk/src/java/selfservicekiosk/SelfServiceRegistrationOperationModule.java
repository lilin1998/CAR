package selfservicekiosk;

import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateless.LeaveEntitySessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import entity.AppointmentEntity;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class SelfServiceRegistrationOperationModule 
{
    private DoctorSessionBeanRemote doctorSessionBeanRemote;
    private AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;
    private PatientSessionBeanRemote patientSessionBeanRemote;
    private LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote;
    
    private static int queueNo = 1;
    
    private String[] timeSlot = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};
    private String[] timeSlotThur = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"};
    private String[] timeSlotFri = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"};

    public SelfServiceRegistrationOperationModule() {
    }

    
    
    public SelfServiceRegistrationOperationModule(DoctorSessionBeanRemote doctorSessionBeanRemote, AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote, PatientSessionBeanRemote patientSessionBeanRemote, LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote) {
        this.doctorSessionBeanRemote = doctorSessionBeanRemote;
        this.appointmentEntitySessionBeanRemote = appointmentEntitySessionBeanRemote;
        this.patientSessionBeanRemote = patientSessionBeanRemote;
        this.leaveEntitySessionBeanRemote = leaveEntitySessionBeanRemote;
    }

    
    
    public void registerWalkinConsultation()
    {
        //eliz paste logic here
    }
    
    
    
    public void registerConsultationByAppointment()
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
