package com.target.myretail.service;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.target.myretail.exception.InformationUnavailableException;
import com.target.myretail.model.Price;
import com.target.myretail.model.Product;
import com.target.myretail.model.productapi.ProductResponse;
import com.target.myretail.repositories.PriceRepository;

@Service
public class MyRetailService {

    static Logger log = Logger.getLogger(MyRetailService.class);

	@Value("${product.description.url}")
	private String productURL;

	@Resource
	private PriceRepository priceRepository;

	private RestTemplate restTemplate = new RestTemplate();

	public Product getProduct(Integer id) {

		ProductResponse response = getProductDescription(id);

		Product product = buildProduct(id, response);

		return product;
	}

    private Product buildProduct(Integer id, ProductResponse productResponse) {
		Product product = new Product();
		product.setId(id);
		try {
		    product.setName(productResponse.getItemOnlineDescription());
            product.setCurrentPrice(retrievePrice(id));
		} catch (InformationUnavailableException e) {
		    log.warn("ProductID: " + id + " => " + e.getMessage());
        }
        return product;
    }

    private Price retrievePrice(Integer id) throws InformationUnavailableException {
        Price price = priceRepository.findOne(id);
        if (price == null) {
            throw new InformationUnavailableException("Pricing is unavailable");
        }
        return price;
    }

    private ProductResponse getProductDescription(Integer id) {
		String url = String.format(productURL, id);
		ProductResponse response = null;
	    response = restTemplate.getForObject(url, ProductResponse.class);
	    log.info(response.toString());

	    return response;
    }

    public void savePrice(Price newPrice) {
        priceRepository.save(newPrice);
    }
}
