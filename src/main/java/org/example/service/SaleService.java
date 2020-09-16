package org.example.service;

import org.example.model.Sale;
import org.example.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SaleService extends AbstractService<Sale> {
	
	@Autowired
	private SaleRepository saleRepository;
	
	@Override
	protected SaleRepository getRepository() {
		return saleRepository;
	}
	
   
	

}
