# FreeMarker Methods

Methods available in `${...}` expressions.

## Autox Common Methods

### compareAndGet()

- **Description**: Used to compare specified attribute with specified value and return appropiate result.
- **Returns**: `java.lang.String` — True or false value based on match.

**Parameters:**

- `name` — Name of the attribute to check Type: `java.lang.String`
- `value` — Expected value of the attribute Type: `java.lang.String`
- `trueVal` — Value to be returned when the attribute value match with specified value Type: `java.lang.String`
- `falseVal` — Value to be returned when the attribute value DOES NOT match with specified value Type: `java.lang.String`

### countOfXpath()

- **Description**: Fetches the count of values matching with specified xpath from specified source object.
- **Returns**: `int` — Number of values matching with specified xpath

**Parameters:**

- `source` — Source object on which xpath should be evaluated Type: `java.lang.Object`
- `xpath` — Xpath to be evaluated Type: `java.lang.String`

### escapeHtml()

- **Description**: Escapes special characters for specified html content.
- **Returns**: `java.lang.String` — Escape html content

**Parameters:**

- `content` — Html content which needs escaping Type: `java.lang.String`

### evalJsonExpr()

- **Description**: Evaluates json expressions in the input template and returns the result json. To input context, automation-context will be added with name 'context'.
- **Returns**: `java.lang.String` — Result of json expression evaluation. This will be json string.

**Parameters:**

- `template` — Json template to be evaluated. If non-string is specified, it will be converted to json first. Type: `java.lang.Object`
- `context` — Context to be used for evaluation Type: `java.util.Map`

### getValueByXpath()

- **Description**: Fetches the value for specified xpath from specified source object.
- **Returns**: `java.lang.Object` — Value of specified xpath

**Parameters:**

- `source` — Source object on which xpath needs to be evaluated Type: `java.lang.Object`
- `xpath` — Xpath to be evaluated Type: `java.lang.String`

### getValuesByXpath()

- **Description**: Fetches the value(s) list for specified xpath from specified source object.
- **Returns**: `java.util.List` — Value of specified xpath

**Parameters:**

- `source` — Source object on which xpath needs to be evaluated Type: `java.lang.Object`
- `xpath` — Xpath to be evaluated Type: `java.lang.String`

### normalizeXml()

- **Description**: Used for normalizing xml content, by removing trailing whitespaces between nodes, which in turn can be used during xml content comparison
- **Returns**: `java.lang.String` — Nomralized xml content (witout white spaces).

**Parameters:**

- `arg0` —  Type: `java.lang.String`

### parseJson()

- **Description**: Parses specified string as json and returns result.
- **Returns**: `java.lang.Object` — parsed json object

**Parameters:**

- `str` — String to parse Type: `java.lang.String`

### removeSpecialChars()

- **Description**: Removes special characters from input string. Also all whitespaces chars will be replaced with space.
- **Returns**: `java.lang.String` — String with special chars removed

**Parameters:**

- `str` — Input string Type: `java.lang.String`

### setAttr()

- **Description**: Used to set value as content attribute. This function will always return empty string.
- **Returns**: `java.lang.String` — Always empty string.

**Parameters:**

- `arg0` —  Type: `java.lang.String`
- `arg1` —  Type: `java.lang.Object`

### storeValue()

- **Description**: Fetches the value of specified key from the store.
- **Returns**: `java.lang.Object` — Matched value

**Parameters:**

- `key` — Key of value to be fetched Type: `java.lang.String`

### toReader()

- **Description**: Used to convert specified data into a reader. Supported parameter types - CharSequence, byte[].
- **Returns**: `java.io.Reader` — Converted input stream.

**Parameters:**

- `input` — Input that needs to be converted to reader Type: `java.lang.Object`

### toStream()

- **Description**: Used to convert specified data into a input stream. Supported parameter types - CharSequence, byte[].
- **Returns**: `java.io.InputStream` — Converted input stream.

**Parameters:**

- `input` — Input that needs to be converted to input stream Type: `java.lang.Object`

## Autox File Methods

### extensionOfFile()

- **Description**: Extracts extension from file name.
- **Returns**: `java.lang.String` — Extension of file name (without dot(.))

**Parameters:**

- `fileName` — File name from which extension has to be extracted Type: `java.lang.String`

### fileExists()

- **Description**: Checks if specified file path exists or not.
- **Returns**: `boolean` — Path to be checked

**Parameters:**

- `path` — Path to be checked Type: `java.lang.String`

### fileNameFromPath()

- **Description**: Extracts file name from path.
- **Returns**: `java.lang.String` — file name from path

**Parameters:**

- `path` — Path from which file name to be extracted Type: `java.lang.String`

### fileNameFromUrl()

- **Description**: Extracts file name from url.
- **Returns**: `java.lang.String` — file name from url

**Parameters:**

- `url` — URL from which file name to be extracted Type: `java.lang.String`

### fullPath()

