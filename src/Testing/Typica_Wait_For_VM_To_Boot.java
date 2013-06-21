package Testing;

import java.util.Arrays;

import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.LaunchConfiguration;
import com.xerox.amazonws.ec2.ReservationDescription;

public class Typica_Wait_For_VM_To_Boot
{
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		String AWSAccessKeyId = "4TDKTOQ8BEOKC4KH8ZCXV";
		String SecretAccessKey = "ythx9u3XekMuZZrV5Cw7Yd4hpLF5ta8vCqVjJPfX";			
		try
		{
			Jec2 connection = new Jec2(AWSAccessKeyId, SecretAccessKey, false, "10.14.79.194", 8773);
			connection.setResourcePrefix("/services/Eucalyptus"); 
			connection.setSignatureVersion(1);
			LaunchConfiguration config = new LaunchConfiguration("emi-A1203A27");
			config.setKeyName("mykey");
			ReservationDescription reservationDescription = connection.runInstances(config);
			String instanceID = reservationDescription.getInstances().get(0).getInstanceId();
			while(true)
			{
				if(Integer.parseInt(connection.describeInstances(Arrays.asList(instanceID)).get(0).getInstances().get(0).getStateCode())==16)
					break;
				Thread.sleep(10000);
			}
			
			System.out.println("VIRTUAL MACHINE HAS STARTED");
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}		
}	