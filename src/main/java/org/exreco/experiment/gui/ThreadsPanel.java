package org.exreco.experiment.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.CaseStatusEvent;
import org.exreco.experiment.Experiment;
import org.exreco.experiment.ExperimentTracker;
import org.exreco.experiment.Case.LifeCycleState;
import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.LiffEventListener;
import org.exreco.liff.core.WorldStatusEvent;


public class ThreadsPanel extends javax.swing.JPanel implements
		LiffEventListener<LiffEvent> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8687817752623148790L;
	private static Logger logger = LogManager.getLogger(ThreadsPanel.class
			.getName());

	public class ThreadLine {

		private final Map<String, JLabel> labels = new LinkedHashMap<String, JLabel>();

		private final JProgressBar progressBar = new JProgressBar();
		private WorldStatusEvent worldStatus;

		/**
		 * @return the progressBar
		 */
		public JProgressBar getProgressBar() {
			return progressBar;
		}

		public ThreadLine() {

			progressBar.setValue(0);
			progressBar.setStringPainted(true);
			// progressBar.setPreferredSize(new Dimension(300, 16));
			progressBar.setForeground(Color.ORANGE);
		}

		public void update() {
			JProgressBar worldProgressBar = this.getProgressBar();
			if (worldStatus != null) {

				if (this.getLabels().containsKey("host")) {
					String value = worldStatus.getHostName();
					this.getLabels().get("host").setText(value);
				}
				if (this.getLabels().containsKey("thread")) {
					String value = worldStatus.getThreadName().replaceAll(
							"node processing-", "");
					this.getLabels().get("thread").setText(value);
				}
				if (this.getLabels().containsKey("id")) {
					String value = String.valueOf(worldStatus.getCaseId());
					this.getLabels().get("id").setText(value);
				}
				if (this.getLabels().containsKey("age")) {
					String value = String.valueOf(worldStatus.getAge());
					this.getLabels().get("age").setText(value);
				}

				int worldActualValue = (int) worldStatus.getAge();
				int worldMaxValue = worldStatus.getMaxLifeTime();
				worldProgressBar.setEnabled(true);
				worldProgressBar.getModel().setMaximum(worldMaxValue);
				worldProgressBar.getModel().setValue(worldActualValue);
				if (worldActualValue >= worldMaxValue) {
					worldProgressBar.setForeground(Color.GREEN);
					this.setWorldStatus(worldStatus);
				} else {
					worldProgressBar.setForeground(Color.ORANGE);
				}
			} else {

				for (String fieldName : this.getLabels().keySet()) {
					this.getLabels().get(fieldName).setText(" ");
				}
				worldProgressBar.setEnabled(false);
				worldProgressBar.getModel().setValue(0);

			}

		}

		/**
		 * @return the worldStatus
		 */
		public WorldStatusEvent getWorldStatus() {
			return worldStatus;
		}

		/**
		 * @param worldStatus
		 *            the worldStatus to set
		 */
		public void setWorldStatus(WorldStatusEvent worldStatus) {
			this.worldStatus = worldStatus;
		}

		/**
		 * @return the labels
		 */
		public Map<String, JLabel> getLabels() {
			return labels;
		}
	}

	private final GridBagLayout layout = new GridBagLayout();
	private final GridBagConstraints constriants = new GridBagConstraints();
	private final TreeMap<String, ThreadLine> threadLines = new TreeMap<String, ThreadLine>();
	private ExperimentTracker experimentTracker;
	private final String[] textFields = { "host", "thread", "id", "age" };

	public ThreadsPanel() {
		this.setLayout(layout);

		constriants.insets = new Insets(2, 5, 5, 2);
		constriants.gridx = 0;
		constriants.gridy = 0;
		constriants.gridwidth = 2;
		constriants.anchor = GridBagConstraints.WEST;
		constriants.fill = GridBagConstraints.HORIZONTAL;
		constriants.fill = GridBagConstraints.BOTH;
		int xPos = 0;
		for (int i = 0; i < textFields.length; i++) {

			constriants.gridx = xPos;
			xPos++;
			JLabel label = new JLabel();
			// threadLine.getLabels().put(textFields[i], label);
			label.setText(textFields[i]);

			this.add(label, constriants);
		}
		constriants.gridx = xPos;
		xPos++;
		JLabel label = new JLabel();
		// threadLine.getLabels().put(textFields[i], label);
		label.setText("progress");

		this.add(label, constriants);
	}

	/**
	 * @return the threadLines
	 */
	public SortedMap<String, ThreadLine> getThreadLines() {
		return threadLines;
	}

	public void add(ThreadLine threadLine) {
		// this.getThreadLines().add(pos, threadLine);
		int pos = threadLines.size() + 1;
		int xPos = 0;
		constriants.anchor = GridBagConstraints.WEST;
		constriants.weightx = 0.3;
		constriants.gridwidth = 1;

		constriants.gridy = pos;

		for (int i = 0; i < textFields.length; i++) {

			constriants.gridx = xPos;
			xPos++;
			JLabel label = new JLabel();
			threadLine.getLabels().put(textFields[i], label);
			this.add(label, constriants);
			label.setForeground(Color.GRAY);
		}

		constriants.gridx = xPos;
		xPos++;
		constriants.fill = GridBagConstraints.HORIZONTAL;
		constriants.weightx = 10;
		this.add(threadLine.getProgressBar(), constriants);

	}

	private void updateProgressBars(
			SortedMap<String, CaseStatusEvent> threadPoolMap) throws Exception {
		Collection<String> threadSlots = new ArrayList<String>(
				threadPoolMap.keySet());

		for (String threadName : threadSlots) {

			ThreadLine threadLine = this.getThreadLines().get(threadName);
			if (threadLine != null) {
				threadLine.setWorldStatus((WorldStatusEvent) threadPoolMap
						.get(threadName));
				threadLine.update();

			}

		}
	}

	@SuppressWarnings("unused")
	private void updateProgressBars() throws Exception {
		ExperimentTracker tracker = this.getCaseTracker();
		SortedMap<String, CaseStatusEvent> threadPoolMap = tracker
				.getThreadPool2CaseStatusMap();
		this.updateProgressBars(threadPoolMap);
	}

	private void updateCaseInfo(WorldStatusEvent worldStatus) {
		if (worldStatus == null) {
			// logger.warn("World Status with empty lifecycle state received.");
			return;
		}
		String threadName = worldStatus.getHostName() + ":"
				+ worldStatus.getThreadName();
		threadName = threadName.replaceAll("node processing-", "");
		if (worldStatus.getLifeCycleState() == LifeCycleState.STARTED) {

			if (!this.getThreadLines().containsKey(threadName)) {

				ThreadLine threadLine = new ThreadLine();

				threadLine.setWorldStatus(worldStatus);
				this.add(threadLine);
				this.getThreadLines().put(threadName, threadLine);

			} else {
				ThreadLine threadLine = this.getThreadLines().get(threadName);
				threadLine.setWorldStatus(worldStatus);
			}

		} else if (worldStatus.getLifeCycleState() == LifeCycleState.ENDED) {

		} else if (worldStatus.getLifeCycleState() == LifeCycleState.RUNNING) {
			if (!this.getThreadLines().containsKey(threadName)) {

				ThreadLine threadLine = new ThreadLine();

				threadLine.setWorldStatus(worldStatus);
				this.add(threadLine);
				this.getThreadLines().put(threadName, threadLine);

			} else {
				ThreadLine threadLine = this.getThreadLines().get(threadName);
				threadLine.setWorldStatus(worldStatus);
			}
		}

	}

	@Override
	public void eventOccurred(LiffEvent liffEvent) {
		try {
			if (liffEvent instanceof Experiment.ExperimentStatusEvent) {
				Experiment.ExperimentStatusEvent experimentStatusEvent = (Experiment.ExperimentStatusEvent) liffEvent;
				Map<String, CaseStatusEvent> statusThreadPoolMap = experimentStatusEvent
						.getThreadPool2CaseStatusMap();
				for (CaseStatusEvent caseStatus : statusThreadPoolMap.values()) {
					WorldStatusEvent worldStatus = (WorldStatusEvent) caseStatus;
					this.updateCaseInfo(worldStatus);
				}
				this.updateProgressBars(experimentStatusEvent
						.getThreadPool2CaseStatusMap());
			}
			/*
			 * else if (liffEvent instanceof CaseStatusEvent) { CaseStatusEvent
			 * caseStatusEvent = (CaseStatusEvent) liffEvent; WorldStatusEvent
			 * worldStatus = (WorldStatusEvent) caseStatusEvent;
			 * this.updateCaseInfo(worldStatus); this.updateProgressBars(); }
			 */
		} catch (Exception ex) {
			logger.warn("Could not update progress bar", ex);
		}
	}

	/**
	 * @return the experimentTracker
	 */
	public ExperimentTracker getCaseTracker() {
		return experimentTracker;
	}

	/**
	 * @param experimentTracker
	 *            the experimentTracker to set
	 */
	public void setCaseTracker(ExperimentTracker experimentTracker) {
		this.experimentTracker = experimentTracker;
	}

	/**
	 * @return the textFields
	 */
	public String[] getTextFields() {
		return textFields;
	}
}
