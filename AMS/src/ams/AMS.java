package ams;

import ws.client.AppointmentEntity;
import ws.client.AppointmentNotFoundException_Exception;

public class AMS {

    public static void main(String[] args) {
        
//        AppointmentEntity appointmentEntity = retrieveAppointmentByAppointmentId(Long.valueOf(1));
    }

    private static AppointmentEntity retrieveAppointmentByAppointmentId(java.lang.Long arg0) throws AppointmentNotFoundException_Exception {
        ws.client.CARSWebService_Service service = new ws.client.CARSWebService_Service();
        ws.client.CARSWebService port = service.getCARSWebServicePort();
        return port.retrieveAppointmentByAppointmentId(arg0);
    }
    
}
