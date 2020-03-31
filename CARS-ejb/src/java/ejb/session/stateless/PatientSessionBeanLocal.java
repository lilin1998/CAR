
package ejb.session.stateless;

import entity.PatientEntity;
import java.util.List;
import util.exception.DeletePatientException;
import util.exception.PatientNotFoundException;
import util.exception.UpdatePatientException;

public interface PatientSessionBeanLocal 
{
    public Long createPatient(PatientEntity newPatientEntity);

    public List<PatientEntity> retrieveAllPatients();

    public PatientEntity retrievePatientByPatientId(Long patientId) throws PatientNotFoundException;

    public void updatePatient(PatientEntity patientEntity) throws UpdatePatientException;

    public void deletePatient(Long patientId) throws PatientNotFoundException, DeletePatientException;
}
