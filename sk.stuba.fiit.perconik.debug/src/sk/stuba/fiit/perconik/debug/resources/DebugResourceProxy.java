package sk.stuba.fiit.perconik.debug.resources;

import java.util.Collection;
import sk.stuba.fiit.perconik.core.Listener;
import sk.stuba.fiit.perconik.core.Resource;
import sk.stuba.fiit.perconik.debug.Debug;
import sk.stuba.fiit.perconik.debug.DebugConsole;
import sk.stuba.fiit.perconik.debug.DebugListeners;
import sk.stuba.fiit.perconik.debug.DebugObjectProxy;
import sk.stuba.fiit.perconik.debug.DebugResource;
import sk.stuba.fiit.perconik.debug.DebugResources;

public final class DebugResourceProxy<L extends Listener> extends DebugObjectProxy<Resource<L>> implements DebugResource<L>
{
	private DebugResourceProxy(final Resource<L> resource, final DebugConsole console)
	{
		super(resource, console);
	}
	
	public static final <L extends Listener> DebugResourceProxy<L> of(final Resource<L> resource)
	{
		return of(resource, Debug.getDefaultConsole());
	}

	public static final <L extends Listener> DebugResourceProxy<L> of(final Resource<L> resource, final DebugConsole console)
	{
		if (resource instanceof DebugResourceProxy)
		{
			return (DebugResourceProxy<L>) resource;
		}
		
		return new DebugResourceProxy<>(resource, console);
	}
	
	public final void register(final L listener)
	{
		this.print("Registering listener %s to resource %s", DebugListeners.toString(listener), DebugResources.toString(this.delegate()));
		this.tab();		
		
		this.delegate().register(listener);
		
		this.untab();
	}

	public final void unregister(final L listener)
	{
		this.print("Unregistering listener %s from resource %s", DebugListeners.toString(listener), DebugResources.toString(this.delegate()));
		this.tab();
		
		this.delegate().unregister(listener);
		
		this.untab();
	}

	public final void unregisterAll(final Class<? extends Listener> type)
	{
		this.print("Unregistering all listeners from resource %s", DebugResources.toString(this.delegate()));
		this.tab();
		
		this.delegate().unregisterAll(type);
		
		this.untab();
	}

	public final <U extends Listener> Collection<U> registered(Class<U> type)
	{
		return this.delegate().registered(type);
	}
}