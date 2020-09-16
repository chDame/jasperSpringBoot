package org.example.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.exception.TechnicalException;
import org.example.model.AccountManager;
import org.example.model.Sale;
import org.example.model.trans.SalesResult;
import org.example.service.AccountManagerService;
import org.example.service.SaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@RestController
public class JasperReportController extends AbstractController {

	private final Logger logger = LoggerFactory.getLogger(JasperReportController.class);

	@Autowired
	private SaleService saleService;
	@Autowired
	private AccountManagerService accountManagerService;

	private Map<String, JasperReport> reportMap = new HashMap<>();
	
	private JasperReport getReport(String template) throws JRException, IOException {
		if (reportMap.containsKey(template)) {
			return reportMap.get(template);
		} 
		InputStream reportStream = getClass().getResourceAsStream(template);
		JasperReport report = JasperCompileManager.compileReport(reportStream);
		reportStream.close();
		reportMap.put(template, report);
		return report;
	}
	
	@GetMapping(value = "/reports/accountManagers", produces = "application/octet-stream")
	@ResponseBody
	public ResponseEntity<InputStreamResource> getAccountMgrPdf() {
		try {
			JasperReport report = getReport("/jasper/accountManager.jrxml");

			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(accountManagerService.list("", 100, 1, "id", "ASC"));

			final Map<String, Object> parameters = new HashMap<>();
			parameters.put("createdBy", "Christophe");

			final JasperPrint print = JasperFillManager.fillReport(report, parameters, source);

			final String filePath = System.getProperty("java.io.tmpdir");
			
			JasperExportManager.exportReportToPdfFile(print, filePath + "accountMgr.pdf");

			InputStream is = new FileInputStream(filePath + "accountMgr.pdf");

			return ResponseEntity.ok()
					.header("content-disposition", "inline; filename=\"accountMgr.pdf\"")
					.contentLength(is.available())
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(new InputStreamResource(is));
		} catch (IOException | JRException e) {
			throw new TechnicalException("File could not be generated", e);
		}
	}
	
	@GetMapping(value = "/reports/salesResult", produces = "application/octet-stream")
	@ResponseBody
	public ResponseEntity<InputStreamResource> getSalesResult() {
		try {

			JasperReport report = getReport("/jasper/sales.jrxml");
			
			// Adding the additional parameters to the pdf.
			final Map<String, Object> parameters = new HashMap<>();
			parameters.put("createdBy", "Christophe");

			Map<Long, SalesResult> results = new HashMap<>();
			List<Sale> sales = saleService.list("", 1000, 1, "id", "ASC");
			for(Sale sale : sales) {
				AccountManager mgr = sale.getAccountManager();
				SalesResult sr = results.get(mgr.getId());
				if (sr==null) {
					sr = new SalesResult();
					sr.setSalesName(mgr.getFirstname()+" "+mgr.getLastname());
					sr.setSalesAmount(0);
					results.put(mgr.getId(), sr);
				}
				sr.setSalesAmount(sr.getSalesAmount()+sale.getPrice());
				sr.setCommissions(sr.getSalesAmount()*mgr.getCommissionPercentage()/100.0);
			}
			final JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(results.values());

			// Filling the report with the employee data and additional parameters information.
			final JasperPrint print = JasperFillManager.fillReport(report, parameters, source);

			// Users can change as per their project requrirements or can take it as request input requirement.
			// For simplicity, this tutorial will automatically place the file under the "c:" drive.
			// If users want to download the pdf file on the browser, then they need to use the "Content-Disposition" technique.
			final String filePath = System.getProperty("java.io.tmpdir");
			// Export the report to a PDF file.
			JasperExportManager.exportReportToPdfFile(print, filePath + "sales.pdf");



			InputStream is = new FileInputStream(filePath + "sales.pdf");

			return ResponseEntity.ok()
					.header("content-disposition", "inline; filename=\"sales.pdf\"")
					.contentLength(is.available())
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(new InputStreamResource(is));
		} catch (IOException | JRException e) {
			throw new TechnicalException("File could not be generated", e);
		}
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
