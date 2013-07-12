package demoisellebehave.serpro.treino;

import java.util.ArrayList;

import org.junit.Test;

import br.gov.frameworkdemoiselle.behave.controller.BehaveContext;

public class Components {
	private BehaveContext eng = BehaveContext.getInstance();

	@Test
	public void testAcesso() throws Throwable {
		ArrayList<String> stories = new ArrayList<String>();
		stories.add("/stories/acesso/acesso.story");
		stories.add("/stories/framework/components.story");

		eng.run(stories);
	}

}