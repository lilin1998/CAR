package ejb.session.stateless;

import entity.StaffEntity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;
import util.exception.StaffNotFoundException;

@Stateless
@Local(StaffEntitySessionBeanLocal.class)
@Remote(StaffEntitySessionBeanRemote.class)
public class StaffEntitySessionBean implements StaffEntitySessionBeanRemote, StaffEntitySessionBeanLocal 
{
    @PersistenceContext(unitName = "CARS-ejbPU")
    private EntityManager em;

    
    
    public StaffEntitySessionBean() 
    {
    }
    
    
    
    @Override
    public Long createStaffEntity(StaffEntity newStaffEntity) 
    {
        em.persist(newStaffEntity);
        em.flush();
        StaffEntity s = null;
        try 
        {
            s = retrieveStaffEntityByStaffId(newStaffEntity.getStaffId());
        } 
        catch (StaffNotFoundException e) 
        {
            Logger.getLogger(StaffEntitySessionBean.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return s.getStaffId();
    }
    
    
    
    @Override
    public List<StaffEntity> retrieveAllStaffs()
    {
        Query query = em.createQuery("SELECT s FROM StaffEntity s");
        
        return query.getResultList();
    }
    
    
    
    @Override
    public StaffEntity retrieveStaffEntityByStaffId(Long staffId) throws StaffNotFoundException
    {
        StaffEntity staffEntity = em.find(StaffEntity.class, staffId);
        
        if(staffEntity != null) 
        {
            return staffEntity;
        }
        else 
        {
            throw new StaffNotFoundException("Staff ID " + staffId + " does not exist!");
        }
    } 
    
    
    
    @Override
    public StaffEntity retrieveStaffByUsername(String username) throws StaffNotFoundException
    {
        Query query = em.createQuery("SELECT s FROM StaffEntity s WHERE s.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try
        {
            return (StaffEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new StaffNotFoundException("Staff Username " + username + " does not exist!");
        }
    }
    
    
    
    @Override
    public StaffEntity staffLogin(String username, String password) throws InvalidLoginCredentialException
    {
        try
        {
            StaffEntity staffEntity = retrieveStaffByUsername(username);
            String userSalt = staffEntity.getUsersalt();
            if (userSalt != null)
            {
                byte[] salt = Base64.getDecoder().decode(userSalt);
                String securePassword = getSecurePassword(password, salt);
        
                if (securePassword.equals(staffEntity.getPassword()))
                {
                    return staffEntity;
                }
                else
                {
                    throw new InvalidLoginCredentialException("Password is invalid!");
                }
            }
            else 
            {
                staffEntity = retrieveStaffByUsername(username);
                return staffEntity;
            }
        }
        catch(StaffNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Username does not exist!");
        }
    }
    
    
    
    @Override
    public void updateStaffEntity(StaffEntity staffEntity)
    {
        try 
        {
            StaffEntity s = retrieveStaffEntityByStaffId(staffEntity.getStaffId());
            s.setFirstName(staffEntity.getFirstName());
            s.setLastName(staffEntity.getLastName());
            s.setPassword(staffEntity.getPassword());
            s.setUsername(staffEntity.getUsername());
            s.setUsersalt(staffEntity.getUsersalt());

        } catch (StaffNotFoundException e) {
            Logger.getLogger(StaffEntitySessionBean.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    
    
    @Override
    public void deleteStaffEntity(Long staffId)
    {
        StaffEntity staffEntity = new StaffEntity();
        try {
            staffEntity = retrieveStaffEntityByStaffId(staffId);
        } catch (StaffNotFoundException ex) {
            Logger.getLogger(StaffEntitySessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        em.remove(staffEntity);
    }
    
    
    
    @Override
    public String getSecurePassword(String passwordToHash, byte[] salt) 
    {
        String generatedPassword = null;
        try 
        {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(salt);
            //Get the hash's bytes 
            byte[] bytes = md.digest(passwordToHash.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) 
        {
            System.err.println("Exception encountered in getSecurePassword()");
        }
        
        return generatedPassword;
    }
     
    
    
    @Override
    public byte[] getSalt() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        //Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        //Create array for salt
        byte[] salt = new byte[16];
        //Get a random salt
        sr.nextBytes(salt);
        //return salt
        return salt;
    }
}
