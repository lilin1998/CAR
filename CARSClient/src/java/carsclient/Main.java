package carsclient;

import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import ejb.session.stateless.StaffEntitySessionBeanRemote;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.ejb.EJB;
import util.exception.AppointmentNotFoundException;
import util.exception.DeletePatientException;
import util.exception.DoctorNotFoundException;
import util.exception.PasswordException;
import util.exception.PatientNotFoundException;
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
    
    public static void main(String[] args) throws DoctorNotFoundException, PatientNotFoundException, UpdatePatientException, NoSuchAlgorithmException, NoSuchProviderException, PasswordException, DeletePatientException, AppointmentNotFoundException 
    {
        MainApp mainApp = new MainApp(staffEntitySessionBeanRemote, doctorSessionBeanRemote, patientSessionBeanRemote, appointmentEntitySessionBeanRemote);
        mainApp.runApp();
    }
    
}
