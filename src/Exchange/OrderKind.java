package Exchange;

import java.util.Locale;

public enum OrderKind
{
	BID("bid"),
	ASK("ask");

	private final String _name;
	OrderKind(String name){ this._name = name; }

	public String GetName() { return _name; }

	public static OrderKind Get(String value)
	{
		return switch (value.toLowerCase(Locale.ROOT)) {
			case "bid" -> BID;
			case "ask" -> ASK;
			default -> null;
		};
	}
}
