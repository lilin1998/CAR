package selfservicekiosk;

import ejb.session.stateful.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateful.LeaveEntitySessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import ejb.session.stateless.QueueSessionBeanRemote;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.ejb.EJB;
import util.exception.AppointmentNotFoundException;
import util.exception.DoctorNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.LeaveApplicationException;
import util.exception.PatientIdentityNumberExist;
import util.exception.PatientNotFoundException;
import util.exception.QueueNotFoundException;
import util.exception.UnknownPersistenceException;

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
    @EJB
    private static QueueSessionBeanRemote queueSessionBeanRemote;
    
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, DoctorNotFoundException, AppointmentNotFoundException, PatientNotFoundException, LeaveApplicationException, PatientIdentityNumberExist, UnknownPersistenceException, InputDataValidationException, QueueNotFoundException
    {
       MainApp mainApp = new MainApp(doctorSessionBeanRemote, patientSessionBeanRemote, appointmentEntitySessionBeanRemote, leaveEntitySessionBeanRemote, queueSessionBeanRemote);
       mainApp.runApp();
    }
}
