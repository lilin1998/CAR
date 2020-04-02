package ejb.session.stateless;

import entity.AppointmentEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.AppointmentNotFoundException;
import util.exception.DoctorNotFoundException;
import util.exception.PatientNotFoundException;

@Local
public interface AppointmentEntitySessionBeanLocal 
{
    public AppointmentEntity createNewAppointment(AppointmentEntity newAppointmentEntity); 

    public List<AppointmentEntity> retrieveAllAppointments();

    public AppointmentEntity retrieveAppointmentByAppointmentId(Long appointmentId) throws AppointmentNotFoundException;

    public List<AppointmentEntity> retrieveAppointmentByPatientIdentityNo(String identityNo) throws PatientNotFoundException;
    
    public List<AppointmentEntity> retrieveAppointmentByDoctorId(Long doctorId) throws DoctorNotFoundException;
    
    public void updateAppointment(AppointmentEntity appointmentEntity);

    public void deleteAppointment(Long appointmentId);
}

    


