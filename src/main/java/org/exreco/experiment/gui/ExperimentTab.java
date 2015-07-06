package org.exreco.experiment.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.ExperimentTracker;
import org.exreco.experiment.ExperimentTrackerImpl;
import org.exreco.experiment.event.PatientExitCommand;
import org.exreco.experiment.event.PatientPauseCommand;
import org.exreco.experiment.event.StartCommand;
import org.exreco.experiment.event.UserCommand;
import org.exreco.experiment.util.events.EventHub;
import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.RemoteLiffEventListener;


public class ExperimentTab extends JComponent implements ActionListener,
		RemoteLiffEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3160372367850762644L;

	private static Logger logger = LogManager.getLogger(ExperimentTab.class
			.getName());

	private ExperimentTracker experimentTracker;
	private final EventHub<LiffEvent> eventHub = new EventHub<LiffEvent>();
	private final JProgressBar experimentprogressBar;
	private final JButton startButton;
	private final JButton pauseButton;
	private final JButton smoothExitButton;
	private final JPanel buttonPanel = new JPanel();
	private final ExperimentInfoPanel experimentInfoPanel = new ExperimentInfoPanel();
	// private final JLabel dimensionQueuedLabel = new JLabel("");
	private final ThreadsPanel threadsPanel = new ThreadsPanel();
	private final JScrollPane scrollPane = new JScrollPane(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private final long experimentId;

	/**
	 * @return the experimentprogressBar
	 */
	public JProgressBar getExperimentprogressBar() {
		return experimentprogressBar;
	}

	/**
	 * @return the smoothExitButton
	 */
	public JButton getSmoothExitButton() {
		return smoothExitButton;
	}

	/**
	 * @return the threadsPanel
	 */
	public ThreadsPanel getThreadsPanel() {
		return threadsPanel;
	}

	private final Timer timer = new Timer("LiffGuiUpdater", true);

	public ExperimentTab(long experimentId) {
		super();
		this.experimentId = experimentId;
		GridBagLayout layout = new GridBagLayout();

		// layout.preferredLayoutSize(this);

		this.setLayout(layout);
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.insets = new Insets(2, 5, 5, 2);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.LINE_START;
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.fill = GridBagConstraints.BOTH;
		// JPanel infoPanel = new ExperimentInfoPanel();

		this.add(this.getExperimentInfoPanel(), constraints);
		constraints.gridy = 1;
		constraints.ipadx = 0;
		constraints.ipady = 0;
		// this.add(this.getDimensionQueuedLabel(), constraints);
		// ---- Button Panel
		this.getButtonPanel().setLayout(new GridBagLayout());

		GridBagConstraints buttonConstraints = new GridBagConstraints();
		buttonConstraints.insets = new Insets(2, 5, 5, 2);
		buttonConstraints.gridx = 0;
		buttonConstraints.gridy = 0;
		buttonConstraints.gridwidth = 1;
		buttonConstraints.anchor = GridBagConstraints.WEST;
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		buttonConstraints.fill = GridBagConstraints.BOTH;
		this.startButton = new JButton("Start");
		this.startButton.setActionCommand("Start");
		this.startButton.addActionListener(this);
		this.getStartButton().setEnabled(false);
		this.getButtonPanel().add(this.getStartButton(), buttonConstraints);

		this.pauseButton = new JButton("Pause");
		this.pauseButton.setActionCommand("Pause");
		this.pauseButton.addActionListener(this);
		buttonConstraints.gridy = 1;
		this.getButtonPanel().add(this.getPauseButton(), buttonConstraints);

		smoothExitButton = new JButton("Stop");
		smoothExitButton.setActionCommand("Stop");
		smoothExitButton.addActionListener(this);

		buttonConstraints.gridy = 2;
		this.getButtonPanel().add(smoothExitButton, buttonConstraints);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 3;
		constraints.fill = GridBagConstraints.BOTH;

		this.add(this.getButtonPanel(), constraints);

		constraints.gridheight = 1;
		constraints.weightx = 10;
		constraints.weighty = 15;
		constraints.gridy = 2;
		constraints.gridx = 0;
		constraints.gridwidth = 1;
		this.getScrollPane().setPreferredSize(new Dimension(400, 50));
		this.getScrollPane().setViewportView(this.getThreadsPanel());

		this.add(this.getScrollPane(), constraints);
		experimentprogressBar = new JProgressBar();
		experimentprogressBar.setValue(0);
		experimentprogressBar.setStringPainted(true);
		experimentprogressBar.setForeground(Color.RED);
		constraints.gridheight = 1;
		constraints.gridwidth = 3;
		constraints.weighty = 1;
		constraints.gridy = 3;
		constraints.gridx = 0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		this.experimentprogressBar.setValue(0);
		this.add(this.experimentprogressBar, constraints);

		// this.setPreferredSize(new Dimension(750, 300));
		// this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

	}

	/*
	 * private void updateInfoLabels(
	 * ExperimentTrackerImpl.ExperimentStatusEvent experimentStatusEvent) throws
	 * Exception { StringBuffer sb = new StringBuffer(100); int i = 0;
	 * sb.append("Experiment space : ");
	 * sb.append(experimentStatusEvent.getDimensionSet().toString());
	 * this.getInfoLables().get(i++).setText(sb.toString()); sb = new
	 * StringBuffer(100); sb.append("Cases finished : ");
	 * sb.append(experimentStatusEvent.getCasesEnded()); sb.append(" / ");
	 * sb.append(experimentStatusEvent.getTotalCases());
	 * 
	 * this.getInfoLables().get(i++).setText(sb.toString()); sb = new
	 * StringBuffer(100); sb.append("Time remaining : "); Time remainingTime =
	 * experimentStatusEvent.getExpectedRemainingTime(); if (remainingTime !=
	 * null) { sb.append(remainingTime.toString()); }
	 * this.getInfoLables().get(i++).setText(sb.toString()); sb = new
	 * StringBuffer(100); sb.append("Expected total run time : "); Time
	 * totalRunTime = new Time((new java.util.Date()).getTime() -
	 * experimentStatusEvent.getStartDate().getTime() +
	 * remainingTime.getMilliseconds());
	 * 
	 * sb.append(totalRunTime.toString());
	 * 
	 * this.getInfoLables().get(i++).setText(sb.toString()); sb = new
	 * StringBuffer(100); sb.append("Started : ");
	 * sb.append(experimentStatusEvent.getStartDate().toString());
	 * this.getInfoLables().get(i++).setText(sb.toString());
	 * 
	 * sb = new StringBuffer(100); sb.append("Last update : ");
	 * sb.append(experimentStatusEvent.getSentDate().toString());
	 * this.getInfoLables().get(i++).setText(sb.toString());
	 * 
	 * sb = new StringBuffer(100); sb.append("Finished : "); java.util.Date
	 * finishDate = experimentStatusEvent.getFinishDate(); if (finishDate !=
	 * null) { sb.append(finishDate.toString()); } else { sb.append(" - "); }
	 * 
	 * this.getInfoLables().get(i++).setText(sb.toString()); sb = new
	 * StringBuffer(100);
	 * 
	 * this.getInfoLables().get(i++).setText(sb.toString());
	 * 
	 * }
	 */

	@SuppressWarnings("unused")
	private void updateProgressBars() throws Exception {
		ExperimentTracker tracker = this.getCaseTracker();
		int actualValue = (int) tracker.getTicksEnded();
		int maxValue = (int) tracker.getExpectedTotalTicks();

		this.getExperimentProgressBar().getModel().setMaximum(maxValue);
		this.getExperimentProgressBar().getModel().setValue(actualValue);

	}

	private void updateProgressBars(
			ExperimentTrackerImpl.ExperimentStatusEvent experimentStatusEvent)
			throws Exception {

		int actualValue = (int) experimentStatusEvent.getTicksEnded();
		int maxValue = (int) experimentStatusEvent.getExpectedTotalTicks();

		this.getExperimentProgressBar().getModel().setMaximum(maxValue);
		this.getExperimentProgressBar().getModel().setValue(actualValue);
		if (experimentStatusEvent.getLifeCycleState() == ExperimentTrackerImpl.LifeCycleState.ENDED) {
			this.getExperimentProgressBar().setBackground(Color.GREEN);
		}

	}

	/**
	 * Invoked when the user presses the start button.
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		UserCommand userCommand = new UserCommand(this.getExperimentId());
		if ("Stop".equals(evt.getActionCommand())) {

			smoothExitButton.setEnabled(false);
			this.getExperimentProgressBar().setForeground(Color.YELLOW);
			// this.getWorldProgressBar().setForeground(Color.YELLOW);

			// this.getExperiment().patientClose();
			userCommand = new PatientExitCommand(this.getExperimentId());
		} else if ("Pause".equals(evt.getActionCommand())) {

			this.getStartButton().setEnabled(true);
			this.getPauseButton().setEnabled(false);
			this.getExperimentProgressBar().setForeground(Color.GRAY);
			userCommand = new PatientPauseCommand(this.getExperimentId());

		} else if ("Start".equals(evt.getActionCommand())) {

			this.getStartButton().setEnabled(false);
			this.getPauseButton().setEnabled(true);
			this.getExperimentProgressBar().setForeground(Color.RED);
			userCommand = new StartCommand(this.getExperimentId());
		}
		this.getEventHub().eventOccurred(userCommand);
	}

	@Override
	public void eventOccurred(LiffEvent liffEvent) throws Exception {

		try {
			if (liffEvent instanceof ExperimentTrackerImpl.ExperimentStatusEvent) {
				ExperimentTrackerImpl.ExperimentStatusEvent experimentStatusEvent = (ExperimentTrackerImpl.ExperimentStatusEvent) liffEvent;

				// this.updateRemainingTime(experimentStatusEvent);
				this.getExperimentInfoPanel().eventOccurred(liffEvent);
				this.getThreadsPanel().eventOccurred(experimentStatusEvent);
				this.updateProgressBars(experimentStatusEvent);

			}

		} catch (Exception e) {
			logger.error("Exceptionn caught ",e);

		}

	}

	public JProgressBar getExperimentProgressBar() {
		return experimentprogressBar;
	}

	/**
	 * @return the remainingTimeTimer
	 */
	public Timer getTimer() {
		return timer;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	/**
	 * @return the pauseButton
	 */
	public JButton getPauseButton() {
		return pauseButton;
	}

	/**
	 * @return the startButton
	 */
	public JButton getStartButton() {
		return startButton;
	}

	/**
	 * @return the buttonPanel
	 */
	public JPanel getButtonPanel() {
		return buttonPanel;
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
		this.getThreadsPanel().setCaseTracker(experimentTracker);
	}

	/**
	 * @return the experimentInfoPanel
	 */
	public ExperimentInfoPanel getExperimentInfoPanel() {
		return experimentInfoPanel;
	}

	/**
	 * @return the eventHub
	 */
	public EventHub<LiffEvent> getEventHub() {
		return eventHub;
	}

	/**
	 * @return the experimentId
	 */
	public long getExperimentId() {
		return experimentId;
	}

}
