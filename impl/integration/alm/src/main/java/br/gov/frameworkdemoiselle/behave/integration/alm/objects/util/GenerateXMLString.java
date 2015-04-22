/*
 * Demoiselle Framework
 * Copyright (C) 2013 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 * 
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 * 
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 * 
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package br.gov.frameworkdemoiselle.behave.integration.alm.objects.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import br.gov.frameworkdemoiselle.behave.config.BehaveConfig;
import br.gov.frameworkdemoiselle.behave.integration.alm.objects.ApprovalState;
import br.gov.frameworkdemoiselle.behave.integration.alm.objects.Executionresult;
import br.gov.frameworkdemoiselle.behave.integration.alm.objects.Executionworkitem;
import br.gov.frameworkdemoiselle.behave.integration.alm.objects.ExecutionworkitemLink;
import br.gov.frameworkdemoiselle.behave.integration.alm.objects.Priority;
import br.gov.frameworkdemoiselle.behave.integration.alm.objects.Testcase;
import br.gov.frameworkdemoiselle.behave.integration.alm.objects.TestcaseCategory;
import br.gov.frameworkdemoiselle.behave.integration.alm.objects.TestcaseLink;
import br.gov.frameworkdemoiselle.behave.integration.alm.objects.Testcasedesign;
import br.gov.frameworkdemoiselle.behave.integration.alm.objects.Testplan;
import br.gov.frameworkdemoiselle.behave.integration.alm.objects.TestplanLink;
import br.gov.frameworkdemoiselle.behave.internal.integration.ScenarioState;

public class GenerateXMLString {

	public static String getTestPlanString(String urlServer, String projectAreaAlias, String encoding, String testCaseId, Testplan oldPlan) throws JAXBException {

		// Adiciona o novo test case se não existir
		boolean exists = false;
		String newTestCaseId = urlServer + "resources/" + projectAreaAlias + "/testcase/" + testCaseId;

		if (oldPlan.getTestcase() != null) {
			for (TestcaseLink link : oldPlan.getTestcase()) {
				if (link.getHref().equals(newTestCaseId)) {
					exists = true;
					break;
				}
			}
		} else {
			oldPlan.setTestcase(new ArrayList<TestcaseLink>());
		}

		if (!exists) {
			TestcaseLink testcase = new TestcaseLink();
			testcase.setHref(newTestCaseId);

			oldPlan.getTestcase().add(testcase);
		}

		Testplan plan = new Testplan();
		plan.setTestcase(oldPlan.getTestcase());

		// Adiciona as categorias
		plan.setCategory(oldPlan.getCategory());

		// Adiciona os aprovadores
		plan.setApprovals(oldPlan.getApprovals());

		JAXBContext jaxb = JAXBContext.newInstance(Testplan.class);
		Marshaller marshaller = jaxb.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
		StringWriter testPlanString = new StringWriter();
		marshaller.marshal(plan, testPlanString);

		return testPlanString.toString();
	}

	public static String getTestcaseString(String urlServer, String projectAreaAlias, String encoding, String name, String steps, Testcase oldTestCase) throws JAXBException {
		Priority priority = new Priority();
		priority.setResource(urlServer + "process-info/_EX3W1K3iEeKZTtTZfLxNXw/priority/literal.priority.101");
		priority.setValue("literal.priority.101");

		Testcasedesign design = new Testcasedesign();
		design.setExtensionDisplayName("RQM-KEY-TC-DESIGN-TITLE");
		design.setValue(escapeHTMLForAlm(steps));

		Testcase testcase = new Testcase();
		testcase.setTitle(name);
		testcase.setPriority(priority);
		testcase.setSuspect(false);
		testcase.setWeight(100);
		testcase.setTestCaseDesign(design);

		// Valor da Categoria
		String categoryTipoExecucao =  BehaveConfig.getIntegration_CategoryTipoExecucao();
		
		// Verifica se no caso de teste vindo da ALM existe a caregoria
		// "Tipo de Execução", se não existe cria.
		boolean objExists = false;
		for (TestcaseCategory c : oldTestCase.getCategory()) {
			if (c.getTerm().toLowerCase().trim().equals("tipo de execução")) {
				objExists = true;				
				// Altera para "Automática"
				c.setValue(categoryTipoExecucao);				
				break;
			}		
		}
		
		if (!objExists) {
			TestcaseCategory newC = new TestcaseCategory();
			newC.setTerm("Tipo de Execução");
			newC.setValue(categoryTipoExecucao);
			oldTestCase.getCategory().add(newC);
		}

		// Adiciona as categorias
		testcase.setCategory(oldTestCase.getCategory());

		JAXBContext jaxb = JAXBContext.newInstance(Testcase.class);
		Marshaller marshaller = jaxb.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
		StringWriter testCaseString = new StringWriter();
		marshaller.marshal(testcase, testCaseString);

		return testCaseString.toString();
	}

	public static Testcase getTestCaseObject(HttpResponse response) throws IOException, JAXBException {

		Testcase testcase = null;
		StringBuffer xmlString = new StringBuffer();
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
				String line = "";
				while ((line = reader.readLine()) != null) {
					xmlString.append(line);
				}
			} finally {
				instream.close();
			}
		}

		if (!xmlString.equals("")) {
			JAXBContext jaxbContext = JAXBContext.newInstance(Testcase.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xmlString.toString());
			testcase = (Testcase) unmarshaller.unmarshal(reader);
		}

		return testcase;

	}

	public static String getExecutionworkitemString(String urlServer, String projectAreaAlias, String encoding, String testCaseId, String testPlanId) throws JAXBException {
		Priority priority = new Priority();
		priority.setResource(urlServer + "/process-info/_EX3W1K3iEeKZTtTZfLxNXw/priority/literal.priority.101");
		priority.setValue("literal.priority.101");

		TestcaseLink workTest = new TestcaseLink();
		workTest.setHref(urlServer + "resources/" + projectAreaAlias + "/testcase/" + testCaseId);

		TestplanLink testPlan = new TestplanLink();
		testPlan.setHref(urlServer + "resources/" + projectAreaAlias + "/testplan/urn:com.ibm.rqm:testplan:" + testPlanId);

		Executionworkitem work = new Executionworkitem();
		work.setFrequency("Once");
		work.setPriority(priority);
		work.setRegression(false);
		work.setTitle("Registro de Execução Automatizado - Plano de Teste " + testPlanId);
		work.setWeight(100);
		work.setTestcase(workTest);
		work.setTestplan(testPlan);

		JAXBContext jaxb = JAXBContext.newInstance(Executionworkitem.class);
		Marshaller marshaller = jaxb.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
		StringWriter resourceString = new StringWriter();
		marshaller.marshal(work, resourceString);

		return resourceString.toString();
	}

	public static String getExecutionresultString(String urlServer, String projectAreaAlias, String encoding, String executionWorkItemUrl, ScenarioState stateOf, Date _startDate, Date _endDate, String details) throws JAXBException {
		Date startDate = (Date) _startDate.clone();
		Date endDate = (Date) _endDate.clone();
		ApprovalState state = new ApprovalState();
		state.setResource(urlServer + "/process-info/_EX3W1K3iEeKZTtTZfLxNXw/workflowstate/com.ibm.rqm.process.testcaseresult.workflow/com.ibm.rqm.planning.common.new");
		state.setValue("com.ibm.rqm.planning.common.new");

		ExecutionworkitemLink workTest = new ExecutionworkitemLink();
		workTest.setHref(executionWorkItemUrl);

		Executionresult result = new Executionresult();
		if (stateOf.equals(ScenarioState.FAILED)) {
			result.setState("com.ibm.rqm.execution.common.state.failed");
		} else {
			if (stateOf.equals(ScenarioState.PENDING)) {
				result.setState("com.ibm.rqm.execution.common.state.blocked");
			} else {
				result.setState("com.ibm.rqm.execution.common.state.passed");
			}
		}
		result.setApprovalstate(state);
		result.setExecutionworkitem(workTest);

		// Adiciona 3 horas (3 * 60 * 60 * 1000)
		startDate.setTime(startDate.getTime() + 10800000L);
		endDate.setTime(endDate.getTime() + 10800000L);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

		result.setStarttime(format.format(startDate));
		result.setEndtime(format.format(endDate));
		result.setDetails(details);

		JAXBContext jaxb = JAXBContext.newInstance(Executionresult.class);
		Marshaller marshaller = jaxb.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
		StringWriter resourceString = new StringWriter();
		marshaller.marshal(result, resourceString);

		return resourceString.toString();
	}

	public static Testplan getTestPlanObject(HttpResponse response) throws IOException, JAXBException {

		Testplan plan = null;
		StringBuffer xmlString = new StringBuffer();
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
				String line = "";
				while ((line = reader.readLine()) != null) {
					xmlString.append(line);
				}
			} finally {
				instream.close();
			}
		}

		if (!xmlString.equals("")) {
			JAXBContext jaxbContext = JAXBContext.newInstance(Testplan.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xmlString.toString());
			plan = (Testplan) unmarshaller.unmarshal(reader);
		}

		return plan;

	}

	/**
	 * Trata todas as tags para serem enviadas para a ALM, exceto a quebra de
	 * linha <br/>
	 * 
	 * @param s
	 *            string a ser tratada
	 * @return string tatada
	 */
	public static String escapeHTMLForAlm(String s) {

		// Substitui as quebras de linha para não serem tratadas
		s = s.replace("<br/>", "\n");

		StringBuilder out = new StringBuilder(Math.max(16, s.length()));
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
				out.append("&#");
				out.append((int) c);
				out.append(';');
			} else {
				out.append(c);
			}
		}

		// Volta as quebras de linha
		String stringRet = out.toString().replace("\n", "<br/>");

		return stringRet;
	}

}
