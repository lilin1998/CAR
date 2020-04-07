package ejb.session.stateless;

import entity.StaffEntity;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import util.exception.InvalidLoginCredentialException;
import util.exception.StaffNotFoundException;

public interface StaffEntitySessionBeanLocal {
    
    public Long createStaffEntity(StaffEntity newStaffEntity);
    
    public List<StaffEntity> retrieveAllStaffs();    
    
    public StaffEntity retrieveStaffEntityByStaffId(Long staffId) throws StaffNotFoundException;

    public StaffEntity retrieveStaffByUsername(String username) throws StaffNotFoundException;
    
    public StaffEntity staffLogin(String username, String password) throws InvalidLoginCredentialException;

    public void updateStaffEntity(StaffEntity staffEntity);

    public void deleteStaffEntity(Long staffId);       

    public String getSecurePassword(String passwordToHash, byte[] salt);

    public byte[] getSalt() throws NoSuchAlgorithmException, NoSuchProviderException;
}
