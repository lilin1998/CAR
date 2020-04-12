package ams;

import ws.client.AppointmentNotFoundException_Exception;
import ws.client.CreateAppointmentException_Exception;
import ws.client.DoctorNotFoundException_Exception;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.PatientNotFoundException_Exception;
import ws.client.UnknownPersistenceException_Exception;

public class AMS {

    public static void main(String[] args) throws UnknownPersistenceException_Exception, InvalidLoginCredentialException_Exception, PatientNotFoundException_Exception, AppointmentNotFoundException_Exception, DoctorNotFoundException_Exception, CreateAppointmentException_Exception 
    {
        MainApp mainApp = new MainApp();
        mainApp.runApp();
    }
}
