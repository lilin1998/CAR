package util.exception;

public class DoctorExistException extends Exception
{

    public DoctorExistException() {
    }
    
    public DoctorExistException(String msg)
    {
        super(msg);
    }            
}
