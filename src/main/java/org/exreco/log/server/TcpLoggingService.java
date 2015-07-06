package org.exreco.log.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.exreco.experiment.util.LiffUtils;


public class TcpLoggingService extends Thread {
	/**
	 * Factory that creates a Configuration for the server.
	 */

	/**
	 * Thread that processes the events.
	 */
	private class SocketHandler extends Thread {

		private final ObjectInputStream ois;

		private boolean shutdown = false;
		private final Socket socket;

		public SocketHandler(Socket socket) throws IOException {

			// ois = new PrivateObjectInputStream(new ObjectInputStream(
			// socket.getInputStream()));
			this.socket = socket;
			ois = new ObjectInputStream(socket.getInputStream());
		}

		public void shutdown() {
			this.shutdown = true;
			interrupt();
		}

		@Override
		public void run() {
			logger.info(
					"SocketHandler started. Remote port : {} ; Local Port: {};",
					this.socket.getPort(), this.socket.getLocalPort());
			String name = this.socket.getRemoteSocketAddress().toString();
			this.setName(name);
			boolean closed = false;
			try {
				try {
					while (!shutdown) {
						LogEvent event = (LogEvent) ois.readObject();
						logger.info(
								"SocketHandler : LogEvent read.  Remote port : {} ; Local Port: {};",
								this.socket.getPort(),
								this.socket.getLocalPort());
						if (event != null) {

							logEventSink.log(event);
						} else {
							logger.error("Null LogEvent received.");
						}
					}
				} catch (EOFException eof) {
					logger.warn("EOF on Socket ", eof);
					closed = true;
				} catch (OptionalDataException opt) {
					logger.error("OptionalDataException eof=" + opt.eof
							+ " length=" + opt.length, opt);
				} catch (ClassNotFoundException cnfe) {
					logger.error("Unable to locate LogEvent class", cnfe);
				} catch (IOException ioe) {
					logger.error(
							"IOException encountered while reading from socket",
							ioe);
				}
				if (!closed) {
					try {
						ois.close();
					} catch (Exception ex) {
						// Ignore the exception;
					}
				}
			} finally {
				handlers.remove(getId());
				logger.info(
						"SocketHandler finished. Remote port : {} ; Local Port: {};",
						this.socket.getPort(), this.socket.getLocalPort());
			}
		}
	}

	private final org.apache.logging.log4j.Logger logger = LogManager
			.getLogger(TcpLoggingService.class.getName());
	private boolean isActive = true;

	private final ConcurrentMap<Long, SocketHandler> handlers = new ConcurrentHashMap<Long, SocketHandler>();
	private final ServerSocket sock;

	public TcpLoggingService(int portNum, LogEventSink logEventSink)
			throws IOException {
		this.sock = new ServerSocket(portNum);
		this.logEventSink = logEventSink;
	}

	public TcpLoggingService() throws IOException {
		this(DEFAULT_PORT, new LogEventSink());
	}

	/**
	 * Shutdown the server.
	 */
	public void shutdown() {
		this.isActive = false;
		Thread.currentThread().interrupt();
	}

	@Override
	public void run() {
		ThreadContext.put("pid", LiffUtils.getProcessId());
		Thread.currentThread().setName(
				"" + this.getClass().getSimpleName() + "-Acceptor");
		logger.info("TCP Logging Service is active on port {}",
				this.sock.getLocalPort());
		while (isActive) {
			try {
				// Accept incoming connections.
				Socket clientSocket = this.sock.accept();
				logger.info(
						"Client socket accepted. Remote port : {} ; Local Port: {};",
						clientSocket.getPort(), clientSocket.getLocalPort());
				// accept() will block until a client connects to the
				// server.
				// If execution reaches this point, then it means that a
				// client
				// socket has been accepted.

				SocketHandler handler = new SocketHandler(clientSocket);
				handlers.put(handler.getId(), handler);
				handler.start();
				logger.info(
						"Handler started. Remote port : {} ; Local Port: {};",
						clientSocket.getPort(), clientSocket.getLocalPort());

				// isActive = false;
			} catch (IOException ioe) {
				logger.warn("Exception encountered on accept. Ignoring. Stack Trace :");
				logger.catching(Level.WARN, ioe);
			}
		}
		logger.info("Deactivated");

		for (Map.Entry<Long, SocketHandler> entry : handlers.entrySet()) {
			SocketHandler handler = entry.getValue();
			handler.shutdown();
			try {
				handler.join();
			} catch (InterruptedException ie) {
				// Ignore the exception
			}
		}
		logger.info("Exit");

	}

	private final LogEventSink logEventSink;

	private static final int DEFAULT_PORT = 8099;
	private static final String DEFAULT_CONFIG_FILE_NAME = "config/log4j2-collector.xml";

	public static void main(String[] args) throws Exception {

		ConfigurationFactory
				.setConfigurationFactory(new ServerConfigurationFactory(
						DEFAULT_CONFIG_FILE_NAME));

		try {
			TcpLoggingService tcp = new TcpLoggingService();
			tcp.start();
			// udp = new UDPSocketServer();
			// udp.start();
		} catch (IOException e) {

			e.printStackTrace();
		}

		// ((LoggerContext) LogManager.getContext()).reconfigure();

	}
}
