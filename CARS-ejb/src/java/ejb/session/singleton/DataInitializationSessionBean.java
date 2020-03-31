package ejb.session.singleton;

import ejb.session.stateless.DoctorSessionBeanLocal;
import ejb.session.stateless.PatientSessionBeanLocal;
import ejb.session.stateless.StaffEntitySessionBeanLocal;
import entity.DoctorEntity;
import entity.GenderEnum;
import static entity.GenderEnum.F;
import static entity.GenderEnum.M;
import entity.PatientEntity;
import entity.StaffEntity;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.exception.StaffNotFoundException;


@Singleton
@LocalBean
@Startup
public class DataInitializationSessionBean 
{
    @EJB
    private StaffEntitySessionBeanLocal staffEntitySessionBeanLocal;
    @EJB
    private DoctorSessionBeanLocal doctorSessionBeanLocal;
    @EJB
    private PatientSessionBeanLocal patientSessionBeanLocal;

    public DataInitializationSessionBean() 
    {
    }
    
    
    
    @PostConstruct
    public void postConstruct()
    {
        try
        {
            staffEntitySessionBeanLocal.retrieveStaffByUsername("manager");
        }
        catch(StaffNotFoundException ex)
        {
            initializeData();
        }
    }
    
    
    
    private void initializeData() 
    {
        staffEntitySessionBeanLocal.createStaffEntity(new StaffEntity("Eric", "Some", "manager", "password"));
        staffEntitySessionBeanLocal.createStaffEntity(new StaffEntity("Victoria", "Newton", "nurse", "password"));
        
        doctorSessionBeanLocal.createNewDoctor(new DoctorEntity("Tan", "Ming", "S10011", "BMBS"));
        doctorSessionBeanLocal.createNewDoctor(new DoctorEntity("Clair", "Hahn", "S41221", "MBBCh"));
        doctorSessionBeanLocal.createNewDoctor(new DoctorEntity("Robert", "Blake", "S58201","MBBS"));
        
        patientSessionBeanLocal.createPatient(new PatientEntity("S9867027A", "DY3ihrBrktQyJIz6uMDsqA", "Sarah", "Yi", F, 22, "93718799", "13, Clementi Road"));
        patientSessionBeanLocal.createPatient(new PatientEntity("G1314207T", "Qa0Xm0UFdx3HZ6Xs7tyKKQ", "Rajesh", "Singh", M, 36, "93506839", "15, Mountbatten Road"));
    }
}
