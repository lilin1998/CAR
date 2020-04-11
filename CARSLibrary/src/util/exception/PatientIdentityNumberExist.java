package util.exception;

public class PatientIdentityNumberExist extends Exception
{

    public PatientIdentityNumberExist() 
    {
    }

    public PatientIdentityNumberExist(String msg)
    {
        super(msg);
    }
}
