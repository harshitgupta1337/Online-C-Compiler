package TestingSSH;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.sun.tools.ws.wsdl.document.Output;

public class TestingJSch 
{
	public static void main(String[] args) 
	{
		JSch jsch = new JSch();
		
        String user = "ubuntu";
        String host = "10.14.254.235";
        Session session;
        java.util.Properties config = new java.util.Properties(); 
        config.put("StrictHostKeyChecking", "no");
		try 
		{
			jsch.addIdentity("/home/harshit/mykey.private");
			session = jsch.getSession(user, host, 22);
			session.setConfig(config);
			session.connect();
			System.out.println(session.isConnected());

			/*ChannelSftp channel= (ChannelSftp)session.openChannel("sftp");
					
			channel.connect();
			((ChannelSftp)channel).cd("/home/ubuntu");
			System.out.println(((ChannelSftp)channel).getHome());
			//channel.put(new FileInputStream("/home/harshit/HelloWorld.c"), "HelloWorld3.c");
			channel.disconnect();*/
			
			Channel execChannel = session.openChannel("exec");
			execChannel.setInputStream(null);
			
			((ChannelExec)execChannel).setCommand("./a.out");
			InputStream in = execChannel.getInputStream();
			execChannel.connect();

			((ChannelExec)execChannel).run();
			byte[] tmp=new byte[1024];
		      while(true){
		        while(in.available()>0){
		          int i=in.read(tmp, 0, 1024);
		          if(i<0)break;
		          System.out.print(new String(tmp, 0, i));
		        }
		        if(execChannel.isClosed()){
		          System.out.println("exit-status: "+execChannel.getExitStatus());
		          break;
		        }
		        try{Thread.sleep(1000);}catch(Exception ee){}
		      }
		      execChannel.disconnect();
		      session.disconnect();
		    
			execChannel.disconnect();
			
			/*ChannelSftp channelGet= (ChannelSftp)session.openChannel("sftp");
			channelGet.connect();
			((ChannelSftp)channelGet).cd("/home/ubuntu");
			channelGet.get("a.out", "/home/harshit/a.out");
			channelGet.disconnect();*/
			
			session.disconnect();

			
			
		} 
		catch (Exception e) 
		{
			System.out.println("Exception : "+ e.getMessage());
		}
	}
}