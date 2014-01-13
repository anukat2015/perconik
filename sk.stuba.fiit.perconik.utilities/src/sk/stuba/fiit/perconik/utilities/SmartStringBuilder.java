package sk.stuba.fiit.perconik.utilities;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import javax.annotation.Nullable;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Chars;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.UnsignedBytes;
import com.google.common.primitives.UnsignedInts;
import com.google.common.primitives.UnsignedLongs;

public final class SmartStringBuilder implements Appendable, CharSequence
{
	// TODO rename appendln
	// TODO add append(Class) use class.getCanonicalName instead of toString -> add strategy for that?
	// TODO add support for custom converters of specific types? (Class.toString vs Class.getCanonicalName)
	// TODO add support for CaseFormat
	// TODO add list(data, transform-function)
	// TODO add list(data, filter, transform-function)
	// TODO move to its own repo on github, include as submodule here + unit tests
	// TODO implement Serializable
	
	private final StringBuilder builder;

	final Options options;
	
	private int indent;
	
	private boolean line;
	
	public SmartStringBuilder()
	{
		this(new Options());
	}

	public SmartStringBuilder(int capacity)
	{
		this(new Options().initialCapacity(capacity));
	}
	
	public SmartStringBuilder(@Nullable CharSequence sequence)
	{
		this(new Options().initialValue(sequence));
	}
	
	public SmartStringBuilder(@Nullable String string)
	{
		this(new Options().initialValue(string));
	}
	
	public SmartStringBuilder(Options options)
	{
		this.builder = options.builder();
		this.options = options;
		this.indent  = 0;
		this.line    = true;
	}

	public static final SmartStringBuilder builder()
	{
		return new SmartStringBuilder();
	}

	public static final SmartStringBuilder builder(int capacity)
	{
		return new SmartStringBuilder(capacity);
	}

	public static final SmartStringBuilder builder(@Nullable CharSequence sequence)
	{
		return new SmartStringBuilder(sequence);
	}

	public static final SmartStringBuilder builder(@Nullable String string)
	{
		return new SmartStringBuilder(string);
	}
	
	public static final class Options
	{
		int initialCapacity = 16;

		CharSequence initialValue = "";

		String entrySeparator = ": ";
		
		String lineSeparator = System.lineSeparator();

		String lineRegex = "\r?\n|\r";

		String listSeparator = ", ";
		
		String nullValue = "null";
		
		int sizePrecision = 2;
		
		String sizeSeparator = " ";
		
		String valueFormat = "%.2f %s";
		
		CharSequence tab = "  ";

		Options()
		{
		}
		
		public static final Options of(SmartStringBuilder builder)
		{
			return builder.options;
		}

		// TODO
//		public static final Options from(Map<String, ?> map)
//		{
//			
//		}
//		
//		public final Options toMap()
//		{
//			
//		}
//		
//		@Override
//		public final String toString()
//		{
//			return this.toMap().toString();
//		}
		
		final StringBuilder builder()
		{
			return new StringBuilder(this.initialCapacity).append(this.initialValue);
		}

		public final Options initialCapacity(int value)
		{
			this.initialCapacity = Math.max(value, this.initialValue.length());
			
			return this;
		}

		public final Options initialValue(CharSequence value)
		{
			this.initialCapacity = Math.max(this.initialCapacity, value.length());
			this.initialValue    = value;
			
			return this;
		}
		
		public final Options initialValue(String value)
		{
			return this.initialValue((CharSequence) value);
		}
		
		public final Options entrySeparator(String value)
		{
			this.entrySeparator = checkNotNull(value);
			
			return this;
		}

		public final Options lineSeparator(String value)
		{
			checkArgument(!value.isEmpty());
			
			this.lineSeparator = value;
			
			return this;
		}

		public final Options lineRegex(String value)
		{
			checkArgument(!value.isEmpty());
			
			this.lineRegex = value;
			
			return this;
		}

		public final Options listSeparator(String value)
		{
			this.listSeparator = checkNotNull(value);
			
			return this;
		}
		
		public final Options nullValue(String value)
		{
			this.nullValue = checkNotNull(value);
			
			return this;
		}

		public final Options sizePrecision(int value)
		{
			this.sizePrecision = value;
			
			return this;
		}

		public final Options sizeSeparator(String value)
		{
			this.sizeSeparator = checkNotNull(value);
			
			return this;
		}

		public final Options valueFormat(String value)
		{
			checkArgument(!value.isEmpty());
			
			this.valueFormat = value;
			
			return this;
		}

		public final Options tab(CharSequence value)
		{
			this.tab = checkNotNull(value);
			
			return this;
		}
	}
	
	public final Options options()
	{
		return this.options;
	}

	public final void ensureCapacity(int minimum)
	{
		this.builder.ensureCapacity(minimum);
	}

	private final void ensureIndent()
	{
		if (this.line)
		{
			for (int i = 0; i < this.indent; i ++)
			{
				this.builder.append(this.options.tab);
			}
			
			this.line = false;
		}
	}

	public final int capacity()
	{
		return this.builder.capacity();
	}

	public final int length()
	{
		return this.builder.length();
	}
	
	public final int indent()
	{
		return this.indent;
	}
	
	public final SmartStringBuilder indent(int value)
	{
		this.setIndent(value);
		
		return this;
	}

	public final char charAt(int i)
	{
		return this.builder.charAt(i);
	}

	public final int codePointAt(int index)
	{
		return this.builder.codePointAt(index);
	}

	public final int codePointBefore(int index)
	{
		return this.builder.codePointBefore(index);
	}

	public final int codePointCount(int from, int to)
	{
		return this.builder.codePointCount(from, to);
	}

	public final SmartStringBuilder appendCodePoint(int value)
	{
		this.ensureIndent();
		this.builder.appendCodePoint(value);

		return this;
	}
	
	public final int offsetByCodePoints(int index, int offset)
	{
		return this.builder.offsetByCodePoints(index, offset);
	}

	public final int indexOf(String s)
	{
		return this.builder.indexOf(s);
	}

	public final int indexOf(String s, int from)
	{
		return this.builder.indexOf(s, from);
	}

