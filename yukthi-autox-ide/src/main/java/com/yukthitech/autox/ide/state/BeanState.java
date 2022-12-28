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
package com.yukthitech.autox.ide.state;

import java.awt.Rectangle;
import java.awt.Window;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Used to persist/load bean state based on annotations.
 * @author akranthikiran
 */
public class BeanState implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Map<String, Object> values = new HashMap<>();
	
	public BeanState(Object bean, Class<?> beanType)
	{
		try
		{
			fetchState("$", bean, beanType.getAnnotation(PersistableState.class));
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetching persistance state of bean: {}", bean, ex);
		}
	}
	
	private void fetchDirectState(String prefix, Object bean)
	{
		if(bean instanceof Window)
		{
			values.put(prefix + ".bounds", ((Window)bean).getBounds());
			return;
		}
		
		if(bean instanceof JTextField)
		{
			values.put(prefix + ".value", ((JTextField)bean).getText());
			return;
		}

		if(bean instanceof JTextArea)
		{
			values.put(prefix + ".value", ((JTextArea)bean).getText());
			return;
		}

		if(bean instanceof JCheckBox)
		{
			values.put(prefix + ".value", ((JCheckBox)bean).isSelected());
			return;
		}

		if(bean instanceof JRadioButton)
		{
			values.put(prefix + ".value", ((JRadioButton)bean).isSelected());
			return;
		}

		if(bean instanceof RSyntaxTextArea)
		{
			values.put(prefix + ".value", ((RSyntaxTextArea)bean).getText());
			return;
		}

		if(bean instanceof Serializable)
		{
			values.put(prefix + ".value", bean);
			return ;
		}
	}
	
	private void fetchState(String prefix, Object bean, PersistableState stateAnnot) throws Exception
	{
		if(stateAnnot.directState())
		{
			fetchDirectState(prefix, bean);
		}
		
		if(!stateAnnot.fields())
		{
			return;
		}

		Field fields[] = bean.getClass().getDeclaredFields();
		
		for(Field fld : fields)
		{
			PersistableState fldStateAnnot = fld.getAnnotation(PersistableState.class);
			
			if(fldStateAnnot == null)
			{
				continue;
			}
			
			fld.setAccessible(true);
			Object value = fld.get(bean);
			
			fetchState(prefix + "." + fld.getName(), value, fldStateAnnot);
		}
	}
	
	private <T, V> void ifNotNull(T bean, V value, BiConsumer<T, V> func)
	{
		if(bean == null || value == null)
		{
			return;
		}
		
		func.accept(bean, value);
	}
	
	private void setValues(String prefix, Object bean, Object parent, Field field) throws Exception
	{
		if(bean instanceof Window)
		{
			ifNotNull(
					(Window) bean, 
					(Rectangle) values.get(prefix + ".bounds"), 
					(window, bounds) -> window.setBounds(bounds));
			return;
		}
		
		if(bean instanceof JTextField)
		{
			ifNotNull(
					(JTextField) bean, 
					(String) values.get(prefix + ".value"), 
					(fld, val) -> fld.setText(val));
			return;
		}

		if(bean instanceof JTextArea)
		{
			ifNotNull(
					(JTextArea) bean, 
					(String) values.get(prefix + ".value"), 
					(fld, val) -> fld.setText(val));
			return;
		}

		if(bean instanceof JCheckBox)
		{
			ifNotNull(
					(JCheckBox) bean, 
					(Boolean) values.get(prefix + ".value"), 
					(fld, val) -> fld.setSelected(val));
			return;
		}

		if(bean instanceof JRadioButton)
		{
			ifNotNull(
					(JRadioButton) bean, 
					(Boolean) values.get(prefix + ".value"), 
					(fld, val) -> fld.setSelected(val));
			return;
		}

		if(bean instanceof RSyntaxTextArea)
		{
			ifNotNull(
					(RSyntaxTextArea) bean, 
					(String) values.get(prefix + ".value"), 
					(fld, val) -> fld.setText(val));
			return;
		}
		
		if(field != null)
		{
			Object val = values.get(prefix + ".value");
			
			if(val != null)
			{
				field.set(parent, val);
			}
		}
	}
	
	private void loadState(String prefix, Object bean, Object parent, Field field, PersistableState stateAnnot) throws Exception
	{
		if(stateAnnot.directState())
		{
			setValues(prefix, bean, parent, field);
		}
		
		if(!stateAnnot.fields())
		{
			return;
		}

		Field fields[] = bean.getClass().getDeclaredFields();
		
		for(Field fld : fields)
		{
			PersistableState fldStateAnnot = fld.getAnnotation(PersistableState.class);
			
			if(fldStateAnnot == null)
			{
				continue;
			}
			
			fld.setAccessible(true);
			Object value = fld.get(bean);
			
			loadState(prefix + "." + fld.getName(), value, bean, fld, fldStateAnnot);
		}
	}

	public void loadState(Object toBean, Class<?> beanType)
	{
		try
		{
			loadState("$", toBean, null, null, beanType.getAnnotation(PersistableState.class));
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading persistance state to bean: {}", toBean, ex);
		}
	}
}
