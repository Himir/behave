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
package br.gov.frameworkdemoiselle.behave.runner.webdriver.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class SwitchDriver {
	
	private Logger logger = Logger.getLogger(SwitchDriver.class);
	private WebDriver driver;
	private List<Node> nodes;
	private int nextFrame = 0;

	public SwitchDriver(WebDriver driver) {
		this.driver = driver;
		mapFrames();
	}

	private void mapFrames() {
		nodes = new ArrayList<SwitchDriver.Node>();
		driver.switchTo().defaultContent();
		Node node = new Node(null, "root");
		nodes.add(node);
		mapFrames(node);
		logger.debug(this);
	}

	/**
	 * Move o driver para seus diversos frames
	 */
	public void switchNextFrame(){
		Node node = nodes.get(nextFrame);
		logger.debug("switch frame:" + node);
		node.switchDriver();
		nextFrame = (nextFrame == nodes.size()-1) ? 0 : (nextFrame + 1);		
	}
	
	private void mapFrames(Node _parent) {
		_parent.switchDriver();
		Pattern pattern = Pattern.compile("(<frame(.*?)name=\")(.*?)(\")");
		Matcher matcher = pattern.matcher(driver.getPageSource());
		while (matcher.find()) {
			Node frame = new Node(_parent, matcher.group(3));			
			nodes.add(frame);
			mapFrames(frame);
		}
	}

	@Override
	public String toString() {
		StringBuffer toSTring = new StringBuffer();
		for (Node node : nodes) {
			toSTring.append(node);
		}
		return toSTring.toString();
	}

	private class Node {

		private Node parent;
		private String name;

		public Node(Node parent, String name) {
			super();
			this.parent = parent;
			this.name = name;
		}

		/**
		 * Faz o driver navegar até o frame do no
		 */
		public void switchDriver() {
			if (isRoot()) {
				driver.switchTo().defaultContent();
			} else {
				parent.switchDriver();
				driver.switchTo().frame(name);
			}
		}

		@Override
		public String toString() {
			StringBuffer toSTring = new StringBuffer();
			if (isRoot()) {
				toSTring.append("\n").append(name);
			} else {
				toSTring.append(parent).append("->");
				toSTring.append(name);				
			}
			return toSTring.toString();
		}

		public boolean isRoot() {
			return (parent == null);
		}

	}
}