	public final int lastIndexOf(String s)
	{
		return this.builder.lastIndexOf(s);
	}

	public final int lastIndexOf(String s, int from)
	{
		return this.builder.lastIndexOf(s, from);
	}

	public final CharSequence subSequence(int from, int to)
	{
		return this.builder.subSequence(from, to);
	}
	
	public final String substring(int offset)
	{
		return this.builder.substring(offset);
	}

	public final String subString(int from, int to)
	{
		return this.builder.substring(from, to);
	}
	
	public final SmartStringBuilder reverse()
	{
		this.builder.reverse();
		
		return this;
	}
	
	public final void trimToSize()
	{
		this.builder.trimToSize();
	}

	public final SmartStringBuilder truncate()
	{
		this.builder.setLength(0);
		
		this.line = true;
		
		return this;
	}

	public final String flush()
	{
		String content = this.builder.toString();
		
		this.truncate();
		
		return content;
	}

	public final void setLength(int value)
	{
		this.builder.setLength(value);
	}

	public final void setIndent(int value)
	{
		checkArgument(value >= 0);
		
		this.indent = value;
	}

	public final void setCharAt(int index, char c)
	{
		this.builder.setCharAt(index, c);
	}

	public final void getChars(int from, int to, char[] result, int offset)
	{
		this.builder.getChars(from, to, result, offset);
	}

	public final boolean isEmpty()
	{
		return this.builder.length() == 0;
	}

	public final boolean isNewLine()
	{
		return this.line;
	}

	public final SmartStringBuilder tab()
	{
		return this.tab(1);
	}

	public final SmartStringBuilder tab(int k)
	{
		int indent = this.indent + k;
		
		checkArgument(indent >= 0);
		
		this.indent = indent;
	
		return this;
	}

	public final SmartStringBuilder untab()
	{
		return this.tab(-1);
	}

	public final SmartStringBuilder untab(int k)
	{
		return this.tab(-k);
	}

	@Override
	public final String toString()
	{
		return this.builder.toString();
	}

	private final String toString(Object o)
	{
		return (o == null) ? this.options.nullValue : o.toString();
	}
	
	public final SmartStringBuilder append(@Nullable Object o)
	{
		this.ensureIndent();
		
		return this.append(this.toString(o));
	}

	public final SmartStringBuilder append(@Nullable String s)
	{
		this.ensureIndent();
		this.builder.append(this.toString(s));
		
		return this;
	}

	public final SmartStringBuilder append(@Nullable CharSequence s)
	{
		this.ensureIndent();
		this.builder.append(this.toString(s));
		
		return this;
	}
	
	public final SmartStringBuilder append(@Nullable CharSequence s, int from, int to)
	{
		this.ensureIndent();
		this.builder.append(this.toString(s), from, to);
		
		return this;
	}

	public final SmartStringBuilder append(boolean b)
	{
		this.ensureIndent();
		this.builder.append(b);
		
		return this;
	}

	public final SmartStringBuilder append(char c)
	{
		this.ensureIndent();
		this.builder.append(c);
		
		return this;
	}

	public final SmartStringBuilder append(char[] s)
	{
		this.ensureIndent();
		this.builder.append(s);
		
		return this;
	}
	
	public final SmartStringBuilder append(char[] s, int offset, int length)
	{
		this.ensureIndent();
		this.builder.append(s, offset, length);
		
		return this;
	}
	
	public final SmartStringBuilder append(int i)
	{
		this.ensureIndent();
		this.builder.append(i);
		
		return this;
	}

	public final SmartStringBuilder append(long l)
	{
		this.ensureIndent();
		this.builder.append(l);
		
		return this;
	}

	public final SmartStringBuilder append(float f)
	{
		this.ensureIndent();
		this.builder.append(f);
		
		return this;
	}

	public final SmartStringBuilder append(double d)
	{
		this.ensureIndent();
		this.builder.append(d);
		
		return this;
	}
	
	public final SmartStringBuilder appendln()
	{
		this.ensureIndent();
		this.builder.append(this.options.lineSeparator);
		
		this.line = true;
		
		return this;
	}
	
	public final SmartStringBuilder appendln(@Nullable Object o)
	{
		return this.appendln(this.toString(o));
	}

	public final SmartStringBuilder appendln(@Nullable String s)
	{
		this.ensureIndent();
		this.builder.append(this.toString(s));
		
		return this.appendln();
	}

	public final SmartStringBuilder appendln(@Nullable CharSequence s)
	{
		this.ensureIndent();
		this.builder.append(this.toString(s));
		
		return this.appendln();
	}
	
	public final SmartStringBuilder appendln(@Nullable CharSequence s, int from, int to)
	{
		this.ensureIndent();
		this.builder.append(this.toString(s), from, to);
		
		return this.appendln();
	}

	public final SmartStringBuilder appendln(boolean b)
	{
		this.ensureIndent();
		this.builder.append(b);
		
		return this.appendln();
	}

	public final SmartStringBuilder appendln(char c)
	{
		this.ensureIndent();
		this.builder.append(c);
		
		return this.appendln();
	}
	
	public final SmartStringBuilder appendln(char[] s)
	{
		this.ensureIndent();
		this.builder.append(s);
		
		return this.appendln();
	}
	
	public final SmartStringBuilder appendln(char[] s, int offset, int length)
	{
		this.ensureIndent();
		this.builder.append(s, offset, length);
		
		return this.appendln();
	}

	public final SmartStringBuilder appendln(int i)
	{
		this.ensureIndent();
		this.builder.append(i);
		
		return this.appendln();
	}

	public final SmartStringBuilder appendln(long l)
	{
		this.ensureIndent();
		this.builder.append(l);
		
		return this.appendln();
	}

	public final SmartStringBuilder appendln(float f)
	{
		this.ensureIndent();
		this.builder.append(f);
		
		return this.appendln();
	}

	public final SmartStringBuilder appendln(double d)
	{
		this.ensureIndent();
		this.builder.append(d);
		
		return this.appendln();
	}
	
	public final SmartStringBuilder delete(int from, int to)
	{
		this.builder.delete(from, to);
		
		return this;
	}

