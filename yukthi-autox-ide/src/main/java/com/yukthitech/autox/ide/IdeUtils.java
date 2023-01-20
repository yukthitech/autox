/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.autox.ide;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.ide.services.ResourceCache;
import com.yukthitech.autox.ide.xmlfile.LocationRange;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.pool.ConsolidatedJobManager;

/**
 * Common util methods used across the ide project.
 * @author akiran
 */
public class IdeUtils
{
	private static Logger logger = LogManager.getLogger(IdeUtils.class);
	
	private static class RunnableWrapper implements Runnable
	{
		private Runnable runnable;

		public RunnableWrapper(Runnable runnable)
		{
			this.runnable = runnable;
		}
		
		@Override
		public void run()
		{
			try
			{
				runnable.run();
			}catch(RuntimeException ex)
			{
				logger.error("An error occurred while executing background job", ex);
				throw ex;
			}
		}
	}
	
	/**
	 * Used to serialize and deserialize the objects.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private static Pattern HYPHEN_PATTERN = Pattern.compile("\\-(\\w)");
	
	private static final int BORDER_SIZE = 8;

	/**
	 * Size of file icon.
	 */
	private static final int FILE_ICON_SIZE = 25;
	
	/**
	 * Extension to icon cache map.
	 */
	private static Map<String, ImageIcon> extensionToIcon = new HashMap<>();
	
