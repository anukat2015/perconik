package sk.stuba.fiit.perconik.core.services.listeners;

import sk.stuba.fiit.perconik.core.Listener;
import sk.stuba.fiit.perconik.core.services.ForwardingProvider;

public abstract class ForwardingListenerProvider extends ForwardingProvider implements ListenerProvider
{
	protected ForwardingListenerProvider()
	{
	}

	@Override
	protected abstract ListenerProvider delegate();

	public <L extends Listener> L forClass(Class<L> type)
	{
		return this.delegate().forClass(type);
	}

	public Class<? extends Listener> loadClass(String name) throws ClassNotFoundException
	{
		return this.delegate().loadClass(name);
	}

	public Iterable<Class<? extends Listener>> classes()
	{
		return this.delegate().classes();
	}
}