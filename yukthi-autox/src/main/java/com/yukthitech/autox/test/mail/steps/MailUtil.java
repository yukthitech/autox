package com.yukthitech.autox.test.mail.steps;

public class MailUtil
{
	public static String[] extractNameAndId(String nameMailId)
	{
		String mailId = nameMailId;
		String name = null;

		if(nameMailId.contains("<"))
		{
			int ltStIdx = nameMailId.indexOf("<");
			name = nameMailId.substring(0, ltStIdx).trim();
			
			mailId = nameMailId.substring(ltStIdx + 1, nameMailId.indexOf(">")).trim();
		}
		else
		{
			name = nameMailId.substring(0, nameMailId.indexOf("@")).trim();
		}

		// remove non alpha characters
		name = name.replaceAll("[^a-zA-Z]", " ").replaceAll("\\s+", " ").trim();
		
		return new String[] {name, mailId};
	}

}
