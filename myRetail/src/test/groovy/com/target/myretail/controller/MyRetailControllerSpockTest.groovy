package com.target.myretail.controller

import com.target.myretail.MyRetailApplication
import com.target.myretail.model.Price
import com.target.myretail.repositories.PriceRepository
import com.target.myretail.service.MyRetailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

import java.nio.charset.Charset

import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.context.web.WebAppConfiguration

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
/**
 * Created by jrtitko1 on 9/11/16.
 */

@SpringApplicationConfiguration(classes = MyRetailApplication.class)
//@WebIntegrationTest
@WebAppConfiguration
class MyRetailControllerSpockTest extends Specification {

    @Autowired
    PriceRepository priceRepository;

    @Autowired
    WebApplicationContext webApplicationContext;

//    MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
//            MediaType.APPLICATION_JSON.getSubtype(),
//            Charset.forName("utf8"));


//    @Autowired
//    def myRetailService = new MyRetailService()

//    def myRetailController = new MyRetailController(myRetailService: myRetailService)
//    def myRetailController = new MyRetailController()

    @Autowired
    MyRetailController myRetailController;

    def mockMvc

    Price origPrice;

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(myRetailController).build()
//        webAppContextSetup(webApplicationContext).build();

        origPrice = priceRepository.findOne(13860428);
        priceRepository.save(new Price(13860428,  13.49, 'USD'));
    }

    def cleanup() {
        if (origPrice != null) {
            priceRepository.save(origPrice);
        } else {
            priceRepository.delete(13860428);
        }
    }

    def "get product"() {
        expect:
        def response = mockMvc.perform(get("/myRetail/products/13860428"))

//        then:
//        1 * myRetailService.getProduct(13860428)

        response
            .andExpect(status().isOk())
//            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andDo(MockMvcResultHandlers.print())
//            .andExpect(jsonPath('$.id', is(13860428)))
//            .andExpect(jsonPath('$.name', is('The Big Lebowski (Blu-ray)')))
//            .andExpect(jsonPath('$.current_price', notNullValue()))
//            .andExpect(jsonPath('$.current_price.value', is(13.49)))
//            .andExpect(jsonPath('$.current_price.currency_code', is('USD')))
    }

    @Unroll
    def "minimum of #a and #b is #c"() {
        expect:
        Math.min(a, b) == c

        where:
        a | b || c
        3 | 7 || 3
        5 | 4 || 4
        9 | 9 || 9
    }

}
