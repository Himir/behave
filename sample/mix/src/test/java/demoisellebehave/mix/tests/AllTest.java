package demoisellebehave.mix.tests;

import org.junit.Test;

import br.gov.frameworkdemoiselle.behave.controller.BehaveContext;
import demoisellebehave.mix.steps.MySteps;

public class AllTest {

	private BehaveContext eng = null;

	public AllTest() {
		eng = BehaveContext.getInstance();
		eng.addSteps(new MySteps());
	}

	@Test
	public void testFrames() {
		eng.run("/stories/frames/frames.story");
	}

	@Test
	public void testIFrames() {
		eng.run("/stories/frames/iframes.story");
	}
	
	@Test
	public void testPopup() {
		eng.run("/stories/popup/popup.story");
	}
	
	@Test
	public void testDialog() {
		eng.run("/stories/dialog/dialog.story");
	}
	
	@Test
	public void testEmbedded(){
		eng.run("/stories/embedded/embedded.story");
	}
	
	
	@Test
	public void testColor(){
		eng.run("/stories/color/color.story");
	}

}
