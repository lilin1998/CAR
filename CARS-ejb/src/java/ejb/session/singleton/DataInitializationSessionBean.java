package ejb.session.singleton;

import ejb.session.stateless.DoctorSessionBeanLocal;
import ejb.session.stateless.PatientSessionBeanLocal;
import ejb.session.stateless.StaffEntitySessionBeanLocal;
import entity.DoctorEntity;
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
        staffEntitySessionBeanLocal.createStaffEntity(new StaffEntity("Eric", "Some", "manager", "6a19bd5f93f7982aae721a97ee2fb02c", "GBLl9FIUI9BrfaL/JTaDGA=="));
        staffEntitySessionBeanLocal.createStaffEntity(new StaffEntity("Victoria", "Newton", "nurse", "cc9c89d57a87e88fbfb9dd951404af87", "TmJTLlnGqmV7VhvgLChQ9w=="));
        
        doctorSessionBeanLocal.createNewDoctor(new DoctorEntity("Tan", "Ming", "S10011", "BMBS"));
        doctorSessionBeanLocal.createNewDoctor(new DoctorEntity("Clair", "Hahn", "S41221", "MBBCh"));
        doctorSessionBeanLocal.createNewDoctor(new DoctorEntity("Robert", "Blake", "S58201","MBBS"));
        
        patientSessionBeanLocal.createPatient(new PatientEntity("S9867027A", "d6dd83e359370b1a64d7fa1ffdc65a6e", "Sarah", "Yi", F, 22, "93718799", "13, Clementi Road", "3uCZ0QU2+a1llCleuLLDDA=="));
        patientSessionBeanLocal.createPatient(new PatientEntity("G1314207T", "947c54483563a637f6676c8300e9b33a", "Rajesh", "Singh", M, 36, "93506839", "15, Mountbatten Road", "a7p2Q7b05oxn9HJKe2YoZw=="));
    }
}
