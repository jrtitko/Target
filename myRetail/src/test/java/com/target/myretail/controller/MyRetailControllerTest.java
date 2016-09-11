package com.target.myretail.controller;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import com.target.myretail.MyRetailApplication;
import com.target.myretail.model.Price;
import com.target.myretail.repositories.PriceRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MyRetailApplication.class)
@WebAppConfiguration
public class MyRetailControllerTest {

    MockMvc mockMvc;
    @SuppressWarnings("rawtypes")
    HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    WebApplicationContext webApplicationContext;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    MyRetailController myRetailController;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    private Price origPrice;

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();

        origPrice = priceRepository.findOne(13860428);
        priceRepository.save(new Price(13860428,  13.49, "USD"));
    }

    @After
    public void tearDown() {
        if (origPrice != null) {
            priceRepository.save(origPrice);
        } else {
            priceRepository.delete(13860428);
        }
    }

    @Test
    public void testGetProduct() throws Exception {
        mockMvc.perform(get("/myRetail/products/13860428"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.id", is(13860428)))
                .andExpect(jsonPath("$.name", is("The Big Lebowski (Blu-ray)")))
                .andExpect(jsonPath("$.current_price", notNullValue()))
                .andExpect(jsonPath("$.current_price.value", is(13.49)))
                .andExpect(jsonPath("$.current_price.currency_code", is("USD")))
                ;
    }

    @Test
    public void testGetProduct_ProductFromAPIDoesntExist() throws Exception {
        mockMvc.perform(get("/myRetail/products/99999999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.id", is(99999999)))
                .andExpect(jsonPath("$").value(not(hasKey("name"))))
                .andExpect(jsonPath("$").value(not(hasKey("current_price"))))
                ;
    }

    @Test
    public void testGetProduct_PriceDoesntExist() throws Exception {
        mockMvc.perform(get("/myRetail/products/16752456"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.id", is(16752456)))
                .andExpect(jsonPath("$.name", is("Lego&reg; Super Heroes The Tumbler 76023")))
                .andExpect(jsonPath("$").value(not(hasKey("current_price"))))
                ;
    }

    @Test
    public void testPostProduct() throws Exception {
        Integer productId = 13860428;

        Price newPrice = new Price(13860428, 99.99, "USD");
        mockMvc.perform(post("/myRetail/products/" + productId)
                        .content(json(newPrice))
                        .contentType(contentType))
                        .andExpect(status().isOk());

        Price afterPrice = priceRepository.findOne(productId);
        assertNotNull(afterPrice);
        assertEquals(99.99, afterPrice.getValue(), 2);
    }

    @Test
    public void testPostProduct_MismatchPriceId() throws Exception {
        Integer productId = 13860428;

        Price beforePrice = priceRepository.findOne(productId);

        try {
        Price newPrice = new Price(99999999, 99.99, "USD");
        mockMvc.perform(post("/myRetail/products/" + productId)
                        .content(json(newPrice))
                        .contentType(contentType))
                        .andExpect(status().is5xxServerError());
        } catch (Exception e) {
            assertTrue(e.getCause().getMessage().contains("A price update"));
        }

        Price afterPrice = priceRepository.findOne(productId);
        assertNotNull(afterPrice);
        assertEquals(beforePrice.getValue(), afterPrice.getValue(), 2);
    }

    @SuppressWarnings("unchecked")
    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
