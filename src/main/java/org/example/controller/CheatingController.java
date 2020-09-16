package org.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.example.model.AccountManager;
import org.example.model.Sale;
import org.example.service.AccountManagerService;
import org.example.service.SaleService;
import  org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheatingController extends AbstractController {

	private final Logger logger = LoggerFactory.getLogger(CheatingController.class);

	@Autowired
	private SaleService saleService;
	@Autowired
	private AccountManagerService accountManagerService;

	@GetMapping(value = "/populateDb")
	public List<Sale> populate() {
		List<Sale> sales = new ArrayList<>();
		
		for(int i=0;i<10;i++) {
			AccountManager a = new AccountManager();
			a.setEmail("user"+i+"@mail.com");
			a.setFirstname("Sales"+i);
			a.setLastname("Lastname"+i);
			a.setSalary(i*10000);
			a.setCommissionPercentage(30+i);
			a = accountManagerService.create(a);
			for(int u=0;u<=i;u++) {
				Sale sale = new Sale();
				sale.setPrice(u*1000);
				sale.setAccountManager(a);
				sales.add(saleService.create(sale));
			}
		}
		
		return sales;
	}
	

	@Override
	public Logger getLogger() {
		return logger;
	}

}
