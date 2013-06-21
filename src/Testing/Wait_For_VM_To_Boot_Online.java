package Testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.DataValidation.ErrorStyle;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.LaunchConfiguration;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.ReservationDescription.Instance;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class Wait_For_VM_To_Boot_Online 
{
	public static void main(String[] args) throws IOException, TemplateException 
	{
		String AWSAccessKeyId = "4TDKTOQ8BEOKC4KH8ZCXV";
		String SecretAccessKey = "ythx9u3XekMuZZrV5Cw7Yd4hpLF5ta8vCqVjJPfX";			
		final Jec2 connection = new Jec2(AWSAccessKeyId, SecretAccessKey, false, "10.14.79.194", 8773);
		connection.setResourcePrefix("/services/Eucalyptus"); 
		connection.setSignatureVersion(1);
		
        
        Spark.get(new Route("/"){
           	@Override
           	public Object handle(final Request request, final Response response)
           	{
           		final Configuration configuration = new Configuration();
        		configuration.setClassForTemplateLoading(Wait_For_VM_To_Boot_Online.class, "/");
        		final StringWriter writer = new StringWriter();
        		try 
        		{
        			configuration.setDirectoryForTemplateLoading(new File("Resources"));
				
        			
        			Template template = null;
					template = configuration.getTemplate("RunInstance.ftl");
				
					Map<String, Object> helloMap = new HashMap<String, Object>();
					template.process(helloMap, writer);
        		}
        		 catch (Exception e) {
					// TODO Auto-generated catch block
        			 response.redirect("/error");
        			 e.printStackTrace();
				}
           		return writer;
           	}
           });
        
        Spark.get(new Route("/running") 
        {	
			@Override
			public Object handle(Request request, Response response) 
			{
				final Configuration configuration = new Configuration();
				configuration.setClassForTemplateLoading(Wait_For_VM_To_Boot_Online.class, "/");

				try 
				{
					configuration.setDirectoryForTemplateLoading(new File("Resources"));
				} catch (IOException e1) {
					response.redirect("/error");
					e1.printStackTrace();
				}
				final StringWriter writer = new StringWriter();
				
				LaunchConfiguration config = new LaunchConfiguration("emi-49E13FC0");
				config.setKeyName("mykey");
				ReservationDescription reservationDescription;
				try 
				{
					reservationDescription = connection.runInstances(config);
					String instanceID = reservationDescription.getInstances().get(0).getInstanceId();
					while(true)
					{
						if(Integer.parseInt(connection.describeInstances(Arrays.asList(instanceID)).get(0).getInstances().get(0).getStateCode())==16)
							break;
						Thread.sleep(10000);
					}	
					
					response.redirect("/running/"+instanceID);
				} 
				catch (Exception e) 
				{	
					response.redirect("/error");
					e.printStackTrace();
				}
				return writer;
			}
		});	
        Spark.post(new Route("/running") 
        {	
			@Override
			public Object handle(Request request, Response response) 
			{
				final Configuration configuration = new Configuration();
				configuration.setClassForTemplateLoading(Wait_For_VM_To_Boot_Online.class, "/");

				LaunchConfiguration config = new LaunchConfiguration("emi-A1203A27");
				config.setKeyName("mykey");
				ReservationDescription reservationDescription;
				try 
				{
					reservationDescription = connection.runInstances(config);
					String instanceID = reservationDescription.getInstances().get(0).getInstanceId();
					while(true)
					{
						if(Integer.parseInt(connection.describeInstances(Arrays.asList(instanceID)).get(0).getInstances().get(0).getStateCode())==16)
							break;
						Thread.sleep(10000);
					}	
					
					response.redirect("/running/"+instanceID);
				} 
				catch (Exception e) 
				{	
					response.redirect("/error");
					e.printStackTrace();
				}
				return null;
			}
		});	
        Spark.get(new Route("/running/:instanceID") 
        {	
			@Override
			public Object handle(Request request, Response response) 
			{
				final Configuration configuration = new Configuration();
				configuration.setClassForTemplateLoading(Wait_For_VM_To_Boot_Online.class, "/");

				try 
				{
					configuration.setDirectoryForTemplateLoading(new File("Resources"));
				
					final StringWriter writer = new StringWriter();
					String instanceID = request.params(":instanceID").toUpperCase();
					Template template = null;
					template = configuration.getTemplate("Running.ftl");
					
					Map<String, Object> helloMap = new HashMap<String, Object>();
					helloMap.put("instanceID", instanceID.toUpperCase());
					template.process(helloMap, writer);		
					return writer;
				} 
				catch (Exception e) 
				{	
					response.redirect("/error");
					e.printStackTrace();
					return null;
				}
				
			}
		});	
        
        Spark.post(new Route("/stopped/:instanceID")
        {
        	@Override
			public Object handle(Request request, Response response) 
			{
				final Configuration configuration = new Configuration();
				configuration.setClassForTemplateLoading(Wait_For_VM_To_Boot_Online.class, "/");
		    	try 
				{
					String instanceID = request.params(":instanceID").toUpperCase();
					instanceID = "i"+instanceID.substring(instanceID.indexOf('-'));
					
					connection.terminateInstances(Arrays.asList(instanceID));
					
					while(true)
					{
						if(Integer.parseInt(connection.describeInstances(Arrays.asList(instanceID)).get(0).getInstances().get(0).getStateCode())==48)
							break;
						Thread.sleep(5000);
					}
					connection.terminateInstances(Arrays.asList(instanceID));
					return ("Instance Terminated. Instance ID : "+ instanceID);//+" "+connection.describeInstances(Arrays.asList(instanceID)).toString());
				} 
				catch (Exception e) 
				{
					response.redirect("/error");
					e.printStackTrace();
					return null;
				}
			}
        });	
        
        Spark.post(new Route("/timedRedirect")
        {
        	@Override
			public Object handle(Request request, Response response) 
			{
        		final Configuration configuration = new Configuration();
        		configuration.setClassForTemplateLoading(Wait_For_VM_To_Boot_Online.class, "/");
        		final StringWriter writer = new StringWriter();
        		try 
        		{
        			configuration.setDirectoryForTemplateLoading(new File("Resources"));
				
        			
        			Template template = null;
					template = configuration.getTemplate("TimedRedirect.ftl");
				
					Map<String, Object> helloMap = new HashMap<String, Object>();
					template.process(helloMap, writer);
        		}
        		 catch (Exception e) {
        			 response.redirect("/error");
					e.printStackTrace();
				}
           		return writer;
           	}
        });
        
        Spark.post(new Route("/compile/:instanceID")
        {
        	@Override
			public Object handle(Request request, Response response) 
			{
        		String instanceID = request.params(":instanceID");
				instanceID = "i"+instanceID.substring(instanceID.indexOf('-')).toUpperCase();
				String ipAddress = "";
        		
        		JSch jsch = new JSch();
        		
                String user = "ubuntu";
                
                Session session;
                java.util.Properties config = new java.util.Properties(); 
                config.put("StrictHostKeyChecking", "no");
        		try 
        		{
        			System.out.println(instanceID);
        			System.out.println(ipAddress);
        			ipAddress = connection.describeInstances(Arrays.asList(instanceID)).get(0).getInstances().get(0).getIpAddress();
        			jsch.addIdentity("mykey.private");
        			session = jsch.getSession(user, ipAddress, 22);
        			session.setConfig(config);
        			session.connect();
        			System.out.println(session.isConnected());

        			ChannelSftp channel = (ChannelSftp)session.openChannel("sftp");
        			channel.setInputStream(null);
        			       			
       				channel.connect();
        			((ChannelSftp)channel).cd("/home/ubuntu");
        			FileUtils.writeStringToFile(new File("program.c"), request.queryParams("textarea"));
        			System.out.println(request.queryParams("textarea"));
        			((ChannelSftp)channel).put(new FileInputStream("program.c"), "program.c");
        			channel.disconnect();

        			session.disconnect();
        			response.redirect("/compilationResult/"+instanceID);
        		} 
        		catch (Exception e) 
        		{
        			
        			System.out.println("Exception : "+ e.getMessage());
        			e.printStackTrace();
        			response.redirect("/error");
        		}
        		return null;
          		
           	}
        });
        
        Spark.get(new Route("/compilationResult/:instanceID")
        {
        	@Override
			public Object handle(Request request, Response response) 
			{
        		String instanceID = request.params(":instanceID");
				instanceID = "i"+instanceID.substring(instanceID.indexOf('-')).toUpperCase();
				String ipAddress = "";
        		
        		JSch jsch = new JSch();
        		
                String user = "ubuntu";
                
                Session session;
                java.util.Properties config = new java.util.Properties(); 
                config.put("StrictHostKeyChecking", "no");
                String result = "";
        		try 
        		{
        			System.out.println(instanceID);
        			System.out.println(ipAddress);
        			ipAddress = connection.describeInstances(Arrays.asList(instanceID)).get(0).getInstances().get(0).getIpAddress();
        			jsch.addIdentity("mykey.private");
        			session = jsch.getSession(user, ipAddress, 22);
        			session.setConfig(config);
        			session.connect();
        			System.out.println(session.isConnected());

        			Channel execChannel = session.openChannel("exec");
        			execChannel.setInputStream(null);
        			((ChannelExec)execChannel).setErrStream(null);
        			
        			((ChannelExec)execChannel).setCommand("gcc program.c");
        			InputStream error = ((ChannelExec)execChannel).getErrStream();
        			execChannel.connect();

        			((ChannelExec)execChannel).run();
        			byte[] tmp=new byte[1024];
        		    while(true)
        		    {
        		        while(error.available()>0)
        		        {
        		          int i=error.read(tmp, 0, 1024);
        		          if(i<0)break;
        		          result = result.concat((new String(tmp, 0, i)));
        		        }
        		        if(execChannel.isClosed())
        		        {
        		        	System.out.println(result);
        		        	System.out.println("exit-status: "+execChannel.getExitStatus());
        		        	break;
        		        }
        		    }
        		    execChannel.disconnect();        			
        			
        			session.disconnect();
        			if(result.isEmpty())
        				response.redirect("/compiledCorrectly/"+instanceID);
        			Configuration configuration = new Configuration();
            		configuration.setClassForTemplateLoading(Wait_For_VM_To_Boot_Online.class, "/");
            		StringWriter writer = new StringWriter();
            		configuration.setDirectoryForTemplateLoading(new File("Resources"));
    				
            		
            		Template template = null;
            		template = configuration.getTemplate("CompliationResult.ftl");
    				
            		Map<String, Object> helloMap = new HashMap<String, Object>();
            		helloMap.put("result", result);
            		template.process(helloMap, writer);
            		return writer;
        			
        		} 
        		catch (Exception e) 
        		{
        			
        			System.out.println("Exception : "+ e.getMessage());
        			e.printStackTrace();
        			response.redirect("/error");
        		}
        		return "Result on the Console...";
           		
           		
           	}
        });
        Spark.get(new Route("/compiledCorrectly/:instanceID")
        {
        	@Override
			public Object handle(Request request, Response response) 
			{
        		final Configuration configuration = new Configuration();
        		configuration.setClassForTemplateLoading(Wait_For_VM_To_Boot_Online.class, "/");
        		final StringWriter writer = new StringWriter();
        		try 
        		{
        			configuration.setDirectoryForTemplateLoading(new File("Resources"));
        			Template template = null;
					template = configuration.getTemplate("CompiledCorrectly.ftl");
				
					Map<String, Object> helloMap = new HashMap<String, Object>();
					helloMap.put("instanceID", request.params(":instanceID"));
					template.process(helloMap, writer);
        		}
        		 catch (Exception e) {
        			 response.redirect("/error");
					e.printStackTrace();
				}
           		return writer;
           	}

        });
        
        Spark.get(new Route("/output/:instanceID")
        {
        	@Override
			public Object handle(Request request, Response response) 
			{
        		final Configuration configuration = new Configuration();
        		configuration.setClassForTemplateLoading(Wait_For_VM_To_Boot_Online.class, "/");
        		final StringWriter writer = new StringWriter();
        		String instanceID = request.params(":instanceID");
        		instanceID = "i"+instanceID.substring(instanceID.indexOf('-')).toUpperCase();
				String ipAddress = new String();
        		
        		JSch jsch = new JSch();
        		
                String user = "ubuntu";
                
                Session session;
                java.util.Properties config = new java.util.Properties(); 
                config.put("StrictHostKeyChecking", "no");
                String result = "";
        		try 
        		{
    				instanceID = "i"+instanceID.substring(instanceID.indexOf('-')).toUpperCase();
            		
    				System.out.println(instanceID);
        			System.out.println(ipAddress);
        			ipAddress = connection.describeInstances(Arrays.asList(instanceID)).get(0).getInstances().get(0).getIpAddress();
        			jsch.addIdentity("mykey.private");
        			session = jsch.getSession(user, ipAddress, 22);
        			session.setConfig(config);
        			session.connect();
        			System.out.println(session.isConnected());

        			Channel execChannel = session.openChannel("exec");
        			execChannel.setInputStream(null);
        			((ChannelExec)execChannel).setErrStream(null);
        			
        			((ChannelExec)execChannel).setCommand("./a.out");
        			InputStream in = ((ChannelExec)execChannel).getInputStream();
        			execChannel.connect();

        			((ChannelExec)execChannel).run();
        			byte[] tmp=new byte[1024];
        		      while(true){
        		        while(in.available()>0){
        		          int i=in.read(tmp, 0, 1024);
        		          if(i<0)break;
        		          result = result.concat((new String(tmp, 0, i)));
        		        }
        		        if(execChannel.isClosed()){
        		        	System.out.println(result);
        		          System.out.println("exit-status: "+execChannel.getExitStatus());
        		          break;
        		        }
        		        //try{Thread.sleep(1000);}catch(Exception ee){}
        		      }
        		      execChannel.disconnect();        			
        			
        			session.disconnect();
    				
    				configuration.setDirectoryForTemplateLoading(new File("Resources"));
        			Template template = null;
					template = configuration.getTemplate("Output.ftl");
				
					Map<String, Object> helloMap = new HashMap<String, Object>();
					helloMap.put("result", result);
					template.process(helloMap, writer);
        		}
        		 catch (Exception e) {
        			 response.redirect("/error");
					e.printStackTrace();
				}
           		return writer;
           	}
        });
        
        Spark.get(new Route("/error")
        {
        	@Override
			public Object handle(Request request, Response response) 
			{
        		return "Some internal error has occured. Please try again.";
			}
        });
        
        Spark.post(new Route("/details/:instanceID")
        {
        	@Override
			public Object handle(Request request, Response response) 
			{
        		String instanceID = request.params(":instanceID").toUpperCase();
				instanceID = "i"+instanceID.substring(instanceID.indexOf('-'));
				
				try 
				{
					 Instance instance = connection.describeInstances(Arrays.asList(instanceID)).get(0).getInstances().get(0);
					 return instance.getIpAddress();
					
				} 
				catch (Exception e) 
				{
					// TODO Auto-generated catch block
					response.redirect("/error");
					e.printStackTrace();
					return null;
				}
			}
        });
	}
}