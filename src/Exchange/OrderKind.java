package Exchange;

public enum OrderKind
{
	BID("bid"),
	ASK("ask");

	private final String _name;
	OrderKind(String name){ this._name = name; }

	public String GetName() { return _name; }

	public static OrderKind Get(String value)
	{
		OrderKind[] values = OrderKind.values();

		for (OrderKind orderKind : values)
		{
			if (orderKind.GetName().equals(value))
				return orderKind;
		}

		return null;
	}
}