	/**
	 * Saves the specified object into specified file.
	 * @param object object to persist
	 * @param file file to persist
	 */
	public static void save(Object object, File file)
	{
		try
		{
			if(!file.canWrite())
			{
				file.setWritable(true);
			}
			
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, object);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while saving object [File: {}, Object: {}]", file.getPath(), object, ex);
		}
	}
	
	/**
	 * Loads the object of specified type from specified file.
	 * @param file file to load
	 * @param type type of object to be loaded
	 * @return loaded object
	 */
	public static <T> T load(File file, Class<T> type)
	{
		if(!file.exists())
		{
			return null;
		}
		
		try
		{
			return objectMapper.readValue(file, type);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading object [File: {}, Type: {}]", file.getPath(), type.getName(), ex);
		}
	}
	
	public static void serialize(Serializable object, File file)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			SerializationUtils.serialize(object, fos);
			fos.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while serializing data to file: {}", file.getPath(), ex);
		}
	}
	
	/**
	 * Deserializes the data from specified file.
	 * @param file File to deserialize
	 * @return
	 */
	public static Object deserialize(File file)
	{
		if(!file.exists())
		{
			return null;
		}
		
		try
		{
			FileInputStream fis = new FileInputStream(file);
			Object readObj = SerializationUtils.deserialize(fis);
			fis.close();
			
			return readObj;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading object [File: {}]", file.getPath(), ex);
		}
	}
	
	private static BufferedImage loadSvg(String resource, int size)
	{
		// Create a PNG transcoder.
        Transcoder t = new PNGTranscoder();

        // Set the transcoding hints.
        t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) size);
        t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) size);
        
        try (InputStream inputStream = IdeUtils.class.getResourceAsStream(resource)) 
        {
            // Create the transcoder input.
            TranscoderInput input = new TranscoderInput(inputStream);

            // Create the transcoder output.
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outputStream);

            // Save the image.
            t.transcode(input, output);

            // Flush and close the stream.
            outputStream.flush();
            outputStream.close();

            // Convert the byte stream into an image.
            byte[] imgData = outputStream.toByteArray();
            return ImageIO.read(new ByteArrayInputStream(imgData));

        } catch (IOException | TranscoderException ex) 
        {
            throw new InvalidStateException("An error occurred while loading svg resource: {}", resource, ex);
        }
	}
	
	public static ImageIcon loadIcon(String resource, int size)
	{
		return loadIcon(resource, size, BORDER_SIZE, false);
	}
	
	/**
	 * Loads specified resource as an icon with specified size.
	 * @param resource
	 * @param size
	 * @return
	 */
	public static ImageIcon loadIcon(String resource, int size, boolean grayScale)
	{
		return loadIcon(resource, size, BORDER_SIZE, grayScale);
	}
	
	private static ImageIcon loadIcon(String resource, int size, int borderSize, boolean grayScale)
	{
		return ResourceCache.getInstance().getFromCache(() -> 
		{
			Image baseImg = null;
			int width = size, height = size;
			
			if(resource.toLowerCase().endsWith(".svg"))
			{
				baseImg = loadSvg(resource, size);
			}
			else
			{
				ImageIcon icon = new ImageIcon(IdeUtils.class.getResource(resource));
				baseImg = icon.getImage();
			}
			
			if(size <= 0)
			{
				width = baseImg.getWidth(null);
				height = baseImg.getHeight(null);
			}

			int halfBorderSize = borderSize / 2;
			
			BufferedImage img = new BufferedImage(width + borderSize, height + borderSize, BufferedImage.TYPE_INT_ARGB);
			img.getGraphics().drawImage(baseImg, halfBorderSize, halfBorderSize, width, height, null);
			
			if(grayScale)
			{
				convertToGrayScale(img);
			}
			
			return new ImageIcon(img);
		}, "%s[s:%s,b:%s,g:%s]", resource, size, borderSize, grayScale);
	}

	private static void convertToGrayScale(BufferedImage img)
	{
		int width = img.getWidth();
		int height = img.getHeight();
		
		int pixel[] = new int[4];
		WritableRaster raster = img.getWritableTile(0, 0);

		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				raster.getPixel(i, j, pixel);

				int grayVal = (int) ((pixel[1] + pixel[2] + pixel[3]) / 3.0);
				pixel[1] = pixel[2] = pixel[3] = grayVal;
				
				raster.setPixel(i, j, pixel);
			}
		}
	}
	
	public static ImageIcon loadIconWithoutBorder(String resource, int size)
	{
		return loadIcon(resource, size, 0, false);
	}

	public static ImageIcon loadIconWithoutBorder(String resource, int size, boolean grayScale)
	{
		return loadIcon(resource, size, 0, grayScale);
	}

	private static ImageIcon getEmptyFileIcon()
	{
		ImageIcon imageIcon = extensionToIcon.get("");
		
		if(imageIcon != null)
		{
			return imageIcon;
		}
			
		String fileIconPath = "/ui/file-icons/empty-file.svg";
		imageIcon = loadIconWithoutBorder(fileIconPath, FILE_ICON_SIZE);
		
		extensionToIcon.put("", imageIcon);
		return imageIcon;
	}
	
	public static ImageIcon getFileIcon(File file)
	{
		ImageIcon emptyFileIcon = getEmptyFileIcon();
		
		String fileName = file.getName();
		int dotIdx = fileName.lastIndexOf(".");
		
		if(dotIdx < 0 || dotIdx == (fileName.length() - 1))
		{
			return emptyFileIcon;
		}

		String fullExtension = fileName.substring(dotIdx + 1).toLowerCase();
		
		ImageIcon fileIcon = extensionToIcon.get(fullExtension);
		
		if(fileIcon != null)
		{
			return fileIcon;
		}
		
		String fileIconPath = "/ui/file-icons/" + fullExtension + ".svg";
		URL fileIconUrl = IdeUtils.class.getResource(fileIconPath);
		
		if(fileIconUrl != null)
		{
			fileIcon = loadIconWithoutBorder(fileIconPath, FILE_ICON_SIZE);
			extensionToIcon.put(fullExtension, fileIcon);
			
			return fileIcon;
		}
		
		fileIcon = emptyFileIcon;
		extensionToIcon.put(fullExtension, fileIcon);
		
		return fileIcon;
	}
	
	public static Window getCurrentWindow()
	{
		return KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
	}
	
	/**
	 * Executes specified runnable task after specified delay.
	 * @param runnable
	 * @param delay
	 */
	public static void execute(Runnable runnable, long delay)
	{
		ConsolidatedJobManager.execute(new RunnableWrapper(runnable), delay);
	}
	
	public static void executeConsolidatedJob(String name, Runnable runnable, long delay)
	{
		ConsolidatedJobManager.executeConsolidatedJob(name, new RunnableWrapper(runnable), delay);
	}
	
	public static synchronized void rescheduleConsolidatedJob(String name, Runnable runnable, long delay)
	{
		ConsolidatedJobManager.rescheduleConsolidatedJob(name, new RunnableWrapper(runnable), delay);
	}

	public static void executeUiTask(Runnable runnable)
	{
		EventQueue.invokeLater(new RunnableWrapper(runnable));
	}
	
	public static void centerOnScreen(Component c)
	{
		final int width = c.getWidth();
		final int height = c.getHeight();
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width / 2) - (width / 2);
		int y = (screenSize.height / 2) - (height / 2);

		c.setLocation(x, y);
	}
	
	public static void maximize(Component c, int gap)
	{
		//double the gap, so that gap is maintained in all directions
		gap = gap * 2;
		
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		c.setSize(screenSize.width - gap, screenSize.height - gap);
	}

	public static void autowireBean(ApplicationContext applicationContext, Object bean)
	{
		applicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
		applicationContext.getAutowireCapableBeanFactory().initializeBean(bean, UUID.randomUUID().toString());
	}

	public static int getLineCount(char chArr[], int tillPos)
	{
		int count = 1;
		tillPos = (tillPos >= 0) ? tillPos : (chArr.length - 1);
		
		for(int  i = 0; i < tillPos; i++)
		{
			if(chArr[i] == '\n')
			{
				count++;
			}
		}
		
		return count;
	}
	
	public static void getLocationRange(char chArr[], int from, int end, LocationRange location)
	{
		if(from > end)
		{
			throw new InvalidStateException("From value {} is greater than end value {}", from, end);
		}
		
		if(end > chArr.length)
		{
			throw new InvalidStateException("End value {} is greater than array length {}", end, chArr.length);
		}
		
		int lineNo = 1;
		int colNo = 1;
		
		for(int i = 0; i <= end; i++)
		{
			if(i == from)
			{
				location.setStartLocation(i, lineNo, colNo);
			}
			
			if(chArr[i] == '\n')
			{
				lineNo++;
				colNo = 0;
				continue;
			}
			
			colNo++;
		}
		
		location.setEndLocation(end, lineNo, colNo);
	}
	
	public static String removeHyphens(String str)
	{
		Matcher matcher = HYPHEN_PATTERN.matcher(str);
		StringBuffer buff = new StringBuffer();
		
		while(matcher.find())
		{
			matcher.appendReplacement(buff, matcher.group(1).toUpperCase());
		}
		
		matcher.appendTail(buff);
		return buff.toString();
	}
	
	public static String removeCarriageReturns(String text)
	{
		text = text.replace("\r\n", "\n");
		text = text.replace("\r", "\n");
		
		return text;
	}
	
	public static String loadResource(String resource)
	{
		try
		{
			InputStream is = IdeUtils.class.getResourceAsStream(resource);
			String res = IOUtils.toString(is, Charset.defaultCharset());
			is.close();
			
			return res;
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to load resource: {}", resource, ex);
		}
	}
}
