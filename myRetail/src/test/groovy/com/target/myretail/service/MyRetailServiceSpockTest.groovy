package com.target.myretail.service

import com.target.myretail.model.Price
import com.target.myretail.model.Product
import com.target.myretail.model.productapi.Item
import com.target.myretail.model.productapi.OnlineDescription
import com.target.myretail.model.productapi.ProductCompositeResponse
import com.target.myretail.model.productapi.ProductResponse
import com.target.myretail.repositories.PriceRepository
import mockit.Expectations
import mockit.Mock
import mockit.MockUp
import mockit.Mocked
import mockit.integration.junit4.JMockit
import org.apache.log4j.Logger
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import spock.lang.*
/**
 * Created by jrtitko1 on 9/11/16.
 */
@RunWith(JMockit.class)
class MyRetailServiceSpockTest extends Specification {

    @Mocked
    RestTemplate mockRestTemplate;

    def productURL = 'https://dummyURL'

    def mockPriceRepository = Mock(PriceRepository)

    def mockLogger = Mock(Logger)

    def myRetailService = new MyRetailService(
            productURL: productURL,
            priceRepository: mockPriceRepository,
            log: mockLogger
    )

//    def mockRestTemplate = Mock(RestTemplate)

    @Test
    def "build product"() {
        given:
        def productId = 13860428
        def price = new Price(13860428, 19.99, "USD")
        def productResponse = buildProductResponse()
//        new MockUp<RestTemplate>() {
//            @Mock
//            public <T> T getForObject(String url, Class<T> responseType, Object... urlVariables) throws RestClientException {
//                System.out.println("##################");
//                System.out.println("#### MOCKED   ####");
//                System.out.println("##################");
//                return productResponse;
//            }
//        }

        when:
        Product product = myRetailService.getProduct(productId)

        then:
        1 * mockRestTemplate.getForObject(_, _) >> productResponse
        1 * mockPriceRepository.findOne(productId) >> price
        1 * mockLogger.info(_ as String)

        product != null
        product.getId() == Integer.valueOf(productId)
        product.getName() == 'Hewy Lewis & The News Greatest Hits'
        product.getCurrentPrice() != null
        product.getCurrentPrice().getId() == Integer.valueOf(productId)
        product.getCurrentPrice().getValue() == 19.99
        product.getCurrentPrice().getCurrencyCode() == 'USD'
    }

    private ProductResponse buildProductResponse() {
        OnlineDescription onlineDescription = new OnlineDescription();
        onlineDescription.setValue("Hewy Lewis & The News Greatest Hits");
        Item item = new Item();
        item.setOnlineDescription(onlineDescription);
        List<Item> items = Arrays.asList(item);
        ProductCompositeResponse productCompositeResponse = new ProductCompositeResponse();
        productCompositeResponse.setItems(items);
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductCompositeResponse(productCompositeResponse);
        return productResponse;
    }

    private ProductResponse buildProductResponseForInvalidProduct() {
        Item item = new Item();
        item.setOnlineDescription(null);
        List<Item> items = Arrays.asList(item);
        ProductCompositeResponse productCompositeResponse = new ProductCompositeResponse();
        productCompositeResponse.setItems(items);
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductCompositeResponse(productCompositeResponse);
        return productResponse;
    }

}
