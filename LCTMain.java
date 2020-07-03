package instance;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpleemail.model.GetIdentityDkimAttributesRequest;


@SuppressWarnings("unused")
public class LCTMain {
	
	
	static AmazonEC2      ec2;
	
	
	  @SuppressWarnings("deprecation")
	static void init() throws Exception {
	    	/*
			 * This credentials provider implementation loads your AWS credentials
			 * from a properties file at the root of your classpath.
			 */
	        AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();

	        ec2 = new AmazonEC2Client(credentialsProvider);
	        ec2.setEndpoint("ec2.us-west-2.amazonaws.com");
	    }
	 

	    public static void main(String[] args) throws Exception {

	        System.out.println("===========================================");
	        System.out.println("This is LCT Main Program Written By BanuMeena!");
	        System.out.println("===========================================");

	        init();
	        ec2.setEndpoint("ec2.us-west-2.amazonaws.com");
	        System.out.println("1. List Instance");
	        System.out.println("2. Create Instance");
	        System.out.println("3. Terminate Instance");
	        System.out.println("4. Exit");
	        int choice=1;
	        
	        while(choice>=1)
	        {
	        	Scanner in = new Scanner(System.in);
		         choice = in.nextInt();

	        	if(choice==1)
	        		{
	        		System.out.println("List Instance(s)");
	        		String a[]=list();
	        		System.out.println("Successfully All instances are listed");
	        		}
	        	else if(choice==2)
	        		{
	        		System.out.println("Create new Instance");
	        		create();
	        		}
	        	else if(choice==3)
	        		{
	        		System.out.println("Terminate Instances");
	        		terminate();
	        		}
	        	else if(choice==5)
        		{
        		System.out.println("Terminate Last Instance");
        		terminateLast();
        		}
	        	
	        	else if(choice==4)
        		{
        		System.out.println("Exited");
        		
        		System.exit(0);
        		}
	        	else
	        		System.out.println("Invalid choice");
	        }
	        
	       
	        
	        
	    }
	    
	    
	    
	    
	
	    static String[] list()
        {
	    	String[] instance_ID =new String[20];
	    	int z = 0;
	    	StringBuffer instIDs=new StringBuffer();
        
        	try {
            
            DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
            List<Reservation> reservations = describeInstancesRequest.getReservations();
            Set<Instance> instances = new HashSet<Instance>();

            for (Reservation reservation : reservations) {
                instances.addAll(reservation.getInstances());
                List<Instance> k= reservation.getInstances();
                for(z=0;z<k.size();z++)
                {
                	         	
               	instance_ID[z]=k.get(z).getInstanceId();
                	if(instance_ID[z]!=null){
                		instIDs.append(instance_ID[z]); 
                		if(k.get(z).getState().toString().contains("running"))
                			instIDs.append(":::running"); 
                		if(k.get(z).getState().toString().contains("terminated"))
                			instIDs.append(":::terminated"); 
                		if(k.get(z).getState().toString().contains("stopping"))
                			instIDs.append(":::stopping"); 
                		if(k.get(z).getState().toString().contains("stopped"))
                			instIDs.append(":::stopped"); 
                		
                		instIDs.append(":::"+k.get(z).getPublicDnsName());
                		instIDs.append(":::"+k.get(z).getPlatform());
                		
                		instIDs.append(",");
                	}
                	
                	System.out.println("Instance ID: "+instance_ID[z]);
                	
                	System.out.println("hi"+k.get(z).getState());
                	
                	System.out.println("Type  :"+k.get(z).getInstanceType());
                	System.out.println("Name  :"+k.get(z).getPublicDnsName());
                	
					
					
                }
                
				
				
            }

            System.out.println("You have " + instances.size() + " Amazon EC2 instance(s) running.");
            
            
        } catch (AmazonServiceException ase) {
                System.out.println("Caught Exception: " + ase.getMessage());
                System.out.println("Reponse Status Code: " + ase.getStatusCode());
                System.out.println("Error Code: " + ase.getErrorCode());
                System.out.println("Request ID: " + ase.getRequestId());
        }
        	
        	instance_ID=instIDs.substring(0, instIDs.length()-1).split(",");
        	//System.out.println("instIDs"+instIDs.substring(0, instIDs.length()-1));        	
        	return	instance_ID;
        	
        	
        
        }
	    
