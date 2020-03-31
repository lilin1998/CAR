package ejb.session.stateless;

import entity.AppointmentEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AppointmentNotFoundException;
import util.exception.DoctorNotFoundException;
import util.exception.PatientNotFoundException;

@Stateless
@Local(AppointmentEntitySessionBeanLocal.class)
@Remote(AppointmentEntitySessionBeanRemote.class)
public class AppointmentEntitySessionBean implements AppointmentEntitySessionBeanRemote, AppointmentEntitySessionBeanLocal 
{

    @PersistenceContext(unitName = "CARS-ejbPU")
    private EntityManager em;

    public AppointmentEntitySessionBean() {
    }
    
    @Override
    public AppointmentEntity createNewAppointment(AppointmentEntity newAppointmentEntity) 
    {
        em.persist(newAppointmentEntity);
        em.flush();
        
        return newAppointmentEntity;
    }
    
    
    
    @Override
    public List<AppointmentEntity> retrieveAllAppointments()
    {
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a");
        
        return query.getResultList();
    }
    
    
    
    @Override
    public AppointmentEntity retrieveAppointmentByAppointmentId(Long appointmentId) throws AppointmentNotFoundException
    {
        AppointmentEntity appointmentEntity = em.find(AppointmentEntity.class, appointmentId);
        
        if(appointmentEntity != null)
        {            
            return appointmentEntity;
        }
        else
        {
            throw new AppointmentNotFoundException("Appointment ID " + appointmentId + " does not exist!");
        }                
    }
    
    
    
    @Override
    public List<AppointmentEntity> retrieveAppointmentByPatientIdentityNo(String identityNo) throws PatientNotFoundException
    {
        Query query = em.createQuery("SELECT DISTINCT a FROM AppointmentEntity a JOIN a.patientEntity p WHERE p.identityNumber = ?1");
        query.setParameter(1, identityNo);
        
        return query.getResultList();
    }
    
    
    
    @Override
    public List<AppointmentEntity> retrieveAppointmentByDoctorId(Long doctorId) throws DoctorNotFoundException
    {
        Query query = em.createQuery("SELECT DISTINCT a FROM AppointmentEntity a JOIN a.doctorEntity d WHERE d.doctorId = :id");
        query.setParameter("id", doctorId);
        
        return query.getResultList();
    }
    
    
    
    @Override
    public void updateAppointment(AppointmentEntity appointmentEntity)
    {
        em.merge(appointmentEntity);
    }    
    
    
    
    @Override
    public void deleteAppoinment(Long appointmentId)
    {
        AppointmentEntity appointmentToRemove;
        
        try {
            appointmentToRemove = retrieveAppointmentByAppointmentId(appointmentId);
            em.remove(appointmentToRemove);
        } catch (AppointmentNotFoundException ex) {
            Logger.getLogger(AppointmentEntitySessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
