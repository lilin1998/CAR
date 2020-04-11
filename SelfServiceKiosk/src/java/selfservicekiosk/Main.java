package selfservicekiosk;

import ejb.session.stateful.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateful.LeaveEntitySessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.ejb.EJB;
import util.exception.AppointmentNotFoundException;
import util.exception.DoctorNotFoundException;
import util.exception.LeaveApplicationException;
import util.exception.PatientNotFoundException;

public class Main 
{
    @EJB
    private static DoctorSessionBeanRemote doctorSessionBeanRemote;
    @EJB
    private static PatientSessionBeanRemote patientSessionBeanRemote;
    @EJB
    private static AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;
    @EJB
    private static LeaveEntitySessionBeanRemote leaveEntitySessionBeanRemote;
    
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, DoctorNotFoundException, AppointmentNotFoundException, PatientNotFoundException, LeaveApplicationException 
    {
       MainApp mainApp = new MainApp(doctorSessionBeanRemote, patientSessionBeanRemote, appointmentEntitySessionBeanRemote, leaveEntitySessionBeanRemote);
        mainApp.runApp();
    }
}
