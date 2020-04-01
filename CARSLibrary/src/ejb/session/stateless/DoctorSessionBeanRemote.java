package ejb.session.stateless;

import entity.AppointmentEntity;
import entity.DoctorEntity;
import java.util.List;
import util.exception.AppointmentNotFoundException;
import util.exception.DeleteDoctorException;
import util.exception.DoctorNotFoundException;
import util.exception.UpdateDoctorException;

public interface DoctorSessionBeanRemote {
        
    public Long createNewDoctor(DoctorEntity newDoctorEntity);

    public List<DoctorEntity> retrieveAllDoctors();

    public DoctorEntity retrieveDoctorByDoctorId(Long doctorId) throws DoctorNotFoundException;
  
    public void updateDoctor(DoctorEntity doctorEntity) throws UpdateDoctorException, DoctorNotFoundException;
    
    public void deleteDoctor(DoctorEntity doctorEntity) throws DeleteDoctorException, DoctorNotFoundException, AppointmentNotFoundException;
    
    public void addAppointmentToDoctorRecord(Long doctorId, AppointmentEntity newAppointmentEntity) throws DoctorNotFoundException;
}
