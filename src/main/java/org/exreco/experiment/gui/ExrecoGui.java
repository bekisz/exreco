package org.exreco.experiment.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.rmi.Remote;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.Experiment;
import org.exreco.experiment.util.events.EventBean;
import org.exreco.experiment.util.events.EventHub;
import org.exreco.experiment.util.events.EventTopicHome;
import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.LiffEventListener;
import org.exreco.experiment.util.events.RemoteLiffEventListener;

public class ExrecoGui extends JFrame implements ActionListener, RemoteLiffEventListener, Remote {
	private static Logger logger = LogManager.getLogger(ExrecoGui.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 1075092081495136866L;
	private final Map<Long, ExperimentTab> tabs = new LinkedHashMap<Long, ExperimentTab>();
	private final JTabbedPane tabbedPane = new JTabbedPane();
	private final EventHub<LiffEvent> eventHub = new EventHub<LiffEvent>();
	private EventTopicHome eventTopicHome;
	private String iconImageLocation;
	private int preferredSizeX = 750;
	private int preferredSizeY = 520;

	/**
	 * @return the tabs
	 */
	public Map<Long, ExperimentTab> getTabs() {
		return tabs;
	}

	public ExrecoGui() {
		super("Exreco");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public void init() throws Exception {
		// Create and set up the content pane.
		// Liff liffSwingGui = new Liff();
		// this.setExperiment(experiment);
		// this.getsetOpaque(true); // content panes must be opaque
		// this.setContentPane(liffSwingGui);
		// String imageLoc = "/3_cubes_64.png";
		URL imageURL = ExrecoGui.class.getResource(this.getIconImageLocation());

		if (imageURL != null) {

			Image img = Toolkit.getDefaultToolkit().getImage(imageURL);
			this.setIconImage(img);

		} else {

			logger.warn("Icon image resource at location " + this.getIconImageLocation() + " was not found ");
		}
		this.setPreferredSize(new Dimension(this.getPreferredSizeX(), this.getPreferredSizeY()));
		this.add(this.getTabbedPane());
		// Display the window
		this.pack();
		this.setVisible(true);
		this.wire();
	}

	@Override
	public void eventOccurred(LiffEvent liffEvent) throws Exception {

		try {
			if (liffEvent instanceof Experiment.ExperimentStatusEvent) {
				Experiment.ExperimentStatusEvent experimentStatusEvent = (Experiment.ExperimentStatusEvent) liffEvent;
				long experimentId = experimentStatusEvent.getExperimentId();
				if (!this.getTabs().containsKey(experimentId)) {
					ExperimentTab newTab = new ExperimentTab(experimentId);

					newTab.getEventHub().wireTo(this.getEventHub());
					this.getTabbedPane().addTab("" + experimentId, newTab);
					// this.getTabbedPane().setMnemonicAt(0, KeyEvent.VK_1);
					this.getTabs().put(experimentId, newTab);

				}
				this.getTabs().get(experimentId).eventOccurred(liffEvent);
			}

		} catch (Exception e) {
			logger.error("Exception caught", e);

		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the tabbedPane
	 */
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	/**
	 * @return the eventHub
	 */
	public EventHub<LiffEvent> getEventHub() {
		return eventHub;
	}

	/**
	 * @return the eventTopicHome
	 */
	public EventTopicHome getEventTopicHome() {
		return eventTopicHome;
	}

	public void setEventTopicHome(EventTopicHome eventTopicHome) {
		this.eventTopicHome = eventTopicHome;
	}

	protected void wire() throws Exception {

		eventTopicHome.getEventSource("LiffExperimentStatus").wireTo(this);

		LiffEventListener<LiffEvent> userCommandEventHandler = eventTopicHome.getEventListener("LiffUserCommand");
		this.getEventHub().wireTo(userCommandEventHandler);

	}

	public String getIconImageLocation() {
		return iconImageLocation;
	}

	public void setIconImageLocation(String iconImageLocation) {
		this.iconImageLocation = iconImageLocation;
	}

	public int getPreferredSizeX() {
		return preferredSizeX;
	}

	public void setPreferredSizeX(int preferredSizedX) {
		this.preferredSizeX = preferredSizedX;
	}

	public int getPreferredSizeY() {
		return preferredSizeY;
	}

	public void setPreferredSizeY(int preferredSizedY) {
		this.preferredSizeY = preferredSizedY;
	}

}