	    static void create()
	    {
	    	
	    	try {
	                      					
	            					 RunInstancesRequest runInstancesRequest = 
	            							  new RunInstancesRequest();
	            						        	
	            						  runInstancesRequest.withImageId("ami-6e833e0e")
	            						                     .withInstanceType("t1.micro")
	            						                     .withMinCount(1)
	            						                     .withMaxCount(1)
	            						                     .withKeyName("ec2need")
	            						                     .withSecurityGroups("launch-wizard-5");	
	            						  RunInstancesResult runInstancesResult = 
	            							  ec2.runInstances(runInstancesRequest);	
	            						  
	            						  
	            					
	            
	        } catch (AmazonServiceException ase) {
	                System.out.println("Caught Exception: " + ase.getMessage());
	                System.out.println("Reponse Status Code: " + ase.getStatusCode());
	                System.out.println("Error Code: " + ase.getErrorCode());
	                System.out.println("Request ID: " + ase.getRequestId());
	        }

	    	
	    	
	    	
	    	
	    }
	    
	    
	    static void terminate()
	    {
	    	
	    	try
	    	{
	    		TerminateInstancesRequest terminateInstancesRequest= new TerminateInstancesRequest();
	    		
	    		DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
	            List<Reservation> reservations = describeInstancesRequest.getReservations();
	            for (Reservation reservation : reservations) 
	            {	            
	            	List<Instance> k= reservation.getInstances();
	            	 for(int z=0;z<k.size();z++)
	                 {
	                 	System.out.println("hi"+k.get(z).getState());
	                 	if(k.get(z).getState().getCode()==16)
	                 	{  
	                 		terminateInstancesRequest.withInstanceIds(k.get(z).getInstanceId());
	                 	}
	                 }
	            	
	            }
	    		
	    		
	    		ec2.terminateInstances(terminateInstancesRequest);
	    	}
	    	 catch (AmazonServiceException ase) {
	                System.out.println("Caught Exception: " + ase.getMessage());
	                System.out.println("Reponse Status Code: " + ase.getStatusCode());
	                System.out.println("Error Code: " + ase.getErrorCode());
	                System.out.println("Request ID: " + ase.getRequestId());
	        }
	    	
	    }
	    static void terminateLast()
	    {
	    	
	    	try
	    	{
	    		TerminateInstancesRequest terminateInstancesRequest= new TerminateInstancesRequest();
	    		
	    		DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
	    		String Lid = null;
	            List<Reservation> reservations = describeInstancesRequest.getReservations();
	            for (Reservation reservation : reservations) 
	            {	            
	            	List<Instance> k= reservation.getInstances();
	            	 for(int z=0;z<k.size();z++)
	                 {
	                 	System.out.println("hi"+k.get(z).getState());
	                 	if(k.get(z).getState().getCode()==16)
	                 	{  
	                 		
	                 		Lid=k.get(z).getInstanceId();
	                 		
	                 	}
	                 }
	            	
	            }
	            terminateInstancesRequest.withInstanceIds(Lid);
	    		
	    		ec2.terminateInstances(terminateInstancesRequest);
	    	}
	    	 catch (AmazonServiceException ase) {
	                System.out.println("Caught Exception: " + ase.getMessage());
	                System.out.println("Reponse Status Code: " + ase.getStatusCode());
	                System.out.println("Error Code: " + ase.getErrorCode());
	                System.out.println("Request ID: " + ase.getRequestId());
	        }
	    	
	    }
	

}
