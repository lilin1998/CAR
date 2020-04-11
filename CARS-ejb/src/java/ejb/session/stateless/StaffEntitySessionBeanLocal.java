package ejb.session.stateless;

import entity.StaffEntity;
import java.util.List;
import util.exception.CreateStaffException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.StaffNotFoundException;
import util.exception.StaffUsernameExistException;
import util.exception.UnknownPersistenceException;

public interface StaffEntitySessionBeanLocal {
    
    public Long createStaffEntity(StaffEntity newStaffEntity) throws StaffUsernameExistException, UnknownPersistenceException, InputDataValidationException;
    
    public List<StaffEntity> retrieveAllStaffs();    
    
    public StaffEntity retrieveStaffEntityByStaffId(Long staffId) throws StaffNotFoundException;

    public StaffEntity retrieveStaffByUsername(String username) throws StaffNotFoundException;
    
    public StaffEntity staffLogin(String username, String password) throws InvalidLoginCredentialException;

    public void updateStaffEntity(StaffEntity staffEntity) throws StaffNotFoundException, InputDataValidationException;

    public void deleteStaffEntity(Long staffId);       

    public String getSecurePassword(String passwordToHash);

    public void inputIsIncorrect(StaffEntity staffEntity) throws CreateStaffException;
}
