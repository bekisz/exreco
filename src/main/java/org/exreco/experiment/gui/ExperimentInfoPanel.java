package org.exreco.experiment.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.CaseStatusEvent;
import org.exreco.experiment.Experiment;
import org.exreco.experiment.dim.Dimension;
import org.exreco.experiment.util.Time;
import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.LiffEventListener;
import org.exreco.liff.core.WorldStatusEvent;

public class ExperimentInfoPanel extends javax.swing.JPanel implements
		LiffEventListener<LiffEvent> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3609369085444321644L;
	private final List<JLabel> infoLabels = new ArrayList<JLabel>();
	private final List<JLabel> infoValues = new ArrayList<JLabel>();
	private int largestSeenIsoCaseId = 0;

	@SuppressWarnings("unused")
	private static Logger logger = LogManager
			.getLogger(ExperimentInfoPanel.class.getName());

	public ExperimentInfoPanel() {
		super(true);
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.insets = new Insets(1, 5, 1, 5);
		constraints.gridx = 0;
		constraints.gridy = 0;

		// constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		// constraints.fill = GridBagConstraints.BOTH;
		// ------------ Info Line -----------------
		JLabel infoLabel = new JLabel("Experiment dimensions");
		constraints.gridx = 0;
		this.add(infoLabel, constraints);
		constraints.gridx = 1;
		JLabel colonLabel = new JLabel(" : ");
		this.add(colonLabel, constraints);
		JLabel infoValue = new JLabel();
		infoValue.setText(" - ");
		constraints.gridx = 2;
		constraints.weightx = 10;
		this.getInfoValues().add(infoValue);
		this.add(infoValue, constraints);
		// ------------ Info Line -----------------
		constraints.weightx = 1;
		constraints.gridy++;
		infoLabel = new JLabel("State space");
		constraints.gridx = 0;
		this.add(infoLabel, constraints);
		constraints.gridx = 1;
		colonLabel = new JLabel(" : ");
		this.add(colonLabel, constraints);
		infoValue = new JLabel();
		infoValue.setText(" - ");
		constraints.gridx = 2;
		this.getInfoValues().add(infoValue);
		this.add(infoValue, constraints);
		// ------------ Info Line -----------------
		constraints.gridy++;
		infoLabel = new JLabel("Dimension Ranges");
		constraints.gridx = 0;
		this.add(infoLabel, constraints);
		constraints.gridx = 1;
		colonLabel = new JLabel(" : ");
		this.add(colonLabel, constraints);
		infoValue = new JLabel();
		infoValue.setText(" - ");
		constraints.gridx = 2;
		this.getInfoValues().add(infoValue);
		this.add(infoValue, constraints);

		// ------------ Info Line -----------------
		constraints.gridy++;
		infoLabel = new JLabel("Completed cases");
		constraints.gridx = 0;
		this.add(infoLabel, constraints);
		constraints.gridx = 1;
		colonLabel = new JLabel(" : ");

		this.add(colonLabel, constraints);
		infoValue = new JLabel();
		infoValue.setText(" - ");
		constraints.gridx = 2;
		this.getInfoValues().add(infoValue);
		this.add(infoValue, constraints);
		// ------------ Info Line -----------------
		constraints.gridy++;
		infoLabel = new JLabel("Last iso case ID");
		constraints.gridx = 0;
		this.add(infoLabel, constraints);
		constraints.gridx = 1;
		colonLabel = new JLabel(" : ");

		this.add(colonLabel, constraints);
		infoValue = new JLabel();
		infoValue.setText(" - ");
		constraints.gridx = 2;
		this.getInfoValues().add(infoValue);
		this.add(infoValue, constraints);
		// ------------ Info Line -----------------
		constraints.gridy++;
		infoLabel = new JLabel("Time remaining");
		constraints.gridx = 0;
		this.add(infoLabel, constraints);
		constraints.gridx = 1;
		colonLabel = new JLabel(" : ");
		this.add(colonLabel, constraints);
		infoValue = new JLabel();
		infoValue.setText(" - ");
		constraints.gridx = 2;
		this.getInfoValues().add(infoValue);
		this.add(infoValue, constraints);
		// ------------ Info Line -----------------
		constraints.gridy++;
		infoLabel = new JLabel("Total run time");
		constraints.gridx = 0;
		this.add(infoLabel, constraints);
		constraints.gridx = 1;
		colonLabel = new JLabel(" : ");
		this.add(colonLabel, constraints);
		infoValue = new JLabel();
		infoValue.setText(" - ");
		constraints.gridx = 2;
		this.getInfoValues().add(infoValue);
		this.add(infoValue, constraints);
		// ------------ Info Line -----------------
		constraints.gridy++;
		infoLabel = new JLabel("Average speed");
		constraints.gridx = 0;
		this.add(infoLabel, constraints);
		constraints.gridx = 1;
		colonLabel = new JLabel(" : ");
		this.add(colonLabel, constraints);
		infoValue = new JLabel();
		infoValue.setText(" - ");
		constraints.gridx = 2;
		this.getInfoValues().add(infoValue);
		this.add(infoValue, constraints);
		// ------------ Info Line -----------------
		constraints.gridy++;
		infoLabel = new JLabel("Current speed");
		constraints.gridx = 0;
		this.add(infoLabel, constraints);
		constraints.gridx = 1;
		colonLabel = new JLabel(" : ");
		this.add(colonLabel, constraints);
		infoValue = new JLabel();
		infoValue.setText(" - ");
		constraints.gridx = 2;
		this.getInfoValues().add(infoValue);
		this.add(infoValue, constraints);
		// ------------ Info Line -----------------
		constraints.gridy++;
		infoLabel = new JLabel("Max speed");
		constraints.gridx = 0;
		this.add(infoLabel, constraints);
		constraints.gridx = 1;
		colonLabel = new JLabel(" : ");
		this.add(colonLabel, constraints);
		infoValue = new JLabel();
		infoValue.setText(" - ");
		constraints.gridx = 2;
		this.getInfoValues().add(infoValue);
		this.add(infoValue, constraints);
		// ------------ Info Line -----------------
		constraints.gridy++;
		infoLabel = new JLabel("Status");
		constraints.gridx = 0;
		this.add(infoLabel, constraints);
		constraints.gridx = 1;
		colonLabel = new JLabel(" : ");
		this.add(colonLabel, constraints);
		infoValue = new JLabel();
		infoValue.setText(" - ");
		constraints.gridx = 2;
		this.getInfoValues().add(infoValue);
		this.add(infoValue, constraints);
	}

	/**
	 * @return the infoLabels
	 */
	public List<JLabel> getInfoLabels() {
		return infoLabels;
	}

	/**
	 * @return the infoValues
	 */
	public List<JLabel> getInfoValues() {
		return infoValues;
	}

	@Override
	public void eventOccurred(LiffEvent event) throws Exception {
		if (event instanceof Experiment.ExperimentStatusEvent) {
			Experiment.ExperimentStatusEvent expStatEvent = (Experiment.ExperimentStatusEvent) event;
			String str = " - ";
			int i = 0;
			// dimensions names
			StringBuffer sb = new StringBuffer(70);

			for (Dimension dim : expStatEvent.getDimensionSet()
					.getDimensionMap().values()) {
				sb.append(dim.getName());
				sb.append("  ");

			}
			this.getInfoValues().get(i++).setText(sb.toString());
			// dimensions space
			sb = new StringBuffer(70);
			String multiplier = "";
			for (Dimension dim : expStatEvent.getDimensionSet()
					.getDimensionMap().values()) {
				sb.append(multiplier);
				sb.append(dim.getMin());
				sb.append("...");
				sb.append(dim.getMax());
				multiplier = " x ";

			}
			this.getInfoValues().get(i++).setText(sb.toString());
			// dimensions ranges
			sb = new StringBuffer(70);
			multiplier = "";
			for (Dimension dim : expStatEvent.getDimensionSet()
					.getDimensionMap().values()) {
				sb.append(multiplier);
				sb.append(dim.totalIncrements());
				multiplier = " x ";

			}
			this.getInfoValues().get(i++).setText(sb.toString());

			// completed cases
			str = "" + expStatEvent.getCasesEnded() + " out of "
					+ expStatEvent.getDimensionSet().getPermutations();

			this.getInfoValues().get(i++).setText(str);
			// largest seen iso case ID
			//TODO : Perfomance !
			Map<String, CaseStatusEvent> statusThreadPoolMap = expStatEvent
					.getThreadPool2CaseStatusMap();
			for (CaseStatusEvent caseStatus : statusThreadPoolMap.values()) {
				WorldStatusEvent worldStatus = (WorldStatusEvent) caseStatus;
				// 0 is the standard index of iso case id
				if (worldStatus != null
						&& worldStatus.getDimensionSetPoint() != null
						&& worldStatus.getDimensionSetPoint().get(0) != null) {

					int currentIsoCaseID = (Integer) worldStatus
							.getDimensionSetPoint().get(0).getValue();
					if (currentIsoCaseID > this.getLargestSeenIsoCaseId()) {
						this.setLargestSeenIsoCaseId(currentIsoCaseID);
					}
				}
			}
			str = "" + this.getLargestSeenIsoCaseId();

			this.getInfoValues().get(i++).setText(str);
			// remaining time
			str = " - ";
			Time remainingTime = expStatEvent.getExpectedRemainingTime();
			if (remainingTime != null) {
				this.getInfoValues().get(i++).setText(remainingTime.toString());
			}
			// expected total run time
			str = " - ";
			if (expStatEvent.getLifeCycleState() == Experiment.LifeCycleState.RUNNING) {
				Time totalRunTime;
				totalRunTime = new Time((new java.util.Date()).getTime()
						- expStatEvent.getStartDate().getTime()
						+ remainingTime.getMilliseconds());
				str = totalRunTime.toString();
			} else if (expStatEvent.getLifeCycleState() == Experiment.LifeCycleState.ENDED) {
				Time totalRunTime;
				long duration = expStatEvent.getFinishDate().getTime()
						- expStatEvent.getStartDate().getTime();
				totalRunTime = new Time(duration);
				str = totalRunTime.toString();
			} else {
				str = " - ";
			}

			this.getInfoValues().get(i++).setText(str);
			// average speed
			// str = " - ";
			double avgSpeedInTicks = expStatEvent.getAvgSpeedInTicks();
			double avgSpeedInCases = expStatEvent.getAvgSpeedInCases();
			DecimalFormat df = new DecimalFormat("#");
			DecimalFormat df2 = new DecimalFormat("#");
			this.getInfoValues()
					.get(i)
					.setText(
							"" + df.format(avgSpeedInTicks) + " ticks/s  "
									+ df2.format(avgSpeedInCases)
									+ " cases/min");

			i++;
			// current speed
			this.getInfoValues()
					.get(i)
					.setText(
							""
									+ df.format(expStatEvent
											.getCurrentSpeedInTicks())
									+ " ticks/sec");
			i++;
			// maximum speed
			this.getInfoValues()
					.get(i)
					.setText(
							"" + df.format(expStatEvent.getMaxSpeedInTicks())
									+ " ticks/sec");
			i++;
			// state
			str = "Unknown";
			if (expStatEvent.getLifeCycleState() == Experiment.LifeCycleState.RUNNING) {
				str = "Running";
			} else if (expStatEvent.getLifeCycleState() == Experiment.LifeCycleState.ENDED) {

				str = "Ended";
			} else if (expStatEvent.getLifeCycleState() == Experiment.LifeCycleState.STARTED) {

				str = "Started";
			} else if (expStatEvent.getLifeCycleState() == Experiment.LifeCycleState.CANCELLED) {

				str = "Cancelled";
			} else if (expStatEvent.getLifeCycleState() == Experiment.LifeCycleState.INITED) {

				str = "Inited";
			} else if (expStatEvent.getLifeCycleState() == Experiment.LifeCycleState.CREATED) {

				str = "Created";
			}
			this.getInfoValues().get(i++).setText(str);
		}
	}

	public int getLargestSeenIsoCaseId() {
		return largestSeenIsoCaseId;
	}

	public void setLargestSeenIsoCaseId(int largestSeenIsoCaseId) {
		this.largestSeenIsoCaseId = largestSeenIsoCaseId;
	}
}
