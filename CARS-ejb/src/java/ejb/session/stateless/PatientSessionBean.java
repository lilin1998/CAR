package ejb.session.stateless;

import entity.AppointmentEntity;
import entity.PatientEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.DeletePatientException;
import util.exception.PasswordException;
import util.exception.PatientNotFoundException;
import util.exception.UpdatePatientException;


@Stateless
@Local(PatientSessionBeanLocal.class)
@Remote(PatientSessionBeanRemote.class)
public class PatientSessionBean implements PatientSessionBeanRemote, PatientSessionBeanLocal 
{
    @PersistenceContext(unitName = "CARS-ejbPU")
    private EntityManager em;
    
    @EJB
    private AppointmentEntitySessionBeanLocal appointmentEntitySessionBeanLocal;

    public PatientSessionBean() 
    {
    }
   
    
    
    @Override
    public Long createPatient(PatientEntity newPatientEntity)
    {
        em.persist(newPatientEntity);
        em.flush();
        PatientEntity p = null;
        
        try 
        {
            p = retrievePatientByPatientId(newPatientEntity.getPatientId());
        }
        catch(PatientNotFoundException ex)
        {
            Logger.getLogger(PatientSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p.getPatientId();
    }
    
    
    
    @Override
    public List<PatientEntity> retrieveAllPatients()
    {
        Query query = em.createQuery("SELECT p FROM PatientEntity p ORDER BY p.patientId ASC");
        
        return query.getResultList();
    }
    
    
    
    @Override
    public PatientEntity retrievePatientByPatientId(Long patientId) throws PatientNotFoundException
    {
        PatientEntity patientEntity = em.find(PatientEntity.class, patientId);
        
        if (patientEntity != null)
        {
            return patientEntity;
        }
        else 
        {
            throw new PatientNotFoundException("Patient ID " + patientId + " does not exist!");
        }
    }
    
    
    
    @Override
    public PatientEntity retrievePatientByPatientIdentityNumber(String identityNumber) throws PatientNotFoundException
    {
        Query query = em.createQuery("SELECT p from PatientEntity p WHERE p.identityNumber = :inIdentityNumber");
        query.setParameter("inIdentityNumber", identityNumber);
        
        try 
        {
            return (PatientEntity)query.getSingleResult();
        } 
        catch (NoResultException | NonUniqueResultException ex) 
        {
            throw new PatientNotFoundException("Patient Identity Number " + identityNumber + " does not exist!");
        }
    }
    
    
    
    @Override
    public void updatePatient(PatientEntity patientEntity) throws UpdatePatientException
    {
        try
        {
            PatientEntity p = retrievePatientByPatientId(patientEntity.getPatientId());
            if (p.getIdentityNumber().equals(patientEntity.getIdentityNumber()))
            {
                p.setFirstName(patientEntity.getFirstName());
                p.setLastName(patientEntity.getLastName());
                p.setAge(patientEntity.getAge());
                p.setPassword(patientEntity.getPassword());
                p.setGender(patientEntity.getGender());
                p.setAge(patientEntity.getAge());
                p.setPhone(patientEntity.getPhone());
                p.setAddress(patientEntity.getAddress());
            }
            else {
                throw new UpdatePatientException("Identity Number provided does not match the existing patient record!");
            }
        }
        catch (PatientNotFoundException ex)
        {
            Logger.getLogger(PatientSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    @Override
    public void deletePatient(Long patientId) throws PatientNotFoundException, DeletePatientException
    {
        PatientEntity patientEntityToRemove = retrievePatientByPatientId(patientId);
        
        List<AppointmentEntity> appointmentsOfPatient = appointmentEntitySessionBeanLocal.retrieveAppointmentByPatientIdentityNo(patientEntityToRemove.getIdentityNumber());
        
        if (appointmentsOfPatient.isEmpty())
        {
            em.remove(patientEntityToRemove);
        }
        else
        {
            throw new DeletePatientException("Patient ID " + patientId + " is associated with existing appointments and cannot be deleted!");
        }
    }
    
    
    
    @Override
    public void updatePatientList(String identityNo) throws PatientNotFoundException
    {
        PatientEntity patientEntity = retrievePatientByPatientIdentityNumber(identityNo);
        em.refresh(patientEntity);
        
        List<AppointmentEntity> patientAppointment = patientEntity.getPatientAppointments();
        patientEntity.setPatientAppointments(patientAppointment);
    }
    
    @Override
    public void checkPassword(String password) throws PasswordException
    {
        int intpw = Integer.valueOf(password);
        if (!password.matches("\\d{6}")) 
        {
            throw new PasswordException("Password is invalid! Please make sure it is a 6 digit number.");
        }
    }
}