- **Description**: Converts input file path (Can be relative, partial path) to full canonical path.
- **Returns**: `java.lang.String` — Canonical path of the specified path

**Parameters:**

- `path` — Path to be converted Type: `java.lang.String`

### isDirectory()

- **Description**: Checks if specified file path is a directory or not.
- **Returns**: `boolean` — Path to be checked

**Parameters:**

- `path` — Path to be checked Type: `java.lang.String`

### isFile()

- **Description**: Checks if specified file path is a file or not.
- **Returns**: `boolean` — Path to be checked

**Parameters:**

- `path` — Path to be checked Type: `java.lang.String`

### suffixOfFile()

- **Description**: Extracts suffix from file name.
- **Returns**: `java.lang.String` — Suffix of file name.

**Parameters:**

- `fileName` — File name from which suffix has to be extracted Type: `java.lang.String`

## Autox Mongo Methods

### mongoCount()

- **Description**: Fetches count from specified collection with specified conditions.
- **Returns**: `long` — Mongo document id as string.

**Parameters:**

- `mongoResourceName` — Name of the mongo resource Type: `java.lang.String`
- `collection` — Name of the collection to query Type: `java.lang.String`
- `conditions` — Name-value pairs to be used as conditions Type: `java.lang.Object[] (var-args)`

### mongoFetchDoc()

- **Description**: Fetches first doc from specified collection with specified conditions.
- **Returns**: `java.util.Map` — Mongo document id as string.

**Parameters:**

- `mongoResourceName` — Name of the mongo resource Type: `java.lang.String`
- `collection` — Name of the collection to query Type: `java.lang.String`
- `conditions` — Name-value pairs to be used as conditions Type: `java.lang.Object[] (var-args)`

### mongoFetchId()

- **Description**: Fetches first _id from specified collection with specified conditions.
- **Returns**: `java.lang.String` — Mongo document id as string.

**Parameters:**

- `mongoResourceName` — Name of the mongo resource Type: `java.lang.String`
- `collection` — Name of the collection to query Type: `java.lang.String`
- `conditions` — Name-value pairs to be used as conditions Type: `java.lang.Object[] (var-args)`

### mongoFetchIds()

- **Description**: Fetches all _id from specified collection with specified conditions.
- **Returns**: `java.util.List` — Mongo document id as string.

**Parameters:**

- `mongoResourceName` — Name of the mongo resource Type: `java.lang.String`
- `collection` — Name of the collection to query Type: `java.lang.String`
- `conditions` — Name-value pairs to be used as conditions Type: `java.lang.Object[] (var-args)`

### mongoObjectId()

- **Description**: Creates bson object-id object with specified id value.
- **Returns**: `org.bson.types.ObjectId` — Bson object id.

**Parameters:**

- `value` — Hex id Type: `java.lang.String`

## Collection Methods

### addToCol()

- **Description**: Adds specified values to the specified collection
- **Returns**: `java.util.Collection` — Input collection post modification

**Parameters:**

- `collection` — Collection to be modified Type: `java.util.Collection`
- `values` — Values to add Type: `java.lang.Object[] (var-args)`

### addToMap()

- **Description**: Adds specified key-values to the specified map
- **Returns**: `java.util.Map` — Input map post modification

**Parameters:**

- `map` — Map to be modified Type: `java.util.Map`
- `keyValues` — Key-value pairs to add Type: `java.lang.Object[] (var-args)`

### collectionToString()

- **Description**: Converts collection of objects into string.
- **Returns**: `java.lang.String` — Converted string

**Parameters:**

- `lst` — Collection to be converted Type: `java.util.Collection`
- `prefix` — Prefix to be used at start of coverted string. Type: `java.lang.String` Default: `empty string`
- `delimiter` — Delimiter to be used between the collection elements. Type: `java.lang.String` Default: `comma (,)`
- `suffix` — Suffix to be used at end of converted string. Type: `java.lang.String` Default: `empty string`
- `emptyString` — String to be used when input list is null or empty. Type: `java.lang.String` Default: `empty string`

**Examples:**

- Usage: `collectionToString(lst, '[', ' | ', ']', '')` → `[a | b | c]`
- Usage: `collectionToString(null, '[', ' | ', ']', '<empty>')` → `<empty>`

### contains()

- **Description**: Checks whether specified value is present in specified collection/map. If map, value will be searched as key. If collection is null or non-collection and non-map, then nullValue will be returned.
- **Returns**: `java.lang.Boolean` — True if value/key is present in collection/map.

**Parameters:**

- `collection` — Collection/map in which value/key has to be searched. Type: `java.lang.Object`
- `value` — Value/key to be searched Type: `java.lang.Object`
- `nullVal` — Flag to be returned when collection is null or non-suppported type Type: `java.lang.Boolean` Default: `false`

### emptyMap()

- **Description**: Creates an empty map and returns the same.
- **Returns**: `java.util.Map` — Empty map

