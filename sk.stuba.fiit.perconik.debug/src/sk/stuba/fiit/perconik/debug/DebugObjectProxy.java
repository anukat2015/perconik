package sk.stuba.fiit.perconik.debug;

import com.google.common.base.Preconditions;

public abstract class DebugObjectProxy<T> extends ForwardingDebugObject
{
	private final T object;
	
	protected DebugObjectProxy(final T object)
	{
		this(object, Debug.getDefaultConsole());
	}

	protected DebugObjectProxy(final T object, final DebugConsole console)
	{
		super(console);
		
		this.object = Preconditions.checkNotNull(object);
	}
	
	@Override
	protected final T delegate()
	{
		return this.object;
	}

	@Override
	public final boolean equals(final Object o)
	{
		return this.object.equals(o);
	}

	@Override
	public final int hashCode()
	{
		return this.object.hashCode();
	}
}