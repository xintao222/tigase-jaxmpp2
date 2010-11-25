package tigase.jaxmpp.gwt.client.xml;

import java.util.List;

import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;

import com.google.gwt.junit.client.GWTTestCase;

public class GwtElementTest extends GWTTestCase {

	private static final Element createElement() throws XMLException {
		return GwtElement.parse("<message to=\"romeo@example.net\" from=\"juliet@example.com/balcony\" type=\"chat\"><subject>I implore you!</subject><body>Wherefore art thou, Romeo?</body><thread>e0ffe42b28561960c6b12b944a092794b9683a38</thread><x xmlns=\"tigase\">tigase:offline</x></message>");
	}

	@Override
	public String getModuleName() {
		return "tigase.jaxmpp.gwt.JaxmppGWTJUnit";
	}

	public void test01() throws Exception {
		GwtElement x = GwtElement.parse("<iq to='x@y.z'><query/></iq>");
		assertEquals("iq", x.getName());
		assertEquals("x@y.z", x.getAttribute("to"));
	}

	public void testGetAttribute() throws XMLException {
		final Element element = createElement();

		assertEquals("juliet@example.com/balcony", element.getAttribute("from"));
		assertEquals("romeo@example.net", element.getAttribute("to"));
	}

	public void testGetChildren() throws XMLException {
		final Element element = createElement();
		assertEquals("message", element.getName());

		List<Element> c = element.getChildren("subject");
		assertEquals(1, c.size());
		Element e = c.get(0);
		assertEquals("subject", e.getName());

		c = element.getChildrenNS("tigase");
		assertEquals(1, c.size());
		e = c.get(0);
		assertEquals("x", e.getName());

		c = element.getChildrenNS("x", "tigase");
		assertEquals(1, c.size());
		e = c.get(0);
		assertEquals("x", e.getName());

	}

	public void testGetChildrenAfter() throws XMLException {
		final Element element = createElement();

		List<Element> c = element.getChildren("subject");
		assertEquals(1, c.size());
		Element e = c.get(0);
		Element body = element.getChildAfter(e);
		assertEquals("body", body.getName());
		assertEquals("Wherefore art thou, Romeo?", body.getValue());
	}

	public void testGetFirstChild() throws XMLException {
		final Element element = createElement();
		Element fc = element.getFirstChild();

		assertEquals("subject", fc.getName());
	}

	public void testGetNextSibling() throws XMLException {
		final Element element = createElement();

		List<Element> c = element.getChildren("subject");
		assertEquals(1, c.size());
		Element e = c.get(0);
		Element body = e.getNextSibling();
		assertEquals("body", body.getName());
		assertEquals("Wherefore art thou, Romeo?", body.getValue());
	}

	public void testGetXMLNS() throws XMLException {
		final Element element = createElement();

		assertNull(element.getXMLNS());

		List<Element> c = element.getChildrenNS("x", "tigase");
		assertEquals(1, c.size());
		Element e = c.get(0);
		assertEquals("x", e.getName());
		assertEquals("tigase", e.getXMLNS());

	}

}