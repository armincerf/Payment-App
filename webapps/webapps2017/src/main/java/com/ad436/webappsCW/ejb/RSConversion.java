/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ad436.webappsCW.ejb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Restful service which given a currency to convert from, one to convert to,
 * and a value returns a JSON file with the format "Currency code + converted
 * value"
 *
 * @author ad436
 */
@Singleton
@Path("/conversion")
public class RSConversion {

    String USD = "USD";
    String GBP = "GBP";
    String EUR = "EUR";
    private final HashMap<String, BigDecimal> currencies;

    /**
     * each currency pair is stored with a value obtained from google as of
     * 05/2017
     */
    public RSConversion() {
        currencies = new HashMap<>();
        currencies.put(USD + GBP, new BigDecimal(0.77));
        currencies.put(USD + EUR, new BigDecimal(0.92));

        currencies.put(GBP + USD, new BigDecimal(1.29));
        currencies.put(GBP + EUR, new BigDecimal(1.18));

        currencies.put(EUR + USD, new BigDecimal(1.09));
        currencies.put(EUR + GBP, new BigDecimal(0.84));

        currencies.put(USD + USD, new BigDecimal(1.00));
        currencies.put(GBP + GBP, new BigDecimal(1.00));
        currencies.put(EUR + EUR, new BigDecimal(1.00));
    }

    /**
     * This method produces a JSON object containing the calculated value and
     * the new currency
     *
     * @param currency1*******************************************************************************************************************
     */
    // The method Produces either a JSON or XML representation of an Employee object
    // builds and returns an HTTP response (200 ok along with object or 404 NOT FOUND if the employee does not exist)
    @GET
    @Path("/{currency1}/{currency2}/{amount_of_currency1}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getEmployee(@PathParam("currency1") String currency1, @PathParam("currency2") String currency2, @PathParam("amount_of_currency1") BigDecimal c1amount) {
        if (!(currency1.equals(USD) || currency1.equals(GBP) || currency1.equals(EUR) || currency2.equals(USD) || currency2.equals(GBP) || currency2.equals(EUR))) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(currency2 + (c1amount.multiply(currencies.get(currency1 + currency2)).setScale(2, RoundingMode.HALF_EVEN)), MediaType.APPLICATION_JSON).build();
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("Singleton Object for this RESTfull Web Service has been created!!");
    }

    @PreDestroy
    public void clean() {
        System.out.println("Singleton Object for this RESTfull Web Service has been cleaned!!");
    }
}
