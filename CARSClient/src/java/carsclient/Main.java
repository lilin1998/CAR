package carsclient;

import ejb.session.stateful.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateful.LeaveEntitySessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import ejb.session.stateless.StaffEntitySessionBeanRemote;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import javax.ejb.EJB;
import util.exception.AppointmentNotFoundException;
import util.exception.CreateAppointmentException;
import util.exception.DeletePatientException;
import util.exception.DoctorNotFoundException;
import util.exception.DoctorRegistrationExistException;
import util.exception.InputDataValidationException;
import util.exception.LeaveApplicationException;
import util.exception.PasswordException;
import util.exception.PatientIdentityNumberExist;
import util.exception.PatientNotFoundException;
import util.exception.StaffNotFoundException;
import util.exception.StaffUsernameExistException;
import util.exception.UnknownPersistenceException;
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
    
    public static void main(String[] args) throws DoctorNotFoundException, PatientNotFoundException, UpdatePatientException, NoSuchAlgorithmException, NoSuchProviderException, PasswordException, DeletePatientException, AppointmentNotFoundException, StaffNotFoundException, LeaveApplicationException, CreateAppointmentException, SQLException, PatientIdentityNumberExist, UnknownPersistenceException, InputDataValidationException, StaffUsernameExistException, DoctorRegistrationExistException  
    {
        MainApp mainApp = new MainApp(staffEntitySessionBeanRemote, doctorSessionBeanRemote, patientSessionBeanRemote, appointmentEntitySessionBeanRemote, leaveEntitySessionBeanRemote);
        mainApp.runApp();
    }
    
}