	public final SmartStringBuilder deleteCharAt(int index)
	{
		this.builder.deleteCharAt(index);
		
		return this;
	}

	public final SmartStringBuilder insert(int offset, @Nullable Object o)
	{
		this.builder.insert(offset, o);
		
		return this;
	}

	public final SmartStringBuilder insert(int offset, @Nullable String s)
	{
		this.builder.insert(offset, s);
		
		return this;
	}

	public final SmartStringBuilder insert(int offset, @Nullable CharSequence s)
	{
		s = this.toString(s);
		
		this.builder.insert(offset, s, 0, s.length());
		
		return this;
	}

	public final SmartStringBuilder insert(int offset, @Nullable CharSequence s, int from, int to)
	{
		this.builder.insert(offset, s, from, to);
		
		return this;
	}

	public final SmartStringBuilder insert(int offset, boolean b)
	{
		this.builder.insert(offset, b);
		
		return this;
	}

	public final SmartStringBuilder insert(int offset, char c)
	{
		this.builder.insert(offset, c);
		
		return this;
	}

	public final SmartStringBuilder insert(int offset, char[] s)
	{
		this.builder.insert(offset, s);
		
		return this;
	}

	public final SmartStringBuilder insert(int index, char[] s, int offset, int length)
	{
		this.builder.insert(index, s, offset, length);
		
		return this;
	}

	public final SmartStringBuilder insert(int offset, int i)
	{
		this.builder.insert(offset, i);
		
		return this;
	}

	public final SmartStringBuilder insert(int offset, long l)
	{
		this.builder.insert(offset, l);
		
		return this;
	}

	public final SmartStringBuilder insert(int offset, float f)
	{
		this.builder.insert(offset, f);
		
		return this;
	}

	public final SmartStringBuilder insert(int offset, double d)
	{
		this.builder.insert(offset, d);
		
		return this;
	}

	public final SmartStringBuilder replace(int from, int to, @Nullable String s)
	{
		this.builder.replace(from, to, this.toString(s));
		
		return this;
	}
	
	public final void replaceAt(int index, int length, @Nullable String replacement)
	{
		this.builder.replace(index, index + length, this.toString(replacement));
	}
	
	public final SmartStringBuilder replace(String source, @Nullable Object replacement)
	{
		return this.replace(source, this.toString(replacement));
	}

	public final SmartStringBuilder replace(String source, @Nullable String replacement)
	{
		int index;
		
		replacement = this.toString(replacement);
		
		while ((index = this.builder.indexOf(source)) != -1)
		{
			this.replace(index, index + source.length(), replacement);
		}
		
		return this;
	}

	public final SmartStringBuilder replace(String source, @Nullable CharSequence replacement)
	{
		return this.replace(source, this.toString(replacement));
	}

