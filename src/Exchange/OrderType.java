package Exchange;

import java.util.Locale;

public enum OrderType
{
	MARKET,
	LIMIT,
	STOP;

	public static OrderType Get(String value)
	{
		return switch (value.toLowerCase(Locale.ROOT)) {
			case "market" -> MARKET;
			case "limit" -> LIMIT;
			case "stop" -> STOP;
			default -> null;
		};

	}
}
