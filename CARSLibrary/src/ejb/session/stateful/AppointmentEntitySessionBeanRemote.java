package ejb.session.stateful;

import entity.AppointmentEntity;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import javax.ejb.Remote;
import util.exception.AppointmentNotFoundException;
import util.exception.CreateAppointmentException;
import util.exception.DoctorNotFoundException;
import util.exception.PatientNotFoundException;

@Remote
public interface AppointmentEntitySessionBeanRemote 
{
    public AppointmentEntity createNewAppointment(AppointmentEntity newAppointmentEntity); 

    public List<AppointmentEntity> retrieveAllAppointments();

    public AppointmentEntity retrieveAppointmentByAppointmentId(Long appointmentId) throws AppointmentNotFoundException;

    public List<AppointmentEntity> retrieveAppointmentByPatientIdentityNo(String identityNo) throws PatientNotFoundException;
    
    public List<AppointmentEntity> retrieveAppointmentByDoctorId(Long doctorId) throws DoctorNotFoundException;
    
    public List<AppointmentEntity> retrieveAppointmentByDoctorIdAndDate(Long doctorId, Date date) throws DoctorNotFoundException;
    
    public void updateAppointment(AppointmentEntity appointmentEntity);

    public void deleteAppointment(Long appointmentId);

    public List<AppointmentEntity> retrieveAppointmentByPatientIdentityNoAndDate(String identityNo, Date date);

    public AppointmentEntity retrieveAppointmentByDoctorIdAndDateAndTime(Long doctorId, Date date, Time time) throws DoctorNotFoundException;

    public void checkApptNotOnWeekendAnd2DaysLater(String date) throws CreateAppointmentException;

}
