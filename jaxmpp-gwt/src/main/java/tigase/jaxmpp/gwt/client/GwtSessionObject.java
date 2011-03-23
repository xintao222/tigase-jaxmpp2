package tigase.jaxmpp.gwt.client;

import java.util.Map;

import tigase.jaxmpp.core.client.DefaultSessionObject;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.connector.AbstractBoshConnector;
import tigase.jaxmpp.core.client.xmpp.modules.ResourceBinderModule;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class GwtSessionObject extends DefaultSessionObject {

	public static final class RestoringSessionException extends Exception {
		public RestoringSessionException(String message) {
			super(message);
		}
	}

	private static JID getJID(JSONObject object, String key) {
		try {
			String v = getString(object, key);
			if (v != null)
				return JID.jidInstance(v);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Long getLong(JSONObject object, String key) {
		try {
			String v = getString(object, key);
			if (v != null)
				return Long.valueOf(v);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getString(JSONObject object, String key) {
		try {
			if (object.containsKey(key)) {
				String v = object.get(key).isString().stringValue();
				return v;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private CharSequence makeEntry(String jsonKey, String propsKey, Map<String, Object> map) {
		StringBuilder sb = new StringBuilder();

		Object v = map.get(propsKey);

		sb.append("\"").append(jsonKey).append("\"").append(":").append("\"").append(v == null ? "" : v).append("\"");

		return sb;
	}

	public void restore(final JSONValue value) throws RestoringSessionException {
		try {
			JSONObject object = value.isObject();

			String sid = getString(object, "sid");
			String authId = getString(object, "authid");
			Long rid = getLong(object, "rid");
			JID jid = getJID(object, "jid");
			String nick = getString(object, "nick");
			JID userJid = getJID(object, "userJid");
			String serverName = getString(object, "serverName");

			if (sid == null)
				throw new RestoringSessionException("sid is null");

			properties.put(AbstractBoshConnector.SID_KEY, sid);
			properties.put(AbstractBoshConnector.AUTHID_KEY, authId);
			properties.put(AbstractBoshConnector.RID_KEY, rid);
			properties.put(ResourceBinderModule.BINDED_RESOURCE_JID, jid);
			userProperties.put(NICKNAME, nick);
			userProperties.put(USER_JID, userJid);
			userProperties.put(SERVER_NAME, serverName);
		} catch (RestoringSessionException e) {
			throw e;
		} catch (Exception e) {
			throw new RestoringSessionException(e.getMessage());
		}
	}

	public String serialize() {
		StringBuilder sb = new StringBuilder();

		sb.append("{");

		// String
		sb.append(makeEntry("sid", AbstractBoshConnector.SID_KEY, properties)).append(",");
		// String
		sb.append(makeEntry("authid", AbstractBoshConnector.AUTHID_KEY, properties)).append(",");
		// Long
		sb.append(makeEntry("rid", AbstractBoshConnector.RID_KEY, properties)).append(",");
		// JID
		sb.append(makeEntry("jid", ResourceBinderModule.BINDED_RESOURCE_JID, properties)).append(",");
		// String
		sb.append(makeEntry("nick", NICKNAME, userProperties)).append(",");
		// JID
		sb.append(makeEntry("userJid", USER_JID, userProperties)).append(",");
		// Stri ng
		sb.append(makeEntry("serverName", SERVER_NAME, userProperties));

		sb.append("}");

		return sb.toString();
	}
}