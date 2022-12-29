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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
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
	
	private static class StateProcessor<C, V>
	{
		private Class<C> componentType;
		
		private String name;
		
		private BiConsumer<C, V> writer;
		
		private Function<C, V> reader;

		public StateProcessor(Class<C> componentType, String name, BiConsumer<C, V> writer, Function<C, V> reader)
		{
			this.componentType = componentType;
			this.name = name;
			this.writer = writer;
			this.reader = reader;
		}
	}
	
	private static List<StateProcessor<?, ?>> processors = new ArrayList<>();
	
	static
	{
		register(Window.class, Rectangle.class, "bounds", (wind, bounds) -> wind.setBounds(bounds), wind -> wind.getBounds());
		
		register(JTextField.class, String.class, "value", (fld, txt) -> fld.setText(txt), fld -> fld.getText());
		register(JTextArea.class, String.class, "value", (fld, txt) -> fld.setText(txt), fld -> fld.getText());
		register(RSyntaxTextArea.class, String.class, "value", (fld, txt) -> fld.setText(txt), fld -> fld.getText());
		
		register(JCheckBox.class, Boolean.class, "value", (fld, flag) -> fld.setSelected(flag), fld -> fld.isSelected());
		register(JRadioButton.class, Boolean.class, "value", (fld, flag) -> fld.setSelected(flag), fld -> fld.isSelected());
		
		register(JTabbedPane.class, Integer.class, "selectedIndex", (fld, idx) -> fld.setSelectedIndex(idx), fld -> fld.getSelectedIndex());
	}
	
	private static <C, V> void register(Class<C> c, Class<V> v, String name, BiConsumer<C, V> writer, Function<C, V> reader)
	{
		processors.add(new StateProcessor<>(c, name, writer, reader));
	}
	
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void fetchDirectState(String prefix, Object bean)
	{
		if(bean == null)
		{
			return;
		}
		
		for(StateProcessor<?, ?> processor : processors)
		{
			if(processor.componentType.isInstance(bean))
			{
				Object value = ((Function) processor.reader).apply(bean);
				
				values.put(prefix + "." + processor.name, value);
				return;
			}
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setValues(String prefix, Object bean, Object parent, Field field) throws Exception
	{
		if(bean == null)
		{
			return;
		}
		
		for(StateProcessor<?, ?> processor : processors)
		{
			if(processor.componentType.isInstance(bean))
			{
				Object value = values.get(prefix + "." + processor.name);
				
				if(value == null)
				{
					return;
				}
				
				((BiConsumer) processor.writer).accept(bean, value);
				return;
			}
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
