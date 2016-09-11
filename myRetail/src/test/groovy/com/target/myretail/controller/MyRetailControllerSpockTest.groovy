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
@WebAppConfiguration
class MyRetailControllerSpockTest extends Specification {

    MockMvc mockMvc

    @SuppressWarnings("rawtypes")
    HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    WebApplicationContext webApplicationContext;

    MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    MyRetailController myRetailController;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

//        mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
//                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        mappingJackson2HttpMessageConverter = converters.find {
            it.class.isAssignableFrom(MappingJackson2HttpMessageConverter)
        }

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    Price origPrice;

    def setup() {
        mockMvc = webAppContextSetup(webApplicationContext).build()

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

//        mockMvc.perform(get('/myRetail/products/13860428'))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(contentType))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(jsonPath('$.id', is(13860428)))
//                .andExpect(jsonPath('$.name', is('The Big Lebowski (Blu-ray)')))
//                .andExpect(jsonPath('$.current_price', notNullValue()))
//                .andExpect(jsonPath('$.current_price.value', is(13.49)))
//                .andExpect(jsonPath('$.current_price.currency_code', is('USD')))
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
