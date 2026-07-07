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

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AutoxVersion;
import com.yukthitech.autox.Group;

/**
 * Generates LLM-friendly markdown reference files from {@link DocInformation}.
 * @author akiran
 */
public class LlmMarkdownGenerator
{
	private static final String REFERENCE_FOLDER = "reference";

	private static final String[] LEGACY_REFERENCE_FILES = {
			"steps-rest.md",
			"steps-common.md",
			"validations.md"
	};

	private LlmMarkdownGenerator()
	{
	}

	/**
	 * Generates all LLM markdown reference files and version stamp in the output folder.
	 */
	public static void generate(File outFolder, DocInformation docInformation) throws Exception
	{
		File referenceFolder = new File(outFolder, REFERENCE_FOLDER);
		FileUtils.forceMkdir(referenceFolder);

		removeLegacyReferenceFiles(referenceFolder);

		List<String> generatedGroupFiles = new ArrayList<>();

		for(String group : docInformation.getActiveGroups())
		{
			String slug = toGroupSlug(group);
			String stepsFile = "steps-" + slug + ".md";
			String validationsFile = "validations-" + slug + ".md";

			writeFile(referenceFolder, stepsFile, generateStepsForGroup(docInformation, group));
			writeFile(referenceFolder, validationsFile, generateValidationsForGroup(docInformation, group));

			generatedGroupFiles.add(stepsFile);
			generatedGroupFiles.add(validationsFile);
		}

		writeFile(referenceFolder, "plugins.md", generatePlugins(docInformation));
		writeFile(referenceFolder, "prefix-expressions.md", generatePrefixExpressions(docInformation));
		writeFile(referenceFolder, "freemarker-methods.md", generateFreeMarkerMethods(docInformation));
		writeFile(referenceFolder, "ui-locators.md", generateUiLocators(docInformation));
		writeFile(referenceFolder, "README.md", generateReferenceReadme(docInformation, generatedGroupFiles));

		FileUtils.writeStringToFile(new File(outFolder, "autox-version.txt"),
				AutoxVersion.getVersion(), Charset.defaultCharset());
	}

	static String toGroupSlug(String groupName)
	{
		return groupName.toLowerCase().replace('_', '-');
	}

	private static void removeLegacyReferenceFiles(File referenceFolder) throws Exception
	{
		for(String fileName : LEGACY_REFERENCE_FILES)
		{
			File legacyFile = new File(referenceFolder, fileName);

			if(legacyFile.isFile())
			{
				FileUtils.deleteQuietly(legacyFile);
				System.out.println("Removed legacy LLM markdown: " + legacyFile.getAbsolutePath());
			}
		}
	}

	private static void writeFile(File folder, String fileName, String content) throws Exception
	{
		File file = new File(folder, fileName);
		FileUtils.writeStringToFile(file, content, Charset.defaultCharset());
		System.out.println("Generated LLM markdown: " + file.getAbsolutePath());
	}

