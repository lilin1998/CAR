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
import util.exception.DoctorRegistrationExistException;
import util.exception.InputDataValidationException;
import util.exception.PatientIdentityNumberExist;
import util.exception.StaffNotFoundException;
import util.exception.StaffUsernameExistException;
import util.exception.UnknownPersistenceException;


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
        try {
            staffEntitySessionBeanLocal.createStaffEntity(new StaffEntity("Eric", "Some", "manager", "5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8"));
            staffEntitySessionBeanLocal.createStaffEntity(new StaffEntity("Victoria", "Newton", "nurse", "7567290aba7f74a6b9de363fe2f7e5f3dfcd4034"));

            doctorSessionBeanLocal.createNewDoctor(new DoctorEntity("Tan", "Ming", "S10011", "BMBS"));
            doctorSessionBeanLocal.createNewDoctor(new DoctorEntity("Clair", "Hahn", "S41221", "MBBCh"));
            doctorSessionBeanLocal.createNewDoctor(new DoctorEntity("Robert", "Blake", "S58201", "MBBS"));

            patientSessionBeanLocal.createPatient(new PatientEntity("S9867027A", "17d53696dc12fdd0d06abe10a903ba53b28518ee", "Sarah", "Yi", F, 22, "93718799", "13, Clementi Road"));
            patientSessionBeanLocal.createPatient(new PatientEntity("G1314207T", "26d729762e95602a41153124f7c0db4b8930c68d", "Rajesh", "Singh", M, 36, "93506839", "15, Mountbatten Road"));          
        } 
        catch (PatientIdentityNumberExist | StaffUsernameExistException | DoctorRegistrationExistException | UnknownPersistenceException | InputDataValidationException ex) 
        {
            ex.printStackTrace();
        }
    }
}
