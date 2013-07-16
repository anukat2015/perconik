package sk.stuba.fiit.perconik.listeners;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import com.google.common.base.Preconditions;

public class Synchronized
{
	private Synchronized()
	{
		throw new AssertionError();
	}
	
	private static class SynchronizedObject<T> implements Serializable
	{
		private static final long serialVersionUID = -4544158938099292339L;

		final T delegate;
		
		final Object mutex;

		SynchronizedObject(final T delegate, final Object mutex)
		{
			this.delegate = Preconditions.checkNotNull(delegate);
			this.mutex    = (mutex == null) ? this : mutex;
		}

		@Override
		public final String toString()
		{
			synchronized (this.mutex)
			{
				return this.delegate.toString();
			}
		}

		private final  void writeObject(final ObjectOutputStream stream) throws IOException
		{
			synchronized (this.mutex)
			{
				stream.defaultWriteObject();
			}
		}
	}
	
	private static final class SynchronizedResource<T extends Listener> extends SynchronizedObject<Resource<T>> implements Resource<T>
	{
		private static final long serialVersionUID = -6955488978911597292L;

		SynchronizedResource(final Resource<T> resource, final Object mutex)
		{
			super(resource, mutex);
		}

		@Override
		public final boolean equals(final Object o)
		{
			if (o == this)
			{
				return true;
			}

			synchronized (this.mutex)
			{
				return this.delegate.equals(o);
			}
		}

		@Override
		public final int hashCode()
		{
			synchronized (this.mutex)
			{
				return this.delegate.hashCode();
			}
		}
		
		public final void register(final T listener)
		{
			synchronized (this.mutex)
			{
				this.delegate.register(listener);
			}
		}

		public final void unregister(final T listener)
		{
			synchronized (this.mutex)
			{
				this.delegate.register(listener);
			}
		}

		public final void unregisterAll(final Class<? super T> type)
		{
			synchronized (this.mutex)
			{
				this.delegate.unregisterAll(type);
			}
		}
	}
	
	private static final class SynchronizedPool<T extends Listener> extends SynchronizedObject<Pool<T>> implements Pool<T>
	{
		private static final long serialVersionUID = -3478838239933812834L;

		SynchronizedPool(final Pool<T> pool, final Object mutex)
		{
			super(pool, mutex);
		}
		
		@Override
		public final boolean equals(final Object o)
		{
			if (o == this)
			{
				return true;
			}

			synchronized (this.mutex)
			{
				return this.delegate.equals(o);
			}
		}

		@Override
		public final int hashCode()
		{
			synchronized (this.mutex)
			{
				return this.delegate.hashCode();
			}
		}

		public final boolean contains(final T listener)
		{
			synchronized (this.mutex)
			{
				return this.delegate.contains(listener);
			}
		}
		
		public final void add(final T listener)
		{
			synchronized (this.mutex)
			{
				this.delegate.add(listener);
			}
		}
		
		public final void remove(final T listener)
		{
			synchronized (this.mutex)
			{
				this.delegate.remove(listener);
			}
		}
		
		public final Collection<T> toCollection()
		{
			synchronized (this.mutex)
			{
				return this.delegate.toCollection();
			}
		}
	}
	
	static final <T extends Listener> Resource<T> resource(final Resource<T> resource)
	{
		return new SynchronizedResource<>(resource, new Object());
	}
	
	static final <T extends Listener> Pool<T> pool(final Pool<T> pool)
	{
		return new SynchronizedPool<>(pool, new Object());
	}
}
