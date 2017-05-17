package com.citibanamex.api.cards.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import javax.xml.datatype.DatatypeConfigurationException;

import com.citibanamex.api.cards.model.cardlistresp.CardResponse;
import com.citibanamex.api.cards.model.exception.CardException;
import com.citibanamex.api.cards.model.exception.CustomException;
import com.citibanamex.api.cards.model.exception.ExceptionResponse;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.citibanamex.api.cards.handler.exception.GlobalExceptionHandler;
import com.citibanamex.api.cards.hateoas.Response;
import com.citibanamex.api.cards.service.CardService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Asit Samantray
 * 
 * 
 *         This service is returns list of credit card based on customer number
 *         or relationship number associated with a
 *         customer. 
 *         @PathVariable("customerId") - customer number is passed as request path variable. 
 *         @RequestHeader("client_id") - ClientId is passed as request header variable. 
 *         @RequestHeader("Authorization") - Authorization is passed
 *         as request header variable.
 *         @RequestHeader("userId") customer user id		  
 * 			@RequestHeader("channelIndicator")
 * 			@RequestHeader("relationshipNumber") customer relationship number
 *  	    @RequestHeader("org") / organization details* 
 * 
 * 
 * 
 */

@RestController
public class CardsController {

	private static final Logger logger = LoggerFactory.getLogger(CardsController.class);

	String data = null;
	Map<String, String> requestData = null;
	GlobalExceptionHandler globalHandler = null;
	CustomException ce = null;
	CardResponse creditCards = null;

	@Autowired
	CardService cardService;

	@RequestMapping(value = "/creditCards/customerAccounts/customer/{customerId}/accounts", method = RequestMethod.GET)
	@ApiOperation(value = "getCardsByCustomer", nickname = "Get the list of cards by customer account")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Failure") })
	public ResponseEntity <CardResponse> getCardsByCustomer(@PathVariable("customerId") String cusId,
			@RequestHeader("client_id") String clientId, @RequestHeader("Authorization") String auth,
			@RequestHeader("uuid") String uuid,@RequestHeader("terminalId") String termId,@RequestHeader("userId") String user,
			@RequestHeader("channelIndicator") String channelInd,@RequestHeader("relationshipNumber") String relationshipNbr,
			@RequestHeader("org") String organization) throws Exception {		
		HttpHeaders headers = new HttpHeaders();
		headers.set("client_id", clientId);
		headers.set("Authorization", auth);
		headers.set("uuid", uuid);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		requestData = new HashMap<String, String>();
		if(((termId != "" && termId != null)
				&& (user != "" && user != null) && (organization != "" && organization != null) 
				&& (relationshipNbr != "" && relationshipNbr != null)
				&& (channelInd != "" && channelInd != null))){
		requestData.put("terminalId", termId);
		requestData.put("userId", user);
		requestData.put("channelIndicator", channelInd);
		requestData.put("relationshipNumber", relationshipNbr);
		requestData.put("org", organization);
		}else{
			globalHandler = new GlobalExceptionHandler();
			ce = new CustomException("The parameter must not be null or empty");
			globalHandler.specialException(ce);			
		}	
		
		/*if(cusId != null){			
			requestData.put("releationshipNbr", cusId);
		}else{
			throw new RuntimeException("Customer relationship number should not be null");
		}	*/	
		
		creditCards = new CardResponse();

		try {
			creditCards = cardService.getCardsByCustomer(cusId, headers, requestData);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}		
		return new ResponseEntity<CardResponse>(creditCards,HttpStatus.OK);
	}

}
