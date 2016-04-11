package com.hpe.adm.nga.sdk;

import com.google.api.client.http.HttpRequestFactory;
import com.hpe.adm.nga.sdk.model.EntityModel;
import org.json.JSONException;

import java.util.Collection;

/**
 * This class hold the entities objects and serve all functunality concern to entities.
 * 
 * @author moris oz
 *
 */
public class EntityList {

	private EntityListService entityListService = null;
	
	
	/**
	 * Creates a new EntityList object 
	 * @param reqFactory - Http Request Factory
	 * @param strEntityListDomain - Domain Name 
	 */
	public EntityList(HttpRequestFactory reqFactory, String strEntityListDomain) {

		entityListService = new EntityListService(reqFactory,strEntityListDomain);
		
	}
	
	/**
	 * getter of an Entities object ( Entities object handle a unique entity model ) 
	 * @param entityId - entity id
	 * @return a new Entities object with specific id
	 */
	public EntityListService.Entities at(int entityId) {
		return entityListService.at(entityId);
	}
	
	/**
	 * getter of an Get object of EntityList ( EntityList object handle a collection of entity models  )
	 * @return a new Get object 
	 */
	public EntityListService.Get get() {
		
		return entityListService.get();
	}
	
	/**
	 * getter of an Update object of EntityList ( EntityList object handle a collection of entity models  )
	 * @return a new Update object 
	 */
	public EntityListService.Update update() {
		
		return entityListService.update();
	}
	
	/**
	 * getter of an Create object of EntityList ( EntityList object handle a collection of entity models
	 * @return a new Create object 
	 */
	public EntityListService.Create create() {

		return entityListService.create();
	}
	/**
	 * getter of an Delete object of EntityList ( EntityList object handle a collection of entity models
	 * @return a new Delete object 
	 */
	public EntityListService.Delete delete() {
		return entityListService.delete();
	}

	/**
	 * TBD - Remove after testing
	 * 
	 * @throws JSONException
	 */
	public Collection<EntityModel> testGetEntityModels(String jason) throws JSONException {

		return entityListService.testGetEntityModels(jason);
	}
	
}