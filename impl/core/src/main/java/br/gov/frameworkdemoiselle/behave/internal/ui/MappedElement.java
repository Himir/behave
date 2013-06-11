package br.gov.frameworkdemoiselle.behave.internal.ui;

import br.gov.frameworkdemoiselle.behave.annotation.ElementMap;
import br.gov.frameworkdemoiselle.behave.internal.spi.InjectionManager;
import br.gov.frameworkdemoiselle.behave.runner.Runner;
import br.gov.frameworkdemoiselle.behave.runner.ui.base.BaseUI;

public abstract class MappedElement implements BaseUI {

	protected Runner runner = (Runner) InjectionManager.getInstance().getInstanceDependecy(Runner.class);
	protected ElementMap elementMap;

	public ElementMap getElementMap() {
		return elementMap;
	}

	public void setElementMap(ElementMap elementMap) {
		this.elementMap = elementMap;
	}

	public Runner getRunner() {
		return runner;
	}
	
	public void setRunner(Runner runner) {
		this.runner = runner;
	}

}
