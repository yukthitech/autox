package com.yukthitech.prism.views.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ContextAttributeTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(ContextAttributeTableModel.class);

	private static ObjectMapper objectMapper = new ObjectMapper();
	
	static
	{
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	private static String columnNames[] = new String[] { "Key", "Value" };
	
	private List<String[]> attributes = new ArrayList<>();

	public ContextAttributeTableModel()
	{
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
	
	public void setContextAttributes(Map<String, byte[]> attributes)
	{
		this.attributes.clear();
		
		if(MapUtils.isEmpty(attributes))
		{
			super.fireTableDataChanged();
			return;
		}
		
		attributes.forEach((key, val) -> 
		{
			this.attributes.add(new String[] {key, toString(val)});
		});
		
		super.fireTableDataChanged();
	}
	
	private String toString(byte[] val)
	{
		if(val == null)
		{
			return "";
		}
		
		Object convVal = null;
		
		try
		{
			convVal = SerializationUtils.deserialize(val);
		}catch(Exception ex)
		{
			return "(Deserialization error)";
		}
		
		if(convVal instanceof String)
		{
			return (String) convVal;
		}
		
		try
		{
			return objectMapper.writeValueAsString(convVal);
		} catch(JsonProcessingException e)
		{
			logger.warn("Failed to convert object into json [Object: {}, Error: {}]", convVal, "" + e);
			return val.toString();
		}
	}
	
	@Override
	public int getRowCount()
	{
		return attributes.size();
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column)
	{
		return columnNames[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		String attr[] = attributes.get(rowIndex);
		return attr[columnIndex];
	}
	
	public String[] getRow(int rowIdx)
	{
		return attributes.get(rowIdx);
	}
}
