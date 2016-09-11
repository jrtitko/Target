package com.target.myretail.controller

import com.target.myretail.MyRetailApplication
import com.target.myretail.model.Price
import com.target.myretail.repositories.PriceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.charset.Charset

import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue
import static org.junit.Assert.assertNotNull
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by jrtitko1 on 9/11/16.
 */

@SpringApplicationConfiguration(classes = MyRetailApplication.class)
class MyRetailControllerSpockTest extends Specification {

    @Autowired
    PriceRepository priceRepository;

    Price origPrice;

    def setup() {
        origPrice = priceRepository.findOne(13860428);
        priceRepository.save(new Price(13860428,  13.49, 'USD'));
    }

//    def cleanup() {
//        if (origPrice != null) {
//            priceRepository.save(origPrice);
//        } else {
//            priceRepository.delete(13860428);
//        }
//    }

    def "get product"() {

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