### groupBy()

- **Description**: Groups elements of specified collection based on specified keyExpression
- **Returns**: `java.util.List` — List of groups. Each group has key (value of key based on which current group is created) and elements having same key.

**Parameters:**

- `collection` — Collection of objects which needs grouping Type: `java.util.Collection`
- `keyExpression` — Freemarker key expression which will be executed on each of collection element. And obtained key will be used for grouping. Type: `java.lang.String`

### intersectionCount()

- **Description**: Evaluates the intersection size of specified collections.
- **Returns**: `int` — intersection size of collections

**Parameters:**

- `collection1` — Collection one to be checked Type: `java.util.Collection`
- `collection2` — Collection two to be checked Type: `java.util.Collection`

### isSubmap()

- **Description**: Checks if specified submap is submap of supermap
- **Returns**: `boolean` — true if comparison is susccessful.

**Parameters:**

- `superMap` — Super-set map in which submap has to be checked Type: `java.util.Map`
- `superMap` — Sub-set map which needs to be checked Type: `java.util.Map`

### listOf()

- **Description**: Creates a list of objects from the specified values.
- **Returns**: `java.util.List` — List of objects.

**Parameters:**

- `values` — Values to be converted into list Type: `java.lang.Object[] (var-args)`

### lstToSet()

- **Description**: Converts specified list into set.
- **Returns**: `java.util.Set` — Converted set.

**Parameters:**

- `list` — List to be converted Type: `java.util.List`

### mapKeys()

- **Description**: Extracts and returns the keys collection as list of specified map.
- **Returns**: `java.util.Collection` — the values collection of specified map.

**Parameters:**

- `map` — Map whose keys has to be extracted Type: `java.util.Map`

### mapToString()

- **Description**: Converts map of objects into string.
- **Returns**: `java.lang.String` — Converted string

**Parameters:**

