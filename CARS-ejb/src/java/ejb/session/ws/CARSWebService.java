package ejb.session.ws;

import ejb.session.stateless.AppointmentEntitySessionBeanLocal;
import ejb.session.stateless.DoctorSessionBeanLocal;
import ejb.session.stateless.LeaveEntitySessionBeanLocal;
import ejb.session.stateless.PatientSessionBeanLocal;
import entity.AppointmentEntity;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.AppointmentNotFoundException;

@WebService(serviceName = "CARSWebService")
@Stateless()
public class CARSWebService {

    @EJB
    private PatientSessionBeanLocal patientSessionBeanLocal;
    @EJB
    private DoctorSessionBeanLocal doctorSessionBeanLocal;
    @EJB
    private AppointmentEntitySessionBeanLocal appointmentEntitySessionBeanLocal;
    @EJB
    private LeaveEntitySessionBeanLocal leaveEntitySessionBeanLocal;
    
    @WebMethod(operationName = "retrieveAppointmentByAppointmentId")
    public AppointmentEntity retrieveAppointmentByAppointmentId(@WebParam Long appointmentId) throws AppointmentNotFoundException 
    {
        return appointmentEntitySessionBeanLocal.retrieveAppointmentByAppointmentId(appointmentId);
    }
    
//    @WebMethod(operationName = "createPatienr")
//    public Long createPatient(@WebParam )
//    
//    @WebMethod(operationName = "patientLogin")
//    public Long pattientLogin(@WebParam )
//            
//    @WebMethod(operationName = "retrievePatientByPatientIdentityNumber")
//    public Long retrievePatientByPatientIdentityNumber(@WebParam )
//            
//    @WebMethod(operationName = "retrieveDoctorByDoctorId")
//    public Long retrieveDoctorByDoctorId(@WebParam )
//            
//    @WebMethod(operationName = "checkAppointmentSchedule")
//    public Long checkAppointmentSchedule(@WebParam )        
//
//    @WebMethod(operationName = "retrieveAllAppointments")
//    public Long retrieveAllAppointments(@WebParam )    
//            
//    @WebMethod(operationName = "retrieveAppointmentByPatientIdentityNo")
//    public Long retrieveAppointmentByPatientIdentityNo(@WebParam )    
//            
//    @WebMethod(operationName = "retrieveAppointmentByPatientIdentityNoAndDate")
//    public Long retrieveAppointmentByPatientIdentityNoAndDate(@WebParam )    
//            
//    @WebMethod(operationName = "retrieveAppointmentByDoctorId")
//    public Long retrieveAppointmentByDoctorId(@WebParam )    
//            
//    @WebMethod(operationName = "retrieveAppointmentByDoctorIdAndDate")
//    public Long retrieveAppointmentByDoctorIdAndDate(@WebParam )   
//            
//    @WebMethod(operationName = "retrieveAppointmentByDoctorIdAndDateAndTime")
//    public Long retrieveAppointmentByDoctorIdAndDateAndTime(@WebParam )   
//            
//    @WebMethod(operationName = "retrieveLeaveByDoctorId")
//    public Long retrieveLeaveByDoctorId(@WebParam )  
//            
//    @WebMethod(operationName = "retrieveLeaveByDateNDoctorId")
//    public Long retrieveLeaveByDateNDoctorId(@WebParam ) 
}
