package com.salesmanager.shop.store.api.v1.initialization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.salesmanager.shop.init.data.InitializeDefaultStoreData;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1")
public class StoreInitializationApi {

	@Autowired
	private InitializeDefaultStoreData InitializeDefaultStoreData;

	/**
	 * 
	 */
	@PostMapping(
			value = { "/private/initstore" } 
			)			
	public @ResponseBody String initializeStore(@RequestBody String id) {
		
		try {
			InitializeDefaultStoreData.initInitialData();
		}catch(Exception ex) {
			return "ERR";	
		}
		return "OK";
	}

}