	public final SmartStringBuilder replace(String source, boolean replacement)
	{
		return this.replace(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replace(String source, char replacement)
	{
		return this.replace(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replace(String source, int replacement)
	{
		return this.replace(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replace(String source, long replacement)
	{
		return this.replace(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replace(String source, float replacement)
	{
		return this.replace(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replace(String source, double replacement)
	{
		return this.replace(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replaceFirst(String source, @Nullable Object replacement)
	{
		return this.replaceFirst(source, this.toString(replacement));
	}	

	public final SmartStringBuilder replaceFirst(String source, @Nullable String replacement)
	{
		int index = this.builder.indexOf(source);
		
		if (index != -1)
		{
			this.replaceAt(index, source.length(), this.toString(replacement));
		}
		
		return this;
	}
	
	public final SmartStringBuilder replaceFirst(String source, @Nullable CharSequence replacement)
	{
		return this.replaceFirst(source, this.toString(replacement));
	}

	public final SmartStringBuilder replaceFirst(String source, boolean replacement)
	{
		return this.replaceFirst(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replaceFirst(String source, char replacement)
	{
		return this.replaceFirst(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replaceFirst(String source, int replacement)
	{
		return this.replaceFirst(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replaceFirst(String source, long replacement)
	{
		return this.replaceFirst(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replaceFirst(String source, float replacement)
	{
		return this.replaceFirst(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replaceFirst(String source, double replacement)
	{
		return this.replaceFirst(source, String.valueOf(replacement));
	}
	
	public final SmartStringBuilder replaceLast(String source, @Nullable Object replacement)
	{
		return this.replaceLast(source, this.toString(replacement));
	}

	public final SmartStringBuilder replaceLast(String source, @Nullable String replacement)
	{
		int index = this.builder.lastIndexOf(source);
		
		if (index != -1)
		{
			this.replaceAt(index, source.length(), this.toString(replacement));
		}
		
		return this;
	}
	
	public final SmartStringBuilder replaceLast(String source, @Nullable CharSequence replacement)
	{
		return this.replaceLast(source, this.toString(replacement));
	}

	public final SmartStringBuilder replaceLast(String source, boolean replacement)
	{
		return this.replaceLast(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replaceLast(String source, char replacement)
	{
		return this.replaceLast(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replaceLast(String source, int replacement)
	{
		return this.replaceLast(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replaceLast(String source, long replacement)
	{
		return this.replaceLast(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replaceLast(String source, float replacement)
	{
		return this.replaceLast(source, String.valueOf(replacement));
	}

	public final SmartStringBuilder replaceLast(String source, double replacement)
	{
		return this.replaceLast(source, String.valueOf(replacement));
	}
	
	public final SmartStringBuilder repeat(int n, @Nullable Object o)
	{
		if (n < 0) throw new IllegalArgumentException();
		if (n > 0) this.ensureIndent();
		
		while ((n --) > 0) this.builder.append(this.toString(o));
	
		return this;
	}

	public final SmartStringBuilder repeat(int n, @Nullable String s)
	{
		if (n < 0) throw new IllegalArgumentException();
		if (n > 0) this.ensureIndent();
		
		while ((n --) > 0) this.builder.append(this.toString(s));
		
		return this;
	}

	public final SmartStringBuilder repeat(int n, @Nullable CharSequence s)
	{
		if (n < 0) throw new IllegalArgumentException();
		if (n > 0) this.ensureIndent();
		
		while ((n --) > 0) this.builder.append(this.toString(s));
		
		return this;
	}

	public final SmartStringBuilder repeat(int n, @Nullable CharSequence s, int from, int to)
	{
		if (n < 0) throw new IllegalArgumentException();
		if (n > 0) this.ensureIndent();
		
		while ((n --) > 0) this.builder.append(this.toString(s), from, to);
		
		return this;
	}

	public final SmartStringBuilder repeat(int n, boolean b)
	{
		if (n < 0) throw new IllegalArgumentException();
		if (n > 0) this.ensureIndent();
		
		while ((n --) > 0) this.builder.append(b);
		
		return this;
	}

	public final SmartStringBuilder repeat(int n, char c)
	{
		if (n < 0) throw new IllegalArgumentException();
		if (n > 0) this.ensureIndent();
		
		while ((n --) > 0) this.builder.append(c);
		
		return this;
	}

	public final SmartStringBuilder repeat(int n, int i)
	{
		if (n < 0) throw new IllegalArgumentException();
		if (n > 0) this.ensureIndent();
		
		while ((n --) > 0) this.builder.append(i);
		
		return this;
	}

	public final SmartStringBuilder repeat(int n, long l)
	{
		if (n < 0) throw new IllegalArgumentException();
		if (n > 0) this.ensureIndent();
		
		while ((n --) > 0) this.builder.append(l);
		
		return this;
	}

	public final SmartStringBuilder repeat(int n, float f)
	{
		if (n < 0) throw new IllegalArgumentException();
		if (n > 0) this.ensureIndent();
		
		while ((n --) > 0) this.builder.append(f);
		
		return this;
	}

	public final SmartStringBuilder repeat(int n, double d)
	{
		if (n < 0) throw new IllegalArgumentException();
		if (n > 0) this.ensureIndent();
		
		while ((n --) > 0) this.builder.append(d);
		
		return this;
	}

	public final SmartStringBuilder repeat(int n, String format, Object ... args)
	{
		this.repeat(n, String.format(format, args));
		
		return this;
	}

	public final SmartStringBuilder format(String format, Object ... args)
	{
		this.ensureIndent();		
		this.builder.append(String.format(format, args));
		
		return this;
	}

	public final SmartStringBuilder list(@Nullable Object first, @Nullable Object second, Object ... rest)
	{
		return this.list(Lists.asList(first, second, rest));
	}
	
	public final SmartStringBuilder list(Object[] values)
	{
		return this.list(Arrays.asList(values));
	}

	public final SmartStringBuilder list(Iterable<?> values)
	{
		return this.list(values.iterator());
	}
	
	public final SmartStringBuilder list(Iterator<?> values)
	{
		return this.list(values, this.options.listSeparator);
	}

	// TODO review list methods, add more signatures like:
//	public final SmartStringBuilder list(@Nullable Object first, @Nullable Object second, Object ... rest)
//	public final SmartStringBuilder list(Object[] values)
//	public final SmartStringBuilder list(Iterable<?> values)
//	public final SmartStringBuilder list(Iterator<?> values)

	// TODO decide if separators are first or last parameters
	
	public final SmartStringBuilder list(Iterable<?> values, String separator)
	{
		return this.list(values.iterator(), separator);
	}
	
	public final SmartStringBuilder list(Iterator<?> values, String separator)
	{
		checkNotNull(separator);
		
		if (values.hasNext())
		{
			this.ensureIndent();
			this.builder.append(this.toString(values.next()));
			
			while (values.hasNext())
			{
				this.builder.append(separator);
				this.builder.append(this.toString(values.next()));
			}
		}
		
		return this;
	}

	public final SmartStringBuilder list(Iterable<?> values, Joiner joiner)
	{
		return this.append(joiner.join(values));
	}
	
	public final SmartStringBuilder list(Iterator<?> values, Joiner joiner)
	{
		return this.append(joiner.join(values));
	}

	public final SmartStringBuilder list(Map<?, ?> values, String listSeparator, String entrySeparator)
	{
		return this.list(values, listSeparator, entrySeparator);
	}

	public final SmartStringBuilder list(Properties values, String listSeparator, String entrySeparator)
	{
		return this.list(Maps.fromProperties(values), listSeparator, entrySeparator);
	}
	
	public final SmartStringBuilder list(Iterable<? extends Entry<?, ?>> values, String listSeparator, String entrySeparator)
	{
		return this.list(values.iterator(), listSeparator, entrySeparator);
	}
	
	public final SmartStringBuilder list(Iterator<? extends Entry<?, ?>> values, String listSeparator, String entrySeparator)
	{
		checkNotNull(listSeparator);
		checkNotNull(entrySeparator);
		
		if (values.hasNext())
		{
			Entry<?, ?> entry = values.next();
			
			this.ensureIndent();
			this.builder.append(this.toString(entry.getKey()));
			this.builder.append(entrySeparator);
			this.builder.append(this.toString(entry.getValue()));
			
			while (values.hasNext())
			{
				this.builder.append(listSeparator);
				
				Entry<?, ?> other = values.next();
				
				this.builder.append(this.toString(other.getKey()));
				this.builder.append(entrySeparator);
				this.builder.append(this.toString(other.getValue()));
			}
		}
		
		return this;
	}

	public final SmartStringBuilder list(Map<?, ?> values, MapJoiner joiner)
	{
		return this.append(joiner.join(values));
	}

	public final SmartStringBuilder list(boolean ... values)
	{
		return this.list(this.options.listSeparator, values);
	}

	public final SmartStringBuilder list(char ... values)
	{
		return this.list(this.options.listSeparator, values);
	}

	public final SmartStringBuilder list(int ... values)
	{
		return this.list(this.options.listSeparator, values);
	}

	public final SmartStringBuilder list(long ... values)
	{
		return this.list(this.options.listSeparator, values);
	}

	public final SmartStringBuilder list(float ... values)
	{
		return this.list(this.options.listSeparator, values);
	}

	public final SmartStringBuilder list(double ... values)
	{
		return this.list(this.options.listSeparator, values);
	}

	public final SmartStringBuilder list(String separator, boolean ... values)
	{
		return this.list(Booleans.asList(values));
	}

	public final SmartStringBuilder list(String separator, char ... values)
	{
		return this.list(Chars.asList(values));
	}

	public final SmartStringBuilder list(String separator, int ... values)
	{
		return this.list(Ints.asList(values));
	}

	public final SmartStringBuilder list(String separator, long ... values)
	{
		return this.list(Longs.asList(values));
	}

	public final SmartStringBuilder list(String separator, float ... values)
	{
		return this.list(Floats.asList(values));
	}

	public final SmartStringBuilder list(String separator, double ... values)
	{
		return this.list(Doubles.asList(values));
	}

	private static enum NotEmptyPredicate implements Predicate<CharSequence>
	{
		INSTANCE;

		public final boolean apply(CharSequence s)
		{
			return s.length() != 0;
		}
	}
	
	public final SmartStringBuilder listNonNull(Object first, Object second, Object ... rest)
	{
		return this.filteredList(Lists.asList(first, second, rest), Predicates.notNull());
	}
	
	public final SmartStringBuilder listNonEmpty(String first, String second, String ... rest)
	{
		return this.listNonEmpty((CharSequence) first, second, rest);
	}
	
	public final SmartStringBuilder listNonEmpty(CharSequence first, CharSequence second, CharSequence ... rest)
	{
		return this.filteredList(Lists.asList(first, second, rest), NotEmptyPredicate.INSTANCE);
	}

	public final <T> SmartStringBuilder filteredList(Iterable<T> values, Predicate<? super T> filter)
	{
		return this.filteredList(values.iterator(), filter);
	}

	public final <T> SmartStringBuilder filteredList(Iterator<T> values, Predicate<? super T> filter)
	{
		// TODO
		return this;
	}

	public final <T extends Comparable<? super T>> SmartStringBuilder sortedList(Iterable<T> values)
	{
		Object[] array = Iterables.toArray(values, Object.class);
		
		Arrays.sort(array);
		
		this.list(array);
		
		return this;
	}

	public final <T> SmartStringBuilder sortedList(Iterable<T> values, Comparator<? super T> comparator)
	{
		T[] array = (T[]) Iterables.toArray(values, Object.class);
		
		Arrays.sort(array, comparator);
		
		this.list(array);
		
		return this;
	}
	
	// TODO
	public final SmartStringBuilder splitList(Object value, Splitter splitter, Joiner joiner)
	{
		return this.splitList(value.toString(), splitter, joiner);
	}

	public final SmartStringBuilder splitList(String value, Splitter splitter, Joiner joiner)
	{
		return this.splitList((CharSequence) value, splitter, joiner);
	}

	public final SmartStringBuilder splitList(CharSequence value, Splitter splitter, Joiner joiner)
	{
		return this.append(joiner.join(splitter.splitToList(value)));
	}

	public final SmartStringBuilder splitList(Object value, MapSplitter splitter, MapJoiner joiner)
	{
		return this.splitList(value.toString(), splitter, joiner);
	}

	public final SmartStringBuilder splitList(String value, MapSplitter splitter, MapJoiner joiner)
	{
		return this.splitList((CharSequence) value, splitter, joiner);
	}

	public final SmartStringBuilder splitList(CharSequence value, MapSplitter splitter, MapJoiner joiner)
	{
		return this.append(joiner.join(splitter.split(value)));
	}

	public final SmartStringBuilder lines(@Nullable Object o)
	{
		return this.lines(this.toString(o));
	}

	public final SmartStringBuilder lines(@Nullable String s)
	{
		String[] lines = this.toString(s).split(this.options.lineRegex);
	
		int last = lines.length - 1;
	
		for (int i = 0; i < last; i ++)
		{
			this.appendln(lines[i]);
		}

		if (s.endsWith("\r") || s.endsWith("\n"))
		{
			this.appendln(lines[last]);
		}
		else
		{
			this.append(lines[last]);
		}
	
		return this;
	}
	
	public final SmartStringBuilder lines(@Nullable CharSequence s)
	{
		return this.lines(s.toString());
	}

	public final SmartStringBuilder signed(byte i)
	{
		return this.append(Integer.toString(i));
	}

	public final SmartStringBuilder signed(byte i, int radix)
	{
		return this.append(Integer.toString(i, radix));
	}

	public final SmartStringBuilder signed(short i)
	{
		return this.append(Integer.toString(i));
	}

	public final SmartStringBuilder signed(short i, int radix)
	{
		return this.append(Integer.toString(i, radix));
	}

	public final SmartStringBuilder signed(int i)
	{
		return this.append(Integer.toString(i));
	}

	public final SmartStringBuilder signed(int i, int radix)
	{
		return this.append(Integer.toString(i, radix));
	}

	public final SmartStringBuilder signed(long i)
	{
		return this.append(Long.toString(i));
	}

	public final SmartStringBuilder signed(long i, int radix)
	{
		return this.append(Long.toString(i, radix));
	}
	
	public final SmartStringBuilder unsigned(byte i)
	{
		return this.append(UnsignedBytes.toString(i));
	}

	public final SmartStringBuilder unsigned(byte i, int radix)
	{
		return this.append(UnsignedBytes.toString(i, radix));
	}

	public final SmartStringBuilder unsigned(short i)
	{
		return this.append(UnsignedInts.toString(i));
	}

	public final SmartStringBuilder unsigned(short i, int radix)
	{
		return this.append(UnsignedInts.toString(i, radix));
	}

	public final SmartStringBuilder unsigned(int i)
	{
		return this.append(UnsignedInts.toString(i));
	}

	public final SmartStringBuilder unsigned(int i, int radix)
	{
		return this.append(UnsignedInts.toString(i, radix));
	}

	public final SmartStringBuilder unsigned(long i)
	{
		return this.append(UnsignedLongs.toString(i));
	}

	public final SmartStringBuilder unsigned(long i, int radix)
	{
		return this.append(UnsignedLongs.toString(i, radix));
	}

	private final SmartStringBuilder appendValue(String format, long value, int base, Object[] symbols, int exponent)
	{
		assert symbols != null;
		
		if (value < 0 || base <= 0 || exponent >= symbols.length)
		{
			throw new IllegalArgumentException();
		}
		
		if (exponent < 0)
		{
			exponent = (int) (Math.log(value) / Math.log(base));
		}
		
	    if (value < base)
	    {
	    	this.ensureIndent();
	    	this.builder.append(value).append(' ').append(symbols[0]);
	    	
	    	return this;
	    }
	
	    this.ensureIndent();
	    this.builder.append(String.format(format, value / Math.pow(base, exponent), symbols[exponent]));
	    
	    return this;
	}

	public final SmartStringBuilder value(long value, int base, Object ... units)
	{
		return this.appendValue(this.options.valueFormat, value, base, units, -1);
	}

	public final <U> SmartStringBuilder value(long value, int base, Iterable<U> units)
	{
		return this.value(value, base, Iterables.toArray(units, Object.class));
	}

	public final SmartStringBuilder value(long value, ValueOptions options)
	{
		options.validate();
		
		return this.appendValue(options.format, value, options.base, options.units.toArray(), options.unit);
	}
	
	public static final class ValueOptions
	{
		String format;
		
		int base;
		
		List<?> units;
	
		int unit;
		
		public ValueOptions()
		{
			this.format = "%.2f %s";
			this.base   = 0;
			this.unit   = -1;
			this.units  = null;
		}
		
		public final ValueOptions format(String value)
		{
			checkArgument(!value.isEmpty());
			
			this.format = value;
			
			return this;
		}
	
		public final ValueOptions base(int value)
		{
			checkArgument(value > 0);
			
			this.base = value;
			
			return this;
		}
		
		public final ValueOptions units(Object ... values)
		{
			checkArgument(values.length > 0);
			
			this.units = Lists.newArrayList(values);
			this.unit  = -1;
			
			return this;
		}
	
		public final ValueOptions units(Iterable<?> values)
		{
			return this.units(Iterables.toArray(values, Object.class));
		}
	
		public final ValueOptions unit(int value)
		{
			checkElementIndex(value, this.units.size());
			
			this.unit = value;
			
			return this;
		}
		
		public final ValueOptions unit(Object value)
		{
			return this.unit(this.units.indexOf(value));
		}
	
		final void validate()
		{
			checkState(this.base != 0);
			checkState(this.units != null);
		}
	}
	
	public static final ValueOptions units(Object ... units)
	{
		return new ValueOptions().units(units);
	}
	
	public static final ValueOptions units(Iterable<?> units)
	{
		return new ValueOptions().units(units);
	}

	private static final String[] bits = {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

	private static final String[] bytes = {"B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"};

	public final SmartStringBuilder bits(long value)
	{
		return this.bits(value, this.options.sizePrecision);
	}

	public final SmartStringBuilder bits(long value, int precision)
	{
		return this.appendValue("%." + precision + "f %s", value, 1000, SmartStringBuilder.bits, -1);
	}

	public final SmartStringBuilder bytes(long value)
	{
		return this.bytes(value, this.options.sizePrecision);
	}

	public final SmartStringBuilder bytes(long value, int precision)
	{
		return this.appendValue("%." + precision + "f %s", value, 1024, SmartStringBuilder.bytes, -1);
	}

	private final static char[] digits = new char[]
	{
		'0', '1', '2', '3',
		'4', '5', '6', '7',
		'8', '9', 'a', 'b',
		'c', 'd', 'e', 'f'
	};

	private final SmartStringBuilder appendBytes(byte[] values, int size, int shift, String separator)
	{
		final int length = values.length;
	
		if (length == 0)
		{
			return this;
		}
		
		this.ensureIndent();
		this.appendByte(values[0], size, shift);
		
		for (int i = 1; i < length; i ++)
		{
			this.builder.append(separator);
			this.appendByte(values[i], size, shift);
		}
		
		return this;
	}

	private final SmartStringBuilder appendByte(int value, int size, int shift)
	{
		char[] buffer = new char[size];

		int index = size;
		int radix = 1 << shift;
		int mask  = radix - 1;

		while (index != 0)
		{
			buffer[-- index] = digits[value & mask];

			value >>>= shift;
		}
		
		this.builder.append(buffer);
		
		return this;
	}

	public final SmartStringBuilder bin(byte ... bytes)
	{
		return this.bin(this.options.sizeSeparator, bytes);
	}
	
	public final SmartStringBuilder bin(String separator, byte ... bytes)
	{
		return this.appendBytes(bytes, 8, 1, separator);
	}
	
	public final SmartStringBuilder hex(byte ... bytes)
	{
		return this.hex(this.options.sizeSeparator, bytes);
	}

	public final SmartStringBuilder hex(String separator, byte ... bytes)
	{
		return this.appendBytes(bytes, 2, 4, separator);
	}
	
	private static final byte[] to8(byte a)
	{
		return new byte[] {a};
	}
	
	private static final byte[] to8(short a, int shift)
	{
		return new byte[] {((byte) ((a >> shift) & 0xff))};
	}
	
	private static final byte[] to8(int a, int shift)
	{
		return new byte[] {((byte) ((a >> shift) & 0xff))};
	}
	
	private static final byte[] to8(long a, int shift)
	{
		return new byte[] {((byte) ((a >> shift) & 0xff))};
	}

	private static final byte[] to16(byte a, byte b)
	{
		return new byte[] {a, b};
	}
	
	private static final byte[] to16(short a)
	{
		return new byte[] {(byte) (a >> 8), (byte) a};
	}
	
	private static final byte[] to16(int a, int shift)
	{
		return new byte[] {((byte) ((a >> (shift + 8)) & 0xff)),
		                   ((byte) ((a >> (shift    )) & 0xff))};
	}

	private static final byte[] to16(long a, int shift)
	{
		return new byte[] {((byte) ((a >> (shift + 8)) & 0xff)),
		                   ((byte) ((a >> (shift    )) & 0xff))};
	}

	private static final byte[] to32(byte a, byte b, byte c, byte d)
	{
		return new byte[] {a, b, c, d};
	}
	
	private static final byte[] to32(short a, short b)
	{
		return new byte[] {(byte) (a >> 8), (byte) a,
		                   (byte) (b >> 8), (byte) b};
	}
	
	private static final byte[] to32(int a)
	{
		return new byte[] {(byte) (a >> 24), (byte) (a >> 16),
		                   (byte) (a >>  8), (byte)  a       };
	}
	
	private static final byte[] to32(long a, int shift)
	{
		return new byte[] {((byte) ((a >> (shift + 24)) & 0xff)),
		                   ((byte) ((a >> (shift + 16)) & 0xff)),
		                   ((byte) ((a >> (shift +  8)) & 0xff)),
		                   ((byte) ((a >> (shift     )) & 0xff))};
	}

	private static final byte[] to48(byte a, byte b, byte c, byte d, byte e, byte f)
	{
		return new byte[] {a, b, c, d, e, f};
	}
	
	private static final byte[] to48(short a, short b, short c)
	{
		return new byte[] {(byte) (a >> 8), (byte) a,
                           (byte) (b >> 8), (byte) b,
                           (byte) (c >> 8), (byte) c};
	}

	private static final byte[] to64(byte a, byte b, byte c, byte d, byte e, byte f, byte g, byte h)
	{
		return new byte[] {a, b, c, d, e, f, g, h};
	}
	
	private static final byte[] to64(short a, short b, short c, short d)
	{
		return new byte[] {(byte) (a >> 8), (byte) a,
					       (byte) (b >> 8), (byte) b,
					       (byte) (c >> 8), (byte) c,
					       (byte) (d >> 8), (byte) d};
	}
	
	private static final byte[] to64(int a, int b)
	{
		return new byte[] {(byte) (a >> 24), (byte) (a >> 16),
                           (byte) (a >>  8), (byte)  a       ,
                           (byte) (b >> 24), (byte) (b >> 16),
                           (byte) (b >>  8), (byte)  b       };
	}
	
	private static final byte[] to64(long a)
	{
		return new byte[] {(byte) (a >> 56), (byte) (a >> 48),
		                   (byte) (a >> 40), (byte) (a >> 32),
		                   (byte) (a >> 24), (byte) (a >> 16),
		                   (byte) (a >>  8), (byte)  a       };
	}
	
	private static final byte[] to96(byte a, byte b, byte c, byte d, byte e, byte f, byte g, byte h, byte i, byte j, byte k, byte l)
	{
		return new byte[] {a, b, c, d, e, f, g, h, i, j, k, l};
	}

	private static final byte[] to96(short a, short b, short c, short d, short e, short f)
	{
		return new byte[] {(byte) (a >> 8), (byte) a,
		                   (byte) (b >> 8), (byte) b,
		                   (byte) (c >> 8), (byte) c,
		                   (byte) (d >> 8), (byte) d,
		                   (byte) (e >> 8), (byte) e,
		                   (byte) (f >> 8), (byte) f};
	}

	private static final byte[] to96(int a, int b, int c)
	{
		return new byte[] {(byte) (a >> 24), (byte) (a >> 16),
		                   (byte) (a >>  8), (byte)  a       ,
		                   (byte) (b >> 24), (byte) (b >> 16),
		                   (byte) (b >>  8), (byte)  b       ,
		                   (byte) (c >> 24), (byte) (c >> 16),
		                   (byte) (c >>  8), (byte)  c       };
	}

	private static final byte[] to128(byte a, byte b, byte c, byte d, byte e, byte f, byte g, byte h, byte i, byte j, byte k, byte l, byte m, byte n, byte o, byte p)
	{
		return new byte[] {a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p};
	}

	private static final byte[] to128(short a, short b, short c, short d, short e, short f, short g, short h)
	{
		return new byte[] {(byte) (a >> 8), (byte) a,
		                   (byte) (b >> 8), (byte) b,
		                   (byte) (c >> 8), (byte) c,
		                   (byte) (d >> 8), (byte) d,
		                   (byte) (e >> 8), (byte) e,
		                   (byte) (f >> 8), (byte) f,
		                   (byte) (g >> 8), (byte) g,
		                   (byte) (h >> 8), (byte) h};
	}

	private static final byte[] to128(int a, int b, int c, int d)
	{
		return new byte[] {(byte) (a >> 24), (byte) (a >> 16),
		                   (byte) (a >>  8), (byte)  a       ,
		                   (byte) (b >> 24), (byte) (b >> 16),
		                   (byte) (b >>  8), (byte)  b       ,
		                   (byte) (c >> 24), (byte) (c >> 16),
		                   (byte) (c >>  8), (byte)  c       ,
		                   (byte) (d >> 24), (byte) (d >> 16),
		                   (byte) (d >>  8), (byte)  d       };
	}

	private static final byte[] to128(long a, long b)
	{
		return new byte[] {(byte) (a >> 56), (byte) (a >> 48),
		                   (byte) (a >> 40), (byte) (a >> 32),
		                   (byte) (a >> 24), (byte) (a >> 16),
		                   (byte) (a >>  8), (byte)  a       ,
		                   (byte) (b >> 56), (byte) (b >> 48),
		                   (byte) (b >> 40), (byte) (b >> 32),
		                   (byte) (b >> 24), (byte) (b >> 16),
		                   (byte) (b >>  8), (byte)  b       };
	}
	
	public final SmartStringBuilder bin8(byte a)
	{
		return this.bin(to8(a));
	}
	
	public final SmartStringBuilder bin8(short a, int shift)
	{
		return this.bin(to8(a, shift));
	}
	
	public final SmartStringBuilder bin8(int a, int shift)
	{
		return this.bin(to8(a, shift));
	}
	
	public final SmartStringBuilder bin8(long a, int shift)
	{
		return this.bin(to8(a, shift));
	}

	public final SmartStringBuilder bin16(byte a, byte b)
	{
		return this.bin(to16(a, b));
	}
	
	public final SmartStringBuilder bin16(short a)
	{
		return this.bin(to16(a));
	}
	
	public final SmartStringBuilder bin16(int a, int shift)
	{
		return this.bin(to16(a, shift));
	}

	public final SmartStringBuilder bin16(long a, int shift)
	{
		return this.bin(to16(a, shift));

	}

	public final SmartStringBuilder bin32(byte a, byte b, byte c, byte d)
	{
		return this.bin(to32(a, b, c, d));

	}
	
	public final SmartStringBuilder bin32(short a, short b)
	{
		return this.bin(to32(a, b));
	}
	
	public final SmartStringBuilder bin32(int a)
	{
		return this.bin(to32(a));
	}
	
	public final SmartStringBuilder bin32(long a, int shift)
	{
		return this.bin(to32(a, shift));
	}

	public final SmartStringBuilder bin48(byte a, byte b, byte c, byte d, byte e, byte f)
	{
		return this.bin(to48(a, b, c, d, e, f));
	}
	
	public final SmartStringBuilder bin48(short a, short b, short c)
	{
		return this.bin(to48(a, b, c));
	}

	public final SmartStringBuilder bin64(byte a, byte b, byte c, byte d, byte e, byte f, byte g, byte h)
	{
		return this.bin(to64(a, b, c, d, e, f, g, h));
	}
	
	public final SmartStringBuilder bin64(short a, short b, short c, short d)
	{
		return this.bin(to64(a, b, c, d));
	}
	
	public final SmartStringBuilder bin64(int a, int b)
	{
		return this.bin(to64(a, b));
	}
	
	public final SmartStringBuilder bin64(long a)
	{
		return this.bin(to64(a));
	}
	
	public final SmartStringBuilder bin96(byte a, byte b, byte c, byte d, byte e, byte f, byte g, byte h, byte i, byte j, byte k, byte l)
	{
		return this.bin(to96(a, b, c, d, e, f, g, h, i, j, k, l));
	}

	public final SmartStringBuilder bin96(short a, short b, short c, short d, short e, short f)
	{
		return this.bin(to96(a, b, c, d, e, f));
	}

	public final SmartStringBuilder bin96(int a, int b, int c)
	{
		return this.bin(to96(a, b, c));
	}

	public final SmartStringBuilder bin128(byte a, byte b, byte c, byte d, byte e, byte f, byte g, byte h, byte i, byte j, byte k, byte l, byte m, byte n, byte o, byte p)
	{
		return this.bin(to128(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p));
	}

	public final SmartStringBuilder bin128(short a, short b, short c, short d, short e, short f, short g, short h)
	{
		return this.bin(to128(a, b, c, d, e, f, g, h));
	}

	public final SmartStringBuilder bin128(int a, int b, int c, int d)
	{
		return this.bin(to128(a, b, c, d));
	}

	public final SmartStringBuilder bin128(long a, long b)
	{
		return this.bin(to128(a, b));
	}
	
	public final SmartStringBuilder hex8(byte a)
	{
		return this.hex(to8(a));
	}
	
	public final SmartStringBuilder hex8(short a, int shift)
	{
		return this.hex(to8(a, shift));
	}
	
	public final SmartStringBuilder hex8(int a, int shift)
	{
		return this.hex(to8(a, shift));
	}
	
	public final SmartStringBuilder hex8(long a, int shift)
	{
		return this.hex(to8(a, shift));
	}

	public final SmartStringBuilder hex16(byte a, byte b)
	{
		return this.hex(to16(a, b));
	}
	
	public final SmartStringBuilder hex16(short a)
	{
		return this.hex(to16(a));
	}
	
	public final SmartStringBuilder hex16(int a, int shift)
	{
		return this.hex(to16(a, shift));
	}

	public final SmartStringBuilder hex16(long a, int shift)
	{
		return this.hex(to16(a, shift));

	}

	public final SmartStringBuilder hex32(byte a, byte b, byte c, byte d)
	{
		return this.hex(to32(a, b, c, d));

	}
	
	public final SmartStringBuilder hex32(short a, short b)
	{
		return this.hex(to32(a, b));
	}
	
	public final SmartStringBuilder hex32(int a)
	{
		return this.hex(to32(a));
	}
	
	public final SmartStringBuilder hex32(long a, int shift)
	{
		return this.hex(to32(a, shift));
	}

	public final SmartStringBuilder hex48(byte a, byte b, byte c, byte d, byte e, byte f)
	{
		return this.hex(to48(a, b, c, d, e, f));
	}
	
	public final SmartStringBuilder hex48(short a, short b, short c)
	{
		return this.hex(to48(a, b, c));
	}

	public final SmartStringBuilder hex64(byte a, byte b, byte c, byte d, byte e, byte f, byte g, byte h)
	{
		return this.hex(to64(a, b, c, d, e, f, g, h));
	}
	
	public final SmartStringBuilder hex64(short a, short b, short c, short d)
	{
		return this.hex(to64(a, b, c, d));
	}
	
	public final SmartStringBuilder hex64(int a, int b)
	{
		return this.hex(to64(a, b));
	}
	
	public final SmartStringBuilder hex64(long a)
	{
		return this.hex(to64(a));
	}
	
	public final SmartStringBuilder hex96(byte a, byte b, byte c, byte d, byte e, byte f, byte g, byte h, byte i, byte j, byte k, byte l)
	{
		return this.hex(to96(a, b, c, d, e, f, g, h, i, j, k, l));
	}

	public final SmartStringBuilder hex96(short a, short b, short c, short d, short e, short f)
	{
		return this.hex(to96(a, b, c, d, e, f));
	}

	public final SmartStringBuilder hex96(int a, int b, int c)
	{
		return this.hex(to96(a, b, c));
	}

	public final SmartStringBuilder hex128(byte a, byte b, byte c, byte d, byte e, byte f, byte g, byte h, byte i, byte j, byte k, byte l, byte m, byte n, byte o, byte p)
	{
		return this.hex(to128(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p));
	}

	public final SmartStringBuilder hex128(short a, short b, short c, short d, short e, short f, short g, short h)
	{
		return this.hex(to128(a, b, c, d, e, f, g, h));
	}

	public final SmartStringBuilder hex128(int a, int b, int c, int d)
	{
		return this.hex(to128(a, b, c, d));
	}

	public final SmartStringBuilder hex128(long a, long b)
	{
		return this.hex(to128(a, b));
	}
}
