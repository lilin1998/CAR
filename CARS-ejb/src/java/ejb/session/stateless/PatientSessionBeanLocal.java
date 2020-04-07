package ejb.session.stateless;

import entity.PatientEntity;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import util.exception.DeletePatientException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PasswordException;
import util.exception.PatientNotFoundException;
import util.exception.UpdatePatientException;

public interface PatientSessionBeanLocal 
{
    public Long createPatient(PatientEntity newPatientEntity);

    public List<PatientEntity> retrieveAllPatients();

    public PatientEntity retrievePatientByPatientId(Long patientId) throws PatientNotFoundException;

    public void updatePatient(PatientEntity patientEntity) throws UpdatePatientException;

    public void deletePatient(Long patientId) throws PatientNotFoundException, DeletePatientException;

    public PatientEntity retrievePatientByPatientIdentityNumber(String identityNumber) throws PatientNotFoundException;
    
    public PatientEntity patientLogin(String identityNo, String password) throws InvalidLoginCredentialException;

    public void updatePatientList(String identityNo) throws PatientNotFoundException;
    
    public void checkPassword(String password) throws PasswordException;

    public String getSecurePassword(String passwordToHash, byte[] salt);

    public byte[] getSalt() throws NoSuchAlgorithmException, NoSuchProviderException;


    
}
