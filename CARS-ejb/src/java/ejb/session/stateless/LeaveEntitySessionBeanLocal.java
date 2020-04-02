package ejb.session.stateless;

import entity.LeaveEntity;
import java.sql.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.DoctorNotFoundException;
import util.exception.LeaveApplicationException;

@Local
public interface LeaveEntitySessionBeanLocal {
    
    public LeaveEntity createNewLeave(LeaveEntity leaveEntity);

    public List<LeaveEntity> retrieveLeaveByDoctorId(Long doctorId) throws DoctorNotFoundException;

    public List<LeaveEntity> retrieveLeaveByDateNDoctorId(Long doctorId, Date date);

    public void checkIfDoctorAppliedInSameWeek(Long doctorId, Date date) throws DoctorNotFoundException, LeaveApplicationException;
    
}
