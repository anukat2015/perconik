package sk.stuba.fiit.perconik.utilities;

import java.util.Comparator;
import com.google.common.base.Function;

/**
 * Static utility methods pertaining to {@code String} or {@code CharSequence}
 * instances.
 *
 * @author Pavol Zbell
 * @since 1.0
 */
public final class MoreStrings
{
	private MoreStrings()
	{
		throw new AssertionError();
	}

	private enum ToStringComparator implements Comparator<Object>
	{
		INSTANCE;
		
		public final int compare(Object a, Object b)
		{
			return a.toString().compareTo(b.toString());
		}
	}
	
	private enum ToLowerCaseFunction implements Function<String, String>
	{
		INSTANCE;

		public final String apply(String s)
		{
			return s.toLowerCase();
		}
	}

	private enum ToUpperCaseFunction implements Function<String, String>
	{
		INSTANCE;

		public final String apply(String s)
		{
			return s.toUpperCase();
		}
	}

	public static final String toDefaultString(Object o)
	{
		return o.getClass().getName() + "@" + Integer.toHexString(o.hashCode());
	}

	public static final String toCanonicalString(Object o)
	{
		String name = o.getClass().getCanonicalName();
		
		if (name == null)
		{
			return null;
		}
		
		return name + "@" + Integer.toHexString(o.hashCode());
	}
	
	public static final String toImplementedString(Object o)
	{
		String result = o.toString();
		
		if (toDefaultString(o).equals(result))
		{
			return null;
		}
		
		return result;
	}

	public static final <T> Comparator<T> toStringComparator()
	{
		@SuppressWarnings("unchecked")
		Comparator<T> comparator = (Comparator<T>) ToStringComparator.INSTANCE;
		
		return comparator;
	}
	
	public static final Function<String, String> toLowerCaseFunction()
	{
		return ToLowerCaseFunction.INSTANCE;
	}

	public static final Function<String, String> toUpperCaseFunction()
	{
		return ToUpperCaseFunction.INSTANCE;
	}
	
	public static final String toStringFallback(Object o)
	{
		String result = toImplementedString(o);

		if (result != null)
		{
			return result;
		}

		result = toCanonicalString(o);
		
		if (result == null)
		{
			return result;
		}

		return toDefaultString(o);
	}
}
