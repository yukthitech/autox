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
package com.yukthitech.autox.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.test.mongo.MongoPlugin;
import com.yukthitech.autox.test.mongo.MongoPluginSession;
import com.yukthitech.autox.test.mongo.MongoResource;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Mongo related free marker methods.
 * @author akranthikiran
 */
public class MongoFreeMarkerMethods
{
	@FreeMarkerMethod(
			description = "Creates bson object-id object with specified id value.",
			returnDescription = "Bson object id."
			)
	public static ObjectId mongoObjectId(
			@FmParam(name = "value", description = "Hex id") String value
			)
	{
		return new ObjectId(value);
	}
	
	private static FindIterable<Document> findDocuments(String mongoResourceName, String collection, Object... conditions)
	{
		MongoPluginSession mongoPluginSession = ExecutionContextManager.getInstance().getPluginSession(MongoPlugin.class);
		MongoResource mongoResource = mongoPluginSession.getMongoResource(mongoResourceName);

		if(mongoResource == null)
		{
			throw new InvalidStateException("No Mongo resource found with specified name - {}", mongoResourceName);
		}
		
		Map<String, Object> queryMap = CommonUtils.toMap(conditions);
		
		MongoClient client = mongoResource.getMongoClient();
		MongoDatabase mongoDatabase = client.getDatabase(mongoResource.getDbName());
		
		FindIterable<Document> docs = mongoDatabase.getCollection(collection)
				.find(new Document(queryMap));
		
		return docs;
	}

	@FreeMarkerMethod(
			description = "Fetches first _id from specified collection with specified conditions.",
			returnDescription = "Mongo document id as string."
			)
	public static String mongoFetchId(
			@FmParam(name = "mongoResourceName", description = "Name of the mongo resource") String mongoResourceName,
			@FmParam(name = "collection", description = "Name of the collection to query") String collection,
			@FmParam(name = "conditions", description = "Name-value pairs to be used as conditions") Object... conditions
			)
	{
		FindIterable<Document> docs = findDocuments(mongoResourceName, collection, conditions); 
				
		Document doc = docs
				.projection(new Document("_id", 1))
				.first();

		if(doc == null)
		{
			return null;
		}
		
		Object id = doc.get("_id");
		return id.toString();
	}

	@FreeMarkerMethod(
			description = "Fetches all _id from specified collection with specified conditions.",
			returnDescription = "Mongo document id as string."
			)
	public static List<String> mongoFetchIds(
			@FmParam(name = "mongoResourceName", description = "Name of the mongo resource") String mongoResourceName,
			@FmParam(name = "collection", description = "Name of the collection to query") String collection,
			@FmParam(name = "conditions", description = "Name-value pairs to be used as conditions") Object... conditions
			)
	{
		FindIterable<Document> docs = findDocuments(mongoResourceName, collection, conditions); 
		docs = docs.projection(new Document("_id", 1));
		
		List<String> idLst = new ArrayList<String>();

		for(Document doc : docs)
		{
			idLst.add(doc.get("_id").toString());
		}
		
		return idLst;
	}

	@FreeMarkerMethod(
			description = "Fetches first doc from specified collection with specified conditions.",
			returnDescription = "Mongo document id as string."
			)
	public static Map<String, Object> mongoFetchDoc(
			@FmParam(name = "mongoResourceName", description = "Name of the mongo resource") String mongoResourceName,
			@FmParam(name = "collection", description = "Name of the collection to query") String collection,
			@FmParam(name = "conditions", description = "Name-value pairs to be used as conditions") Object... conditions
			)
	{
		FindIterable<Document> docs = findDocuments(mongoResourceName, collection, conditions); 
				
		Document doc = docs.first();

		if(doc == null)
		{
			return null;
		}
		
		return doc;
	}

	@FreeMarkerMethod(
			description = "Fetches count from specified collection with specified conditions.",
			returnDescription = "Mongo document id as string."
			)
	public static long mongoCount(
			@FmParam(name = "mongoResourceName", description = "Name of the mongo resource") String mongoResourceName,
			@FmParam(name = "collection", description = "Name of the collection to query") String collection,
			@FmParam(name = "conditions", description = "Name-value pairs to be used as conditions") Object... conditions
			)
	{
		MongoPluginSession mongoPluginSession = ExecutionContextManager.getInstance().getPluginSession(MongoPlugin.class);
		MongoResource mongoResource = mongoPluginSession.getMongoResource(mongoResourceName);

		if(mongoResource == null)
		{
			throw new InvalidStateException("No Mongo resource found with specified name - {}", mongoResourceName);
		}
		
		Map<String, Object> queryMap = CommonUtils.toMap(conditions);
		
		MongoClient client = mongoResource.getMongoClient();
		MongoDatabase mongoDatabase = client.getDatabase(mongoResource.getDbName());
		
		return mongoDatabase.getCollection(collection).countDocuments(new Document(queryMap));
	}
}
