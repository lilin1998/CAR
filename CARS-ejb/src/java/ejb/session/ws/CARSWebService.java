package ejb.session.ws;

import ejb.session.stateful.AppointmentEntitySessionBeanLocal;
import ejb.session.stateful.LeaveEntitySessionBeanLocal;
import ejb.session.stateless.DoctorSessionBeanLocal;
import ejb.session.stateless.PatientSessionBeanLocal;
import entity.AppointmentEntity;
import entity.DoctorEntity;
import entity.LeaveEntity;
import entity.PatientEntity;
import java.sql.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.AppointmentNotFoundException;
import util.exception.CreateAppointmentException;
import util.exception.CreatePatientException;
import util.exception.DoctorNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.LeaveApplicationException;
import util.exception.PasswordException;
import util.exception.PatientIdentityNumberExist;
import util.exception.PatientNotFoundException;
import util.exception.UnknownPersistenceException;

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
  
    
    @WebMethod(operationName = "createPatient")
    public Long createPatient(@WebParam(name = "PatientEntity") PatientEntity patientEntity)
                               throws InvalidLoginCredentialException, PatientIdentityNumberExist, UnknownPersistenceException, InputDataValidationException
    {
        return patientSessionBeanLocal.createPatient(patientEntity);
    }
    
    
    @WebMethod(operationName = "patientLogin")
    public PatientEntity patientLogin(@WebParam String identityNumber,
                              @WebParam String password) throws InvalidLoginCredentialException
    {
        return patientSessionBeanLocal.patientLogin(identityNumber, password);
    }
            
    @WebMethod(operationName = "retrievePatientByPatientIdentityNumber")
    public PatientEntity retrievePatientByPatientIdentityNumber(@WebParam (name = "IdentityNumber") String identityNumber) throws PatientNotFoundException
    {
        return patientSessionBeanLocal.retrievePatientByPatientIdentityNumber(identityNumber);
    }
    
    @WebMethod(operationName = "getSecurePassword")
    public String getSecurePassword(@WebParam(name = "Password") String passwordToHash)
    {
        return patientSessionBeanLocal.getSecurePassword(passwordToHash);
    }
    
    @WebMethod(operationName = "inputIsIncorrect")
    public void inputIsIncorrect(@WebParam(name = "PatientIdentity") PatientEntity patientEntity) throws CreatePatientException
    {
        patientSessionBeanLocal.inputIsIncorrect(patientEntity);
    }
    
    @WebMethod(operationName = "checkPassword")
    public void checkPassword(@WebParam(name = "password") String password) throws PasswordException
    {
        patientSessionBeanLocal.checkPassword(password);
    }
            
    @WebMethod(operationName = "retrieveDoctorByDoctorId")
    public DoctorEntity retrieveDoctorByDoctorId(@WebParam(name = "DoctorId") Long doctorId) throws DoctorNotFoundException 
    {
        return doctorSessionBeanLocal.retrieveDoctorByDoctorId(doctorId);
    }
            
    @WebMethod(operationName = "retrieveAppointmentByPatientIdentityNo")
    public List<AppointmentEntity> retrieveAppointmentByPatientIdentityNo(@WebParam (name = "IdentityNumber") String identityNumber) throws PatientNotFoundException
    {
        return appointmentEntitySessionBeanLocal.retrieveAppointmentByPatientIdentityNo(identityNumber);
    }
    
    @WebMethod(operationName = "retrieveAllDoctors")
    public List<DoctorEntity> retrieveAllDoctors() 
    {
        return doctorSessionBeanLocal.retrieveAllDoctors();
    }  
        
    @WebMethod(operationName = "retrieveAppointmentByDoctorId")
    public List<AppointmentEntity> retrieveAppointmentByDoctorId(@WebParam(name = "DoctorId") Long doctorId) throws DoctorNotFoundException    
    {
        return appointmentEntitySessionBeanLocal.retrieveAppointmentByDoctorId(doctorId);
    }
    
    @WebMethod(operationName = "retrieveLeaveByDoctorId")
    public List<LeaveEntity> retrieveLeaveByDoctorId(@WebParam(name = "DoctorId") Long doctorId) throws DoctorNotFoundException
    {
        return leaveEntitySessionBeanLocal.retrieveLeaveByDoctorId(doctorId);
    }
            
    @WebMethod(operationName = "retrieveLeaveByDateNDoctorId")
    public LeaveEntity retrieveLeaveByDateNDoctorId(@WebParam(name = "DoctorId") Long doctorId,
                                                    @WebParam(name = "Date") Date date) 
    {
        return leaveEntitySessionBeanLocal.retrieveLeaveByDateNDoctorId(doctorId, date);
    }

    @WebMethod(operationName = "checkIfDoctorIsOnLeave")
    public void checkIfDoctorIsOnLeave(@WebParam(name = "DoctorId") Long doctorId,
                                       @WebParam(name = "Date") String date) throws LeaveApplicationException
    {
        leaveEntitySessionBeanLocal.checkifDocIsOnLeave(doctorId, date);
    }
    
    @WebMethod(operationName = "createNewAppointment")
    public AppointmentEntity createNewAppointment(@WebParam(name = "NewAppointmentEntity") AppointmentEntity newAppointmentEntity)
    {
        return appointmentEntitySessionBeanLocal.createNewAppointment(newAppointmentEntity);
    }
    
    @WebMethod(operationName = "deleteAppointment")
    public void deleteAppointment(@WebParam(name = "AppointmentId") Long appointmentId)
    {
        appointmentEntitySessionBeanLocal.deleteAppointment(appointmentId);
    }
    
    @WebMethod(operationName = "updateDoctorList")
    public void updateDoctorList(@WebParam(name = "DoctorId") Long doctorId) throws DoctorNotFoundException
    {
        doctorSessionBeanLocal.updateDoctorList(doctorId);
    }
    
    @WebMethod(operationName = "updatePatientList")
    public void updatePatientList(@WebParam(name = "IdentityNumber") String identityNumber) throws PatientNotFoundException
    {
        patientSessionBeanLocal.updatePatientList(identityNumber);
    }
    
    @WebMethod(operationName = "checkApptNotOnWeekendAnd2DaysLater")
    public void checkApptNotOnWeekendAnd2DaysLater(@WebParam(name = "Date")String date) throws CreateAppointmentException
    {
        appointmentEntitySessionBeanLocal.checkApptNotOnWeekendAnd2DaysLater(date);
    }
}
