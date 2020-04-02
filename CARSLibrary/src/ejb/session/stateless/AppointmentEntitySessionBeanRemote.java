package ejb.session.stateless;

import entity.AppointmentEntity;
import java.sql.Date;
import java.util.List;
import util.exception.AppointmentNotFoundException;
import util.exception.DoctorNotFoundException;
import util.exception.PatientNotFoundException;

public interface AppointmentEntitySessionBeanRemote {
    
    public AppointmentEntity createNewAppointment(AppointmentEntity newAppointmentEntity); 

    public List<AppointmentEntity> retrieveAllAppointments();

    public AppointmentEntity retrieveAppointmentByAppointmentId(Long appointmentId) throws AppointmentNotFoundException;

    public List<AppointmentEntity> retrieveAppointmentByPatientIdentityNo(String identityNo) throws PatientNotFoundException;
    
    public List<AppointmentEntity> retrieveAppointmentByDoctorId(Long doctorId) throws DoctorNotFoundException;
    
    public List<AppointmentEntity> retrieveAppointmentByDoctorIdAndDate(Long doctorId, Date date) throws DoctorNotFoundException;
    
    public void updateAppointment(AppointmentEntity appointmentEntity);

    public void deleteAppointment(Long appointmentId);  
}
