package org.exreco.experiment.util.events;

import java.rmi.Remote;


public interface RemoteLiffEventListener extends Remote, LiffEventListener<LiffEvent> {

}
