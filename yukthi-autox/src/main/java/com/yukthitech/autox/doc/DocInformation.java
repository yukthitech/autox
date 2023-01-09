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
package com.yukthitech.autox.doc;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.yukthitech.autox.Group;

/**
 * Represents the information required to generate documentation.
 * @author akiran
 */
public class DocInformation
{
	/**
	 * Expected hyphen pattern in names.
	 */
	private static final Pattern HYPHEN_PATTERN = Pattern.compile("\\-(\\w)");

	/**
	 * The basic documents.
	 */
	private List<BasicDocs.Document> basicDocuments;
	
	/**
	 * List of plugin's information.
	 */
	private Map<String, PluginInfo> plugins = new TreeMap<>();
	
	/**
	 * List of step information.
	 */
	private Map<String, StepInfo> steps = new TreeMap<>();
	
	/**
	 * List of validation information.
	 */
	private Map<String, ValidationInfo> validations = new TreeMap<>();
	
	/**
	 * SEt of free method documentations.
	 */
	private Map<String, FreeMarkerMethodDocInfo> freeMarkerMethods = new TreeMap<>();
	
	/**
	 * Ui locator details.
	 */
	private Map<String, UiLocatorDoc> uiLocators = new TreeMap<>();
	
	/**
	 * Expression parser details.
	 */
	private Map<String, PrefixExpressionDoc> prefixExpressions = new TreeMap<>();
	
	/**
	 * Gets the basic documents.
	 *
	 * @return the basic documents
	 */
	public List<BasicDocs.Document> getBasicDocuments()
	{
		return basicDocuments;
	}

	/**
	 * Sets the basic documents.
	 *
	 * @param basicDocuments the new basic documents
	 */
	public void setBasicDocuments(List<BasicDocs.Document> basicDocuments)
	{
		this.basicDocuments = basicDocuments;
	}

	/**
	 * Adds specified plugin.
	 * @param plugin plugin to add.
	 */
	public void addPlugin(PluginInfo plugin)
	{
		plugins.put(plugin.getName(), plugin);
	}
	
	/**
	 * Adds specified step.
	 * @param step step to add.
	 */
	public void addStep(StepInfo step)
	{
		steps.put(step.getName(), step);
	}
	
	/**
	 * Adds specified validation.
	 * @param validation validation to add.
	 */
	public void addValidation(ValidationInfo validation)
	{
		validations.put(validation.getName(), validation);
	}
	
	/**
	 * Gets the list of plugin's information.
	 *
	 * @return the list of plugin's information
	 */
	public Collection<PluginInfo> getPlugins()
	{
		return plugins.values();
	}
	
	/**
	 * From the input name removes the hyphens and injects capital letters approp.
	 * @param name name in which hyphens has to be replaced.
	 * @return resultant string after transformation.
	 */
	private static String removeHyphens(String name)
	{
		StringBuffer buff = new StringBuffer();
		Matcher matcher = HYPHEN_PATTERN.matcher(name);
		
		while(matcher.find())
		{
			matcher.appendReplacement(buff, matcher.group(1).toUpperCase());
		}
		
		matcher.appendTail(buff);
		return buff.toString();
	}
	
	/**
	 * Gets the list of step information.
	 *
	 * @return the list of step information
	 */
	public Collection<StepInfo> getSteps()
	{
		return steps.values();
	}
	
	/**
	 * Gets the step.
	 *
	 * @param name the name
	 * @return the step
	 */
	public StepInfo getStep(String name)
	{
		return this.steps.get( removeHyphens(name) );
	}

	/**
	 * Gets the list of validation information.
	 *
	 * @return the list of validation information
	 */
	public Collection<ValidationInfo> getValidations()
	{
		return validations.values();
	}
	
	/**
	 * Gets the validation.
	 *
	 * @param name the name
	 * @return the validation
	 */
	public ValidationInfo getValidation(String name)
	{
		return validations.get( removeHyphens(name) );
	}

	/**
	 * Gets the sEt of free method documentations.
	 *
	 * @return the sEt of free method documentations
	 */
	public Collection<FreeMarkerMethodDocInfo> getFreeMarkerMethods()
	{
		return freeMarkerMethods.values();
	}

	/**
	 * Sets the sEt of free method documentations.
	 *
	 * @param freeMarkerMethods the new sEt of free method documentations
	 */
	public void setFreeMarkerMethods(Set<FreeMarkerMethodDocInfo> freeMarkerMethods)
	{
		for(FreeMarkerMethodDocInfo met : freeMarkerMethods)
		{
			this.freeMarkerMethods.put(met.getName(), met);
		}
	}
	
	public void addFreeMarkerMethod(FreeMarkerMethodDocInfo methodInfo)
	{
		freeMarkerMethods.put(methodInfo.getName(), methodInfo);
	}
	
	/**
	 * Adds the ui locator.
	 *
	 * @param locator the locator
	 */
	public void addUiLocator(UiLocatorDoc locator)
	{
		this.uiLocators.put(locator.getName(), locator);
	}
	
	/**
	 * Gets the ui locator details.
	 *
	 * @return the ui locator details
	 */
	public Collection<UiLocatorDoc> getUiLocators()
	{
		return uiLocators.values();
	}

	/**
	 * Gets the expression parser details.
	 *
	 * @return the expression parser details
	 */
	public Collection<PrefixExpressionDoc> getPrefixExpressions()
	{
		return prefixExpressions.values();
	}
	
	public PrefixExpressionDoc getPrefixExpression(String name)
	{
		return prefixExpressions.get(name);
	}

	/**
	 * Sets the expression parser details.
	 *
	 * @param parsers the new expression parser details
	 */
	public void setPrefixExpressions(Map<String, PrefixExpressionDoc> parsers)
	{
		this.prefixExpressions = parsers;
	}

	/**
	 * Adds the parser.
	 *
	 * @param parser the parser
	 */
	public void addPrefixExpression(PrefixExpressionDoc parser)
	{
		this.prefixExpressions.put(parser.getName(), parser);
	}
	
	public Set<StepInfo> getStepsWithGroup(String group)
	{
		Set<StepInfo> filSteps = steps.values()
				.stream()
				.filter(step -> step.getGroup().equals(group))
				.collect(Collectors.toSet());

		Set<StepInfo> filVal = validations.values()
				.stream()
				.filter(step -> step.getGroup().equals(group))
				.collect(Collectors.toSet());
		
		TreeSet<StepInfo> res = new TreeSet<StepInfo>(filSteps);
		res.addAll(filVal);
		
		return res;
	}

	public Set<String> getActiveGroups()
	{
		Set<String> groups = new TreeSet<>();
		
		steps.values().forEach(step -> groups.add(step.getGroup()));
		validations.values().forEach(validator -> groups.add(validator.getGroup()));
		
		groups.remove(Group.NONE.name());
		return groups;
	}
}
