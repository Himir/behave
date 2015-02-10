/*
 * Demoiselle Framework
 * Copyright (C) 2015 SERPRO
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
package br.gov.frameworkdemoiselle.behave.regression.repository;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.gov.frameworkdemoiselle.behave.config.BehaveConfig;
import br.gov.frameworkdemoiselle.behave.exception.BehaveException;
import br.gov.frameworkdemoiselle.behave.message.BehaveMessage;
import br.gov.frameworkdemoiselle.behave.regression.Repository;
import br.gov.frameworkdemoiselle.behave.regression.Result;

import com.google.common.io.Files;

/**
 * 
 * @author SERPRO
 *
 */
public class LocalRepository implements Repository {

	public static String MESSAGEBUNDLE = "demoiselle-regression-repository-bundle";
	public static char BAR = File.separatorChar;
	private static BehaveMessage message = new BehaveMessage(FactoryRepository.MESSAGEBUNDLE);

	private File root;

	public LocalRepository() {
		String url = getProperty("behave.regression.url");
		String folder = getProperty("behave.regression.folder");
		String urlProperties = System.getProperty(url);
		if (urlProperties == null || urlProperties.length() == 0) {
			root = new File(url + BAR + folder);
		} else {
			root = new File(urlProperties + BAR + folder);
		}
		FileUtils.createFolder(root.getAbsolutePath());
	}

	public void clean() {
		clean(root);
	}

	public void clean(File file) {
		if (file.isDirectory()) {
			for (File c : file.listFiles())
				clean(c);
		}
		if (!file.delete()) {
			throw new BehaveException(message.getString("exception-error-delete-file", file.getAbsoluteFile()));
		}
	}

	private String getProperty(String key) {
		String value = BehaveConfig.getProperty(key);
		if (value == null || value.length() == 0) {
			throw new BehaveException(message.getString("exception-properties-not-found", key));
		}
		return value;
	}

	public void save(Result result) {
		try {
			String folder = getFolder(result);
			FileUtils.createFolder(folder);
			PrintWriter writer = new PrintWriter(folder + BAR + result.getId() + ".txt", "UTF-8");
			writer.println(result.getDetail());
			writer.close();
			if (result.getFile() != null) {
				Files.copy(result.getFile(), new File(folder + BAR + result.getId() + "." + FileUtils.getExtension(result.getFile())));
			}
		} catch (Exception e) {
			throw new BehaveException(message.getString("exception-erro-save-result", e.getMessage()), e);
		}
	}

	public Result get(String location, String id) {
		Result result = new Result();
		result.setLocation(location);
		result.setId(id);
		File folder = new File(root.getAbsolutePath() + BAR + location);
		if (folder.exists() && folder.isDirectory()) {
			File detail = new File(folder.getAbsolutePath() + BAR + id + ".txt");
			if (detail.exists() && detail.isFile()) {
				result.setDetail(FileUtils.readFile(detail));
				result.setFile(getFile(location, id));
				return result;
			}
		}
		return null;
	}
	
	public List<String> getLocations() {
		List<String> r = findFolders(root);
		Collections.sort(r);
 		return r;
	}

	private List<String> findFolders(File _file) {
		List<String> r = new ArrayList<String>();
		if (! FileUtils.hasSubFolder(_file)){
			if (_file.equals(root)){
				return r;
			}else{
				r.add(BAR + _file.getName());
				return r;
			}
		}else{			
			for (File file : _file.listFiles()) {
				if (file.isDirectory()){
					for(String path : findFolders(file)){
						if (_file.equals(root)){
							r.add(path);
						}else{
							r.add(_file.getName() + path);
						}
					}							
				}
			}
		}
		return r;		
	}
		
	private File getFile(String location, String id) {
		File folder = new File(root.getAbsolutePath() + BAR + location);
		for (File file : folder.listFiles()) {
			if (file.getName().startsWith(id + ".") && ! FileUtils.getExtension(file).equals("txt")) {
				return file;
			}
		}
		return null;
	}


	private String getFolder(Result result) {
		return root.getAbsolutePath() + BAR + result.getLocation();
	}

	public int countResults() {
		return countResults(root);
	}

	private int countResults(File file) {
		int count = 0;
		if (file.isDirectory()) {
			for (File c : file.listFiles())
				count += countResults(c);
		} else {
			if (FileUtils.getExtension(file).equals("txt")) {
				count++;
			}
		}
		return count;
	}

	
	


}
