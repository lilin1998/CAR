package ejb.session.stateless;

import entity.DoctorEntity;
import java.sql.Date;
import java.util.List;
import util.exception.AppointmentNotFoundException;
import util.exception.CreateDoctorException;
import util.exception.DeleteDoctorException;
import util.exception.DoctorNotFoundException;
import util.exception.DoctorRegistrationExistException;
import util.exception.InputDataValidationException;
import util.exception.LeaveApplicationException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateDoctorException;

public interface DoctorSessionBeanRemote {
        
    public Long createNewDoctor(DoctorEntity newDoctorEntity) throws DoctorRegistrationExistException, UnknownPersistenceException, InputDataValidationException;

    public List<DoctorEntity> retrieveAllDoctors();

    public DoctorEntity retrieveDoctorByDoctorId(Long doctorId) throws DoctorNotFoundException;
  
    public void updateDoctor(DoctorEntity doctorEntity) throws UpdateDoctorException, DoctorNotFoundException, InputDataValidationException;
    
    public void deleteDoctor(DoctorEntity doctorEntity) throws DeleteDoctorException, DoctorNotFoundException, AppointmentNotFoundException;

    public void updateDoctorList(Long doctorId) throws DoctorNotFoundException;

    public void checkAppointmentSchedule(Long doctorId, Date date) throws LeaveApplicationException, DoctorNotFoundException;
    
    public void inputIsIncorrect(DoctorEntity doctorEntity) throws CreateDoctorException;
}
