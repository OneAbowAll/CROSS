package Exchange;

public enum OrderKind
{
	BID("bid"),
	BUY("buy");

	private final String _name;
	OrderKind(String name){ this._name = name; }

	public String GetName() { return _name; }
}
