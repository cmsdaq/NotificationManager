package cern.cms.daq.nm;


import static org.hamcrest.MatcherAssert.assertThat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.tuple.Pair;
import static org.hamcrest.Matchers.hasItem;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import cern.cms.daq.nm.persistence.Event;
import cern.cms.daq.nm.persistence.EventSenderType;
import cern.cms.daq.nm.persistence.EventType;
import cern.cms.daq.nm.persistence.LogicModuleView;
import cern.cms.daq.nm.servlet.ServletListener;
import cern.cms.daq.nm.sound.Priority;
import cern.cms.daq.nm.task.TaskManager;

public class ReceiveExternalAlarmIT {

	@Rule
	public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

	static CMSWOWStub runnable;
	static Thread thread;
	static ServerSocket ssock;

	@BeforeClass
	public static void initializeMockSoundSystem() throws IOException {
		ssock = new ServerSocket(50505);
		runnable = new CMSWOWStub(ssock);
		thread = new Thread(runnable);
		thread.start();
	}

	@AfterClass
	public static void stop() throws InterruptedException, IOException {
		ssock.close();
		runnable.terminate();
		thread.join();
		System.out.println("Successfully stopped CMSWOW stub");
	}

	@Test
	public void addCriticalEventTest() throws IOException, InterruptedException {
		ServletContext a = new ServletContextMock();
		ServletContextEvent context = new ServletContextEvent(a);

		environmentVariables.set("NM_CONF", "src/test/resources/integration.properties");
		ServletListener serveltListener = new ServletListener();
		serveltListener.contextInitialized(context);

		List<EventType> filteredTypes = Arrays.asList(EventType.values());
		EventResource testEvent = new EventResource();
		testEvent.setTitle("test");
		testEvent.setPriority(Priority.DEFAULTT);
		testEvent.setEventType(EventType.Single);
		testEvent.setEventSenderType(EventSenderType.External);
		testEvent.setTextToSpeech("speakme");
		testEvent.setDate(DatatypeConverter.parseDateTime("2016-11-30T12:00:20Z").getTime());
		TaskManager.get().getEventResourceBuffer().add(testEvent);

		Thread.sleep(4000);

		// Check if sound system received
		Assert.assertEquals(2, runnable.getSuccessfulRequestCounter());
		System.out.println(runnable.getRequests());
		assertThat(runnable.getRequests(), hasItem("<talk>speakme</talk>"));
		assertThat(runnable.getRequests(), hasItem("<play file=\"U2Bell.wav\"/>"));

		// Check if event stored in DB
		Date startDate = DatatypeConverter.parseDateTime("2016-11-30T11:00:20Z").getTime();
		Date endDate = DatatypeConverter.parseDateTime("2016-11-30T14:00:30Z").getTime();

		Pair<List<Event>, Long> result = Application.get().getPersistenceManager().getEvents(startDate, endDate,
				filteredTypes, new ArrayList<LogicModuleView>(), 1, 10);

		Assert.assertEquals(1, result.getLeft().size());
		Assert.assertEquals((Long) 1L, result.getRight());

	}

}

class CMSWOWStub implements Runnable {
	private volatile boolean running = true;

	private int successfulRequestCounter;
	private ServerSocket serverSocket;

	private List<String> requests;

	CMSWOWStub(ServerSocket csocket) {
		this.serverSocket = csocket;
		this.successfulRequestCounter = 0;
		requests = new ArrayList<String>();
	}

	public void run() {

		while (running) {
			Socket sock;
			try {
				System.out.println("Waiting for connections to CMSWOW");
				sock = serverSocket.accept();
				System.out.println("Client connected to CMSWOW");
				PrintWriter printWriter;
				try {

					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
					char[] buffer = new char[4000];
					int count = bufferedReader.read(buffer, 0, 4000);
					String requestToCMSWOW = new String(buffer, 0, count);
					System.out.println(requestToCMSWOW);
					requests.add(requestToCMSWOW);

					printWriter = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));

					printWriter.print("All ok\n");
					printWriter.flush();
					successfulRequestCounter++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (SocketException e) {

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	public void terminate() {
		running = false;
	}

	public int getSuccessfulRequestCounter() {
		return successfulRequestCounter;
	}

	public List<String> getRequests() {
		return requests;
	}

	public void setRequests(List<String> requests) {
		this.requests = requests;
	}

}

class ServletContextMock implements ServletContext {

	private HashMap<String, Object> parameter = new HashMap<>();

	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {

	}

	@Override
	public boolean setInitParameter(String name, String value) {

		return false;
	}

	@Override
	public void setAttribute(String name, Object object) {
		parameter.put(name, object);

	}

	@Override
	public void removeAttribute(String name) {

	}

	@Override
	public void log(String message, Throwable throwable) {

	}

	@Override
	public void log(Exception exception, String msg) {

	}

	@Override
	public void log(String msg) {

	}

	@Override
	public String getVirtualServerName() {

		return null;
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {

		return null;
	}

	@Override
	public Enumeration<Servlet> getServlets() {

		return null;
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {

		return null;
	}

	@Override
	public ServletRegistration getServletRegistration(String servletName) {

		return null;
	}

	@Override
	public Enumeration<String> getServletNames() {

		return null;
	}

	@Override
	public String getServletContextName() {

		return null;
	}

	@Override
	public Servlet getServlet(String name) throws ServletException {

		return null;
	}

	@Override
	public String getServerInfo() {

		return null;
	}

	@Override
	public Set<String> getResourcePaths(String path) {

		return null;
	}

	@Override
	public InputStream getResourceAsStream(String path) {

		return null;
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {

		return null;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {

		return null;
	}

	@Override
	public String getRealPath(String path) {

		return null;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String name) {

		return null;
	}

	@Override
	public int getMinorVersion() {

		return 0;
	}

	@Override
	public String getMimeType(String file) {

		return null;
	}

	@Override
	public int getMajorVersion() {

		return 0;
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {

		return null;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {

		return null;
	}

	@Override
	public String getInitParameter(String name) {

		return null;
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {

		return null;
	}

	@Override
	public FilterRegistration getFilterRegistration(String filterName) {

		return null;
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {

		return null;
	}

	@Override
	public int getEffectiveMinorVersion() {

		return 0;
	}

	@Override
	public int getEffectiveMajorVersion() {

		return 0;
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {

		return null;
	}

	@Override
	public String getContextPath() {

		return null;
	}

	@Override
	public ServletContext getContext(String uripath) {

		return null;
	}

	@Override
	public ClassLoader getClassLoader() {

		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {

		return null;
	}

	@Override
	public Object getAttribute(String name) {

		return parameter.get(name);
	}

	@Override
	public void declareRoles(String... roleNames) {

	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {

		return null;
	}

	@Override
	public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {

		return null;
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {

		return null;
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName,
			Class<? extends Servlet> servletClass) {

		return null;
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {

		return null;
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, String className) {

		return null;
	}

	@Override
	public void addListener(Class<? extends EventListener> listenerClass) {

	}

	@Override
	public <T extends EventListener> void addListener(T t) {

	}

	@Override
	public void addListener(String className) {

	}

	@Override
	public Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {

		return null;
	}

	@Override
	public Dynamic addFilter(String filterName, Filter filter) {

		return null;
	}

	@Override
	public Dynamic addFilter(String filterName, String className) {

		return null;
	}
};
