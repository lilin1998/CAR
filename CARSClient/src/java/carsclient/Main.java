package carsclient;

import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.DoctorSessionBeanRemote;
import ejb.session.stateless.PatientSessionBeanRemote;
import ejb.session.stateless.StaffEntitySessionBeanRemote;
import javax.ejb.EJB;
import util.exception.DoctorNotFoundException;

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
    
    public static void main(String[] args) throws DoctorNotFoundException 
    {
        MainApp mainApp = new MainApp(staffEntitySessionBeanRemote, doctorSessionBeanRemote, patientSessionBeanRemote, appointmentEntitySessionBeanRemote);
        mainApp.runApp();
    }
    
}
