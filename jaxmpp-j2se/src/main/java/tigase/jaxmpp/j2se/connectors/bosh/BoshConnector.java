package tigase.jaxmpp.j2se.connectors.bosh;

import java.net.URL;

import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.connector.AbstractBoshConnector;
import tigase.jaxmpp.core.client.connector.BoshRequest;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.xml.DomBuilderHandler;
import tigase.xml.SimpleParser;
import tigase.xml.SingletonFactory;

public class BoshConnector extends AbstractBoshConnector {

	public static final String URL_KEY = "bosh#url";

	private final DomBuilderHandler domHandler = new DomBuilderHandler();

	private final SimpleParser parser = SingletonFactory.getParserInstance();

	@Override
	protected void processSendData(final Element element) throws XMLException, JaxmppException {
		BoshRequest worker = new BoshWorker(domHandler, parser, sessionObject, element) {

			@Override
			protected void onError(int responseCode, Element response, Throwable caught) {
				BoshConnector.this.onError(responseCode, response, caught);
			}

			@Override
			protected void onSuccess(int responseCode, Element response) throws JaxmppException {
				BoshConnector.this.onResponse(responseCode, response);
			}

			@Override
			protected void onTerminate(int responseCode, Element response) {
				BoshConnector.this.onTerminate(responseCode, response);
			}

		};

		addToRequests(worker);

		(new Thread(worker)).start();
	}

	@Override
	public void start(SessionObject sessionObject) throws XMLException, JaxmppException {
		try {
			String u = sessionObject.getProperty(AbstractBoshConnector.BOSH_SERVICE_URL);
			if (u == null)
				throw new JaxmppException("BOSH service URL not defined!");
			URL url = new URL(u);
			sessionObject.setProperty(URL_KEY, url);
			super.start(sessionObject);
		} catch (JaxmppException e) {
			throw e;
		} catch (Exception e) {
			throw new JaxmppException(e);
		}
	}
}