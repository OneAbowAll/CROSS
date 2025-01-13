package DataStructures;

import java.util.ArrayList;
import java.util.Objects;

public class Tuple<T extends Comparable<T>> implements Comparable<Tuple<T>>
{
	private final ArrayList<T> elements;

	public Tuple(T a, T b)
	{
		elements = new ArrayList<>(2);
		elements.add(a);
		elements.add(b);
	}

	public void Set(int i, T value) throws ArrayIndexOutOfBoundsException
	{
		if(i > 2 || i < 0) throw new ArrayIndexOutOfBoundsException();
		elements.set(i, value);
	}

	public T GetFirst()
	{
		return elements.getFirst();
	}

	public T GetLast()
	{
		return elements.getLast();
	}

	public T Get(int i) throws ArrayIndexOutOfBoundsException
	{
		if(i > 2 || i < 0) throw new ArrayIndexOutOfBoundsException();
		return elements.get(i);
	}

	@Override
	public int compareTo(Tuple<T> o)
	{
		for (int i = 0; i < elements.size(); i++)
		{
			int result = elements.get(i).compareTo(o.Get(i));
			if(result != 0)
			{
				return result;
			}
		}

		return 0;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tuple<?> tuple = (Tuple<?>) o;
		return Objects.equals(elements, tuple.elements);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(elements);
	}
}
