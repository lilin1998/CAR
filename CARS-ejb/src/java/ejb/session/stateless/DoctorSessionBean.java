package ejb.session.stateless;

import entity.AppointmentEntity;
import entity.DoctorEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AppointmentNotFoundException;
import util.exception.DeleteDoctorException;
import util.exception.DoctorNotFoundException;
import util.exception.UpdateDoctorException;

@Stateless
@Local(DoctorSessionBeanLocal.class)
@Remote(DoctorSessionBeanRemote.class)
public class DoctorSessionBean implements DoctorSessionBeanRemote, DoctorSessionBeanLocal 
{
    @PersistenceContext(unitName = "CARS-ejbPU")
    private EntityManager em;
    
    @EJB
    private AppointmentEntitySessionBeanLocal appointmentEntitySessionBeanLocal; 
    
    public DoctorSessionBean() 
    {
    }
    
    
    
    @Override
    public Long createNewDoctor(DoctorEntity newDoctorEntity) 
    {
        em.persist(newDoctorEntity);
        em.flush();
        DoctorEntity d = null;
        try 
        {
            d = retrieveDoctorByDoctorId(newDoctorEntity.getDoctorId());
        } 
        catch (DoctorNotFoundException e) 
        {
            Logger.getLogger(DoctorSessionBean.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return d.getDoctorId();
    }
    
    
    
    @Override
    public List<DoctorEntity> retrieveAllDoctors() 
    {
        Query query = em.createQuery("SELECT d FROM DoctorEntity d ORDER BY d.doctorId ASC");
        
        return query.getResultList();
    }
    
    
    
    @Override
    public void updateDoctor(DoctorEntity doctorEntity) throws UpdateDoctorException, DoctorNotFoundException
    {
        if(doctorEntity.getDoctorId()!= null)
        {
            DoctorEntity doctorEntityToUpdate = retrieveDoctorByDoctorId(doctorEntity.getDoctorId());
            
            if(doctorEntityToUpdate.getDoctorId().equals(doctorEntity.getDoctorId()))
            {
                doctorEntityToUpdate.setFirstName(doctorEntity.getFirstName());
                doctorEntityToUpdate.setLastName(doctorEntity.getLastName());
                doctorEntityToUpdate.setQualifications(doctorEntity.getQualifications());
                doctorEntityToUpdate.setRegistration(doctorEntity.getRegistration());
            }
            else
            {
                throw new UpdateDoctorException("ID of doctor record to be updated does not match the existing record");
            }
        }
        else
        {
            throw new DoctorNotFoundException("Doctor ID not provided for product to be updated");
        }
    }
    
    
    
    @Override
    public DoctorEntity retrieveDoctorByDoctorId(Long doctorId) throws DoctorNotFoundException
    {
        DoctorEntity doctorEntity = em.find(DoctorEntity.class, doctorId);
        
        if(doctorEntity != null) 
        {
            return doctorEntity;
        }
        else 
        {
            throw new DoctorNotFoundException("Doctor ID " + doctorId + " does not exist!");
        }
    } 
    
    
    
    @Override
    public void deleteDoctor(DoctorEntity doctorEntity) throws DeleteDoctorException, DoctorNotFoundException, AppointmentNotFoundException
    {
        Long doctorId = doctorEntity.getDoctorId();
        DoctorEntity doctorEntityToRemove = retrieveDoctorByDoctorId(doctorId);
        
        AppointmentEntity appointmentEntity = appointmentEntitySessionBeanLocal.retrieveAppointmentByAppointmentId(doctorId);
        
        if(appointmentEntity == null)
        {
            em.remove(doctorEntityToRemove);
        }
        else
        {
            throw new DeleteDoctorException("Doctor ID " + doctorId + " is associated with existing appointment(s) and cannot be deleted!");
        }
    }
    
    
    
    @Override
    public void addAppointmentToDoctorRecord(Long doctorId, AppointmentEntity newAppointmentEntity) throws DoctorNotFoundException
    {
        DoctorEntity doctorEntity = retrieveDoctorByDoctorId(doctorId);
        
        List<AppointmentEntity> doctorAppointments = appointmentEntitySessionBeanLocal.retrieveAppointmentByDoctorId(doctorId);
        doctorAppointments.add(newAppointmentEntity);
        doctorEntity.setDoctorAppointments(doctorAppointments);
    }
}
