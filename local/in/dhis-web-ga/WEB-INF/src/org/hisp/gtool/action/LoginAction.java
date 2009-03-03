package org.hisp.gtool.action;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LoginAction {

	Connection con = (new DBConnection()).openConnection();
	
	public String getEncryptedPwd(String userName, String rawPwd)
	{
		String plainText = rawPwd+"{" + userName.hashCode() + "}";		  
		MessageDigest mdAlgorithm = null;
		try
		{
			mdAlgorithm = MessageDigest.getInstance("MD5");
		}
		catch(NoSuchAlgorithmException e)
		{
			System.out.println(e.getMessage());
		}
		mdAlgorithm.update(plainText.getBytes());
		byte[] digest = mdAlgorithm.digest();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < digest.length; i++) 
		{
			plainText = Integer.toHexString(0xFF & digest[i]);
		    if (plainText.length() < 2)         plainText = "0" + plainText;		      
		    hexString.append(plainText);
		}
		  
		return hexString.toString();
	}//getEncryptedPwd end
	
	public  boolean loginValidation(String userName, String userPwd)
	{
		Statement st = null;
		ResultSet rs = null;
		
		if(userName == null || userPwd == null) return false;
		
		System.out.println(userName);
		String encryptedPwd = getEncryptedPwd(userName, userPwd); 
		boolean loginStatus = false;
		
		/*
		String query = "SELECT id FROM usercredentials " +
							"WHERE username LIKE '"+userName+"' AND password LIKE '"+encryptedPwd+"'";		
		*/
		
		String query = "SELECT userid FROM users " +
							"WHERE username LIKE '"+userName+"' AND password LIKE '"+encryptedPwd+"'";		

		try
		{
			st = con.createStatement();
			rs = st.executeQuery(query);
			
			if(rs.next())	{loginStatus = true;}
		} // try block end
		catch(Exception e) 	{	System.out.println("Some Exception");return false;	}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){ System.out.println("Some Exception"); return false;}
		}// finally block end
		
		return loginStatus;
	}// loginValidation end
	
	
	/*
	 * To retrieve login details like user id, user's organisationunit
	 */
	public  List getLoginDetails(String userName)
	{
		Statement st = null;
		ResultSet rs = null;
		
		System.out.println(userName);
		
		List loginDetails = new ArrayList();
		/*
		String query = "SELECT users.id,users.organisationUnit " +
							"FROM usercredentials " +
								"INNER JOIN users " +
									"ON users.id = usercredentials.id " +
							"WHERE usercredentials.username LIKE '"+userName+"'"; 		
		*/
		String query = "SELECT userinfo.userinfoid,userinfo.organisationunitid FROM users " +
					   		"INNER JOIN userinfo ON users.userid = userinfo.userinfoid " +
					   		"WHERE users.username LIKE '"+userName+"'";
		try
		{
			st = con.createStatement();
			rs = st.executeQuery(query);
			
			if(rs.next())	
			{			
				loginDetails.add(0,""+rs.getInt(1));
				loginDetails.add(1,""+rs.getInt(2));
				System.out.println("userID : "+rs.getInt(1)+"ouID : "+rs.getInt(2));
			}							
		} // try block end
		catch(Exception e) 	{	System.out.println("Some Exception"+e.getMessage());return null;	}
		finally
		{
			try
			{
				if(rs!=null) rs.close();
				if(st!=null) st.close();
			}
			catch(Exception e){ System.out.println("Some Exception"); return null;}
		}// finally block end
		
		System.out.println("Function Returns Successfully");
		return loginDetails;
	}// getLoginDetails end
	
}// class end
