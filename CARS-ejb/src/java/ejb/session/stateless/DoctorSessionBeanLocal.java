package ejb.session.stateless;

import entity.DoctorEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.AppointmentNotFoundException;
import util.exception.DeleteDoctorException;
import util.exception.DoctorExistException;
import util.exception.DoctorNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateDoctorException;

@Local
public interface DoctorSessionBeanLocal {
    
    public Long createNewDoctor(DoctorEntity newDoctorEntity);

    public List<DoctorEntity> retrieveAllDoctors();

    public DoctorEntity retrieveDoctorByDoctorId(Long doctorId) throws DoctorNotFoundException;
  
    public void updateDoctor(DoctorEntity doctorEntity) throws UpdateDoctorException, DoctorNotFoundException;

    public void deleteDoctor(DoctorEntity doctorEntity) throws DeleteDoctorException, DoctorNotFoundException, AppointmentNotFoundException;
    
}
