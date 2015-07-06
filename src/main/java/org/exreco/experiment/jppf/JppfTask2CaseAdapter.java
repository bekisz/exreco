package org.exreco.experiment.jppf;

import org.exreco.experiment.CaseShellIf;
import org.jppf.node.protocol.AbstractTask;

public class JppfTask2CaseAdapter extends AbstractTask<String> {
	// JPPF 3.3.7 : public class JppfTask2CaseAdapter extends JPPFTask {

	final private CaseShellIf liffCase;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4852878970689477257L;

	public JppfTask2CaseAdapter(CaseShellIf liffCase) {
		super();
		this.liffCase = liffCase;
	}

	@Override
	public void run() {
		try {
			this.getLiffCase().run();
			this.setResult("success");

		} catch (Exception t) {
			this.setThrowable(t);
			// JPPF 3.3.7 : this.setException(t);
		}

	}

	/**
	 * @return the liffCase
	 */
	public CaseShellIf getLiffCase() {
		return liffCase;
	}

}
