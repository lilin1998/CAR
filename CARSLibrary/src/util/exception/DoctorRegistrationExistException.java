package util.exception;

public class DoctorRegistrationExistException extends Exception
{

    public DoctorRegistrationExistException() 
    {
    }
    
    public DoctorRegistrationExistException(String msg)
    {
        super(msg);
    }
}