	private static String generateReferenceReadme(DocInformation docInformation, List<String> generatedGroupFiles)
	{
		StringBuilder buff = new StringBuilder();
		buff.append("# AutoX API Reference Index\n\n");
		buff.append("Auto-generated index of LLM reference files. Steps and validations are split by the ");
		buff.append("[`Group`](../../src/main/java/com/yukthitech/autox/Group.java) enum ");
		buff.append("declared on each `@Executable` step/validation.\n\n");

		buff.append("## Steps and validations by group\n\n");
		buff.append("| Group | Steps | Validations |\n");
		buff.append("|-------|-------|-------------|\n");

		for(String group : docInformation.getActiveGroups())
		{
			String slug = toGroupSlug(group);
			buff.append("| `").append(group).append("` | ");
			buff.append("[steps-").append(slug).append(".md](steps-").append(slug).append(".md) | ");
			buff.append("[validations-").append(slug).append(".md](validations-").append(slug).append(".md) |\n");
		}

		buff.append("\n## Other reference files\n\n");
		buff.append("| Topic | File |\n");
		buff.append("|-------|------|\n");
		buff.append("| Plugins | [plugins.md](plugins.md) |\n");
		buff.append("| Prefix expressions (`attr:`, `xpath:`, `json:`, etc.) | [prefix-expressions.md](prefix-expressions.md) |\n");
		buff.append("| FreeMarker methods (`${...}`) | [freemarker-methods.md](freemarker-methods.md) |\n");
		buff.append("| UI locator types | [ui-locators.md](ui-locators.md) |\n");
		buff.append("| Full machine-readable schema | [../doc-information.json](../doc-information.json) |\n");

		buff.append("\n## All Group enum values\n\n");
		buff.append("Defined in `com.yukthitech.autox.Group`: ");
		buff.append(String.join(", ", Arrays.stream(Group.values())
				.filter(g -> g != Group.NONE)
				.map(Group::name)
				.toArray(String[]::new)));
		buff.append("\n\nFiles are generated only for groups that have at least one step or validation.\n\n");

		buff.append("## Notes\n\n");
		buff.append("- `proxy-host-port` is a REST step attribute, not a separate group.\n");
		buff.append("- Prefix expressions from `DefaultPrefixExpressions` and plugin parsers are documented in a single file.\n");
		buff.append("- For how-to patterns see the numbered guides in the parent folder (`07-rest-automation.md`, `11-language-steps.md`, etc.).\n");

		return buff.toString();
	}

	private static String generatePlugins(DocInformation docInformation)
	{
		StringBuilder buff = new StringBuilder();
		buff.append("# Plugins\n\n");
		buff.append("Auto-generated reference for AutoX plugins. Configure these in `app-configuration.xml`.\n\n");

		for(PluginInfo plugin : docInformation.getPlugins())
		{
			buff.append("## ").append(plugin.getName()).append("\n\n");
			buff.append("- **Description**: ").append(nullSafe(plugin.getDescription())).append("\n");
			buff.append("- **Java type**: `").append(plugin.getJavaType()).append("`\n");

			if(CollectionUtils.isNotEmpty(plugin.getParams()))
			{
				buff.append("\n### Parameters\n\n");
				for(ParamInfo param : plugin.getParams())
				{
					appendParam(buff, param);
				}
			}

			if(CollectionUtils.isNotEmpty(plugin.getEvents()))
			{
				buff.append("\n### Events\n\n");
				for(PluginEventInfo event : plugin.getEvents())
				{
					buff.append("#### ").append(event.getName()).append("\n\n");
					buff.append("- **Description**: ").append(nullSafe(event.getDescription())).append("\n");
					if(StringUtils.isNotBlank(event.getReturnDescription()))
					{
						buff.append("- **Return**: ").append(event.getReturnDescription()).append("\n");
					}
					if(CollectionUtils.isNotEmpty(event.getParams()))
					{
						buff.append("\n**Event parameters:**\n\n");
						for(ParamInfo param : event.getParams())
						{
							appendParam(buff, param);
						}
					}
					buff.append("\n");
				}
			}

			buff.append("\n");
		}

		return buff.toString();
	}

	private static String generateStepsForGroup(DocInformation docInformation, String group)
	{
		StringBuilder buff = new StringBuilder();
		buff.append("# ").append(group).append(" Steps\n\n");
		buff.append("Auto-generated reference for `Group.").append(group).append("` steps. ");
		buff.append("Use step tags with the `s:` namespace.\n\n");

		Set<StepInfo> steps = docInformation.getStepsForGroup(group);

		if(steps.isEmpty())
		{
			buff.append("_No steps in this group._\n");
			return buff.toString();
		}

		for(StepInfo step : steps)
		{
			appendStep(buff, step);
		}

		return buff.toString();
	}

	private static String generateValidationsForGroup(DocInformation docInformation, String group)
	{
		StringBuilder buff = new StringBuilder();
		buff.append("# ").append(group).append(" Validations\n\n");
		buff.append("Auto-generated reference for `Group.").append(group).append("` assertion/validation steps.\n\n");

		Set<ValidationInfo> validations = docInformation.getValidationsForGroup(group);

		if(validations.isEmpty())
		{
			buff.append("_No validations in this group._\n");
			return buff.toString();
		}

		for(ValidationInfo validation : validations)
		{
			appendStep(buff, validation);
		}

		return buff.toString();
	}

