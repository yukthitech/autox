package com.yukthitech.autox.test.mail.steps;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MailMessage implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static class NameAndMailId implements Serializable
	{
		private static final long serialVersionUID = 1L;

		private String name;
		
		private String mailId;

		public NameAndMailId(String name, String mailId)
		{
			this.name = name;
			this.mailId = mailId;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getMailId()
		{
			return mailId;
		}
	}
	
	public static class Attachment implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		/**
		 * Attachment content in temp file.
		 */
		private transient File file;
		
		/**
		 * Name of the attachment.
		 */
		private String name;
		
		/**
		 * Instantiates a new attachment.
		 */
		public Attachment()
		{}

		/**
		 * Instantiates a new attachment.
		 *
		 * @param file the file
		 * @param name the name
		 */
		public Attachment(File file, String name)
		{
			this.file = file;
			this.name = name;
		}

		/**
		 * Gets the attachment content in temp file.
		 *
		 * @return the attachment content in temp file
		 */
		public File getFile()
		{
			return file;
		}

		/**
		 * Sets the attachment content in temp file.
		 *
		 * @param file the new attachment content in temp file
		 */
		public void setFile(File file)
		{
			this.file = file;
		}

		/**
		 * Gets the name of the attachment.
		 *
		 * @return the name of the attachment
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Sets the name of the attachment.
		 *
		 * @param name the new name of the attachment
		 */
		public void setName(String name)
		{
			this.name = name;
		}
	}

	private NameAndMailId fromId;
	
	private String subject;
	
	private List<NameAndMailId> toIds = new ArrayList<>();
	
	private List<NameAndMailId> ccIds = new ArrayList<>();
	
	private List<NameAndMailId> bccIds = new ArrayList<>();
	
	private String contentType;
	
	private String content;
	
	private List<Attachment> attachments;
	
	public NameAndMailId getFromId()
	{
		return fromId;
	}

	public void setFromId(NameAndMailId fromId)
	{
		this.fromId = fromId;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public List<NameAndMailId> getToIds()
	{
		return toIds;
	}

	public void setToIds(List<NameAndMailId> toIds)
	{
		this.toIds = toIds;
	}

	public List<NameAndMailId> getCcIds()
	{
		return ccIds;
	}

	public void setCcIds(List<NameAndMailId> ccIds)
	{
		this.ccIds = ccIds;
	}

	public List<NameAndMailId> getBccIds()
	{
		return bccIds;
	}

	public void setBccIds(List<NameAndMailId> bccIds)
	{
		this.bccIds = bccIds;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}
	
	public void appendContent(String content)
	{
		if(this.content == null)
		{
			this.content = content;
			return;
		}
		
		this.content += "\n".concat(content);
	}

	public String getContentType()
	{
		return contentType;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public List<Attachment> getAttachments()
	{
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments)
	{
		this.attachments = attachments;
	}
	
	public void addAttachment(Attachment attachment)
	{
		if(this.attachments == null)
		{
			this.attachments = new ArrayList<>();
		}
		
		this.attachments.add(attachment);
	}
}
