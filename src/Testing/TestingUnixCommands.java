package Testing;

import java.io.IOException;

public class TestingUnixCommands {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		try {
			Runtime.getRuntime().exec("ssh root@10.14.254.232 -i /home/harshit/mykey.private");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