	private static void appendStep(StringBuilder buff, StepInfo step)
	{
		String tagName = "s:" + step.getNameWithHyphens();

		buff.append("### ").append(tagName).append("\n\n");
		buff.append("- **Title**: ").append(nullSafe(step.getTitle())).append("\n");
		buff.append("- **Group**: ").append(nullSafe(step.getGroup())).append("\n");
		buff.append("- **Description**: ").append(nullSafe(step.getDescription())).append("\n");
		buff.append("- **Java type**: `").append(step.getJavaType()).append("`\n");

		if(CollectionUtils.isNotEmpty(step.getRequiredPlugins()))
		{
			buff.append("- **Required plugins**: ").append(String.join(", ", step.getRequiredPlugins())).append("\n");
		}

		if(CollectionUtils.isNotEmpty(step.getParams()))
		{
			buff.append("\n**Attributes:**\n\n");
			for(ParamInfo param : step.getParams())
			{
				appendParam(buff, param);
			}
		}

		if(CollectionUtils.isNotEmpty(step.getChildElements()))
		{
			buff.append("\n**Child elements:**\n\n");
			for(ElementInfo child : step.getChildElements())
			{
				buff.append("- `").append(child.getNameWithHyphens()).append("`");
				if(child.isMandatory())
				{
					buff.append(" (mandatory)");
				}
				if(child.isMultiple())
				{
					buff.append(" (multiple)");
				}
				if(StringUtils.isNotBlank(child.getDescription()))
				{
					buff.append(" — ").append(child.getDescription());
				}
				buff.append("\n");
			}
		}

		if(step.hasExamples())
		{
			buff.append("\n**Example");
			if(step.getExamples().size() > 1)
			{
				buff.append("s");
			}
			buff.append(":**\n\n");

			for(Example example : step.getExamples())
			{
				if(StringUtils.isNotBlank(example.getDescription()))
				{
					buff.append("*").append(example.getDescription()).append("*\n\n");
				}
				buff.append("```xml\n").append(trimExampleContent(example.getContent())).append("\n```\n\n");
			}
		}

		buff.append("\n");
	}

	private static String generatePrefixExpressions(DocInformation docInformation)
	{
		StringBuilder buff = new StringBuilder();
		buff.append("# Prefix Expressions\n\n");
		buff.append("Prefix expressions parse and transform values. Chain with `|`.\n\n");
		buff.append("Includes parsers from `DefaultPrefixExpressions` and plugin-specific parsers ");
		buff.append("(SQL, Mongo, UI, etc.).\n\n");

		for(PrefixExpressionDoc parser : docInformation.getPrefixExpressions())
		{
			buff.append("### ").append(parser.getName()).append(":\n\n");
			buff.append("- **Description**: ").append(nullSafe(parser.getDescription())).append("\n");
			if(parser.getContentType() != null)
			{
				buff.append("- **Content type**: ").append(parser.getContentType()).append("\n");
			}

			if(CollectionUtils.isNotEmpty(parser.getParams()))
			{
				buff.append("\n**Parameters:**\n\n");
				for(PrefixExpressionDoc.Param param : parser.getParams())
				{
					buff.append("- `").append(param.getName()).append("`");
					if(param.isMandatory())
					{
						buff.append(" (mandatory)");
					}
					buff.append(" — ").append(nullSafe(param.getDescription()));
					if(StringUtils.isNotBlank(param.getDefaultValue()))
					{
						buff.append(" Default: `").append(param.getDefaultValue()).append("`");
					}
					buff.append("\n");
				}
			}

			if(CollectionUtils.isNotEmpty(parser.getExamples()))
			{
				buff.append("\n**Examples:**\n\n");
				for(Example example : parser.getExamples())
				{
					if(StringUtils.isNotBlank(example.getDescription()))
					{
						buff.append("*").append(example.getDescription()).append("*\n\n");
					}
					buff.append("```\n").append(trimExampleContent(example.getContent())).append("\n```\n\n");
				}
			}

			buff.append("\n");
		}

		return buff.toString();
	}

