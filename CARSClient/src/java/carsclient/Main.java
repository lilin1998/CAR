package carsclient;

import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateless.LeaveEntitySessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import ejb.session.stateless.StaffEntitySessionBeanRemote;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.ejb.EJB;
import util.exception.AppointmentNotFoundException;
import util.exception.CreateAppointmentException;
import util.exception.DeletePatientException;
import util.exception.DoctorNotFoundException;
import util.exception.LeaveApplicationException;
import util.exception.PasswordException;
import util.exception.PatientNotFoundException;
import util.exception.StaffNotFoundException;
import util.exception.UpdatePatientException;

public class Main 
{
    @EJB
    private static StaffEntitySessionBeanRemote staffEntitySessionBeanRemote;
    @EJB
    private static DoctorSessionBeanRemote doctorSessionBeanRemote;
    @EJB
    private static PatientSessionBeanRemote patientSessionBeanRemote;
    @EJB
    private static AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;
    @EJB
    private static LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote;
    
    public static void main(String[] args) throws DoctorNotFoundException, PatientNotFoundException, UpdatePatientException, NoSuchAlgorithmException, NoSuchProviderException, PasswordException, DeletePatientException, AppointmentNotFoundException, StaffNotFoundException, LeaveApplicationException, CreateAppointmentException  
    {
        MainApp mainApp = new MainApp(staffEntitySessionBeanRemote, doctorSessionBeanRemote, patientSessionBeanRemote, appointmentEntitySessionBeanRemote, leaveEntitySessionBeanRemote);
        mainApp.runApp();
    }
    
}
