package org.example.controller;

import java.util.List;

import org.example.exception.TechnicalException;
import org.example.model.Sale;
import org.example.service.SaleService;
import org.example.util.PersistenceUtil;
import  org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaleController extends AbstractController {

	private final Logger logger = LoggerFactory.getLogger(SaleController.class);

	@Autowired
	private SaleService saleService;

	@GetMapping(value = "/sales")
	public List<Sale> list(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "10") int count, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "id") String orderBy, @RequestParam(defaultValue = "ASC") String order) {
		
		Specification<Sale> specifUser = saleService.getSpecifications(q);

		return saleService.list(specifUser, count, page, orderBy, order);
		
	}
	
	@PostMapping(value = "/sales")
	@ResponseStatus(HttpStatus.CREATED)
	public Sale create(@RequestBody Sale s) {
		return saleService.create(s);
	}

	@GetMapping(value = "/sales/{saleId}")
	public Sale get(@PathVariable Long saleId) {
		return saleService.getById(saleId);
	}

	@PatchMapping(value = "/users/{saleId}")
	public Sale patch(@PathVariable Long saleId, @RequestBody Sale s) {
		Sale sale = saleService.getById(saleId);
		if (sale!=null) {
			PersistenceUtil.copyNonNullProperties(s, sale);
			return saleService.update(sale);
		}

		throw new TechnicalException("Sale is not persisted");
	}

	@PutMapping(value = "/users/{saleId}")
	public Sale update(@PathVariable Long saleId, @RequestBody Sale s) {

		Sale updated = saleService.update(s);
	
		if (updated!=null) {
			return updated;
		}

		throw new TechnicalException("Sale is not persisted");
	}

	@DeleteMapping(value = "/users/{saleId}")
	public void delete(@PathVariable Long saleId) {
		saleService.delete(saleId);
	}



	@Override
	public Logger getLogger() {
		return logger;
	}

}
