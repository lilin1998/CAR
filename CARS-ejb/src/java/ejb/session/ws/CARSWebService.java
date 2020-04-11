package ejb.session.ws;

import ejb.session.stateless.PatientSessionBeanLocal;
import entity.AppointmentEntity;
import entity.GenderEnum;
import entity.PatientEntity;
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
//    @EJB
//    private DoctorSessionBeanLocal doctorSessionBeanLocal;
//    @EJB
//    private AppointmentEntitySessionBeanLocal appointmentEntitySessionBeanLocal;
//    @EJB
//    private LeaveEntitySessionBeanLocal leaveEntitySessionBeanLocal;
    
//    @WebMethod(operationName = "retrieveAppointmentByAppointmentId")
//    public AppointmentEntity retrieveAppointmentByAppointmentId(@WebParam Long appointmentId) throws AppointmentNotFoundException 
//    {
//        return appointmentEntitySessionBeanLocal.retrieveAppointmentByAppointmentId(appointmentId);
//    }
    
    @WebMethod(operationName = "createPatient")
    public Long createPatient(@WebParam(name = "IdentityNumber") String identityNumber, 
                              @WebParam(name = "Password") String password, 
                              @WebParam(name = "FirstName") String firstName, 
                              @WebParam(name = "LastName") String lastName, 
                              @WebParam(name = "Gender") GenderEnum gender, 
                              @WebParam(name = "Age") Integer age,
                              @WebParam(name = "Phone") String phone, 
                              @WebParam(name = "Address") String address)
    {
//        GenderEnum setGender;
//        if(gender.equals("M"))
//        {
//            setGender = setGender.M;
//        }
//        else
//        {
//            setGender = setGender.F;
//        }
        return patientSessionBeanLocal.createPatient(new PatientEntity(identityNumber, password, firstName, lastName, gender, age, phone, address));
    }
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