- `map` — Prefix to be used at start of coverted string Type: `java.util.Map`
- `template` — Template representing how key and value should be converted into string (the string can have #key and #value which will act as place holders). Type: `java.lang.String` Default: `#key=#value`
- `prefix` — Prefix to be used at start of coverted string. Type: `java.lang.String` Default: `empty string`
- `delimiter` — Delimiter to be used between elements. Type: `java.lang.String` Default: `comma (,)`
- `suffix` — Suffix to be used at end of string. Type: `java.lang.String` Default: `empty string`
- `emptyString` — String that will be returned if input map is null or empty. Type: `java.lang.String` Default: `empty string`

**Examples:**

- Usage: `mapToString(map, '#key=#value', '[', ' | ', ']', '')` → `[a=1 | b=2 | c=3]`
- Usage: `mapToString(null, '#key=#value', '[', ' | ', ']', '<empty>')` → `<empty>`

### mapValues()

- **Description**: Extracts and returns the values collection as list of specified map.
- **Returns**: `java.util.Collection` — the values collection of specified map.

**Parameters:**

- `map` — Map whose values has to be extracted Type: `java.util.Map`

### newList()

- **Description**: Creates a new list with specified values
- **Returns**: `java.util.List` — newly created list

**Parameters:**

- `values` — Initial values to be set Type: `java.lang.Object[] (var-args)`

### newMap()

- **Description**: Creates a new map with specified values
- **Returns**: `java.util.Map` — newly created map

**Parameters:**

- `keyValues` — Initial key-value pairs to be set Type: `java.lang.Object[] (var-args)`

### newSortedMap()

- **Description**: Creates a new sorted map with specified values
- **Returns**: `java.util.Map` — newly created map

**Parameters:**

- `keyValues` — Initial key-value pairs to be set Type: `java.lang.Object[] (var-args)`

### notContains()

- **Description**: Checks whether specified value is NOT present in specified collection/map. If map, value will be searched as key. If collection is null or non-collection and non-map, then nullValue will be returned.
- **Returns**: `java.lang.Boolean` — True if value/key is NOT present in collection/map.

**Parameters:**

- `collection` — Collection/map in which value/key has to be searched. Type: `java.lang.Object`
- `value` — Value/key to be searched Type: `java.lang.Object`
- `nullVal` — Flag to be returned when collection is null or non-suppported type Type: `java.lang.Boolean` Default: `true`

### pop()

- **Description**: Removes the element from the end. If list is empty null will be returned.
- **Returns**: `java.lang.Object` — Returns the element removed

**Parameters:**

- `list` — Collection to which value should be added Type: `java.util.List`

### push()

- **Description**: Adds to specified value to specified collection
- **Returns**: `java.lang.String` — empty string

**Parameters:**

- `collection` — Collection to which value should be added Type: `java.util.Collection`
- `value` — Value to add Type: `java.lang.Object`

### removeFromCol()

- **Description**: Removes specified values from the specified collection
- **Returns**: `java.util.Collection` — Input collection post modification

**Parameters:**

- `collection` — Collection to be modified Type: `java.util.Collection`
- `values` — Values to remove Type: `java.lang.Object[] (var-args)`

### removeFromMap()

- **Description**: Removes specified keys from the specified map
- **Returns**: `java.util.Map` — Input collection post modification

**Parameters:**

- `map` — Map to be modified Type: `java.util.Map`
- `keys` — Keys to remove Type: `java.lang.Object[] (var-args)`

### setOf()

- **Description**: Creates a set of objects from the specified values.
- **Returns**: `java.util.Set` — Set of objects.

**Parameters:**

- `values` — Values to be converted into set Type: `java.lang.Object[] (var-args)`

### sortBy()

- **Description**: Sorted elements of specified collection based on specified keyExpression. Duplicate elements (with same key) will be kept together (though internal order is not guaranteed).
- **Returns**: `java.util.List` — List of ordered elements based on specified key expression.

**Parameters:**

- `collection` — Collection of objects which needs sorting Type: `java.util.Collection`
- `keyExpression` — Freemarker key expression which will be executed on each of collection element. And obtained key will be used for sorting. Type: `java.lang.String`

### strToList()

- **Description**: Converts specified string into list by splitting it using specified delimiter.
- **Returns**: `java.util.List` — Converted list.

**Parameters:**

- `str` — String to be converted Type: `java.lang.String`
- `delim` — Delimiter to be used Type: `java.lang.String`

## Common Methods

### __fmarker_collect()

- **Description**: Collects the value on thread local which can be accessed later. Not meant for external usage.
- **Returns**: `java.lang.String` — Empty string

**Parameters:**

- `value` — Value to collect Type: `java.lang.Object`

### compare()

- **Description**: Compares the specified values and returns the comparison result as int.
- **Returns**: `int` — Comparison result.

**Parameters:**

- `value1` — Value1 to compare Type: `java.lang.Object`
- `value2` — Value2 to compare Type: `java.lang.Object`

### ifFalse()

- **Description**: Used to check if specified value is false and return approp value Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false. If null, the condition will be considered as false (hence returing falseValue)
- **Returns**: `java.lang.Object` — Specified true-condition-value or false-condition-value.

**Parameters:**

- `value` — Value to be checked for false. Can be boolean true or string 'true' Type: `java.lang.Object`
- `falseValue` — Value to be returned when value is false or null. Type: `java.lang.Object` Default: `true`
- `trueValue` — Value to be returned when value is true. Type: `java.lang.Object` Default: `false`

### ifNotNull()

- **Description**: If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.
- **Returns**: `java.lang.Object` — ifNull or ifNotNull based on nullCheck.

**Parameters:**

- `nullCheck` — object to be checked for null Type: `java.lang.Object`
- `ifNotNull` — object to be returned if not null. Type: `java.lang.Object` Default: `true (boolean)`
- `ifNull` — object to be returned if null. Type: `java.lang.Object` Default: `false (boolean)`

### ifNull()

- **Description**: If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.
- **Returns**: `java.lang.Object` — ifNull or ifNotNull based on nullCheck.

**Parameters:**

- `nullCheck` — object to be checked for null Type: `java.lang.Object`
- `ifNull` — object to be returned if null. Type: `java.lang.Object` Default: `true (boolean)`
- `ifNotNull` — object to be returned if not null Type: `java.lang.Object` Default: `false (boolean)`

### ifTrue()

- **Description**: Used to check if specified value is true and return approp value Can be boolean flag or string. If string, 'true' (case insensitive) will be considered as true otherwise false.
- **Returns**: `java.lang.Object` — Specified true-condition-value or false-condition-value.

**Parameters:**

- `value` — Value to be checked for true. Type: `java.lang.Object`
- `trueValue` — Value to be returned when value is true. Type: `java.lang.Object` Default: `true`
- `falseValue` — Value to be returned when value is false or null. Type: `java.lang.Object` Default: `false`

### initcap()

- **Description**: Makes first letter of every word into capital letter.
- **Returns**: `java.lang.String`

**Parameters:**

- `str` — String to convert Type: `java.lang.String`

### isEmpty()

- **Description**: Used to check if specified value is empty. For collection, map and string, along with null this will check for empty value.
- **Returns**: `boolean` — True if value is empty.

**Parameters:**

- `value` — Value to be checked for empty Type: `java.lang.Object`

### isEqual()

- **Description**: Checks if specified values are equal using Objects.equals() method. Two null values are considered equal.
- **Returns**: `boolean` — True if values are equal.

**Parameters:**

- `value1` — First value to be compared Type: `java.lang.Object`
- `value2` — Second value to be compared Type: `java.lang.Object`

### isNotEmpty()

- **Description**: Used to check if specified value is not empty. For collection, map and string, along with non-null this will check for non-empty value.
- **Returns**: `boolean` — True if value is empty.

**Parameters:**

- `value` — Value to be checked for empty Type: `java.lang.Object`

### isNotNull()

- **Description**: Used to check if specified value is not null.
- **Returns**: `boolean` — True if value is not null.

**Parameters:**

- `value` — Value to be checked for not null Type: `java.lang.Object`

### isNull()

- **Description**: Used to check if specified value is null.
- **Returns**: `boolean` — True if value is null.

**Parameters:**

- `value` — Value to be checked for null Type: `java.lang.Object`

### nullVal()

- **Description**: If 'nullCheck' is null, 'ifNull' will be returned otherwise 'nullCheck' will be returned.
- **Returns**: `java.lang.Object` — ifNull or nullCheck based on nullCheck is null or not.

**Parameters:**

- `nullCheck` — object to be checked for null Type: `java.lang.Object`
- `ifNull` — object to be returned if null Type: `java.lang.Object`

### nvl()

- **Description**: Used to check if specified value is null and return approp value when null and when non-null.
- **Returns**: `java.lang.Object` — Specified null-condition-value or non-null-condition-value.

**Parameters:**

- `value` — Value to be checked for empty Type: `java.lang.Object`
- `nullValue` — Value to be returned when value is null Type: `java.lang.Object`
- `nonNullValue` — Value to be returned when value is non-null Type: `java.lang.Object`

### replace()

- **Description**: Replaces specified substring with replacement in main string.
- **Returns**: `java.lang.String`

**Parameters:**

- `mainString` — String in which replacement should happen Type: `java.lang.String`
- `substring` — Substring to be replaced Type: `java.lang.String`
- `replacement` — Replacement string Type: `java.lang.String`

### sizeOf()

- **Description**: Used to fetch size of specified value. If string length of string is returned, if collection size of collection is returned, if null zero will be returned. Otherwise 1 will be returned.
- **Returns**: `int` — Size of specified object.

**Parameters:**

- `value` — Value whose size to be determined Type: `java.lang.Object`

### toBoolean()

- **Description**: Converts specified string value into boolean value.
- **Returns**: `java.lang.Boolean` — Converted boolean value.

**Parameters:**

- `str` — String value to be converted Type: `java.lang.String`

### toInt()

- **Description**: Converts specified string value into int value.
- **Returns**: `java.lang.Integer` — Converted int value.

**Parameters:**

- `str` — String value to be converted Type: `java.lang.String`

### toLong()

- **Description**: Converts specified string value into long value.
- **Returns**: `java.lang.Long` — Converted long value.

**Parameters:**

- `str` — String value to be converted Type: `java.lang.String`

### toText()

- **Description**: Used to convert specified object into string. toString() will be invoked on input object to convert
- **Returns**: `java.lang.String` — Converted string. If null, 'null' will be returned.

**Parameters:**

- `value` — Value to be converted into string. Type: `java.lang.Object`

## Date Methods

### addDays()

- **Description**: Adds specified number of days to specified date
- **Returns**: `java.util.Date` — Resultant date after addition of specified days

**Parameters:**

- `date` — Date to which days should be added Type: `java.util.Date`
- `days` — Days to be added. Type: `int`

### addHours()

- **Description**: Adds specified number of hours to specified date
- **Returns**: `java.util.Date` — Resultant date after addition of specified hours

**Parameters:**

- `date` — Date to which hours should be added Type: `java.util.Date`
- `hours` — Hours to be added. Type: `int`

### addMinutes()

- **Description**: Adds specified number of minutes to specified date
- **Returns**: `java.util.Date` — Resultant date after addition of specified minutes

**Parameters:**

- `date` — Date to which minutes should be added Type: `java.util.Date`
- `minutes` — Minutes to be added. Type: `int`

### addSeconds()

- **Description**: Adds specified number of seconds to specified date
- **Returns**: `java.util.Date` — Resultant date after addition of specified seconds

**Parameters:**

- `date` — Date to which seconds should be added Type: `java.util.Date`
- `seconds` — Seconds to be added. Type: `int`

### addYears()

- **Description**: Adds specified number of days to specified date
- **Returns**: `java.util.Date` — Resultant date after addition of specified years

**Parameters:**

- `date` — Date to which days should be added Type: `java.util.Date`
- `years` — Years to be added. Type: `int`

### dateToStr()

- **Description**: Converts specified date into string in specified format.
- **Returns**: `java.lang.String` — Fromated date string.

**Parameters:**

- `date` — Date to be converted Type: `java.util.Date`
- `format` — Date format to which date should be converted Type: `java.lang.String`

**Examples:**

- Usage: `dateToStr(date, 'MM/dd/yyy')` → `20/20/2018`

### now()

- **Description**: Returns the current date object
- **Returns**: `java.util.Date` — Current date and time

### parseDate()

- **Description**: Parses specified date string into date object using specified format.
- **Returns**: `java.util.Date` — Parsed date object.

**Parameters:**

- `dateStr` — Date string to be parsed Type: `java.lang.String`
- `format` — Date format to use Type: `java.lang.String`

### toMillis()

- **Description**: Converts specified date into millis.
- **Returns**: `java.lang.Long` — Millis value

**Parameters:**

- `date` — Date to be converted Type: `java.util.Date`

### today()

- **Description**: Returns the current date object
- **Returns**: `java.util.Date` — Current date

## Random Methods

### random()

- **Description**: Generates random int.
- **Returns**: `int` — Random number

### randomAlpha()

- **Description**: Generates random alpha string with specified prefix.
- **Returns**: `java.lang.String` — Random string

**Parameters:**

- `prefix` — Prefix that will added to generated random string Type: `java.lang.String`
- `length` — Expected length of resulting random string Type: `int`

### randomAlphaNumeric()

- **Description**: Generates random alpha-numeric string with specified prefix.
- **Returns**: `java.lang.String` — Random string

**Parameters:**

- `prefix` — Prefix that will added to generated random string Type: `java.lang.String`
- `length` — Expected length of resulting random string Type: `int`

### randomDouble()

- **Description**: Generates random double in given range.
- **Returns**: `double` — Random number

**Parameters:**

- `min` — Min value of the expected range Type: `double`
- `max` — Max value of the expected range Type: `double`

### randomFloat()

- **Description**: Generates random float in given range.
- **Returns**: `float` — Random number

**Parameters:**

- `min` — Min value of the expected range Type: `float`
- `max` — Max value of the expected range Type: `float`

### randomInt()

- **Description**: Generates random int in given range.
- **Returns**: `int` — Random number

**Parameters:**

- `min` — Min value of the expected range Type: `int`
- `max` — Max value of the expected range Type: `int`

### randomString()

- **Description**: Generates random string with specified prefix (based on timestamp).
- **Returns**: `java.lang.String` — Random string

**Parameters:**

- `prefix` — Prefix that will added to generated random string Type: `java.lang.String`

## Regex Methods

### regexMatches()

- **Description**: Checks wether specified content is matching with specified regex.
- **Returns**: `boolean` — True if specified content is matching with specified regex

**Parameters:**

- `content` — String which needs to be evaluated aganist regex Type: `java.lang.String`
- `regex` — Regex to be used Type: `java.lang.String`

### regexParse()

- **Description**: Using specified regex tries to find fist match in given content, from that extracts the groups in specified regex.
- **Returns**: `java.util.Map` — From the first match map using group name in regex as key and group value from the match as value

**Parameters:**

- `content` — String in which specified regex match needs to be found Type: `java.lang.String`
- `regex` — Regex with group names to be extracted from match Type: `java.lang.String`

**Examples:**

- Usage: `Extracing values using standard regex groups. In below example, an attribute with name "dataMap" will be set as a map with structure 
				{name=abc, age=12}` → `<s:set expression="dataMap">
					<value><![CDATA[
						expr: regexParse("some text NAME=abc AGE=12 end", 'NAME\\=(?<name>\w+)\\s+AGE\\=(?<age>\w+)')
					</value>
				]]></s:set>`
- Usage: `When using as part of attribute or direct text the < and > symbols should be escaped properly.` → `<s:set expression="jobIdMap" value="expr: regexParse(toText(attr.result.headers.JUGGLER_LOG_URL[0]), 'jobId\\=(?&lt;jobId&gt;.*)')"/>`

### regexParseAll()

- **Description**: Using specified regex finds all matches for given content, from that extracts the groups in specified regex.
- **Returns**: `java.util.List` — From all matches list of maps are returned. Map uses group name in regex as key and group value from the match as value

**Parameters:**

- `content` — String in which specified regex match needs to be found Type: `java.lang.String`
- `regex` — Regex with group names to be extracted from match Type: `java.lang.String`

### regexParseMatch()

- **Description**: Using specified regex tries to check if content is matched, from that extracts the groups in specified regex.
- **Returns**: `java.util.Map` — From the match map using group name in regex as key and group value from the match as value

**Parameters:**

- `content` — String in which specified regex match needs to be found Type: `java.lang.String`
- `regex` — Regex with group names to be extracted from match Type: `java.lang.String`

**Examples:**

- Usage: `Extracing values using standard regex groups. In below example, an attribute with name "dataMap" will be set as a map with structure 
				{name=abc, age=12}` → `<s:set expression="dataMap">
					<value><![CDATA[
						expr: regexParseMatch("NAME=abc AGE=12", 'NAME\\=(?<name>\w+)\\s+AGE\\=(?<age>\w+)')
					</value>
				]]></s:set>`
- Usage: `When using as part of attribute or direct text the < and > symbols should be escaped properly.` → `<s:set expression="jobIdMap" value="expr: regexParseMatch(toText(attr.result.headers.JUGGLER_LOG_URL[0]), 'jobId\\=(?&lt;jobId&gt;.*)')"/>`

### regexReplaceAll()

- **Description**: Replaces the matches of specified pattern with specified replacement string.
- **Returns**: `java.lang.String` — Resultant string post all replacements

**Parameters:**

- `content` — String in which matches has to be replaced Type: `java.lang.String`
- `regex` — Regex to be used Type: `java.lang.String`
- `replaceWith` — Replacement string Type: `java.lang.String`

## com.yukthitech.autox.plugin.sql.SqlFreeMarkerFunctions

### resBlob()

- **Description**: Used to load specified resource as blob object.
- **Returns**: `java.sql.Blob` — Loaded blob object.

**Parameters:**

- `res` — Resource that needs to be converted to blob Type: `java.lang.String`

### resClob()

- **Description**: Used to load specified resource as clob object.
- **Returns**: `java.sql.Clob` — Loaded blob object.

**Parameters:**

- `res` — Resource to be converted into clob Type: `java.lang.String`

### toBlob()

- **Description**: Used to convert specified data into a blob object. Supported parameter types - CharSequence, byte[], Serializable, InputStream.
- **Returns**: `java.sql.Blob` — Converted blob object.

**Parameters:**

- `input` — Input that needs to be converted to blob Type: `java.lang.Object`

### toClob()

- **Description**: Used to convert specified data into a clob. Supported parameter types - CharSequence, byte[], InputStream, Reader.
- **Returns**: `java.sql.Clob` — Converted input stream.

**Parameters:**

- `input` — Input that needs to be converted to clob Type: `java.lang.Object`

## com.yukthitech.autox.plugin.ui.common.UiFreeMarkerMethods

### escape()

- **Description**: Removes special characters and coverts result into json string (enclosed in double quotes)
- **Returns**: `java.lang.String` — Converted string

**Parameters:**

- `str` — String to be converted Type: `java.lang.String`

### uiBrowserPosition()

- **Description**: Fetches the position of the browser
- **Returns**: `org.openqa.selenium.Point` — Position of the browser

**Parameters:**

- `driverName` — Optional. Name of ui driver to use. Type: `java.lang.String`

### uiBrowserSize()

- **Description**: Fetches the size of the browser
- **Returns**: `org.openqa.selenium.Dimension` — Size of the browser

**Parameters:**

- `driverName` — Optional. Name of ui driver to use. Type: `java.lang.String`

### uiDisplayValue()

- **Description**: Fetches display value of specified locator. For select, option label will be fetched. If element is not Select, its ui value will be fetched.
- **Returns**: `java.lang.String` — Value of the ui element

**Parameters:**

- `locator` — Locator/Webelement of the ui element whose display value needs to be fetched. Type: `java.lang.Object`
- `parent` — Optional. Webelement or ui-locator or attr-name of parent web-element. Type: `java.lang.Object`
- `driverName` — Optional. Name of ui driver to use. Type: `java.lang.String`

### uiElemAttr()

- **Description**: Fetches attribute value of specified locator.
- **Returns**: `java.lang.String` — Value of the ui element attribute

**Parameters:**

- `attrName` — Name of the attribute whose value to be fetched. Type: `java.lang.String`
- `locator` — Locator/Webelement of the ui element whose attribute value needs to be fetched. Type: `java.lang.Object`
- `parent` — Optional. Webelement or ui-locator or attr-name of parent web-element. Type: `java.lang.Object`
- `driverName` — Optional. Name of ui driver to use. Type: `java.lang.String`

### uiGetElement()

- **Description**: Fetches first element of specified locator.
- **Returns**: `org.openqa.selenium.WebElement` — Matching web element

**Parameters:**

- `locator` — Locator of the ui element whose element needs to be fetched. Type: `java.lang.String`
- `parent` — Optional. Webelement or ui-locator or attr-name of parent web-element. Type: `java.lang.Object`
- `driverName` — Optional. Name of ui driver to use. Type: `java.lang.String`

### uiGetElements()

- **Description**: Fetches all elements matching specified locator.
- **Returns**: `java.util.List` — Matching web elements

**Parameters:**

- `locator` — Locator of the ui element whose element needs to be fetched. Type: `java.lang.String`
- `parent` — Optional. Webelement or ui-locator or attr-name of parent web-element. Type: `java.lang.Object`
- `driverName` — Optional. Name of ui driver to use. Type: `java.lang.String`

### uiInnerHtml()

- **Description**: Fetches inner-html of specified locator.
- **Returns**: `java.lang.String` — Value of the ui element

**Parameters:**

- `locator` — Locator/Webelement of the ui element whose element needs to be fetched. Type: `java.lang.Object`
- `parent` — Optional. Webelement or ui-locator or attr-name of parent web-element. Type: `java.lang.Object`
- `driverName` — Optional. Name of ui driver to use. Type: `java.lang.String`

### uiIsPresent()

- **Description**: Checks if specified element is present or not (need not be visible).
- **Returns**: `boolean` — True if element is available (need not be visible)

**Parameters:**

- `locator` — Locator/Webelement of the ui element whose attribute value needs to be fetched. Type: `java.lang.Object`
- `parent` — Optional. Webelement or ui-locator or attr-name of parent web-element. Type: `java.lang.Object`
- `driverName` — Optional. Name of ui driver to use. Type: `java.lang.String`

### uiIsVisible()

- **Description**: Checks if specified element is visible or not.
- **Returns**: `boolean` — True if element is visible

**Parameters:**

- `locator` — Locator/Webelement of the ui element whose attribute value needs to be fetched. Type: `java.lang.Object`
- `parent` — Optional. Webelement or ui-locator or attr-name of parent web-element. Type: `java.lang.Object`
- `driverName` — Optional. Name of ui driver to use. Type: `java.lang.String`

### uiValue()

- **Description**: Fetches value of specified locator. If element is text/textarea, its ui value will be fetched.
- **Returns**: `java.lang.String` — Value of the ui element

**Parameters:**

- `locator` — Locator/Webelement of the ui element whose element needs to be fetched. Type: `java.lang.Object`
- `parent` — Optional. Webelement or ui-locator or attr-name of parent web-element. Type: `java.lang.Object`
- `driverName` — Optional. Name of ui driver to use. Type: `java.lang.String`

## com.yukthitech.jexpr.JelFmarkerMethods

### toJson()

- **Description**: Used to convert specified object into json string.
- **Returns**: `java.lang.String` — Converted json string.

**Parameters:**

- `value` — Value to be converted into json string. Type: `java.lang.Object`

## com.yukthitech.utils.fmarker.met.StringMethods

### indexOf()

- **Description**: Finds the first index of specified substring in specified string.
- **Returns**: `int` — index of subbstring. If not found -1.

**Parameters:**

- `string` — String in which substring needs to be searched Type: `java.lang.String`
- `substr` — Substring that needs to be searched Type: `java.lang.String`

### intToStr()

- **Description**: Converts specified int value to string using specified radix.
- **Returns**: `java.lang.String` — Result substring.

**Parameters:**

- `value` — Int value to be converted Type: `int`
- `radix` — Radix to be used for conversion Type: `int`

### isEqualIgnoreCase()

- **Description**: Checks if specified values are equal ignoring case.
- **Returns**: `boolean` — True if values are equal ignoring case.

**Parameters:**

- `value1` — First value to be compared Type: `java.lang.String`
- `value2` — Second value to be compared Type: `java.lang.String`

### isEqualString()

- **Description**: Checks if specified values are equal post string conversion.
- **Returns**: `boolean` — True if values are equal.

**Parameters:**

- `value1` — First value to be compared Type: `java.lang.Object`
- `value2` — Second value to be compared Type: `java.lang.Object`

### lastIndexOf()

- **Description**: Finds the last index of specified substring in specified string.
- **Returns**: `int` — index of subbstring. If not found -1.

**Parameters:**

- `string` — String in which substring needs to be searched Type: `java.lang.String`
- `substr` — Substring that needs to be searched Type: `java.lang.String`

### lower()

- **Description**: Converts specified string to lower case.
- **Returns**: `java.lang.String` — Lower case string.

**Parameters:**

- `str` — String to be converted to lower case Type: `java.lang.String`

### split()

- **Description**: Splits the given string into list of strings using specified delimiter.
- **Returns**: `java.util.List` — List of string resulted from spliting.

**Parameters:**

- `string` — String to parse Type: `java.lang.String`
- `delimiter` — Delimiter to be used for spliting Type: `java.lang.String`

### strContains()

- **Description**: Checks if specified substring can be found in main string
- **Returns**: `boolean` — true, if substring can be found.

**Parameters:**

- `mainString` — Main string in which search has to be performed Type: `java.lang.String`
- `substr` — Substring to be searched Type: `java.lang.String`
- `ignoreCase` — Flag to indicate if case has to be ignored during search Type: `boolean` Default: `false`

### strToInt()

- **Description**: Converts specified string to int using specified radix.
- **Returns**: `int` — Result int value.

**Parameters:**

- `value` — String value to be converted Type: `java.lang.String`
- `radix` — Radix to be used for conversion Type: `int`

### strTrim()

- **Description**: Trims input string.
- **Returns**: `java.lang.String` — Trimmed value.

**Parameters:**

- `str` — String to be trimmed Type: `java.lang.String`

### substr()

- **Description**: Substring of speicifed string with specified range.
- **Returns**: `java.lang.String` — Result substring.

**Parameters:**

- `string` — String from which substring needs to be extracted Type: `java.lang.String`
- `start` — Start from which substring Type: `int`
- `string` — End index of substring. If negative value is specified, this will be not be used. Type: `int`

### upper()

- **Description**: Converts specified string to upper case.
- **Returns**: `java.lang.String` — Upper case string.

**Parameters:**

- `str` — String to be converted to upper case Type: `java.lang.String`