	private static String generateFreeMarkerMethods(DocInformation docInformation)
	{
		StringBuilder buff = new StringBuilder();
		buff.append("# FreeMarker Methods\n\n");
		buff.append("Methods available in `${...}` expressions.\n\n");

		Map<String, List<FreeMarkerMethodDocInfo>> grouped = new TreeMap<>();
		for(FreeMarkerMethodDocInfo method : docInformation.getFreeMarkerMethods())
		{
			String group = StringUtils.defaultIfBlank(method.getGroup(), "Other");
			grouped.computeIfAbsent(group, k -> new ArrayList<>()).add(method);
		}

		for(Map.Entry<String, List<FreeMarkerMethodDocInfo>> entry : grouped.entrySet())
		{
			buff.append("## ").append(entry.getKey()).append("\n\n");

			for(FreeMarkerMethodDocInfo method : entry.getValue())
			{
				buff.append("### ").append(method.getName()).append("()\n\n");
				buff.append("- **Description**: ").append(nullSafe(method.getDescription())).append("\n");
				if(StringUtils.isNotBlank(method.getReturnType()))
				{
					buff.append("- **Returns**: `").append(method.getReturnType()).append("`");
					if(StringUtils.isNotBlank(method.getReturnDescription()))
					{
						buff.append(" — ").append(method.getReturnDescription());
					}
					buff.append("\n");
				}

				if(CollectionUtils.isNotEmpty(method.getParameters()))
				{
					buff.append("\n**Parameters:**\n\n");
					for(ParamInfo param : method.getParameters())
					{
						appendParam(buff, param);
					}
				}

				if(method.hasExamples())
				{
					buff.append("\n**Examples:**\n\n");
					for(Example example : method.getExamples())
					{
						buff.append("- Usage: `").append(example.getDescription()).append("`");
						if(StringUtils.isNotBlank(example.getContent()))
						{
							buff.append(" → `").append(example.getContent()).append("`");
						}
						buff.append("\n");
					}
				}

				buff.append("\n");
			}
		}

		return buff.toString();
	}

	private static String generateUiLocators(DocInformation docInformation)
	{
		StringBuilder buff = new StringBuilder();
		buff.append("# UI Locators\n\n");
		buff.append("Locator format: `<type>:<value>`\n\n");

		for(UiLocatorDoc locator : docInformation.getUiLocators())
		{
			buff.append("### ").append(locator.getName()).append("\n\n");
			buff.append("- **Description**: ").append(nullSafe(locator.getDescription())).append("\n");
			buff.append("- **Example**: `").append(locator.getName()).append(": ...`\n");

			if(CollectionUtils.isNotEmpty(locator.getExamples()))
			{
				buff.append("\n**Examples:**\n\n");
				for(Example example : locator.getExamples())
				{
					buff.append("```\n").append(trimExampleContent(example.getContent())).append("\n```\n\n");
				}
			}

			buff.append("\n");
		}

		return buff.toString();
	}

	private static void appendParam(StringBuilder buff, ParamInfo param)
	{
		String xmlName = StringUtils.defaultIfBlank(param.getNameWithHyphens(), param.getName());
		buff.append("- `").append(xmlName).append("`");
		if(param.isMandatory())
		{
			buff.append(" (mandatory)");
		}
		buff.append(" — ").append(nullSafe(param.getDescription()));
		if(StringUtils.isNotBlank(param.getType()))
		{
			buff.append(" Type: `").append(param.getType()).append("`");
		}
		if(StringUtils.isNotBlank(param.getDefaultValue()))
		{
			buff.append(" Default: `").append(param.getDefaultValue()).append("`");
		}
		buff.append("\n");
	}

	private static String trimExampleContent(String content)
	{
		if(content == null)
		{
			return "";
		}

		return content.strip();
	}

	private static String nullSafe(String value)
	{
		return value == null ? "" : value;
	}
}
